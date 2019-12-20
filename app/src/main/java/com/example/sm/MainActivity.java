package com.example.sm;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.se.omapi.SEService;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.sm.backgroudProc.MainService;
import com.example.sm.backgroudProc.MqttBroadcast;
import com.example.sm.view.SettingActivity;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.w3c.dom.Text;

import static android.widget.Toast.LENGTH_LONG;


public class MainActivity extends Activity implements View.OnClickListener {
    ImageView settingImg,statusImg;
    TextView statusTxt;
    ProgressBar statusBar;

    TextView rxDataTxt;
    EditText topicDataTxt;
    EditText txDataTxt;
    Button sendBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initView();
        addEvent();
        MainService.beginService(this);


        setStatusView(MqttBroadcast.setOnConnectStatusChange(new MqttBroadcast.OnConnectStatusChange() {
            @Override
            public void onDisconnect() {
                setStatusView(false);
                
            }

            @Override
            public void onConnect() {
                setStatusView(true);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String content = new String(message.getPayload());
                if(message.isRetained())
                    topic = "*" + topic;
                rxDataTxt.append(topic+" : "+ content + "\n\r\n\r");

            }
        }));

    }

    private void addEvent() {
        sendBtn.setOnClickListener(this);
        rxDataTxt.setMovementMethod(new ScrollingMovementMethod());
        settingImg.setOnClickListener(this);
    }

    private void initView() {
        statusImg = findViewById(R.id.statusImg);
        statusBar = findViewById(R.id.statusBar);
        statusTxt = findViewById(R.id.statusTxt);
        settingImg = findViewById(R.id.settingImg);

        rxDataTxt = findViewById(R.id.rxDataTxt);
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
        Log.d("htl",topicDataTxt.getText().toString());
        MqttBroadcast.publish(topicDataTxt.getText().toString(),txDataTxt.getText().toString());
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
}
