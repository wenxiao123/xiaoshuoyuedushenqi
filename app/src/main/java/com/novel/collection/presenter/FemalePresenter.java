package com.novel.collection.presenter;

import com.novel.collection.base.BasePresenter;
import com.novel.collection.constract.IFemaleContract;
import com.novel.collection.entity.data.DiscoveryNovelData;
import com.novel.collection.model.FemaleModel;

import java.util.List;

/**
 * @author
 * Created on 2019/11/8
 */
public class FemalePresenter extends BasePresenter<IFemaleContract.View>
        implements IFemaleContract.Presenter{

    private IFemaleContract.Model mModel;

    public FemalePresenter() {
        mModel = new FemaleModel(this);
    }

    @Override
    public void getHotRankDataSuccess(List<List<String>> novelNameList) {
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
    public void getHotRankData() {
        mModel.getHotRankData();
    }

    @Override
    public void getCategoryNovels() {
        mModel.getCategoryNovels();
    }
}
