package com.example.administrator.xiaoshuoyuedushenqi.weyue.widget;

import com.example.administrator.xiaoshuoyuedushenqi.view.activity.IBaseLoadView;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.model.BookChaptersBean;


/**
 * Created by Liang_Lu on 2017/12/11.
 */

public interface IBookChapters extends IBaseLoadView {
    void bookChapters(BookChaptersBean bookChaptersBean);

    void finishChapters();

    void errorChapters();

}
