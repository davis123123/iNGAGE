package ingage.ingage.handlers;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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
 * Created by Davis on 4/9/2017.
 */

public class SubmitThreadsHandler extends AsyncTask<String, String, String> {

    Context context;
    private AsyncInterface asyncInterface;
    AlertDialog alertDialog;
    Bitmap image;

    public interface AsyncInterface {
        void response(String response);
    }

    public SubmitThreadsHandler(Context context, Bitmap image) {
        this.context = context;
        this.image = image;
        this.asyncInterface = (AsyncInterface) context;
    }



    @Override
    protected String doInBackground(String... params) {
        String type = params[0];

        String ip = App.getAppContext().getResources().getString(R.string.ip);
        String post_thread_url = "http://" + ip + "/insert_thread.php";

        if (type.equals("submit")) {
            try {
                String thread_title = params[1];
                String thread_content = params[2];
                String thread_by = params[3];
                String thread_category = params[4];
                String thread_image_link = params[5];
                String used_image = params[6];
                String encodedImage = null;
                if(image != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                } else{
                    encodedImage = " ";
                }
                URL url = new URL(post_thread_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data =
                        URLEncoder.encode("thread_title","UTF-8")+"="+ URLEncoder.encode(thread_title,"UTF-8")+"&"+
                                URLEncoder.encode("thread_content", "UTF-8")+"="+URLEncoder.encode(thread_content,"UTF-8")+"&"+
                                URLEncoder.encode("thread_by","UTF-8")+"="+ URLEncoder.encode(thread_by,"UTF-8")+"&"+
                                URLEncoder.encode("thread_category","UTF-8")+"="+ URLEncoder.encode(thread_category,"UTF-8")+"&"+
                                URLEncoder.encode("thread_image_link","UTF-8")+"="+ URLEncoder.encode(thread_image_link,"UTF-8")+"&"+
                                URLEncoder.encode("used_image","UTF-8")+"="+ URLEncoder.encode(used_image,"UTF-8")+"&"+
                                URLEncoder.encode("image","UTF-8")+"="+ URLEncoder.encode(encodedImage,"UTF-8");
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