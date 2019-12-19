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


public class SettingActivity extends Activity implements TextWatcher, View.OnClickListener {
    EditText addrTxt,portTxt,nameTxt,passTxt;
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
                break;
            case R.id.exitBtn:
                finish();
                Toast.makeText(this,getResources().getString(R.string.notSave),Toast.LENGTH_SHORT).show();
            break;
        }

    }

    private void saveInfo() {

        MqttInfo = getSharedPreferences(SettingActivity.class.getName(),MODE_PRIVATE);
        MqttInfo.edit().putString("addrTxt",addrTxt.getText().toString())
                .putString("portTxt",addrTxt.getText().toString())
                .putString("nameTxt",addrTxt.getText().toString())
                .putString("passTxt",addrTxt.getText().toString()).apply();

    }
    private void updateData() {
        MqttInfo = getSharedPreferences(SettingActivity.class.getName(),MODE_PRIVATE);
        addrTxt.setText(MqttInfo.getString("addrTxt",""));
        portTxt.setText(MqttInfo.getString("portTxt",""));
        nameTxt.setText(MqttInfo.getString("nameTxt",""));
        passTxt.setText(MqttInfo.getString("passTxt",""));

    }
}
