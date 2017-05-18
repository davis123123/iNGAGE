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
import ingage.ingage20.MainActivity;
import ingage.ingage20.handlers.ChatRoomHandler;
import ingage.ingage20.handlers.MySQLDbHelper;
import ingage.ingage20.handlers.QueryThreadsHandler;
import ingage.ingage20.helpers.ThreadsHelper;
import ingage.ingage20.PostThreadActivity;
import ingage.ingage20.R;
import ingage.ingage20.SessionManager;
import ingage.ingage20.adapters.ThreadListAdapter;

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

    MySQLDbHelper mySQLDbHelper;
    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    String side = "agree";      //set to agree by default

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

        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("server_response");
            int count= 0;
            String thread_id, thread_title, thread_content, thread_by, thread_date, thread_category;
            while(count < jsonArray.length()){
                JSONObject JO = jsonArray.getJSONObject(count);
                thread_id = JO.getString("thread_id");
                thread_title = JO.getString("thread_title");
                thread_content = JO.getString("thread_content");
                thread_by = JO.getString("thread_by");
                thread_date = JO.getString("thread_date");
                thread_category = JO.getString("thread_category");
                ThreadsHelper threadsHelper = new ThreadsHelper(thread_id, thread_title,
                        thread_content,thread_by,thread_date, thread_category);
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
        String result = null;
        result = viewRoomStatus(context, type, thread_id);

        //Error checking for room status
        if (result != null && !result.equals("Number of disagreeing users is at maximum")
                && !result.equals("Number of agreeing users is at maximum")
                && !result.equals("Room/Thread Doesn't Exist")) {
            type = "join";
            result = joinRoom(context, type, thread_id);
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
        } else {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
        }


        /**
        Intent startChildActivityIntent = new Intent(getActivity(), ViewThreadActivity.class);
        startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, thread_id);
        startActivity(startChildActivityIntent);**/
    }

    private void chooseSideDialog(){

        new AlertDialog.Builder(getActivity())
                .setTitle("Choose a side")
                .setMessage("Do you agree/disagree with this issue?")
                .setPositiveButton("agree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setNegativeButton("disagree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       side= "disagree";
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public String viewRoomStatus(Context context, String type, String thread_id){
        session = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(SessionManager.KEY_NAME);
        String token = MainActivity.appToken;
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

    public String joinRoom(Context context, String type, String thread_id){

        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(SessionManager.KEY_NAME);
        String token = MainActivity.appToken;
        String result = null;

        JSONObject objJson= new JSONObject();
        JSONArray arrJSON = new JSONArray();
        try {
            objJson.put("user_name", username);
            arrJSON.put(objJson);
            objJson = new JSONObject();
            objJson.put("token", token);
            arrJSON.put(objJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ChatRoomHandler chatRoomHandler = new ChatRoomHandler(context);

        try {
            chooseSideDialog();
            result = chatRoomHandler.execute(type, thread_id, arrJSON.toString(), side).get();
            Log.d("STATE", "join: " + result);
            //Toast.makeText(getActivity().getApplicationContext(), "view: " + store, Toast.LENGTH_LONG).show();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void getThreadsJSON(){
        queryThreadsHandler = new QueryThreadsHandler();
        try {
            json_string = queryThreadsHandler.execute().get();
            Log.d("STATE" , "result : " + json_string);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
