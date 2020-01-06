package com.example.sm.Presenter;

import android.util.Log;

import com.example.sm.BackgroudProccess.MqttBroadcast;
import com.example.sm.Model.SettingStore;
import com.example.sm.Presenter.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class IoManagerSetting {
    static IoManagerSetting instance;
    private String topic="",content="";
    static OnUpdateSettingDataListenner _onUpdateSettingDataListenner;

    public void setOnUpdateSettingDataListenner(OnUpdateSettingDataListenner onUpdateSettingDataListenner) {
        MqttBroadcast.reSubcribe();

        _onUpdateSettingDataListenner = onUpdateSettingDataListenner;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public IoManagerSetting() {
        SettingStore.getInstance().setOnNewSettingListenner(new SettingStore.OnNewSettingListenner() {
            @Override
            public void onNewSetting(String key, String inComeNewSetting) {

                if(key.equals(IoManagerSetting.class.getName())
                && Utils.isValidJsonObject(inComeNewSetting)){

                    if(_onUpdateSettingDataListenner != null)
                        _onUpdateSettingDataListenner.onUpdateSettingData();
                    try {
                        JSONObject jsonObject = new JSONObject(inComeNewSetting);
                        setContent(jsonObject.getString("content"));
                        setTopic(jsonObject.getString("topic"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public static IoManagerSetting getInstance() {
        if(instance == null)
            instance = new IoManagerSetting();

        return instance;
    }
    public void setSettingData(String topic, String content){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("topic",topic);
            jsonObject.put("content",content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SettingStore.getInstance().commitSetting(IoManagerSetting.class.getName(),jsonObject.toString());
    }
    public interface OnUpdateSettingDataListenner{
        void onUpdateSettingData();
    }
}
