package ingage.ingage.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ingage.ingage.R;


/**
 * Created by wuv66 on 5/20/2018.
 */

public class WalkthroughAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    private String header, description;
    ArrayList<WalkthroughHelper> walkthroughPages = new ArrayList<>();;
    List<String> headers;
    List<String> descriptions;
    int position;

    TextView tvHeader;
    TextView tvDescription;
    ImageView img;
    Button btn;


    public static class WalkthroughHelper{
        public String header;
        public String description;

        public void setHeader(String header){
            this.header = header;
        }

        public void setDescription(String description){
            this.description = description;
        }

        public String getHeader(){
            return header;
        }

        public String getDescription(){
            return description;
        }
    }


    public WalkthroughAdapter(Context context) {
        context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        headers = Arrays.asList(context.getResources().getStringArray(R.array.walkthrough_headers));
        descriptions = Arrays.asList(context.getResources().getStringArray(R.array.walkthrough_descriptions));

        WalkthroughAdapter.WalkthroughHelper item;

        for(int i = 0; i < headers.size(); i++){
            item = new WalkthroughAdapter.WalkthroughHelper();
            item.setHeader(headers.get(i));
            item.setDescription(descriptions.get(i));
            walkthroughPages.add(item);
        }

    }

    @Override
    public int getCount() {
        return walkthroughPages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.item_fragment_walkthrough, container, false);


        header = walkthroughPages.get(position).getHeader();
        description = walkthroughPages.get(position).getDescription();

        tvHeader = (TextView) itemView.findViewById(R.id.tvHeader);
        tvHeader.setText(header);

        tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
        tvDescription.setText(description);
        container.addView(itemView);

        img = (ImageView) itemView.findViewById(R.id.ivImg);
        setImage(position);

        btn = (Button) itemView.findViewById(R.id.btClose);
        if(position == getCount() - 1)
            btn.setText(R.string.walkthough_done);

        return itemView;
    }

    public boolean setImage(int position){

        switch (position) {
            case 0:
                img.setImageResource(R.drawable.walkthrough_get_started);
                return true;

            case 1:
                img.setImageResource(R.drawable.walkthrough_how_it_works);
                return true;
            case 2:
                img.setImageResource(R.drawable.walkthrough_discussion);
                return true;
            case 3:
                img.setImageResource(R.drawable.walkthrough_spectate);
                return true;
            case 4:
                img.setImageResource(R.drawable.walkthrough_topics);
                return true;
        }

        return true;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

}
