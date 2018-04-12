package ingage.ingage20.managers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Set;

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

    //public static final String ROOM_USERS = "room_users";

    public static final String SIDE = "side";
    public static final String SPECTATOR = "spectator";

    public static final String ROOM_USERS = "room_users";
    private static final String PREF_NAME = "ChatRoomPref";
    public static final Set<String> none = null;
    public static final String TOTAL_PAGES = "total_pages";
    public static final String CUR_PAGE = "current_page";
    public static final String TIME_REMAINING = "time_remaining";

    public ChatRoomManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }




    public void updateUserRoomSession(String thread_id, String side, String spectator){
        editor.putString(THREAD_ID, thread_id);
        editor.putString(SIDE, side);
        editor.putString(SPECTATOR, spectator);

        // commit changes
        editor.commit();
    }

    public void updateTimeRemaining(String timeRemaining){
        editor.putString(TIME_REMAINING, timeRemaining);
        editor.commit();
    }

    public void updateLatestPage(String totalPages){
        editor.putString(TOTAL_PAGES, totalPages);
        editor.commit();
    }

    public void updateCurrentPage(String curPage){
        editor.putString(CUR_PAGE, curPage);
        editor.commit();
    }

    public void updateRoomUsers(Set<String> roomUsers){
        editor.putStringSet(ROOM_USERS, roomUsers);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // thread_id
        user.put(THREAD_ID, pref.getString(THREAD_ID, null));
        user.put(SIDE, pref.getString(SIDE, null));
        user.put(SPECTATOR, pref.getString(SPECTATOR, null));
        user.put(TOTAL_PAGES, pref.getString(TOTAL_PAGES, null));
        user.put(CUR_PAGE, pref.getString(CUR_PAGE, null));
        user.put(TIME_REMAINING, pref.getString(TIME_REMAINING, null));
        // return user
        return user;
    }

    // Get User room State
    public boolean isInsideRoom(){
        return pref.getBoolean(IS_IN_ROOM, false);
    }


}
