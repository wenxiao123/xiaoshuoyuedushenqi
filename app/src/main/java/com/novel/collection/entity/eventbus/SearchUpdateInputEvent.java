package com.novel.collection.entity.eventbus;

/**
 * 搜索页面更新输入
 *
 * @author
 * Created on 2019/11/12
 */
public class SearchUpdateInputEvent {
    private String input;

    public SearchUpdateInputEvent(String input) {
        this.input = input;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
