package com.example.sm.Presenter.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.sm.view.RingActivity;

public class Utils {
    private Utils() {

    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void callActivity(Context curContext, Class<?> cls, boolean oneTime){
        ActivityManager am = (ActivityManager)curContext.getSystemService(curContext.ACTIVITY_SERVICE);
        String topAct = am.getRunningTasks(22).get(0).topActivity.getClassName();

        if(oneTime
        && !topAct.equals(RingActivity.class.getName())){

            Intent i = new Intent(curContext, cls);
            curContext.startActivity(i);
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void callActivity(Context curContext, Class<?> cls){
        callActivity(curContext,cls,true);
    }

}