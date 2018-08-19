package ingage.ingage.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import ingage.ingage.R;
import ingage.ingage.handlers.ContactUsHandler;
import ingage.ingage.managers.SessionManager;

public class ContactUsActivity extends AppCompatActivity implements ContactUsHandler.AsyncInterface{

    Button btnEmailUs;
    EditText etSubject;
    EditText etMessage;

    SessionManager session;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.contact_us_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        session = new SessionManager(this);
        HashMap<String, String> user = session.getUserDetails();
        final String email = user.get(SessionManager.KEY_EMAIL);

        //etSender = (EditText) findViewById(R.id.et_email_sender);
        etSubject= (EditText) findViewById(R.id.et_email_title);
        etMessage= (EditText) findViewById(R.id.et_email_content);

        btnEmailUs = (Button) findViewById(R.id.btnEmailUs);
        btnEmailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = String.valueOf(etSubject.getText());
                String message = String.valueOf(etMessage.getText());
                if (etSubject.getText().length() == 0){
                    Toast.makeText(ContactUsActivity.this, "Please enter subject line", Toast.LENGTH_SHORT).show();
                } else if (etMessage.getText().length() == 0){
                    Toast.makeText(ContactUsActivity.this, "Please enter your message", Toast.LENGTH_SHORT).show();
                }else{
                    loadingDialog();

                    String type = "submit";
                    ContactUsHandler contactUsHandler = new ContactUsHandler(ContactUsActivity.this);
                    contactUsHandler.execute(type, email, subject, message);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
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

    private void loadingDialog(){
        pd = new ProgressDialog(this);
        pd.setTitle("Submitting");
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pd.show();
    }

    @Override
    public void response(String response) {
        pd.dismiss();
        Toast.makeText(this, getResources().getString(R.string.contact_us_sent), Toast.LENGTH_LONG).show();
        finish();
    }

}
