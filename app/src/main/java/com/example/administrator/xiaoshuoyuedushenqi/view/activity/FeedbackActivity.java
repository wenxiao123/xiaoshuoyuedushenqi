package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;

public class FeedbackActivity extends BaseActivity {
    EditText et_nickname;
    TextView iv_close,tv_notice2,tv_notice1;
    TextView tv_message;
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

    }


    @Override
    protected void initView() {
        et_nickname=findViewById(R.id.et_search_search_bar);
        iv_close=findViewById(R.id.iv_search_delete_search_text);
        tv_message=findViewById(R.id.txt_notice);
        tv_notice2=findViewById(R.id.tv_notice2);
        tv_notice1=findViewById(R.id.tv_notice1);
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
                iv_close.setText(charSequence.length()+"/"+300);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

}
