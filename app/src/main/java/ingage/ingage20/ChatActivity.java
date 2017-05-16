package ingage.ingage20;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

import ingage.ingage20.adapters.ChatArrayAdapter;
import ingage.ingage20.adapters.ThreadListAdapter;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ChatArrayAdapter chatAdapter;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        session = new SessionManager(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.listView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chatAdapter = new ChatArrayAdapter();
        recyclerView.setAdapter(chatAdapter);


        //add messages to recycler view by clicking send
        Button addButton = (Button) findViewById(R.id.send);
        if (addButton != null) {
            addButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    EditText textField = (EditText) findViewById(R.id.msgField);
                    //chat message
                    String messageText = textField.getText().toString();
                    HashMap<String, String> user = session.getUserDetails();
                    String messageBy = user.get(SessionManager.KEY_NAME);

                    textField.setText("");

                    ChatMessage msg = new ChatMessage(true, messageText, messageBy);
                    chatAdapter.add(msg);

                }

            });
        }
    }

    @Override
    public void onBackPressed() {
    }

}
