package com.example.administrator.xiaoshuoyuedushenqi.constract;

import com.example.administrator.xiaoshuoyuedushenqi.entity.data.CatalogData;

/**
 * @author WX
 * Created on 2019/11/17
 */
public interface ICatalogContract {
    interface View {
        void getCatalogDataSuccess(CatalogData catalogData);
        void getCatalogDataError(String errorMsg);
    }
    interface Presenter {
        void getCatalogDataSuccess(CatalogData catalogData);
        void getCatalogDataError(String errorMsg);

        void getCatalogData(String url);
    }
    interface Model {
        void getCatalogData(String url);    // 获取目录信息
    }
}
