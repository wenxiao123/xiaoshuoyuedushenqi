package com.example.administrator.xiaoshuoyuedushenqi.model;

import android.util.Log;

import com.example.administrator.xiaoshuoyuedushenqi.constract.ILoginContract;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IRankContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
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

import javax.security.auth.login.LoginException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
 * Created on 2019/11/6
 */
public class LoginModel implements ILoginContract.Model {
    private static final String TAG = "LoginModel";
    private static final int RANK_NOVEL_NUM = 3;     // 每个排行榜展示的小说数

    private ILoginContract.Presenter mPresenter;
    private Gson mGson = new Gson();

    public LoginModel(ILoginContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public void getVertical(String moildle) {
        String url = UrlObtainer.GetUrl() + "/api/index/get_code";
        RequestBody requestBody = new FormBody.Builder()
                .add("mobile", moildle)
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据

                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        String data=jsonObject.getString("msg");
                        mPresenter.getVerticalSuccess(data);
                    }else {
                        mPresenter.getVerticalError("请求失败");
                    }
                } catch (JSONException e) {
                        mPresenter.getVerticalError("请求失败");
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getVerticalError("请求失败");
            }
        });
    }

    @Override
    public void getLogin(String diceviid,String mobile, String code) {
            String url = UrlObtainer.GetUrl() + "/api/index/mobilelogin";
            RequestBody requestBody = new FormBody.Builder()
                    .add("mobile", mobile)
                    .add("code", code)
                    .add("uuid", diceviid)
                    .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                Log.e("qqq", "onResponse: "+diceviid+" "+mobile+" "+" "+code+" "+json);
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONObject object=jsonObject.getJSONObject("data");
                        Login_admin login_admin=mGson.fromJson(object.toString(),Login_admin.class);
                        mPresenter.getLoginSuccess(login_admin);
                    }else {
                        mPresenter.getLoginError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                mPresenter.getLoginError(errorMsg);
            }
        });
    }
}
