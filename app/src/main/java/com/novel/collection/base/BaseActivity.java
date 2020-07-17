package com.novel.collection.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
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

import com.novel.collection.R;
import com.novel.collection.app.App;
import com.novel.collection.util.ActivityHook;
import com.novel.collection.util.EventBusUtil;
import com.novel.collection.util.NetUtil;
import com.novel.collection.util.SpUtil;
import com.novel.collection.util.ToastUtil;
import com.novel.collection.view.activity.LauncherActivity;
import com.novel.collection.view.activity.MainActivity;
import com.novel.collection.view.activity.NovelIntroActivity;
import com.novel.collection.view.activity.WYReadActivity;
import com.novel.collection.weyue.utils.ReadSettingManager;

/**
 * @author
 * Created on 2020/2/19
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity {

    protected T mPresenter;
    private Bundle mSavedInstanceState;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActivityHook.hookOrientation(this);//hook，绕过检查
        if (savedInstanceState != null) {
            savedInstanceState.clear();
            //isTaskRoot();
            LauncherActivity.startClearLastTask(this);
        }
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
        if(!(this instanceof WYReadActivity)) {
            isNight = ReadSettingManager.getInstance().isNightMode();
            if (isNight == true) {
               // backgroundAlpha(0.5f);
                changeToNight();
            } else {
                changeToDay();
                //backgroundAlpha(1f);
            }
            //Log.e("QQQ7", "onResume: "+2222);
        }
    }
    boolean isNight;
    MyBackgroungReceiver backgroungReceiver;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mNetReceiver!=null){
            unregisterReceiver(mNetReceiver);
        }
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
               // backgroundAlpha(0.4f);
                changeToNight();
            }else {
                changeToDay();
                //backgroundAlpha(1f);
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
    private WindowManager mWindowManager = null;
    private View mNightView = null;
    private WindowManager.LayoutParams mNightViewParam;
    private boolean mIsAddedView;
    /**
     * 设置夜间模式
     */
    private void changeToNight() {
        if (mIsAddedView == true)
            return;                                           //WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
        mNightViewParam = new WindowManager.LayoutParams(  //WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                PixelFormat.TRANSPARENT);
        mWindowManager = getWindowManager();
        mNightView = new View(this);
        mNightView.setBackgroundResource(R.color.color_night);
        mWindowManager.addView(mNightView, mNightViewParam);
        mIsAddedView = true;
    }

    /**
     * 设置白天模式
     */
    public void changeToDay(){
        if (mIsAddedView && mNightView!=null) {
            mWindowManager.removeViewImmediate(mNightView);
            mWindowManager = null;
            mNightView = null;
            mIsAddedView=false;
        }
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

    public void initNetworkReceiver() {
        mNetReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, filter);
    }
    public void NetConect(){};
    public void DisNetConect(){}
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
                            NetConect();
                            //Toast.makeText(context, "当前移动网络正常", Toast.LENGTH_SHORT).show();
                            break;
                        case ConnectivityManager.TYPE_WIFI:
                            NetConect();
                            //Toast.makeText(context, "当前WIFI网络正常", Toast.LENGTH_SHORT).show();
                            break;
                        case ConnectivityManager.TYPE_ETHERNET:
                            NetConect();
                            //Toast.makeText(context, "当前以太网网络正常", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    DisNetConect();
                    //说明当前没有网络
                   // Toast.makeText(context, "当前网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
