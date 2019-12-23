package com.example.sm.Presenter;

import android.util.Log;
import android.widget.Toast;

import com.example.sm.BackgroudProccess.MqttBroadcast;
import com.example.sm.Presenter.RxDataListView.Adapter;
import com.example.sm.Presenter.RxDataListView.Item;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class MqttConnectManager {
    static MqttConnectManager instance;
    private ArrayList<Callback> onEventMqttList;
    private Callback e;

    private MqttConnectManager() {
        MqttBroadcast.setOnConnectStatusChange(new MqttBroadcast.OnConnectStatusChange() {
            @Override
            public void onDisconnect() {
                for (Callback onEventMqtt:
                onEventMqttList) {
                    if(onEventMqtt!=null)
                        onEventMqtt.onDisconnect();
                }

            }
            @Override
            public void onConnect() {
                for (Callback onEventMqtt:
                        onEventMqttList) {
                    if (onEventMqtt != null)
                        onEventMqtt.onConnect();
                }
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) {
                for (Callback onEventMqtt:
                        onEventMqttList) {
                    if (onEventMqtt != null)
                        onEventMqtt.onMessageArrived(topic, message);
                }
            }
        });
    }

    public static MqttConnectManager getInstance() {
        if(instance==null)
            instance = new MqttConnectManager();
        return instance;
    }

    public void setOnEventMqtt(Callback onEventMqtt) {
        if(onEventMqttList == null)
            onEventMqttList = new ArrayList<>();

        Log.d("htl",String.valueOf(onEventMqttList.size()));
        onEventMqttList.add(onEventMqtt);

    }
    public void removeOnEventMqtt(Callback onEventMqtt) {
        if(onEventMqttList == null)
            return;
//        for (Callback e:
//                onEventMqttList) {
//            if (e.toString().equals(onEventMqtt.toString()))
//                onEventMqttList.remove(e);
//        }


    }
    public static void sendData(String topic, String content) {

        MqttBroadcast.publish(topic,content);
    }

    public interface Callback{
        void onDisconnect();
        void onConnect();
        void onMessageArrived(String topic, MqttMessage message);

    }

}
