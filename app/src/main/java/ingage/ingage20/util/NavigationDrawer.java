package ingage.ingage20.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

import ingage.ingage20.R;
import ingage.ingage20.fragments.FrontPageFragment;
import ingage.ingage20.managers.SessionManager;

import static ingage.ingage20.R.string.app_name;

/**
 * Created by Davis on 4/17/2017.
 */

public class NavigationDrawer {
    private AppCompatActivity containingActivity;

    /**
     * The helper class used to toggle the side navigation drawer open and closed.
     */
    private ActionBarDrawerToggle drawerToggle;

    /* The navigation drawer layout view control. */
    private DrawerLayout drawerLayout;

    /**
     * The view group that will contain the navigation drawer submit_post_toolbar items.
     */


    /**
     * The id of the fragment container.
     */
    private int fragmentContainerId;
    private ArrayAdapter<Configurations.Feature> adapter;
    private Context mContext;

    private Button   signOutButton;

    /**
     * Constructs the Navigation Drawer.
     *
     * @param activity             the activity that will contain this navigation drawer.
     * @param toolbar              the toolbar the activity is using.
     * @param layout               the DrawerLayout for this navigation drawer.
     */
    public NavigationDrawer(final android.support.v7.app.AppCompatActivity activity,
                            final Toolbar toolbar,
                            final DrawerLayout layout,
                            final int fragmentContainerId,
                            Context context) {
        // Keep a reference to the activity containing this navigation drawer.
        this.containingActivity = activity;
        mContext = context;

        adapter = new ArrayAdapter<Configurations.Feature>(activity, R.layout.nav_drawer_item) {
            @Override
            public View getView(final int position, final View convertView,
                                final ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    view = activity.getLayoutInflater().inflate(R.layout.nav_drawer_item, parent, false);
                }
                final Configurations.Feature item = getItem(position);
                return view;
            }
        };


        this.drawerLayout = layout;
        this.fragmentContainerId = fragmentContainerId;

        // Create the navigation drawer toggle helper.
        drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar,
                app_name, app_name) {

            @Override
            public void syncState() {
                super.syncState();
                updateUserName(activity);
                updateUserImage(activity);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateUserName(activity);
                updateUserImage(activity);
            }
        };

        // Set the listener to allow a swipe from the screen edge to bring up the navigation drawer.
        drawerLayout.setDrawerListener(drawerToggle);

        // Display the home selected_page_button on the toolbar that will open the navigation drawer.
        final ActionBar supportActionBar = containingActivity.getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeButtonEnabled(true);

        // Switch to display the hamburger icon for the home selected_page_button.
        drawerToggle.syncState();
    }

    private void updateUserName(final AppCompatActivity activity) {
        final SessionManager sessionManager = new SessionManager(mContext);

        final TextView userNameView = (TextView) activity.findViewById(R.id.userName);

        if (!sessionManager.isLoggedIn()) {
            // Not signed in
            userNameView.setText(activity.getString(R.string.main_nav_menu_default_user_text));
            //userNameView.setBackgroundColor(activity.getResources().getColor(R.color.nav_drawer_no_user_background));
            return;
        }
        HashMap<String, String> user = sessionManager.getUserDetails();
        final String userName =
                user.get(SessionManager.KEY_NAME);

        if (userName != null) {
            userNameView.setText(userName);
        }
    }

    private void updateUserImage(final AppCompatActivity activity) {

        final SessionManager sessionManager = new SessionManager(mContext);

        final ImageView imageView =
                (ImageView)activity.findViewById(R.id.userImage);

        if (!sessionManager.isLoggedIn()) {
            // Not signed in
            if (Build.VERSION.SDK_INT < 22) {
                imageView.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.user));
            }
            else {
                imageView.setImageDrawable(activity.getDrawable(R.mipmap.user));
            }

            return;
        }

        /**final Bitmap userImage = identityManager.getUserImage();
        if (userImage != null) {
            imageView.setImageBitmap(userImage);
        }**/

    }

    public void showHome() {
        final Fragment fragment = new FrontPageFragment();

        containingActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentContainerId, fragment, FrontPageFragment.class.getSimpleName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        // Set the title for the fragment.
        final ActionBar actionBar = containingActivity.getSupportActionBar();
        actionBar.setTitle(app_name);
        closeDrawer();
    }

    public void closeDrawer() {
        drawerLayout.closeDrawers();
    }

    public boolean isDrawerOpen() {
        return drawerLayout.isDrawerOpen(GravityCompat.START);}

    public void addFeatureToMenu(Configurations.Feature feature) {
        adapter.add(feature);
        adapter.notifyDataSetChanged();
    }

}
