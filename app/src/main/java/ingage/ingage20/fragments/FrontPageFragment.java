package ingage.ingage20.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import ingage.ingage20.ChatRoomActivity;
import ingage.ingage20.MySQL.MySQLDbHelper;
import ingage.ingage20.MySQL.QueryThreadsHandler;
import ingage.ingage20.MySQL.ThreadsHelper;
import ingage.ingage20.R;
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

    MySQLDbHelper mySQLDbHelper;
    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;

    Toast mToast;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        getJSON();
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
        startActivity(new Intent(getActivity(),ChatRoomActivity.class));
    }


    @Override
    public void onListItemClick(int clickedItemIndex) {

        if(mToast != null){
            mToast.cancel();
        }
        ThreadsHelper threadsHelper = (ThreadsHelper) threadListAdapter.getItem(clickedItemIndex);
        String thread_id = threadsHelper.getThread_id();

        //LEAVVE UNJTIL COMMENTS A RE FINISSHED
        String toastMessage = "Item #" + thread_id + "clicked.";
        mToast = Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_LONG);
        mToast.show();

        /**
        Intent startChildActivityIntent = new Intent(getActivity(), ViewThreadActivity.class);
        startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, thread_id);
        startActivity(startChildActivityIntent);**/

    }

    public void getJSON(){
        queryThreadsHandler = new QueryThreadsHandler();
        try {
            json_string = queryThreadsHandler.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
