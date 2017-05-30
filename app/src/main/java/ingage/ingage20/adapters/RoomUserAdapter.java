package ingage.ingage20.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ingage.ingage20.R;
import ingage.ingage20.helpers.ChatRoomUserHelper;
import ingage.ingage20.helpers.ThreadsHelper;

/**
 * Created by Davis on 5/29/2017.
 */

public class RoomUserAdapter extends RecyclerView.Adapter<RoomUserAdapter.RoomUserViewHolder> {
    private Context mContext;

    private static final String TAG = RoomUserAdapter.class.getSimpleName();
    List list = new ArrayList();
    private static RoomUserAdapter.ListItemClickListener mOnClickListener;

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }
    public RoomUserAdapter(RoomUserAdapter.ListItemClickListener listener){
        mOnClickListener = listener;
    }

    @Override
    public RoomUserViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(R.layout.room_user_row_layout, viewGroup, shouldAttachToParentImmediately);
        RoomUserAdapter.RoomUserViewHolder viewHolder = new RoomUserAdapter.RoomUserViewHolder(view);
        return viewHolder;
}

    @Override
    public void onBindViewHolder(RoomUserViewHolder holder, int position) {
        ChatRoomUserHelper chatRoomUserHelper = (ChatRoomUserHelper) this.getItem(position);
        holder.bind(position);
    }

    public void add(ChatRoomUserHelper object){
        list.add(object);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Object getItem(int position){
        return list.get(position);
    }

    public class RoomUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView usernameTextView;

        public RoomUserViewHolder(View itemView) {
            super(itemView);
            usernameTextView = (TextView) itemView.findViewById(R.id.username_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }

        private void bind(int listIndex){
            ChatRoomUserHelper chatRoomUserHelper = (ChatRoomUserHelper) getItem(listIndex);
            usernameTextView.setText(chatRoomUserHelper.getUsername());
        }
    }
}
