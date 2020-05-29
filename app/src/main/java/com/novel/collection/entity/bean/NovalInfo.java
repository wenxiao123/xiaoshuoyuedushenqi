package com.novel.collection.entity.bean;

public class NovalInfo {
    int id;
    int category;
    int serialize;
    int position;
    String title;
    String author;
    String pic;
    String content;
    String rating;
    String word;
    String update_time;
    String category_name;
    int comment_count;

    @Override
    public String toString() {
        return "NovalInfo{" +
                "id=" + id +
                ", category=" + category +
                ", serialize=" + serialize +
                ", position=" + position +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", pic='" + pic + '\'' +
                ", content='" + content + '\'' +
                ", rating='" + rating + '\'' +
                ", word='" + word + '\'' +
                ", update_time='" + update_time + '\'' +
                ", category_name='" + category_name + '\'' +
                ", comment_count=" + comment_count +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getSerialize() {
        return serialize;
    }

    public void setSerialize(int serialize) {
        this.serialize = serialize;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }
}
