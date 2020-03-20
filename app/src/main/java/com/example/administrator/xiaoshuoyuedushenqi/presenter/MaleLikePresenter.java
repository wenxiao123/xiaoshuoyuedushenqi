package com.example.administrator.xiaoshuoyuedushenqi.presenter;

import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IMaleLikeContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Catagorys;
import com.example.administrator.xiaoshuoyuedushenqi.model.MaleLikeModel;

import java.util.List;

/**
 * @author
 * Created on 2019/11/6
 */
public class MaleLikePresenter extends BasePresenter<IMaleLikeContract.View>
        implements IMaleLikeContract.Presenter {

    private IMaleLikeContract.Model mModel;

    public MaleLikePresenter() {
        mModel = new MaleLikeModel(this);
    }
    @Override
    public void getCategoryNovelsSuccess(List<Catagorys> dataList) {
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
