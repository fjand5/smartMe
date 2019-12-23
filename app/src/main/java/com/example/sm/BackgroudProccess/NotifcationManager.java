package com.example.sm.BackgroudProccess;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.sm.R;


public class NotifcationManager {
    public static void createChannel(Context mContext,
                       String channelId,
                       String channelName,
                       int importance){
        NotificationManager notificationManager =
                (NotificationManager)  mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public  static void callNotify(Context mContext,
                                   int notifyId,
                                   String channelId,
                                   String title,
                                   String text
                    ){
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder notification = new Notification.Builder(mContext)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher_background);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification.setChannelId(channelId);
        }

        notificationManager.notify(notifyId, notification.build());
    }
}
