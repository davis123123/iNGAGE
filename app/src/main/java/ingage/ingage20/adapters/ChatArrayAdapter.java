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
import java.util.List;

import ingage.ingage20.R;
import ingage.ingage20.helpers.ChatMessageHelper;


public class ChatArrayAdapter extends RecyclerView.Adapter<ChatArrayAdapter.ChatViewHolder>{

    private Context mContext;

    private ItemClickCallback itemClickCallback;
    String side;

    public interface ItemClickCallback{
        void onUpvoteClick(int p);
        void onDownvoteClick(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }

    private static final String TAG = ThreadListAdapter.class.getSimpleName();
    List <ChatMessageHelper> list = new ArrayList<ChatMessageHelper>();

    public ChatArrayAdapter( ){

        //mOnClickListener = listener;

    }//interface for thread-click

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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Object getItem(int position){
        return list.get(position);
    }

    @Override
    public void onBindViewHolder(ChatArrayAdapter.ChatViewHolder holder, int position) {
        ChatMessageHelper chatMessageHelper = (ChatMessageHelper) this.getItem(position);
        //side = chatMessageHelper.getSide();
        holder.bind(position);
    }



    class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView messageContentView, messageUserView, messageDateView;
        Button bUpvote, bDownvote;

        public ChatViewHolder(View itemView) {
            super(itemView);
            bUpvote = (Button) itemView.findViewById(R.id.upvote);
            bUpvote.setOnClickListener(this);
            bDownvote = (Button) itemView.findViewById(R.id.downvote);
            bDownvote.setOnClickListener(this);
            messageContentView = (TextView) itemView.findViewById(R.id.message_content_view);
            messageUserView = (TextView) itemView.findViewById(R.id.message_user_view);
            messageDateView = (TextView) itemView.findViewById(R.id.message_date_view);

        }


        private void bind(int listIndex){
            final ChatMessageHelper chatMessageHelper = (ChatMessageHelper) getItem(listIndex);
            messageContentView.setText(chatMessageHelper.getMessageText());
            messageUserView.setText(chatMessageHelper.getMessageUser());
            messageDateView.setText(chatMessageHelper.getMessageTime());
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.upvote){
                itemClickCallback.onUpvoteClick(getAdapterPosition());
            }
            if (v.getId() == R.id.downvote){
                itemClickCallback.onDownvoteClick(getAdapterPosition());
            }
        }

    }

}