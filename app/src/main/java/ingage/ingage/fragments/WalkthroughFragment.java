package ingage.ingage.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ingage.ingage.R;
import ingage.ingage.adapters.WalkthroughAdapter;

public class WalkthroughFragment extends Fragment {

    //in case we need this fragment class in the future

    public static WalkthroughFragment newInstance(WalkthroughAdapter.WalkthroughHelper item) {
        WalkthroughFragment fragment = new WalkthroughFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_fragment_walkthrough, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
