package com.novel.collection.entity.eventbus;

import com.novel.collection.view.activity.ReadActivity;
import com.novel.collection.view.activity.WYReadActivity;

/**
 * @author
 * Created on 2019/12/5
 */
public class HoldReadActivityEvent {
    private WYReadActivity readActivity;

    public HoldReadActivityEvent(WYReadActivity readActivity) {
        this.readActivity = readActivity;
    }

    public WYReadActivity getReadActivity() {
        return readActivity;
    }

    public void setReadActivity(WYReadActivity readActivity) {
        this.readActivity = readActivity;
    }
}
