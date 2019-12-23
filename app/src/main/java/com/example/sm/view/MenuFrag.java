package com.example.sm.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.fragment.app.Fragment;

import com.example.sm.R;

public class MenuFrag extends Fragment {
    Button alarmBtn;
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.frag_menu,container,false);

        alarmBtn = v.findViewById(R.id.alarmBtn);
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               callAlarmActivity();
            }
        });
        return v;
    }

    private void callAlarmActivity() {

            Intent i = new Intent(getActivity(), AlarmActivity.class);
            startActivity(i);

    }
}
