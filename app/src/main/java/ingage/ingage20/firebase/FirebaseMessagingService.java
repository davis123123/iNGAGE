package ingage.ingage20.firebase;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import ingage.ingage20.activities.MainActivity;

/**
 * Created by Davis on 5/8/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    private static final String TAG = "fcmexamplemessage";
    //public static final String TOKEN_BROADCAST = "fcmtokenbroadcast";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        notifyUser(remoteMessage.getFrom(), remoteMessage.getNotification().getBody());
    }

    public void notifyUser(String from, String notification){
        FirebaseNotificationManager firebaseNotificationManager = new FirebaseNotificationManager(getApplicationContext());
        firebaseNotificationManager.showNotification(from, notification, new Intent(getApplicationContext(), MainActivity.class));

    }

}
