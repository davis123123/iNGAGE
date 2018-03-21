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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ingage.ingage20.R;
import ingage.ingage20.activities.UserProfileActivity;
import ingage.ingage20.adapters.UserProfileInfoAdapter;
import ingage.ingage20.handlers.UserInfoHandler;
import ingage.ingage20.managers.SessionManager;


public class UserInfoFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    RecyclerView recycler;
    private View mLoadingView;
    UserProfileInfoAdapter adapter = new UserProfileInfoAdapter();
    String username, email, tribute_pts, subs, date_joined;
    protected static ArrayList<String> sub_arr = new ArrayList<>();
    String result = "Subscriptions: ";
    View rootView;
    private int mShortAnimationDuration;
    UserProfileActivity userProfileActivity;
    public static UserInfoHandler handler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userProfileActivity = (UserProfileActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
        recycler = (RecyclerView) rootView.findViewById(R.id.info);
        mLoadingView = rootView.findViewById(R.id.loading_spinner);
        SessionManager session = new SessionManager(getContext());
        HashMap<String, String> info = session.getUserDetails();
        if(userProfileActivity.getUsername().equals(info.get(SessionManager.KEY_NAME))) {
            mLoadingView.setVisibility(View.GONE);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recycler.setLayoutManager(layoutManager);
            adapter = new UserProfileInfoAdapter();
            recycler.setAdapter(adapter);

            username = info.get(SessionManager.KEY_NAME);
            email = info.get(SessionManager.KEY_EMAIL);
            tribute_pts = info.get(SessionManager.KEY_TRIBUTE_POINTS);
            subs = info.get(SessionManager.KEY_SUBSCRIPTIONS);
            date_joined = info.get(SessionManager.KEY_DATE_JOINED);
            if (email != null && email.length() > 0)
                adapter.add("Email: " + email);
            else
                adapter.add("Email: N/A");
            adapter.add("Tribute points: " + tribute_pts);
            setSubscriptions();
            adapter.add("Date joined: " + date_joined);
            adapter.notifyDataSetChanged();
        }
        else{
            UserInfoHandler.CallBackData callBackData = new UserInfoHandler.CallBackData() {
                @Override
                public void notifyChange(String serverResponse) {
                    Log.d("SERVERREeeSPONSE", serverResponse);
                    parseProfileJSON(serverResponse);
                    crossFadeRecyler();
                }
            };
            handler = new UserInfoHandler();
            handler.setCallBackData(callBackData);
            handler.enqueue(userProfileActivity.getUsername());
            recycler.setVisibility(View.GONE);
            mShortAnimationDuration = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
        }
        return rootView;
    }

    private void crossFadeRecyler() {
        recycler = (RecyclerView) rootView.findViewById(R.id.info);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
        adapter = new UserProfileInfoAdapter();
        recycler.setAdapter(adapter);

        if(email != null && email.length() > 0)
            adapter.add("Email: " + email);
        else
            adapter.add("Email: N/A" );
        adapter.add("Tribute points: " + tribute_pts);
        setSubscriptions();
        adapter.add("Date joined: " + date_joined);
        adapter.notifyDataSetChanged();

        recycler.setAlpha(0f);
        recycler.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        recycler.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

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

    //Parse the thread subscriptions JSON string
    protected ArrayList parseSubs(String thread_subscriptions){

        thread_subscriptions = thread_subscriptions.replace("["," ");
        thread_subscriptions = thread_subscriptions.replace("]"," ");
        sub_arr.clear();

        String arr[] = thread_subscriptions.split(",");
        for(int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].substring(arr[i].lastIndexOf(":") + 1);
            arr[i] = arr[i].replace("\"","");
            arr[i] = arr[i].replace("}","");
            sub_arr.add(arr[i]);
        }

        return sub_arr;
    }

    protected void parseProfileJSON(String json_string){
        JSONObject jsonObject;
        JSONArray jsonArray;
        Log.i("STATE", "userjson string: " + json_string);

        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("users");
            int count= 0;
            while(count < jsonArray.length()){
                JSONObject JO = jsonArray.getJSONObject(count);
                email = JO.getString("email");
                tribute_pts = JO.getString("tribute_points");
                subs = JO.getString("thread_subscriptions");
                date_joined = JO.getString("date_joined");
                count++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void setSubscriptions(){
        parseSubs(subs);

        for(int i=0; i < sub_arr.size(); i++){
            if(i == 0)
                result = result + sub_arr.get(i);
            else
                result = result + ", " + sub_arr.get(i);
        }
        adapter.add(result);
    }

    // TODO: Rename method, update argument and hook method into UI event
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
