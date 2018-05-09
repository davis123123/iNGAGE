package ingage.ingage.fragments;

import android.content.Intent;
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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage.activities.PostThreadActivity;
import ingage.ingage.R;
import ingage.ingage.managers.SessionManager;
import ingage.ingage.adapters.ThreadListAdapter;
import ingage.ingage.handlers.QueryThreadsHandler;
import ingage.ingage.helpers.ThreadsHelper;

/**
 * Created by Davis on 4/4/2017.
 */

public class ArchivedFragment extends FragmentBase implements ThreadListAdapter.ItemClickCallback{

    QueryThreadsHandler queryThreadsHandler;

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    int rowCount;
    String default_path = "data:image/JPG;base64,";
    private static final String TAG = "ArchivedFragment";

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
        threadListAdapter = new ThreadListAdapter(this, getActivity());
        rowCount = 0;
        getThreadsJSON(rowCount);

        rootView = inflater.inflate(R.layout.fragment_archived, container, false);
        rootView.setTag(TAG);

        return rootView;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

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
                //threadListAdapter.list.add(null);
                //threadListAdapter.notifyItemInserted(threadListAdapter.list.size() - 1);
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
                   // threadListAdapter.list.remove(threadListAdapter.list.size() - 1);
                   // threadListAdapter.notifyItemRemoved(threadListAdapter.list.size());
                    inflateThreads();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //getThreadsJSON(rowCount);

                //inflateThreads();

                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");
                        threadListAdapter.list.remove(threadListAdapter.list.size() - 1);
                        threadListAdapter.notifyItemRemoved(threadListAdapter.list.size());
                        rowCount += 10;
                        getThreadsJSON(rowCount);
                        inflateThreads();
                        threadListAdapter.setLoaded();
                    }
                }, 10000);*/
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
                        //threadListAdapter.list.add(null);
                        threadListAdapter.mOnLoadMoreListener.onLoadMore();
                        //}
                        //loading = false;

                        //rowCount += 10;
                        //getThreadsJSON(rowCount);
                        //inflateThreads();
                        //Do pagination.. i.e. fetch new data
                    }
                }
            }
        });
    }

    public void getThreadsJSON(int rowCount){
        queryThreadsHandler = new QueryThreadsHandler();
        SessionManager session = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String test = user.get(SessionManager.CATEGORY_TYPE);
        Log.d("ARCCATE"," "+ test + " " + rowCount);
        try {
            if(user.get(SessionManager.CATEGORY_TYPE)!= null) {

                json_string = queryThreadsHandler.execute("categoryArchived", user.get(SessionManager.CATEGORY_TYPE),String.valueOf(rowCount)).get();
                Log.d("ARCCATE2"," "+ json_string);
            }
            else {
                json_string = queryThreadsHandler.execute("archived", String.valueOf(rowCount)).get();
            }
            Log.d("STATE" , "query result : " + json_string);
            threadListAdapter.setLoaded(false);
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
        int count = 0;
        Log.d("archiveinflate", "clicked");
        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("server_response");
            String thread_id, thread_title, thread_content, thread_by, thread_date, thread_category;
            String thread_img_bitmap = null;
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
        checkIfNoThreads(count);
    }

}
