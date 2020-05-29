package com.novel.collection.constract;

import com.novel.collection.entity.data.BookshelfNovelDbData;
import com.novel.collection.entity.epub.OpfData;

import java.util.List;

/**
 * @author
 * Created on 2019/12/12
 */
public interface IBookshelfContract {
    interface View {
        void queryAllBookSuccess(List<BookshelfNovelDbData> dataList);
        void queryAllBookError(String errorMsg);
        void unZipEpubSuccess(String filePath, OpfData opfData);
        void unZipEpubError(String errorMsg);
    }
    interface Presenter {
        void queryAllBookSuccess(List<BookshelfNovelDbData> dataList);
        void queryAllBookError(String errorMsg);
        void unZipEpubSuccess(String filePath, OpfData opfData);
        void unZipEpubError(String errorMsg);

        void queryAllBook();
        void unZipEpub(String filePath);
    }
    interface Model {
        void queryAllBook();                // 从数据库中查询所有书籍信息
        void unZipEpub(String filePath);    // 解压 epub 文件
    }
}
