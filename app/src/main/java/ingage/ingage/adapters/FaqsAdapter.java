package ingage.ingage.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ingage.ingage.R;
import ingage.ingage.helpers.FaqsHelper;

/**
 * Created by wuv66 on 8/26/2018.
 */

public class FaqsAdapter extends RecyclerView.Adapter<FaqsAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<FaqsHelper> faqs = new ArrayList<>();

    public FaqsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faqs, parent, false);
        return new FaqsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.tvQuestion.setText(faqs.get(position).question);
        holder.tvAnswer.setText(faqs.get(position).answer);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.tvAnswer.getVisibility() == View.GONE){
                    holder.tvAnswer.setVisibility(View.VISIBLE);
                    holder.ivIcon.setImageResource(android.R.drawable.arrow_up_float);
                }
                else {
                    holder.tvAnswer.setVisibility(View.GONE);
                    holder.ivIcon.setImageResource(android.R.drawable.arrow_down_float);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return faqs.size();
    }

    public void add(FaqsHelper item){
        faqs.add(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvQuestion;
        public final TextView tvAnswer;
        public final ImageView ivIcon;

        public ViewHolder(View view) {
            super(view);
            tvQuestion = (TextView) view.findViewById(R.id.tvQuestion);
            tvAnswer = (TextView) view.findViewById(R.id.tvAnswer);
            ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
        }
    }
}
