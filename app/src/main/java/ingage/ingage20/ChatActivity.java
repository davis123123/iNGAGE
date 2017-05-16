package ingage.ingage20;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.HashMap;

import ingage.ingage20.Adapters.ChatArrayAdapter;
import ingage.ingage20.Helpers.ChatMessageHelper;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ChatArrayAdapter chatAdapter;
    SessionManager session;
    String JsonString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        session = new SessionManager(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.chatrecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chatAdapter = new ChatArrayAdapter();
        recyclerView.setAdapter(chatAdapter);


        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)){
            JsonString = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
        }

        //add messages to recycler view by clicking send
        ImageButton addButton = (ImageButton) findViewById(R.id.sendMessageButton);
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

                    ChatMessageHelper msg = new ChatMessageHelper(true, messageText, messageBy);
                    chatAdapter.add(msg);

                }

            });
        }
    }

    @Override
    public void onBackPressed() {
    }

}
