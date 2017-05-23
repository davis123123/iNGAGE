package ingage.ingage20.helpers;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Davis on 5/4/2017.
 */

public class ChatMessageHelper {
    private String side;
    private String messageText;
    private String messageUser;
    private String messageTime;
    private Long messageUpvote;
    private Long messageDownvote;
    private String messageID;

    public ChatMessageHelper(String messageID, String side, String messageText, String messageUser,
                             Long messageUpvote, Long messageDownvote, String messageTime){
        this.messageID = messageID;
        this.side = side;
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageTime = messageTime;
        this.messageUpvote = messageUpvote;
        this.messageDownvote = messageDownvote;
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

    public String isSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Long getMessageDownvote() {
        return messageDownvote;
    }

    public void setMessageDownvote(Long messageDownvote) {
        this.messageDownvote = messageDownvote;
    }

    public Long getMessageUpvote() {
        return messageUpvote;
    }

    public void setMessageUpvote(Long messageUpvote) {
        this.messageUpvote = messageUpvote;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
}
