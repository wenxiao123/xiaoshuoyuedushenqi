package com.example.administrator.xiaoshuoyuedushenqi.model;

import android.util.Log;

import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IAllNovelContract;
import com.example.administrator.xiaoshuoyuedushenqi.constract.NovelInfoContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovelsBean;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.ANNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.RequestCNData;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

/**
 * @author WX
 * Created on 2019/12/21
 */
public class NovelInfoModel implements NovelInfoContract.Model {

    private NovelInfoContract.Presenter mPresenter;
    private Gson mGson;

    public NovelInfoModel(NovelInfoContract.Presenter mPresenter) {
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

    @Override
    public void getCategoryNovels() {
        final List<DiscoveryNovelData> dataList = new ArrayList<>();
        final int[] finishCount = {0};
        final int n = 3;  // 类别数
        final int num = 6;    // 获取条数（最终得到的可能多于 6 条）
        List<String> majorList = Arrays.asList(Constant.CATEGORY_MAJOR_XH,
                Constant.CATEGORY_MAJOR_DS, Constant.CATEGORY_MAJOR_WX);
        for (int i = 0; i < n; i++) {
            String url = UrlObtainer.getCategoryNovels(Constant.CATEGORY_GENDER_MALE,
                    majorList.get(i), num);
            dataList.add(null);
            final int finalI = i;
            OkhttpUtil.getRequest(url, new OkhttpCall() {
                @Override
                public void onResponse(String json) {   // 得到 json 数据
                    finishCount[0]++;
                    CategoryNovelsBean bean = mGson.fromJson(json, CategoryNovelsBean.class);
                    if (bean.isOk()) {
                        DiscoveryNovelData discoveryNovelData = new DiscoveryNovelData();
                        List<CategoryNovelsBean.BooksBean> books = bean.getBooks();
                        List<String> novelNameList = new ArrayList<>();
                        List<String> coverUrlList = new ArrayList<>();
                        for (int j = 0; j < Math.min(books.size(), num); j++) {
                            novelNameList.add(books.get(j).getTitle());
                            coverUrlList.add("http://statics.zhuishushenqi.com" + books.get(j).getCover());
                        }
                        discoveryNovelData.setNovelNameList(novelNameList);
                        discoveryNovelData.setCoverUrlList(coverUrlList);
                        dataList.set(finalI, discoveryNovelData);
                    }
                    if (finishCount[0] == n) {
                        boolean hasFinished = true;
                        for (int j = 0; j < n; j++) {
                            if (dataList.get(j) == null) {
                                hasFinished = false;
                                mPresenter.getCategoryNovelsError("获取分类小说失败");
                                break;
                            }
                        }
                        if (hasFinished) {
                            mPresenter.getCategoryNovelsSuccess(dataList);
                        }
                    }
                }

                @Override
                public void onFailure(String errorMsg) {
                    finishCount[0]++;
                    Log.d(TAG, "getCategoryNovels onFailure: " + errorMsg);
                    if (finishCount[0] == n) {
                        mPresenter.getCategoryNovelsError("获取分类小说失败");
                    }
                }
            });
        }
    }
}
