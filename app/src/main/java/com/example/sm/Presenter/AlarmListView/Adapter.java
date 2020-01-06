package com.example.sm.Presenter.AlarmListView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.sm.BackgroudProccess.MainService;
import com.example.sm.BackgroudProccess.MqttBroadcast;
import com.example.sm.Presenter.AlarmSetting;
import com.example.sm.Presenter.IoManagerSetting;
import com.example.sm.Presenter.ListDeviceInfo;
import com.example.sm.Presenter.MqttConnectManager;
import com.example.sm.R;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Adapter extends ArrayAdapter<Item> {
    static Adapter instance;
    Context mContext;
    List<Item> listItem;
    Adapter(@NonNull Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
        mContext = context;
        listItem = objects;

    }

    public static Adapter getInstance(Context context, int resource, List<Item> objects) {
        if(instance == null)
            instance=new Adapter(context, resource, objects);

        AlarmSetting.getInstance().setOnUpdateSettingDataListenner(new AlarmSetting.OnUpdateSettingDataListenner() {
            @Override
            public void onUpdateSettingData() {
                instance.syncAlarm();
            }
        });
        MqttConnectManager.getInstance().setOnEventMqtt(new MqttConnectManager.Callback() {
            @Override
            public void onDisconnect() {

            }

            @Override
            public void onConnect() {

            }

            @Override
            public void onMessageArrived(String topic, MqttMessage message) {
                instance.syncAlarm();
            }
        });

        return instance;
    }
    public static Adapter getInstance() {
        return instance;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if(v == null){
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            v = layoutInflater.inflate(R.layout.item_alarm,null);
            final TextView name = v.findViewById(R.id.alarmNameTxt);
            final Button deleteAlarmBtn = v.findViewById(R.id.deleteAlarmBtn);
            name.setText(listItem.get(position).name);
            deleteAlarmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                                    Adapter.getInstance().removeAlarm(name.getText().toString());
                                    syncAlarm();
                                }
                            });
                    dialog.create();
                    dialog.show();
                }
            });
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createDialog(name.getText().toString());
                }
            });
        }
        return v;
    }

    public void addAlarm(String name, String topic, String content, String respone){
        AlarmSetting.getInstance().addAlarm(name,topic,content,respone);
        syncAlarm();
    }
    public void editAlarm(String oldName, String name, String topic, String content,String respone){
        AlarmSetting.getInstance().editAlarm(oldName, name,topic,content,respone);
        syncAlarm();
    }

    public JSONObject getAlarm(String name){
        return AlarmSetting.getInstance().getAlarm(name);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void removeAlarm(String name){
        AlarmSetting.getInstance().removeAlarm(name);
    }
    public  void syncAlarm(){
        JSONArray tmp = AlarmSetting.getInstance().getListAlarm();
        listItem.clear();
        for (int i = 0; i<tmp.length();i++) {
            try {
                JSONObject jsonObject  = tmp.getJSONObject(i);
                listItem.add(new Item(
                        jsonObject.getString("name"),
                        jsonObject.getString("topic"),
                        jsonObject.getString("content"),
                        jsonObject.getString("respone")
                ));
            } catch (JSONException er) {
                er.printStackTrace();
            }
        }
        notifyDataSetInvalidated();
    }


    public void createDialog(Context context){

        createDialog("");
    }
    public void createDialog(String name) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_add_alarm);
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


        dialog.show();

    }
}
