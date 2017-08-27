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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.handlers.SubmitThreadsHandler;
import ingage.ingage20.handlers.UploadImageHandler;
import ingage.ingage20.managers.SessionManager;

public class  PostThreadActivity extends AppCompatActivity {
    /** Class name for log messages. */
    private static final String LOG_TAG = PostThreadActivity.class.getSimpleName();

    /** Bundle key for saving/restoring the toolbar title. */
    private static final String BUNDLE_KEY_TOOLBAR_TITLE = "title";


    private EditText mInsertThreadTitle;
    private EditText mInsertThreadContent;
    private Button bUploadImage;
    SessionManager session;
    private Spinner categorySpinner;
    ArrayAdapter<CharSequence> adapter;
    private static final int requestURL = 1;
    String returnedURL;
    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageView imageToUpload;
    UploadImageHandler uploadImageHandler;
    private boolean usedImage = false;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    public static String extension;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getApplicationContext());

        setContentView(R.layout.activity_post_thread);

        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.thread_categories,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position)+"selected",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mInsertThreadContent = (EditText) findViewById(R.id.insert_thread_content_text_view);
        mInsertThreadTitle = (EditText) findViewById(R.id.insert_thread_title_text_view);
        imageToUpload = (ImageView) findViewById(R.id.uploadImageView);
        addListenerOnSpinnerItemSelection();

        bUploadImage = (Button) findViewById(R.id.upload_image_button);
        bUploadImage.setOnClickListener(new View.OnClickListener() {
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
    public boolean onOptionsItemSelected(MenuItem item){
        int menuItemThatWasSelected = item.getItemId();
        if (menuItemThatWasSelected == R.id.submit_post_button){
            addData();
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }

    public void addListenerOnSpinnerItemSelection(){
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                Toast.makeText(
                        PostThreadActivity.this, parent.getSelectedItem().toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void addData(){
        Context context = PostThreadActivity.this;
        String image_link = "";
        String message = "Post Submitted";
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

        //categorySpinner = (Spinner) findViewById(R.id.spinner);
        String cSpinner = String.valueOf(categorySpinner.getSelectedItem());

        //USER INSERT
        HashMap<String, String> user = session.getUserDetails();
        String threadBy = user.get(SessionManager.KEY_NAME);
        String type = "submit";
        String submission_status = " ";
        SubmitThreadsHandler submitThreadsHandler = new SubmitThreadsHandler(image);

        //CREATE NEW THREAD
        try {
            submission_status = submitThreadsHandler.execute(type, threadTitle, threadContent, threadBy, cSpinner, image_link, String.valueOf(usedImage)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("INSERTTRHEAD", "THIS " + submission_status + usedImage);
        if (submission_status.equals("Submission Failed")){
            message = "Submission Failed";
        }
        else {
            addDataToFirebase(submission_status);
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void addDataToFirebase(String threadId){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put(threadId,"");
        root.updateChildren(map);
        checkPageExist(threadId);
    }

    public void checkPageExist(String threadTitle){
        Log.d("CHECKPAGE", "yes3");
        final DatabaseReference post_root = FirebaseDatabase.getInstance().getReference().child(threadTitle);
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
                    String fPage = "1";
                    post_root.updateChildren(map_page);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    } //used only for first comment

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
                        Toast.makeText(this, "Permission denied to access external storage!", Toast.LENGTH_SHORT).show();
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
            mInsertThreadContent.setVisibility(View.INVISIBLE);
            usedImage = true;
        }
    }
}
