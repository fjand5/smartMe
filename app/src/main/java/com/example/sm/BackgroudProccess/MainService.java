package com.example.sm.BackgroudProccess;

import android.app.ActivityManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import android.os.IBinder;

import android.widget.RemoteViews;
import android.widget.Toast;


import androidx.core.app.NotificationCompat;

import static android.widget.Toast.LENGTH_LONG;


public class MainService extends Service {
    MqttBroadcast mqttBroadcast;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {

        super.onCreate();
        mqttBroadcast = new MqttBroadcast();
        registerReceiver(new MqttBroadcast(),new IntentFilter(MqttBroadcast.getActionName()));

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mqttBroadcast.startMe(this);

        return START_REDELIVER_INTENT;
    }

    public static void beginService(Context context){

        Intent mqttServiceIntent = new Intent(context, MainService.class);
        if(!isMyServiceRunning(context,MainService.class)){
            Toast.makeText(context,"Đang mở dịch vụ", LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                context.startForegroundService(mqttServiceIntent);
            }else{

                context.startService(mqttServiceIntent);

            }
        }else{
            Toast.makeText(context,"Dịch vụ đang chạy", LENGTH_LONG).show();
        }

    }
    private static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void initFore() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mainNotifChannelHigh =
                    new NotificationChannel(MAIN_CHANNEL_ID,
                            "Dịch vụ chính của ứng dụng",
                            NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(mainNotifChannelHigh);
        }




        // create notif compat and notify
        NotificationCompat.Builder ntf = new NotificationCompat.Builder(this,MAIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setChannelId(MAIN_CHANNEL_ID);
        collapsedView = new RemoteViews(getPackageName(),R.layout.notify_main);

        collapsedView.setOnClickPendingIntent(R.id.btn_on, PendingIntent.getBroadcast(this,
                0,
                new Intent(MainNotifyAction.BROADCAST_NAME).putExtra("cmd","on"),
                0));
        collapsedView.setOnClickPendingIntent(R.id.btn_off,PendingIntent.getBroadcast(this,
                1,
                new Intent(MainNotifyAction.BROADCAST_NAME).putExtra("cmd","off"),
                0));

        ntf.setCustomContentView(collapsedView);

        startForeground(MAIN_ID_NOTIF, ntf.build());


    }

}
