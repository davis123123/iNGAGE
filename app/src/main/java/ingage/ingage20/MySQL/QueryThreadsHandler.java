package ingage.ingage20.MySQL;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Davis on 4/14/2017.
 */

public class QueryThreadsHandler extends AsyncTask<Void, Void, String> {
        String json_url;
        String JSON_STRING;
        Context context;
        MySQLDbHelper mySQLDbHelper;
        //static public String mResult = "asda";

    /**
    public QueryThreadsHandler(Context mcontext) {
        context = mcontext;

    }**/

    @Override
    protected String doInBackground(Void... voids) {
            try{
            URL url= new URL(json_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            while ((JSON_STRING = bufferedReader.readLine()) != null){
            stringBuilder.append(JSON_STRING+"\n");
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
            e.printStackTrace();
            } catch (IOException e) {
            e.printStackTrace();
            }

            return null;
            }

    @Override
    protected void onPreExecute(){
            json_url = "http://24.7.128.143/query_post.php";
            }

    @Override
    protected void onProgressUpdate(Void... values){
        super.onProgressUpdate();
        }

    @Override
    protected void onPostExecute(String result){
        }

}
