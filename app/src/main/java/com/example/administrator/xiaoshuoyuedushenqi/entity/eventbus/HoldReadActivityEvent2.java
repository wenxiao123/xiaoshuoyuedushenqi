package com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus;

import com.example.administrator.xiaoshuoyuedushenqi.view.activity.WYReadActivity;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.page.TxtChapter;

import java.util.List;

/**
 * @author
 * Created on 2019/12/5
 */
public class HoldReadActivityEvent2 {
    private WYReadActivity readActivity;
    protected List<TxtChapter> mChapterList;

    public List<TxtChapter> getmChapterList() {
        return mChapterList;
    }

    public void setmChapterList(List<TxtChapter> mChapterList) {
        this.mChapterList = mChapterList;
    }

    public HoldReadActivityEvent2(WYReadActivity readActivity,List<TxtChapter> mChapterList) {
        this.readActivity = readActivity;
        this.mChapterList=mChapterList;
    }

    public WYReadActivity getReadActivity() {
        return readActivity;
    }

    public void setReadActivity(WYReadActivity readActivity) {
        this.readActivity = readActivity;
    }
}
