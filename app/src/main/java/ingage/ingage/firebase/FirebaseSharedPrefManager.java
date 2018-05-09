package ingage.ingage.firebase;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Davis on 5/8/2017.
 */

public class FirebaseSharedPrefManager {

    private static Context mContext;
    private static FirebaseSharedPrefManager mInstance;
    private static final String SHARED_PREF_NAME = "fcmsharedprefdemo";
    private static final String KEY_ACCESS_TOKEN = "token";

    private FirebaseSharedPrefManager(Context context){
        mContext = context;
    }

    public static synchronized FirebaseSharedPrefManager getInstance(Context context){
        if (mInstance == null){
            mInstance= new FirebaseSharedPrefManager(context);
        }
        return mInstance;
    }

    public boolean storeToken(String token){
        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
        return true;
    }

    public String getToken(){
        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

}
