package ingage.ingage.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage.R;
import ingage.ingage.handlers.DownloadAvatarHandler;
import ingage.ingage.handlers.UploadAvatarHandler;
import ingage.ingage.managers.SessionManager;

/**
 * Created by wuv66 on 6/30/2017.
 */

public class ChangeAvatarActivity extends AppCompatActivity implements UploadAvatarHandler.AsyncInterface {

    private static final int RESULT_LOAD_IMAGE = 1;
    Button change;
    CircularImageView new_avatar_preview;
    boolean verified_image = false;
    String username;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog("Loading");

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
                        Toast.makeText(this, R.string.user_permision_granted, Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            }
        }
    }

    @Override
    public void response(String response) {
        String message = "Updated profile photo!";
        if (response.equals("Submission Failed")){
            message = "Profile photo failed to upload!";
        }

        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadingDialog(String title){
        pd = new ProgressDialog(this);
        pd.setTitle(title);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pd.show();
    }

    private void goUploadImage(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                new_avatar_preview.setImageURI(resultUri);
                new_avatar_preview.setVisibility(View.VISIBLE);
                verified_image = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
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
                    loadingDialog("Updating profile");
                    Bitmap image = ((BitmapDrawable) new_avatar_preview.getDrawable()).getBitmap();
                    UploadAvatarHandler uploadAvatarHandler = new UploadAvatarHandler(ChangeAvatarActivity.this, image);
                    String avatar_link = "http://107.170.232.60/avatars/" + username + ".JPG";
                    uploadAvatarHandler.execute(username, avatar_link);
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
                        pd.dismiss();
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
