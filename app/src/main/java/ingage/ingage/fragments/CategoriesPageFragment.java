package ingage.ingage.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
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

import ingage.ingage.handlers.QueryThreadsHandler;
import ingage.ingage.helpers.ThreadsHelper;
import ingage.ingage.R;
import ingage.ingage.adapters.ThreadListAdapter;
import ingage.ingage.managers.SessionManager;

/**
 * Created by Davis on 4/4/2017.
 */

public class CategoriesPageFragment extends FragmentBase implements ThreadListAdapter.ItemClickCallback{

    QueryThreadsHandler queryThreadsHandler;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    int rowCount;
    String default_path = "data:image/JPG;base64,";
    private static final String TAG = "CategoriesPageFragment";

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
        Log.d("ROWCOUNT","num"+rowCount);
        threadListAdapter = new ThreadListAdapter(this, getActivity(), true);
        rowCount = 0;
        getThreadsJSON(rowCount);
        rootView = inflater.inflate(R.layout.fragment_archived, container, false);
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

        threadListRecyclerView.setAdapter(threadListAdapter);
        Log.d("STATE", "serverstring" + json_string);

        inflateThreads();

        threadListAdapter.setOnLoadMoreListener(new ThreadListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                Log.d("haint", "Load More");
                rowCount += 10;
                Thread getJSON = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getThreadsJSON(rowCount);
                        while (true){
                            if(!threadListAdapter.getLoadStat()){
                                Log.d("haint", "Load More222");
                                break;
                            }//no longer loading
                        }
                    }
                });
                getJSON.start();
                try {
                    getJSON.join();
                    //threadListAdapter.list.remove(threadListAdapter.list.size() - 1);
                    //threadListAdapter.notifyItemRemoved(threadListAdapter.list.size());
                    inflateThreads();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        threadListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                Log.d("...", "Lastnot Item Wow !");
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    if ( !threadListAdapter.isLoading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        //if(mOnLoadMoreListener != null){
                        Log.d("...", "Last Item Wow !");
                        threadListAdapter.isLoading = true;
                    //    threadListAdapter.list.add(null);
                        threadListAdapter.mOnLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
    }

    public void getThreadsJSON(int rowCount){
        queryThreadsHandler = new QueryThreadsHandler();
        session = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String type = user.get(SessionManager.PAGE_TYPE);
        String categoryType = user.get(SessionManager.CATEGORY_TYPE);
        try {
            if(categoryType != null)
                json_string = queryThreadsHandler.execute("categoryDate", categoryType, String.valueOf(rowCount)).get();
            else
                json_string = queryThreadsHandler.execute("date", String.valueOf(rowCount)).get();
            Log.d("STATE" , "query result : " + json_string);
            threadListAdapter.setLoaded(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
        int count = 0;
        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("server_response");
            String thread_id, thread_title, thread_content, thread_by, thread_date, thread_category, thread_time_remaining;
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
                thread_time_remaining = JO.getString("seconds_remaining");
                long timer = time_remaining(thread_time_remaining);

                ThreadsHelper threadsHelper = new ThreadsHelper(thread_id, thread_title,
                        thread_content, thread_by, thread_date, thread_category, thread_img, timer);
                threadListAdapter.add(threadsHelper);
                threadListAdapter.notifyDataSetChanged();
                count++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        checkIfNoThreads(count);
    }

    private long time_remaining(String thread_time_passed){
        int[] time = new int[3];
        int total_seconds = Integer.parseInt(thread_time_passed);
        int full_duration = 43200; //12 hrs
        int seconds_remaining = full_duration - total_seconds;
        long miliseconds = seconds_remaining * 1000;
        /*
        int hours = seconds_remaining / 3600;
        int minutes = (seconds_remaining - (hours * 3600)) / 60;
        int seconds = (seconds_remaining - (hours * 3600) - (minutes * 60));
        time[0] = hours;
        time[1] = minutes;
        time[2] = seconds;*/
        return miliseconds;
    }//array with index 0=hours 1=minutes 2=seconds;

    private String time_remaining_display(int[] time){
        String time_display;
        if(time[0] < 1){
            if(time[1] < 1)
                return "Less than a minute remaining.";//if less than a minute
            int minutes = time[1];
            time_display = "Less than " + Integer.toString(minutes) + " remaining.";
            return time_display;
        }//if less than an hours remaining

        int hours = time[0];
        return "Less than " + Integer.toString(hours) + " remaining.";
    }

}

