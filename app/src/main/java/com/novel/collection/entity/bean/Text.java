package com.novel.collection.entity.bean;

public class Text {
    String chapter_num;
    String chapter_name;
    String chapter_url;

    @Override
    public String toString() {
        return "Text{" +
                "chapter_num='" + chapter_num + '\'' +
                ", chapter_name='" + chapter_name + '\'' +
                ", chapter_url='" + chapter_url + '\'' +
                '}';
    }

    public String getChapter_num() {
        return chapter_num;
    }

    public void setChapter_num(String chapter_num) {
        this.chapter_num = chapter_num;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public String getChapter_url() {
        return chapter_url;
    }

    public void setChapter_url(String chapter_url) {
        this.chapter_url = chapter_url;
    }
}
