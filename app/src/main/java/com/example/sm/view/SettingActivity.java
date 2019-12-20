package com.example.sm.view;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sm.R;
import com.example.sm.backgroudProc.MqttBroadcast;

import org.eclipse.paho.client.mqttv3.MqttException;


public class SettingActivity extends Activity implements TextWatcher, View.OnClickListener {
    EditText addrTxt,portTxt,nameTxt,passTxt,topicTxt;
    TextView checkTxt;
    Button saveBtn,exitBtn;

    SharedPreferences MqttInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        addEvent();
        updateData();
        checkInfo();

    }



    private void addEvent() {
        addrTxt.addTextChangedListener(this);
        portTxt.addTextChangedListener(this);
        nameTxt.addTextChangedListener(this);
        passTxt.addTextChangedListener(this);
        saveBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);

    }

    private void initView() {
        checkTxt = findViewById(R.id.checkTxt);

        addrTxt = findViewById(R.id.addrTxt);
        portTxt = findViewById(R.id.portTxt);
        nameTxt = findViewById(R.id.nameTxt);
        passTxt = findViewById(R.id.passTxt);
        topicTxt = findViewById(R.id.topicTxt);

        saveBtn = findViewById(R.id.saveBtn);
        exitBtn = findViewById(R.id.exitBtn);

    }

    void checkInfo(){
        if(addrTxt.getText().length()>0
        && portTxt.getText().length()>0
        && nameTxt.getText().length()>0
        && passTxt.getText().length()>0
        ){
            saveBtn.setEnabled(true);
            checkTxt.setText(
                    ""
            );

        }
        else{
            saveBtn.setEnabled(false);
            checkTxt.setText(getResources().getString(R.string.fillInfo));
            checkTxt.setTextColor(getResources().getColor(R.color.red));
        }
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        checkInfo();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.saveBtn:
                saveInfo();
                Toast.makeText(this, getResources().getString(R.string.saved),Toast.LENGTH_LONG).show();
                finish();
                break;
            case R.id.exitBtn:
                finish();
                Toast.makeText(this,getResources().getString(R.string.notSave),Toast.LENGTH_SHORT).show();
            break;
        }

    }

    private void saveInfo() {

        MqttInfo = getSharedPreferences(SettingActivity.class.getName(),MODE_PRIVATE);
        SharedPreferences.Editor editor = MqttInfo.edit();
        editor.putString("addrTxt",addrTxt.getText().toString()).commit();
        editor.putString("portTxt",portTxt.getText().toString()).commit();
        editor.putString("nameTxt",nameTxt.getText().toString()).commit();
        editor.putString("passTxt",passTxt.getText().toString()).commit();
        editor.putString("topicTxt",topicTxt.getText().toString()).commit();

        try {
            MqttBroadcast.connectNow=true;
            MqttBroadcast.client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }
    private void updateData() {
        MqttInfo = getSharedPreferences(SettingActivity.class.getName(),MODE_PRIVATE);
        addrTxt.setText(MqttInfo.getString("addrTxt",""));
        portTxt.setText(MqttInfo.getString("portTxt",""));
        nameTxt.setText(MqttInfo.getString("nameTxt",""));
        passTxt.setText(MqttInfo.getString("passTxt",""));
        topicTxt.setText(MqttInfo.getString("topicTxt",""));

    }


}
