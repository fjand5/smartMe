package com.example.sm.Presenter.RxDataListView;


import android.content.Context;
import android.content.Intent;
import android.os.Build;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;

import com.example.sm.BackgroudProccess.MainService;
import com.example.sm.BackgroudProccess.MqttBroadcast;
import com.example.sm.Presenter.MqttConnectManager;
import com.example.sm.Presenter.MqttSetting;
import com.example.sm.R;
import com.example.sm.view.DetailMessageActivity;
import com.example.sm.view.SettingActivity;


import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class Adapter  extends ArrayAdapter<Item> {
    Context mContext;
    static List<Item> listItem;
    static Adapter instance;


    static public Adapter getInstance(Context context, int resource, List<Item> objects){
        if(instance == null)
            instance = new Adapter(context,resource,objects);
        return instance;
    }
    private Adapter(Context context, int resource,List<Item> objects) {
        super(context, resource, objects);
        mContext = context;
        listItem =objects;

//        MqttConnectManager.getInstance().setOnEventMqtt(new MqttConnectManager.Callback() {
//            @Override
//            public void onDisconnect() {
//
//            }
//
//            @Override
//            public void onConnect() {
//
//            }
//
//            @Override
//            public void onMessageArrived(String topic, MqttMessage message) {
//                if(listItem !=null){
//                    listItem.add(new Item(message.isRetained(), topic, new String(message.getPayload())));
//                    Adapter.instance.notifyDataSetInvalidated();
//                }
//            }
//        });

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if(v == null){
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            v = layoutInflater.inflate(R.layout.item_rx_data,null);

        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, DetailMessageActivity.class);
                i.putExtra("topic",listItem.get(position).getTopic());
                i.putExtra("content",listItem.get(position).getContent());
                mContext.startActivity(i);

            }
        });
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
                
            }
        });

        TextView tp = v.findViewById(R.id.topicItemTxt);
        TextView ct = v.findViewById(R.id.contentItemTxt);
        ct.setText(listItem.get(position).getContent());
        tp.setText(listItem.get(position).getTopic());
        return v;
    }

    public static void clearData(){
        if(listItem !=null){
            listItem.clear();
            Adapter.instance.notifyDataSetInvalidated();
        }
    }
    public static boolean getStatus(){
        return MqttBroadcast.getStatus();
    }

}
