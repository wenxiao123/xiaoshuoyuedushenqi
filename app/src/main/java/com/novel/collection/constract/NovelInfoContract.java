package com.novel.collection.constract;

import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.data.ANNovelData;
import com.novel.collection.entity.data.DiscoveryNovelData;
import com.novel.collection.entity.data.RequestCNData;

import java.util.List;

/**
 * @author
 * Created on 2019/12/21
 */
public interface NovelInfoContract {
    interface View {
        void getNovelsSuccess(Noval_details noval_details,List<Noval_details> novalDetails);
        void getNovelsError(String errorMsg);

        void getCategoryNovelsSuccess(List<DiscoveryNovelData> dataList);
        void getCategoryNovelsError(String errorMsg);
    }
    interface Presenter {
        void getNovelsSuccess(Noval_details noval_details,List<Noval_details> novalDetails);
        void getNovelsError(String errorMsg);
        void getCategoryNovelsSuccess(List<DiscoveryNovelData> dataList);
        void getCategoryNovelsError(String errorMsg);
        void getNovels(String id);    // 获取小说信息
        void getCategoryNovels();   // 获取分类小说
    }
    interface Model {
        void getNovels(String  id);    // 获取小说信息
        void getCategoryNovels();   // 获取分类小说
    }
}
