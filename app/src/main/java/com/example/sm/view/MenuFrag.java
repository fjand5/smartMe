package com.example.sm.view;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.sm.R;

import static com.example.sm.Presenter.Utils.Utils.callActivity;

public class MenuFrag extends Fragment {
    Button alarmBtn,ioManagerBtn;
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.frag_menu,container,false);

        alarmBtn = v.findViewById(R.id.alarmBtn);
        ioManagerBtn = v.findViewById(R.id.ioManagerBtn);
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
               callAlarmActivity(view.getContext());
            }
        });
        ioManagerBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                callIoManagerActivity(view.getContext());
            }
        });
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void callAlarmActivity(Context context) {
        callActivity(context,AlarmActivity.class);


    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void callIoManagerActivity(Context context) {
        callActivity(context,IoManagerActivity.class);

    }
}
