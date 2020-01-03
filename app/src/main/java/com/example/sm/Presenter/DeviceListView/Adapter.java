package com.example.sm.Presenter.DeviceListView;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;

import com.example.sm.Model.ListDeviceInfo;
import com.example.sm.Presenter.MqttConnectManager;
import com.example.sm.Presenter.Utils.Utils;
import com.example.sm.R;


import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

public class Adapter extends ArrayAdapter<Item> {
    Context mContext;
    public static List<Item> listItem;
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
                    Log.d("htl",jsonObject.toString());
                    for (Item item:
                         listItem) {
                        item.setCurBeginTime(jsonObject.getJSONObject(item.getName()).getInt("start"));
                        item.setCurEndTime(jsonObject.getJSONObject(item.getName()).getInt("end"));

                    }
                    notifyDataSetInvalidated();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
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
                try {
                    new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String timeInMin = String.valueOf(hourOfDay*60 + minute);
                            String terminal2Send = listItem.get(position).getBeginTime().replace("@",timeInMin);
                            MqttConnectManager.sendData(listItem.get(position).getTopic(),terminal2Send);

                        }
                    },
                            0,
                            0,
                            true).show();
                }
                catch (WindowManager.BadTokenException e) {
                    //use a log message
                }
            }
        });
       endTimeItemDeviceTxt.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               try {
                   new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                       @Override
                       public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                           String timeInMin = String.valueOf(hourOfDay*60 + minute);
                           String terminal2Send = listItem.get(position).getEndTime().replace("@",timeInMin);
                           MqttConnectManager.sendData(listItem.get(position).getTopic(),terminal2Send);

                       }
                   },
                           0,
                           0,
                           true).show();
               }catch (WindowManager.BadTokenException e) {
                   //use a log message
               }
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
                createDialog(nameDeviceTxt.getText().toString());
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

    public void createDialog(){
        createDialog("");
    }
    public void createDialog(String name) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_add_device);
        String _topic="",_name="",_on="",_off="";
        String _startTime="",_endTime="";
        boolean wanaEdit=false;
        final Adapter adapterNullAble = Adapter.getInstance();
        JSONObject deviceJsonObj = adapterNullAble.getDevice(name);
        if(!name.equals("")){
            wanaEdit = true;
            try {
                _topic=deviceJsonObj.getString("topic");
                _name=deviceJsonObj.getString("name");
                _on=deviceJsonObj.getString("cmdOn");
                _off=deviceJsonObj.getString("cmdOff");
                _startTime=deviceJsonObj.getString("start");
                _endTime=deviceJsonObj.getString("end");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final EditText nameAddDeviceTxt = dialog.findViewById(R.id.nameAddDeviceTxt);
        final EditText cmdOnAddDeviceTxt = dialog.findViewById(R.id.cmdOnAddDeviceTxt);
        final EditText cmdOffAddDeviceTxt = dialog.findViewById(R.id.cmdOffAddDeviceTxt);
        final EditText startTimeAddDeviceTxt = dialog.findViewById(R.id.startTimeAddDeviceTxt);
        final EditText endTimeAddDeviceTxt = dialog.findViewById(R.id.endTimeAddDeviceTxt);
        final EditText topicAddDeviceTxt = dialog.findViewById(R.id.topicAddDeviceTxt);
        Button addAddDeviceBtn = dialog.findViewById(R.id.addAddDeviceBtn);
        Button cloneAddDeviceBtn = dialog.findViewById(R.id.cloneAddDeviceBtn);
        nameAddDeviceTxt.setText(_name);
        cmdOnAddDeviceTxt.setText(_on);
        cmdOffAddDeviceTxt.setText(_off);
        topicAddDeviceTxt.setText(_topic);
        startTimeAddDeviceTxt.setText(_startTime);
        endTimeAddDeviceTxt.setText(_endTime);
        final boolean finalWanaEdit = wanaEdit;
        final String final_name = _name;
        addAddDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(adapterNullAble != null){
                    if(finalWanaEdit){
                        adapterNullAble.editDevice(final_name,nameAddDeviceTxt.getText().toString(),
                                topicAddDeviceTxt.getText().toString(),
                                cmdOnAddDeviceTxt.getText().toString(),
                                cmdOffAddDeviceTxt.getText().toString(),
                                startTimeAddDeviceTxt.getText().toString(),
                                endTimeAddDeviceTxt.getText().toString());
                    }else{
                        adapterNullAble.addDevice(nameAddDeviceTxt.getText().toString(),
                                topicAddDeviceTxt.getText().toString(),
                                cmdOnAddDeviceTxt.getText().toString(),
                                cmdOffAddDeviceTxt.getText().toString(),
                                startTimeAddDeviceTxt.getText().toString(),
                                endTimeAddDeviceTxt.getText().toString());
                    }

                }
                dialog.cancel();

            }
        });
        cloneAddDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adapterNullAble != null){
                        adapterNullAble.addDevice(nameAddDeviceTxt.getText().toString()+" - copy",
                                topicAddDeviceTxt.getText().toString(),
                                cmdOnAddDeviceTxt.getText().toString(),
                                cmdOffAddDeviceTxt.getText().toString(),
                                startTimeAddDeviceTxt.getText().toString(),
                                endTimeAddDeviceTxt.getText().toString());
                }
                dialog.cancel();
            }
        });
        dialog.show();
    }
}
