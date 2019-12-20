package com.example.sm;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.sm.backgroudProc.MainService;
import com.example.sm.backgroudProc.MqttBroadcast;
import com.example.sm.view.SettingActivity;

import org.w3c.dom.Text;

import static android.widget.Toast.LENGTH_LONG;


public class MainActivity extends Activity implements View.OnClickListener {
    ImageView settingImg,statusImg;
    TextView statusTxt;
    ProgressBar statusBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initView();

        MainService.beginService(this);

    }

    private void initView() {
        statusImg = findViewById(R.id.statusImg);
        statusBar = findViewById(R.id.statusBar);
        statusTxt = findViewById(R.id.statusTxt);
        settingImg = findViewById(R.id.settingImg);
        settingImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.settingImg:
                callSettingActivity();
                break;
        }

    }

    private void callSettingActivity() {
        Intent i = new Intent(this,SettingActivity.class);
        startActivity(i);
    }
}
