package com.example.administrator.xiaoshuoyuedushenqi.entity.data;

/**
 * @author
 * Created on 2019/11/25
 */
public class DetailedChapterData {
    private String title;    // 章节名
    private String content; // 章节内容
    private String id;
    private String weigh;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWeigh() {
        return weigh;
    }

    public void setWeigh(String weigh) {
        this.weigh = weigh;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "DetailedChapterData{" +
                "name='" + title + '\'' +
                ", id='" + id + '\'' +
                ", weigh='" + weigh + '\'' +
                '}';
    }
}
