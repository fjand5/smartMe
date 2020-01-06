package com.example.sm.Presenter.Utils;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.sm.Presenter.DeviceListView.Adapter;
import com.example.sm.R;
import com.example.sm.view.RingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Utils {

    private Utils() {

    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void callActivity(Context curContext, Class<?> cls,String msg ,boolean oneTime){
        ActivityManager am = (ActivityManager)curContext.getSystemService(curContext.ACTIVITY_SERVICE);
        String topAct = am.getRunningTasks(1).get(0).topActivity.getClassName();
        if(!oneTime){
            Intent i = new Intent(curContext, cls);
            i.putExtra("msg",msg);
            curContext.startActivity(i);
            return;
        }
        if(!topAct.equals(cls.getName())){
            Intent i = new Intent(curContext, cls);
            i.putExtra("msg",msg);
            curContext.startActivity(i);
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void callActivity(Context curContext, Class<?> cls){
        callActivity(curContext,cls,"",true);
    }
    public static int getHourFromTimeInMin(int timeInmin){
        return timeInmin/60;
    }
    public static int getMinFromTimeInMin(int timeInmin){
        return timeInmin%60;
    }
    public static String getTimeStringFromTimeInMin(int timeInmin){
        int h = getHourFromTimeInMin(timeInmin);
        int m =  getMinFromTimeInMin(timeInmin);
        String sh="";
        if(h<10)
            sh = "0"+h;
        else
            sh = ""+h;
        String sm="";
        if(m<10)
            sm = "0"+m;
        else
            sm = ""+m;
        return sh + ":" + sm;
    }
    public static boolean isValidJsonArray(String jsonStr) {
        Object json = null;
        try {
            json = new JSONTokener(jsonStr).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json instanceof JSONArray) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isValidJsonObject(String jsonStr) {
        Object json = null;
        try {
            json = new JSONTokener(jsonStr).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json instanceof JSONObject) {
            return true;
        } else {
            return false;
        }
    }


}
