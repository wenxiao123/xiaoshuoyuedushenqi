package com.novel.collection.presenter;

import com.novel.collection.base.BasePresenter;
import com.novel.collection.constract.IAllNovelContract;
import com.novel.collection.constract.IListNovelContract;
import com.novel.collection.entity.bean.NovalInfo;
import com.novel.collection.entity.data.ANNovelData;
import com.novel.collection.entity.data.RequestCNData;
import com.novel.collection.model.AllNovelModel;
import com.novel.collection.model.ListNovelModel;

import java.util.List;

/**
 * @author
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
    public void getNovels(String id,String type,String z) {
        mModel.getNovels(id,type,z);
    }
}
