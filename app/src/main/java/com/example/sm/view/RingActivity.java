package com.example.sm.view;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.sm.Presenter.MqttConnectManager;
import com.example.sm.R;

import org.json.JSONException;
import org.json.JSONObject;


public class RingActivity extends Activity {
    Button gotitBtn;
    static Ringtone ringtone;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        initView();
        addEvent();
        settupRingtone();
    }

    private void settupRingtone() {
        ringtone = RingtoneManager.getRingtone(this,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        playRingtone();
    }

    private void addEvent() {
        gotitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String tmp = null;
                try {
                    tmp = new JSONObject().put("cmd","SBL").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MqttConnectManager.sendData("luat/espAL/rx",tmp);
                stopRingtone();
                finish();

            }
        });
    }

    private void initView() {
        gotitBtn = findViewById(R.id.gotitBtn);
    }
    static void playRingtone(){
        if(ringtone!=null)
            ringtone.play();
    }
    static void stopRingtone(){
        if(ringtone!=null)
            ringtone.stop();
    }

}
