package ingage.ingage.handlers;

import android.util.Log;

import java.io.IOException;

import ingage.ingage.App;
import ingage.ingage.R;
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
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Davis on 3/20/2018.
 */

public class UserInfoHandler {
    public RecentComment[] arr;
    String serverResponse;
    private UserInfoHandler.CallBackData callBackData;

    public interface CallBackData{
        void notifyChange(String serverResponse);
    }

    public interface Interface {

        @FormUrlEncoded
        @POST("http://{ip}/user_profile.php/" )
        Call<ResponseBody> get(
                @Field("user_name") String username,
                @Path("ip") String ip
        );
    }

    public void setCallBackData(UserInfoHandler.CallBackData callBackData){
        this.callBackData = callBackData;
    }


    public void enqueue(String username, String ip){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        String url = "http://" + ip + "/user_profile.php/";

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();
        Interface service = retrofit.create(Interface.class);

        Call<ResponseBody> call = service.get(username, url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("STATE","Retrofit response code: " + response.code());

                if(response.isSuccessful()) {
                    Log.i("STATE","Retrofit POST Success");
                    try {
                        serverResponse = response.body().string();

                        callBackData.notifyChange(serverResponse);

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
