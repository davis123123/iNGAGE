package ingage.ingage20;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by Davis on 5/3/2017.
 */

public class ChatRoomActivity extends AppCompatActivity{
    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    SessionManager sessionManager;
    RelativeLayout activity_chat_room;
    FloatingActionButton fab;


    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        sessionManager = new SessionManager(getApplicationContext());
        final HashMap<String, String> user = sessionManager.getUserDetails();
        //activity_chat_room = (RelativeLayout) findViewById(R.id.activity_chat_room);
        fab = (FloatingActionButton) findViewById(R.id.send_fab);

        //check if not signed in, then navigate to signin page
        sessionManager.checkLogin();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input_text);
                FirebaseDatabase.getInstance().getReference().push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                user.get(SessionManager.KEY_NAME)));
                input.setText("");
            }
        });
        displayChatMessage();
    }

    private void displayChatMessage() {
        ListView listOfMessage = (ListView) findViewById(R.id.list_of_message);
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.chat_list_item, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                //get reference to the views of the chat_list_item.xml
                TextView messageText, messageUser, messageTime;
                messageText = (TextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
            }
        };

        listOfMessage.setAdapter(adapter);
    }

}
