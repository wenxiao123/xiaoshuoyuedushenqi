package com.novel.collection.presenter;

import android.os.Handler;
import android.os.Looper;

import com.novel.collection.base.BasePresenter;
import com.novel.collection.constract.IRankContract;
import com.novel.collection.constract.IReakcoredContract;
import com.novel.collection.db.DatabaseManager;
import com.novel.collection.entity.bean.Noval_Readcored;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.data.BookshelfNovelDbData;
import com.novel.collection.model.RankModel;
import com.novel.collection.model.ReadcoredModel;

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
    public void getDelectReadcoredData(String token, int type) {
        mModel.getDelectReadcoredData(token, type);
    }
}
