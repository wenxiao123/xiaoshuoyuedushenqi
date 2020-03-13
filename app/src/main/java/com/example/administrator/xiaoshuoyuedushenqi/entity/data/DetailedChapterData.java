package com.example.administrator.xiaoshuoyuedushenqi.entity.data;

/**
 * @author WX
 * Created on 2019/11/25
 */
public class DetailedChapterData {
    private String name;    // 章节名
    private String content; // 章节内容
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DetailedChapterData(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
