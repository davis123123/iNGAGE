package ingage.ingage20.handlers;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Davis on 6/12/2017.
 */

public class SearchHandler extends AsyncTask<String, String, String> {
        //String json_url;
        String JSON_STRING;
        Context context;
        MySQLDbHelper mySQLDbHelper;
    @Override
    protected String doInBackground(String... params) {

        String type = params[0];

        String search_title_url = "http://10.0.0.199/search_title_url.php";
        if(type.equals("search")) {
        try {
        String rowCount = params[1];
        String searchString = params[2];
        URL url = new URL(search_title_url);
        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        OutputStream outputStream = httpURLConnection.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        String post_data =
        URLEncoder.encode("rowCount","UTF-8")+"="+ URLEncoder.encode(rowCount,"UTF-8") + "&" +
            URLEncoder.encode("rowCount","UTF-8")+"="+ URLEncoder.encode(rowCount,"UTF-8");
        bufferedWriter.write(post_data);
        bufferedWriter.flush();
        bufferedWriter.close();
        outputStream.close();
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
        String result = "";
        String line;
        while((line = bufferedReader.readLine()) != null){
        result += line;
        }
        bufferedReader.close();;
        inputStream.close();
        httpURLConnection.disconnect();
        return result;
        } catch (IOException e) {
        e.printStackTrace();
        }
        }

    return null;
    }

    @Override
    protected void onPreExecute(){
    }

    @Override
    protected void onProgressUpdate(String... values){
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result){
    }

}
