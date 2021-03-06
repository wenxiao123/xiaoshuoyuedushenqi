package com.novel.collection.entity.bean;

import java.io.Serializable;

public class PersonBean implements Serializable {
    private String name;
    private String firstPinYin;
    private String pinYin;
    private String filepath;
    private String filetype;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getPinYin() {
        return pinYin;
    }

    public void setPinYin(String pinYin) {
        this.pinYin = pinYin;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PersonBean{" +
                "name='" + name + '\'' +
                ", firstPinYin='" + firstPinYin + '\'' +
                ", pinYin='" + pinYin + '\'' +
                ", filepath='" + filepath + '\'' +
                ", filetype='" + filetype + '\'' +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstPinYin() {
        return firstPinYin;
    }

    public void setFirstPinYin(String firstPinYin) {
        this.firstPinYin = firstPinYin;
    }
}
