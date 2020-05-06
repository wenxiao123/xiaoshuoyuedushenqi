package com.example.administrator.xiaoshuoyuedushenqi.constract;

import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Cataloginfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.CatalogData;

import java.util.List;

/**
 * @author
 * Created on 2019/11/17
 */
public interface ICatalogContract {
    interface View {
        void getCatalogDataSuccess(List<Cataloginfo> catalogData,int weight);
        void getCatalogDataError(String errorMsg);
    }
    interface Presenter {
        void getCatalogDataSuccess(List<Cataloginfo> catalogData,int weight);
        void getCatalogDataError(String errorMsg);

        void getCatalogData(String url,int posion,int type);
    }
    interface Model {
        void getCatalogData(String url,int posion,int type);    // 获取目录信息
    }
}
