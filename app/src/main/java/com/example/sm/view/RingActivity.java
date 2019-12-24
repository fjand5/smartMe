package com.example.sm.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.sm.R;


public class RingActivity extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        Log.d("htl","RingActivity" );
    }
}
