package ingage.ingage20.handlers;

import android.util.Log;

import java.io.IOException;

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

    public static final String url = "http://107.170.232.60/track_user_comment.php/";

    public interface Interface {

        @FormUrlEncoded
        @POST(url)
        Call<ResponseBody> post(
                @Field("username") String username,
                @Field("thread_id") String thread_id,
                @Field("recent_comment") String recent_comment
        );


    }

    public UserRecentCommentHandler(){

    }


    public void enqueue(String username, String thread_id, String messageText){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build(); Interface service = retrofit.create(Interface.class);

        Call<ResponseBody> call = service.post(username, thread_id, messageText);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("STATE","Retrofit response code: " + response.code());

                if(response.isSuccessful()) {
                    Log.i("STATE","Retrofit POST Success");
                    try {
                        Log.i("STATE","Retrofit reponse: " + response.body().string());
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


}
