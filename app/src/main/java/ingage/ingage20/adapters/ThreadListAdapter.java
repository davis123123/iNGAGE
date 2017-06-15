package ingage.ingage20.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
    ArrayList<Boolean> is_img_set= new ArrayList();



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

    /*public void onClick(View view) {
        if (view.getId() == R.id.thread_row_root){
            itemClickCallback.onContainerClick(getAdapterPosition());
        }
        if (view.getId() == R.id.spectateBtn){
            Log.d("CLICKSTATE", "specatebtn");
            itemClickCallback.onSpectateBtnClick(getAdapterPosition());
        }
        //int clickedPosition = getAdapterPosition();
        //mOnClickListener.onListItemClick(clickedPosition);
    }*/

    @Override
    public void onBindViewHolder(ThreadListAdapter.ThreadViewHolder holder, int position) {
        ThreadsHelper threadsHelper = (ThreadsHelper) this.getItem(position);
        holder.bind(position);
        final int pos = position;
        holder.container.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                itemClickCallback.onContainerClick(pos);
            }
        });
        holder.mSpectateBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                itemClickCallback.onSpectateBtnClick(pos);
            }
        });
        //holder.itemView.setOnClickListener();
        holder.threadTitleTextView.setText(threadsHelper.getThread_title());
        holder.threadByTextView.setText(threadsHelper.getThread_by());
        holder.threadCategoryTextView.setText(threadsHelper.getThread_category());
        if(threadsHelper.getThread_content() != null)
            //Log.d("STATE", "content: " + threadsHelper.getThread_content());
            holder.threadContentTextView.setText(threadsHelper.getThread_content());

        //holder.threadImageView = (ImageView) itemView.findViewById(R.id.img_post);

        //BitmapDrawable drawable = (BitmapDrawable)holder.threadImageView.getDrawable();
        //Bitmap bitmap = drawable.getBitmap();
        downloadImage(threadsHelper, pos, holder);
    }

    //retrieve Base64 from FireBase and convert to image
    private void downloadImage(ThreadsHelper threadsHelper, int pos, ThreadListAdapter.ThreadViewHolder holder){
        Context context = holder.itemView.getContext();
        DownloadImageHandler dlHandler = new DownloadImageHandler(context);
        String type = "download";

        String thread_id = threadsHelper.getThread_id();

        //do conversion
        try {
            //threadImageView = (ImageView) itemView.findViewById(R.id.img_post);
            String result = dlHandler.execute(type, thread_id).get();


            if(result.substring(0,4).equals("data")) {
                is_img_set.set(pos, true);

                    /*int index =result.indexOf(",") + 1;
                    String code = result.substring(index, result.length());
                    byte[] decodedString = Base64.decode(code, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    threadImageView.setImageBitmap(decodedByte);
                    LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 1000);
                    threadImageView.setLayoutParams(img_params);
                    threadContentTextView.setText(" ");*/
            }
            else {
                is_img_set.set(pos, false);
                //threadImageView.setImageBitmap(null);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //set padding programmatically
        if(holder.threadImageView.getDrawable() != null) {
            float density = context.getResources().getDisplayMetrics().density;
            int padding = (int)(20 * density);
            holder.threadImageView.setPadding(padding, padding, padding, padding);
        }

        for(int i=0; i < is_img_set.size(); i++){
            context = holder.itemView.getContext();
            dlHandler = new DownloadImageHandler(context);
            type = "download";

            thread_id = threadsHelper.getThread_id();
            String result = null;
            Log.d("STATE", "content: " + threadsHelper.getThread_title());
            Log.d("STATE", "has img: " + is_img_set.get(i));
            try {
                result = dlHandler.execute(type, thread_id).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(is_img_set.get(i)){
                int index = result.indexOf(",") + 1;
                String code = result.substring(index, result.length());
                byte[] decodedString = Base64.decode(code, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.threadImageView.setImageBitmap(decodedByte);
                LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 1000);
                holder.threadImageView.setLayoutParams(img_params);
                holder.threadContentTextView.setText(" ");
            }
            else if (!is_img_set.get(i))
                holder.threadImageView.setImageBitmap(null);
        }
    }

    class ThreadViewHolder extends RecyclerView.ViewHolder{

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
            threadImageView = (ImageView) itemView.findViewById(R.id.img_post);

            container = itemView.findViewById(R.id.thread_row_root);

            mSpectateBtn = (Button) itemView.findViewById(R.id.spectateBtn);


            //testing setting a drawable programatically
            /*if(threadImageView != null)
                threadImageView.setImageResource(R.drawable.logo);
            else
                Log.d("STATE", "img is null");
*/

        }


        private void bind(int listIndex){
            //Log.d("STATE", "room title: " + threadsHelper.getThread_title());
            //Log.d("STATE", "thread helper img: "+ str + ",length: " + str.length());
            //if(!threadsHelper.getThread_img().equalsIgnoreCase("") && str.length() != 0) {
                //Log.d("STATE", "call download...");


        }
    }
}
