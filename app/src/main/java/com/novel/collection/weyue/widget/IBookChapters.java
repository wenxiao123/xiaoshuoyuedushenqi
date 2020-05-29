package com.novel.collection.weyue.widget;

import com.novel.collection.weyue.model.BookChaptersBean;


/**
 * Created by Liang_Lu on 2017/12/11.
 */

public interface IBookChapters extends IBaseLoadView {
    void bookChapters(BookChaptersBean bookChaptersBean);

    void finishChapters();

    void errorChapters();

}
