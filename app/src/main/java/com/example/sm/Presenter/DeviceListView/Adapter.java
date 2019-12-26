package com.example.sm.Presenter.DeviceListView;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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


import org.json.JSONArray;
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
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            v = layoutInflater.inflate(R.layout.item_io_device,null);

        }
        Button onDevideBtn = v.findViewById(R.id.onDevideBtn);
        Button offDevideBtn = v.findViewById(R.id.offDevideBtn);
        final TextView nameDeviceTxt = v.findViewById(R.id.nameDeviceTxt);
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
        nameDeviceTxt.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("Xác nhận !")
                        .setMessage("Bạn có muốn xóa thiết bị này không ?")
                        .setIcon(R.drawable.icon)
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Adapter.getInstance().removeDevice(nameDeviceTxt.getText().toString());
                                syncDevice();
                            }
                        });
                dialog.create();
                dialog.show();

                return false;
            }
        });
        return v;
    }

    public void addDevice(String name, String topic, String cmdOn, String cmdOff){
        ListDeviceInfo.getInstance().addDevice(mContext,
                name,
                topic,
                cmdOn,
                cmdOff);
        syncDevice();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void removeDevice(String name){
        ListDeviceInfo.getInstance().removeDevice(mContext,name);
    }
    void syncDevice(){
        JSONArray tmp = ListDeviceInfo.getInstance().getListDevice(mContext);
        listItem.clear();
        for (int i = 0; i<tmp.length();i++) {
            try {
                JSONObject jsonObject  = tmp.getJSONObject(i);
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
