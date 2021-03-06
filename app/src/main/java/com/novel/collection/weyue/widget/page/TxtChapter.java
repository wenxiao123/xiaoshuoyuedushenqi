package com.novel.collection.weyue.widget.page;

import java.io.Serializable;

/**
 * Created by newbiechen on 17-7-1.
 */

public class TxtChapter implements Serializable {

    //章节所属的小说(网络)
   public String bookId;
    //章节的链接(网络)
    public String link;

    //章节名(共用)
    public String title;

    //章节内容在文章中的起始位置(本地)
    public long start;
    //章节内容在文章中的终止位置(本地)
    public long end;

    public TxtChapter(String bookId, String link, String title, long start, long end) {
        this.bookId = bookId;
        this.link = link;
        this.title = title;
        this.start = start;
        this.end = end;
    }

    public TxtChapter() {
    }

    //选中目录
    boolean isSelect;


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String id) {
        this.bookId = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "TxtChapter{" +
                "title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
