package ingage.ingage.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import ingage.ingage.R;
import ingage.ingage.handlers.IdentityHandler;

public class ResetPasswordActivity extends AppCompatActivity {
    IdentityHandler identityHandler;
    Button resetPasswordButton;
    EditText oldPasswordEt, newPasswordEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetPasswordButton = (Button) findViewById(R.id.submitResetPasswordButton);
        oldPasswordEt = (EditText) findViewById(R.id.oldpassword);
        newPasswordEt = (EditText) findViewById(R.id.newpassword);

        identityHandler = new IdentityHandler(getApplicationContext());
    }

    private void onResetPassword(){
        String oldPassword = oldPasswordEt.getText().toString();
        String newPassword = newPasswordEt.getText().toString();

        identityHandler = new IdentityHandler(getApplicationContext());
        identityHandler.execute("reset_password", oldPassword, newPassword);
    }


}
