package ingage.ingage20.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.adapters.DrawerAdapter;
import ingage.ingage20.firebase.FirebaseSharedPrefManager;
import ingage.ingage20.fragments.CategoriesPageFragment;
import ingage.ingage20.fragments.SearchResultFragment;
import ingage.ingage20.handlers.AnnouncementHandler;
import ingage.ingage20.handlers.DownloadAvatarHandler;
import ingage.ingage20.handlers.SearchHandler;
import ingage.ingage20.helpers.ThreadsHelper;
import ingage.ingage20.util.NavigationDrawer;
import ingage.ingage20.R;
import ingage.ingage20.handlers.IdentityHandler;
import ingage.ingage20.fragments.FrontPageFragment;
import ingage.ingage20.managers.AlertDiaLogManager;
import ingage.ingage20.managers.SessionManager;
import ingage.ingage20.adapters.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    /** Alert Dialogue used for failed sign-out**/
    AlertDiaLogManager alert = new AlertDiaLogManager();

    /** Class name for log messages. */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /** Bundle key for saving/restoring the toolbar title. */
    private static final String BUNDLE_KEY_TOOLBAR_TITLE = "title";

    /** Our navigation drawer class for handling navigation drawer logic. */
    private NavigationDrawer navigationDrawer;
    SessionManager session;
    private Bundle fragmentBundle;
    Context mContext;
    /** The toolbar view control. */
    private Toolbar toolbar;
    protected static ArrayList<String> subs = new ArrayList<>();

    private Button   signOutButton;
    protected CircularImageView avatar;
    protected TextView userName;
    String default_path = "data:image/JPG;base64,";

    public static ArrayAdapter<String> adapter = null;

    public static String appToken;
    ListView lvItems;
    RecyclerView options;
    android.support.v7.widget.SearchView searchView;

    //dont use enum cuz bad  performance in Android, uses more RAM and memory
    static String pageCategory = "noneDate";
    static String pageType = "date";

    private TabLayout tabLayout;
   // private ViewPager viewPager;

    private void setupToolbar(final Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Set up the activity to use this toolbar. As a side effect this sets the Toolbar's title
        // to the activity's title.
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            // Some IDEs such as Android Studio complain about possible NPE without this check.
            assert getSupportActionBar() != null;

            // Restore the Toolbar's title.
            getSupportActionBar().setTitle(
                    savedInstanceState.getCharSequence(BUNDLE_KEY_TOOLBAR_TITLE));
        }
    }

    /**
     * Initializes the navigation drawer submit_post_toolbar to allow toggling via the toolbar or swipe from the
     * side of the screen.
     */
    private void setupNavigationMenu(final Bundle savedInstanceState) {
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ListView drawerItems = (ListView) findViewById(R.id.nav_drawer_items);

        // Create the navigation drawer.
        navigationDrawer = new NavigationDrawer(this, toolbar, drawerLayout, drawerItems,
                R.id.main_fragment_container, mContext);

        //FOR DISPLAYING CATEGORIES
        /**for (Configurations.Feature feature : Configurations.getFeatureList()) {
         navigationDrawer.addFeatureToMenu(feature);
         }**/

        if (savedInstanceState == null) {
            // Add the home fragment to be displayed initially.
            navigationDrawer.showHome();
        }
    }

    protected void setupNavigationDrawer(){

        //set up array adapter for subscribed categories
        lvItems = (ListView) findViewById(R.id.nav_drawer_items);
        adapter=new ArrayAdapter<String>(this, R.layout.lv_item, subs);
        lvItems.setAdapter(adapter);
        setupSubscriptionsListener();
        adapter.notifyDataSetChanged();

        //set up recycler view adapter for drawer options
        options = (RecyclerView) findViewById(R.id.options);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        options.setLayoutManager(layoutManager);
        ArrayList arr = new ArrayList();

        DrawerAdapter options_adapter = new DrawerAdapter(getApplicationContext());

        options.setAdapter(options_adapter);
        options_adapter.add("View Profile");
        options_adapter.add("Create Thread");
        options_adapter.add("Sign Out");
        options_adapter.notifyDataSetChanged();


        userName = (TextView) findViewById(R.id.userName);
        userName.setTextColor(Color.parseColor("#FFFFFF"));

        avatar = (CircularImageView) findViewById(R.id.userImage);
        downloadAvatar();

    }

    private void downloadAvatar(){
        final String url = "http://107.170.232.60/avatars/" + userName.getText() + ".JPG";

        Context context = getBaseContext();

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        final int imgHeight = (int) (screenHeight * 0.25);
        final int imgWidth = (int) (screenWidth* 0.25);

        LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, imgHeight);
        img_params.setMargins(40,0,0, 20);
        avatar.setLayoutParams(img_params);

        Picasso.with(this)
                .load(url)
                //.networkPolicy(NetworkPolicy.OFFLINE)
                .resize(imgWidth, imgHeight)
                .noPlaceholder()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .onlyScaleDown()
                .into(avatar, new Callback() {
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
                                .into(avatar, new Callback() {
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


    /*private void downloadAvatar(){
        Context context = getApplicationContext();
        DownloadAvatarHandler avatarHandler = new DownloadAvatarHandler(context);
        String type = "download";


        //do conversion
        try {
            String username = (String) userName.getText();
            String result = avatarHandler.execute(type, username).get();
            //Log.d("STATE", "room title: " + threadsHelper.getThread_title());
            Log.d("STATE", "download avatar result: " + result);
            if(result.length() > default_path.length()) {
                int index = result.indexOf(",") + 1;
                String code = result.substring(index, result.length());
                byte[] decodedString = Base64.decode(code, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                avatar.setImageBitmap(decodedByte);
                LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(700, 700);
                avatar.setLayoutParams(img_params);

            }

            else
                avatar.setImageResource(R.mipmap.user);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //set padding programmatically
        if(avatar.getDrawable() != null) {
            float density = context.getResources().getDisplayMetrics().density;
            int padding = (int)(20 * density);
            avatar.setPadding(padding, padding, padding, padding);
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();

        session = new SessionManager(mContext);
        session.checkLogin();

        setContentView(R.layout.activity_main);

        setupToolbar(savedInstanceState);

        setupNavigationMenu(savedInstanceState);
        /**RecyclerView (TEMPORARY, MOVE TO A FRAGMENT LATER)**/
        ListView lvItems;

        parseJSON();

        setupNavigationDrawer();
        session.updatePage(pageType);
        /* initilize FrontPage Fragment*/
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        final Class fragmentClass = FrontPageFragment.class;
        final Fragment fragment = Fragment.instantiate(this, fragmentClass.getName());

        fragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment, fragmentClass.getSimpleName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        // Set the title for the fragment.
        final ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //viewPager = (ViewPager) findViewById(R.id.viewpager);
        //setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("New"));
        tabLayout.addTab(tabLayout.newTab().setText("Trending"));
        final int[] ICONS = new int[]{
                android.R.drawable.ic_menu_today,
                android.R.drawable.ic_menu_week,
                android.R.drawable.ic_menu_search};
        tabLayout.getTabAt(0).setIcon(ICONS[0]);
        tabLayout.getTabAt(1).setIcon(ICONS[1]);
        tabLayout.getTabAt(2).setIcon(ICONS[2]);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
                int position = tab.getPosition();
                Log.i("STATE", "tab selected: " + position);
                if(position == 0){
                    Class fragmentClass = FrontPageFragment.class;
                    pageType = "date";
                    session.updatePage(pageType);

                    final Fragment fragment = Fragment.instantiate(MainActivity.this, fragmentClass.getName());

                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.main_fragment_container, fragment, fragmentClass.getSimpleName())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();

                    // Set the title for the fragment.
                    final ActionBar actionBar = getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setTitle(getString(R.string.app_name));
                    }
                }
                else if(position == 1)
                    onNew();
                else if(position == 2)
                    onTrend();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //tabLayout.setupWithViewPager(viewPager);

        AnnouncementHandler announcementHandler = new AnnouncementHandler();
        String msg = null;
        try {
            msg= announcementHandler.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        TextView announcement =(TextView)findViewById(R.id.announcement);
        announcement.setText(msg);
        announcement.setSelected(true);

    }

 /*   private void setupViewPager(ViewPager viewPager) {
        final Class fragmentClass = FrontPageFragment.class;
        final Fragment mainFragment = Fragment.instantiate(this, fragmentClass.getName());
        final Fragment trendingFragment = Fragment.instantiate(this, fragmentClass.getName());
        final Fragment newFragment = Fragment.instantiate(this, fragmentClass.getName());
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mainFragment, "Home");
        adapter.addFragment(trendingFragment, "Trending");
        adapter.addFragment(newFragment, "New");
        viewPager.setAdapter(adapter);

        ViewPager.OnPageChangeListener pagechangelistener =new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int pos) {

                adapter.notifyDataSetChanged();
                Log.i("STATE", "Pg selected: " + pos);
               /* if(pos == 1)
                    onTrend();
                else if (pos == 2)
                    onNew();*/
                //indicator.setCurrentItem(arg0);
         /*   }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

                //Logger.logMessage("Called second");

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

                //Logger.logMessage("Called third");

            }
        };
        viewPager.setOnPageChangeListener(pagechangelistener);
    }*/

    //get JSON object containing user info
    protected void parseJSON(){
        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(SessionManager.KEY_NAME);
        String password = user.get(SessionManager.KEY_PASSWORD);
        appToken = FirebaseSharedPrefManager.getInstance(this).getToken();
        String type = "login";
        IdentityHandler identityHandler = new IdentityHandler(this);
        String loginStatus = null;
        try {
            loginStatus = identityHandler.execute(type, username, password, appToken).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(loginStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("user_profile");
            int count = 0;
            while(count < jsonArray.length()){
                JSONObject JO = jsonArray.getJSONObject(count);
                String thread_subscriptions = JO.getString("thread_subscriptions");
                parseSubs(thread_subscriptions);
                count++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Parse the thread subscriptions JSON string
    protected ArrayList parseSubs(String thread_subscriptions){
        thread_subscriptions = thread_subscriptions.replace("["," ");
        thread_subscriptions = thread_subscriptions.replace("]"," ");

        String arr[] = thread_subscriptions.split(",");
        for(int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].substring(arr[i].lastIndexOf(":") + 1);
            arr[i] = arr[i].replace("\"","");
            arr[i] = arr[i].replace("}","");
            subs.add(arr[i].trim());
            Log.d("STATE", "Subs: " + subs.get(i));
        }

        return subs;
    }

    private void setupSubscriptionsListener() {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        lvItems.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final int pos = position;
            String selectedCategory = (String) lvItems.getItemAtPosition(pos);
                navigationDrawer.closeDrawer();
                Intent intent = new Intent(MainActivity.this, FilteredActivity.class);
                intent.putExtra("type", "category");
                intent.putExtra("category", selectedCategory);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        final Class fragmentClass = SearchResultFragment.class;

        if (navigationDrawer.isDrawerOpen()) {
            navigationDrawer.closeDrawer();
            return;
        }
        else{
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onStart(){
        super.onStart();
        adapter.clear();
        adapter.notifyDataSetChanged();

        parseJSON();
        lvItems = (ListView) findViewById(R.id.nav_drawer_items);
        adapter=new ArrayAdapter<String>(this, R.layout.lv_item, subs);
        lvItems.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        downloadAvatar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        searchView = (android.support.v7.widget.SearchView) myActionMenuItem.getActionView();
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        final Class fragmentClass = SearchResultFragment.class;
        searchView.setOnSearchClickListener(this);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("SEARCH",s);
                SearchHandler searchHandler = new SearchHandler();
                String result = "";
                session.updateSearch(s);

                final Fragment fragment = Fragment.instantiate(getApplicationContext(), fragmentClass.getName());

                fragmentManager
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, fragment, fragmentClass.getSimpleName())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("SEARCH","yes");

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up selected_page_button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //TODO finish settings when nessecary
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_new) {
            onNew();
            return true;
        }
        else if (id == R.id.action_trending) {
            onTrend();
            return true;
        }

        if(id == R.id.action_refresh){
            onRefresh();
            return true;
        }

        if (id == R.id.action_search) {
            onSearch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSearch(){

        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        final Class fragmentClass = SearchResultFragment.class;

        searchView.setOnSearchClickListener(this);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("SEARCH","submit");
                SearchHandler searchHandler = new SearchHandler();
                String result = "";
                try {
                    result = searchHandler.execute("0", s).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                final Fragment fragment = Fragment.instantiate(getApplicationContext(), fragmentClass.getName());

                fragmentManager
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, fragment, fragmentClass.getSimpleName())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("SEARCH","yes");

                return false;
            }
        });
    }

    private Fragment onNew() {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        Class fragmentClass = FrontPageFragment.class;
        HashMap<String, String> user = session.getUserDetails();
        final Fragment fragment;
        if(user.get(SessionManager.CATEGORY_TYPE) != null){
            fragmentClass = CategoriesPageFragment.class;
            session.updatePage("categoryDate");
            fragment = Fragment.instantiate(this, fragmentClass.getName());
        }//category
        else{
            session.updatePage("date");
            fragment = Fragment.instantiate(this, fragmentClass.getName());
        }//frontpage

        fragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment, fragmentClass.getSimpleName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        // Set the title for the fragment.
        final ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
        }
        return fragment;
    }

    private Fragment onTrend() {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        Class fragmentClass = FrontPageFragment.class;
        HashMap<String, String> user = session.getUserDetails();
        final Fragment fragment;
        if(user.get(SessionManager.CATEGORY_TYPE) != null){
            fragmentClass = CategoriesPageFragment.class;
            session.updatePage("categoryTrend");
            fragment = Fragment.instantiate(this, fragmentClass.getName());
        }//category
        else{
            session.updatePage("trend");
            fragment = Fragment.instantiate(this, fragmentClass.getName());
        }//frontpage

        fragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment, fragmentClass.getSimpleName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        // Set the title for the fragment.
        final ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
        }
        return fragment;
    }


    /**
     * Stores data to be passed between fragments.
     * @param fragmentBundle fragment data
     */
    public void setFragmentBundle(final Bundle fragmentBundle) {
        this.fragmentBundle = fragmentBundle;
    }

    /**
     * Gets data to be passed between fragments.
     * @return fragmentBundle fragment data
     */
    public Bundle getFragmentBundle() {
        return this.fragmentBundle;
    }

    @Override
    public void onClick(View view) {

    }

    public void onRefresh(){
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        Class fragmentClass = FrontPageFragment.class;
        HashMap<String, String> user = session.getUserDetails();
        pageCategory = user.get(SessionManager.PAGE_TYPE);
        switch (pageCategory){
            case "categoryDate":
                fragmentClass = CategoriesPageFragment.class;
                pageType = "categoryDate";
                session.updatePage(pageType);//LOOK AT THIS SHIT LMAOOOOOOOOOOOO
                break;
            case "categoryTrend":
                fragmentClass = CategoriesPageFragment.class;
                pageType = "categoryTrend";
                session.updatePage(pageType);//fuck LAAAA
                break;
            case "noneDate":
                fragmentClass = FrontPageFragment.class;
                pageType = "date";
                session.updatePage(pageType);//DIU HAI MAN
                break;
            case "noneTrend":
                fragmentClass = FrontPageFragment.class;
                pageType = "trend";
                session.updatePage(pageType);
                break;
        }
        final Fragment fragment = Fragment.instantiate(this, fragmentClass.getName());

        fragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment, fragmentClass.getSimpleName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        // Set the title for the fragment.
        final ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
        }
    }
}