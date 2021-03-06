package com.novel.collection.entity.eventbus;

import com.novel.collection.entity.data.NovelSourceData;

/**
 * 小说介绍页面初始化数据
 *
 * @author
 * Created on 2019/11/16
 */
public class NovelIntroInitEvent {
    private NovelSourceData novelSourceData;

    public NovelIntroInitEvent(NovelSourceData novelSourceData) {
        this.novelSourceData = novelSourceData;
    }

    public NovelSourceData getNovelSourceData() {
        return novelSourceData;
    }

    public void setNovelSourceData(NovelSourceData novelSourceData) {
        this.novelSourceData = novelSourceData;
    }
}
