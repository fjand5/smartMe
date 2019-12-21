package com.example.sm.view;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.sm.R;
import com.example.sm.BackgroudProccess.MainService;
import com.example.sm.BackgroudProccess.MqttBroadcast;

import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MainActivity extends Activity implements View.OnClickListener {
    ImageView settingImg,statusImg;
    TextView statusTxt;
    ProgressBar statusBar;


    ListView rxDataLsv;
    EditText topicDataTxt;
    EditText txDataTxt;
    Button sendBtn;


    @Override
    protected void onStart() {
        super.onStart();
        if(topicDataTxt.getText().equals("")){
            String topic;
            SharedPreferences MqttInfo = getSharedPreferences(SettingActivity.class.getName(),MODE_PRIVATE);
            topic = MqttInfo.getString("nameTxt","#");
            topicDataTxt.setText(topic+"/");
        }


    }

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


            }
        }));

    }

    private void addEvent() {
        sendBtn.setOnClickListener(this);
        settingImg.setOnClickListener(this);
    }

    private void initView() {
        statusImg = findViewById(R.id.statusImg);
        statusBar = findViewById(R.id.statusBar);
        statusTxt = findViewById(R.id.statusTxt);
        settingImg = findViewById(R.id.settingImg);

        rxDataLsv = findViewById(R.id.rxDataLsv);
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
        txDataTxt.setText("");
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
