package ingage.ingage20.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import ingage.ingage20.R;
import ingage.ingage20.adapters.UserProfileInfoAdapter;
import ingage.ingage20.managers.SessionManager;


public class UserInfoFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    RecyclerView recycler;
    UserProfileInfoAdapter adapter = new UserProfileInfoAdapter();
    String username, email, tribute_pts, subs, date_joined;
    protected static ArrayList<String> sub_arr = new ArrayList<>();
    String result = "Subscriptions: ";

    public UserInfoFragment() {

    }


    public static UserInfoFragment newInstance() {
        UserInfoFragment fragment = new UserInfoFragment();
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
        recycler = (RecyclerView) rootView.findViewById(R.id.info);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
        adapter = new UserProfileInfoAdapter();
        recycler.setAdapter(adapter);

        SessionManager session = new SessionManager(getContext());
        HashMap<String, String> info = session.getUserDetails();
        username = info.get(SessionManager.KEY_NAME);
        email = info.get(SessionManager.KEY_EMAIL);
        tribute_pts = info.get(SessionManager.KEY_TRIBUTE_POINTS);
        subs = info.get(SessionManager.KEY_SUBSCRIPTIONS);
        date_joined = info.get(SessionManager.KEY_DATE_JOINED);

        if(email != null && email.length() > 0)
            adapter.add("Email: " + email);
        else
            adapter.add("Email: N/A" );
        adapter.add("Tribute points: " + tribute_pts);
        setSubscriptions();
        adapter.add("Date joined: " + date_joined);
        adapter.notifyDataSetChanged();
        return rootView;
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
