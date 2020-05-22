package com.example.administrator.xiaoshuoyuedushenqi.model;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.administrator.xiaoshuoyuedushenqi.constract.IRankContract;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IReadContract;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IReakcoredContract;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovels;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_Readcored;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
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
public class ReadcoredModel implements IReakcoredContract.Model {
    private static final String TAG = "MaleModel";
    private static final int RANK_NOVEL_NUM = 3;     // 每个排行榜展示的小说数
    private DatabaseManager mDbManager;
    private IReakcoredContract.Presenter mPresenter;
    private Gson mGson = new Gson();

    public ReadcoredModel(IReakcoredContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
        mDbManager = DatabaseManager.getInstance();
    }

    @Override
    public void getReadcoredData(String token, String page) {
        String url = UrlObtainer.GetUrl()+"/"+"api/lookbook/index";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("page", page)
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                Log.e("QQQ", "onResponse: "+page+" "+json);
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONObject object=jsonObject.getJSONObject("data");
                        JSONArray jsonArray=object.getJSONArray("data");
                        List<Noval_Readcored> novalDetailsList=new ArrayList<>();
                        for(int i=0;i<jsonArray.length();i++){
                            novalDetailsList.add(mGson.fromJson(jsonArray.getJSONObject(i).toString(),Noval_Readcored.class));
                        }
                        mPresenter.getReadcoredDataSuccess(novalDetailsList);
                    }else {
                        mPresenter.getReadcoredDataError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getReadcoredDataError(errorMsg);
            }
        });
    }
    /**
     * 从数据库中查询所有书籍信息
     */

    public void queryAllBook() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Noval_Readcored> dataList = mDbManager.queryAllReadcordefNovel();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.queryAllBookSuccess(dataList);
                    }
                });
            }
        }).start();
    }
    @Override
    public void getDelectReadcoredData(String token, String novel_id,int type) {
        if(novel_id==null){
            return;
        }
        String url = UrlObtainer.GetUrl()+"/"+"api/Lookbook/del";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("type", type+"")
                .add("novel_id", novel_id)
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                       String msg=jsonObject.getString("msg");
                        mPresenter.getDelectReadcoredDataSuccess(msg);
                    }else {
                        mPresenter.getDelectReadcoredDataError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getReadcoredDataError(errorMsg);
            }
        });
    }
}
