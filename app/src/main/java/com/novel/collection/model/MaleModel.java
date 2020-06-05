package com.novel.collection.model;

import android.util.Log;

import com.novel.collection.constant.Constant;
import com.novel.collection.constract.IMaleContract;
import com.novel.collection.entity.bean.CategoryNovels;
import com.novel.collection.entity.bean.CategoryNovelsBean;
import com.novel.collection.entity.bean.HotRankBean;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.bean.Wheel;
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
 * Created on 2019/11/6
 */
public class MaleModel implements IMaleContract.Model {
    private static final String TAG = "MaleModel";
    private static final int RANK_NOVEL_NUM = 3;     // 每个排行榜展示的小说数

    private IMaleContract.Presenter mPresenter;
    private Gson mGson = new Gson();

    public MaleModel(IMaleContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    /**
     * 获取热门榜单信息
     */
    @Override
    public void getHotRankData(String id) {
        String url = UrlObtainer.GetUrl()+"/"+"api/Rmlist/Lan_list";
        RequestBody requestBody = new FormBody.Builder()
                .add("type", id)
                .add("limit", "4")
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        List<CategoryNovels> novalDetailsList=new ArrayList<>();
                        for(int i=0;i<jsonArray.length();i++){
                            novalDetailsList.add(mGson.fromJson(jsonArray.getJSONObject(i).toString(),CategoryNovels.class));
                        }
                        mPresenter.getHotRankDataSuccess(novalDetailsList);
                    }else {
                        mPresenter.getHotRankDataError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getHotRankDataError(errorMsg);
            }
        });
    }

    @Override
    public void getNewRankData(String id) {
        String url = UrlObtainer.GetUrl()+"/"+"api/Rmlist/New_List";
        RequestBody requestBody = new FormBody.Builder()
                .add("type", id)
                .add("limit", "4")
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
               // Log.e("QQQ", "onResponse: "+json);
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
                        mPresenter.getNewDataSuccess(novalDetailsList);
                    }else {
                        mPresenter.getNewDataError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getNewDataError(errorMsg);
            }
        });
    }

    /**
     * 获取发现页的分类小说数据
     */
    @Override
    public void getCategoryNovels(String id) {
        String url = UrlObtainer.GetUrl()+"/"+"api/Rmlist/Rem_List";
        RequestBody requestBody = new FormBody.Builder()
                .add("type", id)
                .add("limit", "4")
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
               // Log.e("QQQ", "onResponse: "+id+" "+json);
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
                        mPresenter.getCategoryNovelsSuccess(novalDetailsList);
                    }else {
                        mPresenter.getCategoryNovelsError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getCategoryNovelsError(errorMsg);
            }
        });
    }

    @Override
    public void getListImage(String type) {
        String url = UrlObtainer.GetUrl()+"/"+"api/Rmlist/Get_Wheel";
        RequestBody requestBody = new FormBody.Builder()
                .add("type", type)
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONObject object=jsonObject.getJSONObject("data");
                        JSONArray jsonArray=object.getJSONArray("data");
                        List<Wheel> novalDetailsList=new ArrayList<>();
                        for(int i=0;i<jsonArray.length();i++){
                            novalDetailsList.add(mGson.fromJson(jsonArray.getJSONObject(i).toString(),Wheel.class));
                        }
                        mPresenter.getListImageSuccess(novalDetailsList);
                    }else {
                        mPresenter.getListImageError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getListImageError(errorMsg);
            }
        });
    }


}
