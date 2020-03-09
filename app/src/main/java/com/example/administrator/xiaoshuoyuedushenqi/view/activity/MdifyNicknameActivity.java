package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.MainSetAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MdifyNicknameActivity extends BaseActivity {
    EditText et_nickname;
    ImageView iv_close;
    TextView tv_message;
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

    }


    @Override
    protected void initView() {
        et_nickname=findViewById(R.id.et_search_search_bar);
        iv_close=findViewById(R.id.iv_search_delete_search_text);
        tv_message=findViewById(R.id.txt_notice);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

}
