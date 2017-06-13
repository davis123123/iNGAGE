package ingage.ingage20.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.activities.PostThreadActivity;
import ingage.ingage20.adapters.ThreadListAdapter;
import ingage.ingage20.handlers.SearchHandler;
import ingage.ingage20.helpers.ThreadsHelper;
import ingage.ingage20.managers.SessionManager;

/**
 * Created by Davis on 6/12/2017.
 */

public class SearchResultFragment extends FragmentBase implements ThreadListAdapter.ItemClickCallback{

    SearchHandler searchHandler;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    int rowCount = 0;
    String searchString = "";

    private static final String TAG = "FrontPageFragment";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

            View v = create(inflater, container, savedInstanceState);
            return v;
            }

    public View create(final LayoutInflater inflater, final ViewGroup container,
    final Bundle savedInstanceState){
            // Inflate the layout for this fragment

            getThreadsJSON(rowCount, searchString);
            rootView = inflater.inflate(R.layout.fragment_front_page, container, false);
            rootView.setTag(TAG);
            return rootView;
            }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);



        //TODO fix threadlistadapter for dynamic threads
        threadListRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_posts);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        threadListRecyclerView.setLayoutManager(layoutManager);


        threadListAdapter = new ThreadListAdapter(this);
        threadListRecyclerView.setAdapter(threadListAdapter);
        Log.d("STATE", "serverstring" + json_string);

        inflateThreads();

        postThreadButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        postThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == postThreadButton){
                goInsertThread();
                }
            }
            });


        threadListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //Log.d("...", "Lastnot Item Wow !");
                if(dy > 0){ //check for scroll down{
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        //loading = false;
                        Log.d("...", "Last Item Wow !");
                        rowCount += 10;
                        getThreadsJSON(rowCount, searchString);
                        inflateThreads();

                        //Do pagination.. i.e. fetch new data
                        }
                    }
                }
            }
        });
    }

    public void getThreadsJSON(int rowCount, String searchString){
        searchHandler = new SearchHandler();
        Log.d("ROWCOUNT" , " result : " + rowCount);
        session = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String type = user.get(SessionManager.PAGE_TYPE);
        try {
        json_string = searchHandler.execute(type, String.valueOf(rowCount), searchString).get();
        Log.d("STATE" , "query result : " + json_string);
        } catch (InterruptedException e) {
        e.printStackTrace();
        } catch (ExecutionException e) {
        e.printStackTrace();
        }
    }

    public void goInsertThread(){
            startActivity(new Intent(getActivity(),PostThreadActivity.class));
            }


    @Override
    public void onContainerClick(int p) {
            itemClick(p);
            }



    @Override
    public void onSpectateBtnClick(int p) {
        Log.d("SPECTATEBUTTON", "clicked");
        spectate(p);
    }


    void inflateThreads() {
    try {

        jsonObject = new JSONObject(json_string);
        jsonArray = jsonObject.getJSONArray("server_response");
        int count = 0;
        String thread_id, thread_title, thread_content, thread_by, thread_date, thread_category;
        String thread_img = null;
        while (count < jsonArray.length()) {
            JSONObject JO = jsonArray.getJSONObject(count);
            thread_id = JO.getString("thread_id");
            thread_title = JO.getString("thread_title");
            thread_content = JO.getString("thread_content");
            thread_by = JO.getString("thread_by");
            thread_date = JO.getString("thread_date");
            thread_category = JO.getString("thread_category");
            thread_img = JO.getString("thread_image_link");
            ThreadsHelper threadsHelper = new ThreadsHelper(thread_id, thread_title,
                    thread_content, thread_by, thread_date, thread_category, thread_img);
            threadListAdapter.add(threadsHelper);
            threadListAdapter.notifyDataSetChanged();
            count++;
        }
    } catch (JSONException e) {
        e.printStackTrace();
        }
    }
}
