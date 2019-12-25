package com.example.sm.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.example.sm.Presenter.DeviceListView.Adapter;
import com.example.sm.Presenter.DeviceListView.Item;
import com.example.sm.R;

import java.util.ArrayList;
import java.util.List;

public class IoManagerActivity extends Activity {
    ListView ioDeviceLsv;

    Adapter deviceAdapter;
    List<Item> itemList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io_manager);
        ioDeviceLsv = findViewById(R.id.ioDeviceLsv);

        itemList = new ArrayList<>();

        deviceAdapter = Adapter.getInstance(this,R.layout.item_io_device,itemList);
        ioDeviceLsv.setAdapter(deviceAdapter);
        deviceAdapter.notifyDataSetInvalidated();



    }
}
