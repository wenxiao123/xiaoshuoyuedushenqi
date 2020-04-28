package com.example.administrator.xiaoshuoyuedushenqi.model;

import android.util.Log;

import com.example.administrator.xiaoshuoyuedushenqi.constract.IMaleContract;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IRankContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovels;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
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
 * Created on 2019/11/6
 */
public class RankModel implements IRankContract.Model {
    private static final String TAG = "MaleModel";
    private static final int RANK_NOVEL_NUM = 3;     // 每个排行榜展示的小说数

    private IRankContract.Presenter mPresenter;
    private Gson mGson = new Gson();

    public RankModel(IRankContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }


    @Override
    public void getRankData(String id,String type,String id1,String type1,String z) {
        String url;
        RequestBody requestBody;
        if(type.equals("1")) {
            url = UrlObtainer.GetUrl() + "/api/Rmlist/Rem_List";
             requestBody = new FormBody.Builder()
                    .add("type", id)
                    .add("sort", id1)
                     .add("page", z)
                    .add("limit", "8")
                    .build();
        }else if(type.equals("2")) {
            url = UrlObtainer.GetUrl() + "/api/Rmlist/New_List";
            requestBody = new FormBody.Builder()
                    .add("type", id)
                    .add("sort", id1)
                    .add("page", z)
                    .add("limit", "8")
                    .build();
        }else {
            url = UrlObtainer.GetUrl() + "/api/index/Book_List";
            requestBody = new FormBody.Builder()
                    .add("type", id)
                    .add("sort", id1)
                    .add("page", z)
                    .add("category_id", type1)
                    .add("order", 1+"")
                    .add("limit", "8")
                    .build();
        }
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
            // Log.e(":QQQ", "onResponse: "+json);
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONObject object=jsonObject.getJSONObject("data");
                        JSONArray jsonArray=object.getJSONArray("data");
                        List<Noval_details> novalDetailsList=new ArrayList<>();
                        for(int i=0;i<jsonArray.length();i++){
                            novalDetailsList.add(mGson.fromJson(jsonArray.getJSONObject(i).toString(),Noval_details.class));
                        }
                        mPresenter.getDataSuccess(novalDetailsList);
                    }else {
                        mPresenter.getDataError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getDataError(errorMsg);
            }
        });
    }
}
