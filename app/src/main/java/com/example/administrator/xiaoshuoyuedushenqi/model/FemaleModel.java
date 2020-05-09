package com.example.administrator.xiaoshuoyuedushenqi.model;

import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IFemaleContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovelsBean;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.HotRankBean;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author
 * Created on 2019/11/8
 */
public class FemaleModel implements IFemaleContract.Model {
    private static final int RANK_NOVEL_NUM = 3;     // 每个排行榜展示的小说数

    private IFemaleContract.Presenter mPresenter;
    private Gson mGson = new Gson();

    public FemaleModel(IFemaleContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    /**
     * 获取热门榜单信息
     */
    @Override
    public void getHotRankData() {
        final List<List<String>> novelNameList = new ArrayList<>();
        final int[] findCount = {0};
        for (int i = 0; i < Constant.FEMALE_HOT_RANK_NUM; i++) {
            novelNameList.add(new ArrayList<String>());
            String url = UrlObtainer.getRankNovels(Constant.FEMALE_HOT_RANK_ID.get(i));
            final int finalI = i;
            OkhttpUtil.getRequest(url, new OkhttpCall() {
                @Override
                public void onResponse(String json) {   // 得到 json 数据
                    HotRankBean bean = mGson.fromJson(json, HotRankBean.class);
                    if (bean.isOk()) {
                        List<HotRankBean.RankingBean.BooksBean> books = bean.getRanking().getBooks();
                        List<String> list = novelNameList.get(finalI);
                        for (int j = 0; j < Math.min(RANK_NOVEL_NUM, books.size()); j++) {
                            list.add(books.get(j).getTitle());
                        }
                    }
                    findCount[0]++;
                    if (findCount[0] == Constant.FEMALE_HOT_RANK_NUM) {
                        boolean isSucceed = true;
                        for (int j = 0; j < novelNameList.size(); j++) {
                            if (novelNameList.get(j).isEmpty()) {
                                isSucceed = false;
                            }
                        }
                        if (isSucceed) {
                            mPresenter.getHotRankDataSuccess(novelNameList);
                        } else {
                            mPresenter.getHotRankDataError("获取数据失败");
                        }
                    }
                }

                @Override
                public void onFailure(String errorMsg) {
                    findCount[0]++;
                    if (findCount[0] == Constant.FEMALE_HOT_RANK_NUM) {
                        mPresenter.getHotRankDataError("获取数据失败");
                    }
                }
            });
        }
    }

    /**
     * 获取发现页的分类小说数据
     */
    @Override
    public void getCategoryNovels() {}

}
