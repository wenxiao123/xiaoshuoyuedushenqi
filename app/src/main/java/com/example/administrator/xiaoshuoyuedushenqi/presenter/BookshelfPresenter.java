package com.example.administrator.xiaoshuoyuedushenqi.presenter;

import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IBookshelfContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.epub.OpfData;
import com.example.administrator.xiaoshuoyuedushenqi.model.BookshelfModel;

import java.util.List;

/**
 * @author
 * Created on 2019/12/12
 */
public class BookshelfPresenter extends BasePresenter<IBookshelfContract.View>
        implements IBookshelfContract.Presenter {

    private IBookshelfContract.Model mModel;

    public BookshelfPresenter() {
        mModel = new BookshelfModel(this);
    }

    @Override
    public void queryAllBookSuccess(List<BookshelfNovelDbData> dataList) {
        if (isAttachView()) {
            getMvpView().queryAllBookSuccess(dataList);
        }
    }

    @Override
    public void queryAllBookError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().queryAllBookError(errorMsg);
        }
    }

    @Override
    public void unZipEpubSuccess(String filePath, OpfData opfData) {
        if (isAttachView()) {
            getMvpView().unZipEpubSuccess(filePath, opfData);
        }
    }


    @Override
    public void unZipEpubError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().unZipEpubError(errorMsg);
        }
    }

    @Override
    public void queryAllBook() {
        mModel.queryAllBook();
    }

    @Override
    public void unZipEpub(String filePath) {
        mModel.unZipEpub(filePath);
    }
}
