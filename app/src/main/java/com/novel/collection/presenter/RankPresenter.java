package com.novel.collection.presenter;

import com.novel.collection.base.BasePresenter;
import com.novel.collection.constract.IMaleContract;
import com.novel.collection.constract.IRankContract;
import com.novel.collection.entity.bean.CategoryNovels;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.model.MaleModel;
import com.novel.collection.model.RankModel;

import java.util.List;

/**
 * @author
 * Created on 2019/11/6
 */
public class RankPresenter extends BasePresenter<IRankContract.View>
        implements IRankContract.Presenter {

    private IRankContract.Model mModel;

    public RankPresenter() {
        mModel = new RankModel(this);
    }

    @Override
    public void getDataSuccess(List<Noval_details> novelNameList) {
        if (isAttachView()) {
            getMvpView().getDataSuccess(novelNameList);
        }
    }

    @Override
    public void getDataError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getDataError(errorMsg);
        }
    }

    @Override
    public void getRankData(String id,String type,String id1,String type1,String z) {
        mModel.getRankData(id,type,id1,type1,z);
    }
}
