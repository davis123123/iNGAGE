package ingage.ingage20.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.handlers.IdentityHandler;
import ingage.ingage20.managers.AlertDiaLogManager;

public class SignUpActivity extends AppCompatActivity {
    EditText mUserName, mPassword, mEmail, mRePassword;
    Button mSignUp;
    AlertDiaLogManager alert = new AlertDiaLogManager();
    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mUserName = (EditText) findViewById(R.id.sign_up_username);
        mPassword = (EditText) findViewById(R.id.sign_up_password);
        mRePassword = (EditText) findViewById(R.id.retype_password);
        mEmail = (EditText) findViewById(R.id.sign_up_email);
        mSignUp = (Button)  findViewById(R.id.sign_up_button);
        mSignUp.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (view == mSignUp){
                    onRegister();
                }
            }
        });
    }

    public void onRegister(){
        String username = mUserName.getText().toString();
        String password = mPassword.getText().toString();
        String email = mEmail.getText().toString();
        String rePassword = mRePassword.getText().toString();

        //CHECK PASSWORD AND RE-PASSWORD MATCH
        boolean passMatch = passwordCheck(password, rePassword);
        if (passMatch){
            sendData(username, password, email);
        }
        else{
            mToast = Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT);
            mToast.show();
            mPassword.getText().clear();
            mRePassword.getText().clear();
        }

    }

    public void goLogin(){
        Intent intent;
        intent = new Intent(this, LoginActivity.class);
        this.startActivity(intent);
    }//log in

    public boolean passwordCheck(String password, String rePassword){
        return password.equals(rePassword);
    }//checks is password == repassword

    public void sendData(String username, String password, String email){
        String type = "registration";
        //TODO FIX jsonArray(HARADCODED RN)
        JSONObject categoryJSON = new JSONObject();
        JSONArray initialSubscriptionArray = new JSONArray();
        try {
            categoryJSON.put("category_name","Politics");
            initialSubscriptionArray.put(categoryJSON);
            categoryJSON = new JSONObject();
            categoryJSON.put("category_name","Music");
            initialSubscriptionArray.put(categoryJSON);
            categoryJSON = new JSONObject();
            categoryJSON.put("category_name","Movies");
            initialSubscriptionArray.put(categoryJSON);
            categoryJSON = new JSONObject();
            categoryJSON.put("category_name","Art");
            initialSubscriptionArray.put(categoryJSON);
            categoryJSON = new JSONObject();
            categoryJSON.put("category_name","Sports");
            initialSubscriptionArray.put(categoryJSON);
            categoryJSON = new JSONObject();
            categoryJSON.put("category_name","Games");
            initialSubscriptionArray.put(categoryJSON);
            categoryJSON = new JSONObject();
            categoryJSON.put("category_name","Philosophy");
            initialSubscriptionArray.put(categoryJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        IdentityHandler identityHandler = new IdentityHandler(this);

        String registration_result = null;

        try {
            registration_result = identityHandler.execute(type, username, password,
                    email, initialSubscriptionArray.toString()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        /**mToast = Toast.makeText(this, registration_result, Toast.LENGTH_SHORT);
         mToast.show();**/

        if(registration_result.equals("registration successful")){
            mToast = Toast.makeText(this, "Registration Success", Toast.LENGTH_SHORT);
            mToast.show();
            goLogin();
        }
        else{
            //TODO add multiple conditions for registraion failure
            alert.showAlertDialog(SignUpActivity.this, "Registration Failed", "Please try again.", false);
        }
    }//send data for registration
}
