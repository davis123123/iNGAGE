package ingage.ingage20.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.handlers.DownloadImageHandler;
import ingage.ingage20.helpers.ThreadsHelper;

/**
 * Created by Davis on 4/4/2017.
 */

public class ThreadListAdapter extends RecyclerView.Adapter<ThreadListAdapter.ThreadViewHolder> {

    private Context mContext;
    private static final String TAG = ThreadListAdapter.class.getSimpleName();
    List list = new ArrayList();
    private ItemClickCallback itemClickCallback;

    //Result returned from backend if no image exists
    String default_path = "data:image/JPG;base64,";


    public interface ItemClickCallback{
        void onContainerClick(int p);
        void onSpectateBtnClick(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }


    public ThreadListAdapter( ItemClickCallback listener){
        itemClickCallback = listener;
    }//interface for thread-click

    @Override
    public ThreadViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Get the RecyclerView item layout
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(R.layout.thread_row_layout, viewGroup, shouldAttachToParentImmediately);
        ThreadViewHolder viewHolder = new ThreadViewHolder(view);
        return viewHolder;
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

    @Override
    public void onBindViewHolder(ThreadListAdapter.ThreadViewHolder holder, int position) {
        ThreadsHelper threadsHelper = (ThreadsHelper) this.getItem(position);
        holder.bind(position);
    }



    class ThreadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView threadTitleTextView, threadByTextView, threadCategoryTextView, threadContentTextView ;
        ImageView threadImageView;
        View container;
        Button mSpectateBtn;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews
         *
         * @param itemView The View that you inflated in
         *
         */
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
            if(threadsHelper.getThread_content() != null)
                //Log.d("STATE", "content: " + threadsHelper.getThread_content());
                threadContentTextView.setText(threadsHelper.getThread_content());

            threadImageView = (ImageView) itemView.findViewById(R.id.img_post);

            String str = threadsHelper.getThread_img();
            //Log.d("STATE", "room title: " + threadsHelper.getThread_title());
            //Log.d("STATE", "thread helper img: "+ str + ",length: " + str.length());
            //if(!threadsHelper.getThread_img().equalsIgnoreCase("") && str.length() != 0) {
                //Log.d("STATE", "call download...");

            downloadImage(threadsHelper);

        }

        //retrieve Base64 from FireBase and convert to image
        private void downloadImage(ThreadsHelper threadsHelper){
            Context context = itemView.getContext();
            DownloadImageHandler dlHandler = new DownloadImageHandler(context);
            String type = "download";

            String thread_id = threadsHelper.getThread_id();

            //do conversion
            try {
                threadImageView = (ImageView) itemView.findViewById(R.id.img_post);
                String result = dlHandler.execute(type, thread_id).get();
                //Log.d("STATE", "room title: " + threadsHelper.getThread_title());
                //Log.d("STATE", "download result: " + result);
                if(result.length() > default_path.length()) {
                    int index =result.indexOf(",") + 1;
                    String code = result.substring(index, result.length());
                    byte[] decodedString = Base64.decode(code, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    threadImageView.setImageBitmap(decodedByte);
                    LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 1000);
                    threadImageView.setLayoutParams(img_params);
                    threadContentTextView.setText(" ");
                }
                else
                    threadImageView.setImageBitmap(null);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            //set padding programmatically
            if(threadImageView.getDrawable() != null) {
                float density = context.getResources().getDisplayMetrics().density;
                int padding = (int)(20 * density);
                threadImageView.setPadding(padding, padding, padding, padding);
            }
        }

    }
}
