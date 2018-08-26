package ingage.ingage.handlers;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ingage.ingage.App;
import ingage.ingage.R;

/**
 * Created by wuv66 on 8/26/2018.
 */

public class FaqsHandler extends AsyncTask<String, String, String> {

    Context context;
    private FaqsHandler.AsyncInterface asyncInterface;

    public interface AsyncInterface {
        void response(String response);
    }

    public FaqsHandler(Context context) {
        this.context = context;
        this.asyncInterface = (FaqsHandler.AsyncInterface) context;
    }

    @Override
    protected String doInBackground(String... params) {
        String ip = App.getAppContext().getResources().getString(R.string.ip);
        String post_thread_url = "http://" + ip + "/faqs.php";

            try {
                URL url = new URL(post_thread_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
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
        return null;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(String... String){
        super.onProgressUpdate(String);
    }

    @Override
    protected void onPostExecute(String result) {
        asyncInterface.response(result);
    }

}
