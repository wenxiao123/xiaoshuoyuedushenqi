package com.example.administrator.xiaoshuoyuedushenqi.model;

import android.util.Log;

import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constract.ISearchResultContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovels;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.NovalInfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.NovelsSourceBean;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.NovelSourceData;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.httpUrlUtil.HttpUrlRequest;
import com.example.administrator.xiaoshuoyuedushenqi.httpUrlUtil.HttpUrlRequestBuilder;
import com.example.administrator.xiaoshuoyuedushenqi.httpUrlUtil.Request;
import com.example.administrator.xiaoshuoyuedushenqi.httpUrlUtil.Response;
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
    public void getNovelsSource(String novelName) {
        String url = UrlObtainer.GetUrl()+"api/index/book_cke";
        RequestBody requestBody = new FormBody.Builder()
                .add("name", novelName)
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
              //  Log.e("QQQ", "onResponse: "+json);
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
