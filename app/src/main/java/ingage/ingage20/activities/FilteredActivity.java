package ingage.ingage20.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ingage.ingage20.R;
import ingage.ingage20.fragments.CategoriesPageFragment;
import ingage.ingage20.fragments.SearchResultFragment;
import ingage.ingage20.handlers.SearchHandler;
import ingage.ingage20.managers.SessionManager;

public class FilteredActivity extends AppCompatActivity {

    SessionManager session;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        session = new SessionManager(getApplicationContext());

        bundle = getIntent().getExtras();
        String type = bundle.getString("type");
        if(type.equals("category"))
            filterByCategory();
        else if(type.equals("search"))
            filterBySearch();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            //toolbar back selected_page_button listener
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void filterByCategory(){
        final FragmentManager fragmentManager = this.getSupportFragmentManager();

        String category = bundle.getString("category");
        Animation fade = AnimationUtils.loadAnimation(FilteredActivity.this, R.anim.fade_in);

        session.updateCategory(category);
        session.updatePage("categoryDate");
        Log.d("STATE", "Nav item clicked: "+ category);
        final Class fragmentClass = CategoriesPageFragment.class;
        final Fragment fragment = Fragment.instantiate(getBaseContext(), fragmentClass.getName());
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_fragment_container, fragment, fragmentClass.getSimpleName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void filterBySearch(){
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        final Class fragmentClass = SearchResultFragment.class;
        String s = bundle.getString("query");
        Log.d("SEARCH",s);
                session.updateSearch(s);

                final Fragment fragment = Fragment.instantiate(getApplicationContext(), fragmentClass.getName());

                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.main_fragment_container, fragment, fragmentClass.getSimpleName())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
    }

}
