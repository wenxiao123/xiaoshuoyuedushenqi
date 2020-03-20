package com.example.administrator.xiaoshuoyuedushenqi.constract;

import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovels;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;

import java.util.List;

/**
 * @author
 * Created on 2019/11/6
 */
public interface IRankContract {
    interface View {
        void getDataSuccess(List<Noval_details> novelNameList);
        void getDataError(String errorMsg);
    }
    interface Presenter {
        void getDataSuccess(List<Noval_details> novelNameList);
        void getDataError(String errorMsg);

        void getRankData(String id,String type,String id1,String type1);
    }
    interface Model {
        void getRankData(String id,String type,String id1,String type1);
    }
}
