package com.example.administrator.xiaoshuoyuedushenqi.constract;

import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Catagorys;

import java.util.List;

/**
 * @author
 * Created on 2019/11/6
 */
public interface IMaleLikeContract {
    interface View {
        void getCategoryNovelsSuccess(List<Catagorys> dataList);
        void getCategoryNovelsError(String errorMsg);
    }
    interface Presenter {
        void getCategoryNovelsSuccess(List<Catagorys> dataList);
        void getCategoryNovelsError(String errorMsg);
        void getCategoryNovels();   // 获取分类小说
    }
    interface Model {
        void getCategoryNovels();   // 获取分类小说
    }
}
