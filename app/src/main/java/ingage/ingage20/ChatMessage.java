package ingage.ingage20;

/**
 * Created by Davis on 5/4/2017.
 */

public class ChatMessage {
    public boolean left;
    public String message;

    public ChatMessage(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }


    /** private String messageText;
    private String messageUser;
    private long messageTime;

    public ChatMessage(String messageText, String messageUser){
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
    }**/
}
