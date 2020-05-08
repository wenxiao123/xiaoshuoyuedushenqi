package com.example.administrator.xiaoshuoyuedushenqi.entity.bean;

public class DownBean {
    int weight;
    int position;
    String title;
    String pic;
    String id;

    public DownBean(int weight, int position, String title, String pic, String id) {
        this.weight = weight;
        this.position = position;
        this.title = title;
        this.pic = pic;
        this.id = id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DownBean{" +
                "weight=" + weight +
                ", position=" + position +
                ", title='" + title + '\'' +
                ", pic='" + pic + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
