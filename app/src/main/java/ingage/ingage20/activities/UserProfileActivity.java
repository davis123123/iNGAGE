package ingage.ingage20.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import ingage.ingage20.handlers.DownloadImageHandler;
import ingage.ingage20.helpers.ThreadsHelper;
import ingage.ingage20.managers.SessionManager;


public class UserProfileActivity extends AppCompatActivity {

    String username, email, tribute_pts, subs;
    Button upload, change;
    ImageView avatar;
    private static final int RESULT_LOAD_IMAGE = 1;


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

        upload = (Button) findViewById(R.id.upload_profile_img);
        avatar = (ImageView) findViewById(R.id.prof_img_preview);
        change = (Button) findViewById(R.id.change_avatar);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goUploadImage();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //download image na update profile

            }
        });
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
            String filename = getContentResolver().getType(selectedImage);
            avatar.setImageURI(selectedImage);
        }
    }


}
