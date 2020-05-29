package com.novel.collection.constract;

import com.novel.collection.entity.bean.NovalInfo;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.data.NovelSourceData;

import java.util.List;

/**
 * @author
 * Created on 2019/11/9
 */
public interface ISearchResultContract {
    interface View {
        void getNovelsSourceSuccess(List<NovalInfo> novelSourceDataList);     // 获取小说源成功
        void getNovelsSourceError(String errorMsg);     // 获取小说源失败
    }
    interface Presenter {
        void getNovelsSourceSuccess(List<NovalInfo> novelSourceDataList);     // 获取小说源成功
        void getNovelsSourceError(String errorMsg);     // 获取小说源失败

        void getNovelsSource(String novelName,int z);     // 获取小说源
    }
    interface Model {
        void getNovelsSource(String novelName,int z);     // 获取小说源
    }
}
