package com.example.sm.BackgroudProccess;

import android.app.ActivityManager;

import android.app.Notification;
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


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.sm.R;

import static android.widget.Toast.LENGTH_LONG;
import static com.example.sm.BackgroudProccess.NotifcationManager.createChannel;


public class MainService extends Service {
    static MainService instance;
    MqttBroadcast mqttBroadcast;
    NotificationCompat.Builder ntf;
     RemoteViews rv;
    int MAIN_ID = 1;
    public static MainService getInstance(){

        if(instance==null)
            instance = new MainService();
        return instance;
    }
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



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mqttBroadcast.startMe(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            initFore();
        }
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initFore() {

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.createNotificationChannel(new NotificationChannel(
                MainService.class.getName(),
                Long.toString(System.currentTimeMillis()),
                NotificationManager.IMPORTANCE_HIGH)
        );


        ntf = new NotificationCompat.Builder(this,MainService.class.getName());

        rv= new RemoteViews(getPackageName(),R.layout.notify_layout);
        ntf.setSmallIcon(R.drawable.ic_launcher_background);
        ntf.setCustomContentView(rv);
        rv.setTextViewText(R.id.button,"setText");
        startForeground(MAIN_ID, ntf.build());


    }


}
