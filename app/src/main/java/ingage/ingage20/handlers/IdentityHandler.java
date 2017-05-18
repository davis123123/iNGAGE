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
 * Created by Davis on 4/6/2017.
 */

public class IdentityHandler extends AsyncTask<String, String, String> {
    Context context;
    AlertDialog alertDialog;
    public IdentityHandler(Context mcontext){
        context = mcontext;
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        //TODO need change for server change
        String login_url = "http://10.0.0.199/login.php";  //10.0.2.2 CHANGE FOR OTHER SERVER
        String registration_url = "http://24.7.128.143/registration.php";
        String sign_out_url = "http://10.0.0.199/sign_out.php";
        if (type.equals("sign_out")) {

            try {
                String user_name = params[1];
                String password = params[2];
                URL url = new URL(sign_out_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data =
                        URLEncoder.encode("user_name","UTF-8")+"="+ URLEncoder.encode(user_name,"UTF-8")+"&"+
                        URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password,"UTF-8");

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
                //SETS SIGNIN TO TRUE
                //signInHandler.setSignInState(true);
                return result;
            } catch (MalformedURLException e) {
                //IF FAILED TO SIGNIN
                //signInHandler.setSignInState(false);
                e.printStackTrace();
            } catch (IOException e) {
                //IF FAILED TO SIGNIN
                //signInHandler.setSignInState(false);
                e.printStackTrace();
            }

        }
        else if (type.equals("login")) {
            try {
                String user_name = params[1];
                String password = params[2];
                String token = params[3];

                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data =
                        URLEncoder.encode("user_name","UTF-8")+"="+ URLEncoder.encode(user_name,"UTF-8")+"&"+
                        URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"+
                        URLEncoder.encode("token", "UTF-8")+"="+URLEncoder.encode(token,"UTF-8");

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
                //SETS SIGNIN TO TRUE
                //signInHandler.setSignInState(true);
                return result;
            } catch (MalformedURLException e) {
                //IF FAILED TO SIGNIN
                //signInHandler.setSignInState(false);
                e.printStackTrace();
            } catch (IOException e) {
                //IF FAILED TO SIGNIN
                //signInHandler.setSignInState(false);
                e.printStackTrace();
            }

        }

        else if (type.equals("registration")){
            try {
                String user_name = params[1];
                String password = params[2];
                String email = params[3];
                String initialSubscription = params[4];

                URL url = new URL(registration_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data =
                        URLEncoder.encode("user_name","UTF-8")+"="+ URLEncoder.encode(user_name,"UTF-8")+"&"+
                        URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"+
                        URLEncoder.encode("email","UTF-8")+"="+ URLEncoder.encode(email,"UTF-8")+"&"+
                        URLEncoder.encode("thread_subscriptions","UTF-8")+"="+ URLEncoder.encode(initialSubscription,"UTF-8");
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
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(String result) {
        //TODO setup signin handler and new activity
    }

}
