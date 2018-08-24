package ingage.ingage.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ingage.ingage.R;
import ingage.ingage.handlers.IdentityHandler;
import ingage.ingage.managers.AlertDiaLogManager;

public class ForgotPasswordActivity extends AppCompatActivity {
    AlertDiaLogManager alert = new AlertDiaLogManager();
    IdentityHandler identityHandler;
    Button forgotPasswordButton;
    EditText usernameEt, emailEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        identityHandler = new IdentityHandler(getApplicationContext());
        Button forgotPasswordButton = (Button) findViewById(R.id.submitForgotPasswordButton);
        usernameEt = (EditText) findViewById(R.id.username);
        emailEt = (EditText) findViewById(R.id.email);

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onForgotPassword();
            }
        });
    }

    public void onForgotPassword(){
        String username = usernameEt.getText().toString();
        String email = emailEt.getText().toString();
        if(username.length() < 1){
            showDialog("Uh Oh","Please enter Username");
        }
        else if(email.length() < 1){
            showDialog("Uh Oh","Please enter email");
        }
        else if(!email.contains("@")){
            showDialog("Uh Oh","Please enter a valid email");
        }
        else{
            identityHandler = new IdentityHandler(getApplicationContext());
            identityHandler.execute("forgot_password", username, email);
        }
    }

    public void showDialog(String title, String message){
        alert.showAlertDialog(ForgotPasswordActivity.this,title ,message, false);
    }

}
