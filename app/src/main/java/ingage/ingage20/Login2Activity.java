package ingage.ingage20;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import ingage.ingage20.MySQL.IdentityHandler;

/**
 * Created by Davis on 4/6/2017.
 */

public class Login2Activity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    EditText usernameEt, passwordEt;
    Button LoginEt;
    TextView signUpTV;
    private SignInProvider signInProvider;
    SessionManager session;
    AlertDiaLogManager alert = new AlertDiaLogManager();
    //String thread_subs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        session = new SessionManager(getApplicationContext());

        usernameEt = (EditText) findViewById(R.id.username);
        passwordEt = (EditText) findViewById(R.id.password);
        LoginEt = (Button) findViewById(R.id.sign_in_button);
        signUpTV =  (TextView) findViewById(R.id.signIn_textView_CreateNewAccount);

        signUpTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                goSignUp();
            }
        });

        LoginEt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (view == LoginEt){
                    OnLogin();
                }
            }
        });
    }

    public void goSignUp(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void goMain() {
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra("thread_subs", thread_subs);
        startActivity(intent);
        finish();
    }

    public void OnLogin(){
        String username = usernameEt.getText().toString();
        String password = passwordEt.getText().toString();
        String type = "login";
        IdentityHandler identityHandler = new IdentityHandler(this);
        String loginStatus = null;
        try {
            loginStatus = identityHandler.execute(type, username, password).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        /**toast = Toast.makeText(this, loginStatus, Toast.LENGTH_LONG);
         toast.show();**/
        if(loginStatus.equals("login failed")){
            alert.showAlertDialog(Login2Activity.this, "Login failed..", "Username/Password is incorrect", false);
            usernameEt.getText().clear();
            passwordEt.getText().clear();
        }

        else{
            session.createLoginSession(username, password);
            parseProfileJSON(loginStatus);
            goMain();
            //TODO query the profile

        }
    }

    protected void parseProfileJSON(String json_string){
        JSONObject jsonObject;
        JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("user_profile");
            int count= 0;
            String email, tribute_points, thread_subscriptions;
            while(count < jsonArray.length()){
                JSONObject JO = jsonArray.getJSONObject(count);
                email = JO.getString("email");
                tribute_points = JO.getString("tribute_points");
                thread_subscriptions = JO.getString("thread_subscriptions");
                //thread_subs = thread_subscriptions;
                session.updateProfile(email, tribute_points, thread_subscriptions);
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