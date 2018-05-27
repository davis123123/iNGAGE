package ingage.ingage.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import ingage.ingage.R;
import ingage.ingage.adapters.WalkthroughAdapter;
import me.relex.circleindicator.CircleIndicator;

public class WalkthroughActivity extends AppCompatActivity {

    private ViewPager walkthroughPager;
    WalkthroughAdapter adapter;
    LinearLayout llWalkthrough;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);
        setUpWalkthrough();

    }

    //onClick callback for walkthrough button
    public void closeWalkthrough(View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(String.valueOf(R.string.is_first_launch), false);
        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    private void setUpWalkthrough(){

        walkthroughPager = (ViewPager) findViewById(R.id.vpPager);
        adapter = new WalkthroughAdapter(this);
        walkthroughPager.setAdapter(adapter);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(walkthroughPager);

    }

}
