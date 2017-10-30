package ingage.ingage20.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ingage.ingage20.R;
import ingage.ingage20.handlers.DownloadImageHandler;
import ingage.ingage20.helpers.ThreadsHelper;

/**
 * Created by Davis on 4/4/2017.
 */

public class ThreadListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private static final String TAG = ThreadListAdapter.class.getSimpleName();
    public List list = new ArrayList();
    public static boolean isLoading = false;
    private ItemClickCallback itemClickCallback;

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

    public void setItemClickCallback(final ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public ThreadListAdapter( ItemClickCallback listener){
        itemClickCallback = listener;
    }//interface for thread-click

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Get the RecyclerView item layout
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        Log.d("VIEWTYPE",":"+viewType);
        boolean shouldAttachToParentImmediately = false;
        if (viewType == VIEW_TYPE_ITEM) {
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

    public void setLoaded() {
        isLoading = false;
    }

    @Override public int getItemViewType(int position) {
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

        TextView threadTitleTextView, threadByTextView, threadCategoryTextView, threadContentTextView ;
        ImageView threadImageView;
        View container;
        Button mSpectateBtn;

        public ThreadViewHolder(View itemView) {
            super(itemView);
            threadTitleTextView = (TextView) itemView.findViewById(R.id.thread_title_view);
            threadByTextView = (TextView) itemView.findViewById(R.id.thread_by_view);
            threadCategoryTextView = (TextView) itemView.findViewById(R.id.thread_category_view);
            threadImageView = (ImageView) itemView.findViewById(R.id.img_post);
            threadContentTextView = (TextView) itemView.findViewById(R.id.thread_content);

            container = itemView.findViewById(R.id.thread_row_root);
            container.setOnClickListener(this);

            mSpectateBtn = (Button) itemView.findViewById(R.id.spectateBtn);
            mSpectateBtn.setOnClickListener(this);

            //testing setting a drawable programatically
            /*if(threadImageView != null)
                threadImageView.setImageResource(R.drawable.logo);
            else
                Log.d("STATE", "img is null");
*/
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.thread_row_root){
                itemClickCallback.onContainerClick(getAdapterPosition());
            }
            if (view.getId() == R.id.spectateBtn){
                Log.d("CLICKSTATE", "specatebtn");
                itemClickCallback.onSpectateBtnClick(getAdapterPosition());
            }
            //int clickedPosition = getAdapterPosition();
            //mOnClickListener.onListItemClick(clickedPosition);
        }

        private void bind(int listIndex){
            ThreadsHelper threadsHelper = (ThreadsHelper) getItem(listIndex);
            threadTitleTextView.setText(threadsHelper.getThread_title());
            threadByTextView.setText(threadsHelper.getThread_by());
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

                RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 10);
                params.addRule(RelativeLayout.BELOW, R.id.thread_title_view);
                int margin = convertToDP(itemView.getContext(), 20);
                params.setMargins(margin, 0, margin, 0);
                threadContentTextView.setLayoutParams(params);
            }


        }

        //retrieve Base64 from FireBase and convert to image
        private void getImage(ThreadsHelper threadsHelper){
            Context context = itemView.getContext();
            DownloadImageHandler dlHandler = new DownloadImageHandler(context);
            String type = "download";

            String thread_id = threadsHelper.getThread_id();

            //do conversion

            threadImageView = (ImageView) itemView.findViewById(R.id.img_post);
            String result = threadsHelper.getThread_img_bitmap();
            //Log.d("STATE", "room title: " + threadsHelper.getThread_title());
            Log.d("STATE", "download thread img result: " + result);
            if(result != null && result.length() > default_path.length()) {
                int index =result.indexOf(",") + 1;
                String code = result.substring(index, result.length());
                byte[] decodedString = Base64.decode(code, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                threadImageView.setImageBitmap(decodedByte);

                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                int screenHeight = metrics.heightPixels;
                int imgHeight = (int) (screenHeight * 0.4);
                Log.v("STATE", "Screenheight: " + screenHeight + ", imgHeight: " + imgHeight);

                LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, imgHeight);
                threadImageView.setLayoutParams(img_params);
                threadContentTextView.setText(" ");
            }
            else
                threadImageView.setImageBitmap(null);


            //set padding programmatically
            if(threadImageView.getDrawable() != null) {
                //float density = context.getResources().getDisplayMetrics().density;
                //int padding = (int)(20 * density);
                int padding = convertToDP(context, 20);
                threadImageView.setPadding(padding, padding, padding, padding);
            }
        }

        public int convertToDP(Context context, int dip){
            float density = context.getResources().getDisplayMetrics().density;
            int result = (int)(dip * density);
            return result;
        }

    }
}
