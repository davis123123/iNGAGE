package ingage.ingage20.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ingage.ingage20.R;
import ingage.ingage20.helpers.ChatMessageHelper;
import ingage.ingage20.managers.SessionManager;


public class ChatArrayAdapter extends RecyclerView.Adapter<ChatArrayAdapter.ChatViewHolder>{

    private Context mContext;
    private static final String TAG = ChatArrayAdapter.class.getSimpleName();
    List <ChatMessageHelper> list = new ArrayList<ChatMessageHelper>();
    HashMap<String, Integer> chatHash = new HashMap<String, Integer>();
    SessionManager session;
    String username;

    //placement method
    boolean voteBind = false;

    private ItemClickCallback itemClickCallback;
    String side;

    public interface ItemClickCallback{
        void onUpvoteClick(int p);
        void onDownvoteClick(int p);
        void removeUpvote(int p);
        void removeDownvote(int p);
        //inserts vote into userprofile
        void insertVote(int p, String prev_voted, String vote);

        void onAvatarClick(int p);

    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }


    public ChatArrayAdapter(ItemClickCallback listener){
        itemClickCallback = listener;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Get the RecyclerView item layout
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(R.layout.chat_layout, viewGroup, shouldAttachToParentImmediately);
        Log.d("STATE", "viewType: " + viewType);

        session = new SessionManager(viewGroup.getContext());
        HashMap<String, String> user = session.getUserDetails();
        username = user.get(SessionManager.KEY_NAME);

        if(viewType == 0)
            view = inflater.inflate(R.layout.chat_layout_own, viewGroup, shouldAttachToParentImmediately);
        else if(viewType == 1)
            view = inflater.inflate(R.layout.chat_layout, viewGroup, shouldAttachToParentImmediately);
        if(viewType == 2)
            view = inflater.inflate(R.layout.chat_layout_own_right, viewGroup, shouldAttachToParentImmediately);
        else if(viewType == 3)
            view = inflater.inflate(R.layout.chat_layout_right, viewGroup, shouldAttachToParentImmediately);

        ChatViewHolder viewHolder = new ChatViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        //final Object dataObj = list.get(position);
        ChatMessageHelper chatMessageHelper = (ChatMessageHelper) getItem(position);
        String name = chatMessageHelper.getMessageUser();

        if (list.get(position).getSide().equals("agree")) {
            if(name.equals(username))
                return 0;
            else
                return 1;
        }

        else if (list.get(position).getSide().equals("disagree")) {
            if(name.equals(username))
                return 2;
            else
                return 3;
        }

        return -1;
    }


    public void add(ChatMessageHelper object){
        list.add(object);

        //hash msgs
        String chat_id = object.getMessageID();
        chatHash.put(chat_id, getItemCount() - 1);
    }

    public void update(ChatMessageHelper newObject, String chat_id, boolean updateBind){
        //get old chatmsg
        int position = chatHash.get(chat_id);
        Log.d("UPvote state: ", " upvote called");
        //update oldmsg with newobject
        ChatMessageHelper chatMessageHelper = (ChatMessageHelper) getItem(position);
        chatMessageHelper.setMessageDownvote(newObject.getMessageDownvote());
        chatMessageHelper.setMessageUpvote(newObject.getMessageUpvote());
        chatMessageHelper.setMessageText(newObject.getMessageText());
        chatMessageHelper.setMessageTime(newObject.getMessageTime());
        voteBind = updateBind;
        notifyDataSetChanged();
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


        if(holder.avatar.getDrawable() == null) {
            Log.d("Upvote state: ", " no avatar");
            holder.downloadAvatar();
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

                //insert into user profile
                itemClickCallback.insertVote(position, prev_voted, vote);
            }
        });

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickCallback.onAvatarClick(position);
            }
        });
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView messageContentView, messageUserView, upVoteView, downVoteView, messageTime;
        ImageView avatar;
        ImageButton bUpvote, bDownvote;

        public ChatViewHolder(View itemView) {
            super(itemView);

            bUpvote = (ImageButton) itemView.findViewById(R.id.upvote);

            bDownvote = (ImageButton) itemView.findViewById(R.id.downvote);

            messageContentView = (TextView) itemView.findViewById(R.id.message_content_view);
            messageUserView = (TextView) itemView.findViewById(R.id.message_user_view);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);

            avatar = (ImageView ) itemView.findViewById(R.id.avatar);

            upVoteView = (TextView) itemView.findViewById(R.id.up_label);
            downVoteView = (TextView) itemView.findViewById(R.id.down_label);
        }

        private void bind(int listIndex){
            final ChatMessageHelper chatMessageHelper = (ChatMessageHelper) getItem(listIndex);
            messageContentView.setText(chatMessageHelper.getMessageText());
            messageUserView.setText(chatMessageHelper.getMessageUser());
            upVoteView.setText(chatMessageHelper.getMessageUpvote().toString());
            downVoteView.setText(chatMessageHelper.getMessageDownvote().toString());
            messageTime.setText(chatMessageHelper.getMessageTime().toString());
            if(!voteBind)
                avatar.setImageDrawable (null);
            Log.d("UPvote state: ", " "+ voteBind);
            String userVote = "";
            userVote = chatMessageHelper.getUserVote();
            Log.d("VOTESTATE", "text: " + userVote + chatMessageHelper.getMessageText() + " " + chatMessageHelper.getUserVote());
        }

        private void downloadAvatar(){
            final String url = "http://107.170.232.60/avatars/" + messageUserView.getText() + ".JPG";

            final Context context = itemView.getContext();

            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int screenHeight = metrics.heightPixels;
            int screenWidth = metrics.widthPixels;
            final int imgHeight = (int) (screenHeight * 0.2);
            final int imgWidth = imgHeight;


            Picasso.with(context)
                    .load(url)
                    .resize(imgWidth, imgHeight)
                    .onlyScaleDown()
                    .noPlaceholder()
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(avatar, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            //If cache fails, try to fetch from url
                            Picasso.with(context)
                                    .load(url)
                                    .resize(imgWidth, imgHeight)
                                    .onlyScaleDown()
                                    .noPlaceholder()
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    //.error(R.drawable.header)
                                    .into(avatar, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Log.e("Picasso","Could not get image");
                                            avatar.setImageResource(R.mipmap.user);
                                        }
                                    });
                        }
                    });
        }

    }
}