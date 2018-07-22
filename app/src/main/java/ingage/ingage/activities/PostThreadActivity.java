package ingage.ingage.activities;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ingage.ingage.R;
import ingage.ingage.fragments.ListDialogFragment;
import ingage.ingage.handlers.SubmitThreadsHandler;
import ingage.ingage.helpers.ThreadsHelper;
import ingage.ingage.managers.SessionManager;

public class  PostThreadActivity extends AppCompatActivity implements SubmitThreadsHandler.AsyncInterface
            , ListDialogFragment.ListItemSelectionListener{

    private EditText mInsertThreadTitle;
    private EditText mInsertThreadContent;
    private LinearLayout llUploadImage;
    private RelativeLayout rlPage1;
    private RelativeLayout rlPage2;
    SessionManager session;
    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageView imageToUpload;
    private boolean usedImage = false;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    DatabaseReference post_root;
    ProgressDialog pd;
    ListDialogFragment f;
    Button btnCategorySpinner;
    Button btnNext;
    Button btnSubmit;
    ImageView ivIcon1;
    ImageView ivIcon2;

    ThreadsHelper thread;
    Bitmap image = null;
    String image_link = "";

    int activeColor, inactiveColor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getApplicationContext());

        setContentView(R.layout.activity_post_thread);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        thread = new ThreadsHelper();

        rlPage1 = (RelativeLayout) findViewById(R.id.rlPage1);
        rlPage2 = (RelativeLayout) findViewById(R.id.rlPage2);

        mInsertThreadContent = (EditText) findViewById(R.id.insert_thread_content_text_view);
        mInsertThreadTitle = (EditText) findViewById(R.id.insert_thread_title_text_view);
        imageToUpload = (ImageView) findViewById(R.id.uploadImageView);
        llUploadImage = (LinearLayout) findViewById(R.id.llUploadImage);
        btnCategorySpinner = (Button) findViewById(R.id.btnCategorySpinner);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        ivIcon1 = (ImageView) findViewById(R.id.ivIcon1);
        ivIcon2 = (ImageView) findViewById(R.id.ivIcon2);

        Resources res = getResources();
        activeColor = res.getColor(R.color.colorPrimary);
        inactiveColor = res.getColor(R.color.gray);

        ivIcon1.setColorFilter(activeColor, PorterDuff.Mode.SRC_ATOP);
        ivIcon2.setColorFilter(inactiveColor, PorterDuff.Mode.SRC_ATOP);

        btnCategorySpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                f = ListDialogFragment.newInstance();
                f.show(fm, "");
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String threadContent = mInsertThreadContent.getText().toString();
                String threadTitle = mInsertThreadTitle.getText().toString();

                String cSpinner= (String) btnCategorySpinner.getText();

                //USER INSERT
                HashMap<String, String> user = session.getUserDetails();
                String threadBy = user.get(SessionManager.KEY_NAME);

                thread.setThread_title(threadTitle);
                thread.setThread_content(threadContent);
                thread.setThread_category(cSpinner);
                thread.setThread_by(threadBy);

                if(threadTitle.length() == 0){
                    Toast.makeText(PostThreadActivity.this, "Please provide a title.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(threadContent.length() == 0){
                    Toast.makeText(PostThreadActivity.this, "Please provide a description", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(btnCategorySpinner.getText().equals(getResources().getString(R.string.category_default)) || btnCategorySpinner.getText() == null){
                    Toast.makeText(PostThreadActivity.this, "Please select a category", Toast.LENGTH_SHORT).show();
                    return;
                }

                ivIcon2.setColorFilter(activeColor, PorterDuff.Mode.SRC_ATOP);

                rlPage1.setVisibility(View.GONE);
                rlPage2.setVisibility(View.VISIBLE);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String imageTitle = thread.getThread_title().replaceAll("\\s+", "");

                //submit image
                if(usedImage) {
                    image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
                    image_link = "http://107.170.232.60/images/"+imageTitle+".JPG";
                }

                showConfirmationDialog();

            }
        });

        imageToUpload.setVisibility(View.INVISIBLE);
        setUploadImageLayout();

        llUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goUploadImage();
            }
        });

        imageToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goUploadImage();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.thread_post, menu);
        return true;
    }

    @Override
     public void onBackPressed() {
        if(rlPage1.getVisibility()==View.VISIBLE){
            super.onBackPressed();
        }else{
            ivIcon2.setColorFilter(inactiveColor, PorterDuff.Mode.SRC_ATOP);
            rlPage2.setVisibility(View.GONE);
            rlPage1.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onListItemSelected(String item) {
        f.dismiss();
        btnCategorySpinner.setText(item);
    }

    public void showConfirmationDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        String imageIncluded = "No";
        if(usedImage) {
            imageIncluded = "Yes";
        }

        dialog.setTitle("Confirmation");
        dialog.setMessage("Are you sure you want to submit? \n\n\n" +
                "Title: " + thread.getThread_title() + "\n\n" +
                "Description: " + thread.getThread_content() + "\n\n" +
                "Image: " + imageIncluded);
        dialog.setCancelable(true);

        dialog.setPositiveButton(
                "Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        loadingDialog();

                        String type = "submit";
                        SubmitThreadsHandler submitThreadsHandler = new SubmitThreadsHandler(PostThreadActivity.this, image);

                        //CREATE NEW THREAD
                        submitThreadsHandler.execute(type, thread.getThread_title(), thread.getThread_content(),
                                thread.getThread_by(), thread.getThread_category(), image_link, String.valueOf(usedImage));
                        dialog.cancel();
                    }
                });

        dialog.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = dialog.create();
        alert11.show();
    }

    private void setUploadImageLayout(){
        DisplayMetrics metrics = getBaseContext().getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        final int imgHeight = (int) (screenHeight * 0.30);
        final int imgWidth = (int) (screenWidth * 0.75);

        RelativeLayout.LayoutParams img_params = new RelativeLayout.LayoutParams(imgWidth, imgHeight);
        img_params.setMargins(0, (int)(screenHeight * 0.20),0, 0);
        img_params.addRule(RelativeLayout.BELOW, R.id.insert_thread_content_text_view);
        img_params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        llUploadImage.setLayoutParams(img_params);
        imageToUpload.setLayoutParams(img_params);
    }

    @Override
    public void response(String response) {
        if(response != null) {
            Log.d("INSERTTRHEAD", "THIS " + response + usedImage);
            String message = "Post Submitted";
            String[] splitResult = response.split("-");
            response = splitResult[0];
            if (response.equals("Submission Failed")) {
                message = "Submission Failed";
            } else {
                addDataToFirebase(response);
            }

            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, R.string.post_thread_error, Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }
    }

    private void loadingDialog(){
        pd = new ProgressDialog(this);
        pd.setTitle("Submitting");
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pd.show();
    }


    private void addDataToFirebase(String threadId){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put(threadId,"");
        Log.d("SUBMITTHREAD",  " "+ map);
        root.updateChildren(map);
        checkPageExist(threadId);
    }

    public void checkPageExist(String threadTitle){
        Log.d("CHECKPAGE", "yes3");
        post_root = FirebaseDatabase.getInstance().getReference().child(threadTitle);
        post_root.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Log.d("ROOT", String.valueOf(mutableData));
                if (mutableData.hasChildren()) {
                    Log.d("ROOTCHILDREN", "yes");
                } else {
                    Log.d("ROOTCHILDREN", "no");
                    Map<String, Object> map_page = new HashMap<String, Object>();
                    map_page.put("1","");
                    post_root.updateChildren(map_page);
                    createOP();
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d("Fiebase", "first page added");
            }
        });
    } //used only for first comment

    //insert description field as first comment aka orignal post
    public void createOP(){
        HashMap<String, String> user = session.getUserDetails();
        String messageBy = user.get(SessionManager.KEY_NAME);
        String msg = mInsertThreadContent.getText().toString();

        insertComment(messageBy, msg);

        // UserRecentCommentHandler handler = new UserRecentCommentHandler();
        // handler.enqueue(username, thread_id, msg, "agree");

    }

    private void insertComment(final String messageBy, final String messageText){
        DatabaseReference page_root = post_root.child("1");
        String temp_key = page_root.push().getKey();

        DatabaseReference message_root = page_root.child(temp_key);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Map<String, Object> map_message = new HashMap<String, Object>();
        map_message.put("Username", messageBy);
        map_message.put("Msg", messageText);
        map_message.put("Side", "agree");
        map_message.put("upvotes", 0);
        map_message.put("downvotes", 0);
        map_message.put("TimeStamp", currentDateTimeString);
        message_root.updateChildren(map_message);
    }

    private void goUploadImage(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }


    @Override
    public void  onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("savedTitle", mInsertThreadTitle.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mInsertThreadTitle.getText().insert(mInsertThreadTitle.getSelectionStart(),
                savedInstanceState.getString("savedTitle"));
        
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
                        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        Toast.makeText(this, R.string.user_permision_request, Toast.LENGTH_SHORT).show();
                    }
                }

                else {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);


                    } else {
                        Toast.makeText(this, R.string.user_permision_request, Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                imageToUpload.setImageURI(resultUri);

                imageToUpload.setVisibility(View.VISIBLE);
                llUploadImage.setVisibility(View.INVISIBLE);
                usedImage = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
