package com.novel.collection.presenter;

import com.novel.collection.base.BasePresenter;
import com.novel.collection.constract.IAllNovelContract;
import com.novel.collection.entity.data.ANNovelData;
import com.novel.collection.entity.data.RequestCNData;
import com.novel.collection.model.AllNovelModel;

import java.util.List;

/**
 * @author
 * Created on 2019/12/21
 */
public class AllNovelPresenter extends BasePresenter<IAllNovelContract.View>
        implements IAllNovelContract.Presenter {

    private IAllNovelContract.Model mModel;

    public AllNovelPresenter() {
        mModel = new AllNovelModel(this);
    }

    @Override
    public void getNovelsSuccess(List<ANNovelData> dataList, boolean isEnd) {
        if (isAttachView()) {
            getMvpView().getNovelsSuccess(dataList, isEnd);
        }
    }

    @Override
    public void getNovelsError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getNovelsError(errorMsg);
        }
    }

    @Override
    public void getNovels(RequestCNData requestCNData) {
        mModel.getNovels(requestCNData);
    }
}
