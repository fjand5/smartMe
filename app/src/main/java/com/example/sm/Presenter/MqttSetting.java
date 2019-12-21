package com.example.sm.Presenter;

import android.content.Context;
import android.util.Log;

import com.example.sm.BackgroudProccess.MqttBroadcast;
import com.example.sm.Model.MqttInfo;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
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

    public Dictionary<String, Object> getInfo(Context context){
        Dictionary<String, Object>  ret = new Hashtable<>();
        MqttInfo mqttInfo = MqttInfo.getInstance();

        ret.put("address",mqttInfo.getAddress(context));
        ret.put("port",mqttInfo.getPort(context));
        ret.put("username",mqttInfo.getUsername(context));
        ret.put("password",mqttInfo.getPassword(context));
        ret.put("topic",mqttInfo.getTopic(context));

    return  ret;
    }
}

