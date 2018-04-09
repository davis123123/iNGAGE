package ingage.ingage20.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ingage.ingage20.R;
import ingage.ingage20.activities.UserProfileActivity;
import ingage.ingage20.adapters.RecentCommentsAdapter;
import ingage.ingage20.handlers.UserRecentCommentHandler;

public class RecentCommentsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    RecyclerView recycler;
    private View mLoadingView;
    private int mShortAnimationDuration;
    View rootView;
    RecentCommentsAdapter adapter;
    public static UserRecentCommentHandler handler;
    UserProfileActivity userProfileActivity;
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

        UserRecentCommentHandler.CallBackData callBackData = new UserRecentCommentHandler.CallBackData() {
            @Override
            public void notifyChange() {
                crossFadeRecyler();
            }
        };
        userProfileActivity = (UserProfileActivity) getActivity();
        handler = new UserRecentCommentHandler();
        handler.setCallBackData(callBackData);
        handler.enqueue(userProfileActivity.getUsername());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_recent_comments, container, false);
        recycler = (RecyclerView) rootView.findViewById(R.id.rvRecentComments);
        mLoadingView = rootView.findViewById(R.id.loading_spinner);

        recycler.setVisibility(View.GONE);
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        return rootView;
    }

    private void crossFadeRecyler(){
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

        //if no recent activities/comments, then show message
        if(adapter.getItemCount() == 0){
            recycler.setVisibility(View.GONE);
            RelativeLayout rlMessage = (RelativeLayout) rootView.findViewById(R.id.rlMessage);

            rlMessage.setAlpha(0f);
            rlMessage.setVisibility(View.VISIBLE);
            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            rlMessage.animate()
                    .alpha(1f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);

            // Animate the loading view to 0% opacity. After the animation ends,
            // set its visibility to GONE as an optimization step (it won't
            // participate in layout passes, etc.)
            mLoadingView.animate()
                    .alpha(0f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoadingView.setVisibility(View.GONE);
                        }
                    });
            //ImageView icon = (ImageView) rootView.findViewById(R.id.ivIcon);
            //icon.setColorFilter(getContext().getResources().getColor(R.color.dark_gray));
        }

        else {
            recycler.setAlpha(0f);
            recycler.setVisibility(View.VISIBLE);

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            recycler.animate()
                    .alpha(1f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);
        }

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mLoadingView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoadingView.setVisibility(View.GONE);
                    }
                });
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
