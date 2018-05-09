package ingage.ingage.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ingage.ingage.R;

/**
 * Created by wuv66 on 7/2/2017.
 */

public class UserProfileInfoAdapter extends RecyclerView.Adapter<UserProfileInfoAdapter.UserProfileInfoHolder> {

        List list = new ArrayList();

    public class UserProfileInfoHolder extends RecyclerView.ViewHolder {
        public TextView content;
        public ImageView icon;

        public UserProfileInfoHolder(View view) {
            super(view);
            content = (TextView) view.findViewById(R.id.user_profile_content);
            icon = (ImageView) view.findViewById(R.id.user_profile_icon);
        }
    }


    public UserProfileInfoAdapter() {

    }

    public void add(String content){
        list.add(content);
    }

    @Override
    public UserProfileInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_profile_lv, parent, false);

        return new UserProfileInfoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserProfileInfoHolder holder, int position) {
        String field = (String) list.get(position);
        holder.content.setText(field);
        if(position == 1)
            holder.icon.setImageResource(R.drawable.ic_menu_send);
        if (position == 2)
            holder.icon.setImageResource(R.drawable.ic_menu_slideshow);
        if (position == 3)
            holder.icon.setImageResource(R.drawable.ic_menu_share);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

