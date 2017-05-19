package ingage.ingage20;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Davis on 5/18/2017.
 */

public class ChatRoomManager {
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    public static final String THREAD_ID = "thread_id";

    private static final String IS_IN_ROOM = "IsInRoom";

    public static final String ROOM_USERS = "room_users";

    public static final String SIDE = "side";

    private static final String PREF_NAME = "ChatRoomPref";

    public ChatRoomManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void updateUserRoomSession(String thread_id, String side,  String room_users){
        editor.putString(THREAD_ID, thread_id);
        editor.putString(SIDE, side);
        editor.putString(ROOM_USERS, room_users);
        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getUserRDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // thread_id
        user.put(THREAD_ID, pref.getString(THREAD_ID, null));
        user.put(SIDE, pref.getString(SIDE, null));
        // return user
        return user;
    }

    // Get User room State
    public boolean isInsideRoom(){
        return pref.getBoolean(IS_IN_ROOM, false);
    }


}
