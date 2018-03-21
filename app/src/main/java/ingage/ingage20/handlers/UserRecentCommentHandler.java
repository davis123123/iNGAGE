package ingage.ingage20.handlers;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import ingage.ingage20.R;
import ingage.ingage20.activities.UserProfileActivity;
import ingage.ingage20.util.RecentComment;
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
import retrofit2.http.POST;

/**
 * Created by wuv66 on 2/10/2018.
 */

public class UserRecentCommentHandler {

    static final String post_url = "http://107.170.232.60/track_user_comment.php/";

    static final String get_url = "http://107.170.232.60/query_user_recent_comments.php/";

    public RecentComment[] arr;
    //public ArrayList<RecentComment> recentComments  = new ArrayList<>();
    String serverResponse;

    private CallBackData callBackData;
    public interface CallBackData{
        void notifyChange();
    }

    public interface Interface {

        @FormUrlEncoded
        @POST(post_url )
        Call<ResponseBody> post(
                @Field("username") String username,
                @Field("thread_id") String thread_id,
                @Field("recent_comment") String recent_comment
        );

        @FormUrlEncoded
        @POST(get_url )
        Call<ResponseBody> get(
                @Field("username") String username
        );
    }

    public UserRecentCommentHandler(){
    }

    public void setCallBackData(CallBackData callBackData){
        this.callBackData = callBackData;
    }

    //save recent comment in database
    public void enqueue(String username, String thread_id, String messageText){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(post_url)
                .build();

        Interface service = retrofit.create(Interface.class);

        Call<ResponseBody> call = service.post(username, thread_id, messageText);

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
    public void enqueue(String username){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(get_url)
                .build();
        Interface service = retrofit.create(Interface.class);

        Call<ResponseBody> call = service.get(username);
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
