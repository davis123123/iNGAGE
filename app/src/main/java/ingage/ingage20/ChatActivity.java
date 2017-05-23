package ingage.ingage20;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.adapters.ChatArrayAdapter;
import ingage.ingage20.handlers.ChatRoomHandler;
import ingage.ingage20.helpers.ChatMessageHelper;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ChatArrayAdapter chatAdapter;
    SessionManager session;
    ChatRoomManager chatRoomManager;
    String JsonString;
    String temp_key;
    DatabaseReference root;
    String chat_msg, chat_username, chat_side, chat_timestamp;
    String user_side;
    TextView timerTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        session = new SessionManager(getApplicationContext());
        chatRoomManager = new ChatRoomManager(getApplicationContext());

        recyclerView = (RecyclerView) findViewById(R.id.chatrecyclerView);

        timerTv = (TextView) findViewById(R.id.timertv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chatAdapter = new ChatArrayAdapter();
        recyclerView.setAdapter(chatAdapter);

        HashMap<String, String> chat = chatRoomManager.getUserDetails();
        String thread_id = chat.get(ChatRoomManager.THREAD_ID);
        user_side = chat.get(ChatRoomManager.SIDE);
        Log.d("STATE", "side: " + user_side);

        root = FirebaseDatabase.getInstance().getReference().child(thread_id);

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
                    timer();
                    String messageText = textField.getText().toString();
                    HashMap<String, String> user = session.getUserDetails();
                    String messageBy = user.get(SessionManager.KEY_NAME);

                    //firebase area
                    Map<String, Object> map = new HashMap<String, Object>();
                    temp_key = root.push().getKey();
                    root.updateChildren(map);
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                    DatabaseReference message_root = root.child(temp_key);
                    Map<String, Object> map_message = new HashMap<String, Object>();
                    map_message.put("Usernane", messageBy);
                    map_message.put("Msg", messageText);
                    map_message.put("Side", user_side);
                    map_message.put("TimeStamp", currentDateTimeString);
                    message_root.updateChildren(map_message);
                    textField.setText("");

                    RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                    int pos = chatAdapter.getItemCount()-1;
                    manager.scrollToPosition(pos);
                }
            });//click to send message

            //calls event listener to update message in realtime
            eventListener(root);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        leaveRoom();
    }

    private void leaveRoom() {
        HashMap<String, String> chat_user = chatRoomManager.getUserDetails();
        String side = chat_user.get(ChatRoomManager.SIDE);
        String thread_id = chat_user.get(ChatRoomManager.THREAD_ID);

        String type = "leave";
        String result = null;

        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(SessionManager.KEY_NAME);

        ChatRoomHandler chatRoomHandler = new ChatRoomHandler(getApplicationContext());
        try {
            result = chatRoomHandler.execute(type, thread_id, username, side).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void eventListener(DatabaseReference root){
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()){
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_side = (String) ((DataSnapshot)i.next()).getValue();
            chat_username = (String) ((DataSnapshot)i.next()).getValue();
            chat_timestamp = (String) ((DataSnapshot)i.next()).getValue();
            ChatMessageHelper msg = new ChatMessageHelper(chat_side, chat_msg, chat_username, chat_timestamp);
            chatAdapter.add(msg);
        }
    } //iterates through all comments under the thread_id to get information

    private void timer() {
        Timer t = new Timer();
        timerTv = (TextView) findViewById(R.id.timertv);
        //Set the schedule function and rate
        new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerTv.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timerTv.setVisibility(View.INVISIBLE);
            }
        }.start();
    }
}
