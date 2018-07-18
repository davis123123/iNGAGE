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
import java.net.URL;
import java.net.URLEncoder;

import ingage.ingage.App;
import ingage.ingage.R;

/**
 * Created by Davis on 5/30/2017.
 */

public class ChatFeaturesHandler  extends AsyncTask<String, String, String> {

    Context context;
    AlertDialog alertDialog;

    public ChatFeaturesHandler(Context mcontext) {
        context = mcontext;
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String ip = App.getAppContext().getResources().getString(R.string.ip);

        String send_token_url = "http://" + ip + "/send_coin.php";
        String insert_vote_url = "http://" + ip + "/insert_vote.php";
        String use_coin_url = "http://" + ip + "/use_coin.php";

        if (type.equals("send_coin")) {
            try {
                String target_user = params[1];
                URL url = new URL(send_token_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data =
                        URLEncoder.encode("username","UTF-8")+"="+ URLEncoder.encode(target_user,"UTF-8");
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

        else if (type.equals("use_coin")) {
            try {
                String username = params[1];
                URL url = new URL(use_coin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data =
                        URLEncoder.encode("username","UTF-8")+"="+ URLEncoder.encode(username,"UTF-8");
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

        else if (type.equals("insert_vote")) {
            try {
                String username = params[1];
                String thread_id = params[2];
                String prev_voted = params[3];
                String chat_id = params[4];
                String vote = params[5];
                String chat_side = params[6];
                String chat_user = params[7];
                URL url = new URL(insert_vote_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data =
                        URLEncoder.encode("username","UTF-8")+"="+ URLEncoder.encode(username,"UTF-8")+"&"+
                                URLEncoder.encode("vote_type","UTF-8")+"="+ URLEncoder.encode(vote,"UTF-8")+"&"+
                                URLEncoder.encode("thread_id","UTF-8")+"="+ URLEncoder.encode(thread_id,"UTF-8")+"&"+
                                URLEncoder.encode("chat_id","UTF-8")+"="+ URLEncoder.encode(chat_id,"UTF-8")+"&"+
                                URLEncoder.encode("chat_side","UTF-8")+"="+ URLEncoder.encode(chat_side,"UTF-8")+"&"+
                                URLEncoder.encode("prev_voted","UTF-8")+"="+ URLEncoder.encode(prev_voted,"UTF-8")+"&"+
                                URLEncoder.encode("chat_user","UTF-8")+"="+ URLEncoder.encode(chat_user,"UTF-8");
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
    protected void onProgressUpdate(String... String){
        super.onProgressUpdate(String);
    }

    @Override
    protected void onPostExecute(String result) {

    }
}