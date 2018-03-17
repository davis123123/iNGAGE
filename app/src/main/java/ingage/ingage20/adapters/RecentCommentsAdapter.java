package ingage.ingage20.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ingage.ingage20.R;
import ingage.ingage20.activities.UserProfileActivity;
import ingage.ingage20.util.RecentComment;

/**
 * Created by wuv66 on 2/11/2018.
 */

public class RecentCommentsAdapter extends RecyclerView.Adapter<RecentCommentsAdapter.RecentCommentsHolder>{

    List list = new ArrayList();
    Context mContext;

    public class RecentCommentsHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvRecentComment, tvCategory;
        public ImageView ivImage;

        public RecentCommentsHolder(View view) {
            super(view);

            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvRecentComment = (TextView) view.findViewById(R.id.tvRecentComment);
            tvCategory = (TextView) view.findViewById(R.id.tvCategory);
            ivImage = (ImageView) view.findViewById(R.id.ivImage);


        }

        private void getImage(RecentComment helper){
            final String url = "http://107.170.232.60/images/" + helper.thread_id + ".JPG";

            Picasso.with(mContext)
                    .load(url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .resize(450, 400)
                    .into(ivImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            //If cache fails, try to fetch from url
                            Picasso.with(mContext)
                                    .load(url)
                                    .resize(450, 400)
                                    //.error(R.drawable.header)
                                    .into(ivImage, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError() {
                                            //ivImage.setVisibility(View.GONE);
                                            Log.e("Picasso","Could not get image");
                                        }
                                    });
                        }
                    });
        }
    }


    public RecentCommentsAdapter(Context context) {
        mContext = context;
    }

    public void add(RecentComment item){
        list.add(item);
    }

    @Override
    public RecentCommentsAdapter.RecentCommentsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_comments_rv, parent, false);

        return new RecentCommentsAdapter.RecentCommentsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecentCommentsAdapter.RecentCommentsHolder holder, int position) {
        String comment = "\"" + UserProfileActivity.recentComments.get(position).recent_comment + "\"";
        holder.tvTitle.setText(UserProfileActivity.recentComments.get(position).thread_title);
        holder.tvRecentComment.setText(comment);
        holder.tvCategory.setText(UserProfileActivity.recentComments.get(position).thread_category);
        holder.getImage(UserProfileActivity.recentComments.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



}
