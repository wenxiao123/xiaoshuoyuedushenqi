package com.example.administrator.xiaoshuoyuedushenqi.constract;

import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovels;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Wheel;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;

import java.util.List;

/**
 * @author
 * Created on 2019/11/6
 */
public interface IExclusiveContract {
    interface View {
        void getNewDataSuccess(List<Noval_details> novelNameList);
        void getNewDataError(String errorMsg);
        void getHotRankDataSuccess(List<CategoryNovels> novelNameList);
        void getHotRankDataError(String errorMsg);
        void getCategoryNovelsSuccess(List<Noval_details> novalDetailsList);
        void getCategoryNovelsError(String errorMsg);
        void getCategoryNovels2Success(List<Noval_details> novalDetailsList);
        void getCategoryNovels2Error(String errorMsg);
    }
    interface Presenter {
        void getHotRankDataSuccess(List<CategoryNovels> novelNameList);
        void getHotRankDataError(String errorMsg);
        void getNewDataSuccess(List<Noval_details> novelNameList);
        void getNewDataError(String errorMsg);
        void getCategoryNovelsSuccess(List<Noval_details> novalDetailsList);
        void getCategoryNovelsError(String errorMsg);
        void getCategoryNovels2Success(List<Noval_details> novalDetailsList);
        void getCategoryNovels2Error(String errorMsg);


        void getHotRankData(String id);      // 获取热门排行
        void getCategoryNovels(String id);   // 获取分类小说
        void getNewRankData(String id);
        void getCategoryNovels2(String id);   // 获取分类小说
    }
    interface Model {
        void getHotRankData(String id);      // 获取热门排行
        void getNewRankData(String id);      // 获取热门排行
        void getCategoryNovels(String id);   // 获取分类小说
        void getCategoryNovels2(String id);   // 获取分类小说
    }
}
