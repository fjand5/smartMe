package com.example.sm;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.sm.view.SettingActivity;


public class MainActivity extends Activity implements View.OnClickListener {
    ImageView settingImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
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
