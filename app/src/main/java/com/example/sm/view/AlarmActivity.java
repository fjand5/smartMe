package com.example.sm.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    TextView statusResTxt;
    TextView curThresholdTxt;
    EditText desDeltaTxt;
    Button setDeltaBtn,calibBtn;

    MqttConnectManager.Callback callback;
    CountDownTimer countDownTimer;
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
        countDownTimer = new CountDownTimer(5000,5000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                statusResTxt.setText("Mất kết nối với thiết bị");
            }

        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        MqttConnectManager.sendData("luat/espAL/rx","{\"cmd\":\"GOJ\",\"obj\":\"\"}");
        InitSystem.setSendSignalFlag(true);
        countDownTimer.start();
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
                countDownTimer.cancel();
                statusResTxt.setText("Đang kết nối");
                countDownTimer.start();
                String content = new String(message.getPayload());
                Log.d("htl",content);

                try {
                    JSONObject jsonObject  =new JSONObject(content);
                    if(jsonObject.has("cmd")
                            && jsonObject.get("cmd").equals("GDT")){
                        float val = jsonObject.getInt("val");
                        curDeltaTxt.setText(String.valueOf(val));

                    } if(jsonObject.has("MPU")){
                        JSONObject mpuObj = jsonObject.getJSONObject("MPU");
                        float val = mpuObj.getInt("delta");
                        curThresholdTxt.setText(String.valueOf(val));
                    }



                    }catch (JSONException e) {
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
                RingActivity.ringtone.stop();


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

                new CountDownTimer(100,100){

                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        MqttConnectManager.sendData("luat/espAL/rx","{\"cmd\":\"GOJ\",\"obj\":\"\"}");

                    }
                }.start();
            }
        });
        MqttConnectManager.getInstance().setOnEventMqtt(callback);
    }

    private void initView() {
        curDeltaTxt =  findViewById(R.id.curDeltaTxt);
        curThresholdTxt =  findViewById(R.id.curThresholdTxt);
        desDeltaTxt = findViewById(R.id.desDeltaTxt);
        statusResTxt = findViewById(R.id.statusResTxt);
        setDeltaBtn = findViewById(R.id.setDeltaBtn);
        calibBtn = findViewById(R.id.calibBtn);
    }

}
