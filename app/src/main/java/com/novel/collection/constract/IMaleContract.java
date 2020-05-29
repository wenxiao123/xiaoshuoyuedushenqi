package com.novel.collection.constract;

import com.novel.collection.entity.bean.CategoryNovels;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.bean.Wheel;
import com.novel.collection.entity.data.DiscoveryNovelData;

import java.util.List;

/**
 * @author
 * Created on 2019/11/6
 */
public interface IMaleContract {
    interface View {
        void getNewDataSuccess(List<Noval_details> novelNameList);
        void getNewDataError(String errorMsg);
        void getHotRankDataSuccess(List<CategoryNovels> novelNameList);
        void getHotRankDataError(String errorMsg);
        void getCategoryNovelsSuccess(List<Noval_details> novalDetailsList);
        void getCategoryNovelsError(String errorMsg);
        void getListImageSuccess(List<Wheel> novalDetailsList);
        void getListImageError(String errorMsg);
    }
    interface Presenter {
        void getHotRankDataSuccess(List<CategoryNovels> novelNameList);
        void getHotRankDataError(String errorMsg);
        void getNewDataSuccess(List<Noval_details> novelNameList);
        void getNewDataError(String errorMsg);
        void getCategoryNovelsSuccess(List<Noval_details> novalDetailsList);
        void getCategoryNovelsError(String errorMsg);
        void getListImageSuccess(List<Wheel> novalDetailsList);
        void getListImageError(String errorMsg);

        void getHotRankData(String id);      // 获取热门排行
        void getCategoryNovels(String id);   // 获取分类小说
        void getNewRankData(String id);
        void getListImage(String type);
    }
    interface Model {
        void getHotRankData(String id);      // 获取热门排行
        void getNewRankData(String id);      // 获取热门排行
        void getCategoryNovels(String id);   // 获取分类小说
        void getListImage(String type);
    }
}
