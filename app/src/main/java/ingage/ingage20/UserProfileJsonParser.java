package ingage.ingage20;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Davis on 4/27/2017.
 */

public class UserProfileJsonParser {
    private static String username, password, email, tribute_points, thread_subscriptions;

    void parseJSON(String json_string){
        JSONObject jsonObject;
        JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("user_profile");
            int count= 0;
            JSONObject JO = jsonArray.getJSONObject(count);
            setUsername(JO.getString("username"));
            setPassword(JO.getString("password"));
            setEmail(JO.getString("email"));
            setTribute_points(JO.getString("tribute_points"));
            setThread_subscriptions(JO.getString("thread_subscriptions"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTribute_points() {
        return tribute_points;
    }

    public void setTribute_points(String tribute_points) {
        this.tribute_points = tribute_points;
    }

    public String getThread_subscriptions() {
        return thread_subscriptions;
    }

    public void setThread_subscriptions(String thread_subscriptions) {
        this.thread_subscriptions = thread_subscriptions;
    }
}
