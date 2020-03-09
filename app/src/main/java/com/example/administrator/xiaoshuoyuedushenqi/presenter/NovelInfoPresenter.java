package com.example.administrator.xiaoshuoyuedushenqi.presenter;

import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IAllNovelContract;
import com.example.administrator.xiaoshuoyuedushenqi.constract.NovelInfoContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.ANNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.RequestCNData;
import com.example.administrator.xiaoshuoyuedushenqi.model.AllNovelModel;
import com.example.administrator.xiaoshuoyuedushenqi.model.NovelInfoModel;

import java.util.List;

/**
 * @author WX
 * Created on 2019/12/21
 */
public class NovelInfoPresenter extends BasePresenter<NovelInfoContract.View>
        implements NovelInfoContract.Presenter {

    private NovelInfoContract.Model mModel;

    public NovelInfoPresenter() {
        mModel = new NovelInfoModel(this);
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
    public void getCategoryNovelsSuccess(List<DiscoveryNovelData> dataList) {
        if (isAttachView()) {
            getMvpView().getCategoryNovelsSuccess(dataList);
        }
    }

    @Override
    public void getCategoryNovelsError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getCategoryNovelsError(errorMsg);
        }
    }

    @Override
    public void getNovels(RequestCNData requestCNData) {
        mModel.getNovels(requestCNData);
    }

    @Override
    public void getCategoryNovels() {
        mModel.getCategoryNovels();
    }
}
