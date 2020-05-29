package com.novel.collection.model;

import com.novel.collection.constant.Constant;
import com.novel.collection.constract.IAllNovelContract;
import com.novel.collection.entity.bean.CategoryNovelsBean;
import com.novel.collection.entity.data.ANNovelData;
import com.novel.collection.entity.data.RequestCNData;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * Created on 2019/12/21
 */
public class AllNovelModel implements IAllNovelContract.Model {

    private IAllNovelContract.Presenter mPresenter;
    private Gson mGson;

    public AllNovelModel(IAllNovelContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
        mGson = new Gson();
    }

    /**
     * 获取小说信息
     */
    @Override
    public void getNovels(final RequestCNData requestCNData) {
        String url = UrlObtainer.getCategoryNovels(requestCNData.getGender(), requestCNData.getMajor(),
                requestCNData.getMinor(), requestCNData.getType(),
                requestCNData.getStart(), requestCNData.getNum());
        OkhttpUtil.getRequest(url, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                CategoryNovelsBean bean = mGson.fromJson(json, CategoryNovelsBean.class);
                boolean isToEnd = false;
                if (bean.getTotal() <= requestCNData.getStart() + Constant.NOVEL_PAGE_NUM - 1) {
                    isToEnd = true;
                }
                List<ANNovelData> dataList = new ArrayList<>();
                List<CategoryNovelsBean.BooksBean> books = bean.getBooks();
                for (int i = 0; i < Math.min(books.size(), requestCNData.getNum()); i++) {
                    dataList.add(new ANNovelData(books.get(i).getTitle(), books.get(i).getAuthor(),
                            books.get(i).getShortIntro(),
                            "http://statics.zhuishushenqi.com" + books.get(i).getCover()));
                }
                mPresenter.getNovelsSuccess(dataList, isToEnd);
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getNovelsError(errorMsg);
            }
        });
    }
}
