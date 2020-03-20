package com.example.administrator.xiaoshuoyuedushenqi.entity.bean;

public class Wheel {
    int id;
    String picpath;
    String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Wheel(int id, String picpath, String title) {
        this.id = id;
        this.picpath = picpath;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicpath() {
        return picpath;
    }

    public void setPicpath(String picpath) {
        this.picpath = picpath;
    }
}
