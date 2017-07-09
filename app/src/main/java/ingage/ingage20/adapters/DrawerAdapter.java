package ingage.ingage20.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.R;
import ingage.ingage20.activities.LoginActivity;
import ingage.ingage20.activities.MainActivity;
import ingage.ingage20.activities.UserProfileActivity;
import ingage.ingage20.handlers.IdentityHandler;
import ingage.ingage20.managers.SessionManager;


/**
 * Created by wuv66 on 7/6/2017.
 */

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerHolder> {

    List list = new ArrayList();
    private Context context;


    public DrawerAdapter(Context context) {
        this.context = context;
    }

    public void add(String content){
        list.add(content);
    }

    @Override
    public DrawerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drawer_item, parent, false);

        return new DrawerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DrawerHolder holder, int position) {
        String field = (String) list.get(position);
        holder.content.setText(field);
        if(position == 1)
            holder.icon.setImageResource(R.drawable.ic_menu_send);
        if (position == 2)
            holder.icon.setImageResource(R.drawable.ic_menu_slideshow);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class DrawerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView content;
        public ImageView icon;

        public DrawerHolder(View view) {
            super(view);
            content = (TextView) view.findViewById(R.id.option_text);
            icon = (ImageView) view.findViewById(R.id.option_icon);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int pos = getAdapterPosition();

                    if(list.get(pos).equals("View Profile")){
                        Intent intent = new Intent(context, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    else if (list.get(pos).equals("Sign Out")){
                        goSignOut();
                    }

                    Log.d("STATE", "drawer item clicked! " + list.get(pos));
                }
            });
        }

        @Override
        public void onClick(View v) {

        }

        public void goSignOut(){
            String type = "sign_out";
            SessionManager session = new SessionManager(context);
            HashMap<String, String> user = session.getUserDetails();
            String username = user.get(SessionManager.KEY_NAME);
            String password = user.get(SessionManager.KEY_PASSWORD);
            IdentityHandler identityHandler = new IdentityHandler(context);
            String loginStatus = "";

            try {
                loginStatus = identityHandler.execute(type, username, password).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if(loginStatus.equals("sign out success")) {
                //must logout user in phone AFTER successfully logged out in server
                session.logoutUser();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                MainActivity.adapter.clear();
                MainActivity.adapter.notifyDataSetChanged();
                context.startActivity(intent);

                Toast.makeText(context, "Successfully signed out!", Toast.LENGTH_SHORT).show();
            }//only sign out if proper connection to server is made

            else{
                Toast.makeText(context, "Sign out Failed, please Check Connection", Toast.LENGTH_SHORT).show();
            }
        }

    }
}

