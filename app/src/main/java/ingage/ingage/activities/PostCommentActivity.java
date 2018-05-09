package ingage.ingage.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;

import ingage.ingage.R;
import ingage.ingage.handlers.SubmitCommentsHandler;
import ingage.ingage.managers.SessionManager;

/**
 * Created by Davis on 4/17/2017.
 */

public class PostCommentActivity extends AppCompatActivity {

    private Spinner sidesSpinner;
    SessionManager session;
    EditText mInsertComment;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);
        session = new SessionManager(getApplicationContext());
        sidesSpinner = (Spinner) findViewById(R.id.spinner);
        mInsertComment = (EditText) findViewById(R.id.comment_edit_text);

        addListenerOnSpinnerItemSelection();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.comment_post, menu);
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
        sidesSpinner = (Spinner) findViewById(R.id.spinner);
        sidesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                Toast.makeText(
                        PostCommentActivity.this, parent.getSelectedItem().toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void addData(){
        Context context = PostCommentActivity.this;
        String message = "Comment Submitted";
        String sInsertComment = mInsertComment.getText().toString();
        //USER INSERT
        HashMap<String, String> user = session.getUserDetails();
        String commentBy = user.get(SessionManager.KEY_NAME);
        //SIDE INSERT
        sidesSpinner = (Spinner) findViewById(R.id.spinner);

        String sSpinner = String.valueOf(sidesSpinner.getSelectedItem());
        String type = "submit";
        SubmitCommentsHandler submitCommentsHandler = new SubmitCommentsHandler(context);
        submitCommentsHandler.execute(type, sInsertComment, commentBy, sSpinner);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
    }
}
