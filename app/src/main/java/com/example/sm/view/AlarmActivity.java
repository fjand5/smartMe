package com.example.sm.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.sm.InitSystem;
import com.example.sm.Presenter.MqttConnectManager;
import com.example.sm.R;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.sm.Presenter.Utils.Utils.callActivity;


public class AlarmActivity extends Activity {
    TextView curDeltaTxt;
    EditText desDeltaTxt;
    Button setDeltaBtn,calibBtn;

    MqttConnectManager.Callback callback;

    @Override
    protected void onPause() {
        InitSystem.setSendSignalFlag(false);
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
        InitSystem.setSendSignalFlag(true);
        callback = new MqttConnectManager.Callback() {
            @Override
            public void onDisconnect() {

            }

            @Override
            public void onConnect() {

            }

            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onMessageArrived(String topic, MqttMessage message) {
                String content = new String(message.getPayload());
                Log.d("htl",content);

                try {
                    JSONObject jsonObject  =new JSONObject(content);
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



    private void addEvent() {
        calibBtn.setOnClickListener(new View.OnClickListener() {
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
        calibBtn = findViewById(R.id.calibBtn);
    }

}
