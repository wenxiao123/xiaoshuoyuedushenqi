package com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus;

import com.example.administrator.xiaoshuoyuedushenqi.entity.data.NovelSourceData;

/**
 * 小说介绍页面初始化数据
 *
 * @author WX
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
