package ingage.ingage20.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.handlers.DownloadAvatarHandler;
import ingage.ingage20.handlers.UploadAvatarHandler;
import ingage.ingage20.managers.SessionManager;


public class UserProfileActivity extends AppCompatActivity {

    String username, email, tribute_pts, subs;
    //Button upload, change;
    ImageButton curr_avatar;
    protected static ArrayList<String> sub_arr = new ArrayList<>();
    String default_path = "data:image/JPG;base64,";
    String result = "Subscriptions: ";


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

        TextView user_info = (TextView) findViewById(R.id.user_name);
        TextView email_info = (TextView) findViewById(R.id.email);
        TextView pts_info = (TextView) findViewById(R.id.tribute_points);
        TextView subs_info = (TextView) findViewById(R.id.subscriptions);

        user_info.setText(username);
        if(email.length() == 0 || email == null)
            email_info.setText("Email: N/A" );
        else
            email_info.setText("Email:" + email);
        pts_info.setText("Tribute points: " + tribute_pts);
        setSubscriptions(subs_info);
        //subs_info.setText(subs);


        curr_avatar = (ImageButton) findViewById(R.id.profile_img);
        curr_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeAvatarActivity.class);
                finish();
                startActivity(intent);
            }
        });


        downloadImage();
        //setListeners();
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

    protected void setSubscriptions(TextView subs_info){
        parseSubs(subs);

        for(int i=0; i < sub_arr.size(); i++){
            if(i == 0)
                result = result + sub_arr.get(i);
            else
                result = result + ", " + sub_arr.get(i);
        }
        subs_info.setText(result);
    }

    //retrieve Base64 from FireBase and convert to image
    private void downloadImage(){
        Context context = getApplicationContext();
        DownloadAvatarHandler avatarHandler = new DownloadAvatarHandler(context);
        String type = "download";


        //do conversion
        try {
            String result = avatarHandler.execute(type, username).get();
            //Log.d("STATE", "room title: " + threadsHelper.getThread_title());
            Log.d("STATE", "download avatar result: " + result);
            if(result.length() > 0) {
                if(result.length() > default_path.length()) {
                    int index = result.indexOf(",") + 1;
                    String code = result.substring(index, result.length());
                    byte[] decodedString = Base64.decode(code, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    curr_avatar.setImageBitmap(decodedByte);
                    LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 1000);
                    curr_avatar.setLayoutParams(img_params);
                }
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
