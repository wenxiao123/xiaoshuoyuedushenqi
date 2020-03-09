package com.example.administrator.xiaoshuoyuedushenqi.presenter;

import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.ISearchResultContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.NovelSourceData;
import com.example.administrator.xiaoshuoyuedushenqi.model.SearchResultModel;

import java.util.List;

/**
 * @author WX
 * Created on 2019/11/9
 */
public class SearchResultPresenter extends BasePresenter<ISearchResultContract.View>
        implements ISearchResultContract.Presenter{

    private ISearchResultContract.Model mModel;

    public SearchResultPresenter() {
        mModel = new SearchResultModel(this);
    }

    @Override
    public void getNovelsSourceSuccess(List<NovelSourceData> novelSourceDataList) {
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
    public void getNovelsSource(String novelName) {
        mModel.getNovelsSource(novelName);
    }
}
