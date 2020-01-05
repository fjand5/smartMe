package com.example.sm.Presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.sm.Model.SettingStore;
import com.example.sm.Presenter.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.sm.Presenter.Utils.Utils.isValidJsonArray;

public class AlarmSetting {
    static AlarmSetting instance;
    volatile String settingData="";
    private AlarmSetting(){
        SettingStore.getInstance().setOnNewSettingListenner(new SettingStore.OnNewSettingListenner() {
            @Override
            public void onNewSetting(String key, String inComeNewSetting) {
                if(key.equals(AlarmSetting.class.getName())){
                    settingData = inComeNewSetting;
                }

            }
        });
    };
    public static AlarmSetting getInstance(){
        if(instance == null){
            instance = new AlarmSetting();
        }

        return instance;
    };

    public void addAlarm( String name, String topic, String content){
        JSONArray tmp = getListAlarm();
        for (int i = 0; i<tmp.length(); i++){
            try {
                if(tmp.getJSONObject(i).getString("name").equals(name)){
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject jsonObject  = new JSONObject();

        try {
            jsonObject.put("name",name);
            jsonObject.put("topic",topic);
            jsonObject.put("content",content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tmp.put(jsonObject);
        setListAlarm(tmp);
    }
    public JSONObject getAlarm( String name){
        JSONArray tmp = getListAlarm();
        for (int i = 0; i<tmp.length(); i++){
            try {
                if(tmp.getJSONObject(i).getString("name").equals(name)){
                    return tmp.getJSONObject(i);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new JSONObject();
    }
    @SuppressLint("NewApi")
    public void editDevice(Context context,String beforName, String name, String topic, String content){
        JSONArray tmp = getListAlarm();
        JSONObject jsonObject  = new JSONObject();
        try {
            jsonObject.put("name",name);
            jsonObject.put("topic",topic);
            jsonObject.put("content",content);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i<tmp.length(); i++){
            try {
                if(tmp.getJSONObject(i).getString("name").equals(beforName)){
                    tmp.remove(i);
                    tmp.put(jsonObject);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setListAlarm(tmp);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void removeDevice(Context context, String name){
        JSONArray jsonArray  = getListAlarm();
        for (int i =0;i<jsonArray.length();i++) {
            try {
                JSONObject jsonObject  = jsonArray.getJSONObject(i);
                if(jsonObject.has("name")
                        && jsonObject.get("name").equals(name)){
                    jsonArray.remove(i);
                    setListAlarm(jsonArray);
                    return;
                }

            } catch (JSONException er) {
                er.printStackTrace();
            }
        }

    }

    public JSONArray getListAlarm(){
        JSONArray ret= null;
        String data = settingData;
        if(isValidJsonArray(data)){
            try {
                ret = new JSONArray(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            ret = new JSONArray();
        }

        return ret;
    }
    void setListAlarm(JSONArray jsonArray){
        SettingStore.getInstance().commitSetting(AlarmSetting.class.getName(),jsonArray.toString());

    }

}
