package com.example.administrator.xiaoshuoyuedushenqi.model;

import android.util.Log;

import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IPressContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovelsBean;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.util.ToastUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author
 * Created on 2019/12/22
 */
public class PressModel implements IPressContract.Model {

    private IPressContract.Presenter mPresenter;
    private Gson mGson = new Gson();

    public PressModel(IPressContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    /**
     * 获取发现页的分类小说数据
     */
    @Override
    public void getCategoryNovels() {
        final List<DiscoveryNovelData> dataList = new ArrayList<>();
        final int[] finishCount = {0};
        final int n = 4;  // 类别数
        final int num = 6;    // 获取条数（最终得到的可能多于 6 条）
        List<String> majorList = Arrays.asList(Constant.CATEGORY_MAJOR_CBXS,
                Constant.CATEGORY_MAJOR_QCYQ, Constant.CATEGORY_MAJOR_ZJMZ, Constant.CATEGORY_MAJOR_RWSK);
        for (int i = 0; i < n; i++) {
            String url = UrlObtainer.getCategoryNovels(Constant.CATEGORY_GENDER_PRESS,
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
                    if (finishCount[0] == n) {
                        mPresenter.getCategoryNovelsError("获取分类小说失败");
                    }
                }
            });
        }
    }
}
