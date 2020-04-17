package io.eyec.bombo.bothopele;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MessagingService extends FirebaseMessagingService {
    private final String Tag = "FCM Message";

    @Override
    public void onNewToken(String s) {
        System.out.println("newToken is:- " + s);
        super.onNewToken(s);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getNotification() !=null){
            /*Intent appActivityIntent = new Intent(this, main.class);

            PendingIntent contentAppActivityIntent =
                    PendingIntent.getActivity(
                            context,  // calling from Activity
                            0,
                            appActivityIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);*/
            remoteMessage.getData();
            String []source = remoteMessage.getNotification().getBody().split("•");
            String title = remoteMessage.getNotification().getTitle();
            String target = source[0];
            //String body = source[1];
            String body = remoteMessage.getNotification().getBody();
            Map<String, String> map = remoteMessage.getData();
            //messagingHelper.displayNotification(getApplicationContext(),title,body);
            //String clickAction = remoteMessage.getNotification().getClickAction();
            //Intent intent = new Intent(clickAction);
            Intent intent = new Intent(this, MainActivity.class);
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            String jsonString = gson.toJson(map);
            intent.putExtra("extras",jsonString);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //check notification type

            //PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent,PendingIntent.FLAG_ONE_SHOT);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            if(remoteMessage.getData().isEmpty()) {
                messagingHelper.showNotification(getApplicationContext(), title, body,contentIntent);
            }else{
                messagingHelper.showBackGroundNotification(getApplicationContext(),remoteMessage.getData(),contentIntent);
            }
            //methods.globalMethods.loadFragments(R.id.main, new GlobalMessages(), getApplicationContext());
        }

    }
}
