package com.novel.collection.constract;

import com.novel.collection.entity.bean.NovalInfo;
import com.novel.collection.entity.data.ANNovelData;
import com.novel.collection.entity.data.RequestCNData;

import java.util.List;

/**
 * @author
 * Created on 2019/12/21
 */
public interface IListNovelContract {
    interface View {
        void getNovelsSuccess(List<NovalInfo> dataList);
        void getNovelsError(String errorMsg);
    }
    interface Presenter {
        void getNovelsSuccess(List<NovalInfo> dataList);
        void getNovelsError(String errorMsg);

        void getNovels(String id,String type,String z);    // 获取小说信息
    }
    interface Model {
        void getNovels(String id,String type,String z);    // 获取小说信息
    }
}
