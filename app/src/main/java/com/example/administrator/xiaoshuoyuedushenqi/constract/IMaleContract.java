package com.example.administrator.xiaoshuoyuedushenqi.constract;

import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;

import java.util.List;

/**
 * @author WX
 * Created on 2019/11/6
 */
public interface IMaleContract {
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

        void getHotRankData();      // 获取热门排行
        void getCategoryNovels();   // 获取分类小说
    }
    interface Model {
        void getHotRankData();      // 获取热门排行
        void getCategoryNovels();   // 获取分类小说
    }
}
