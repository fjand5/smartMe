package com.example.sm.Presenter.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.sm.view.RingActivity;

public class Utils {
    private Utils() {

    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void callActivity(Context curContext, Class<?> cls, boolean oneTime){
        ActivityManager am = (ActivityManager)curContext.getSystemService(curContext.ACTIVITY_SERVICE);
        String topAct = am.getRunningTasks(1).get(0).topActivity.getClassName();
        if(!oneTime){
            Intent i = new Intent(curContext, cls);
            curContext.startActivity(i);
            return;
        }
        if(!topAct.equals(cls.getName())){
            Intent i = new Intent(curContext, cls);
            curContext.startActivity(i);
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void callActivity(Context curContext, Class<?> cls){
        callActivity(curContext,cls,true);
    }
    public static int getHourFromTimeInMin(int timeInmin){
        return timeInmin/60;
    }
    public static int getMinFromTimeInMin(int timeInmin){
        return timeInmin%60;
    }

}
