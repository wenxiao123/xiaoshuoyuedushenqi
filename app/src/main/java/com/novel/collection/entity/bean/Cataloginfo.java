package com.novel.collection.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author
 * Created on 2019/11/17
 */
public class Cataloginfo implements Serializable {
    String id;
    String novel_id;
    String title;
    String reurl;
    String update_time;
    int weigh;
    int hits;
    int word;

    public Cataloginfo(String id, String novel_id, String title, String reurl,int weigh) {
        this.id = id;
        this.novel_id = novel_id;
        this.title = title;
        this.reurl = reurl;
        this.weigh=weigh;
    }

    @Override
    public String toString() {
        return "Cataloginfo{" +
                "id=" + id +
                ", novel_id=" + novel_id +
                ", title='" + title + '\'' +
                ", reurl='" + reurl + '\'' +
                ", update_time='" + update_time + '\'' +
                ", weigh=" + weigh +
                ", hits=" + hits +
                ", word=" + word +
                '}';
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReurl() {
        return reurl;
    }

    public void setReurl(String reurl) {
        this.reurl = reurl;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public int getWeigh() {
        return weigh;
    }

    public void setWeigh(int weigh) {
        this.weigh = weigh;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getWord() {
        return word;
    }

    public void setWord(int word) {
        this.word = word;
    }
}
