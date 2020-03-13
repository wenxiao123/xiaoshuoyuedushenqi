package com.example.administrator.xiaoshuoyuedushenqi.entity.bean;

public class Noval_details {
    int id;
    String title;
    int category;
    String author;
    String pic;
    String content;
    int hits;
    String rating;
    int serialize;
    String update_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Noval_details{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", author='" + author + '\'' +
                ", pic='" + pic + '\'' +
                ", content='" + content + '\'' +
                ", hits=" + hits +
                ", rating='" + rating + '\'' +
                ", serialize=" + serialize +
                ", update_time='" + update_time + '\'' +
                ", word=" + word +
                ", category_name='" + category_name + '\'' +
                ", chapter=" + chapter +
                '}';
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getSerialize() {
        return serialize;
    }

    public void setSerialize(int serialize) {
        this.serialize = serialize;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public int getWord() {
        return word;
    }

    public void setWord(int word) {
        this.word = word;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    int  word;
    String category_name;
    Chapter chapter;
    public class Chapter{
       int id;
       String title;

       @Override
       public String toString() {
           return "Chapter{" +
                   "id=" + id +
                   ", title='" + title + '\'' +
                   ", update_time='" + update_time + '\'' +
                   ", weigh=" + weigh +
                   ", word=" + word +
                   '}';
       }

       public int getId() {
           return id;
       }

       public void setId(int id) {
           this.id = id;
       }

       public String getTitle() {
           return title;
       }

       public void setTitle(String title) {
           this.title = title;
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

       public int getWord() {
           return word;
       }

       public void setWord(int word) {
           this.word = word;
       }

       String update_time;
       int weigh;
       int word;
    }
}
