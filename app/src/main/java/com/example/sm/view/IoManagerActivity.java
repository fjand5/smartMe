package com.example.sm.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.sm.Model.SettingStore;
import com.example.sm.Presenter.DeviceListView.Adapter;
import com.example.sm.Presenter.DeviceListView.Item;
import com.example.sm.Presenter.IoManagerSetting;
import com.example.sm.Presenter.MqttConnectManager;
import com.example.sm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IoManagerActivity extends Activity {
    ListView ioDeviceLsv;
    Adapter deviceAdapter;
    List<Item> itemList;
    FloatingActionButton floatingActionButton;
    volatile boolean runSignal = false;

    String topic="";
    String content="";

    public static Context mContext;
    @Override
    protected void onResume() {

        super.onResume();
        SettingStore.getInstance();


        runSignal = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (runSignal){
                    topic = IoManagerSetting.getInstance().getTopic();
                    content = IoManagerSetting.getInstance().getContent();
                    if(topic.equals("") || content.equals(""))
                        continue;
                    MqttConnectManager.sendData(topic,
                            content);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        runSignal = false;
        super.onPause();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mContext =this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io_manager);
        initView();
        addEvent();
        itemList = new ArrayList<>();
        deviceAdapter = Adapter.getInstance(this,R.layout.item_io_device,itemList);
        ioDeviceLsv.setAdapter(deviceAdapter);
        deviceAdapter.notifyDataSetInvalidated();



    }

    @SuppressLint("ClickableViewAccessibility")
    private void addEvent() {


        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                createDialog();
                return false;
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.dialog_setting_signal);
                final EditText topicSignalTxt = dialog.findViewById(R.id.topicSignalTxt);
                final EditText nameSignalTxt = dialog.findViewById(R.id.nameSignalTxt);
                topicSignalTxt.setText(topic);
                nameSignalTxt.setText(content);
                Button okSignalBtn = dialog.findViewById(R.id.okSignalBtn);
                okSignalBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setInfo(topicSignalTxt.getText().toString(),nameSignalTxt.getText().toString());
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });

    }


    private void initView() {
        ioDeviceLsv = findViewById(R.id.ioDeviceLsv);
        floatingActionButton =findViewById(R.id.addDeviceBtn);
    }

    private void setInfo(String topic, String content) {
        IoManagerSetting.getInstance().setSettingData(topic,content);

    }
    public static void confirmDialog(final String name){
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
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Adapter.getInstance().removeDevice(name);
                    Adapter.getInstance().syncDevice();
                }
            });
    dialog.create();
    if (! ((Activity) mContext).isFinishing()) {
        dialog.show();
    }else{
        Log.d("htl","isFinishing DV");
    }
//                dialog.show();
}
    public  void createDialog(){
        createDialog("");
    }
    public static void createDialog(String name) {
//        context= IoManagerActivity.mContext;
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
        if (! ((Activity)mContext).isFinishing()) {
//            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.show();
        }

//            dialog.show();


    }
    public static void createTimePicker(final String topic, final String preString){
        try {
            new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String timeInMin = String.valueOf(hourOfDay*60 + minute);
                    String terminal2Send = preString.replace("@",timeInMin);
                    MqttConnectManager.sendData(topic,terminal2Send);

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
}
