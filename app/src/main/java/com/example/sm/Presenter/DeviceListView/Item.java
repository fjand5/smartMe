package com.example.sm.Presenter.DeviceListView;

public class Item {
    private String name;
    private String topic;
    private String cmdOn;
    private String cmdOff;
    private String beginTime;
    private String endTime;

    private int curBeginTime;
    private int curEndTime;
    public String getBeginTime() {
        return beginTime;
    }

    public int getCurBeginTime() {
        return curBeginTime;
    }

    public void setCurBeginTime(int curBeginTime) {
        this.curBeginTime = curBeginTime;
    }

    public int getCurEndTime() {
        return curEndTime;
    }

    public void setCurEndTime(int curEndTime) {
        this.curEndTime = curEndTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Item(String name, String topic, String cmdOn, String cmdOff, String beginTime, String endTime, int curBeginTime, int curEndTime) {
        this.name = name;
        this.topic = topic;
        this.cmdOn = cmdOn;
        this.cmdOff = cmdOff;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.curBeginTime = curBeginTime;
        this.curEndTime = curEndTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCmdOn() {
        return cmdOn;
    }

    public void setCmdOn(String cmdOn) {
        this.cmdOn = cmdOn;
    }

    public String getCmdOff() {
        return cmdOff;
    }

    public void setCmdOff(String cmdOff) {
        this.cmdOff = cmdOff;
    }
}
