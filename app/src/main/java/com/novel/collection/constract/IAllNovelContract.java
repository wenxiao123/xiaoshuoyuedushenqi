package com.novel.collection.constract;

import com.novel.collection.entity.data.ANNovelData;
import com.novel.collection.entity.data.RequestCNData;

import java.util.List;

/**
 * @author
 * Created on 2019/12/21
 */
public interface IAllNovelContract {
    interface View {
        void getNovelsSuccess(List<ANNovelData> dataList, boolean isEnd);
        void getNovelsError(String errorMsg);
    }
    interface Presenter {
        void getNovelsSuccess(List<ANNovelData> dataList, boolean isEnd);
        void getNovelsError(String errorMsg);

        void getNovels(RequestCNData requestCNData);    // 获取小说信息
    }
    interface Model {
        void getNovels(RequestCNData requestCNData);    // 获取小说信息
    }
}
