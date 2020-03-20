package com.example.administrator.xiaoshuoyuedushenqi.constract;

import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;

import java.util.List;

/**
 * @author
 * Created on 2019/12/22
 */
public interface IPressContract {
    interface View {
        void getCategoryNovelsSuccess(List<DiscoveryNovelData> dataList);
        void getCategoryNovelsError(String errorMsg);
    }
    interface Presenter {
        void getCategoryNovelsSuccess(List<DiscoveryNovelData> dataList);
        void getCategoryNovelsError(String errorMsg);

        void getCategoryNovels();   // 获取分类小说
    }
    interface Model {
        void getCategoryNovels();   // 获取分类小说
    }
}
