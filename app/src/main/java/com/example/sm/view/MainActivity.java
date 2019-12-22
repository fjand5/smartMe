package com.example.sm.view;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.sm.Presenter.MqttSetting;
import com.example.sm.Presenter.RxDataListView.Adapter;
import com.example.sm.Presenter.RxDataListView.Item;
import com.example.sm.R;
import com.example.sm.BackgroudProccess.MainService;
import com.example.sm.BackgroudProccess.MqttBroadcast;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {
    ImageView settingImg,statusImg;
    TextView statusTxt;
    ProgressBar statusBar;


    ListView rxDataLsv;
    Adapter adapter;

    EditText topicDataTxt;
    EditText txDataTxt;
    Button sendBtn;


    @Override
    protected void onStart() {
        super.onStart();
            String topic  =  MqttSetting.getInstance().getInfo(this).get("topic").toString();

            topicDataTxt.setText(topic);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initView();
        addEvent();
        MainService.beginService(this);
        setStatusView(Adapter.getStatus());
    }

    private void addEvent() {
        sendBtn.setOnClickListener(this);
        settingImg.setOnClickListener(this);
        adapter.setOnEventMqtt(new Adapter.Callback() {
            @Override
            public void onDisconnect() {
                setStatusView(false);
            }

            @Override
            public void onConnect() {
                setStatusView(true);
            }

        });
    }

    private void initView() {
        statusImg = findViewById(R.id.statusImg);
        statusBar = findViewById(R.id.statusBar);
        statusTxt = findViewById(R.id.statusTxt);
        settingImg = findViewById(R.id.settingImg);

        rxDataLsv = findViewById(R.id.rxDataLsv);


        adapter = Adapter.getInstance(this,R.layout.item_rx_data,new ArrayList<Item>());
        rxDataLsv.setAdapter(adapter);



        topicDataTxt = findViewById(R.id.topicDataTxt);
        txDataTxt = findViewById(R.id.txDataTxt);
        sendBtn = findViewById(R.id.sendBtn);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.settingImg:
                callSettingActivity();
                break;
            case R.id.sendBtn:
                sendData();
                break;
        }

    }

    private void sendData() {
        Adapter.sendData(topicDataTxt.getText().toString(),txDataTxt.getText().toString());
    }

    private void callSettingActivity() {
        Intent i = new Intent(this,SettingActivity.class);
        startActivity(i);
    }
    void setStatusView(boolean isConnect){
        if(isConnect){
            statusImg.setVisibility(View.VISIBLE);
            statusBar.setVisibility(View.INVISIBLE);
            statusTxt.setText(getResources().getText(R.string.isConnecting));
            statusTxt.setTextColor(getResources().getColor(R.color.green));
        }else{
            statusImg.setVisibility(View.INVISIBLE);
            statusBar.setVisibility(View.VISIBLE);
            statusTxt.setText(getResources().getText(R.string.notConnect));
            statusTxt.setTextColor(getResources().getColor(R.color.red));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN
                && event.getX()<getResources().getDimension(R.dimen.padSize)){
            Log.d("htl","ggg");
        }
        return super.onTouchEvent(event);
    }
}
