package com.example.sm.Model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.JsonReader;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Set;
import java.util.TreeSet;


public class ListDeviceInfo{

    static ListDeviceInfo instance;

    private ListDeviceInfo(){

    };
    public static ListDeviceInfo getInstance(){
        if(instance == null){
            instance = new ListDeviceInfo();
        }

        return instance;
    };

    public void addDevice(Context context, String name,String topic, String cmdOn, String cmdOff){
        JSONArray tmp = getListDevice(context);
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
            jsonObject.put("cmdOn",cmdOn);
            jsonObject.put("cmdOff",cmdOff);
            jsonObject.put("topic",topic);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tmp.put(jsonObject);
        setListDevice(context,tmp);
    }
    public JSONObject getDevice(Context context, String name){
        JSONArray tmp = getListDevice(context);
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
    public void editDevice(Context context,String beforName, String name, String topic, String cmdOn, String cmdOff){
        JSONArray tmp = getListDevice(context);
        JSONObject jsonObject  = new JSONObject();
        try {
            jsonObject.put("name",name);
            jsonObject.put("cmdOn",cmdOn);
            jsonObject.put("cmdOff",cmdOff);
            jsonObject.put("topic",topic);
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
        setListDevice(context,tmp);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void removeDevice(Context context, String name){
        JSONArray jsonArray  = getListDevice(context);
        for (int i =0;i<jsonArray.length();i++) {
            try {
                JSONObject jsonObject  = jsonArray.getJSONObject(i);
                if(jsonObject.has("name")
                        && jsonObject.get("name").equals(name)){
                    jsonArray.remove(i);
                    setListDevice(context,jsonArray);
                    return;
                }

            } catch (JSONException er) {
                er.printStackTrace();
            }
        }

    }
    public boolean isValidJsonArray(String jsonStr) {
        Object json = null;
        try {
            json = new JSONTokener(jsonStr).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json instanceof JSONArray) {
            return true;
        } else {
            return false;
        }
    }
    public JSONArray getListDevice(Context context){
        JSONArray ret= null;
        String data = getSharedPreferences(context).getString(ListDeviceInfo.class.getName(),"");
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
    void setListDevice(Context context, JSONArray jsonArray){
        getSharedPreferences(context)
                .edit()
                .remove(ListDeviceInfo.class.getName())
                .putString(ListDeviceInfo.class.getName(),
                        jsonArray.toString())
        .apply();

    }
    public void clearDevice(Context context){
        setListDevice(context,null);
    }
    private SharedPreferences getSharedPreferences(Context context){
        SharedPreferences  ret;
        ret = context.getSharedPreferences(
                com.example.sm.Model.ListDeviceInfo.class.toString()
                ,context.MODE_PRIVATE);

        return ret;
    }

}
