package com.example.administrator.xiaoshuoyuedushenqi.model;

import android.util.Log;

import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constract.NovelInfoContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovelsBean;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
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
                        mPresenter.getNovelsError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mPresenter.getNovelsError("请求错误");
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getNovelsError(errorMsg);
            }
        });
    }

    @Override
    public void getCategoryNovels() {}
}
