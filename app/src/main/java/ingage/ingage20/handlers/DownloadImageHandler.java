package ingage.ingage20.handlers;

import android.app.AlertDialog;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Davis on 5/1/2017.
 */

public class DownloadImageHandler extends AsyncTask<String, String, String>{

    Context context;
    AlertDialog alertDialog;

    public DownloadImageHandler(Context mcontext){
        context = mcontext;
    }


    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        //TODO need change for server change
        String post_thread_url ="http://24.7.128.143/download_image.php";  //10.0.2.2 CHANGE FOR OTHER SERVER

        if (type.equals("download")) {
            try {
                String thread_id = params[1];
                URL url = new URL(post_thread_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data =
                        URLEncoder.encode("thread_id","UTF-8")+"="+ URLEncoder.encode(thread_id,"UTF-8");
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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Downloading Image");
    }

    @Override
    protected void onProgressUpdate(String... String){
        super.onProgressUpdate(String);
    }

    @Override
    protected void onPostExecute(String result) {

    }

}
