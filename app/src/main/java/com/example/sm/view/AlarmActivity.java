package com.example.sm.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
    public static Context mContext;
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
        mContext = this;
        setContentView(R.layout.activity_alarm_manager);
        initView();
        addEvent();



    }



    private void addEvent() {
        addAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog();
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
    public static void confirmDialog(final String name){
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("Xác nhận !")
                .setMessage("Bạn có muốn xóa cảnh báo này không ?")
                .setIcon(R.drawable.icon)
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Adapter.getInstance().removeAlarm(name);
                        Adapter.getInstance().syncAlarm();
                    }
                });
        dialog.create();
        if (! ((Activity) mContext).isFinishing()) {
            dialog.show();
        }
//                    dialog.show();
    }
    public static void createDialog(){

        createDialog("");
    }
    public static void createDialog(String name) {
        final Dialog dialog = new Dialog(mContext);

        String _topic="",_name="",_content="",_respone="";
        boolean wanaEdit=false;
        final Adapter adapterNullAble = Adapter.getInstance();
        JSONObject deviceJsonObj = adapterNullAble.getAlarm(name);
        if(!name.equals("")){
            wanaEdit = true;
            try {
                _topic=deviceJsonObj.getString("topic");
                _name=deviceJsonObj.getString("name");
                _content=deviceJsonObj.getString("content");
                _respone=deviceJsonObj.getString("respone");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        dialog.setContentView(R.layout.dialog_add_alarm);
        final EditText nameAddAlarmTxt = dialog.findViewById(R.id.nameAddAlarmTxt);
        final EditText topicAddAlarmTxt = dialog.findViewById(R.id.topicAddAlarmTxt);
        final EditText contentAddAlarmTxt = dialog.findViewById(R.id.contentAddAlarmTxt);
        final EditText responeAddAlarmTxt = dialog.findViewById(R.id.responeAddAlarmTxt);

        Button addAddAlarmBtn = dialog.findViewById(R.id.addAddAlarmBtn);
        Button cloneAddAlarmBtn = dialog.findViewById(R.id.cloneAddAlarmBtn);
        nameAddAlarmTxt.setText(_name);
        topicAddAlarmTxt.setText(_topic);
        contentAddAlarmTxt.setText(_content);
        responeAddAlarmTxt.setText(_respone);

        final boolean finalWanaEdit = wanaEdit;
        final String final_name = _name;
        addAddAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(adapterNullAble != null){
                    if(finalWanaEdit){
                        adapterNullAble.editAlarm(final_name,nameAddAlarmTxt.getText().toString(),
                                topicAddAlarmTxt.getText().toString(),
                                contentAddAlarmTxt.getText().toString(),
                                responeAddAlarmTxt.getText().toString());
                    }else{
                        adapterNullAble.addAlarm(nameAddAlarmTxt.getText().toString(),
                                topicAddAlarmTxt.getText().toString(),
                                contentAddAlarmTxt.getText().toString(),
                                responeAddAlarmTxt.getText().toString());
                    }

                }
                dialog.cancel();

            }
        });
        cloneAddAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adapterNullAble != null){
                    adapterNullAble.addAlarm(nameAddAlarmTxt.getText().toString()+ " - coppy",
                            topicAddAlarmTxt.getText().toString(),
                            contentAddAlarmTxt.getText().toString(),
                            responeAddAlarmTxt.getText().toString());
                }
                dialog.cancel();
            }
        });

        if (! ((Activity) mContext).isFinishing()) {
            dialog.show();
        }
    }

}
