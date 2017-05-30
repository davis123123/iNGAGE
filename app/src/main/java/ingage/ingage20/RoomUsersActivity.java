package ingage.ingage20;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ingage.ingage20.adapters.RoomUserAdapter;
import ingage.ingage20.handlers.ChatRoomHandler;
import ingage.ingage20.helpers.ChatRoomUserHelper;

/**
 * Created by Davis on 5/29/2017.
 */

public class RoomUsersActivity extends AppCompatActivity implements RoomUserAdapter.ListItemClickListener{
    ChatRoomHandler chatRoomHandler;
    JSONObject jsonObject;
    JSONArray jsonArray;
    ChatRoomUserHelper chatRoomUserHelper;
    RecyclerView roomUserRecyclerView;
    RoomUserAdapter roomUserAdapter;
    ChatRoomManager chatRoomManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_users);

        String type = "check";
        String json_string = "";
        chatRoomManager = new ChatRoomManager(getApplicationContext());
        HashMap<String, String> chatroom = chatRoomManager.getUserDetails();
        String thread_id = chatroom.get(ChatRoomManager.THREAD_ID);

        chatRoomHandler = new ChatRoomHandler(getApplicationContext());

        roomUserRecyclerView = (RecyclerView) findViewById(R.id.rv_posts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        roomUserRecyclerView.setLayoutManager(layoutManager);
        roomUserAdapter = new RoomUserAdapter(this);
        roomUserRecyclerView.setAdapter(roomUserAdapter);
        try {
            json_string = chatRoomHandler.execute(type, thread_id).get();
            Log.d("CHECK" , "result : " + json_string);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("users");
            int count= 0;
            String username, token;
            while(count < jsonArray.length()){
                JSONObject JO = jsonArray.getJSONObject(count);
                username = JO.getString("username");
                token = JO.getString("token");
                ChatRoomUserHelper chatRoomUserHelper = new ChatRoomUserHelper(username, token);
                roomUserAdapter.add(chatRoomUserHelper);
                count++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

        Context context = getApplicationContext();

        ChatRoomUserHelper chatRoomUserHelper = (ChatRoomUserHelper) roomUserAdapter.getItem(clickedItemIndex);
        String username = chatRoomUserHelper.getUsername();

        //LEAVE UNTIL COMMENTS A RE FINISHED
        String toastMessage = "Item #" + username + "clicked.";
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
        Intent i = new Intent();
        i.putExtra("Username", username);
        setResult(RESULT_OK, i);
        finish();

    }
}
