package com.example.sm.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sm.Presenter.MqttConnectManager;
import com.example.sm.R;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;


public class AlarmActivity extends Activity {
    TextView curDeltaTxt;
    EditText desDeltaTxt;
    Button setDeltaBtn;

    Thread thread;
    volatile boolean stop = false;
    MqttConnectManager.Callback callback;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            stop=true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        MqttConnectManager.getInstance().removeOnEventMqtt(callback);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_manager);
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();

        setThread();
        callback = new MqttConnectManager.Callback() {
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
                    JSONObject jsonObject  =new JSONObject(content);
                    if(jsonObject.has("cmd")
                            && jsonObject.get("cmd").equals("ALR")){
                        // onAlarm

                        String tmp = new JSONObject().put("cmd","UAL")
                                .put("time",30000).toString();
                        MqttConnectManager.sendData("luat/espAL/rx",tmp);

                    }
                    if(jsonObject.has("cmd")
                            && jsonObject.get("cmd").equals("GDT")){
                        float val = jsonObject.getInt("val");
                        curDeltaTxt.setText(String.valueOf(val));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        addEvent();
    }

    private void setThread() {
        stop = false;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!stop){
                    String tmp = null;
                    try {
                        tmp = new JSONObject().put("cmd","GDT").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MqttConnectManager.sendData("luat/espAL/rx",tmp);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        thread.start();
    }

    private void addEvent() {
        curDeltaTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    String tmp = null;
                    try {
                        tmp = new JSONObject().put("cmd","SBL").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MqttConnectManager.sendData("luat/espAL/rx",tmp);


            }
        });
        setDeltaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmp = null;
                try {
                    tmp = new JSONObject().put("cmd","SDT")
                            .put("delta",desDeltaTxt.getText()).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MqttConnectManager.sendData("luat/espAL/rx",tmp);
            }
        });
        MqttConnectManager.getInstance().setOnEventMqtt(callback);
    }

    private void initView() {
        curDeltaTxt =  findViewById(R.id.curDeltaTxt);
        desDeltaTxt = findViewById(R.id.desDeltaTxt);
        setDeltaBtn = findViewById(R.id.setDeltaBtn);
    }
}
