package ingage.ingage.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ingage.ingage.App;
import ingage.ingage.R;
import ingage.ingage.activities.MainActivity;
import ingage.ingage.fragments.CategoriesFragment;

import java.util.Arrays;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private final List<String> mValues;
    private final CategoriesFragment.categoriesFragmentListener mListener;

    public CategoriesAdapter(CategoriesFragment.categoriesFragmentListener listener) {
        mValues = Arrays.asList(App.getAppContext().getResources().getStringArray(R.array.thread_categories));
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvCategory.setText(mValues.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onCategorySelected(mValues.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvCategory;
        public final ImageView ivCategory;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvCategory = (TextView) view.findViewById(R.id.tvCategory);
            ivCategory = (ImageView) view.findViewById(R.id.ivCategory);
        }

    }
}
