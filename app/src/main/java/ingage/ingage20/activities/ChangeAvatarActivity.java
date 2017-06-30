package ingage.ingage20.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.handlers.UploadAvatarHandler;
import ingage.ingage20.managers.SessionManager;

/**
 * Created by wuv66 on 6/30/2017.
 */

public class ChangeAvatarActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    Button upload, change;
    ImageView new_avatar_preview;
    boolean verified_image = false;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_avatar);
        upload = (Button) findViewById(R.id.upload_profile_img);
        change = (Button) findViewById(R.id.change_avatar);
        new_avatar_preview = (ImageView) findViewById(R.id.prof_img_preview);

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> info = session.getUserDetails();
        username = info.get(SessionManager.KEY_NAME);

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
            new_avatar_preview.setImageURI(selectedImage);
            verified_image = true;
        }
    }

    protected void setListeners(){

        //upload avatar
        upload.setOnClickListener(new View.OnClickListener() {
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
                if(new_avatar_preview.getDrawable() != null) {
                    Bitmap image = ((BitmapDrawable) new_avatar_preview.getDrawable()).getBitmap();
                    UploadAvatarHandler uploadAvatarHandler = new UploadAvatarHandler(image);
                    Log.d("STATE", "upload avatar clicked" );
                    try {
                        if(verified_image) {
                            String success = uploadAvatarHandler.execute(username).get();
                            Log.d("STATE", "upload avatar " + success);
                            String avatar_link = "http://107.170.232.60/images/" + username + ".JPG";
                            //downloadImage();
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
}
