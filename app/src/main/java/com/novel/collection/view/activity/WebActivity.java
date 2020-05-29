package com.novel.collection.view.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.novel.collection.R;
import com.novel.collection.base.BaseActivity;
import com.novel.collection.base.BasePresenter;
import com.novel.collection.entity.bean.CategoryNovels;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.util.StatusBarUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class WebActivity extends BaseActivity {
    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    WebView webView;
    WebSettings settings;
    TextView title;

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        webView = findViewById(R.id.web);
        title = findViewById(R.id.title);
        Intent i = getIntent();
        String type = i.getStringExtra("type");
        String name = i.getStringExtra("title");
        if (name != null) {
            title.setText(name);
        }
        getHotRankData(type);
        ImageView relativeLayout = findViewById(R.id.back2);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void doAfterInit() {

    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    public void getHotRankData(String id) {
        String url = UrlObtainer.GetUrl() + "/api/index/get_link";
        RequestBody requestBody = new FormBody.Builder()
                .add("type", id)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        String content = object.getString("content");
                        //webView.loadUrl(url);
                        webView.setWebViewClient(new WebViewClient());
                        //使用简单的loadData()方法总会导致乱码，有可能是Android API的Bug
                        //webView.loadData(data, "text/html", "GBK");
                        webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
                        //webView.set(articleDetails);;
                        settings = webView.getSettings();
                        settings.setJavaScriptEnabled(true);
                        settings.setUseWideViewPort(true);
                        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
                        settings.setTextZoom(250);
                        settings.setLoadWithOverviewMode(true);
                    } else {
                        showShortToast("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                showShortToast("请求错误");
            }
        });
    }
}
