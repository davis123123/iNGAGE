package ingage.ingage.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import ingage.ingage.R;
import ingage.ingage.handlers.MySQLDbHelper;
import ingage.ingage.handlers.QueryThreadsHandler;
import ingage.ingage.adapters.Testadapter;

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
        listView.setAdapter(testadapter);


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
