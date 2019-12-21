package com.example.sm.Presenter.RxDataListView;

public class Item {
    private String topic;
    private String content;
    private boolean isRetain;

    public boolean isRetain() {
        return isRetain;
    }

    public void setRetain(boolean retain) {
        isRetain = retain;
    }

    public Item(boolean isRetain, String topic, String content) {
        this.topic = topic;
        this.content = content;
        this.isRetain = isRetain;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
