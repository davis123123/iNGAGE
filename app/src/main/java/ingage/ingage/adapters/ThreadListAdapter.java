package ingage.ingage.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ingage.ingage.R;
import ingage.ingage.handlers.DownloadImageHandler;
import ingage.ingage.helpers.ThreadsHelper;
import ingage.ingage.util.CustomRunnable;

/**
 * Created by Davis on 4/4/2017.
 */

public class ThreadListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private static final String TAG = ThreadListAdapter.class.getSimpleName();
    public List list = new ArrayList();
    public static boolean isLoading = false;
    private ItemClickCallback itemClickCallback;
    private Handler handler = new Handler();
    private boolean thread_active = false;

    //Result returned from backend if no image exists
    String default_path = "data:image/JPG;base64,";
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public OnLoadMoreListener mOnLoadMoreListener;
    public interface ItemClickCallback{
        void onContainerClick(int p);
        void onSpectateBtnClick(int p);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public ThreadListAdapter( ItemClickCallback listener, Activity activity, boolean thread_active){
        this.thread_active = thread_active;
        itemClickCallback = listener;
        mActivity = activity;
    }//interface for thread-click

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Get the RecyclerView item layout
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        Log.d("VIEWTYPE",":"+viewType);
        boolean shouldAttachToParentImmediately = false;
        if (viewType == VIEW_TYPE_ITEM) {
            Log.d("LOAD","LayoutNNN");
            View view = inflater.inflate(R.layout.thread_row_layout, viewGroup, shouldAttachToParentImmediately);
            ThreadViewHolder viewHolder = new ThreadViewHolder(view);
            return viewHolder;
        } else if (viewType == VIEW_TYPE_LOADING) {
            Log.d("LOAD","Layout");
            View view = inflater.inflate(R.layout.layout_loading_item, viewGroup, shouldAttachToParentImmediately);
            //ThreadViewHolder viewHolder = new ThreadViewHolder(view);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ThreadViewHolder) {
            ThreadsHelper threadsHelper = (ThreadsHelper) this.getItem(position);
            String containImg = threadsHelper.getThread_img();
            //Log.i("STATE","onbindviewholder str: " + containImg);
            //Check if view holder contains an image
            if(containImg.trim().length() == 0) {
                ((ThreadViewHolder) holder).threadImageView.setVisibility(View.GONE);
            }
            else{
                ((ThreadViewHolder) holder).threadImageView.setVisibility(View.VISIBLE);
                //((ThreadViewHolder) holder).threadContentTextView.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, R.id.thread_title_view);
                params.setMargins(0, 0, 0, 0);
                ((ThreadViewHolder) holder).threadImageView.setLayoutParams(params);
            }
            //holder.bind(position);
            ThreadViewHolder threadViewHolder = (ThreadViewHolder) holder;
            threadViewHolder.bind(position);
        }
        else if(holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    public void add(ThreadsHelper object){
        list.add(object);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Object getItem(int position){
        return list.get(position);
    }

    public void setLoaded(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public boolean getLoadStat(){
        return isLoading;
    }

    private void updateTimer(ThreadsHelper newObject){

    }

    @Override public int getItemViewType(int position) {
        Log.d("LOADER", "isLoading");
        return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    class ThreadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView threadTitleTextView, threadDurationTextView, threadCategoryTextView, threadContentTextView ;
        ImageView threadImageView;
        View container;
        Button mSpectateBtn;
        CustomRunnable customRunnable;

        public ThreadViewHolder(View itemView) {
            super(itemView);
            threadTitleTextView = (TextView) itemView.findViewById(R.id.thread_title_view);
            threadDurationTextView = (TextView) itemView.findViewById(R.id.thread_duration);
            threadCategoryTextView = (TextView) itemView.findViewById(R.id.thread_category_view);
            threadImageView = (ImageView) itemView.findViewById(R.id.img_post);
            threadContentTextView = (TextView) itemView.findViewById(R.id.thread_content);
            container = itemView.findViewById(R.id.thread_row_root);
            container.setOnClickListener(this);

            mSpectateBtn = (Button) itemView.findViewById(R.id.spectateBtn);
            mSpectateBtn.setOnClickListener(this);
            customRunnable = new CustomRunnable(handler,threadDurationTextView,5000);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.thread_row_root){ itemClickCallback.onContainerClick(getAdapterPosition()); }

            if (view.getId() == R.id.spectateBtn){
                Log.d("CLICKSTATE", "specatebtn");
                itemClickCallback.onSpectateBtnClick(getAdapterPosition());
            }
        }

        private void getImage(ThreadsHelper helper){
            final String url = "http://107.170.232.60/images/" + helper.getThread_id() + ".JPG";
            threadImageView = (ImageView) itemView.findViewById(R.id.img_post);

            Context context = itemView.getContext();

            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int screenHeight = metrics.heightPixels;
            int screenWidth = metrics.widthPixels;
            final int imgHeight = (int) (screenHeight * 0.4);
            final int imgWidth = (int) (screenWidth * 1);

            LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, imgHeight);
            img_params.setMargins(0,0,0, 20);
            threadImageView.setLayoutParams(img_params);
            threadContentTextView.setText(" ");

            Picasso.with(mActivity)
                    .load(url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .resize(imgWidth, imgHeight)
                    .onlyScaleDown()
                    .into(threadImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(){
                            //If cache fails, try to fetch from url
                            Picasso.with(mActivity)
                                    .load(url)
                                    .resize(imgWidth, imgHeight)
                                    .onlyScaleDown()
                                    //.error(R.drawable.header)
                                    .into(threadImageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError() {
                                            Log.e("Picasso","Could not get image");
                                        }
                                    });
                        }
                    });
        }

        private void bind(int listIndex){
            ThreadsHelper threadsHelper = (ThreadsHelper) getItem(listIndex);
            threadTitleTextView.setText(threadsHelper.getThread_title());

            if(threadsHelper.getThread_duration() < 0){
                threadDurationTextView.setText(threadsHelper.getThread_by());
            }//if archived

            else if (threadsHelper.getThread_duration() > 0){

                handler.removeCallbacks(customRunnable);
                customRunnable.holder = threadDurationTextView;
                customRunnable.millisUntilFinished = threadsHelper.getThread_duration();
                handler.postDelayed(customRunnable, 100);
            }//if active

            else{
                threadDurationTextView.setText("ENDED");
            }//if active and timer reached 0

            //threadDurationTextView.setText(threadsHelper.getThread_duration());
            threadCategoryTextView.setText(threadsHelper.getThread_category());
            threadContentTextView.setVisibility(View.INVISIBLE);
            threadImageView = (ImageView) itemView.findViewById(R.id.img_post);
            String str = threadsHelper.getThread_img();
            if(str != null) {
                getImage(threadsHelper);
            }
            //If there's no image
            if(str.trim().length() == 0) {
                //Log.d("STATE", "content: " + threadsHelper.getThread_content());
                RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, R.id.thread_title_view);
                int margin = convertToDP(itemView.getContext(), 20);
                params.setMargins(margin, 0, margin, margin);
                threadContentTextView.setLayoutParams(params);
                threadContentTextView.setVisibility(View.VISIBLE);
                threadContentTextView.setText(threadsHelper.getThread_content());
            }
            //If contains image
            else{
                threadContentTextView.setVisibility(View.INVISIBLE);
                RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, R.id.thread_title_view);
                int margin = convertToDP(itemView.getContext(), 20);
                params.setMargins(margin, 0, margin, 0);
                threadContentTextView.setLayoutParams(params);
                LinearLayout.LayoutParams spectateParams= (LinearLayout.LayoutParams) mSpectateBtn.getLayoutParams();
                //spectateParams.addRule(RelativeLayout.BELOW, R.id.img);
                mSpectateBtn.setLayoutParams(spectateParams);
            }
        }

        public int convertToDP(Context context, int dip){
            float density = context.getResources().getDisplayMetrics().density;
            int result = (int)(dip * density);
            return result;
        }
    }
}
