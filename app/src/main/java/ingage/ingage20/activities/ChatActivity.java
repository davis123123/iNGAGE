package ingage.ingage20.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.fragments.ChatFragment;
import ingage.ingage20.fragments.ChatPageListFragment;
import ingage.ingage20.handlers.ChatFeaturesHandler;
import ingage.ingage20.handlers.ChatRoomHandler;
import ingage.ingage20.handlers.SpectateRoomHandler;
import ingage.ingage20.handlers.UserRecentCommentHandler;
import ingage.ingage20.helpers.ChatMessageHelper;
import ingage.ingage20.managers.ChatRoomManager;
import ingage.ingage20.managers.SessionManager;

public class ChatActivity extends AppCompatActivity{

    SessionManager session;
    ChatRoomManager chatRoomManager;
    String temp_key, targetUser, username;
    private static final int RESULT_TARGET_USER = 1;
    DatabaseReference root;
    String thread_id;
    Long currentCooldown;
    Long currentTimeLeft;
    public static String user_side;
    TextView timerTv;
    Button addButton;
    EditText textField;
    CountDownTimer mCountDownTimer;
    Button useCoinBt;
    boolean tagged = false, paused = false, crossedPgeLmt = false, collapsed = true, haschar = false, threadEnded = false;
    int noPages;
    CountDownTimer mKickTimer;
    CountDownTimer mThreadEndTimer;
    DatabaseReference page_root;
    public static int curPage = -1;
    LinearLayout textArea, textButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new SessionManager(getApplicationContext());
        HashMap <String, String> user = session.getUserDetails();
        username = user.get(SessionManager.KEY_NAME);

        chatRoomManager = new ChatRoomManager(getApplicationContext());
        HashMap <String, String> chatRoomManagerUserDetails = chatRoomManager.getUserDetails();

        timerTv = (TextView) findViewById(R.id.timertv);
        useCoinBt = (Button) findViewById(R.id.cooldownButton);

        textArea = (LinearLayout) findViewById(R.id.text_area);
        textButtons = (LinearLayout) findViewById(R.id.text_right);

        timerTv.setVisibility(View.GONE);
        useCoinBt.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String title = extras.getString("title");
            Log.i("clicked title: " , title);
            ActionBar mActionBarToolbar = getSupportActionBar();
            mActionBarToolbar.setTitle(title);
        }


        useCoinBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("Click", "clicked");
                useCoin();
            }
        });//click to use token

        //getuser details
        HashMap<String, String> chat = chatRoomManager.getUserDetails();
        thread_id = chat.get(ChatRoomManager.THREAD_ID);
        user_side = chat.get(ChatRoomManager.SIDE);
        long secRemaining = secondsRemaining(chatRoomManagerUserDetails.get(ChatRoomManager.TIME_REMAINING));
        if(user_side != null) {
            endThreadTimer(secRemaining);
            kickTimer(900000); //fifteen minutes of inactivity will kick user out
            Log.d("STATE", "side: " + user_side);
        }//Does not kick out spectator with timer
        //ENTER MESSAGES WITH @TAGS
        textField = (EditText) findViewById(R.id.msgField);
        textChangeListener();
        setKeyBoardListener();

        //thread id for root of comments tree
        root = FirebaseDatabase.getInstance().getReference().child(thread_id);

        pageCount(root); //TAKES TIME TO TRANSACT

        //add messages to recycler view by clicking send
        addButton = (Button) findViewById(R.id.sendMessageButton);
        if (addButton != null) {
            addButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    sendMsg();
                }
            });//click to send message
        }
        HashMap<String, String> chat_user = chatRoomManager.getUserDetails();
        String spectator = chat_user.get(ChatRoomManager.SPECTATOR);
        if (spectator.equals("true")){
            setSpectateMode();
        }

    }

    private long secondsRemaining(String timeRemaining){
        String[] splittedString = timeRemaining.split(":");
        long seconds = Integer.parseInt(splittedString[0]) * 360; //get hours into seconds
        seconds += Integer.parseInt(splittedString[1]) * 60; //get minutes into seconds
        seconds += Integer.parseInt(splittedString[2]); //get seconds
        Log.d("TIME REMAINING", ""+seconds);
        return seconds;
    }

    private void textChangeListener(){
        textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String ss =  textField.getText().toString();
                if (ss.contains("@") && !haschar){
                    haschar = true;
                    //Start Tagging here
                    startTagActivity();
                }
                else if (!(ss.contains("@"))){
                    haschar = false;
                    tagged = false;
                    //Log.d("STATE", "text: " + ss);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //Expand message text field if keyboard is up
    public void setKeyBoardListener(){
        final View activityRootView = findViewById(R.id.rlChatActivity);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                String msg =  textField.getText().toString();

                //if keyboard is down
                if (heightDiff < convertToPx(this, 200)) {
                    if(!collapsed  && msg.length() == 0) {
                        useCoinBt.setVisibility(View.GONE);
                        textField.setSingleLine(true);
                        textField.setLines(1);
                        textField.setMaxLines(1);
                        collapsed = true;
                    }
                }

                //if keyboard is up
                else {
                    if(collapsed) {
                        textField.setSingleLine(false);
                        textField.setLines(4);
                        textField.setMaxLines(4);
                        useCoinBt.setVisibility(View.VISIBLE);
                        collapsed = false;
                    }
                }
            }
        });
    }

    //converts dp to px
    public float convertToPx(ViewTreeObserver.OnGlobalLayoutListener context, float valueInDp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    private void pageCount(final DatabaseReference root) {
        root.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                Log.d("STATE", "pageCOUNT() transaction called");
                noPages = (int) currentData.getChildrenCount();
                Log.d("STATE:nopage", String.valueOf(noPages));
                page_root = root.child(String.valueOf(noPages));
                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
            }

            //TODO:Error handle here
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                //inflate NUMBER OF PAGES HERE!!!!!!!!!!!
                Log.d("STATE", "pageCOUNT() oncomplete called");
                chatRoomManager.updateLatestPage(String.valueOf(noPages));
                chatRoomManager.updateCurrentPage(String.valueOf(noPages));
                goChatFragment();
                goPageFragment();
            }
        });
    }

    private void goPageFragment(){
        Log.d("PAGEFRAG" , "initialize PAGEFragment : ");
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        final Class fragmentClass = ChatPageListFragment.class;
        final Fragment fragment = Fragment.instantiate(getApplicationContext(), fragmentClass.getName());

        fragmentManager
                .beginTransaction()
                .replace(R.id.chat_pages_container, fragment, fragmentClass.getSimpleName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void goChatFragment(){
        /* initilize Chat Fragment*/
        Log.d("CHATFRAG" , "initialize ChatFragment : ");
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        final Class fragmentClass = ChatFragment.class;
        final Fragment fragment = Fragment.instantiate(getApplicationContext(), fragmentClass.getName());

        fragmentManager
                .beginTransaction()
                .replace(R.id.chat_fragment_container, fragment, fragmentClass.getSimpleName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void sendMsg(){
        String messageText = textField.getText().toString();

        if(threadEnded){
            Toast.makeText(getApplicationContext(), "Debate has ended. You can no longer make anymore arguments.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(messageText.length() > 0) {
            HashMap<String, String> user = session.getUserDetails();
            String messageBy = user.get(SessionManager.KEY_NAME);
            //checkCommentNum();

            //firebase area to send msg
            Map<String, Object> map = new HashMap<String, Object>();

            checkCommentNum(messageBy, messageText);
            UserRecentCommentHandler handler = new UserRecentCommentHandler();
            handler.enqueue(username, thread_id, messageText, user_side);

            //send token
            if (tagged) {
                tagged = false;
                sendCoin();
            }
            textField.setText("");

            //start cooldown timer
            timer(180000);
            //on send restart kicktimer
            mKickTimer.cancel();
            kickTimer(900000);
        }
        else{
            Toast.makeText(getApplicationContext(), "Please enter a message", Toast.LENGTH_LONG).show();
        }
    }

    private void checkCommentNum(final String messageBy, final String messageText) {
        Log.d("CHECKCOMMENTNUM"," " + page_root);
        //final DatabaseReference page_root = root.child("test");
        final String key = page_root.getKey();

        root.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                Log.d("CHECKCOMMENTNUM", String.valueOf(currentData) + " " + currentData.getChildrenCount());
                MutableData page = currentData.child(page_root.getKey());//get page
                int keyNo = Integer.parseInt(key) + 1;
                String newKey = String.valueOf(keyNo);
                Log.d("NEWKEY", " "+ newKey + " " + page);
                if(page.getChildrenCount() == 10){
                    Log.d("here", " "+ newKey);
                    if(!currentData.hasChild(newKey)) {//no one else created new page
                        Log.d("here", "if " + newKey);
                        crossPageLimit(key);
                        page_root =  root.child(newKey);
                        Log.d("NEWROOTIF", " "+ page_root);
                    }
                    else{
                        Log.d("here", "else "+ newKey);
                        page_root =  root.child(newKey);//newkey is incrmental of old page
                        Log.d("NEWROOT", " "+ page_root);
                    } //new page already created
                }

                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
            }

            //TODO:Error handle here
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d("TRANSCOMPLETE", " "+ dataSnapshot);
                insertComment(messageBy, messageText);//NEED TEST
                    if (crossedPgeLmt) {
                        crossedPgeLmt = false;
                        refreshPage();
                    }
            }
        });


    }

    private void insertComment(final String messageBy, final String messageText){
        Log.d("PAGELIMIT", " " + page_root);
        temp_key = page_root.push().getKey();

        DatabaseReference message_root = page_root.child(temp_key);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Map<String, Object> map_message = new HashMap<String, Object>();
        map_message.put("Username", messageBy);
        map_message.put("Msg", messageText);
        map_message.put("Side", user_side);
        map_message.put("upvotes", 0);
        map_message.put("downvotes", 0);
        map_message.put("TimeStamp", currentDateTimeString);
        message_root.updateChildren(map_message);
    }

    private void crossPageLimit(String key){
        Log.d("PAGELIMIT","yes");
        //create new page
        int keyNo = Integer.parseInt(key) + 1;
        String newKey = String.valueOf(keyNo);
        Log.d("PAGENO",key);
        chatRoomManager.updateLatestPage(newKey);
        chatRoomManager.updateCurrentPage(newKey);
        Map<String, Object> map_page = new HashMap<String, Object>();
        map_page.put(newKey, "");
        root.updateChildren(map_page);
        crossedPgeLmt = true;
    }//used ing checkPageNum

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        leaveRoom();
        Log.d("SPECTATORSTATE", "backpress: ");
    }

    @Override
    public void onPause(){
        super.onPause();
        HashMap<String, String> chat_user = chatRoomManager.getUserDetails();
        String side = chat_user.get(ChatRoomManager.SIDE);
        String thread_id = chat_user.get(ChatRoomManager.THREAD_ID);
        String spectator = chat_user.get(ChatRoomManager.SPECTATOR);
        if (spectator.equals("true")){
            paused = true;
            String type = "leave_spectate";
            String result = null;

            HashMap<String, String> user = session.getUserDetails();
            String username = user.get(SessionManager.KEY_NAME);

            SpectateRoomHandler spectateRoomHandler = new SpectateRoomHandler(getApplicationContext());
            try {
                result = spectateRoomHandler.execute(type, thread_id, username, side).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            Log.d("SPECTATORSTATE", "textpause: " + result);
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("RESUMECHAT", "textRESUME ");
        HashMap<String, String> chat_user = chatRoomManager.getUserDetails();
        String spectator = chat_user.get(ChatRoomManager.SPECTATOR);
        //check from cache is user is spectator
        if(spectator.equals("true") && paused) {
            String type = "spectate";
            session = new SessionManager(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            String username = user.get(SessionManager.KEY_NAME);

            SpectateRoomHandler spectateRoomHandler = new SpectateRoomHandler(getApplicationContext());
            String result;
            setSpectateMode();
            try {
                result = spectateRoomHandler.execute(type, thread_id, username).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            paused = false;
        }
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

    private void setSpectateMode(){
        textField.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);
        timerTv.setVisibility(View.GONE);
        useCoinBt.setVisibility(View.GONE);
        textArea.setVisibility(View.GONE);
        textButtons.setVisibility(View.GONE);
    }

    private void leaveRoom() {
        HashMap<String, String> chat_user = chatRoomManager.getUserDetails();
        String side = chat_user.get(ChatRoomManager.SIDE);
        String thread_id = chat_user.get(ChatRoomManager.THREAD_ID);
        String spectator = chat_user.get(ChatRoomManager.SPECTATOR);

        if(mKickTimer != null){
            mKickTimer.cancel();
            mKickTimer = null;
        }

        if(mThreadEndTimer != null){
            mThreadEndTimer.cancel();
            mThreadEndTimer = null;
        }

        if (spectator.equals("true")){
            String type = "leave_spectate";
            String result = null;

            HashMap<String, String> user = session.getUserDetails();
            String username = user.get(SessionManager.KEY_NAME);

            SpectateRoomHandler spectateRoomHandler = new SpectateRoomHandler(getApplicationContext());
            try {
                result = spectateRoomHandler.execute(type, thread_id, username, side).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            Log.d("SPECTATORSTATE", "text: " + result);
        }
        else {
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
        //leave user's session on cache
        chatRoomManager.updateUserRoomSession("", "", "");

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
             new AlertDialog.Builder(this)
                     .setTitle("Sorry you are out of coins!")
                     .setMessage("Try again when you get tagged!")
                     .setIcon(android.R.drawable.ic_dialog_alert)
                     .show();
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
    }//timer for meesage cooldown

    private void endThreadTimer(long remainingTime) {
        remainingTime *= 1000;
        mThreadEndTimer =
                new CountDownTimer(remainingTime, 1000) {

                    public void onTick(long millisUntilFinished) {
                        //keeps track of current cooldown
                        currentTimeLeft = millisUntilFinished;
                    }

                    public void onFinish() {
                        Toast.makeText(getApplicationContext(), "Debate has Ended! Hope you had fun!!", Toast.LENGTH_LONG).show();
                        threadEnded = true;
                    }
                }.start();

    }
    private void kickTimer(long inactiveTime){
        mKickTimer =
                new CountDownTimer(inactiveTime, 1000) {

                    public void onTick(long millisUntilFinished) {
                        //keeps track of current cooldown
                        currentCooldown = millisUntilFinished;
                        //timerTv.setText(millisUntilFinished / 1000 + " s");
                    }

                    public void onFinish() {
                        Toast.makeText(getApplicationContext(), "You have been kicked out due to inactivity!", Toast.LENGTH_LONG).show();
                        leaveRoom();
                        finish();

                    }
                }.start();
    }//timer for kicking user out for inactivity

    private void blockMSG(){
        addButton.setVisibility(View.GONE);
        timerTv.setVisibility(View.VISIBLE);
    }//modify block functions here

    private void unblockMSG(){
        addButton.setVisibility(View.VISIBLE);
        timerTv.setVisibility(View.GONE);
    }//modify unblock functions here

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

    public void startProfileActivity(String user){
        Intent i = new Intent(this, UserProfileActivity.class);
        i.putExtra("USER", user);
        startActivity(i);
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

    public void refreshPage(){
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        Class fragmentClass = ChatFragment.class;
        final Fragment fragment = Fragment.instantiate(this, fragmentClass.getName());

        Class pageFragmentClass = ChatPageListFragment.class;
        final Fragment pageFragment = Fragment.instantiate(this, pageFragmentClass.getName());
        fragmentManager
                .beginTransaction()
                .replace(R.id.chat_fragment_container, fragment, fragmentClass.getSimpleName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        fragmentManager
                .beginTransaction()
                .replace(R.id.chat_pages_container, pageFragment, pageFragmentClass.getSimpleName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
}