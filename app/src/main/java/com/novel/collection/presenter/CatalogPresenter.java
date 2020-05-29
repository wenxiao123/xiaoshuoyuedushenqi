package com.novel.collection.presenter;

import com.novel.collection.base.BasePresenter;
import com.novel.collection.constract.ICatalogContract;
import com.novel.collection.entity.bean.Cataloginfo;
import com.novel.collection.entity.data.CatalogData;
import com.novel.collection.model.CatalogModel;

import java.util.List;

/**
 * @author
 * Created on 2019/11/17
 */
public class CatalogPresenter extends BasePresenter<ICatalogContract.View>
        implements ICatalogContract.Presenter {

    private ICatalogContract.Model mModel;

    public CatalogPresenter() {
        mModel = new CatalogModel(this);
    }

    @Override
    public void getCatalogDataSuccess(List<Cataloginfo> catalogData,int weight) {
        if (isAttachView()) {
            getMvpView().getCatalogDataSuccess(catalogData,weight);
        }
    }

    @Override
    public void getCatalogDataError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getCatalogDataError(errorMsg);
        }
    }

    @Override
    public void getCatalogData(String id,int posion,int type) {
        mModel.getCatalogData(id,posion,type);
    }
}
