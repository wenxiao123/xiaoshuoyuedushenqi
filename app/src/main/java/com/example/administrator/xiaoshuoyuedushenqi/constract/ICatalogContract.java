package com.example.administrator.xiaoshuoyuedushenqi.constract;

import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Cataloginfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.CatalogData;

import java.util.List;

/**
 * @author WX
 * Created on 2019/11/17
 */
public interface ICatalogContract {
    interface View {
        void getCatalogDataSuccess(List<Cataloginfo> catalogData);
        void getCatalogDataError(String errorMsg);
    }
    interface Presenter {
        void getCatalogDataSuccess(List<Cataloginfo> catalogData);
        void getCatalogDataError(String errorMsg);

        void getCatalogData(String url,int posion);
    }
    interface Model {
        void getCatalogData(String url,int posion);    // 获取目录信息
    }
}
