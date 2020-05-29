package com.novel.collection.model;

import android.content.Intent;
import android.util.Log;

import com.novel.collection.constant.Constant;
import com.novel.collection.constract.ICatalogContract;
import com.novel.collection.entity.bean.CatalogBean;
import com.novel.collection.entity.bean.Cataloginfo;
import com.novel.collection.entity.bean.Login_admin;
import com.novel.collection.entity.data.CatalogData;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.util.SpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
 * Created on 2019/11/17
 */
public class CatalogModel implements ICatalogContract.Model {

    private ICatalogContract.Presenter mPresenter;
    private Gson mGson = new Gson();
    public CatalogModel(ICatalogContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    /**
     * 请求书籍分类信息
     * @param id
     * @param posion
     * @param type
     */
    @Override
    public void getCatalogData(String id,int posion,int type) {
        String url = UrlObtainer.GetUrl()+"/"+"/api/index/Books_List";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)
                .add("type", type+"")
                .add("page",posion+"")
                .add("limit","30")
               // .add("limit", id)
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONObject jsonObject1=jsonObject.getJSONObject("data");
                        JSONArray object=jsonObject1.getJSONArray("data");
                        String weight=jsonObject1.getString("total");
                        List<Cataloginfo> catalogData=new ArrayList<>();
                        for(int i=0;i<object.length();i++){
                            catalogData.add(mGson.fromJson(object.getJSONObject(i).toString(),Cataloginfo.class));
                        }
                        mPresenter.getCatalogDataSuccess(catalogData, Integer.parseInt(weight));
                    }else {
                        mPresenter.getCatalogDataError("请求数据失败");
                    }

                } catch (JsonSyntaxException | JSONException e) {
                    mPresenter.getCatalogDataError(Constant.JSON_ERROR);
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getCatalogDataError(errorMsg);
            }
        });
    }
}
