package ingage.ingage20.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.adapters.UserProfileInfoAdapter;
import ingage.ingage20.adapters.ViewPagerAdapter;
import ingage.ingage20.fragments.RecentCommentsFragment;
import ingage.ingage20.fragments.UserInfoFragment;
import ingage.ingage20.handlers.DownloadAvatarHandler;
import ingage.ingage20.handlers.UserRecentCommentHandler;
import ingage.ingage20.managers.SessionManager;
import ingage.ingage20.util.RecentComment;


public class UserProfileActivity extends AppCompatActivity {

    String username, viewType;
    ImageView curr_avatar;
    String default_path = "data:image/JPG;base64,";

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static ArrayList<RecentComment> recentComments  = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> info = session.getUserDetails();

        //viewing other profiles
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            username = extras.getString("USER");
            //Toast.makeText(getApplicationContext(), username, Toast.LENGTH_LONG).show();
        }

        //view own profile
        else
            username = info.get(SessionManager.KEY_NAME);

        curr_avatar = (ImageView) findViewById(R.id.profile_img);

        if(username.equals(info.get(SessionManager.KEY_NAME))) {
            curr_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ChangeAvatarActivity.class);
                    finish();
                    startActivity(intent);
                }
            });
        }

        getSupportActionBar().setTitle(username);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        downloadAvatar();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setupViewPager(ViewPager viewPager) {

            final Class recentFragmentClass = RecentCommentsFragment.class;
            Fragment recentCommentFragment = Fragment.instantiate(this, recentFragmentClass.getName());

            final Class fragmentClass = UserInfoFragment.class;
            final Fragment userInfoFragment = Fragment.instantiate(this, fragmentClass.getName());

            final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(recentCommentFragment, "Recent Activity");
            adapter.addFragment(userInfoFragment, "User Info");
            viewPager.setAdapter(adapter);

            ViewPager.OnPageChangeListener pagechangelistener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int pos) {
                        adapter.notifyDataSetChanged();
                        Log.i("STATE", "Pg selected: " + pos);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {

                }
            };
                viewPager.setOnPageChangeListener(pagechangelistener);
    }



    private void downloadAvatar(){
        final String url = "http://107.170.232.60/avatars/" + username + ".JPG";

        Context context = getBaseContext();

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        final int imgHeight = (int) (screenHeight * 0.25);
        final int imgWidth = imgHeight;

        Picasso.with(this)
                .load(url)
     //           .networkPolicy(NetworkPolicy.OFFLINE)
                .resize(imgWidth, imgHeight)
                .onlyScaleDown()
                .noPlaceholder()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
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
                                .noPlaceholder()
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
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

    public String getUsername(){
        return username;
    }


}
