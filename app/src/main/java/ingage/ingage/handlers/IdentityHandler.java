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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import ingage.ingage.App;
import ingage.ingage.R;

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
        String type = params[0];//IMPORTANT

        String ip = App.getAppContext().getResources().getString(R.string.ip);
        String forgotpassword_url = "http://" + ip + "/forgot_password.php";
        String login_url = "http://" + ip + "/login.php";
        String registration_url = "http://" + ip + "/registration.php";
        String sign_out_url = "http://" + ip + "/sign_out.php";
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

        else if (type.equals("forgot_password")){
            String user_name = params[1];
            String email = params[2];

            try {
                URL url = new URL(forgotpassword_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8")+"&"+
                        URLEncoder.encode("token", "UTF-8")+"="+URLEncoder.encode(email,"UTF-8");
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
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        else if (type.equals("reset_password")){
            String old_password = params[1];
            String new_password = params[2];

            try {
                URL url = new URL(forgotpassword_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(old_password,"UTF-8")+"&"+
                        URLEncoder.encode("token", "UTF-8")+"="+URLEncoder.encode(new_password,"UTF-8");
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
            } catch (IOException e) {
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
                String full_name = params[1];
                String user_name = params[2];
                String password = params[3];
                String email = params[4];
                String date_joined = params[5];
                String initialSubscription = params[6];

                URL url = new URL(registration_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data =
                        URLEncoder.encode("full_name","UTF-8")+"="+ URLEncoder.encode(full_name,"UTF-8")+"&"+
                        URLEncoder.encode("user_name","UTF-8")+"="+ URLEncoder.encode(user_name,"UTF-8")+"&"+
                        URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"+
                        URLEncoder.encode("email","UTF-8")+"="+ URLEncoder.encode(email,"UTF-8")+"&"+
                        URLEncoder.encode("date_joined","UTF-8")+"="+ URLEncoder.encode(date_joined,"UTF-8")+"&"+
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
