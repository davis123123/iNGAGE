package ingage.ingage20.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.handlers.MySQLDbHelper;
import ingage.ingage20.handlers.QueryThreadsHandler;

/**
 * Created by Davis on 4/24/2017.
 */

public class test2 extends Activity {
    String JSON_STRING;
    MySQLDbHelper mySQLDbHelper;
    QueryThreadsHandler queryThreadsHandler;
    String mResult = "asdasd";
    Button mbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test2);


        queryThreadsHandler = new QueryThreadsHandler();
        //mySQLDbHelper = new MySQLDbHelper();
        //mResult = MySQLDbHelper.getJson_string();
        //mySQLDbHelper = new MySQLDbHelper();
        try {
            mResult = queryThreadsHandler.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        setText();

    }


    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String json_url;


        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputstream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputstream));
                StringBuilder stringBuilder = new StringBuilder();

                while((JSON_STRING = bufferedReader.readLine()) != null){
                    stringBuilder.append(JSON_STRING+"\n");
                }

                bufferedReader.close();
                inputstream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute(){
            json_url = "http://10.0.0.199/query_post.php";

        }


        @Override
        protected void onProgressUpdate(Void... values){
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result){
            //TextView textView = (TextView) findViewById(R.id.test2view);
            //textView.setText(result);
            mResult = result;

            //MySQLDbHelper.setJson_string(result);
        }
    }

    public void setText(){
        TextView textView = (TextView) findViewById(R.id.test2view);
        textView.setText(mResult);
    }

}
