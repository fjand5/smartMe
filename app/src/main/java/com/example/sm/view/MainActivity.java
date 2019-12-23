package com.example.sm.view;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.sm.Presenter.MqttConnectManager;
import com.example.sm.Presenter.MqttSetting;
import com.example.sm.Presenter.RxDataListView.Adapter;
import com.example.sm.Presenter.RxDataListView.Item;
import com.example.sm.R;
import com.example.sm.BackgroudProccess.MainService;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

import static android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        GestureDetector.OnGestureListener {
    private static final String DEBUG_TAG = "htl";
    ImageView settingImg,statusImg;
    TextView statusTxt;
    ProgressBar statusBar;


    ListView rxDataLsv;
    Adapter adapter;

    EditText topicDataTxt;
    EditText txDataTxt;
    Button sendBtn;


    MenuFrag menuFrag;
    View menuPos;
    GestureDetector detector;

    MqttConnectManager.Callback callback;
    @Override
    protected void onStart() {
        super.onStart();
            String topic  =  MqttSetting.getInstance().getInfo(this).get("topic").toString();

            topicDataTxt.setText(topic);
    }
    @Override
    protected void onPause() {
        MqttConnectManager.getInstance().removeOnEventMqtt(callback);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addEvent();
        MainService.beginService(this);
        setStatusView(Adapter.getStatus());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        detector = new GestureDetector(this,this);
        initView();

    }

    private void addEvent() {
        sendBtn.setOnClickListener(this);
        settingImg.setOnClickListener(this);
        callback = new MqttConnectManager.Callback() {
            @Override
            public void onDisconnect() {
                setStatusView(false);
            }

            @Override
            public void onConnect() {
                setStatusView(true);
            }

            @Override
            public void onMessageArrived(String topic, MqttMessage message) {

            }

        };
        MqttConnectManager.getInstance().setOnEventMqtt(callback);
    }

    private void initView() {
        getSupportActionBar().hide();
        menuFrag = new MenuFrag();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragMenuPos,menuFrag,MenuFrag.class.getName()).commit();

        statusImg = findViewById(R.id.statusImg);
        statusBar = findViewById(R.id.statusBar);
        statusTxt = findViewById(R.id.statusTxt);
        settingImg = findViewById(R.id.settingImg);

        rxDataLsv = findViewById(R.id.rxDataLsv);


        adapter = Adapter.getInstance(this,R.layout.item_rx_data,new ArrayList<Item>());
        rxDataLsv.setAdapter(adapter);


        topicDataTxt = findViewById(R.id.topicDataTxt);
        txDataTxt = findViewById(R.id.txDataTxt);
        sendBtn = findViewById(R.id.sendBtn);

        menuPos = findViewById(R.id.fragMenuPos);


    }

    private void showMenu() {
        View view = this.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), HIDE_IMPLICIT_ONLY);
        }
        menuPos.setLayoutParams(new LinearLayout.LayoutParams(
                (int) (150*getResources().getDisplayMetrics().density),
                ViewGroup.LayoutParams.MATCH_PARENT));
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.show_menu);
        menuPos.startAnimation(animation);



    }
    private void hideMenu() {


        Animation animation = AnimationUtils.loadAnimation(this, R.anim.hide_menu);
        menuPos.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menuPos.setLayoutParams(new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.settingImg:
                callSettingActivity();
                break;
            case R.id.sendBtn:
                hideMenu();
                sendData();
                break;
        }

    }

    private void sendData() {
        MqttConnectManager.getInstance().sendData(topicDataTxt.getText().toString(),txDataTxt.getText().toString());
    }

    private void callSettingActivity() {
        Intent i = new Intent(this,SettingActivity.class);
        startActivity(i);
    }
    void setStatusView(boolean isConnect){
        if(isConnect){
            statusImg.setVisibility(View.VISIBLE);
            statusBar.setVisibility(View.INVISIBLE);
            statusTxt.setText(getResources().getText(R.string.isConnecting));
            statusTxt.setTextColor(getResources().getColor(R.color.green));
        }else{
            statusImg.setVisibility(View.INVISIBLE);
            statusBar.setVisibility(View.VISIBLE);
            statusTxt.setText(getResources().getText(R.string.notConnect));
            statusTxt.setTextColor(getResources().getColor(R.color.red));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {

        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

        if(v<0 &&
                Math.abs(motionEvent.getY() - motionEvent1.getY()) <50){
            hideMenu();
        }
        if(v>0 &&
                Math.abs(motionEvent.getY() - motionEvent1.getY()) <50){
            showMenu();
        }

        if(v1>0 &&
                Math.abs(motionEvent.getX() - motionEvent1.getX()) <50){
            Adapter.clearData();

        }
//        Log.d("htl",Float.toString(v1));
        return false;
    }
}
