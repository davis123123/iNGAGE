package ingage.ingage20.firebase;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Davis on 5/8/2017.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    public static final String TOKEN_BROADCAST = "myfcmtokenbroadcast";


    @Override
    public void onTokenRefresh(){
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("myfirebaseid","Refreshed token: "+ refreshedToken);
        //Toast.makeText(this,refreshedToken,Toast.LENGTH_LONG).show();
        getApplicationContext().sendBroadcast(new Intent(TOKEN_BROADCAST));
        storeToken(refreshedToken);
    }

    private void storeToken(String token) {
        FirebaseSharedPrefManager.getInstance(getApplicationContext()).storeToken(token);
    }
}
