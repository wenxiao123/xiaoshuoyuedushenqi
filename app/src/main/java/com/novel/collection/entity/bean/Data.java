package com.novel.collection.entity.bean;

import java.util.List;

public class Data<T> {
    String code;
    String msg;
    String time;
    List<Catagorys> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<Catagorys> getData() {
        return data;
    }

    public void setData(List<Catagorys> data) {
        this.data = data;
    }
}
