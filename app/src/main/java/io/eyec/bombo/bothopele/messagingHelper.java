package io.eyec.bombo.bothopele;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Map;
import java.util.Random;

import methods.globalMethods;

public class messagingHelper {

    static String channelId = "io.eyec.bombo.bothopele";
    private static boolean includeLargeIcon;
    private static String largeIcon;

    public static boolean isIncludeLargeIcon() {
        return includeLargeIcon;
    }

    public static void setIncludeLargeIcon(boolean includeLargeIcon) {
        messagingHelper.includeLargeIcon = includeLargeIcon;
    }

    public static String getLargeIcon() {
        return largeIcon;
    }

    public static void setLargeIcon(String largeIcon) {
        messagingHelper.largeIcon = largeIcon;
    }

    public static void displayNotification (final Context context, final String title, final String body){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channelId)
                .setSmallIcon(R.mipmap.ic_launcher_round) //change
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void showNotification (final Context context, final String title, final String body, final PendingIntent PendingIntent){

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(channelId,"Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("EDMT Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(R.color.Purple);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channelId);
        builder.setAutoCancel(true)
                //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.schoolname)) change
                .setSmallIcon(R.mipmap.ic_launcher_round) //change
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentInfo("info")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(PendingIntent);
        if(messagingHelper.isIncludeLargeIcon()){
            builder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(globalMethods.getBitmapFromURL(getLargeIcon())));
        }
        notificationManager.notify(new Random().nextInt(),builder.build());


        /*Intent appActivityIntent = new Intent(context, main.class);

        PendingIntent contentAppActivityIntent =
                PendingIntent.getActivity(
                        context,  // calling from Activity
                        0,
                        appActivityIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager.notify(new Random().nextInt(),builder.build());
        builder.setContentIntent(contentAppActivityIntent);
        //PendingIntent contentIntent =
        //        PendingIntent.getActivity(context, 0, new Intent(context, main.class), 0);*/

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void showBackGroundNotification (final Context context, Map<String, String> data, final PendingIntent PendingIntent){

        //String []source = data.get("body").split("•");
        String title = data.get("title");
        //String body = source[1];
        String body = data.get("body");

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel  notificationChannel = new NotificationChannel(channelId,"Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("EDMT Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(R.color.Purple);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableLights(true);
            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.getApplicationContext().getPackageName() + "/" + R.raw.notification);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            notificationChannel.setSound(soundUri,audioAttributes);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channelId);
        builder.setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentInfo("info")
                .setSound(defaultSoundUri)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(PendingIntent);
        if(messagingHelper.isIncludeLargeIcon()){
            builder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(globalMethods.getBitmapFromURL(getLargeIcon())));
        }

        notificationManager.notify(new Random().nextInt(),builder.build());

    }
}
