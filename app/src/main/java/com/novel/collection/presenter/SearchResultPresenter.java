package com.novel.collection.presenter;

import com.novel.collection.base.BasePresenter;
import com.novel.collection.constract.ISearchResultContract;
import com.novel.collection.entity.bean.NovalInfo;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.data.NovelSourceData;
import com.novel.collection.model.SearchResultModel;

import java.util.List;

/**
 * @author
 * Created on 2019/11/9
 */
public class SearchResultPresenter extends BasePresenter<ISearchResultContract.View>
        implements ISearchResultContract.Presenter{

    private ISearchResultContract.Model mModel;

    public SearchResultPresenter() {
        mModel = new SearchResultModel(this);
    }

    @Override
    public void getNovelsSourceSuccess(List<NovalInfo> novelSourceDataList) {
        if (isAttachView()) {
            getMvpView().getNovelsSourceSuccess(novelSourceDataList);
        }
    }

    @Override
    public void getNovelsSourceError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getNovelsSourceError(errorMsg);
        }
    }

    @Override
    public void getNovelsSource(String novelName,int z) {
        mModel.getNovelsSource(novelName,z);
    }
}
