package ingage.ingage20;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by Davis on 4/27/2017.
 */

public class UserProfileActivity extends AppCompatActivity {

    String username, email, tribute_pts, subs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_user_profile);

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> info = session.getUserDetails();
        username = info.get(SessionManager.KEY_NAME);
        email = info.get(SessionManager.KEY_EMAIL);
        tribute_pts = info.get(SessionManager.KEY_TRIBUTE_POINTS);
        subs = info.get(SessionManager.KEY_SUBSCRIPTIONS);

        TextView user_info = (TextView) findViewById(R.id.user_name);
        TextView email_info = (TextView) findViewById(R.id.email);
        TextView pts_info = (TextView) findViewById(R.id.tribute_points);
        TextView subs_info = (TextView) findViewById(R.id.subscriptions);

        user_info.setText(username);
        email_info.setText(email);
        pts_info.setText(tribute_pts);
        subs_info.setText(subs);


    }


}
