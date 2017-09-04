package ingage.ingage20.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ingage.ingage20.R;
import ingage.ingage20.helpers.ChatMessageHelper;
import ingage.ingage20.managers.SessionManager;

/**
 * Created by Davis on 6/24/2017.
 */

public class ChatPageListAdapter extends RecyclerView.Adapter<ChatPageListAdapter.ChatPageViewHolder> {

    List<String> list = new ArrayList<String>();
    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback{
        void onPgeBtnClick(ChatPageViewHolder h, int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }

    public ChatPageListAdapter(final ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }

    @Override
    public ChatPageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(R.layout.page_list_layout, parent, shouldAttachToParentImmediately);
        ChatPageViewHolder viewHolder = new ChatPageViewHolder(view);
        Log.d("PAGEFRAG", "BINDVIEW");
        return viewHolder;
    }

    public String getItem(int position){
        return list.get(position);
    }

    @Override
    public void onBindViewHolder(final ChatPageViewHolder holder, final int position) {
        String pageNo = this.getItem(position);
        holder.bind(position);
        holder.pageNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickCallback.onPgeBtnClick(holder, position);
            }
        });
    }

    public void add(String pageNo){
        list.add(pageNo);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatPageViewHolder extends RecyclerView.ViewHolder {
        public Button pageNoBtn;
        public ChatPageViewHolder(View itemView) {
            super(itemView);
            pageNoBtn = (Button) itemView.findViewById(R.id.pageBtn);
        }

        private void bind(int listIndex){
            pageNoBtn.setText(getItem(listIndex));//gives page Number
        }
    }
}