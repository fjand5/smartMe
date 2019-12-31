package com.example.sm.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.example.sm.Model.ListDeviceInfo;
import com.example.sm.Presenter.DeviceListView.Adapter;
import com.example.sm.Presenter.DeviceListView.Item;
import com.example.sm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IoManagerActivity extends Activity{
    ListView ioDeviceLsv;

    Adapter deviceAdapter;
    List<Item> itemList;
    FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io_manager);
        initView();
        addEvent();

    }

    private void addEvent() {
        itemList = new ArrayList<>();
        deviceAdapter = Adapter.getInstance(this,R.layout.item_io_device,itemList);
        ioDeviceLsv.setAdapter(deviceAdapter);
        deviceAdapter.notifyDataSetInvalidated();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Adapter.getInstance().createDialog();

            }
        });
    }


    private void initView() {
        ioDeviceLsv = findViewById(R.id.ioDeviceLsv);
        floatingActionButton =findViewById(R.id.addDeviceBtn);
    }



}
