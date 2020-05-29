package com.novel.collection.model;

import android.util.Log;

import com.novel.collection.constant.Constant;
import com.novel.collection.constract.IPressContract;
import com.novel.collection.entity.bean.CategoryNovelsBean;
import com.novel.collection.entity.data.DiscoveryNovelData;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.util.ToastUtil;
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
    public void getCategoryNovels() {}
}
