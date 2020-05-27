package com.example.administrator.xiaoshuoyuedushenqi.model;

import android.util.Log;

import com.example.administrator.xiaoshuoyuedushenqi.constract.IMaleLikeContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Catagorys;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Data;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
 * Created on 2019/11/6
 */
public class MaleLikeModel implements IMaleLikeContract.Model {
    private static final String TAG = "MaleModel";

    private IMaleLikeContract.Presenter mPresenter;
    private Gson mGson = new Gson();

    public MaleLikeModel(IMaleLikeContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }


    /**
     * 获取发现页的分类小说数据
     */
    @Override
    public void getCategoryNovels(int pid) {
        String url = UrlObtainer.GetUrl()+"/api/index/Type_one";
        RequestBody requestBody = new FormBody.Builder()
                .add("pid", pid+"")
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    Log.e("ASA", "onResponse: " + json);
                    Data bean = mGson.fromJson(json, Data.class);
                    List<Catagorys> catagorysList = bean.getData();
                    mPresenter.getCategoryNovelsSuccess(catagorysList);
                }catch (Exception ex){
                    mPresenter.getCategoryNovelsError("获取分类小说失败");
                }
            }

            @Override
            public void onFailure(String errorMsg) {
            mPresenter.getCategoryNovelsError("获取分类小说失败");
            }
        });
    }


}
