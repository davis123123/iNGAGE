package ingage.ingage20.fragments;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.activities.ChatActivity;
import ingage.ingage20.activities.MainActivity;
import ingage.ingage20.adapters.ThreadListAdapter;
import ingage.ingage20.handlers.ChatRoomHandler;
import ingage.ingage20.handlers.MySQLDbHelper;
import ingage.ingage20.handlers.SpectateRoomHandler;
import ingage.ingage20.helpers.ThreadsHelper;
import ingage.ingage20.managers.ChatRoomManager;
import ingage.ingage20.managers.SessionManager;

/**
 * Created by Davis on 4/5/2017.
 */

public class FragmentBase extends Fragment{

    Context mContext;
    FloatingActionButton postThreadButton;
    protected RecyclerView threadListRecyclerView;
    ThreadListAdapter threadListAdapter;
    View rootView;
    SessionManager session;
    ChatRoomManager chatRoomManager;

    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    String side = "agree";      //set to agree by default
    String result = null;

    public static String threadTitle;
    public static String threadDescription;
    public static String threadId;
    public static String threadType;
    public static String threadCapacity;

    Toast mToast;

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                return null;
            }

            @Override
            protected void onPostExecute(final Void aVoid) {
                final View view = getView();
            }
        }.execute();
    }

    @Override
    public void onPause() {
        super.onPause();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                return null;
            }
        }.execute();
    }


    public void itemClick(int p){
        Context context = getActivity().getApplicationContext();


        if(mToast != null){
            mToast.cancel();
        }
        ThreadsHelper threadsHelper = (ThreadsHelper) threadListAdapter.getItem(p);
        String thread_id = threadsHelper.getThread_id();
        threadTitle= threadsHelper.getThread_title();
        threadDescription = threadsHelper.getThread_content();
        Log.i("clicked: " , threadTitle);

        //LEAVE UNTIL COMMENTS A RE FINISHED
        //String toastMessage = "Item #" + thread_id + "clicked.";
        //mToast = Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_LONG);
        //mToast.show();

        String type = "view";
        chooseSideDialog(context, thread_id, type);
    }

    private void chooseSideDialog(final Context context, final String thread_id, final String type){

        String userNo = viewRoomStatus(context, type, thread_id);

        //index 0 for disagree, index 1 for agree
        String[] splittedString = userNo.split("-");
        Toast.makeText(getActivity(), splittedString[0] + " " +splittedString[1], Toast.LENGTH_LONG).show();
        threadCapacity = "No. Disagree users: " + splittedString[0] + "/3\n" + "No. Agree users: " + splittedString[1] +"/3";
        mContext = getActivity().getApplicationContext();
        threadId = thread_id;
        threadType = type;

        android.support.v4.app.FragmentManager fm = getFragmentManager();
        SideChooserDialogFragment f =SideChooserDialogFragment.newInstance(threadDescription);
        f.setTargetFragment(this, SideChooserDialogFragment.REQUEST_CODE_SIDE_DIALOG);
        f.show(fm, "");
    }

    //handle callbacks
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SideChooserDialogFragment.REQUEST_CODE_SIDE_DIALOG) {
            boolean agree = data.getBooleanExtra("side", true);
            if(agree)
                side= "agree";
            else
                side= "disagree";
            verify(mContext, threadType, threadId);
        }

    }

    //check room status after user selects a side from the dialog
    private void verify(Context context, String type, String thread_id){
        result = viewRoomStatus(context, type, thread_id);
        String[] splittedString = result.split("-");
        result = splittedString[splittedString.length - 1];
        Log.d("STATE", "viewR: " + result);
        //Log.d("STATE", "side: " + side);

        //Error checking for room status
        if (result != null && !result.equals("Number of disagreeing users is at maximum")
                && !result.equals("Number of agreeing users is at maximum")
                && !result.equals("Room/Thread Doesn't Exist")) {
            String join = "join";
            // chooseSideDialog(context, thread_id, type);

            result = joinRoom(context, join, thread_id, result);
            goToChat(result);

        } else if(result.equals("Number of disagreeing users is at maximum")){
            Toast.makeText(getActivity(), "Number of disagreeing users is at maximum!", Toast.LENGTH_LONG).show();
        }
        else if(result.equals("Number of agreeing users is at maximum")){
            Toast.makeText(getActivity(), "Number of agreeing users is at maximum!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
        }
    }

    public String viewRoomStatus(Context context, String type, String thread_id){
        session = new SessionManager(getActivity().getApplicationContext());
        String result = null;

        ChatRoomHandler chatRoomHandler = new ChatRoomHandler(context);
        try {
            result = chatRoomHandler.execute(type, thread_id, side).get();
            Log.d("STATE", "view: " + result);
            //Toast.makeText(getActivity().getApplicationContext(), "view: " + store, Toast.LENGTH_LONG).show();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String joinRoom(Context context, String type, String thread_id, String userJSON){
        session = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(SessionManager.KEY_NAME);
        String token = MainActivity.appToken;
        String result = null;

        ChatRoomHandler chatRoomHandler = new ChatRoomHandler(context);

        try {
            result = chatRoomHandler.execute(type, thread_id, username, token, side).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        chatRoomManager = new ChatRoomManager(getActivity().getApplicationContext());
        chatRoomManager.updateUserRoomSession(thread_id, side, "false");
        return result;
    }

    private void goToChat(String result){

        HashMap<String, String> user = chatRoomManager.getUserDetails();
        String spectate = user.get(ChatRoomManager.SPECTATOR);
        if(spectate.equals("false")) {
            //Error checking for join status
            if (result != null && !result.equals("Number of disagreeing users is at maximum")
                    && !result.equals("Number of agreeing users is at maximum")
                    && !result.equals("Room/Thread Doesn't Exist")) {

                Intent startChildActivityIntent = new Intent(getActivity(), ChatActivity.class);
                startChildActivityIntent.putExtra("title", threadTitle);
                startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, result);
                startActivity(startChildActivityIntent);

            } else {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            }
        }//for joining arguments
        else {
            Intent startChildActivityIntent = new Intent(getActivity(), ChatActivity.class);
            startChildActivityIntent.putExtra("title", threadTitle);
            startChildActivityIntent.putExtra("isSpectate", "true");
            startActivity(startChildActivityIntent);
        }//for spectating
    }

    public void spectate(int p){
        ThreadsHelper threadsHelper = (ThreadsHelper) threadListAdapter.getItem(p);
        String thread_id = threadsHelper.getThread_id();
        String type = "spectate";
        session = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(SessionManager.KEY_NAME);

        SpectateRoomHandler spectateRoomHandler = new SpectateRoomHandler(getActivity().getApplicationContext());

        try {
            //Log.d("JOINSPECTATE", "yes");
            result = spectateRoomHandler.execute(type, thread_id, username).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (result.equals("Spectate room failed")){
            Toast.makeText(getActivity(), "spectate room failed!", Toast.LENGTH_LONG).show();
        }
        else{
            chatRoomManager = new ChatRoomManager(getActivity().getApplicationContext());
            chatRoomManager.updateUserRoomSession(thread_id, null, "true");
            goToChat(result);
        }
    }
}

