package com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus;

import com.example.administrator.xiaoshuoyuedushenqi.view.activity.ReadActivity;

/**
 * @author
 * Created on 2019/12/5
 */
public class HoldReadActivityEvent {
    private ReadActivity readActivity;

    public HoldReadActivityEvent(ReadActivity readActivity) {
        this.readActivity = readActivity;
    }

    public ReadActivity getReadActivity() {
        return readActivity;
    }

    public void setReadActivity(ReadActivity readActivity) {
        this.readActivity = readActivity;
    }
}
