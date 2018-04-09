package ingage.ingage20.handlers;

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
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Davis on 5/1/2017.
 */

public class UploadImageHandler extends AsyncTask<String, String, String> {
    Bitmap image;
    public UploadImageHandler(Bitmap image) {
        this.image = image;

    }


    @Override
    protected String doInBackground(String... params) {
        String post_image_url = "http://107.170.232.60/upload_image.php";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

        try {
            String thread_title = params[0];

            URL url = new URL(post_image_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data =
                    URLEncoder.encode("thread_title", "UTF-8") + "=" + URLEncoder.encode(thread_title, "UTF-8") + "&" +
                            URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(encodedImage, "UTF-8");
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
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(String result) {
    }

}

