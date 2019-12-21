package com.example.sm.Presenter;

import android.content.Context;
import android.util.Log;

import com.example.sm.BackgroudProccess.MqttBroadcast;
import com.example.sm.Model.MqttInfo;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;

public class MqttSetting{
    static MqttSetting instance;
    private MqttSetting(){};
    static public MqttSetting getInstance(){
        if(instance == null)
            instance = new MqttSetting();

        return instance;
    }
    public void setInfo(Context context, String addr, int port, String name, String pass, String topic){
        MqttInfo.getInstance().setInfo(context,addr,port,name,pass,topic);
            try {
                MqttBroadcast.connectNow=true;
                MqttBroadcast.client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }

    }
    public ArrayList<Object> getInfo(Context context){
        ArrayList<Object>  ret = new ArrayList<>();
        MqttInfo mqttInfo = MqttInfo.getInstance();

        ret.add(mqttInfo.getAddress(context));
        ret.add(mqttInfo.getPort(context));
        ret.add(mqttInfo.getUsername(context));
        ret.add(mqttInfo.getPassword(context));
        ret.add(mqttInfo.getTopic(context));

    return  ret;
    }
}

