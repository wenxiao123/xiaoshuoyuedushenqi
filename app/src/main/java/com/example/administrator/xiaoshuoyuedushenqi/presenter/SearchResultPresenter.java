package com.example.administrator.xiaoshuoyuedushenqi.presenter;

import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.ISearchResultContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.NovalInfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.NovelSourceData;
import com.example.administrator.xiaoshuoyuedushenqi.model.SearchResultModel;

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
