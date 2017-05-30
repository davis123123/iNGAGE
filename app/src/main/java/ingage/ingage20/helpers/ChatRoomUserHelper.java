package ingage.ingage20.helpers;

/**
 * Created by Davis on 5/29/2017.
 */

public class ChatRoomUserHelper {
    private String username, token;
    public ChatRoomUserHelper(String  username, String token){
        this.setUsername(username);
        this.setToken(token);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
