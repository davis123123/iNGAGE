package ingage.ingage.handlers;

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

import ingage.ingage.App;
import ingage.ingage.R;

/**
 * Created by Davis on 4/17/2017.
 */

public class SubmitCommentsHandler extends AsyncTask<String, String, String> {
    Context context;
    AlertDialog alertDialog;

    public SubmitCommentsHandler(Context mcontext){
        context = mcontext;
    }


    @Override
    protected String doInBackground(String...params) {
        String type = params[0];

        String ip = App.getAppContext().getResources().getString(R.string.ip);
        String post_comment_url = "http://" + ip + "/insert_comment.php";

        if (type.equals("submit")) {
            try {
                String thread_id = params[1];
                String comment_content = params[2];
                String comment_by = params[3];
                String comment_side = params[4];

                URL url = new URL(post_comment_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data =
                        URLEncoder.encode("thread_id", "UTF-8") + "=" + URLEncoder.encode(thread_id, "UTF-8") + "&" +
                        URLEncoder.encode("comment_content", "UTF-8") + "=" + URLEncoder.encode(comment_content, "UTF-8") + "&" +
                                URLEncoder.encode("comment_by", "UTF-8") + "=" + URLEncoder.encode(comment_by, "UTF-8") + "&" +
                                URLEncoder.encode("comment_side", "UTF-8") + "=" + URLEncoder.encode(comment_side, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                ;
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
            alertDialog.setTitle("Submission status");
        }

        @Override
        protected void onPostExecute(String result) {
        }

}