package com.example.sm.Presenter.DeviceListView;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;

import com.example.sm.BackgroudProccess.MainService;
import com.example.sm.BackgroudProccess.MqttBroadcast;
import com.example.sm.Model.SettingStore;
import com.example.sm.Presenter.IoManagerSetting;
import com.example.sm.Presenter.ListDeviceInfo;
import com.example.sm.Presenter.MqttConnectManager;
import com.example.sm.Presenter.Utils.Utils;
import com.example.sm.R;
import com.example.sm.view.IoManagerActivity;
import com.example.sm.view.MainActivity;


import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Adapter extends ArrayAdapter<Item> {
    Context mContext;
    public static List<Item> listItem;
    static Adapter instance;

    static public Adapter getInstance(Context context, int resource, List<Item> objects){
        if(instance == null){
            instance = new Adapter(context,resource,objects);
        }
        IoManagerSetting.getInstance().setOnUpdateSettingDataListenner(new IoManagerSetting.OnUpdateSettingDataListenner() {
            @Override
            public void onUpdateSettingData() {

                instance.syncDevice();
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
                String content = new String(message.getPayload());

                try {
                    JSONObject jsonObject = new JSONObject(content);

                    for (Item item:
                            listItem) {
                        item.setCurBeginTime(jsonObject.getJSONObject(item.getName()).getInt("start"));
                        item.setCurEndTime(jsonObject.getJSONObject(item.getName()).getInt("end"));

                    }

                    instance.syncDevice();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        instance.syncDevice();

        return instance;
    }
    static public Adapter getInstance(){
        return instance;
    }

    public Adapter(Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
        mContext = context;
        listItem = objects;


    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if(v == null){
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.item_io_device,null);

        }
        Button onDevideBtn = v.findViewById(R.id.onDevideBtn);
        Button offDevideBtn = v.findViewById(R.id.offDevideBtn);
        final TextView nameDeviceTxt = v.findViewById(R.id.nameDeviceTxt);
        final TextView deleteDeviceTxt = v.findViewById(R.id.deleteDeviceTxt);
        final TextView startTimeItemDeviceTxt = v.findViewById(R.id.startTimeItemDeviceTxt);
        final TextView endTimeItemDeviceTxt = v.findViewById(R.id.endTimeItemDeviceTxt);
        startTimeItemDeviceTxt.setText(
                Utils.getTimeStringFromTimeInMin(listItem.get(position).getCurBeginTime())
        );
        endTimeItemDeviceTxt.setText(
                Utils.getTimeStringFromTimeInMin(listItem.get(position).getCurEndTime())
        );
        nameDeviceTxt.setText(listItem.get(position).getName());
        onDevideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    MqttConnectManager.sendData(
                            listItem.get(position).getTopic(),
                            listItem.get(position).getCmdOn());
            }
        });
        startTimeItemDeviceTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              IoManagerActivity.createTimePicker(listItem.get(position).getTopic(),listItem.get(position).getBeginTime());
            }
        });
       endTimeItemDeviceTxt.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               IoManagerActivity.createTimePicker(listItem.get(position).getTopic(),listItem.get(position).getEndTime());
           }
       });

        offDevideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    MqttConnectManager.sendData(
                            listItem.get(position).getTopic(),
                            listItem.get(position).getCmdOff());
            }
        });
        nameDeviceTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IoManagerActivity.createDialog(nameDeviceTxt.getText().toString());
            }
        });
        deleteDeviceTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             IoManagerActivity.confirmDialog(nameDeviceTxt.getText().toString());
            }
        });

        return v;
    }

    public void addDevice(String name, String topic, String cmdOn, String cmdOff, String beginTime, String endTime){
        ListDeviceInfo.getInstance().addDevice(mContext,
                name,
                topic,
                cmdOn,
                cmdOff,
                beginTime,
                endTime);
        syncDevice();
    }

    public void editDevice(String beforName, String name, String topic, String cmdOn, String cmdOff, String beginTime, String endTime){
        ListDeviceInfo.getInstance().editDevice(mContext,
                beforName,
                name,
                topic,
                cmdOn,
                cmdOff,
                beginTime,
                endTime);
        syncDevice();
    }
    public JSONObject getDevice(String name){
        return ListDeviceInfo.getInstance().getDevice(mContext,
                name);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void removeDevice(String name){
        ListDeviceInfo.getInstance().removeDevice(mContext,name);
    }
    public  void syncDevice(){
        JSONArray tmp = ListDeviceInfo.getInstance().getListDevice(mContext);
        listItem.clear();
        for (int i = 0; i<tmp.length();i++) {
            try {
                JSONObject jsonObject  = tmp.getJSONObject(i);
                listItem.add(new Item(
                        jsonObject.getString("name"),
                        jsonObject.getString("topic"),
                        jsonObject.getString("cmdOn"),
                        jsonObject.getString("cmdOff"),
                        jsonObject.getString("start"),
                        jsonObject.getString("end"),
                        -1,
                        -1
                ));
            } catch (JSONException er) {
                er.printStackTrace();
            }
        }
        notifyDataSetInvalidated();
    }


}
