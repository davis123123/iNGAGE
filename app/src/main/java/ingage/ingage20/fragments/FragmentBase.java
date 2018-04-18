package ingage.ingage20.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.activities.ChatActivity;
import ingage.ingage20.activities.MainActivity;
import ingage.ingage20.adapters.ThreadListAdapter;
import ingage.ingage20.handlers.ChatRoomHandler;
import ingage.ingage20.handlers.SpectateRoomHandler;
import ingage.ingage20.helpers.ThreadsHelper;
import ingage.ingage20.managers.ChatRoomManager;
import ingage.ingage20.managers.SessionManager;

/**
 * Created by Davis on 4/5/2017.
 */

public class FragmentBase extends Fragment{

    Context mContext;
    protected RecyclerView threadListRecyclerView;
    static ThreadListAdapter threadListAdapter;
    View rootView;
    static TextView msg;
    static ImageView icon;

    SessionManager session;
    HashMap<String, String> user;

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

    private SwipeRefreshLayout swipeContainer;

    Toast mToast;

    public static class RefreshEvent {}

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUserDetails();
        chatRoomManager = new ChatRoomManager(getActivity().getApplicationContext());
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        msg = (TextView) view.findViewById(R.id.tvMsg);
        icon = (ImageView) view.findViewById(R.id.ivIcon);

        int color = getContext().getResources().getColor(R.color.gray);
        icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EventBus.getDefault().post(new RefreshEvent());
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.darker_gray);
    }

    //If there's no threads, then display helper message
    public static void checkIfNoThreads(int total){
        Log.d("search count ", String.valueOf(total));
        if (total < 1) {
            msg.setVisibility(View.VISIBLE);
            icon.setVisibility(View.VISIBLE);
        }
        else {
            msg.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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

        String type = "view";
        chooseSideDialog(context, thread_id, type);
    }

    private void chooseSideDialog(final Context context, final String thread_id, final String type){

        String userNo = viewRoomStatus(context, type, thread_id);
        if(userNo.equals("thread archived")){
            Toast.makeText(getActivity(), "This discussion has just ended. Please try another post.", Toast.LENGTH_LONG).show();
            return;
        }
        //index 0 for disagree, index 1 for agree
        Log.d("Viewchat",userNo);
        String[] splittedString = userNo.split("-");
        String remainingTime = splittedString[0];
        //Toast.makeText(getActivity(), splittedString[1] + " " +splittedString[2], Toast.LENGTH_LONG).show();
        threadCapacity = "No. Disagree users: " + splittedString[1] + "/3\n" + "No. Agree users: " + splittedString[2] +"/3";
        mContext = getActivity().getApplicationContext();
        threadId = thread_id;
        threadType = type;

        android.support.v4.app.FragmentManager fm = getFragmentManager();
        SideChooserDialogFragment f = SideChooserDialogFragment.newInstance(threadDescription);
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

        if(result.equals("thread archived")){
            Toast.makeText(getActivity(), "This discussion has just ended. Please try another post.", Toast.LENGTH_LONG).show();
            return;
        }
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
            Log.d("Joinchat", result);
            String[] splitResult = result.split("-");
            result = splitResult[1];

            String timeRemaining = splitResult[0];
            chatRoomManager.updateTimeRemaining(timeRemaining);

            Toast.makeText(getActivity(), "Time remaining on this Debate: " + timeRemaining, Toast.LENGTH_LONG).show();
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
        String result = null;
        Log.d("VIEW", thread_id);
        ChatRoomHandler chatRoomHandler = new ChatRoomHandler(context);
        try {
            result = chatRoomHandler.execute(type, thread_id, side).get();
            Log.d("VIEW", result);
            //Toast.makeText(getActivity().getApplicationContext(), "view: " + result, Toast.LENGTH_LONG).show();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String joinRoom(Context context, String type, String thread_id, String userJSON){
        String username = user.get(SessionManager.KEY_NAME);
        String token = MainActivity.appToken;
        String result = null;

        ChatRoomHandler chatRoomHandler = new ChatRoomHandler(context);

        try {
            result = chatRoomHandler.execute(type, thread_id, username, token, side).get();
            Log.d("STATE", "view: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

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
        threadTitle = threadsHelper.getThread_title();
        String type = "spectate";
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
            chatRoomManager.updateUserRoomSession(thread_id, null, "true");
            goToChat(result);
        }
    }
}