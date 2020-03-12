package com.example.administrator.xiaoshuoyuedushenqi.presenter;

import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IAllNovelContract;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IListNovelContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.NovalInfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.ANNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.RequestCNData;
import com.example.administrator.xiaoshuoyuedushenqi.model.AllNovelModel;
import com.example.administrator.xiaoshuoyuedushenqi.model.ListNovelModel;

import java.util.List;

/**
 * @author WX
 * Created on 2019/12/21
 */
public class ListNovelPresenter extends BasePresenter<IListNovelContract.View>
        implements IListNovelContract.Presenter {

    private IListNovelContract.Model mModel;

    public ListNovelPresenter() {
        mModel = new ListNovelModel(this);
    }


    @Override
    public void getNovelsSuccess(List<NovalInfo> dataList) {
        if (isAttachView()) {
            getMvpView().getNovelsSuccess(dataList);
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
