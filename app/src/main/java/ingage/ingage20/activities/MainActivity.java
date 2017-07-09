package ingage.ingage20.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.adapters.DrawerAdapter;
import ingage.ingage20.firebase.FirebaseSharedPrefManager;
import ingage.ingage20.fragments.CategoriesPageFragment;
import ingage.ingage20.fragments.SearchResultFragment;
import ingage.ingage20.handlers.DownloadAvatarHandler;
import ingage.ingage20.handlers.SearchHandler;
import ingage.ingage20.util.NavigationDrawer;
import ingage.ingage20.R;
import ingage.ingage20.handlers.IdentityHandler;
import ingage.ingage20.fragments.FrontPageFragment;
import ingage.ingage20.managers.AlertDiaLogManager;
import ingage.ingage20.managers.SessionManager;

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
    protected ImageView avatar;
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
        options_adapter.add("Sign Out");
        options_adapter.notifyDataSetChanged();


        userName = (TextView) findViewById(R.id.userName);
        userName.setTextColor(Color.parseColor("#FFFFFF"));

        avatar = (ImageView) findViewById(R.id.userImage);
        downloadAvatar();

    }


    private void downloadAvatar(){
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
    }

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
            subs.add(arr[i]);
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
                session.updateCategory((String) lvItems.getItemAtPosition(pos));
                session.updatePage("categoryTrend");
                Log.d("STATE", "Nav item clicked: "+ lvItems.getItemAtPosition(pos));
                navigationDrawer.closeDrawer();
                final Class fragmentClass = CategoriesPageFragment.class;
                final Fragment fragment = Fragment.instantiate(getApplicationContext(), fragmentClass.getName());
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, fragment, fragmentClass.getSimpleName())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();

                //close nav drawer after click
                navigationDrawer.closeDrawer();
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
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

    private void onNew() {
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
            session.updatePage("noneDate");
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
    }

    private void onTrend() {
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
            session.updatePage("noneTrend");
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
            case "search":
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