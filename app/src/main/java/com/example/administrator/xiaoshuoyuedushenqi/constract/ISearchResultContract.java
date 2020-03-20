package com.example.administrator.xiaoshuoyuedushenqi.constract;

import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.NovalInfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.NovelSourceData;

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

        void getNovelsSource(String novelName);     // 获取小说源
    }
    interface Model {
        void getNovelsSource(String novelName);     // 获取小说源
    }
}
