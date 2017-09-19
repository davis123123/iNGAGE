package ingage.ingage20.managers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import ingage.ingage20.activities.LoginActivity;

/**
 * Created by Davis on 4/10/2017.
 */

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "UserSessionPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Password (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";

    // Email (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    // Tribute_points (make variable public to access from outside)
    public static final String KEY_TRIBUTE_POINTS = "tribute_points";
    // Subscriptions (make variable public to access from outside)
    public static final String KEY_SUBSCRIPTIONS = "subscriptions";

    public static final String CATEGORY_TYPE = "categories";

    public static final String PAGE_TYPE = "page_type";
    public static final String SEARCH_STRING = "search_string";
    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String name, String password){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing password in pref
        editor.putString(KEY_PASSWORD, password);

        // commit changes
        editor.commit();
    }

    public void updateProfile(String email, String tribute_points,
                              String subscriptions){

        editor.putString(KEY_EMAIL, email);

        editor.putString(KEY_TRIBUTE_POINTS, tribute_points);

        editor.putString(KEY_SUBSCRIPTIONS, subscriptions);

        // commit changes
        editor.commit();
    }

    public void updatePage(String pageType){
        editor.putString(PAGE_TYPE, pageType);
        //editor.putString(CATEGORY_TYPE, categoryType);
        // commit changes
        editor.commit();
    }

    public void updateCategory(String categoryType){
        editor.putString(CATEGORY_TYPE, categoryType);

        editor.commit();
    }

    public void updateSearch(String searchString){
        editor.putString(SEARCH_STRING, searchString);
        editor.commit();
    }

    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // password
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_SUBSCRIPTIONS, pref.getString(KEY_SUBSCRIPTIONS, null));
        user.put(KEY_TRIBUTE_POINTS, pref.getString(KEY_TRIBUTE_POINTS, null));
        user.put(PAGE_TYPE, pref.getString(PAGE_TYPE, null));
        user.put(CATEGORY_TYPE, pref.getString(CATEGORY_TYPE, null));
        user.put(SEARCH_STRING, pref.getString(SEARCH_STRING, null));
        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
