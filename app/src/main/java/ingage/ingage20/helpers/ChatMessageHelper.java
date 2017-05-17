package ingage.ingage20.helpers;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Davis on 5/4/2017.
 */

public class ChatMessageHelper {
    private boolean side;
    private String messageText;
    private String messageUser;
    private String messageTime;

    public ChatMessageHelper(boolean side, String messageText, String messageUser){
        this.side = side;
        this.messageText = messageText;
        this.messageUser = messageUser;

        messageTime = DateFormat.getDateTimeInstance().format(new Date());
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

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public boolean isSide() {
        return side;
    }

    public void setSide(boolean side) {
        this.side = side;
    }
}
