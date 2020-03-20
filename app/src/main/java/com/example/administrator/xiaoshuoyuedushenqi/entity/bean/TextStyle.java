package com.example.administrator.xiaoshuoyuedushenqi.entity.bean;

public class TextStyle {
    String id;
    String name;
    String url;
    boolean isLoad;

    public boolean isLoad() {
        return isLoad;
    }

    public TextStyle(String name,boolean isLoad) {
        this.name = name;
        this.isLoad=isLoad;
    }

    public void setLoad(boolean load) {
        isLoad = load;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
