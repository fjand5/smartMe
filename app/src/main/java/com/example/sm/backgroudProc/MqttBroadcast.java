package com.example.sm.backgroudProc;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.sm.view.SettingActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static android.content.Context.MODE_PRIVATE;


public class MqttBroadcast extends BroadcastReceiverExt {
    static ListenConnect listenConnect;
    public static void setListenConnect(ListenConnect callback){
        listenConnect=callback;
    }

    static Context mContext;

    static String _add;
    static int _port;
    static String _user;
    static String _pass;
    static String[] _topicRx;

    static String getActionName(){
        return MqttBroadcast.class.getName();
    }
    MqttBroadcast() {
        super(MqttBroadcast.class.getName());
    }
    public static void setInfo(){
        SharedPreferences MqttInfo;
        MqttInfo = mContext.getSharedPreferences(SettingActivity.class.getName(),MODE_PRIVATE);


        _add=MqttInfo.getString("addrTxt","");
        try {
            _port=Integer.valueOf(MqttInfo.getString("portTxt",""));
        } catch (NumberFormatException nfe) {
            _port = 0;
        }

        _user=MqttInfo.getString("nameTxt","");
        _pass=MqttInfo.getString("passTxt","");
        _topicRx= new String[]{"luat/espAL/tx", ""};
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        initMqttConnection();

    }
    void initMqttConnection(){
        setInfo();
        MqttConnectOptions options;
        String clientId;
        final MqttAndroidClient client;
        IMqttActionListener mqttListenner;
        MqttCallback callbackMQTT;


        options = new MqttConnectOptions();
        clientId = MqttClient.generateClientId();

        client= new MqttAndroidClient(mContext, "tcp://"+_add+":"+_port,
                clientId);

        options.setUserName(_user);
        options.setPassword(_pass.toCharArray());


        mqttListenner = new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                listenConnect.onConnect();
                Log.d("htl","onSuccess");


                try {
                    for (String s: _topicRx) {
                        client.subscribe(s,2);
                    }

                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                listenConnect.onDisconnect();
                reCallMe(mContext);

            }
        };
        callbackMQTT = new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {
                listenConnect.onDisconnect();
                reCallMe(mContext);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String content = new String(message.getPayload());
                Toast.makeText(mContext,content,Toast.LENGTH_LONG).show();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        };

        client.setCallback(callbackMQTT);
        try {
            client.connect(options,mContext,mqttListenner);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    public interface ListenConnect{
        void onDisconnect();
        void onConnect();
    }
}
