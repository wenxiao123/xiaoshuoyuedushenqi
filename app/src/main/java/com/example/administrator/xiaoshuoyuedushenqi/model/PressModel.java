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
    public void getCategoryNovels() {}
}
