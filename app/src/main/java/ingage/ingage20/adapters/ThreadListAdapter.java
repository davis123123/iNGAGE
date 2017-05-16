package ingage.ingage20.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ingage.ingage20.Helpers.ThreadsHelper;
import ingage.ingage20.R;

/**
 * Created by Davis on 4/4/2017.
 */

public class ThreadListAdapter extends RecyclerView.Adapter<ThreadListAdapter.ThreadViewHolder> {

    private Context mContext;

    private static final String TAG = ThreadListAdapter.class.getSimpleName();
    List list = new ArrayList();
    private static ListItemClickListener mOnClickListener;




    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }

    public ThreadListAdapter( ListItemClickListener listener){
        mOnClickListener = listener;
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

        TextView threadTitleTextView, threadByTextView, threadCategoryTextView;

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

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }

        private void bind(int listIndex){
            ThreadsHelper threadsHelper = (ThreadsHelper) getItem(listIndex);
            threadTitleTextView.setText(threadsHelper.getThread_title());
            threadByTextView.setText(threadsHelper.getThread_by());
            threadCategoryTextView.setText(threadsHelper.getThread_category());
        }
    }
}
