package ingage.ingage.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ingage.ingage.helpers.ThreadsHelper;
import ingage.ingage.R;

/**
 * Created by Davis on 4/17/2017.
 */

public class Testadapter extends ArrayAdapter {
    private Context mContext;

    private static final String TAG = ThreadListAdapter.class.getSimpleName();
    private List list = new ArrayList();

    public Testadapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(ThreadsHelper object){
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public Object getItem(int position){
        return list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        View row;
        row = convertView;
        ThreadHolder threadHolder;
        if(row == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.thread_row_layout,parent,false);
            threadHolder = new ThreadHolder();
            threadHolder.thread_title = (TextView) row.findViewById(R.id.thread_title_view);
            row.setTag(threadHolder);
        }

        else {
            threadHolder = (ThreadHolder) row.getTag();
        }

        ThreadsHelper threadsHelper = (ThreadsHelper) this.getItem(position);
        assert threadsHelper != null;
        threadHolder.thread_title.setText(threadsHelper.getThread_title());

        return row;
    }


    private static class ThreadHolder{
        TextView thread_title;
    }
}
