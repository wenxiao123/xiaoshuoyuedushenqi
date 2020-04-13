package com.example.administrator.xiaoshuoyuedushenqi.constract;

import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_Readcored;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;

import java.util.List;

/**
 * @author
 * Created on 2019/11/6
 */
public interface IReakcoredContract {
    interface View {

        void queryAllBookSuccess(List<Noval_Readcored> dataList);
        void queryAllBookError(String errorMsg);

        void getReadcoredDataSuccess(List<Noval_Readcored> novelNameList);
        void getReadcoredDataError(String errorMsg);

        void getDelectReadcoredDataSuccess(String msg);
        void getDelectReadcoredDataError(String errorMsg);
    }
    interface Presenter {
        void queryAllBookSuccess(List<Noval_Readcored> dataList);
        void queryAllBookError(String errorMsg);

        void getReadcoredDataSuccess(List<Noval_Readcored> novelNameList);
        void getReadcoredDataError(String errorMsg);

        void getDelectReadcoredDataSuccess(String msg);
        void getDelectReadcoredDataError(String errorMsg);

        void getReadcoredData(String token, String page);
        void getDelectReadcoredData(String token, String novel_id,int type);
        void queryAllBook();
    }
    interface Model {
        void queryAllBook();
        void getReadcoredData(String token, String page);
        void getDelectReadcoredData(String token, String novel_id,int type);
    }
}
