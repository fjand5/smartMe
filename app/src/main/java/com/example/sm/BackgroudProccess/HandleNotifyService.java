package com.example.sm.BackgroudProccess;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.sm.Presenter.MqttConnectManager;
import com.example.sm.Presenter.MqttSetting;
import com.example.sm.R;

public class HandleNotifyService extends IntentService {
    String name;
    Context mContext;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public HandleNotifyService(String name) {
        super(name);
        this.name =name;
    }
    public HandleNotifyService() {
        super(null);

    }

    @SuppressLint("ResourceType")
    @Override
    protected void onHandleIntent(Intent intent) {

        mContext = this;
        boolean state = intent.getBooleanExtra("state",true);
        if(state == false){
            MqttConnectManager
                    .getInstance()
                    .setRunning(false);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,"Chương trình đã tạm dừng",Toast.LENGTH_LONG).show();
                }
            });
        }
        if(state == true){
            MqttConnectManager
                    .getInstance()
                    .setRunning(true);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,"Chương trình Đang chạy",Toast.LENGTH_LONG).show();
                }
            });
        }


    }

}
