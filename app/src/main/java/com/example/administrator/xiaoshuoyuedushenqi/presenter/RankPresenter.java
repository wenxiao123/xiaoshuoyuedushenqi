package com.example.administrator.xiaoshuoyuedushenqi.presenter;

import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IMaleContract;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IRankContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovels;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.model.MaleModel;
import com.example.administrator.xiaoshuoyuedushenqi.model.RankModel;

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
    public void getRankData(String id,String type,String id1,String type1) {
        mModel.getRankData(id,type,id1,type1);
    }
}
