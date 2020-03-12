package com.example.administrator.xiaoshuoyuedushenqi.constract;

import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;

import java.util.List;

/**
 * @author WX
 * Created on 2019/11/6
 */
public interface IExclusiveContract {
    interface View {
        void getHotRankDataSuccess(List<List<String>> novelNameList);
        void getHotRankDataError(String errorMsg);
        void getCategoryNovelsSuccess(List<DiscoveryNovelData> dataList);
        void getCategoryNovelsError(String errorMsg);
    }
    interface Presenter {
        void getHotRankDataSuccess(List<List<String>> novelNameList);
        void getHotRankDataError(String errorMsg);
        void getCategoryNovelsSuccess(List<DiscoveryNovelData> dataList);
        void getCategoryNovelsError(String errorMsg);

        void getCategoryNovels();   // 获取分类小说
    }
    interface Model {
        void getCategoryNovels();   // 获取分类小说
    }
}
