package com.example.sm.Presenter.DeviceListView;

public class Item {
    private String name;
    private String topic;
    private String cmdOn;
    private String cmdOff;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Item(String name, String topic, String cmdOn, String cmdOff) {
        this.name = name;
        this.topic = topic;
        this.cmdOn = cmdOn;
        this.cmdOff = cmdOff;
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
