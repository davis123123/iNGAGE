package ingage.ingage20.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.handlers.DownloadAvatarHandler;
import ingage.ingage20.handlers.UploadAvatarHandler;
import ingage.ingage20.managers.SessionManager;


public class UserProfileActivity extends AppCompatActivity {

    String username, email, tribute_pts, subs;
    Button upload, change;
    ImageView new_avatar_preview, curr_avatar;
    private static final int RESULT_LOAD_IMAGE = 1;
    boolean verified_image = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_user_profile);

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> info = session.getUserDetails();
        username = info.get(SessionManager.KEY_NAME);
        email = info.get(SessionManager.KEY_EMAIL);
        tribute_pts = info.get(SessionManager.KEY_TRIBUTE_POINTS);
        subs = info.get(SessionManager.KEY_SUBSCRIPTIONS);

        TextView user_info = (TextView) findViewById(R.id.user_name);
        TextView email_info = (TextView) findViewById(R.id.email);
        TextView pts_info = (TextView) findViewById(R.id.tribute_points);
        TextView subs_info = (TextView) findViewById(R.id.subscriptions);

        user_info.setText(username);
        email_info.setText(email);
        pts_info.setText(tribute_pts);
        subs_info.setText(subs);

        new_avatar_preview = (ImageView) findViewById(R.id.prof_img_preview);
        curr_avatar = (ImageView) findViewById(R.id.profile_img);
        upload = (Button) findViewById(R.id.upload_profile_img);
        change = (Button) findViewById(R.id.change_avatar);

        downloadImage();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goUploadImage();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //download image and update profile
                if(new_avatar_preview.getDrawable() != null) {
                    Bitmap image = ((BitmapDrawable) new_avatar_preview.getDrawable()).getBitmap();
                    UploadAvatarHandler uploadAvatarHandler = new UploadAvatarHandler(image);
                    Log.d("STATE", "upload avatar clicked" );
                    try {
                        if(verified_image) {
                            String success = uploadAvatarHandler.execute(username).get();
                            Log.d("STATE", "upload avatar " + success);
                            String avatar_link = "http://107.170.232.60/images/" + username + ".JPG";
                            downloadImage();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(getApplication(), "No image selected/uploaded!", Toast.LENGTH_LONG).show();
            }
        });
    }

    //retrieve Base64 from FireBase and convert to image
    private void downloadImage(){
        Context context = getApplicationContext();
        DownloadAvatarHandler avatarHandler = new DownloadAvatarHandler(context);
        String type = "download";


        //do conversion
        try {
            String result = avatarHandler.execute(type, username).get();
            //Log.d("STATE", "room title: " + threadsHelper.getThread_title());
            Log.d("STATE", "download avatar result: " + result);
            if(result.length() > 0) {
                if (result.substring(0, 4).equals("data")) {
                    int index = result.indexOf(",") + 1;
                    String code = result.substring(index, result.length());
                    byte[] decodedString = Base64.decode(code, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    curr_avatar.setImageBitmap(decodedByte);
                    LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 1000);
                    curr_avatar.setLayoutParams(img_params);
                }
            }
            else
                curr_avatar.setImageResource(R.mipmap.user);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //set padding programmatically
        if(curr_avatar.getDrawable() != null) {
            float density = context.getResources().getDisplayMetrics().density;
            int padding = (int)(20 * density);
            curr_avatar.setPadding(padding, padding, padding, padding);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (Build.VERSION.SDK_INT >= 23) {
                    Log.d("STATE", "API LVL >= 23");
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                    } else {

                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }

                else {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);


                    } else {
                        Toast.makeText(this, "Permission denied to access external storage!", Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            }
        }
    }

    private void goUploadImage(){

        Context mContext = getApplicationContext();
        int check = mContext.getPackageManager().checkPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                mContext.getPackageName());
        if (check == PackageManager.PERMISSION_GRANTED) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
        }

        else
            // Required to ask user for permission to access user's external storage
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            //String filename = getContentResolver().getType(selectedImage);
            new_avatar_preview.setImageURI(selectedImage);
            verified_image = true;
        }
    }


}
