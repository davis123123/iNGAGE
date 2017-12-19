package ingage.ingage20.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import ingage.ingage20.firebase.FirebaseSharedPrefManager;
import ingage.ingage20.R;
import ingage.ingage20.managers.WifiManager;
import ingage.ingage20.util.SignInProvider;
import ingage.ingage20.handlers.IdentityHandler;
import ingage.ingage20.managers.AlertDiaLogManager;
import ingage.ingage20.managers.SessionManager;

/**
 * Created by Davis on 4/6/2017.
 */

public class LoginActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    EditText usernameEt, passwordEt;
    Button LoginEt;
    TextView signUpTV;
    private SignInProvider signInProvider;
    SessionManager session;
    AlertDiaLogManager alert = new AlertDiaLogManager();
    ProgressDialog pd;
    WifiManager wifiManager;
    //String thread_subs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        session = new SessionManager(getApplicationContext());

        wifiManager = new WifiManager(getBaseContext());

        usernameEt = (EditText) findViewById(R.id.username);
        passwordEt = (EditText) findViewById(R.id.password);
        LoginEt = (Button) findViewById(R.id.sign_in_button);
        signUpTV =  (TextView) findViewById(R.id.signIn_textView_CreateNewAccount);

        signUpTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(wifiManager.checkInternet())
                    goSignUp();
                else
                    showDialog();
            }
        });

        LoginEt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (view == LoginEt){
                    if(wifiManager.checkInternet())
                        OnLogin();
                    else
                        showDialog();
                }
            }
        });
    }

    public void showDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Connection Error")
                .setMessage( "Please check if device is connected to internet")
                .setPositiveButton(android.R.string.yes, null)
                .show();
    }

    public void goSignUp(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void goMain() {
        loadingDialog();
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra("thread_subs", thread_subs);
        startActivity(intent);
        finish();
    }

    private void loadingDialog(){
        pd = new ProgressDialog(this);
        pd.setTitle("Login Successful!");
        pd.setMessage("Signing in...");
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pd.show();
    }

    public void OnLogin(){
        String username = usernameEt.getText().toString();
        String password = passwordEt.getText().toString();
        String appToken = FirebaseSharedPrefManager.getInstance(this).getToken();
        String type = "login";
        IdentityHandler identityHandler = new IdentityHandler(this);
        String loginStatus = null;
        if(appToken != null) {
            try {
                loginStatus = identityHandler.execute(type, username, password, appToken).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            Log.e("login status: ", loginStatus);
            /**toast = Toast.makeText(this, loginStatus, Toast.LENGTH_LONG);
             toast.show();**/
            if (loginStatus.equals("login failed")) {
                //pd.dismiss();
                alert.showAlertDialog(LoginActivity.this, "Login failed..", "Username/Password is incorrect", false);
                usernameEt.getText().clear();
                passwordEt.getText().clear();
            } else if (loginStatus.equals("error getting token")) {
                //pd.dismiss();
                alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please try again later", false);
                Log.e("STATE", "Token not Registered");
                usernameEt.getText().clear();
                passwordEt.getText().clear();
            } else {
                session.createLoginSession(username, password);
                parseProfileJSON(loginStatus);
                goMain();
            }
        } else{
            alert.showAlertDialog(LoginActivity.this, "Login failed...", "Please try again later", false);
            Log.e("STATE", "Token not Registered");
            usernameEt.getText().clear();
            passwordEt.getText().clear();
        }
    }

    protected void parseProfileJSON(String json_string){
        JSONObject jsonObject;
        JSONArray jsonArray;
        Log.i("STATE", "json string: " + json_string);

        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("user_profile");
            int count= 0;
            String email, tribute_points, thread_subscriptions, date_joined, avatar_link;
            while(count < jsonArray.length()){
                JSONObject JO = jsonArray.getJSONObject(count);
                email = JO.getString("email");
                tribute_points = JO.getString("tribute_points");
                thread_subscriptions = JO.getString("thread_subscriptions");
                date_joined = JO.getString("date_joined");
                avatar_link = JO.getString("avatar_link");
                //thread_subs = thread_subscriptions;
                session.updateProfile(email, tribute_points, thread_subscriptions, date_joined);
                session.updateAvatarLink(avatar_link);
                count++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}