package com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus;

import com.example.administrator.xiaoshuoyuedushenqi.view.activity.ReadActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.WYReadActivity;

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
