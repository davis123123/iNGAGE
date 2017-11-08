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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

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
        Context context = getApplicationContext();
        DownloadAvatarHandler avatarHandler = new DownloadAvatarHandler(context);
        String type = "download";

        //do conversion
        try {
            curr_avatar = (CircularImageView) findViewById(R.id.profile_img);
            String result = avatarHandler.execute(type, username).get();
            //Log.d("STATE", "room title: " + threadsHelper.getThread_title());
            Log.d("STATE", "download avatar result: " + result);
            if(result.length() > default_path.length()) {
                int index = result.indexOf(",") + 1;
                String code = result.substring(index, result.length());
                byte[] decodedString = Base64.decode(code, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                curr_avatar.setImageBitmap(decodedByte);
                LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(700, 700);
                curr_avatar.setLayoutParams(img_params);
            }

            else
                curr_avatar.setImageResource(R.mipmap.user);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //set padding programmatically
        if(curr_avatar.getDrawable() != null) {
            float density = context.getResources().getDisplayMetrics().density;
            int padding = (int)(20 * density);
            curr_avatar.setPadding(padding, padding, padding, padding);
        }
    }




}
