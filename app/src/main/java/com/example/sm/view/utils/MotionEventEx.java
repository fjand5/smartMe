package com.example.sm.view.utils;

import android.view.MotionEvent;

public class MotionEventEx {
    MotionEventEx motionEventEx;
    OnLisnerMotionEventEx _onLisnerMotionEvent;

    static boolean startAction=false;
    static float startX=-1;
    static float startY=-1;
    void setOnLisnerMotionEventEx(OnLisnerMotionEventEx onLisnerMotionEvent){
        _onLisnerMotionEvent=onLisnerMotionEvent;
    }
    private MotionEventEx(){};
    MotionEventEx getInstance(){
        if(motionEventEx == null)
            motionEventEx = new MotionEventEx();
        return motionEventEx;
    };
    public void passEvent(MotionEvent e){
        if(e.getAction() == MotionEvent.ACTION_DOWN){
            startAction = true;
            startX=e.getX();
            startY=e.getY();
        }
    }
    interface OnLisnerMotionEventEx{
        void onLeftSwipe (int y);
    }
}
