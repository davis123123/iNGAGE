package ingage.ingage20.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.adapters.UserProfileInfoAdapter;
import ingage.ingage20.handlers.DownloadAvatarHandler;
import ingage.ingage20.managers.SessionManager;


public class UserProfileActivity extends AppCompatActivity {

    String username, email, tribute_pts, subs, date_joined;
    //Button upload, change;
    CircularImageView curr_avatar;
    TextView display_username;
    protected static ArrayList<String> sub_arr = new ArrayList<>();
    String default_path = "data:image/JPG;base64,";
    String result = "Subscriptions: ";
    RecyclerView recycler;
    UserProfileInfoAdapter adapter = new UserProfileInfoAdapter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> info = session.getUserDetails();
        username = info.get(SessionManager.KEY_NAME);
        email = info.get(SessionManager.KEY_EMAIL);
        tribute_pts = info.get(SessionManager.KEY_TRIBUTE_POINTS);
        subs = info.get(SessionManager.KEY_SUBSCRIPTIONS);
        date_joined = info.get(SessionManager.KEY_DATE_JOINED);

        curr_avatar = (CircularImageView) findViewById(R.id.profile_img);
        curr_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeAvatarActivity.class);
                finish();
                startActivity(intent);
            }
        });

        display_username = (TextView) findViewById(R.id.user_name);
        display_username.setText(username);

        recycler = (RecyclerView) findViewById(R.id.info);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);

        adapter = new UserProfileInfoAdapter();
        recycler.setAdapter(adapter);


        if(email != null && email.length() > 0)
            adapter.add("Email: " + email);
        else
            adapter.add("Email: N/A" );
        adapter.add("Tribute points: " + tribute_pts);
        setSubscriptions();
        adapter.add("Date joined: " + date_joined);
        adapter.notifyDataSetChanged();

        downloadAvatar();
    }

    //Parse the thread subscriptions JSON string
    protected ArrayList parseSubs(String thread_subscriptions){
        thread_subscriptions = thread_subscriptions.replace("["," ");
        thread_subscriptions = thread_subscriptions.replace("]"," ");
        sub_arr.clear();

        String arr[] = thread_subscriptions.split(",");
        for(int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].substring(arr[i].lastIndexOf(":") + 1);
            arr[i] = arr[i].replace("\"","");
            arr[i] = arr[i].replace("}","");
            sub_arr.add(arr[i]);
        }

        return sub_arr;
    }

    protected void setSubscriptions(){
        parseSubs(subs);

        for(int i=0; i < sub_arr.size(); i++){
            if(i == 0)
                result = result + sub_arr.get(i);
            else
                result = result + ", " + sub_arr.get(i);
        }
        adapter.add(result);
    }

    private void downloadAvatar(){
        final String url = "http://107.170.232.60/avatars/" + username + ".JPG";

        Context context = getBaseContext();

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        final int imgHeight = (int) (screenHeight * 0.3);
        final int imgWidth = (int) (screenWidth* 0.3);

        Picasso.with(this)
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .resize(imgWidth, imgHeight)
                .onlyScaleDown()
                .into(curr_avatar, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //If cache fails, try to fetch from url
                        Picasso.with(getBaseContext())
                                .load(url)
                                .resize(imgWidth, imgHeight)
                                .onlyScaleDown()
                                //.error(R.drawable.header)
                                .into(curr_avatar, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.e("Picasso","Could not get image");
                                    }
                                });
                    }
                });
    }




}
