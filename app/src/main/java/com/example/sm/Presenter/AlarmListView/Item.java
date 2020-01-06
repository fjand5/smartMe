package com.example.sm.Presenter.AlarmListView;

public class Item {
    String ringMsg;
    String topic;
    String name;
    String respone;

    public String getRespone() {
        return respone;
    }

    public void setRespone(String respone) {
        this.respone = respone;
    }

    public Item(String name, String topic, String ringMsg, String respone) {
        this.ringMsg = ringMsg;
        this.topic = topic;
        this.name = name;
        this.respone = respone;
    }

    public String getRingMsg() {
        return ringMsg;
    }

    public void setRingMsg(String ringMsg) {
        this.ringMsg = ringMsg;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
