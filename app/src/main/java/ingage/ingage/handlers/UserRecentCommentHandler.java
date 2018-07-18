package ingage.ingage.handlers;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import ingage.ingage.App;
import ingage.ingage.R;
import ingage.ingage.activities.UserProfileActivity;
import ingage.ingage.util.RecentComment;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by wuv66 on 2/10/2018.
 */

public class UserRecentCommentHandler {

    public RecentComment[] arr;
    //public ArrayList<RecentComment> recentComments  = new ArrayList<>();
    String serverResponse;

    private CallBackData callBackData;
    public interface CallBackData{
        void notifyChange();
    }

    public interface Interface {

        @FormUrlEncoded
        @POST("http://{ip}/track_user_comment.php/")
        Call<ResponseBody> post(
                @Field("username") String username,
                @Field("thread_id") String thread_id,
                @Field("recent_comment") String recent_comment,
                @Field("side") String side,
                @Path("ip") String ip
        );

        @FormUrlEncoded
        @POST("http://{ip}/query_user_recent_comments.php/")
        Call<ResponseBody> get(
                @Field("username") String username,
                @Path("ip") String ip
        );
    }

    public UserRecentCommentHandler(){
    }

    public void setCallBackData(CallBackData callBackData){
        this.callBackData = callBackData;
    }

    //save recent comment in database
    public void enqueue(String username, String thread_id, String messageText, String side, String ip){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        String url = "http://" + ip +"/track_user_comment.php/";

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();

        Interface service = retrofit.create(Interface.class);

        Call<ResponseBody> call = service.post(username, thread_id, messageText, side, ip);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("STATE","Retrofit response code: " + response.code());

                if(response.isSuccessful()) {
                    Log.i("STATE","Retrofit POST Success");
                    try {
                        serverResponse = response.body().string();
                        Log.i("STATE","Retrofit reponse: " + serverResponse);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.i("Retrofit Error Code:", String.valueOf(response.code()));
                    Log.i("Retrofit Error Body", response.errorBody().toString());

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.i("STATE","Retrofit Failure");
            }
        });
    }

    //get all recent comments for each room, for the user
    public void enqueue(String username, String ip){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        String url = "http://" + ip +"/query_user_recent_comments.php/";

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();
        Interface service = retrofit.create(Interface.class);

        Call<ResponseBody> call = service.get(username, ip);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("STATE","Retrofit response code: " + response.code());

                if(response.isSuccessful()) {
                    Log.i("STATE","Retrofit POST Success");
                    try {
                        serverResponse = response.body().string();
                        createCommentsList(serverResponse);
                        callBackData.notifyChange();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.i("Retrofit Error Code:", String.valueOf(response.code()));
                    Log.i("Retrofit Error Body", response.errorBody().toString());

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.i("STATE","Retrofit Failure");
            }
        });
    }



    public void createCommentsList(String json){

        //Log.i("STATE","recent comment list json: " + json);
        Gson gson = new Gson();
        arr = gson.fromJson(json, RecentComment[].class);
        UserProfileActivity.recentComments.clear();
        //Log.i("VICTOR", "recent comment: " + UserProfileActivity.recentComments.get(i).thread_title + ", " + UserProfileActivity.recentComments.get(i).recent_comment);
        UserProfileActivity.recentComments.addAll(Arrays.asList(arr));
        //i think this is a very bad desgin pattern?
    }
}