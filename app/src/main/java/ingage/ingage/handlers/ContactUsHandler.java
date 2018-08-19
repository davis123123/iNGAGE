package ingage.ingage.handlers;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
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
 * Created by wuv66 on 8/18/2018.
 */

public class ContactUsHandler  extends AsyncTask<String, String, String> {

    Context context;
    private ContactUsHandler.AsyncInterface asyncInterface;

    public interface AsyncInterface {
        void response(String response);
    }

    public ContactUsHandler(Context context) {
        this.context = context;
        this.asyncInterface = (ContactUsHandler.AsyncInterface) context;
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];

        String ip = App.getAppContext().getResources().getString(R.string.ip);
        String post_thread_url = "http://" + ip + "/contact_us.php";

        if (type.equals("submit")) {
            try {
                String sender = params[1];
                String subject = params[2];
                String message = params[3];
                URL url = new URL(post_thread_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data =
                        URLEncoder.encode("sender","UTF-8")+"="+ URLEncoder.encode(sender,"UTF-8")+"&"+
                                URLEncoder.encode("subject", "UTF-8")+"="+URLEncoder.encode(subject,"UTF-8")+"&"+
                                URLEncoder.encode("message","UTF-8")+"="+ URLEncoder.encode(message,"UTF-8");
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
