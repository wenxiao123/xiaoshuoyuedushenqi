package com.example.administrator.xiaoshuoyuedushenqi.weyue.event;


import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CollBookBean;

/**
 * Created by LiangLu on 17-12-27.
 */

public class DeleteResponseEvent {
    public boolean isDelete;
    public CollBookBean collBook;
    public DeleteResponseEvent(boolean isDelete, CollBookBean collBook){
        this.isDelete = isDelete;
        this.collBook = collBook;
    }
}
