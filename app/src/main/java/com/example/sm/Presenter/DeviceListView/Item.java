package com.example.sm.Presenter.DeviceListView;

public class Item {
    private String name;
    private String topic;
    private String cmdOn;
    private String cmdOff;
    private int beginTime;
    private int endTime;

    public int getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Item(String name, String topic, String cmdOn, String cmdOff, int beginTime, int endTime) {
        this.name = name;
        this.topic = topic;
        this.cmdOn = cmdOn;
        this.cmdOff = cmdOff;
        this.beginTime = beginTime;
        this.endTime = endTime;
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
