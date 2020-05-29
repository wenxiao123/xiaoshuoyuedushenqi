package com.novel.collection.model;

import android.util.Log;

import com.novel.collection.constant.Constant;
import com.novel.collection.constract.ISearchResultContract;
import com.novel.collection.entity.bean.CategoryNovels;
import com.novel.collection.entity.bean.NovalInfo;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.bean.NovelsSourceBean;
import com.novel.collection.entity.data.NovelSourceData;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.httpUrlUtil.HttpUrlRequest;
import com.novel.collection.httpUrlUtil.HttpUrlRequestBuilder;
import com.novel.collection.httpUrlUtil.Request;
import com.novel.collection.httpUrlUtil.Response;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
 * Created on 2019/11/9
 */
public class SearchResultModel implements ISearchResultContract.Model {

    private ISearchResultContract.Presenter mPresenter;
    private Gson mGson = new Gson();

    public SearchResultModel(ISearchResultContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public void getNovelsSource(String novelName,int z) {
        String url = UrlObtainer.GetUrl()+"/"+"api/index/book_cke";
        RequestBody requestBody = new FormBody.Builder()
                .add("name", novelName)
                .add("page",z+"")
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                Log.e("QQQ", "onResponse: "+json);
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONObject object=jsonObject.getJSONObject("data");
                        JSONArray jsonArray=object.getJSONArray("data");
                        List<NovalInfo> novalDetailsList=new ArrayList<>();
                        for(int i=0;i<jsonArray.length();i++){
                            novalDetailsList.add(mGson.fromJson(jsonArray.getJSONObject(i).toString(),NovalInfo.class));
                        }
                        mPresenter.getNovelsSourceSuccess(novalDetailsList);
                    }else {
                        mPresenter.getNovelsSourceError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getNovelsSourceError(errorMsg);
            }
        });
    }
}
