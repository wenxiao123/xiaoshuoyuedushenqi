package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.app.App;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.PersonBean;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.MoreIntoEvent;
import com.example.administrator.xiaoshuoyuedushenqi.service.CacheService;
import com.example.administrator.xiaoshuoyuedushenqi.util.EventBusUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.fragment.main.BookshelfFragment;
import com.example.administrator.xiaoshuoyuedushenqi.view.fragment.main.BookstoreFragment;
import com.example.administrator.xiaoshuoyuedushenqi.view.fragment.main.DiscoveryFragment;
import com.example.administrator.xiaoshuoyuedushenqi.view.fragment.main.MoreFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "fzh";

    private static final int DUR_BOTTOM_BAR_ICON_ANIM = 400;
    private static final int REQUEST_CODE_SD = 1;

    private static final int FG_BOOKSHELF = 0;
    private static final int FG_DISCOVERY = 1;
    private static final int FG_MORE = 2;
    public static final int FG_BOOKMARK = 3;

    private static final String KEY_BOOKSHELF_FG = "bookshelf_fg";
    private static final String KEY_DISCOVERY_FG = "discovery_fg";
    private static final String KEY_BOOKMARK_FG = "bookmark_fg";
    private static final String KEY_MORE_FG = "more_fg";

    private View mBookshelfBg;
    private View mDiscoveryBg;
    private View mMoreBg;
    private View mBookmarkBg;
    private ImageView mBookshelfBeforeIv;
    private ImageView mDiscoveryBeforeIv;
    private ImageView mMoreBeforeIv,mBookMarkBefore;
    private ImageView mBookshelfAfterIv;
    private ImageView mDiscoveryAfterIv,mBookMarkAfter;
    private ImageView mMoreAfterIv;

    private Animator mBookshelfAnim;
    private Animator mDiscoveryAnim,mBookmarkAnim;
    private Animator mMoreAnim;
    private TimeInterpolator mTimeInterpolator = new AccelerateDecelerateInterpolator();

    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private Fragment mBookshelfFragment;//书架
    private Fragment mDiscoveryFragment;//发现
    private Fragment mBookstorFragment;//发现
    private Fragment mBookmarkFragment;//发现
    private Fragment mMoreFragment;//更多
    private Fragment mCurrFragment; // 当前正在显示的 Fragment
    private PersonBean personBean;
    @Override
    protected void doBeforeSetContentView() {
      // supportRequestWindowFeature(Window.FEATURE_NO_TITLE);   //隐藏标题栏
      //  StatusBarUtil.setStatusBarColor(this,getResources().getColor(R.color.colorAccent2));
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {

    }
    MyReceiver receiver;
    @Override
    protected void initView() {
        mBookshelfBg = findViewById(R.id.v_main_bottom_bar_bookshelf_bg);
        mBookshelfBg.setOnClickListener(this);
        mDiscoveryBg = findViewById(R.id.v_main_bottom_bar_discovery_bg);
        mDiscoveryBg.setOnClickListener(this);
        mMoreBg = findViewById(R.id.v_main_bottom_bar_more_bg);
        mMoreBg.setOnClickListener(this);
        mBookmarkBg= findViewById(R.id.v_main_bottom_bar_bookmark_bg);
        mBookmarkBg.setOnClickListener(this);

        mBookshelfBeforeIv = findViewById(R.id.iv_main_bottom_bar_bookshelf_before);
        mDiscoveryBeforeIv = findViewById(R.id.iv_main_bottom_bar_discovery_before);
        mMoreBeforeIv = findViewById(R.id.iv_main_bottom_bar_more_before);
        mBookMarkBefore = findViewById(R.id.iv_main_bottom_bar_bookmark_before);

        mBookshelfAfterIv= findViewById(R.id.iv_main_bottom_bar_bookshelf_after);
        mDiscoveryAfterIv = findViewById(R.id.iv_main_bottom_bar_discovery_after);
        mBookMarkAfter = findViewById(R.id.iv_main_bottom_bar_bookmark_after);
        mMoreAfterIv = findViewById(R.id.iv_main_bottom_bar_more_after);
        receiver = new MyReceiver();
        // 注册广播接受者
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.zhh.android");//要接收的广播
        registerReceiver(receiver, intentFilter);//注册接收者
        isNightthod=SpUtil.getIsNightMode();
    }
    String isload=null;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void doAfterInit() {

        if (getSavedInstanceState() != null) {
            // 取出保存的 Fragment，并用 mCurrFragment 记录当前显示的 Fragment
            mBookshelfFragment = mFragmentManager.getFragment(getSavedInstanceState(), KEY_BOOKSHELF_FG);
            if (mBookshelfFragment != null && !mBookshelfFragment.isHidden()) {
                mCurrFragment = mBookshelfFragment;
            }
            mDiscoveryFragment = mFragmentManager.getFragment(getSavedInstanceState(), KEY_DISCOVERY_FG);
            if (mDiscoveryFragment != null && !mDiscoveryFragment.isHidden()) {
                mCurrFragment = mDiscoveryFragment;
            }
            mBookmarkFragment= mFragmentManager.getFragment(getSavedInstanceState(), KEY_BOOKMARK_FG);
            if (mBookmarkFragment != null && !mBookmarkFragment.isHidden()) {
                mCurrFragment = mBookmarkFragment;
            }

            mMoreFragment = mFragmentManager.getFragment(getSavedInstanceState(), KEY_MORE_FG);
            if (mMoreFragment != null && !mMoreFragment.isHidden()) {
                mCurrFragment = mMoreFragment;
            }
        } else {
            // 第一次 onCreate 时默认加载该页面
            changeFragment(FG_BOOKSHELF);
        }

        // 检查权限
        checkPermission();
        Intent intent=getIntent();
        isload=intent.getStringExtra("islaod");//
        personBean= (PersonBean) intent.getSerializableExtra("personBean");
        if(isload!=null&&isload.equals("1")){
            if(personBean!=null){
               updata(personBean);
            }
        }else if(isload!=null&&isload.equals("2")){
            updata2(false);
        }
        String is_naghit=intent.getStringExtra("is_naghit");
        if(is_naghit!=null&&is_naghit.equals("2")){
            mBookshelfBeforeIv.setVisibility(View.VISIBLE);
            mBookshelfAfterIv.setVisibility(View.INVISIBLE);
            mDiscoveryBeforeIv.setVisibility(View.VISIBLE);
            mDiscoveryAfterIv.setVisibility(View.INVISIBLE);
            mBookMarkBefore.setVisibility(View.VISIBLE);
            mBookMarkAfter.setVisibility(View.INVISIBLE);
            mMoreAfterIv.setVisibility(View.VISIBLE);
            mMoreBeforeIv.setVisibility(View.INVISIBLE);
            changeFragment(FG_MORE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存已创建的 Fragment
        if (mBookshelfFragment != null) {
            mFragmentManager.putFragment(outState, KEY_BOOKSHELF_FG, mBookshelfFragment);
        }
        if (mDiscoveryFragment != null) {
            mFragmentManager.putFragment(outState, KEY_DISCOVERY_FG, mDiscoveryFragment);
        }
        if (mBookmarkFragment != null) {
            mFragmentManager.putFragment(outState, KEY_BOOKMARK_FG, mBookmarkFragment);
        }
        if (mMoreFragment != null) {
            mFragmentManager.putFragment(outState, KEY_MORE_FG, mMoreFragment);
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.v_main_bottom_bar_bookshelf_bg:
                // 如果已经点击了该菜单项，无视该操作
                if (mBookshelfAfterIv.getVisibility() == View.VISIBLE) {
                    break;
                }
                // 在开启当前菜单项的动画前，先切换其他菜单项的 icon
                mDiscoveryBeforeIv.setVisibility(View.VISIBLE);
                mDiscoveryAfterIv.setVisibility(View.INVISIBLE);
                mMoreBeforeIv.setVisibility(View.VISIBLE);
                mMoreAfterIv.setVisibility(View.INVISIBLE);
                mBookMarkBefore.setVisibility(View.VISIBLE);
                mBookMarkAfter.setVisibility(View.INVISIBLE);
                // 开启当前菜单项的动画
                initBookshelfShowAnim();
                mBookshelfAfterIv.setVisibility(View.VISIBLE);
                mBookshelfAnim.start();
                // 切换 Fragment
                changeFragment(FG_BOOKSHELF);
                // 改变状态栏颜色
                //StatusBarUtil.setLightColorStatusBar(this);
                break;
            case R.id.v_main_bottom_bar_discovery_bg:
                // 如果已经点击了该菜单项，无视该操作
                if (mDiscoveryAfterIv.getVisibility() == View.VISIBLE) {
                    break;
                }
                // 在开启当前菜单项的动画前，先切换其他菜单项的 icon
                mBookshelfBeforeIv.setVisibility(View.VISIBLE);
                mBookshelfAfterIv.setVisibility(View.INVISIBLE);
                mMoreBeforeIv.setVisibility(View.VISIBLE);
                mMoreAfterIv.setVisibility(View.INVISIBLE);
                mBookMarkBefore.setVisibility(View.VISIBLE);
                mBookMarkAfter.setVisibility(View.INVISIBLE);
                // 开启当前菜单项的动画
                initDiscoveryShowAnim();
                mDiscoveryAfterIv.setVisibility(View.VISIBLE);
                mDiscoveryAnim.start();
                // 切换 Fragment
                changeFragment(FG_DISCOVERY);
                // 改变状态栏颜色
               // StatusBarUtil.setLightColorStatusBar(this);
                break;
            case R.id.v_main_bottom_bar_bookmark_bg:
                // 如果已经点击了该菜单项，无视该操作
                if (mBookMarkAfter.getVisibility() == View.VISIBLE) {
                    break;
                }
                // 在开启当前菜单项的动画前，先切换其他菜单项的 icon
                mBookshelfBeforeIv.setVisibility(View.VISIBLE);
                mBookshelfAfterIv.setVisibility(View.INVISIBLE);
                mDiscoveryBeforeIv.setVisibility(View.VISIBLE);
                mDiscoveryAfterIv.setVisibility(View.INVISIBLE);
                mMoreBeforeIv.setVisibility(View.VISIBLE);
                mMoreAfterIv.setVisibility(View.INVISIBLE);
                // 开启当前菜单项的动画
                initBookmarkShowAnim();
                mBookMarkAfter.setVisibility(View.VISIBLE);
                mBookmarkAnim.start();
                // 切换 Fragment
                changeFragment(FG_BOOKMARK);
                //ToastUtil.showToast(this,"暂未开放");
                // 改变状态栏颜色
                //StatusBarUtil.setLightColorStatusBar(this);
                break;
            case R.id.v_main_bottom_bar_more_bg:
                // 如果已经点击了该菜单项，无视该操作
                if (mMoreAfterIv.getVisibility() == View.VISIBLE) {
                    break;
                }
                // 在开启当前菜单项的动画前，先切换其他菜单项的 icon
                mBookshelfBeforeIv.setVisibility(View.VISIBLE);
                mBookshelfAfterIv.setVisibility(View.INVISIBLE);
                mDiscoveryBeforeIv.setVisibility(View.VISIBLE);
                mDiscoveryAfterIv.setVisibility(View.INVISIBLE);
                mBookMarkBefore.setVisibility(View.VISIBLE);
                mBookMarkAfter.setVisibility(View.INVISIBLE);
                // 开启当前菜单项的动画
                initMoreShowAnim();
                mMoreAfterIv.setVisibility(View.VISIBLE);
                mMoreAnim.start();
                // 切换 Fragment
                changeFragment(FG_MORE);
                // 改变状态栏颜色
                //StatusBarUtil.setLightColorStatusBar(this);
                break;
            default:
                break;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void checked(){
        // 如果已经点击了该菜单项，无视该操作
        if (mDiscoveryAfterIv.getVisibility() == View.VISIBLE) {
            return;
        }
        // 在开启当前菜单项的动画前，先切换其他菜单项的 icon
        mBookshelfBeforeIv.setVisibility(View.VISIBLE);
        mBookshelfAfterIv.setVisibility(View.INVISIBLE);
        mMoreBeforeIv.setVisibility(View.VISIBLE);
        mMoreAfterIv.setVisibility(View.INVISIBLE);
        mBookMarkBefore.setVisibility(View.VISIBLE);
        mBookMarkAfter.setVisibility(View.INVISIBLE);
        // 开启当前菜单项的动画
        initDiscoveryShowAnim();
        mDiscoveryAfterIv.setVisibility(View.VISIBLE);
        mDiscoveryAnim.start();
        // 切换 Fragment
        changeFragment(FG_DISCOVERY);
    }
    /**
     * 初始化“书架”图标的显示动画
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initBookshelfShowAnim() {
        int cx = mBookshelfAfterIv.getMeasuredWidth() / 2;   // 揭露动画中心点的 x 坐标
        int cy = mBookshelfAfterIv.getMeasuredHeight() / 2;  // 揭露动画中心点的 y 坐标
        float startRadius = 0f;     // 开始半径
        float endRadius = (float) Math.max(mBookshelfAfterIv.getWidth()
                , mBookshelfAfterIv.getHeight()) / 2;  // 结束半径
        mBookshelfAnim = ViewAnimationUtils
                .createCircularReveal(mBookshelfAfterIv, cx, cy, startRadius, endRadius); // 创建揭露动画
        mBookshelfAnim.setDuration(DUR_BOTTOM_BAR_ICON_ANIM);
        mBookshelfAnim.setInterpolator(mTimeInterpolator);
        mBookshelfAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束时，隐藏 before icon
                if (mBookshelfAfterIv.getVisibility() == View.VISIBLE) {
                    mBookshelfBeforeIv.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * 初始化“发现”图标的显示动画
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initDiscoveryShowAnim() {
        int cx = mDiscoveryAfterIv.getMeasuredWidth() / 2;   // 揭露动画中心点的 x 坐标
        int cy = mDiscoveryAfterIv.getMeasuredHeight() / 2;  // 揭露动画中心点的 y 坐标
        float startRadius = 0f;     // 开始半径
        float endRadius = (float) Math.max(mDiscoveryAfterIv.getWidth()
                , mDiscoveryAfterIv.getHeight()) / 2;  // 结束半径
        mDiscoveryAnim = ViewAnimationUtils
                .createCircularReveal(mDiscoveryAfterIv, cx, cy, startRadius, endRadius); // 创建揭露动画
        mDiscoveryAnim.setDuration(DUR_BOTTOM_BAR_ICON_ANIM);
        mDiscoveryAnim.setInterpolator(mTimeInterpolator);
        mDiscoveryAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束时，隐藏 before icon
                if (mDiscoveryAfterIv.getVisibility() == View.VISIBLE) {
                    mDiscoveryBeforeIv.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * 初始化“发现”图标的显示动画
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initBookmarkShowAnim() {
        int cx = mBookMarkAfter.getMeasuredWidth() / 2;   // 揭露动画中心点的 x 坐标
        int cy = mBookMarkAfter.getMeasuredHeight() / 2;  // 揭露动画中心点的 y 坐标
        float startRadius = 0f;     // 开始半径
        float endRadius = (float) Math.max(mBookMarkAfter.getWidth()
                , mBookMarkAfter.getHeight()) / 2;  // 结束半径
        mBookmarkAnim = ViewAnimationUtils
                .createCircularReveal(mBookMarkAfter, cx, cy, startRadius, endRadius); // 创建揭露动画
        mBookmarkAnim.setDuration(DUR_BOTTOM_BAR_ICON_ANIM);
        mBookmarkAnim.setInterpolator(mTimeInterpolator);
        mBookmarkAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束时，隐藏 before icon
                if (mBookMarkAfter.getVisibility() == View.VISIBLE) {
                    mBookMarkBefore.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * 初始化“更多”图标的显示动画
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initMoreShowAnim() {
        int cx = mMoreAfterIv.getMeasuredWidth() / 2;   // 揭露动画中心点的 x 坐标
        int cy = mMoreAfterIv.getMeasuredHeight() / 2;  // 揭露动画中心点的 y 坐标
        float startRadius = 0f;     // 开始半径
        float endRadius = (float) Math.max(mMoreAfterIv.getWidth()
                , mMoreAfterIv.getHeight()) / 2;  // 结束半径
        mMoreAnim = ViewAnimationUtils
                .createCircularReveal(mMoreAfterIv, cx, cy, startRadius, endRadius); // 创建揭露动画
        mMoreAnim.setDuration(DUR_BOTTOM_BAR_ICON_ANIM);
        mMoreAnim.setInterpolator(mTimeInterpolator);
        mMoreAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束时，隐藏 before icon
                if (mMoreAfterIv.getVisibility() == View.VISIBLE) {
                    mMoreBeforeIv.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
   public void updata(PersonBean personBean){
       if(mBookshelfFragment!=null){
           ((BookshelfFragment)mBookshelfFragment).setPersonBean(personBean);
       }
   }

    public void updata2(boolean flag){
        if(mBookshelfFragment!=null){
            ((BookshelfFragment)mBookshelfFragment).updata2(flag);
        }
    }
    /**
     * 切换 Fragment
     *
     * @param i 切换后新的 Fragment
     *
     * 可选值：
     * @see #FG_BOOKSHELF 书架页面（BookshelfFragment）
     * @see #FG_DISCOVERY 发现页面（DiscoveryFragment）
     * @see #FG_MORE 更多页面（MoreFragment）
     */
    public void changeFragment(int i) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment showFragment = null;
        switch (i) {
            case FG_BOOKSHELF:
                if (mBookshelfFragment == null) {
                    mBookshelfFragment = new BookshelfFragment();
                    ft.add(R.id.fv_main_fragment_container, mBookshelfFragment);
                }
                showFragment = mBookshelfFragment;
                break;
            case FG_DISCOVERY:
                if (mDiscoveryFragment == null) {
                    mDiscoveryFragment = new DiscoveryFragment();
                    ft.add(R.id.fv_main_fragment_container, mDiscoveryFragment);
                }
                showFragment = mDiscoveryFragment;
                break;
            case FG_BOOKMARK:
                if (mBookmarkFragment == null) {
                    mBookmarkFragment = new BookstoreFragment();
                    ft.add(R.id.fv_main_fragment_container, mBookmarkFragment);
                }
                showFragment = mBookmarkFragment;
                break;
            case FG_MORE:
                if (mMoreFragment == null) {
                    mMoreFragment = new MoreFragment();
                    ft.add(R.id.fv_main_fragment_container, mMoreFragment);
                }
                showFragment = mMoreFragment;
                // 通知 More 页面更新相关信息
                Event<MoreIntoEvent> moreEvent = new Event<>(EventBusCode.MORE_INTO,
                        new MoreIntoEvent());
                EventBusUtil.sendEvent(moreEvent);
                break;
            default:
                break;
        }
        // 隐藏当前的 Fragment，显示新的 Fragment
        if (mCurrFragment != null) {
            ft.hide(mCurrFragment);
        }
        if (showFragment != null) {
            ft.show(showFragment);
        }
        mCurrFragment = showFragment;

        ft.commit();
    }
    /**
     * 检查权限
     */
    private void checkPermission() {
        //如果没有WRITE_EXTERNAL_STORAGE权限，则需要动态申请权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_SD);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_SD:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    break;
                }
                // 用户不同意
                finish();
                break;
            default:
                break;
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            //code........
            exit();
        }
        return false;
    }
    //系统退出
    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };
    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            App.getInstance().exit();
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int i=intent.getIntExtra("type",0);
            if(i==0) {
                updata2(false);
            }else {
                updata2(true);
            }
        }
    }
    boolean isNightthod;

    @Override
    protected void onResume() {
        super.onResume();
//        App app= (App) getApplication();
//        if(app.isNight()==true) {
//            app.setNight(false);
//            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//                AppCompatDelegate.setDefaultNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
//                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
//            Intent intent = new Intent(this, this.getClass());
//            startActivity(intent);
//            overridePendingTransition(R.anim.activity_in,R.anim.activity_out);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册
        unregisterReceiver(receiver);
        finish();
    }
}
