package com.novel.collection.entity.bean;

public class Noval_Readcored {
    String id;
    String novel_id;
    String chapter_id;
    String status;
    String create_time;
    String update_time;
    String title;
    int category;
    String author;
    String  pic;
    String rating;
    int chapter_count;

    public int getChapter_count() {
        return chapter_count;
    }

    public void setChapter_count(int chapter_count) {
        this.chapter_count = chapter_count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    int hits;
    String content;
    String serialize;
    int word;
    String is_che;
    String chapter_name;
    String weigh;

    public Noval_Readcored(String novel_id, String chapter_id, String status, String title, String author, String pic, String is_che, String chapter_name, String weigh) {
        this.novel_id = novel_id;
        this.chapter_id = chapter_id;
        this.status = status;
        this.title = title;
        this.author = author;
        this.pic = pic;
        this.is_che = is_che;
        this.chapter_name = chapter_name;
        this.weigh = weigh;
    }
    public Noval_Readcored(String novel_id, String chapter_id, String title, String pic, String is_che, String chapter_name, String weigh) {
        this.novel_id = novel_id;
        this.chapter_id = chapter_id;
        this.title = title;
        this.pic = pic;
        this.is_che = is_che;
        this.chapter_name = chapter_name;
        this.weigh = weigh;
    }
    public String getWeigh() {
        return weigh;
    }

    public void setWeigh(String weigh) {
        this.weigh = weigh;
    }


    public String getNovel_id() {
        return novel_id;
    }

    public void setNovel_id(String novel_id) {
        this.novel_id = novel_id;
    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSerialize() {
        return serialize;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }

    public int getWord() {
        return word;
    }

    public void setWord(int word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "Noval_Readcored{" +
                " novel_id='" + novel_id + '\'' +
                ", chapter_id='" + chapter_id + '\'' +
                ", status='" + status + '\'' +
                ", create_time='" + create_time + '\'' +
                ", update_time='" + update_time + '\'' +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", author='" + author + '\'' +
                ", pic='" + pic + '\'' +
                ", rating='" + rating + '\'' +
                ", hits=" + hits +
                ", content='" + content + '\'' +
                ", serialize='" + serialize + '\'' +
                ", word=" + word +
                ", is_che='" + is_che + '\'' +
                ", chapter_name='" + chapter_name + '\'' +
                ", weigh='" + weigh + '\'' +
                '}';
    }

    public String getIs_che() {
        return is_che;
    }

    public void setIs_che(String is_che) {
        this.is_che = is_che;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }
}
