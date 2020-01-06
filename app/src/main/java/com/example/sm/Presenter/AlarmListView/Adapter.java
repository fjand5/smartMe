package com.example.sm.Presenter.AlarmListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.sm.BackgroudProccess.MainService;
import com.example.sm.BackgroudProccess.MqttBroadcast;
import com.example.sm.Presenter.AlarmSetting;
import com.example.sm.Presenter.IoManagerSetting;
import com.example.sm.Presenter.ListDeviceInfo;
import com.example.sm.Presenter.MqttConnectManager;
import com.example.sm.Presenter.Utils.Utils;
import com.example.sm.R;
import com.example.sm.view.AlarmActivity;
import com.example.sm.view.MainActivity;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Adapter extends ArrayAdapter<Item> {
    static Adapter instance;
    static Context mContext;
    List<Item> listItem;
    Adapter(@NonNull Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
        mContext = context;
        listItem = objects;

    }

    public static Adapter getInstance(Context context, int resource, List<Item> objects) {
        if(instance == null)
            instance=new Adapter(context, resource, objects);

        AlarmSetting.getInstance().setOnUpdateSettingDataListenner(new AlarmSetting.OnUpdateSettingDataListenner() {
            @Override
            public void onUpdateSettingData() {
                instance.syncAlarm();
            }
        });
        MqttConnectManager.getInstance().setOnEventMqtt(new MqttConnectManager.Callback() {
            @Override
            public void onDisconnect() {

            }

            @Override
            public void onConnect() {

            }

            @Override
            public void onMessageArrived(String topic, MqttMessage message) {
                instance.syncAlarm();
            }
        });

        return instance;
    }
    public static Adapter getInstance() {
        return instance;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if(v == null) {

            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.item_alarm, null);
        }

            final TextView name = v.findViewById(R.id.alarmNameTxt);
            final Button deleteAlarmBtn = v.findViewById(R.id.deleteAlarmBtn);
            name.setText(listItem.get(position).name);
            deleteAlarmBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public void onClick(View view) {
                    AlarmActivity.confirmDialog(name.getText().toString());
                }
            });
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlarmActivity.createDialog(name.getText().toString());
                }
            });

        return v;
    }

    public void addAlarm(String name, String topic, String content, String respone){
        AlarmSetting.getInstance().addAlarm(name,topic,content,respone);
        syncAlarm();
    }
    public void editAlarm(String oldName, String name, String topic, String content,String respone){
        AlarmSetting.getInstance().editAlarm(oldName, name,topic,content,respone);
        syncAlarm();
    }

    public JSONObject getAlarm(String name){
        return AlarmSetting.getInstance().getAlarm(name);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void removeAlarm(String name){
        AlarmSetting.getInstance().removeAlarm(name);
    }
    public  void syncAlarm(){
        JSONArray tmp = AlarmSetting.getInstance().getListAlarm();
        listItem.clear();
        for (int i = 0; i<tmp.length();i++) {
            try {
                JSONObject jsonObject  = tmp.getJSONObject(i);
                listItem.add(new Item(
                        jsonObject.getString("name"),
                        jsonObject.getString("topic"),
                        jsonObject.getString("content"),
                        jsonObject.getString("respone")
                ));
            } catch (JSONException er) {
                er.printStackTrace();
            }
        }
        notifyDataSetInvalidated();
    }



}
