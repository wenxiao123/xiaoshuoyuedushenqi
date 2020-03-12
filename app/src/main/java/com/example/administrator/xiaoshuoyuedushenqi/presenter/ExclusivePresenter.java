package com.example.administrator.xiaoshuoyuedushenqi.presenter;

import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IExclusiveContract;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IMaleContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.model.ExclusiveModel;
import com.example.administrator.xiaoshuoyuedushenqi.model.MaleModel;

import java.util.List;

/**
 * @author WX
 * Created on 2019/11/6
 */
public class ExclusivePresenter extends BasePresenter<IExclusiveContract.View>
        implements IExclusiveContract.Presenter {

    private IExclusiveContract.Model mModel;

    public ExclusivePresenter() {
        mModel = new ExclusiveModel(this);
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
    public void getCategoryNovels() {
        mModel.getCategoryNovels();
    }
}
