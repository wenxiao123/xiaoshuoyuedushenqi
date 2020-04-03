package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class FeedbackActivity extends BaseActivity {
    EditText et_nickname, et_password;
    TextView iv_close, tv_notice2, tv_notice1;
    TextView tv_message;
    Login_admin login_admin;

    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.feedback;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        login_admin = (Login_admin) SpUtil.readObject(this);
    }


    @Override
    protected void initView() {
        et_nickname = findViewById(R.id.et_search_search_bar);
        et_password = findViewById(R.id.et_mobile_phone);
        iv_close = findViewById(R.id.iv_search_delete_search_text);
        tv_message = findViewById(R.id.txt_notice);
        tv_notice2 = findViewById(R.id.tv_notice2);
        tv_notice1 = findViewById(R.id.tv_notice1);
        findViewById(R.id.tv_feed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postExit();
            }
        });
    }


    @Override
    protected void doAfterInit() {
        String str1 = "<font color=\"#e60606\">*</font><font color=\"#000000\">请描述具体问题</font>";
        tv_notice1.setText(Html.fromHtml(str1));
        String str2 = "<font color=\"#e60606\">*</font><font color=\"#000000\">您的联系方式</font>";
        tv_notice2.setText(Html.fromHtml(str2));
        et_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str1 = "<font color=\"#FA5F4B\">"+charSequence.length()+"</font><font color=\"#9FA2B3\">/300</font>";
                iv_close.setText(Html.fromHtml(str1));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_nickname.setText("");
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void postExit() {
        String url = UrlObtainer.GetUrl() + "api/user/contact";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", login_admin.getToken())
                .add("contact", et_password.getText().toString())
                .add("text", et_nickname.getText().toString())
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        String msg=jsonObject.getString("msg");
                        showShortToast(msg);
                        finish();
                    } else {
                        showShortToast("提交失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                showShortToast(errorMsg);
            }
        });
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

}
