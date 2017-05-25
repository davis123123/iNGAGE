package ingage.ingage20.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.ChatActivity;
import ingage.ingage20.ChatRoomManager;
import ingage.ingage20.MainActivity;
import ingage.ingage20.PostThreadActivity;
import ingage.ingage20.R;
import ingage.ingage20.SessionManager;
import ingage.ingage20.adapters.ThreadListAdapter;
import ingage.ingage20.handlers.ChatRoomHandler;
import ingage.ingage20.handlers.MySQLDbHelper;
import ingage.ingage20.handlers.QueryThreadsHandler;
import ingage.ingage20.helpers.ThreadsHelper;

/**
 * Created by Davis on 4/4/2017.
 */

public class FrontPageFragment extends FragmentBase implements ThreadListAdapter.ListItemClickListener{
    private static final String TAG = "FrontPageFragment";

    String JSON_STRING;
    FloatingActionButton postThreadButton;
    protected RecyclerView threadListRecyclerView;
    ThreadListAdapter threadListAdapter;
    QueryThreadsHandler queryThreadsHandler;
    View rootView;
    SessionManager session;
    ChatRoomManager chatRoomManager;

    MySQLDbHelper mySQLDbHelper;
    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    String side = "agree";      //set to agree by default

    String result = null;

    Toast mToast;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        getThreadsJSON();
        rootView = inflater.inflate(R.layout.fragment_front_page, container, false);
        rootView.setTag(TAG);
        return rootView;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        //TODO fix threadlistadapter for dynamic threads
        threadListRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_posts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        threadListRecyclerView.setLayoutManager(layoutManager);
        threadListAdapter = new ThreadListAdapter(this);
        threadListRecyclerView.setAdapter(threadListAdapter);
        Log.d("STATE", "serverstring" + json_string);
        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("server_response");
            int count= 0;
            String thread_id, thread_title, thread_content, thread_by, thread_date, thread_category;
            String thread_img = null;
            while(count < jsonArray.length()){
                JSONObject JO = jsonArray.getJSONObject(count);
                thread_id = JO.getString("thread_id");
                thread_title = JO.getString("thread_title");
                thread_content = JO.getString("thread_content");
                thread_by = JO.getString("thread_by");
                thread_date = JO.getString("thread_date");
                thread_category = JO.getString("thread_category");
                thread_img = JO.getString("thread_image_link");
                ThreadsHelper threadsHelper = new ThreadsHelper(thread_id, thread_title,
                        thread_content,thread_by,thread_date, thread_category, thread_img);
                threadListAdapter.add(threadsHelper);
                count++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        postThreadButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        postThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == postThreadButton){
                    goInsertThread();
                }
            }
        });
    }

    public void goInsertThread(){
        startActivity(new Intent(getActivity(),PostThreadActivity.class));
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

        Context context = getActivity().getApplicationContext();


        if(mToast != null){
            mToast.cancel();
        }
        ThreadsHelper threadsHelper = (ThreadsHelper) threadListAdapter.getItem(clickedItemIndex);
        String thread_id = threadsHelper.getThread_id();

        //LEAVE UNTIL COMMENTS A RE FINISHED
        String toastMessage = "Item #" + thread_id + "clicked.";
        mToast = Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_LONG);
        mToast.show();

        String type = "view";
        chooseSideDialog(context, thread_id, type);

        /**
         Intent startChildActivityIntent = new Intent(getActivity(), ViewThreadActivity.class);
         startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, thread_id);
         startActivity(startChildActivityIntent);**/
    }

    private void chooseSideDialog(final Context context, final String thread_id, final String type){


        new AlertDialog.Builder(getActivity())
                .setTitle("Choose a side")
                .setMessage("Do you agree/disagree with this issue?")
                .setPositiveButton("agree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        side= "agree";
                        //result = joinRoom(context, type, thread_id);

                        verify(context, type, thread_id);

                        //goToChat(result);
                    }
                })
                .setNegativeButton("disagree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        side= "disagree";

                        //result = joinRoom(context, type, thread_id);

                        verify(context, type, thread_id);

                        //goToChat(result);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //check room status after user selects a side from the dialog
    private void verify(Context context, String type, String thread_id){
        result = viewRoomStatus(context, type, thread_id);
        Log.d("STATE", "view: " + result);
        Log.d("STATE", "side: " + side);

        //Error checking for room status
        if (result != null && !result.equals("Number of disagreeing users is at maximum")
                && !result.equals("Number of agreeing users is at maximum")
                && !result.equals("Room/Thread Doesn't Exist")) {
            String join = "join";
            // chooseSideDialog(context, thread_id, type);

            result = joinRoom(context, join, thread_id, result);
            goToChat(result);

        } else {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
        }
    }

    private void goToChat(String result){

        //Error checking for join status
        if (result != null && !result.equals("Number of disagreeing users is at maximum")
                && !result.equals("Number of agreeing users is at maximum")
                && !result.equals("Room/Thread Doesn't Exist")){

            Intent startChildActivityIntent = new Intent(getActivity(), ChatActivity.class);
            startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, result);
            startActivity(startChildActivityIntent);

        } else{
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
        }


    }

    public String viewRoomStatus(Context context, String type, String thread_id){
        session = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
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
        chatRoomManager.updateUserRoomSession(thread_id, side);
        return result;
    }

    public void getThreadsJSON(){
        queryThreadsHandler = new QueryThreadsHandler();
        try {
            json_string = queryThreadsHandler.execute().get();
            Log.d("STATE" , "query result : " + json_string);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
