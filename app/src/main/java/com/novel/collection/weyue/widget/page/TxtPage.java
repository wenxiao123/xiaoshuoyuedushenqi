package com.novel.collection.weyue.widget.page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by newbiechen on 17-7-1.
 */

public class TxtPage {
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    int position;
    String title="";
    int titleLines; //当前 lines 中为 title 的行数。
    List<String> lines=new ArrayList<>();

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }
}
