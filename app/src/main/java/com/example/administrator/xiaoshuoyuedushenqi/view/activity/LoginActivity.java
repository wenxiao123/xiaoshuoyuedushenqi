package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.app.App;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.ILoginContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.LoginPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.CarOnlyIdUtils;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.widget.OvalImageView;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

public class LoginActivity extends BaseActivity<LoginPresenter> implements ILoginContract.View {
    EditText et_mobile_phone, et_vertical;
    TextView tv_notice, tv_login, tv_verification;
    ImageView img_select;
    OvalImageView img_title;
    TextView tv_user, tv_yinsi;
    private TimeCount time;

    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    /**
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected LoginPresenter getPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected void initData() {
        time = new TimeCount(60000, 1000);
    }

    boolean isChecked;

    @Override
    protected void initView() {
        img_title = findViewById(R.id.img_title);
        Glide.with(this)
                .load(R.mipmap.admin)
                .apply(new RequestOptions()
                        )
                .into(img_title);
        findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // finish();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
        tv_verification = findViewById(R.id.tv_verification);
        tv_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPhoneNumber(et_mobile_phone.getText().toString())){
                    mPresenter.getVertical(et_mobile_phone.getText().toString());
                    showShortToast("已发送");
                    tv_verification.setEnabled(false);
                }else {
                    showShortToast("电话号码格式错误！");
                }
            }
        });
        et_mobile_phone = findViewById(R.id.et_login_phone);
        et_mobile_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isPhoneNumber(charSequence.toString())) {
                    tv_notice.setVisibility(View.GONE);
                } else {
                    tv_notice.setVisibility(View.VISIBLE);
                }
                if (charSequence.length() == 0) {
                    tv_notice.setVisibility(View.GONE);
                    tv_verification.setBackground(getResources().getDrawable(R.drawable.bachground_ash));
                } else {
                    tv_verification.setBackground(getResources().getDrawable(R.drawable.bachground_yellow_light));
                }
                if (charSequence.length() > 0 && !et_vertical.getText().toString().trim().equals("")) {
                    tv_login.setBackground(getResources().getDrawable(R.drawable.bachground_yellow_light));
                } else {
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
                if (!hasFocus) {
                    if (!isPhoneNumber2(et_mobile_phone.getText().toString()))
                        tv_notice.setVisibility(View.VISIBLE);
                    else
                        tv_notice.setVisibility(View.GONE);

                }
            }
        });
        et_vertical = findViewById(R.id.et_vertical);
        et_vertical.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0 && !et_mobile_phone.getText().toString().trim().equals("")) {
                    tv_login.setBackground(getResources().getDrawable(R.drawable.bachground_yellow_light));
                    tv_verification.setClickable(true);
                } else {
                    tv_login.setBackground(getResources().getDrawable(R.drawable.bachground_ash));
                    tv_verification.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        tv_notice = findViewById(R.id.tv_notice_phone);
        tv_login = findViewById(R.id.tv_login);
        img_select = findViewById(R.id.img_select);
        img_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isChecked) {
                    isChecked = true;
                    img_select.setImageResource(R.mipmap.sys_selected);
                } else {
                    isChecked = false;
                    img_select.setImageResource(R.mipmap.sys_select);
                }
            }
        });
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChecked) {
                    String Diviceid= CarOnlyIdUtils.getOnlyID(LoginActivity.this);
                    mPresenter.getLogin(Diviceid,et_mobile_phone.getText().toString(), et_vertical.getText().toString());
                } else {
                    showShortToast("请先同意用户协议与隐私条款");
                }
            }
        });
        tv_user = findViewById(R.id.tv_user);
        tv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(!isExit1){
                   isExit1=true;
                   Intent intent = new Intent(LoginActivity.this, WebActivity.class);
                   intent.putExtra("type", "3");
                   intent.putExtra("title", "用户协议");
                   startActivity(intent);
               }

            }
        });

        tv_yinsi = findViewById(R.id.tv_yinsi);
        tv_yinsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isExit2) {
                    isExit2 = true;
                    Intent intent = new Intent(LoginActivity.this, WebActivity.class);
                    intent.putExtra("type", "2");
                    intent.putExtra("title", "隐私条款");
                    startActivity(intent);
                }
            }
        });
    }
    private static boolean isExit1 = false,isExit2 = false;
    @Override
    protected void doAfterInit() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        isExit1 = false;
        isExit2 = false;
    }

    public static boolean isPhoneNumber(String input) {// 判断手机号码是否规则
        int z = input.length();
        if (z < 11) {
            for (int i = 0; i < 11 - z; i++) {
                input = input + "3";
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
    public void getVerticalSuccess(String code) {
        if(code!=null) {
           showShortToast(code);
        }
        tv_verification.setEnabled(true);
        time.start();
    }

    @Override
    public void getVerticalError(String errorMsg) {
        showShortToast(errorMsg);
        tv_verification.setEnabled(true);
    }

    @Override
    public void getLoginSuccess(Login_admin loginAdminl) {
        SpUtil.saveObject(this, loginAdminl);
        Log.e("EEE", "getLoginSuccess: "+loginAdminl.getToken());
        if (loginAdminl != null) {
            startActivity(new Intent(this, MainActivity.class));
        }
        Intent recever = new Intent("com.name.android");
        sendBroadcast(recever);
        Intent intent_recever = new Intent("com.zhh.android");
        intent_recever.putExtra("type", 1);
        sendBroadcast(intent_recever);
        //Login_admin login_admin= (Login_admin) SpUtil.readObject(this);
        //Log.e("AAA", "getLoginSuccess: "+login_admin);
    }

    @Override
    public void getLoginError(String errorMsg) {
        showShortToast(errorMsg);
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv_verification.setBackground(getResources().getDrawable(R.drawable.bachground_ash));
            //btnGetcode.setBackgroundColor(Color.parseColor("#B6B6D8"));
            tv_verification.setClickable(false);
            tv_verification.setText("获取验证码 " + millisUntilFinished / 1000 + " s");
        }

        @Override
        public void onFinish() {
            tv_verification.setText("重新获取");
            tv_verification.setClickable(true);
            tv_verification.setBackground(getResources().getDrawable(R.drawable.bachground_yellow_light));
            //btnGetcode.setBackgroundColor(Color.parseColor("#4EB84A"));

        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            //code........
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//        }
//        return false;
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
