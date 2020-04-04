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
    private int max_num;

    public int getMax_num() {
        return max_num;
    }

    public void setMax_num(int max_num) {
        this.max_num = max_num;
    }

    public DetailedChapterData(String id,String title, String content, String weigh, int max_num) {
        this.title = title;
        this.id = id;
        this.content = content;
        this.weigh = weigh;
        this.max_num = max_num;
    }

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
