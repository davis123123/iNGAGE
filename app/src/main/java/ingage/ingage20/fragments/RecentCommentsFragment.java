package ingage.ingage20.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ingage.ingage20.R;
import ingage.ingage20.activities.UserProfileActivity;
import ingage.ingage20.adapters.RecentCommentsAdapter;

public class RecentCommentsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    RecyclerView recycler;
    RecentCommentsAdapter adapter;

    public RecentCommentsFragment() {

    }


    public static RecentCommentsFragment newInstance() {
        RecentCommentsFragment fragment = new RecentCommentsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recent_comments, container, false);
        adapter = new RecentCommentsAdapter(getContext());
        recycler = (RecyclerView) rootView.findViewById(R.id.rvRecentComments);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);

        for(int i=0; i<UserProfileActivity.recentComments.size(); i++){
            //Log.i("STATE", "recent comment : " + UserProfileActivity.recentComments.get(i).thread_title + ", " + UserProfileActivity.recentComments.get(i).recent_comment);
            adapter.add(UserProfileActivity.recentComments.get(i));
        }

        adapter.notifyDataSetChanged();
        return rootView;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
