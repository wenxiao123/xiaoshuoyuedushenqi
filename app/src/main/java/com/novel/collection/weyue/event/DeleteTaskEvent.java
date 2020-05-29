package com.novel.collection.weyue.event;


import com.novel.collection.weyue.db.entity.CollBookBean;

/**
 * Created by LiangLu on 17-12-27.
 */

public class DeleteTaskEvent {
    public CollBookBean collBook;

    public DeleteTaskEvent(CollBookBean collBook) {
        this.collBook = collBook;
    }
}
