package com.example.administrator.xiaoshuoyuedushenqi.model;

import android.util.Log;

import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constract.ICatalogContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CatalogBean;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Cataloginfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.CatalogData;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
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

    @Override
    public void getCatalogData(String id,int posion) {
        String url = UrlObtainer.GetUrl()+"api/index/Books_List";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)
                .add("page",posion+"")
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
                        List<Cataloginfo> catalogData=new ArrayList<>();
                        for(int i=0;i<object.length();i++){
                            catalogData.add(mGson.fromJson(object.getJSONObject(i).toString(),Cataloginfo.class));
                        }
                        mPresenter.getCatalogDataSuccess(catalogData);
                    }else {
                        mPresenter.getCatalogDataError("请求数据失败");
                    }
//                    CatalogBean catalogBean = mGson.fromJson(json, CatalogBean.class);
//                    if (catalogBean.getCode() != 0) {
//                        mPresenter.getCatalogDataError(Constant.NOT_FOUND_CATALOG_INFO);
//                        return;
//                    }
//                    List<CatalogBean.ListBean> list = catalogBean.getList();
//                    List<String> chapterNameList = new ArrayList<>();
//                    List<String> chapterUrlList = new ArrayList<>();
//                    for (int i = 0; i < list.size(); i++) {
//                        chapterNameList.add(list.get(i).getNum());
//                        chapterUrlList.add(list.get(i).getUrl());
//                    }

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