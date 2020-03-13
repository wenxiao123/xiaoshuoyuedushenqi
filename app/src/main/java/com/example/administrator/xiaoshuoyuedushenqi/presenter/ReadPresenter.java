package com.example.administrator.xiaoshuoyuedushenqi.presenter;

import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IReadContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DetailedChapterData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.epub.EpubData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.epub.OpfData;
import com.example.administrator.xiaoshuoyuedushenqi.model.ReadModel;

import java.util.List;

/**
 * @author WX
 * Created on 2019/11/25
 */
public class ReadPresenter extends BasePresenter<IReadContract.View>
        implements IReadContract.Presenter {

    private IReadContract.Model mModel;

    public ReadPresenter() {
        mModel = new ReadModel(this);
    }

    @Override
    public void getChapterUrlListSuccess(List<String> chapterUrlList, List<String> chapterNameList) {
        if (isAttachView()) {
            getMvpView().getChapterUrlListSuccess(chapterUrlList, chapterNameList);
        }
    }

    @Override
    public void getChapterUrlListError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getChapterUrlListError(errorMsg);
        }
    }

    @Override
    public void getDetailedChapterDataSuccess(DetailedChapterData data) {
        if (isAttachView()) {
            getMvpView().getDetailedChapterDataSuccess(data);
        }
    }

    @Override
    public void getDetailedChapterDataError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getDetailedChapterDataError(errorMsg);
        }
    }

    @Override
    public void loadTxtSuccess(String text) {
        if (isAttachView()) {
            getMvpView().loadTxtSuccess(text);
        }
    }

    @Override
    public void loadTxtError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().loadTxtError(errorMsg);
        }
    }

    @Override
    public void getOpfDataSuccess(OpfData opfData) {
        if (isAttachView()) {
            getMvpView().getOpfDataSuccess(opfData);
        }
    }

    @Override
    public void getOpfDataError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getOpfDataError(errorMsg);
        }
    }

    @Override
    public void getEpubChapterDataSuccess(List<EpubData> dataList) {
        if (isAttachView()) {
            getMvpView().getEpubChapterDataSuccess(dataList);
        }
    }

    @Override
    public void getEpubChapterDataError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getEpubChapterDataError(errorMsg);
        }
    }

    @Override
    public void getChapterList(String url) {
        mModel.getChapterList(url);
    }

    @Override
    public void getDetailedChapterData(String url) {
        mModel.getDetailedChapterData(url);
    }

    @Override
    public void getDetailedChapterData(String bookid, String id) {
        mModel.getDetailedChapterData(bookid,id);
    }

    @Override
    public void loadTxt(String filePath) {
        mModel.loadTxt(filePath);
    }

    @Override
    public void getOpfData(String filePath) {
        mModel.getOpfData(filePath);
    }

    @Override
    public void getEpubChapterData(String parentPath, String filePath) {
        mModel.getEpubChapterData(parentPath, filePath);
    }
}
