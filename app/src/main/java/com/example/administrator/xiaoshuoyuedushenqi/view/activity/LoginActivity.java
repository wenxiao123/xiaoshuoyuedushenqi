package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

public class LoginActivity extends BaseActivity {
    EditText et_mobile_phone,et_vertical;
    TextView tv_notice,tv_login;
    ImageView img_select;
    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {

    }
    boolean isChecked;
    @Override
    protected void initView() {
        findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    });
        et_mobile_phone=findViewById(R.id.et_login_phone);
        et_mobile_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if(isPhoneNumber(charSequence.toString())){
                   tv_notice.setVisibility(View.GONE);
               }else {
                   tv_notice.setVisibility(View.VISIBLE);
               }
               if(charSequence.length()==0){
                   tv_notice.setVisibility(View.GONE);
               }
               if(charSequence.length()>0&&!et_vertical.getText().toString().trim().equals("")){
                   tv_login.setBackground(getResources().getDrawable(R.drawable.bachground_yellow_light));
               }else {
                   tv_login.setBackground(getResources().getDrawable(R.drawable.bachground_ash));
               }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_mobile_phone.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(!isPhoneNumber2(et_mobile_phone.getText().toString()))
                    tv_notice.setVisibility(View.VISIBLE);
                else
                    tv_notice.setVisibility(View.GONE);

                }
            }
        });
        et_vertical=findViewById(R.id.et_vertical);
        et_vertical.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0&&!et_mobile_phone.getText().toString().trim().equals("")){
                    tv_login.setBackground(getResources().getDrawable(R.drawable.bachground_yellow_light));
                }else {
                    tv_login.setBackground(getResources().getDrawable(R.drawable.bachground_ash));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        tv_notice=findViewById(R.id.tv_notice_phone);
        tv_login=findViewById(R.id.tv_login);
        img_select=findViewById(R.id.img_select);
        img_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isChecked){
                    isChecked=true;
                    img_select.setImageResource(R.mipmap.sys_selected);
                }else {
                    isChecked=false;
                    img_select.setImageResource(R.mipmap.sys_select);
                }
            }
        });
    }

    @Override
    protected void doAfterInit() {

    }
    public static boolean isPhoneNumber(String input) {// 判断手机号码是否规则
        int z=input.length();
        if(z<11){
            for(int i=0;i<11-z;i++){
                input=input+"3";
            }
        }
        String regex = "^(0|86|17951)?(13[0-9]|15[012356789]|16[6]|19[89]]|17[01345678]|18[0-9]|14[579])[0-9]{8}$";
        Pattern p = Pattern.compile(regex);
        return p.matches(regex, input);//如果不是号码，则返回false，是号码则返回true

    }

    public static boolean isPhoneNumber2(String input) {// 判断手机号码是否规则
        String regex = "^(0|86|17951)?(13[0-9]|15[012356789]|16[6]|19[89]]|17[01345678]|18[0-9]|14[579])[0-9]{8}$";
        Pattern p = Pattern.compile(regex);
        return p.matches(regex, input);//如果不是号码，则返回false，是号码则返回true

    }
    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }
}
