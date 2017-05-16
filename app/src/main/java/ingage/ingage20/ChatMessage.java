package ingage.ingage20;

import java.util.Date;

/**
 * Created by Davis on 5/4/2017.
 */

public class ChatMessage {
    private boolean side;
    private String messageText;
    private String messageUser;
    private long messageTime;

    public ChatMessage(boolean side, String messageText, String messageUser){
        this.side = side;
        this.messageText = messageText;
        this.messageUser = messageUser;

        messageTime = new Date().getTime();
    }


    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public boolean isSide() {
        return side;
    }

    public void setSide(boolean side) {
        this.side = side;
    }
}
