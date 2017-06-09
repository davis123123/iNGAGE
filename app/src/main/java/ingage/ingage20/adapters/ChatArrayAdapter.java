package ingage.ingage20.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ingage.ingage20.R;
import ingage.ingage20.helpers.ChatMessageHelper;


public class ChatArrayAdapter extends RecyclerView.Adapter<ChatArrayAdapter.ChatViewHolder>{

    private Context mContext;
    private static final String TAG = ThreadListAdapter.class.getSimpleName();
    List <ChatMessageHelper> list = new ArrayList<ChatMessageHelper>();
    HashMap<String, Integer> chatHash = new HashMap<String, Integer>();


    private ItemClickCallback itemClickCallback;
    String side;

    public interface ItemClickCallback{
        void onUpvoteClick(int p);
        void onDownvoteClick(int p);
        void removeUpvote(int p);
        void removeDownvote(int p);
        //inserts vote into userprofile
        void insertVote(int p, String prev_voted, String vote);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }


    public ChatArrayAdapter( ){
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Get the RecyclerView item layout
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(R.layout.chat_layout, viewGroup, shouldAttachToParentImmediately);
        Log.d("STATE", "viewType: " + viewType);

        if(viewType == 0)
            view = inflater.inflate(R.layout.chat_layout, viewGroup, shouldAttachToParentImmediately);
        else if(viewType == 1)
            view = inflater.inflate(R.layout.chat_layout_right, viewGroup, shouldAttachToParentImmediately);

        ChatViewHolder viewHolder = new ChatViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        //final Object dataObj = list.get(position);

        if (list.get(position).getSide().equals("agree")) {
            return 0;
        }

        else if (list.get(position).getSide().equals("disagree")) {
            return 1;
        }

        return -1;
    }


    public void add(ChatMessageHelper object){
        list.add(object);
        String chat_id = object.getMessageID();
        chatHash.put(chat_id, getItemCount() - 1);
    }

    public void update(ChatMessageHelper newObject, String chat_id){
        //get old chatmsg
        int position = chatHash.get(chat_id);

        //update oldmsg with newobject
        ChatMessageHelper chatMessageHelper = (ChatMessageHelper) getItem(position);
        chatMessageHelper.setMessageDownvote(newObject.getMessageDownvote());
        chatMessageHelper.setMessageUpvote(newObject.getMessageUpvote());
        chatMessageHelper.setMessageText(newObject.getMessageText());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Object getItem(int position){
        return list.get(position);
    }

    public Object getItemFromID(String chat_id){
        int position = chatHash.get(chat_id);
        return list.get(position);
    }

    @Override
    public void onBindViewHolder(final ChatArrayAdapter.ChatViewHolder holder, final int position) {
        final ChatMessageHelper chatMessageHelper = (ChatMessageHelper) this.getItem(position);
        holder.bind(position);
        String userVote = "";
        userVote= chatMessageHelper.getUserVote();
        //side = chatMessageHelper.getSide();
        if (userVote != null) {
            if (userVote.equals("up")) {
                holder.bUpvote.setEnabled(false);
                holder.bDownvote.setEnabled(true);
            } else if (userVote.equals("down")) {
                holder.bDownvote.setEnabled(false);
                holder.bUpvote.setEnabled(true);
            }
        }
        else {
            holder.bUpvote.setEnabled(true);
            holder.bDownvote.setEnabled(true);
        }

        holder.bUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                itemClickCallback.onUpvoteClick(position);
                //MAKE boolean LOL
                String prev_voted = "false", vote = "up";
                //check if previously voted
                if (chatMessageHelper.getUserVote() != null) {
                    if (chatMessageHelper.getUserVote().equals("down")) {
                        prev_voted = "true";
                        itemClickCallback.removeDownvote(position);
                    }//remove previous downvote
                }

                chatMessageHelper.setUserVote(vote); //set user vote to up
                if(chatMessageHelper.getUserVote().equals(vote)){
                    holder.bUpvote.setEnabled(false);
                    holder.bDownvote.setEnabled(true);
                }else {
                    holder.bUpvote.setEnabled(true);
                    holder.bDownvote.setEnabled(true);
                }



                /*if(!holder.bDownvote.isEnabled()) {
                    holder.bDownvote.setEnabled(true);
                    //itemClickCallback.removeDownvote(getAdapterPosition());
                    prev_voted = "true";
                }*/

                //insert into user profile
                itemClickCallback.insertVote(position, prev_voted, vote);
            }
        });
        holder.bDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                itemClickCallback.onDownvoteClick(position);
                //MAKE boolean LOL
                String prev_voted = "false", vote = "down";
                //check if previously voted
                if (chatMessageHelper.getUserVote() != null) {
                    if (chatMessageHelper.getUserVote().equals("up")) {
                        prev_voted = "true";
                        itemClickCallback.removeUpvote(position);
                    }//remove previous downvote
                }

                chatMessageHelper.setUserVote(vote);
                if(chatMessageHelper.getUserVote().equals(vote)){
                    holder.bDownvote.setEnabled(false);
                    holder.bUpvote.setEnabled(true);
                }else {
                    holder.bUpvote.setEnabled(true);
                    holder.bDownvote.setEnabled(true);
                }
                /**if(!holder.bUpvote.isEnabled()) {
                 holder.bUpvote.setEnabled(true);
                 //itemClickCallback.removeUpvote(getAdapterPosition());
                 prev_voted = "true";
                 }**/

                //insert into user profile
                itemClickCallback.insertVote(position, prev_voted, vote);
            }
        });
    }



    class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView messageContentView, messageUserView, messageDateView, upVoteView, downVoteView;
        Button bUpvote, bDownvote;

        public ChatViewHolder(View itemView) {
            super(itemView);
            bUpvote = (Button) itemView.findViewById(R.id.upvote);

            bDownvote = (Button) itemView.findViewById(R.id.downvote);

            messageContentView = (TextView) itemView.findViewById(R.id.message_content_view);
            messageUserView = (TextView) itemView.findViewById(R.id.message_user_view);
            messageDateView = (TextView) itemView.findViewById(R.id.message_date_view);
            upVoteView = (TextView) itemView.findViewById(R.id.up_label);
            downVoteView = (TextView) itemView.findViewById(R.id.down_label);

        }


        private void bind(int listIndex){
            final ChatMessageHelper chatMessageHelper = (ChatMessageHelper) getItem(listIndex);
            messageContentView.setText(chatMessageHelper.getMessageText());
            messageUserView.setText(chatMessageHelper.getMessageUser());
            messageDateView.setText(chatMessageHelper.getMessageTime());
            upVoteView.setText(chatMessageHelper.getMessageUpvote().toString());
            downVoteView.setText(chatMessageHelper.getMessageDownvote().toString());
            String userVote = "";
            userVote = chatMessageHelper.getUserVote();
            Log.d("VOTESTATE", "text: " + userVote + chatMessageHelper.getMessageText() + " " + chatMessageHelper.getUserVote());
        }
/*
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.upvote){
                itemClickCallback.onUpvoteClick(getAdapterPosition());
                Log.d("VOTECLICK", "yes" );
                //MAKE boolean LOL
                String prev_voted = "false", vote = "up";;
                bUpvote.setEnabled(false);
                if(!bDownvote.isEnabled()) {
                    bDownvote.setEnabled(true);
                    itemClickCallback.removeDownvote(getAdapterPosition());
                    prev_voted = "true";
                }

                //insert into user profile
                itemClickCallback.insertVote(getAdapterPosition(), prev_voted, vote);
            }
            else if (v.getId() == R.id.downvote){
                itemClickCallback.onDownvoteClick(getAdapterPosition());
                //MAKE boolean LOL
                String prev_voted = "false", vote = "down";
                bDownvote.setEnabled(false);
                if(!bUpvote.isEnabled()) {
                    bUpvote.setEnabled(true);
                    itemClickCallback.removeUpvote(getAdapterPosition());
                    prev_voted = "true";
                }

                //insert into user profile
                itemClickCallback.insertVote(getAdapterPosition(), prev_voted, vote);
            }
        }*/

    }

}