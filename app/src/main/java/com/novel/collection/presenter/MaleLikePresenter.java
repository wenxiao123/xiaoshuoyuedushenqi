package com.novel.collection.presenter;

import com.novel.collection.base.BasePresenter;
import com.novel.collection.constract.IMaleLikeContract;
import com.novel.collection.entity.bean.Catagorys;
import com.novel.collection.model.MaleLikeModel;

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
    public void getCategoryNovels(int pid) {
        mModel.getCategoryNovels(pid);
    }
}
