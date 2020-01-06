package com.example.sm.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.sm.Presenter.AlarmListView.Adapter;
import com.example.sm.Presenter.AlarmListView.Item;
import com.example.sm.Presenter.AlarmSetting;
import com.example.sm.Presenter.MqttConnectManager;
import com.example.sm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.sm.Presenter.Utils.Utils.callActivity;


public class AlarmActivity extends Activity {
   FloatingActionButton addAlarmBtn;
   ListView alarmLsv;
   List<Item> alarmItems;
   Adapter  alarmAdapter;

    @Override
    protected void onPause() {



        super.onPause();
    }

    @Override

    protected void onResume() {

        super.onResume();
        alarmItems = new ArrayList<>();
        alarmAdapter = Adapter.getInstance(this, R.layout.item_alarm,alarmItems);

        alarmLsv.setAdapter(alarmAdapter);
        alarmAdapter.notifyDataSetInvalidated();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_manager);
        initView();
        addEvent();



    }



    private void addEvent() {
        addAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Adapter.createDialog(view.getContext());
            }
        });
    }

    private void initView() {
        addAlarmBtn =  findViewById(R.id.addAlarmBtn);
        alarmLsv = findViewById(R.id.alarmLsv);



    }

    public static void initAlarmSystem(final Context context){
        MqttConnectManager.getInstance().setOnEventMqtt(new MqttConnectManager.Callback() {
            @Override
            public void onDisconnect() {

            }

            @Override
            public void onConnect() {

            }

            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onMessageArrived(String topic, MqttMessage message) {
                if(message.isRetained())
                    return;
                    String content = new String(message.getPayload());
                    JSONArray jsonArray = AlarmSetting.getInstance().getListAlarm();

                    for (int i = 0; i<jsonArray.length(); i++){
                    try {
                        String alarmTopic = jsonArray.getJSONObject(i).getString("topic");
                        String alarmContent = jsonArray.getJSONObject(i).getString("content");
                         String name = jsonArray.getJSONObject(i).getString("name");
                        if(alarmTopic.equals(topic)
                        && alarmContent.equals(content)){
                            callRingActiity(context, name);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static void callRingActiity(Context context, String msg) {
        callActivity(context, RingActivity.class,msg,true);

    }
}
