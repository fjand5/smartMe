package com.example.sm.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.sm.Model.ListDeviceInfo;
import com.example.sm.Presenter.DeviceListView.Adapter;
import com.example.sm.Presenter.DeviceListView.Item;
import com.example.sm.Presenter.MqttConnectManager;
import com.example.sm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.eclipse.paho.client.mqttv3.MqttMessage;
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
    Context mContext;
    @Override
    protected void onResume() {
        runSignal = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (runSignal){
                    MqttConnectManager.sendData(getSharedPreferences().getString("topic",""),
                            getSharedPreferences().getString("content",""));
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        super.onResume();
    }

    @Override
    protected void onPause() {
        runSignal = false;
        super.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io_manager);
        initView();
        addEvent();
        mContext =this;

    }

    @SuppressLint("ClickableViewAccessibility")
    private void addEvent() {
        itemList = new ArrayList<>();
        deviceAdapter = Adapter.getInstance(this,R.layout.item_io_device,itemList);
        ioDeviceLsv.setAdapter(deviceAdapter);

        deviceAdapter.notifyDataSetInvalidated();

        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Adapter.getInstance().createDialog();
                return false;
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_setting_signal);
                final EditText topicSignalTxt = dialog.findViewById(R.id.topicSignalTxt);
                final EditText nameSignalTxt = dialog.findViewById(R.id.nameSignalTxt);
                topicSignalTxt.setText(getSharedPreferences().getString("topic",""));
                nameSignalTxt.setText(getSharedPreferences().getString("content",""));
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

        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString("topic",topic).commit();
        editor.putString("content",content).commit();

    }
    private SharedPreferences getSharedPreferences(){
        SharedPreferences  ret;
        ret = getSharedPreferences(
                IoManagerActivity.class.toString()
                ,MODE_PRIVATE);

        return ret;
    }


}
