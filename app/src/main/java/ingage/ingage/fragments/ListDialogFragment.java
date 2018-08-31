package ingage.ingage.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import ingage.ingage.App;
import ingage.ingage.R;

/**
 * Created by wuv66 on 7/4/2018.
 */

public class ListDialogFragment extends DialogFragment {

    /** Declaring the interface, to invoke a callback function in the implementing activity class */
    public static ListItemSelectionListener priceRangeListener;

    public ListView lvItems;
    public TextView tvTitle;
    public ArrayAdapter<String> adapter;
    public ArrayList<String> items;

    /** An interface to be implemented in the hosting activity for "OK" button click listener */
    public interface ListItemSelectionListener {
        void onListItemSelected(String price);
    }

    public static ListDialogFragment newInstance() {

        ListDialogFragment f = new ListDialogFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getDialog().setCanceledOnTouchOutside(false);
        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.list_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        lvItems = (ListView) view.findViewById(R.id.lvItems);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);

        items = new ArrayList<String>(Arrays.asList(App.getAppContext().getResources().getStringArray(R.array.thread_categories)));
        adapter = new ArrayAdapter(getActivity(), R.layout.item_list_dialog_fragment, items);
        lvItems.setAdapter(adapter);

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                String price = adapter.getItem(position);
                priceRangeListener.onListItemSelected(price);
            }
        });
    }



    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        try{
            priceRangeListener = (ListItemSelectionListener) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement PriceRangeListener");
        }
    }



}
