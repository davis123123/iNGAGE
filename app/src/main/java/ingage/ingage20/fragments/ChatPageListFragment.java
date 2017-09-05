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

import java.util.HashMap;

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

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = create(inflater, container, savedInstanceState);
        chatRoomManager = new ChatRoomManager(getContext());
        return v;
    }

    public View create(final LayoutInflater inflater, final ViewGroup container,
                       final Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_page_list, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatActivity = (ChatActivity) getActivity();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.pagerecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager
                (getActivity(),LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        chatPageListAdapter = new ChatPageListAdapter(this);
        Log.d("PAGEFRAG", "STARTED");
        recyclerView.setAdapter(chatPageListAdapter);

        chatPageListAdapter.setItemClickCallback(this);
        HashMap<String, String> chat = chatRoomManager.getUserDetails();
        totalPageNo = chat.get(ChatRoomManager.TOTAL_PAGES);
        Log.d("STATE", "totalpageno: " + chat.get(ChatRoomManager.TOTAL_PAGES));
        final int totalPages = Integer.parseInt(totalPageNo);
        for(int i = 1; i <= totalPages; i++){
            chatPageListAdapter.add(String.valueOf(i));
            Log.d("NOPAGES", " "+  i);
        }

        //initialize page indicator to last page
        autoClick(totalPages - 1);

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
        chatActivity.refreshPage();

        ChatPageListAdapter.ChatPageViewHolder prev =
                (ChatPageListAdapter.ChatPageViewHolder) recyclerView.findViewHolderForAdapterPosition(currentPage);
        if(prev != null)
            prev.pageNoBtn.setBackgroundResource(R.drawable.page_list_button);

        h.pageNoBtn.setBackgroundResource(R.drawable.selected_page_button);
        currentPage = p;
    }
}
