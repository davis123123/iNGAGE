package ingage.ingage20.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.adapters.ChatArrayAdapter;
import ingage.ingage20.handlers.ChatFeaturesHandler;
import ingage.ingage20.handlers.ChatRoomHandler;
import ingage.ingage20.helpers.ChatMessageHelper;
import ingage.ingage20.managers.ChatRoomManager;
import ingage.ingage20.managers.SessionManager;

public class ChatActivity extends AppCompatActivity implements ChatArrayAdapter.ItemClickCallback{

    RecyclerView recyclerView;
    ChatArrayAdapter chatAdapter;
    SessionManager session;
    ChatRoomManager chatRoomManager;
    String JsonString;
    String temp_key;
    String targetUser;
    private static final int RESULT_TARGET_USER = 1;
    DatabaseReference root;
    String chat_msg, chat_username, chat_side, chat_timestamp, chat_id;
    Long chat_upvote, chat_downvote, currentCooldown;
    public static String user_side;
    TextView timerTv;
    ImageButton addButton;
    EditText textField;
    boolean haschar = false;
    CountDownTimer mCountDownTimer;
    Button useCoinBt;
    boolean tagged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        session = new SessionManager(getApplicationContext());
        chatRoomManager = new ChatRoomManager(getApplicationContext());

        recyclerView = (RecyclerView) findViewById(R.id.chatrecyclerView);

        timerTv = (TextView) findViewById(R.id.timertv);
        useCoinBt = (Button) findViewById(R.id.cooldownButton);

        useCoinBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("Click", "clicked");
                useCoin();
            }
        });//click to use token

        //getuser details
        HashMap<String, String> chat = chatRoomManager.getUserDetails();
        String thread_id = chat.get(ChatRoomManager.THREAD_ID);
        user_side = chat.get(ChatRoomManager.SIDE);
        Log.d("STATE", "side: " + user_side);


        //start adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chatAdapter = new ChatArrayAdapter();
        recyclerView.setAdapter(chatAdapter);

        //set click for upvote and downvotes in each chatmessage
        chatAdapter.setItemClickCallback(this);

        //ENTER MESSAGES WITH @TAGS
        textField = (EditText) findViewById(R.id.msgField);
        textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String ss =  textField.getText().toString();
                Toast.makeText(getBaseContext(),"itemclick" + haschar,Toast.LENGTH_SHORT).show();
                if (ss.contains("@") && !haschar){
                    haschar = true;
                    //Start Tagging here
                    startTagActivity();
                }
                else if (!(ss.contains("@"))){
                    haschar = false;
                    tagged = false;
                    Log.d("STATE", "text: " + ss);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        root = FirebaseDatabase.getInstance().getReference().child(thread_id);

        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)){
            JsonString = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
        }

        //add messages to recycler view by clicking send
        addButton = (ImageButton) findViewById(R.id.sendMessageButton);
        if (addButton != null) {
            addButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    sendMsg();
                }
            });//click to send message

            //calls event listener to update message in realtime
            eventListener(root);
        }
    }

    private void sendMsg(){
        timer(180000);
        String messageText = textField.getText().toString();
        HashMap<String, String> user = session.getUserDetails();
        String messageBy = user.get(SessionManager.KEY_NAME);

        //firebase area to send msg
        Map<String, Object> map = new HashMap<String, Object>();
        temp_key = root.push().getKey();
        root.updateChildren(map);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        DatabaseReference message_root = root.child(temp_key);
        Map<String, Object> map_message = new HashMap<String, Object>();
        map_message.put("Username", messageBy);
        map_message.put("Msg", messageText);
        map_message.put("Side", user_side);
        map_message.put("upvotes", 0);
        map_message.put("downvotes", 0);
        map_message.put("TimeStamp", currentDateTimeString);
        message_root.updateChildren(map_message);

        //send token
        if (tagged) {
            tagged = false;
            sendCoin();
        }
        textField.setText("");

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        int pos = chatAdapter.getItemCount()-1;
        manager.scrollToPosition(pos);
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

    private void sendCoin(){
        String type = "send_coin";
        String result;
        ChatFeaturesHandler chatFeaturesHandler= new ChatFeaturesHandler(getApplicationContext());
        try {
            result = chatFeaturesHandler.execute(type, targetUser).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void eventListener(DatabaseReference root){
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendChatConversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateChatConversation(dataSnapshot);

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

    private void updateChatConversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()){
            chat_id = dataSnapshot.getKey();
            Log.d("STATE" , "result : " + chat_id);
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_side = (String) ((DataSnapshot)i.next()).getValue();
            chat_timestamp = (String) ((DataSnapshot)i.next()).getValue();
            chat_username = (String) ((DataSnapshot)i.next()).getValue();
            chat_downvote = (Long)((DataSnapshot)i.next()).getValue();
            chat_upvote = (Long)((DataSnapshot)i.next()).getValue();
            ChatMessageHelper msg = new ChatMessageHelper(chat_id, chat_side, chat_msg, chat_username, chat_upvote,
                    chat_downvote, chat_timestamp);
            chatAdapter.update(msg, chat_id);
            chatAdapter.notifyDataSetChanged();
        }


    }

    private void appendChatConversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()){
            chat_id = dataSnapshot.getKey();
            Log.d("STATE" , "result : " + chat_id);
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_side = (String) ((DataSnapshot)i.next()).getValue();
            chat_timestamp = (String) ((DataSnapshot)i.next()).getValue();
            chat_username = (String) ((DataSnapshot)i.next()).getValue();
            chat_downvote = (Long)((DataSnapshot)i.next()).getValue();
            chat_upvote = (Long)((DataSnapshot)i.next()).getValue();

            ChatMessageHelper msg = new ChatMessageHelper(chat_id, chat_side, chat_msg, chat_username, chat_upvote,
                    chat_downvote, chat_timestamp);
            chatAdapter.add(msg);
        }
        chatAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    } //iterates through all comments under the thread_id to get information

    private void useCoin(){
        String type = "use_coin";
        String result = "";
        HashMap<String, String> chat_user = session.getUserDetails();
        String username = chat_user.get(SessionManager.KEY_NAME);
        ChatFeaturesHandler chatFeaturesHandler= new ChatFeaturesHandler(getApplicationContext());
        try {
            result = chatFeaturesHandler.execute(type, username).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if(result.equals("success")) {
            if (mCountDownTimer != null)
                mCountDownTimer.cancel();
            if (currentCooldown > 30000) {
                timer(currentCooldown - 30000);
            }
        }
        else{
            //tell user no coins left
        }
    }

    private void timer(long initialCooldown) {
        Timer t = new Timer();
        timerTv = (TextView) findViewById(R.id.timertv);

        //Set the schedule function and rate
        mCountDownTimer =
        new CountDownTimer(initialCooldown, 1000) {

            public void onTick(long millisUntilFinished) {
                blockMSG();
                //keeps track of current cooldown
                currentCooldown = millisUntilFinished;
                timerTv.setText(millisUntilFinished / 1000 + " s");
            }

            public void onFinish() {
                unblockMSG();

            }
        }.start();
    }

    private void blockMSG(){
        addButton.setVisibility(View.INVISIBLE);
    }//modify block functions here

    private void unblockMSG(){
        addButton.setVisibility(View.VISIBLE);
        timerTv.setVisibility(View.INVISIBLE);
    }//modify unblock functions here

    @Override
    public void onUpvoteClick(int p) {
        //Log.d("vote" , "up : ");
        //get correct chat msg with ith key from chatmessage helper
        ChatMessageHelper chatMessageHelper = (ChatMessageHelper) chatAdapter.getItem(p);
        String chat_key = chatMessageHelper.getMessageID();
        DatabaseReference message_root = root.child(chat_key);
        //get upvote data
        DatabaseReference upvote_count = message_root.child("upvotes");


        upvote_count.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                Log.d("Data", String.valueOf(currentData));

                if(currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
            }

            //TODO:Error handle here
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    @Override
    public void onDownvoteClick(int p) {
        //Log.d("vote" , "down : ");
        ChatMessageHelper chatMessageHelper = (ChatMessageHelper) chatAdapter.getItem(p);
        String chat_key = chatMessageHelper.getMessageID();
        DatabaseReference message_root = root.child(chat_key);
        //get upvote data
        DatabaseReference downvote_count = message_root.child("downvotes");

        downvote_count.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                Log.d("Data", String.valueOf(currentData));

                if(currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
            }

            //TODO:Error handle here
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });
    }

    @Override
    public void removeUpvote(int p) {
        ChatMessageHelper chatMessageHelper = (ChatMessageHelper) chatAdapter.getItem(p);
        String chat_key = chatMessageHelper.getMessageID();
        DatabaseReference message_root = root.child(chat_key);
        //get upvote data
        DatabaseReference upvote_count = message_root.child("upvotes");


        upvote_count.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                Log.d("Data", String.valueOf(currentData));

                if(currentData.getValue() == null) {
                    currentData.setValue(0);
                } else {
                    currentData.setValue((Long) currentData.getValue() - 1);
                }
                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
            }

            //TODO:Error handle here
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    @Override
    public void removeDownvote(int p) {
        ChatMessageHelper chatMessageHelper = (ChatMessageHelper) chatAdapter.getItem(p);
        String chat_key = chatMessageHelper.getMessageID();
        DatabaseReference message_root = root.child(chat_key);
        //get upvote data
        DatabaseReference downvote_count = message_root.child("downvotes");

        downvote_count.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                Log.d("Data", String.valueOf(currentData));

                if(currentData.getValue() == null) {
                    currentData.setValue(0);
                } else {
                    currentData.setValue((Long) currentData.getValue() - 1);
                }
                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
            }

            //TODO:Error handle here
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });
    }

    @Override
    public void insertVote(int p, String prev_voted, String vote) {
        String type = "insert_vote";
        ChatFeaturesHandler chatFeaturesHandler = new ChatFeaturesHandler(getApplicationContext());
        ChatMessageHelper chatMessageHelper = (ChatMessageHelper) chatAdapter.getItem(p);
        String chat_user = chatMessageHelper.getMessageUser();
        Log.d("insertvote", type+ chat_user+ vote+ prev_voted);

        chatFeaturesHandler.execute(type, chat_user, vote, prev_voted);
    }


    @Override
    public void  onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("savedTitle", textField.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        textField.getText().insert(textField.getSelectionStart(),
                savedInstanceState.getString("savedTitle"));
    }

    private void startTagActivity(){
        Intent selectUser = new Intent(this, RoomUsersActivity.class);
        startActivityForResult(selectUser, RESULT_TARGET_USER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_TARGET_USER && resultCode == RESULT_OK && data != null) {
            targetUser = data.getExtras().getString("Username");
            textField.append(targetUser);
            Log.d("TARGET" , "user : " + targetUser);
            tagged = true;
        }
    }
}