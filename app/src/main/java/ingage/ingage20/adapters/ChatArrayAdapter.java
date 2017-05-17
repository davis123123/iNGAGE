package ingage.ingage20.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ingage.ingage20.helpers.ChatMessageHelper;
import ingage.ingage20.R;


public class ChatArrayAdapter extends RecyclerView.Adapter<ChatArrayAdapter.ChatViewHolder>{

    private Context mContext;

    private static final String TAG = ThreadListAdapter.class.getSimpleName();
    List <ChatMessageHelper> list = new ArrayList<ChatMessageHelper>();

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }

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
        ChatViewHolder viewHolder = new ChatViewHolder(view);
        return viewHolder;
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
        holder.bind(position);
    }



    class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView messageContentView, messageUserView, messageDateView;

        public ChatViewHolder(View itemView) {
            super(itemView);
            messageContentView = (TextView) itemView.findViewById(R.id.message_content_view);
            messageUserView = (TextView) itemView.findViewById(R.id.message_user_view);
            messageDateView = (TextView) itemView.findViewById(R.id.message_date_view);

        }


        private void bind(int listIndex){
            ChatMessageHelper chatMessageHelper = (ChatMessageHelper) getItem(listIndex);
            messageContentView.setText(chatMessageHelper.getMessageText());
            messageUserView.setText(chatMessageHelper.getMessageUser());
            messageDateView.setText(chatMessageHelper.getMessageTime());
        }
    }
}