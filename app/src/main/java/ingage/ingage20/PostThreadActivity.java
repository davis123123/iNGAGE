package ingage.ingage20;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;

import ingage.ingage20.MySQL.SubmitThreadsHandler;
import ingage.ingage20.MySQL.UploadImageHandler;

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

        //UPLOAD TO IMGUR BUTTON GOES TO NEW ACTVITY
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

        //submit image
        if(usedImage) {
            Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
            uploadImageHandler = new UploadImageHandler(image);
            uploadImageHandler.execute(threadTitle);
            image_link = "http://10.0.0.199/images/"+threadTitle+".JPG";
        }

        //categorySpinner = (Spinner) findViewById(R.id.spinner);
        String cSpinner = String.valueOf(categorySpinner.getSelectedItem());

        //USER INSERT
        HashMap<String, String> user = session.getUserDetails();
        String threadBy = user.get(SessionManager.KEY_NAME);
        String type = "submit";

        SubmitThreadsHandler submitThreadsHandler = new SubmitThreadsHandler(context);
        submitThreadsHandler.execute(type, threadTitle, threadContent, threadBy, cSpinner, image_link);

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private void goUploadImage(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
            mInsertThreadContent.setVisibility(View.INVISIBLE);
            usedImage = true;
        }
    }

}
