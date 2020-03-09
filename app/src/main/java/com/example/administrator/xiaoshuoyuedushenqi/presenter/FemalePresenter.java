package com.example.administrator.xiaoshuoyuedushenqi.presenter;

import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IFemaleContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.model.FemaleModel;

import java.util.List;

/**
 * @author WX
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
