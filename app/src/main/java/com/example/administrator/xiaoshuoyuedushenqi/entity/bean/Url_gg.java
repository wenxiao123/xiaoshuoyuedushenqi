package com.example.administrator.xiaoshuoyuedushenqi.entity.bean;

import java.io.Serializable;

public class Url_gg implements Serializable {
    String url;
    String time;
    String https;

    @Override
    public String toString() {
        return "Url_gg{" +
                "url='" + url + '\'' +
                ", time='" + time + '\'' +
                ", https='" + https + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public Url_gg(String url, String time, String https) {
        this.url = url;
        this.time = time;
        this.https = https;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHttps() {
        return https;
    }

    public void setHttps(String https) {
        this.https = https;
    }
}
