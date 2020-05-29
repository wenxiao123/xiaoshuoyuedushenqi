package com.novel.collection.presenter;

import com.novel.collection.base.BasePresenter;
import com.novel.collection.constract.IBookshelfContract;
import com.novel.collection.entity.data.BookshelfNovelDbData;
import com.novel.collection.entity.epub.OpfData;
import com.novel.collection.model.BookshelfModel;

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
