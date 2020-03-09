package com.example.administrator.xiaoshuoyuedushenqi.presenter;

import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IAllNovelContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.ANNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.RequestCNData;
import com.example.administrator.xiaoshuoyuedushenqi.model.AllNovelModel;

import java.util.List;

/**
 * @author WX
 * Created on 2019/12/21
 */
public class AllNovelPresenter extends BasePresenter<IAllNovelContract.View>
        implements IAllNovelContract.Presenter {

    private IAllNovelContract.Model mModel;

    public AllNovelPresenter() {
        mModel = new AllNovelModel(this);
    }

    @Override
    public void getNovelsSuccess(List<ANNovelData> dataList, boolean isEnd) {
        if (isAttachView()) {
            getMvpView().getNovelsSuccess(dataList, isEnd);
        }
    }

    @Override
    public void getNovelsError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getNovelsError(errorMsg);
        }
    }

    @Override
    public void getNovels(RequestCNData requestCNData) {
        mModel.getNovels(requestCNData);
    }
}
