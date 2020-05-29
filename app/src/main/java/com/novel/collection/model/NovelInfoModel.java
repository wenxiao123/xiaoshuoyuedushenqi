package com.novel.collection.model;

import android.util.Log;

import com.novel.collection.constant.Constant;
import com.novel.collection.constract.NovelInfoContract;
import com.novel.collection.entity.bean.CategoryNovelsBean;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.data.DiscoveryNovelData;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
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
    public void getNovels(final String  id) {
        String url = UrlObtainer.GetUrl()+"/"+"api/index/Book_data";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)
                .add("limit", "8")
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                Log.e("WWW", "onResponse: "+json);
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONObject object=jsonObject.getJSONObject("data");
                        Noval_details noval_details=mGson.fromJson(object.toString(),Noval_details.class);
                        JSONArray jsonArray=object.getJSONArray("run_list");
                        List<Noval_details> novalDetailsList=new ArrayList<>();
                        for(int i=0;i<jsonArray.length();i++){
                            novalDetailsList.add(mGson.fromJson(jsonArray.getJSONObject(i).toString(),Noval_details.class));
                        }
                        mPresenter.getNovelsSuccess(noval_details,novalDetailsList);
                    }else {
                        mPresenter.getNovelsError("请求失败,请检查网络");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mPresenter.getNovelsError("请求失败,请检查网络");
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getNovelsError("请求失败,请检查网络");
            }
        });
    }

    @Override
    public void getCategoryNovels() {}
}
