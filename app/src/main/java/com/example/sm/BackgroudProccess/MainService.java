package com.example.sm.BackgroudProccess;

import android.app.ActivityManager;

import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import android.os.IBinder;

import android.widget.Toast;



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
        return START_STICKY;
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


}
