package ingage.ingage.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage.adapters.DrawerAdapter;
import ingage.ingage.adapters.ViewPagerAdapter;
import ingage.ingage.firebase.FirebaseSharedPrefManager;
import ingage.ingage.fragments.ArchivedFragment;
import ingage.ingage.fragments.CategoriesFragment;
import ingage.ingage.fragments.CategoriesPageFragment;
import ingage.ingage.fragments.FragmentBase;
import ingage.ingage.fragments.SearchResultFragment;
import ingage.ingage.handlers.AnnouncementHandler;
import ingage.ingage.handlers.SearchHandler;
import ingage.ingage.handlers.UserRecentCommentHandler;
import ingage.ingage.managers.WifiManager;
import ingage.ingage.util.NavigationDrawer;
import ingage.ingage.R;
import ingage.ingage.handlers.IdentityHandler;
import ingage.ingage.managers.SessionManager;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, CategoriesFragment.categoriesFragmentListener {

    /** Bundle key for saving/restoring the toolbar title. */
    private static final String BUNDLE_KEY_TOOLBAR_TITLE = "title";

    /** Our navigation drawer class for handling navigation drawer logic. */
    private NavigationDrawer navigationDrawer;
    SessionManager session;
    Context mContext;
    /** The toolbar view control. */
    private Toolbar toolbar;
    public static ArrayList<String> subs = new ArrayList<>();

    protected ImageView avatar;
    protected TextView userName;
    String default_path = "data:image/JPG;base64,";
    ViewPagerAdapter viewPagerAdapter;// = new ViewPagerAdapter(getSupportFragmentManager());
    //public static ArrayAdapter<String> adapter = null;

    public static String appToken;
    RecyclerView options;
    FloatingSearchView searchBar;

    //dont use enum cuz bad  performance in Android, uses more RAM and memory
    static String pageCategory = null, pageType = "date";
    private int tabIconColor;

    WifiManager wifiManager;
    FragmentManager fragmentManager;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static UserRecentCommentHandler handler;

    private void setupToolbar(final Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        // Set up the activity to use this toolbar. As a side effect this sets the Toolbar's title
        // to the activity's title.
        setSupportActionBar(toolbar);
        if (savedInstanceState != null) {
            // Some IDEs such as Android Studio complain about possible NPE without this check.
            assert getSupportActionBar() != null;
            Log.d("Toolbar", BUNDLE_KEY_TOOLBAR_TITLE);
            // Restore the Toolbar's title.
            getSupportActionBar().setTitle(
                    savedInstanceState.getCharSequence("HELLOW WORLD"));
        }

        //getSupportActionBar().setTitle("HELLOW WORLD");
    }

    /**
     * Initializes the navigation drawer submit_post_toolbar to allow toggling via the toolbar or swipe from the
     * side of the screen.
     */
    private void setupNavigationMenu(final Bundle savedInstanceState) {
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //final ListView drawerItems = (ListView) findViewById(R.id.nav_drawer_items);

        // Create the navigation drawer.
        navigationDrawer = new NavigationDrawer(this, toolbar, drawerLayout, R.id.main_fragment_container, mContext);

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

        avatar = (ImageView) findViewById(R.id.userImage);
        downloadAvatar();

    }

    private void downloadAvatar(){
        final String url = "http://107.170.232.60/avatars/" + userName.getText() + ".JPG";

        Context context = getBaseContext();

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        final int imgHeight = (int) (screenHeight * 0.25);
        final int imgWidth = imgHeight;

        LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(imgWidth, imgHeight);
        img_params.setMargins(40,40,40, 40);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();

        session = new SessionManager(mContext);
        session.checkLogin();

        setContentView(R.layout.activity_main);
        setupToolbar(savedInstanceState);
        /**RecyclerView (TEMPORARY, MOVE TO A FRAGMENT LATER)**/
        parseJSON();

        setupNavigationMenu(savedInstanceState);
        setupNavigationDrawer();

        session.updatePage(pageType);
        session.updateCategory(pageCategory);
        //initTabs();
        searchBar = (FloatingSearchView) findViewById(R.id.floating_search_view);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
        //initAnnouncement();

        HashMap<String, String> user = session.getUserDetails();
        String categoryType = "";
        categoryType = user.get(SessionManager.CATEGORY_TYPE);
        //Log.d("CategoryName", categoryType);
        if(categoryType == null){
            categoryType = "Home";
        }
        // Set the title for the fragment.
        final ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(categoryType);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UserProfileActivity.recentComments.clear();
    }

    private void setupViewPager(final ViewPager viewPager) {
        //viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());;
        final Class categoriesFragmentClass = CategoriesFragment.class;
        Fragment categoriesFragment = Fragment.instantiate(this, categoriesFragmentClass.getName());

        final Class archivedFragmentClass = ArchivedFragment.class;
        Fragment archivedFragment = Fragment.instantiate(this, archivedFragmentClass.getName());

        final Class categoriesPageFragmentClass = CategoriesPageFragment.class;
        Fragment categoriesPageFragment = Fragment.instantiate(this, categoriesPageFragmentClass.getName());

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(categoriesPageFragment, "Active");
        viewPagerAdapter.addFragment(archivedFragment, "Archived");
        viewPagerAdapter.addFragment(categoriesFragment, "Categories");

        viewPager.setAdapter(viewPagerAdapter);

        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
              //  CategoriesPageFragment categoriesPageFragment1 = (CategoriesPageFragment) viewPagerAdapter.instantiateItem(viewPager, 0);
               // ArchivedFragment archivedFragment1 = (ArchivedFragment) viewPagerAdapter.instantiateItem(viewPager, 1);
                //viewPagerAdapter.notifyDataSetChanged();
                //viewPager.setAdapter(viewPagerAdapter);
            }

            @Override
            public void onPageSelected(int position) {
              //  viewPagerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //viewPagerAdapter.notifyDataSetChanged();
            }
        };

    }


   /* private void initTabs(){
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        TabLayout.Tab homeTab = tabLayout.newTab().setText("Home");
        TabLayout.Tab newTab = tabLayout.newTab().setText("Archive");
        TabLayout.Tab trendingTab = tabLayout.newTab().setText("Categories");

        tabLayout.addTab(homeTab);
        tabLayout.addTab(newTab);
        tabLayout.addTab(trendingTab);

        tabLayout.setSelectedTabIndicatorHeight(0);
        final int[] ICONS = new int[]{
                android.R.drawable.ic_menu_today,
                android.R.drawable.ic_menu_week,
                android.R.drawable.ic_menu_search};
        tabLayout.getTabAt(0).setIcon(ICONS[0]);
        tabLayout.getTabAt(1).setIcon(ICONS[1]);
        tabLayout.getTabAt(2).setIcon(ICONS[2]);

        tabIconColor = ContextCompat.getColor(mContext, R.color.tab_text_selected);
        homeTab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

        tabIconColor = ContextCompat.getColor(mContext, R.color.tab_text_unselected);
        newTab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        trendingTab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
                int position = tab.getPosition();
                Log.i("STATE", "tab selected: " + position);

                tabIconColor = ContextCompat.getColor(mContext, R.color.tab_text_selected);

                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                if (position == 0) {
                    onHome();
                } else if (position == 1)
                    //onNew();

                else if (position == 2)

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tabIconColor = ContextCompat.getColor(mContext, R.color.tab_text_unselected);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }*/

    private void initAnnouncement(){
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        final Class fragmentClass = SearchResultFragment.class;

        if (navigationDrawer.isDrawerOpen()) {
            navigationDrawer.closeDrawer();
            return;
        }
        if (searchBar.getVisibility() == View.VISIBLE) {
            searchBar.setVisibility(View.GONE);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(FragmentBase.RefreshEvent event) {

        onRefresh();
    };

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
        /*if(wifiManager.checkInternet()) {

            parseJSON();
        }
        else{
            wifiErrorDialog();
        }*/
        parseJSON();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //if(wifiManager.checkInternet())
        downloadAvatar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up selected_page_button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_search) {
            setSearchBarListeners();
            if(searchBar.getVisibility() == View.GONE) {
                searchBar.setVisibility(View.VISIBLE);
                searchBar.setSearchFocused(true);
            }
            else
                searchBar.setVisibility(View.GONE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void wifiErrorDialog(){

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Connection Error")
                .setMessage("Please check if device is connected to internet")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }

    private void setSearchBarListeners(){

        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        final Class fragmentClass = SearchResultFragment.class;

        searchBar.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {

                session.updateSearch(currentQuery);
                searchBar.setCloseSearchOnKeyboardDismiss(false);
                if(currentQuery.length() >= 3) {
                    Intent searchIntent = new Intent(mContext, SearchResultActivity.class);
                    startActivity(searchIntent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Search needs to be at least 3 characters long", Toast.LENGTH_LONG).show();
                }

            }
        });

        searchBar.setOnHomeActionClickListener(
                new FloatingSearchView.OnHomeActionClickListener() {
                    @Override
                    public void onHomeClicked() {
                        searchBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onCategorySelected(String item) {
        if(item.equals("All")){
            session.updateCategory(null);
            viewPagerAdapter.notifyDataSetChanged();
            //viewPager.setAdapter(viewPagerAdapter);
            viewPager.setCurrentItem(0,true);
        }
        else {
            session.updateCategory(item);
            viewPagerAdapter.notifyDataSetChanged();
            //  viewPager.setAdapter(viewPagerAdapter);
           viewPager.setCurrentItem(0, true);
            //tabLayout.getTabAt(0).select();
        }

        getSupportActionBar().setTitle(item);
    }

    private Fragment onHome(){
        Class fragmentClass = ArchivedFragment.class;
        //pageType = "date";
        //session.updatePage(pageType);
        final Fragment fragment;
        HashMap<String, String> user = session.getUserDetails();

        if(user.get(SessionManager.CATEGORY_TYPE) != null){
            fragmentClass = CategoriesPageFragment.class;
            session.updatePage("categoryDate");
            fragment = Fragment.instantiate(this, fragmentClass.getName());
        }//category
        else{
            session.updatePage("date");
            fragment = Fragment.instantiate(this, fragmentClass.getName());
        }//frontpage

        //fragment = Fragment.instantiate(MainActivity.this, fragmentClass.getName());

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
        return fragment;
    }

    @Override
    public void onClick(View view) {

    }

    public void onRefresh(){
        //viewPager.setCurrentItem(2);
        viewPagerAdapter.notifyDataSetChanged();
        //viewPager.setAdapter(viewPagerAdapter);
    }
}