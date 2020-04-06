package com.example.administrator.xiaoshuoyuedushenqi.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.administrator.xiaoshuoyuedushenqi.app.App;
import com.example.administrator.xiaoshuoyuedushenqi.util.EventBusUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.ToastUtil;

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

        mPresenter = getPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }

        if (isRegisterEventBus()) {
            EventBusUtil.register(this);
        }
        App.getInstance().addActivity(this);
        mSavedInstanceState = savedInstanceState;
        initData();
        initView();
        doAfterInit();
//        String is_naghit=intent.getStringExtra("is_naghit");
//        if(is_naghit!=null&&is_naghit.equals("2")){
//            mBookshelfBeforeIv.setVisibility(View.VISIBLE);
//            mBookshelfAfterIv.setVisibility(View.INVISIBLE);
//            mDiscoveryBeforeIv.setVisibility(View.VISIBLE);
//            mDiscoveryAfterIv.setVisibility(View.INVISIBLE);
//            mBookMarkBefore.setVisibility(View.VISIBLE);
//            mBookMarkAfter.setVisibility(View.INVISIBLE);
//            mMoreAfterIv.setVisibility(View.VISIBLE);
//            mMoreBeforeIv.setVisibility(View.INVISIBLE);
//            changeFragment(FG_MORE);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        App app= (App) getApplication();
        App.init(this);
        if(app.isNight()==true) {
            app.setNight(false);
            App.updateNightMode(!SpUtil.getIsNightMode());
            finish();
//            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//                AppCompatDelegate.setDefaultNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
//                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            Intent intent = new Intent(this, this.getClass());
            //intent.putExtra("is_naghit", "2");
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.removeActivity(this);
        System.gc();
        if (mPresenter != null) {
            mPresenter.detachView();
        }

        if (isRegisterEventBus()) {
            EventBusUtil.unregister(this);
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

    /**
     * 获取 onCreate 方法中的 Bundle 参数 savedInstanceState
     *
     * @return
     */
    protected Bundle getSavedInstanceState() {
        return mSavedInstanceState;
    }

}
