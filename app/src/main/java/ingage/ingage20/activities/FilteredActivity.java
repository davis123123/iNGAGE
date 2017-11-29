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
import android.view.View;

import ingage.ingage20.R;
import ingage.ingage20.fragments.CategoriesPageFragment;
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

        session = new SessionManager(getApplicationContext());

        bundle = getIntent().getExtras();
        String type = bundle.getString("type");
        if(type.equals("category"))
            filterByCategory();
    }

    private void filterByCategory(){
        final FragmentManager fragmentManager = this.getSupportFragmentManager();

        String category = bundle.getString("category");

        session.updateCategory(category);
        session.updatePage("categoryDate");
        Log.d("STATE", "Nav item clicked: "+ category);
        final Class fragmentClass = CategoriesPageFragment.class;
        final Fragment fragment = Fragment.instantiate(getBaseContext(), fragmentClass.getName());
        fragmentManager
                .beginTransaction()
                .replace(R.id.filtered_fragment_container, fragment, fragmentClass.getSimpleName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

}
