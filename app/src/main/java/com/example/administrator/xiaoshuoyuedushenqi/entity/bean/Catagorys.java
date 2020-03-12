package com.example.administrator.xiaoshuoyuedushenqi.entity.bean;

import java.io.Serializable;

public class Catagorys implements Serializable {
    int id;
    String title;
    String sort;
    int pid;
    String count;
    String html;
    String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "Catagory{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", sort='" + sort + '\'' +
                ", pid=" + pid +
                ", count='" + count + '\'' +
                ", html='" + html + '\'' +
                '}';
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
