package com.example.sm;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.sm.Presenter.MqttConnectManager;
import com.example.sm.view.RingActivity;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.sm.Presenter.Utils.Utils.callActivity;

public class InitSystem {
    static boolean LISTEN_MESSAGE = true;

    static Thread thread;
    static InitSystem instancs;

    Context mContext;


    static boolean sendSignalFlag = false;
    static int sendSignalPeriod = 1000;
    static long getSendSignalPretime=System.currentTimeMillis();

    MqttConnectManager.Callback callbackAlarm;
    public static void setSendSignalFlag(boolean sendSignalFlag) {
        InitSystem.sendSignalFlag = sendSignalFlag;
    }

    private InitSystem(Context context){
        mContext = context;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(sendSignalFlag
                    && (System.currentTimeMillis()-getSendSignalPretime) > sendSignalPeriod){
                        sendSignal();
                    }
                }

            }
        });
        thread.start();
        callbackAlarm= new MqttConnectManager.Callback() {
            @Override
            public void onDisconnect() {

            }

            @Override
            public void onConnect() {

            }

            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onMessageArrived(String topic, MqttMessage message) {
                String content = new String(message.getPayload());
                JSONObject jsonObject  = null;
                try {
                    jsonObject = new JSONObject(content);
                    if(jsonObject.has("cmd")
                            && jsonObject.get("cmd").equals("ALR")){
                        // onAlarm

                        String tmp = new JSONObject().put("cmd","UAL")
                                .put("time",5000).toString();
                        MqttConnectManager.sendData("luat/espAL/rx",tmp);
                        callRingActiity();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        MqttConnectManager.getInstance().setOnEventMqtt(callbackAlarm);
    }

    public static InitSystem getInstancs(Context context) {
        if(instancs==null)
            instancs = new InitSystem(context);
        return instancs;
    }
    private void sendSignal() {

        String tmp = null;
        try {
            tmp = new JSONObject().put("cmd","GDT").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttConnectManager.sendData("luat/espAL/rx",tmp);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void callRingActiity() {
        callActivity(mContext, RingActivity.class);

    }

}
