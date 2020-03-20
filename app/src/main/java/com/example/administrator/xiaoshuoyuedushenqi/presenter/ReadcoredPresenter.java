package com.example.administrator.xiaoshuoyuedushenqi.presenter;

import android.os.Handler;
import android.os.Looper;

import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IRankContract;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IReakcoredContract;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_Readcored;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.model.RankModel;
import com.example.administrator.xiaoshuoyuedushenqi.model.ReadcoredModel;

import java.util.List;

/**
 * @author Created on 2019/11/6
 */
public class ReadcoredPresenter extends BasePresenter<IReakcoredContract.View>
        implements IReakcoredContract.Presenter {

    private IReakcoredContract.Model mModel;

    public ReadcoredPresenter() {
        mModel = new ReadcoredModel(this);

    }
    @Override
    public void queryAllBookSuccess(List<Noval_Readcored> dataList) {
        if (isAttachView()) {
            getMvpView().queryAllBookSuccess(dataList);
        }
    }

    @Override
    public void queryAllBookError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().queryAllBookError(errorMsg);
        }
    }
    @Override
    public void getReadcoredDataSuccess(List<Noval_Readcored> novelNameList) {
        if (isAttachView()) {
            getMvpView().getReadcoredDataSuccess(novelNameList);
        }
    }

    public void queryAllBook() {
        mModel.queryAllBook();
    }
    @Override
    public void getReadcoredDataError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getReadcoredDataError(errorMsg);
        }
    }

    @Override
    public void getDelectReadcoredDataSuccess(String msg) {
        if (isAttachView()) {
            getMvpView().getDelectReadcoredDataSuccess(msg);
        }
    }

    @Override
    public void getDelectReadcoredDataError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getDelectReadcoredDataError(errorMsg);
        }
    }

    @Override
    public void getReadcoredData(String token, String page) {
        mModel.getReadcoredData(token, page);
    }

    @Override
    public void getDelectReadcoredData(String token, String novel_id) {
        mModel.getDelectReadcoredData(token, novel_id);
    }
}
