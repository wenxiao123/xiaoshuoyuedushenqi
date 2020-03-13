package com.example.administrator.xiaoshuoyuedushenqi.entity.bean;

import java.util.List;

public class CategoryNovels {
    int id;
    String title;
    List<Noval_details> data_list;

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

    public List<Noval_details> getData_list() {
        return data_list;
    }

    public void setData_list(List<Noval_details> data_list) {
        this.data_list = data_list;
    }
}
