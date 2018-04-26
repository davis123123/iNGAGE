package ingage.ingage20.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Objects;

import ingage.ingage20.R;
import ingage.ingage20.adapters.ViewPagerAdapter;
import ingage.ingage20.fragments.ArchivedFragment;
import ingage.ingage20.fragments.CategoriesFragment;
import ingage.ingage20.fragments.CategoriesPageFragment;
import ingage.ingage20.fragments.FragmentBase;
import ingage.ingage20.fragments.SearchResultArchivedFragment;
import ingage.ingage20.managers.SessionManager;

public class SearchResultActivity extends AppCompatActivity {
    Context mContext;
    SessionManager session;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        session = new SessionManager(mContext);
        HashMap<String, String> user = session.getUserDetails();
        String searchString = user.get(SessionManager.SEARCH_STRING);
        setTitle(searchString);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_search);
        Log.d("SEARCHRESULTACT", "here");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
    }

    private void setupViewPager(final ViewPager viewPager) {
        final Class searchResultArchivedFragmentClass = SearchResultArchivedFragment.class;
        Fragment searchResultArchivedFragment = Fragment.instantiate(this, searchResultArchivedFragmentClass.getName());

        final Class searchResultFragmentClass = CategoriesPageFragment.class;
        Fragment searchResultFragment = Fragment.instantiate(this, searchResultFragmentClass.getName());

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(searchResultFragment, "Active");
        viewPagerAdapter.addFragment(searchResultArchivedFragment, "Archived");

        viewPager.setAdapter(viewPagerAdapter);

        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(FragmentBase.RefreshEvent event) {
        onRefresh();
    };

    public void onRefresh(){
        viewPagerAdapter.notifyDataSetChanged();
    }
}
