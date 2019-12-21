package com.example.sm.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.sm.R;

public class DetailMessageActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_message);

        TextView tp = findViewById(R.id.topicTxt);
        TextView ct = findViewById(R.id.contentTxt);
        ct.setText(getIntent().getStringExtra("content"));
        tp.setText(getIntent().getStringExtra("topic"));
        Button ok = findViewById(R.id.okBtn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
