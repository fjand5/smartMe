package com.example.sm.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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
        Set<String> tmp = getListDevice(context);
        JSONObject jsonObject  = new JSONObject();
        try {
            jsonObject.put("name",name);
            jsonObject.put("cmdOn",cmdOn);
            jsonObject.put("cmdOff",cmdOff);
            jsonObject.put("topic",topic);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tmp.add(jsonObject.toString());
        setListDevice(context,tmp);
    }
    public void removeDevice(Context context,String name){
        Set<String> tmp = getListDevice(context);
        for (String e:
                tmp) {
            try {
                JSONObject jsonObject  =new JSONObject(e);
                if(jsonObject.has("name")
                        && jsonObject.get("name").equals(name)){
                    tmp.remove(e);
                    setListDevice(context,tmp);
                    return;
                }

            } catch (JSONException er) {
                er.printStackTrace();
            }
        }
    }
    public Set<String> getListDevice(Context context){

        return getSharedPreferences(context).getStringSet("ListDevice",new TreeSet<String>());
    }
    void setListDevice(Context context, Set<String> setString){
        getSharedPreferences(context)
                .edit()
                .remove("ListDevice")
                .putStringSet("ListDevice",
                        setString)
        .commit();

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
