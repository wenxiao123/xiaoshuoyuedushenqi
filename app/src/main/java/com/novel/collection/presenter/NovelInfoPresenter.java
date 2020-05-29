package com.novel.collection.presenter;

import com.novel.collection.base.BasePresenter;
import com.novel.collection.constract.IAllNovelContract;
import com.novel.collection.constract.NovelInfoContract;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.data.ANNovelData;
import com.novel.collection.entity.data.DiscoveryNovelData;
import com.novel.collection.entity.data.RequestCNData;
import com.novel.collection.model.AllNovelModel;
import com.novel.collection.model.NovelInfoModel;

import java.util.List;

/**
 * @author
 * Created on 2019/12/21
 */
public class NovelInfoPresenter extends BasePresenter<NovelInfoContract.View>
        implements NovelInfoContract.Presenter {

    private NovelInfoContract.Model mModel;

    public NovelInfoPresenter() {
        mModel = new NovelInfoModel(this);
    }

    @Override
    public void getNovelsSuccess(Noval_details noval_details,List<Noval_details> novalDetails) {
        if (isAttachView()) {
            getMvpView().getNovelsSuccess(noval_details,novalDetails);
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
    public void getNovels(String id) {
        mModel.getNovels(id);
    }

    @Override
    public void getCategoryNovels() {
        mModel.getCategoryNovels();
    }
}
