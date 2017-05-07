package ingage.ingage20;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.MySQL.IdentityHandler;
import ingage.ingage20.fragments.FrontPageFragment;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

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
        ListView lvItems = (ListView) findViewById(R.id.nav_drawer_items);
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, R.layout.lv_item, subs);
        lvItems.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //set up sign out listener
        Button signOut = (Button) findViewById(R.id.button_signout);
        if ( signOut != null) {
            signOut.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    session.logoutUser();
                    adapter.clear();
                    adapter.notifyDataSetChanged();

                    Intent intent = new Intent(MainActivity.this, Login2Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    Toast.makeText(getBaseContext(),"Successfully signed out!",Toast.LENGTH_SHORT).show();
                }

            });
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

        parseJSON();

        setupNavigationDrawer();

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
        String type = "login";
        IdentityHandler identityHandler = new IdentityHandler(this);
        String loginStatus = null;
        try {
            loginStatus = identityHandler.execute(type, username, password).get();
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


    @Override
    public void onBackPressed() {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        if(id == R.id.action_refresh){

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

            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
