package com.novel.collection.presenter;

import com.novel.collection.base.BasePresenter;
import com.novel.collection.constract.IPressContract;
import com.novel.collection.entity.data.DiscoveryNovelData;
import com.novel.collection.model.PressModel;

import java.util.List;

/**
 * @author
 * Created on 2019/12/22
 */
public class PressPresenter extends BasePresenter<IPressContract.View>
        implements IPressContract.Presenter {

    private IPressContract.Model mModel;

    public PressPresenter() {
        mModel = new PressModel(this);
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
