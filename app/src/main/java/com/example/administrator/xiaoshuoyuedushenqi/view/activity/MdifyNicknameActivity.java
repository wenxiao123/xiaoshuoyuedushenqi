package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
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

public class MdifyNicknameActivity extends BaseActivity {
    EditText et_nickname;
    ImageView iv_close;
    TextView tv_message,tv_sure;
    Login_admin login_admin;
    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.modify_nickname;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        login_admin= (Login_admin) SpUtil.readObject(this);
    }


    @Override
    protected void initView() {
        tv_sure=findViewById(R.id.tv_sure);
        et_nickname=findViewById(R.id.et_search_search_bar);
        iv_close=findViewById(R.id.iv_search_delete_search_text);
        tv_message=findViewById(R.id.txt_notice);
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postModify("nickname",et_nickname.getText().toString());
            }
        });
        findViewById(R.id.back1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    protected void doAfterInit() {
        et_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
             if(charSequence.length()>0){
                 if(charSequence.length()>8){
                   showShortToast("输入长度限输入8个字符");
                 }
                 iv_close.setVisibility(View.VISIBLE);
                 tv_message.setVisibility(View.VISIBLE);
             }else{
                 iv_close.setVisibility(View.GONE);
                 tv_message.setVisibility(View.GONE);
             }
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
    }
    public void postModify(String nickname,String name) {
        String url = UrlObtainer.GetUrl()+"api/user/profile";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", login_admin.getToken())
                .add(nickname, name)
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        showShortToast("修改成功");
                        finish();
                    }else {
                        showShortToast("修改失败");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

}
