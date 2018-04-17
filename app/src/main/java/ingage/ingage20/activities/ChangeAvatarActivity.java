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
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.handlers.DownloadAvatarHandler;
import ingage.ingage20.handlers.UploadAvatarHandler;
import ingage.ingage20.managers.SessionManager;

/**
 * Created by wuv66 on 6/30/2017.
 */

public class ChangeAvatarActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    Button change;
    CircularImageView new_avatar_preview;
    boolean verified_image = false;
    String username;
    String default_path = "data:image/JPG;base64,";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_avatar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile Photo");
        change = (Button) findViewById(R.id.change_avatar);
        new_avatar_preview = (CircularImageView) findViewById(R.id.prof_img_preview);

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> info = session.getUserDetails();
        username = info.get(SessionManager.KEY_NAME);
        downloadCurrentAvatar();

        setListeners();
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
            new_avatar_preview.setVisibility(View.VISIBLE);
            new_avatar_preview.setImageURI(selectedImage);
            verified_image = true;
            new_avatar_preview.setAlpha((float) 1.0);
        }
    }


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    protected void setListeners(){
        //upload avatar
        new_avatar_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goUploadImage();
            }
        });

        //change avatar
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //download image and update profile
                if(verified_image) {
                    Bitmap image = ((BitmapDrawable) new_avatar_preview.getDrawable()).getBitmap();
                    UploadAvatarHandler uploadAvatarHandler = new UploadAvatarHandler(image);
                    Log.d("STATE", "upload avatar clicked" );
                    try {
                            String avatar_link = "http://107.170.232.60/avatars/" + username + ".JPG";
                            String success = uploadAvatarHandler.execute(username, avatar_link).get();
                            Log.d("STATE", "upload avatar " + success);
                            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                            finish();
                            startActivity(intent);
                            //downloadImage();

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


    private void downloadCurrentAvatar(){
        final String url = "http://107.170.232.60/avatars/" + username + ".JPG";

        Context context = getBaseContext();

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        final int imgHeight = (int) (screenHeight * 0.3);
        final int imgWidth = (int) (screenWidth* 0.3);
        new_avatar_preview.setAlpha((float) 0.5);

        Picasso.with(this)
                .load(url)
         //       .networkPolicy(NetworkPolicy.OFFLINE)
                .resize(imgWidth, imgHeight)
                .onlyScaleDown()
                .noPlaceholder()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(new_avatar_preview, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //If cache fails, try to fetch from url
                        Picasso.with(getBaseContext())
                                .load(url)
                                .resize(imgWidth, imgHeight)
                                .onlyScaleDown()
                                .noPlaceholder()
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                //.error(R.drawable.header)
                                .into(new_avatar_preview, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {
                                        Log.e("Picasso","Could not get image");
                                        new_avatar_preview.setImageResource(R.mipmap.user);
                                    }
                                });
                    }
                });
    }
}
