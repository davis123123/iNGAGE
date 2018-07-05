package ingage.ingage.activities;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ingage.ingage.R;
import ingage.ingage.fragments.ListDialogFragment;
import ingage.ingage.handlers.SubmitThreadsHandler;
import ingage.ingage.handlers.UploadImageHandler;
import ingage.ingage.managers.SessionManager;

public class  PostThreadActivity extends AppCompatActivity implements SubmitThreadsHandler.AsyncInterface
            , ListDialogFragment.ListItemSelectionListener{
    /** Class name for log messages. */
    private static final String LOG_TAG = PostThreadActivity.class.getSimpleName();

    /** Bundle key for saving/restoring the toolbar title. */
    private static final String BUNDLE_KEY_TOOLBAR_TITLE = "title";


    private EditText mInsertThreadTitle;
    private EditText mInsertThreadContent;
    private LinearLayout llUploadImage;
    SessionManager session;
    ArrayAdapter<CharSequence> adapter;
    private static final int requestURL = 1;
    String returnedURL;
    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageView imageToUpload;
    UploadImageHandler uploadImageHandler;
    private boolean usedImage = false;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    DatabaseReference post_root;
    public static String extension;
    ProgressDialog pd;
    ListDialogFragment f;
    Button btnCategorySpinner;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getApplicationContext());

        setContentView(R.layout.activity_post_thread);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mInsertThreadContent = (EditText) findViewById(R.id.insert_thread_content_text_view);
        mInsertThreadTitle = (EditText) findViewById(R.id.insert_thread_title_text_view);
        imageToUpload = (ImageView) findViewById(R.id.uploadImageView);
        llUploadImage = (LinearLayout) findViewById(R.id.llUploadImage);
        btnCategorySpinner = (Button) findViewById(R.id.btnCategorySpinner);

        btnCategorySpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                f = ListDialogFragment.newInstance();
                f.show(fm, "");
            }
        });

        imageToUpload.setVisibility(View.INVISIBLE);
        setUploadImageLayout();
       // addListenerOnSpinnerItemSelection();


        //bUploadImage = (Button) findViewById(R.id.upload_image_button);
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
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()) {
            case R.id.submit_post_button:
                addData();
                return true;

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

    public void addData(){
        Context context = PostThreadActivity.this;
        String image_link = "";
        String threadContent = mInsertThreadContent.getText().toString();
        String threadTitle = mInsertThreadTitle.getText().toString();
        String imageTitle = threadTitle.replaceAll("\\s+", "");
        Bitmap image = null;

        //submit image
        if(usedImage) {
            image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
            //uploadImageHandler = new UploadImageHandler(image);
            //uploadImageHandler.execute(imageTitle);
            image_link = "http://107.170.232.60/images/"+imageTitle+".JPG";
        }

        if(threadTitle.length() == 0){
            Toast.makeText(this, "Please provide a title.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(threadContent.length() == 0){
            Toast.makeText(this, "Please provide a description", Toast.LENGTH_SHORT).show();
            return;
        }

        if(btnCategorySpinner.getText().equals("-") || btnCategorySpinner.getText() == null){
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }


        loadingDialog();
        String cSpinner= (String) btnCategorySpinner.getText();

        //USER INSERT
        HashMap<String, String> user = session.getUserDetails();
        String threadBy = user.get(SessionManager.KEY_NAME);
        String type = "submit";
        SubmitThreadsHandler submitThreadsHandler = new SubmitThreadsHandler(this, image);

        //CREATE NEW THREAD
        submitThreadsHandler.execute(type, threadTitle, threadContent, threadBy, cSpinner, image_link, String.valueOf(usedImage));

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
    public void  onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("savedTitle", mInsertThreadTitle.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mInsertThreadTitle.getText().insert(mInsertThreadTitle.getSelectionStart(),
                savedInstanceState.getString("savedTitle"));

        /**mInsertThreadContent.getText().insert(mInsertThreadContent.getSelectionStart(),
         returnedURL);**/
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String filename = getContentResolver().getType(selectedImage);
            extension = filename.substring(filename.lastIndexOf('/') + 1);
            Log.d("STATE", "Uri: " + extension);
            imageToUpload.setImageURI(selectedImage);
            imageToUpload.setVisibility(View.VISIBLE);
            llUploadImage.setVisibility(View.INVISIBLE);
            //mInsertThreadContent.setVisibility(View.INVISIBLE);
            usedImage = true;
        }
    }
}
