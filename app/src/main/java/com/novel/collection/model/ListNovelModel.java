package com.novel.collection.model;

import android.util.Log;

import com.novel.collection.constant.Constant;
import com.novel.collection.constract.IAllNovelContract;
import com.novel.collection.constract.IListNovelContract;
import com.novel.collection.entity.bean.CategoryNovelsBean;
import com.novel.collection.entity.bean.NovalInfo;
import com.novel.collection.entity.data.ANNovelData;
import com.novel.collection.entity.data.RequestCNData;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
 * Created on 2019/12/21
 */
public class ListNovelModel implements IListNovelContract.Model {

    private IListNovelContract.Presenter mPresenter;
    private Gson mGson;

    public ListNovelModel(IListNovelContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
        mGson = new Gson();
    }

    /**
     * 获取小说信息
     */
    @Override
    public void getNovels(String id,String type,String z) {
        RequestBody requestBody = new FormBody.Builder()
                .add("type", 1+"")
                .add("sort", 3+"")
                .add("page", z)
                .add("category_id", id)
                .add("order", type)
                .add("limit", "8")
                .build();
        OkhttpUtil.getpostRequest(UrlObtainer.GetUrl()+"/api/index/Book_List",requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                //Log.e("qqq", "onResponse: "+json);
                try {
                    JSONObject jsonObject=new JSONObject (json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONObject jsonObject1=jsonObject.getJSONObject("data");
                        JSONArray jsonArray=jsonObject1.getJSONArray("data");
                        List<NovalInfo> dataList=new ArrayList<>();
                        for(int z=0;z<jsonArray.length();z++){
                            dataList.add(mGson.fromJson(jsonArray.getJSONObject(z).toString(),NovalInfo.class));
                        }
                        mPresenter.getNovelsSuccess(dataList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getNovelsError(errorMsg);
            }
        });
    }
}
