package ingage.ingage.activities;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import ingage.ingage.firebase.FirebaseSharedPrefManager;
import ingage.ingage.R;
import ingage.ingage.fragments.EULADialogFragment;
import ingage.ingage.managers.WifiManager;
import ingage.ingage.handlers.IdentityHandler;
import ingage.ingage.managers.AlertDiaLogManager;
import ingage.ingage.managers.SessionManager;

/**
 * Created by Davis on 4/6/2017.
 */

public class LoginActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    EditText usernameEt, passwordEt;
    Button LoginEt;
    CheckBox cbRemember;
    TextView signUpTV;
    SessionManager session;
    AlertDiaLogManager alert = new AlertDiaLogManager();
    ProgressDialog pd;
    WifiManager wifiManager;
    String loginStatus;
    //String thread_subs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        session = new SessionManager(getApplicationContext());

        wifiManager = new WifiManager(getBaseContext());

        usernameEt = (EditText) findViewById(R.id.username);
        passwordEt = (EditText) findViewById(R.id.password);
        cbRemember = (CheckBox) findViewById(R.id.cbRememberMe);
        cbRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                String key = getResources().getString(R.string.remember_user_checked);
                SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();

                if (cbRemember.isChecked()) {
                    prefEditor.putBoolean(key, true);
                } else {
                    prefEditor.putBoolean(key, false);
                }
                prefEditor.commit();
            }
        });

        LoginEt = (Button) findViewById(R.id.sign_in_button);
        signUpTV =  (TextView) findViewById(R.id.signIn_textView_CreateNewAccount);

        signUpTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(wifiManager.checkInternet())
                    goSignUp();
                else
                    wifiErrorDialog();
            }
        });

        LoginEt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (view == LoginEt){
                    if(wifiManager.checkInternet()) {
                        OnLogin();
                    }
                    else
                        wifiErrorDialog();
                }
            }
        });

        loadUserName();
        showEULA();
    }

    public void showEULA(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean shouldShowEULA = preferences.getBoolean(String.valueOf(R.string.show_eula), true);
        if(shouldShowEULA){
            FragmentManager fm = getFragmentManager();
            EULADialogFragment f = EULADialogFragment.newInstance();
            f.show(fm, "");
        }
    }

    public void wifiErrorDialog(){
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean firstLaunch = preferences.getBoolean(String.valueOf(R.string.is_first_launch), true);
        pd.dismiss();

        if(!firstLaunch) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(this, WalkthroughActivity.class);
            startActivity(intent);
            finish();
        }
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
        //loginStatus = null;
        if(appToken != null) {
            try {
                loginStatus = identityHandler.execute(type, username, password, appToken).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            Log.e("login status: ", loginStatus);
            if (loginStatus.equals("login failed")) {
                alert.showAlertDialog(LoginActivity.this, "Login failed..", "Username or password is incorrect", false);
                passwordEt.getText().clear();
            } else if(loginStatus.equals("banned")){
                alert.showAlertDialog(LoginActivity.this, "Notice", "This user has been permanently banned due to reports of misconduct.", false);
                passwordEt.getText().clear();
            }

            else if (loginStatus.equals("please confirm email")){
                alert.showAlertDialog(LoginActivity.this, "Login failed...", "Please confirm your e-mail, make sure to check your spam box.", false);
                Log.e("STATE", "Token not Registered");
                passwordEt.getText().clear();
            }

            else {
                Log.e("state", "go to main");
                rememberUser();
                session.createLoginSession(username, password);
                parseProfileJSON(loginStatus);
                goMain();
            }
        }
        /*else if (loginStatus.equals("please confirm email")){
            alert.showAlertDialog(LoginActivity.this, "Login failed...", "Please confirm your e-mail, make sure to check your spam box.", false);
            Log.e("STATE", "Token not Registered");
            passwordEt.getText().clear();
        }*/
        else{
            alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please try again later", false);
            Log.e("STATE", "Token not Registered");
            passwordEt.getText().clear();
        }

    }

    public void rememberUser(){
        String key = getResources().getString(R.string.saved_user);
        SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        if(cbRemember.isChecked())
            prefEditor.putString(key, String.valueOf(usernameEt.getText()));

        //clear cached username
        else
            prefEditor.putString(key, "");

        prefEditor.commit();
    }

    public void loadUserName(){
        String key = getResources().getString(R.string.remember_user_checked);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean load = prefs.getBoolean(key, false);
        if(load){
            cbRemember.setChecked(true);
            key = getResources().getString(R.string.saved_user);
            String user = prefs.getString(key, "");
            usernameEt.setText(user);
            usernameEt.setSelection(user.length());
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