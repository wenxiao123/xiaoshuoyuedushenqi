package com.novel.collection.constract;

import com.novel.collection.entity.bean.Noval_Readcored;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.data.BookshelfNovelDbData;

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
        void getDelectReadcoredData(String token, int type);
        void queryAllBook();
    }
    interface Model {
        void queryAllBook();
        void getReadcoredData(String token, String page);
        void getDelectReadcoredData(String token,int type);
    }
}
