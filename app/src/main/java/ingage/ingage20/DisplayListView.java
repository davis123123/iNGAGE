package ingage.ingage20;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import ingage.ingage20.Handlers.MySQLDbHelper;
import ingage.ingage20.Handlers.QueryThreadsHandler;
import ingage.ingage20.Adapters.Testadapter;

/**
 * Created by Davis on 4/17/2017.
 */

public class DisplayListView extends Activity{
    String json_string;
    TextView textView;
    MySQLDbHelper mySQLDbHelper;
    Context mContext;
    JSONObject jsonObject;
    JSONArray jsonArray;
    Testadapter testadapter;
    ListView listView;
    QueryThreadsHandler queryThreadsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getJSON();
        setContentView(R.layout.display_listview_layout);
        mySQLDbHelper = new MySQLDbHelper();
        listView = (ListView)findViewById(R.id.listview);
        testadapter = new Testadapter(this,R.layout.thread_row_layout);
        //setContentView(R.layout.thread_row_layout);
        //textView = (TextView) findViewById(R.id.thread_title_view);
        //textView.setText(json_string);
        listView.setAdapter(testadapter);

        /**try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("server_response");
            int count = 0;
            String thread_title, thread_content, thread_by, thread_date, thread_category;
            while(count < jsonArray.length()){
                JSONObject JO= jsonArray.getJSONObject(count);
                thread_title = JO.getString("thread_title");
                thread_content = JO.getString("thread_content");
                thread_by = JO.getString("thread_by");
                thread_date = JO.getString("thread_date");
                thread_category = JO.getString("thread_category");
                ThreadsHelper threadsHelper = new ThreadsHelper(thread_title, thread_content, thread_by, thread_date, thread_category);
                HashMap<String, String> threadsDetail = new HashMap<String, String>();
                threadsDetail.put("TAG_TITLE", thread_title);
                threadsDetail.put("TAG_CONTENT", thread_content);
                threadsDetail.put("TAG_BY", thread_by);
                threadsDetail.put("TAG_DATE", thread_date);
                testadapter.add(threadsHelper);
                count++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }**/

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
