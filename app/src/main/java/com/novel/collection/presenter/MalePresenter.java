package com.novel.collection.presenter;

import com.novel.collection.base.BasePresenter;
import com.novel.collection.constract.IMaleContract;
import com.novel.collection.entity.bean.CategoryNovels;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.bean.Wheel;
import com.novel.collection.entity.data.DiscoveryNovelData;
import com.novel.collection.model.MaleModel;

import java.net.IDN;
import java.util.List;

/**
 * @author
 * Created on 2019/11/6
 */
public class MalePresenter extends BasePresenter<IMaleContract.View>
        implements IMaleContract.Presenter {

    private IMaleContract.Model mModel;

    public MalePresenter() {
        mModel = new MaleModel(this);
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
    public void getListImageSuccess(List<Wheel> novalDetailsList) {
        if (isAttachView()) {
            getMvpView().getListImageSuccess(novalDetailsList);
        }
    }

    @Override
    public void getListImageError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getListImageError(errorMsg);
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
    public void getListImage(String type) {
        mModel.getListImage(type);
    }
}
