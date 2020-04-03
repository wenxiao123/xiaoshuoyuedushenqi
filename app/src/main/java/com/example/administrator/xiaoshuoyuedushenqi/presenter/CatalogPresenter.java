package com.example.administrator.xiaoshuoyuedushenqi.presenter;

import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.ICatalogContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Cataloginfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.CatalogData;
import com.example.administrator.xiaoshuoyuedushenqi.model.CatalogModel;

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
    public void getCatalogDataSuccess(List<Cataloginfo> catalogData) {
        if (isAttachView()) {
            getMvpView().getCatalogDataSuccess(catalogData);
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
