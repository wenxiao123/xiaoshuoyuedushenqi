package com.example.administrator.xiaoshuoyuedushenqi.entity.bean;

import java.util.List;

public class Categorys_one {
    String id;
    String novel_id;
    String reurl;
    String create_time;
    String update_time;
    String url;
    String chapter_sum;
    String captername;
    String div;

    public String getDiv() {
        return div;
    }

    public void setDiv(String div) {
        this.div = div;
    }

    public String getCaptername() {
        if(getText()==null){
            return "";
        }else {
            return getText().get(getText().size() - 1).getChapter_name();
        }
    }

    public void setCaptername(String captername) {
        this.captername = captername;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getChapter_sum() {
        return chapter_sum;
    }

    public void setChapter_sum(String chapter_sum) {
        this.chapter_sum = chapter_sum;
    }

    List<Text> text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNovel_id() {
        return novel_id;
    }

    public void setNovel_id(String novel_id) {
        this.novel_id = novel_id;
    }

    public String getReurl() {
        return reurl;
    }

    public void setReurl(String reurl) {
        this.reurl = reurl;
    }

    public List<Text> getText() {
        return text;
    }

    public void setText(List<Text> text) {
        this.text = text;
    }
}
