package ingage.ingage20.handlers;

import android.util.Log;

import java.io.IOException;

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
 * Created by Davis on 3/20/2018.
 */

public class UserInfoHandler {
    public RecentComment[] arr;
    //public ArrayList<RecentComment> recentComments  = new ArrayList<>();
    String serverResponse;
    static final String get_url = "http://107.170.232.60/user_profile.php/";
    private UserInfoHandler.CallBackData callBackData;

    public interface CallBackData{
        void notifyChange(String serverResponse);
    }

    public interface Interface {

        @FormUrlEncoded
        @POST(get_url )
        Call<ResponseBody> get(
                @Field("user_name") String username
        );
    }

    public void setCallBackData(UserInfoHandler.CallBackData callBackData){
        this.callBackData = callBackData;
    }


    public void enqueue(String username){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Log.i("STATE","Retrofit url"+get_url);
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
