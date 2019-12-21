package com.example.sm.Model;

import android.content.Context;

public interface IMqttInfo {
    String getAddress(Context context);
    int getPort(Context context);
    String getUsername(Context context);
    String getPassword(Context context);
    String getTopic(Context context);
    void setInfo(Context context, String addr, int port, String name, String pass, String topic);
}
