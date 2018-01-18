package ingage.ingage20.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import java.util.HashMap;
import java.util.Iterator;

import ingage.ingage20.R;
import ingage.ingage20.activities.ChatActivity;
import ingage.ingage20.adapters.ChatPageListAdapter;
import ingage.ingage20.managers.ChatRoomManager;

/**
 * Created by Davis on 6/24/2017.
 */

public class ChatPageListFragment extends Fragment implements ChatPageListAdapter.ItemClickCallback{
    View rootView;
    RecyclerView recyclerView;
    ChatPageListAdapter chatPageListAdapter;
    String totalPageNo;
    ChatRoomManager chatRoomManager;
    ChatActivity chatActivity;
    int currentPage = 1;
    DatabaseReference root;
    String thread_id;
    HashMap<String, String> chat;
    int pageCount =1;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = create(inflater, container, savedInstanceState);

        return v;
    }

    public View create(final LayoutInflater inflater, final ViewGroup container,
                       final Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_page_list, container, false);
        chatRoomManager = new ChatRoomManager(getContext());
        chatPageListAdapter = new ChatPageListAdapter(this,getContext());
        chat = chatRoomManager.getUserDetails();
        thread_id = chat.get(ChatRoomManager.THREAD_ID);
        root = FirebaseDatabase.getInstance().getReference().child(thread_id);
        chatPageListAdapter.setItemClickCallback(this);
        pageEventListener(root);
        return rootView;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatActivity = (ChatActivity) getActivity();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.pagerecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager
                (getActivity(),LinearLayoutManager.HORIZONTAL, false);
        currentPage = Integer.parseInt(chat.get(ChatRoomManager.CUR_PAGE));
        Log.d("PAGEFRAG", "STARTED " + currentPage);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatPageListAdapter);
        //recyclerView.scrollToPosition(chatPageListAdapter.getItemCount()-1);

    }

    private void pageEventListener(DatabaseReference root) {
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendPage(dataSnapshot);
            }
            @Override

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
    private void appendPage(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        Iterable<DataSnapshot> t = dataSnapshot.getChildren();
        Log.d("NEWPAGE", "has been made " + i);
        chatPageListAdapter.add(String.valueOf(pageCount++));
        chatPageListAdapter.notifyDataSetChanged();

    }

    public void autoClick(final int pos){

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                ChatPageListAdapter.ChatPageViewHolder lastHolder =
                        (ChatPageListAdapter.ChatPageViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);
                if (lastHolder != null)
                    onPgeBtnClick(lastHolder, pos);
                recyclerView.scrollToPosition(pos);

            }
        }, 500);

    }

    @Override
    public void onPgeBtnClick(ChatPageListAdapter.ChatPageViewHolder h , int p) {
        String pageNo = chatPageListAdapter.getItem(p);
        Log.d("PAGENO", String.valueOf(pageNo));
        chatRoomManager.updateCurrentPage(pageNo);

        //No need to refresh/update if there's only 1 page
        if(chatPageListAdapter.getItemCount() > 1) {
            Log.d("REFRESHED", String.valueOf(pageNo));
            chatActivity.refreshPage();
        }
        ChatPageListAdapter.ChatPageViewHolder prev =
                (ChatPageListAdapter.ChatPageViewHolder) recyclerView.findViewHolderForAdapterPosition(currentPage);
        if(prev != null) {
            Log.d("PAGENO","this "+prev);
            prev.pageNoBtn.setBackgroundResource(R.drawable.page_list_button);
            chatPageListAdapter.notifyDataSetChanged();
        }
        h.pageNoBtn.setBackgroundResource(R.drawable.selected_page_button);
        currentPage = p;
    }
}
