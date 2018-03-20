package ingage.ingage20.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.activities.ChatActivity;
import ingage.ingage20.activities.UserProfileActivity;
import ingage.ingage20.adapters.ChatArrayAdapter;
import ingage.ingage20.adapters.ChatPageListAdapter;
import ingage.ingage20.handlers.ChatFeaturesHandler;
import ingage.ingage20.handlers.VotesHandler;
import ingage.ingage20.helpers.ChatMessageHelper;
import ingage.ingage20.managers.ChatRoomManager;
import ingage.ingage20.managers.SessionManager;

/**
 * Created by Davis on 6/24/2017.
 */

public class ChatFragment extends Fragment implements ChatArrayAdapter.ItemClickCallback{

    RecyclerView recyclerView;
    ChatArrayAdapter chatAdapter;
    SessionManager session;
    ChatRoomManager chatRoomManager;
    ChatActivity chatActivity;
    DatabaseReference root;
    View rootView;
    Long chat_upvote, chat_downvote;
    DatabaseReference currPageData;
    public static String user_side;
    String chat_msg, chat_username, chat_side, chat_timestamp, chat_id, thread_id, username;
    HashMap<String, String> userVotes = new HashMap<String, String>();

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        View v = create(inflater, container, savedInstanceState);
        //thread id for root of comments tree
        //getuser details
        chatRoomManager = new ChatRoomManager(getContext());
        HashMap<String, String> chat = chatRoomManager.getUserDetails();
        thread_id = chat.get(ChatRoomManager.THREAD_ID);
        user_side = chat.get(ChatRoomManager.SIDE);

        root = FirebaseDatabase.getInstance().getReference().child(thread_id);

        session = new SessionManager(getContext());
        HashMap <String, String> user = session.getUserDetails();
        username = user.get(SessionManager.KEY_NAME);
        username = user.get(SessionManager.KEY_NAME);
        return v;
    }

    public View create(final LayoutInflater inflater, final ViewGroup container,
                       final Bundle savedInstanceState){
        // Inflate the layout for this fragment
        Log.d("INFLATECHATFRAG" , "here" );
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        rootView.bringToFront();
        return rootView;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        //start adapter
        recyclerView = (RecyclerView) rootView.findViewById(R.id.chatrecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        chatAdapter = new ChatArrayAdapter(this);
        recyclerView.setAdapter(chatAdapter);
        //get user votes
        insertUserVotesHashMap();
        chatActivity = (ChatActivity) getActivity();
        //set click for upvote and downvotes in each chatmessage
        chatAdapter.setItemClickCallback(this);
        //dataSnapshot == root, need child of pages
        HashMap<String, String> chat = chatRoomManager.getUserDetails();

        currPageData = root.child(String.valueOf(
                chat.get(ChatRoomManager.CUR_PAGE)));//get current page from cache
        Log.d("CURPAGE", "pageNo: "+ chat.get(ChatRoomManager.CUR_PAGE) );

        eventListener(currPageData);
    }


    private void insertUserVotesHashMap() {
        VotesHandler votesHandler = new VotesHandler(getContext());
        String type = "getUserVotes";
        String result = "";

        try {
            result = votesHandler.execute(type, thread_id, username).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("INSERTHASHSTATE", "text: "+result );
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonObject = new JSONObject(result);
            jsonArray = jsonObject.getJSONArray("server_response");
            int count= 0;
            String chat_id = "", vote_type = "";
            while(count < jsonArray.length()){
                JSONObject JO = jsonArray.getJSONObject(count);
                chat_id = JO.getString("chat_id");
                vote_type = JO.getString("vote_type");
                Log.d("JSONSTATE", "text: " + chat_id+ vote_type);
                //put all votes into hashmap with chat_id as key
                userVotes.put(chat_id, vote_type);
                count++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onUpvoteClick(int p) {
        //Log.d("vote" , "up : ");
        //get correct chat msg with ith key from chatmessage helper
        ChatMessageHelper chatMessageHelper = (ChatMessageHelper) chatAdapter.getItem(p);
        String chat_key = chatMessageHelper.getMessageID();
        DatabaseReference message_root = currPageData.child(chat_key);
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
        DatabaseReference message_root = currPageData.child(chat_key);
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
        DatabaseReference message_root = currPageData.child(chat_key);
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
        DatabaseReference message_root = currPageData.child(chat_key);
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
        ChatFeaturesHandler chatFeaturesHandler = new ChatFeaturesHandler(getContext());
        ChatMessageHelper chatMessageHelper = (ChatMessageHelper) chatAdapter.getItem(p);

        //sets user's vote to memory
        chatMessageHelper.setUserVote(vote);
        //username is messageBy
        String messageBy = chatMessageHelper.getMessageUser();
        Log.d("insertvote", type + vote+ prev_voted);
        String chat_id = chatMessageHelper.getMessageID();
        String chat_side = chatMessageHelper.getSide();
        HashMap<String, String> chat = chatRoomManager.getUserDetails();
        String thread_id = chat.get(ChatRoomManager.THREAD_ID);
        session = new SessionManager(getContext());
        HashMap<String, String> user = session.getUserDetails();
        //chatuser is current user of app
        String chat_user = user.get(SessionManager.KEY_NAME);
        //insert vote into target user profile and own profile
        Log.d("insertvote", chat_user);

        String result = "";
        try {
            result = chatFeaturesHandler.execute(type, messageBy, thread_id, prev_voted, chat_id, vote, chat_side, chat_user).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //Log.d("insertvote", result);
    }//inserts vote in mysql database

    @Override
    public void onAvatarClick(int p) {
        ChatFeaturesHandler chatFeaturesHandler = new ChatFeaturesHandler(getContext());
        ChatMessageHelper chatMessageHelper = (ChatMessageHelper) chatAdapter.getItem(p);
        String user = chatMessageHelper.getMessageUser();
        //Log.d("AvatarClick", chatMessageHelper.getMessageUser());
        chatActivity.startProfileActivity(user);
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
            //gets previous msg of user's vote status
            ChatMessageHelper msgId = (ChatMessageHelper) chatAdapter.getItemFromID(chat_id);

            String chat_userVote = msgId.getUserVote();
            Log.d("USERVOTE" , "result : " + chat_userVote);
            ChatMessageHelper msg = new ChatMessageHelper(chat_id, chat_side, chat_msg, chat_username,
                    chat_upvote, chat_downvote, chat_timestamp, chat_userVote);
            chatAdapter.update(msg, chat_id, true);
            //chatAdapter.notifyDataSetChanged();
        }
    }

    private void appendChatConversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        Log.d("STATE" , "pageData2 : " + dataSnapshot);
        while (i.hasNext()){
            chat_id = dataSnapshot.getKey();
            Log.d("STATE" , "result : " + chat_id);
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_side = (String) ((DataSnapshot)i.next()).getValue();
            chat_timestamp = (String) ((DataSnapshot)i.next()).getValue();
            chat_username = (String) ((DataSnapshot)i.next()).getValue();
            chat_downvote = (Long)((DataSnapshot)i.next()).getValue();
            chat_upvote = (Long)((DataSnapshot)i.next()).getValue();

            String chat_userVote;
            chat_userVote = userVotes.get(chat_id);
            Log.d("CHATVOTE" , "result : " + chat_userVote);
            ChatMessageHelper msg = new ChatMessageHelper(chat_id, chat_side, chat_msg, chat_username, chat_upvote,
                    chat_downvote, chat_timestamp, chat_userVote);
            chatAdapter.add(msg);
        }
        chatAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    } //iterates through all comments under the thread_id to get information

}
