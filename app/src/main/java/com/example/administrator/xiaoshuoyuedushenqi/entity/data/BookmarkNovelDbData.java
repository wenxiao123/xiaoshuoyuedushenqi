package com.example.administrator.xiaoshuoyuedushenqi.entity.data;

/**
 * @author WX
 * Created on 2019/11/26
 */
public class BookmarkNovelDbData {
    private String novelUrl;
    private String name;
    private String content;
    private float chapterIndex;
    private int position;
    private int type;
    private String time;

    public BookmarkNovelDbData(String novelUrl, String name, String content,
                               float chapterIndex, int position, int type) {
        this(novelUrl, name, content, chapterIndex, position, type, "");
    }

    public BookmarkNovelDbData(String novelUrl, String name, String content, float chapterIndex,
                               int position, int type, String time) {
        this.novelUrl = novelUrl;
        this.name = name;
        this.content = content;
        this.chapterIndex = chapterIndex;
        this.position = position;
        this.type = type;
        this.time = time;
    }

    public String getNovelUrl() {
        return novelUrl;
    }

    public void setNovelUrl(String novelUrl) {
        this.novelUrl = novelUrl;
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

    public void setContent(String cover) {
        this.content = cover;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public float getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(float chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void settime(String secondPosition) {
        this.time = secondPosition;
    }

    @Override
    public String toString() {
        return "BookshelfNovelDbData{" +
                "novelUrl='" + novelUrl + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", chapterIndex=" + chapterIndex +
                ", position=" + position +
                ", type=" + type +
                ", secondPosition=" + time +
                '}';
    }
}
