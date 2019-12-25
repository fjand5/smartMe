package com.example.sm.Presenter.DeviceListView;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.sm.Model.ListDeviceInfo;
import com.example.sm.Presenter.MqttConnectManager;
import com.example.sm.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

public class Adapter extends ArrayAdapter<Item> {
    Context mContext;
    static List<Item> listItem;
    static Adapter instance;

    static public Adapter getInstance(Context context, int resource, List<Item> objects){
        if(instance == null){
            instance = new Adapter(context,resource,objects);
        }
        return instance;
    }
    public Adapter(Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
        mContext = context;
        listItem = objects;
        addDevice("Bảng Hiệu","luat/esp/rx",
                "{\"cmd\":\"SEP\",\"pin\":\"D2\",\"state\":\"LOW\"}",
                "{\"cmd\":\"SEP\",\"pin\":\"D2\",\"state\":\"HIGH\"}");
addDevice("Đèn Sao Băng","luat/esp/rx",
                "{\"cmd\":\"SEP\",\"pin\":\"D0\",\"state\":\"LOW\"}",
                "{\"cmd\":\"SEP\",\"pin\":\"D0\",\"state\":\"HIGH\"}");
addDevice("Đèn Đỏ","luat/esp/rx",
                "{\"cmd\":\"SEP\",\"pin\":\"D1\",\"state\":\"LOW\"}",
                "{\"cmd\":\"SEP\",\"pin\":\"D1\",\"state\":\"HIGH\"}");


    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if(v == null){
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            v = layoutInflater.inflate(R.layout.item_io_device,null);

        }
        Button onDevideBtn = v.findViewById(R.id.onDevideBtn);
        Button offDevideBtn = v.findViewById(R.id.offDevideBtn);
        TextView nameDeviceTxt = v.findViewById(R.id.nameDeviceTxt);
        nameDeviceTxt.setText(listItem.get(position).getName());
        onDevideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    MqttConnectManager.sendData(
                            listItem.get(position).getTopic(),
                            listItem.get(position).getCmdOn());
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

        return v;
    }

    void addDevice(String name,String topic, String cmdOn, String cmdOff){
        ListDeviceInfo.getInstance().addDevice(mContext,
                name,
                topic,
                cmdOn,
                cmdOff);
        syncDevice();
    }
    void syncDevice(){
        Set<String> tmp = ListDeviceInfo.getInstance().getListDevice(mContext);
        listItem.clear();
        for (String elm:
             tmp) {
            try {
                JSONObject jsonObject  =new JSONObject(elm);
                listItem.add(new Item(
                        jsonObject.getString("name"),
                        jsonObject.getString("topic"),
                        jsonObject.getString("cmdOn"),
                        jsonObject.getString("cmdOff")
                ));
            } catch (JSONException er) {
                er.printStackTrace();
            }
        }
        notifyDataSetInvalidated();
    }
}
