package com.example.administrator.xiaoshuoyuedushenqi.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.app.App;
import com.example.administrator.xiaoshuoyuedushenqi.util.EventBusUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.NetUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.ToastUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.MainActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.NovelIntroActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.WYReadActivity;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.ReadSettingManager;

/**
 * @author
 * Created on 2020/2/19
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity {

    protected T mPresenter;
    private Bundle mSavedInstanceState;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        App.updateNightMode(isNight);
        doBeforeSetContentView();
        setContentView(getLayoutId());
        backgroungReceiver = new MyBackgroungReceiver();
        // 注册广播接受者
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.changebackground.android");//要接收的广播
        registerReceiver(backgroungReceiver, intentFilter);//注册接收者
        mPresenter = getPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }

        if (isRegisterEventBus()) {
            EventBusUtil.register(this);
        }
        App.getInstance().addActivity(this);
        mSavedInstanceState = savedInstanceState;
        try {
            initData();
            initView();
            doAfterInit();
        }catch (Exception ex){
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(NetUtil.hasInternet(this)){

        }else {
           onCreate(mSavedInstanceState);
        }

        if(!(this instanceof WYReadActivity)) {
            isNight = ReadSettingManager.getInstance().isNightMode();
            if (isNight == true) {
                backgroundAlpha(0.4f);
            } else {
                backgroundAlpha(1f);
            }
        }
    }
    boolean isNight;
    MyBackgroungReceiver backgroungReceiver;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(backgroungReceiver);
        App.removeActivity(this);
        System.gc();
        if (mPresenter != null) {
            mPresenter.detachView();
        }

        if (isRegisterEventBus()) {
            EventBusUtil.unregister(this);
        }
    }
    public class MyBackgroungReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            isNight = ReadSettingManager.getInstance().isNightMode();
            if(isNight) {
                backgroundAlpha(0.4f);
            }else {
                backgroundAlpha(1f);
            }
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }
    /**
     * 在setContentView方法前的操作
     */
    protected abstract void doBeforeSetContentView();

    /**
     *  获取当前活动的布局
     *
     * @return 布局id
     */
    protected abstract int getLayoutId();

    /**
     * 获取当前活动的Presenter
     *
     * @return 相应的Presenter实例，没有则返回 null
     */
    protected abstract T getPresenter();


    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 初始化数据和视图后在 OnCreate 方法中的操作
     */
    protected abstract void doAfterInit();


    /**
     * 是否注册EventBus，注册后才可以订阅事件
     *
     * @return 若需要注册 EventBus，则返回 true；否则返回 false
     */
    protected abstract boolean isRegisterEventBus();

    /**
     * 弹出Toast
     *
     * @param content 弹出的内容
     */
    protected void showShortToast(String content) {
        ToastUtil.showToast(this, content);
    }

    /**
     * 跳转到另一活动
     *
     * @param activity 新活动.class
     */
    protected void jumpToNewActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    /**
     * 带Bundle的跳转活动
     *
     * @param activity 新活动.class
     * @param bundle
     */
    protected void jump2ActivityWithBundle(Class activity, Bundle bundle) {
        Intent intent = new Intent(this, activity);
        startActivity(intent, bundle);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }


    /**
     * 获取 onCreate 方法中的 Bundle 参数 savedInstanceState
     *
     * @returnnovelid
     */
    protected Bundle getSavedInstanceState() {
        return mSavedInstanceState;
    }

    private void initNetworkReceiver() {
        mNetReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, filter);
    }
    NetworkReceiver mNetReceiver;
    public class NetworkReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                //说明当前有网络
                if (networkInfo != null && networkInfo.isAvailable()) {
                    int type = networkInfo.getType();
                    switch (type) {
                        case ConnectivityManager.TYPE_MOBILE:
                            Toast.makeText(context, "当前移动网络正常", Toast.LENGTH_SHORT).show();
                            break;
                        case ConnectivityManager.TYPE_WIFI:
                            Toast.makeText(context, "当前WIFI网络正常", Toast.LENGTH_SHORT).show();
                            break;
                        case ConnectivityManager.TYPE_ETHERNET:
                            Toast.makeText(context, "当前以太网网络正常", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    //说明当前没有网络
                    Toast.makeText(context, "当前网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
