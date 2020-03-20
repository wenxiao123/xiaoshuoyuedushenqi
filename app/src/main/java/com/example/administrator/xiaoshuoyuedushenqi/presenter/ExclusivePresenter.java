package com.example.administrator.xiaoshuoyuedushenqi.presenter;

import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IExclusiveContract;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IMaleContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovels;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Wheel;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.model.ExclusiveModel;
import com.example.administrator.xiaoshuoyuedushenqi.model.MaleModel;

import java.util.List;

/**
 * @author
 * Created on 2019/11/6
 */
public class ExclusivePresenter extends BasePresenter<IExclusiveContract.View>
        implements IExclusiveContract.Presenter {

    private IExclusiveContract.Model mModel;

    public ExclusivePresenter() {
        mModel = new ExclusiveModel(this);
    }

    @Override
    public void getHotRankDataSuccess(List<CategoryNovels> novelNameList) {
        if (isAttachView()) {
            getMvpView().getHotRankDataSuccess(novelNameList);
        }
    }

    @Override
    public void getHotRankDataError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getHotRankDataError(errorMsg);
        }
    }

    @Override
    public void getNewDataSuccess(List<Noval_details> novelNameList) {
        if (isAttachView()) {
            getMvpView().getNewDataSuccess(novelNameList);
        }
    }

    @Override
    public void getNewDataError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getNewDataError(errorMsg);
        }
    }

    @Override
    public void getCategoryNovelsSuccess(List<Noval_details> dataList) {
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
    public void getCategoryNovels2Success(List<Noval_details> novalDetailsList) {
        if (isAttachView()) {
            getMvpView().getCategoryNovels2Success(novalDetailsList);
        }
    }

    @Override
    public void getCategoryNovels2Error(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getCategoryNovels2Error(errorMsg);
        }
    }


    @Override
    public void getHotRankData(String id) {
        mModel.getHotRankData(id);
    }
    @Override
    public void getCategoryNovels(String id) {
        mModel.getCategoryNovels(id);
    }

    @Override
    public void getNewRankData(String id) {
        mModel.getNewRankData(id);
    }

    @Override
    public void getCategoryNovels2(String id) {
        mModel.getCategoryNovels2(id);
    }
}
