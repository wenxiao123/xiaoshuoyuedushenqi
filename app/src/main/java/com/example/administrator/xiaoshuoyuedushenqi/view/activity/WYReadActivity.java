package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CollBookBean;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.EpubCatalogInitEvent;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.HoldReadActivityEvent;
import com.example.administrator.xiaoshuoyuedushenqi.util.EventBusUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.ScreenUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.ToastUtil;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.model.BookChaptersBean;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.model.VMBookContentInfo;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.IBookChapters;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.page.NetPageLoader;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.page.PageLoader;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.page.PageView;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.page.TxtChapter;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.disposables.Disposable;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class WYReadActivity extends BaseActivity implements View.OnClickListener ,IBookChapters {
    RelativeLayout rv_read_top_bar;
    ConstraintLayout cv_read_bottom_bar;
    private LinearLayout mSettingBarCv;
    ImageView mCatalogIv, mDayAndNightModeIv, mSettingIv, mBrightnessIv, iv_read_day_and_night_mode;
    PageView txt_page;
    View v_title;
    private TextView mSys_light;
    private ImageView sys_select;
    private SeekBar mBrightnessProcessSb;
    public static final String EXTRA_COLL_BOOK = "extra_coll_book";
    public static final String EXTRA_IS_COLLECTED = "extra_is_collected";
    public static final String CHPTER_ID = "chpter_id";
    private PageLoader mPageLoader;
    public View mTheme0;
    public View mTheme1;
    public View mTheme2;
    public View mTheme3;
    public View mTheme4;
    TextView iv_read_decrease_font, tv_textsize, iv_read_increase_font;
    private ImageView iv_read_decrease_row_space, iv_read_increase_row_space;
    private TextView mTurnNormalTv;
    private TextView mTurnRealTv;
    String chpter_id;
    // 监听系统亮度的变化
    private ContentObserver mBrightnessObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (mIsSystemBrightness) {
                // 屏幕亮度更新为新的系统亮度
                ScreenUtil.setWindowBrightness(WYReadActivity.this,
                        (float) ScreenUtil.getSystemBrightness() / ScreenUtil.getBrightnessMax());
            }
        }
    };
   ImageView img,iv_read_brightness;
    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wyread;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        mCollBook = (CollBookBean) getIntent().getSerializableExtra(EXTRA_COLL_BOOK);
        chpter_id=getIntent().getStringExtra(CHPTER_ID);
        mVmContentInfo = new VMBookContentInfo(getApplicationContext(), this);
    }

    @Override
    protected void initView() {
        img=findViewById(R.id.iv_read_menu);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPupowindpw(img);
            }
        });
        iv_read_brightness=findViewById(R.id.iv_read_brightness);
        iv_read_brightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBar();
                showPupowindpwChangeWebSite(iv_read_brightness);
            }
        });
        txt_page = findViewById(R.id.txt_page);
        rv_read_top_bar = findViewById(R.id.rv_read_top_bar);
        cv_read_bottom_bar = findViewById(R.id.cv_read_bottom_bar);
        v_title = findViewById(R.id.v_title);
        mSettingBarCv = findViewById(R.id.cv_read_setting_bar);
        mCatalogIv = findViewById(R.id.iv_read_catalog);
        mCatalogIv.setOnClickListener(this);
        mBrightnessIv = findViewById(R.id.iv_read_brightness);
        mBrightnessIv.setOnClickListener(this);
        mDayAndNightModeIv = findViewById(R.id.iv_read_day_and_night_mode);
        mDayAndNightModeIv.setOnClickListener(this);
        mSettingIv = findViewById(R.id.iv_read_setting);
        mSettingIv.setOnClickListener(this);
        mSys_light = findViewById(R.id.sys_ligin);
        iv_read_day_and_night_mode = findViewById(R.id.iv_read_day_and_night_mode);
        iv_read_day_and_night_mode.setOnClickListener(this);
        mSys_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChecked) {
                    // 变为系统亮度
                    mIsSystemBrightness = true;
                    mBrightness = -1f;
                    // 将屏幕亮度设置为系统亮度
                    ScreenUtil.setWindowBrightness(WYReadActivity.this,
                            (float) ScreenUtil.getSystemBrightness() / ScreenUtil.getBrightnessMax());
                    //mSys_light.setBackground(getResources().getDrawable(R.drawable.bachground_red));
                    sys_select.setImageResource(R.mipmap.sys_selected);
                    isChecked = false;
                } else {
                    // 变为自定义亮度
                    mIsSystemBrightness = false;
                    // 将屏幕亮度设置为自定义亮度
                    mBrightness = (float) mBrightnessProcessSb.getProgress() / 100;
                    ScreenUtil.setWindowBrightness(WYReadActivity.this, mBrightness);
                    //mSys_light.setBackground(getResources().getDrawable(R.drawable.bachground_cricyle));
                    sys_select.setImageResource(R.mipmap.sys_select);
                    isChecked = true;
                }
            }
        });
        sys_select = findViewById(R.id.sys_select);
        sys_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChecked) {
                    // 变为系统亮度
                    mIsSystemBrightness = true;
                    mBrightness = -1f;
                    // 将屏幕亮度设置为系统亮度
                    ScreenUtil.setWindowBrightness(WYReadActivity.this,
                            (float) ScreenUtil.getSystemBrightness() / ScreenUtil.getBrightnessMax());
                    //mSys_light.setBackground(getResources().getDrawable(R.drawable.bachground_red));
                    sys_select.setImageResource(R.mipmap.sys_selected);
                    isChecked = false;
                } else {
                    // 变为自定义亮度
                    mIsSystemBrightness = false;
                    // 将屏幕亮度设置为自定义亮度
                    mBrightness = (float) mBrightnessProcessSb.getProgress() / 100;
                    ScreenUtil.setWindowBrightness(WYReadActivity.this, mBrightness);
                    //mSys_light.setBackground(getResources().getDrawable(R.drawable.bachground_cricyle));
                    sys_select.setImageResource(R.mipmap.sys_select);
                    isChecked = true;
                }
            }
        });
        mBrightnessProcessSb = findViewById(R.id.sb_read_brightness_bar_brightness_progress);
        mBrightnessProcessSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!mIsSystemBrightness) {
                    // 调整亮度
                    mBrightness = (float) progress / 100;
                    ScreenUtil.setWindowBrightness(WYReadActivity.this, mBrightness);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mTheme0 = findViewById(R.id.v_read_theme_0);
        mTheme0.setOnClickListener(this);
        mTheme1 = findViewById(R.id.v_read_theme_1);
        mTheme1.setOnClickListener(this);
        mTheme2 = findViewById(R.id.v_read_theme_2);
        mTheme2.setOnClickListener(this);
        mTheme3 = findViewById(R.id.v_read_theme_3);
        mTheme3.setOnClickListener(this);
        mTheme4 = findViewById(R.id.v_read_theme_4);
        mTheme4.setOnClickListener(this);
        mTurnNormalTv = findViewById(R.id.tv_read_turn_normal);
        mTurnNormalTv.setOnClickListener(this);
        mTurnRealTv = findViewById(R.id.tv_read_turn_real);
        mTurnRealTv.setOnClickListener(this);
        switch (mTurnType) {
            case 0:
                mTurnNormalTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_white_selected));
                mTurnNormalTv.setTextColor(getResources().getColor(R.color.red_aa));
                mTurnRealTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                mTurnRealTv.setTextColor(getResources().getColor(R.color.black));
                //mPageView.setTurnType(com.example.administrator.xiaoshuoyuedushenqi.widget.PageView.TURN_TYPE.NORMAL);
                break;
            case 1:
                mTurnNormalTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                mTurnNormalTv.setTextColor(getResources().getColor(R.color.black));
                mTurnRealTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_white_selected));
                mTurnRealTv.setTextColor(getResources().getColor(R.color.red_aa));
                //mPageView.setTurnType(com.example.administrator.xiaoshuoyuedushenqi.widget.PageView.TURN_TYPE.REAL);
                break;
        }
        switch (mTheme) {
            case 0:
                mTheme0.setSelected(true);
                mTheme0.setScaleY(1.2F);
                mTheme0.setScaleX(1.2F);
                break;
        }
        iv_read_decrease_font = findViewById(R.id.iv_read_decrease_font);
        iv_read_decrease_font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int t = Integer.parseInt(tv_textsize.getText().toString());
                t--;
                tv_textsize.setText(t + "");
                mPageLoader.setTextSize(t);
            }
        });
        tv_textsize = findViewById(R.id.tv_textsize);
        iv_read_increase_font = findViewById(R.id.iv_read_increase_font);
        iv_read_increase_font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int t = Integer.parseInt(tv_textsize.getText().toString());
                t++;
                tv_textsize.setText(t + "");
                mPageLoader.setTextSize(t);
            }
        });
        iv_read_decrease_row_space = findViewById(R.id.iv_read_decrease_row_space);
        iv_read_decrease_row_space.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int t=mPageLoader.getmTextInterval();
                t--;
                mPageLoader.setmTextInterval(t);
            }
        });
        iv_read_increase_row_space = findViewById(R.id.iv_read_increase_row_space);
        iv_read_increase_row_space.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int t=mPageLoader.getmTextInterval();
                t++;
                mPageLoader.setmTextInterval(t);
            }
        });
        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
                true,
                mBrightnessObserver);
//        txt_page.post(
//                () -> hideSystemBar()
//        );
    }

    private final int[] StyleTextColors = new int[]{
            Color.parseColor("#4a453a"),
            Color.parseColor("#505550"),
            Color.parseColor("#453e33"),
            Color.parseColor("#8f8e88"),
            Color.parseColor("#27576c")
    };
    int mTheme = 0;
    int mTurnType = 0;

    private void updateWithTheme() {
        mTheme0.setSelected(false);
        mTheme0.setScaleY(1F);
        mTheme0.setScaleX(1F);
        mTheme1.setSelected(false);
        mTheme1.setScaleY(1F);
        mTheme1.setScaleX(1F);
        mTheme2.setSelected(false);
        mTheme2.setScaleY(1F);
        mTheme2.setScaleX(1F);
        mTheme3.setSelected(false);
        mTheme3.setScaleY(1F);
        mTheme3.setScaleX(1F);
        mTheme4.setSelected(false);
        mTheme4.setScaleY(1F);
        mTheme4.setScaleX(1F);
        switch (mTheme) {
            case 0:
                mTheme0.setSelected(true);
                mTheme0.setScaleY(1.2F);
                mTheme0.setScaleX(1.2F);
                break;
            case 1:
                mTheme1.setSelected(true);
                mTheme1.setScaleY(1.2F);
                mTheme1.setScaleX(1.2F);
                break;
            case 2:
                mTheme2.setSelected(true);
                mTheme2.setScaleY(1.2F);
                mTheme2.setScaleX(1.2F);
                break;
            case 3:
                mTheme3.setSelected(true);
                mTheme3.setScaleY(1.2F);
                mTheme3.setScaleX(1.2F);
                break;
            case 4:
                mTheme4.setSelected(true);
                mTheme4.setScaleY(1.2F);
                mTheme4.setScaleX(1.2F);
                break;
        }
        // 设置相关颜色
        //mNovelTitleTv.setTextColor(textColor);
        mPageLoader.setBgColor(mTheme, v_title);
    }

    /**
     * 显示设置栏
     */
    private void showSettingBar() {
//        mStyle = SpUtil.getTextStyle();
//        Typeface tf = null;
//        AssetManager mgr = getAssets();
//        if(mStyle.equals("1")) {
//            tf = Typeface.createFromAsset(mgr, "font/方正卡通简体.ttf");
//            tv_textstyle.setTypeface(tf);
//            tv_textstyle.setText("方正卡通简体");
//        }else if(mStyle.equals("2")){
//            tf = Typeface.createFromAsset(mgr, "font/方正楷体.ttf");
//            tv_textstyle.setTypeface(tf);
//            tv_textstyle.setText("方正楷体");
//        }else if(mStyle.equals("3")){
//            tf = Typeface.createFromAsset(mgr, "font/流行体简体.ttf");
//            tv_textstyle.setTypeface(tf);
//            tv_textstyle.setText("流行体简体");
//        }else {
//            tf=Typeface.create("sans-serif-medium",Typeface.NORMAL);
//            tv_textstyle.setTypeface(tf);
//            tv_textstyle.setText("系统字体");
//        }
        mIsShowSettingBar = true;
        Animation bottomAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_enter);
        mSettingBarCv.startAnimation(bottomAnim);
        mSettingBarCv.setVisibility(View.VISIBLE);
    }
    PopupWindow popupWindow;

    @SuppressLint("WrongConstant")
    private void showPupowindpwChangeWebSite(View parent) {
//        if (mType == 1) {
//            showShortToast("本地缓存小说不支持换源功能");
//            return;
//        }
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popu_changewebsite, null);
//        is_all_one = false;
//        tv_nodata = view.findViewById(R.id.tv_nodata);
//        s_line = view.findViewById(R.id.s_line);
//        m_line = view.findViewById(R.id.m_line);
//        tvCatalog = view.findViewById(R.id.tv_mulu);
//        tvCatalog.setOnClickListener(this);
//        progressBar = view.findViewById(R.id.pb_over);
//        mBookMark = view.findViewById(R.id.tv_book_mark);
//        mBookMark.setOnClickListener(this);
//        s_line.setVisibility(View.GONE);
//        m_line.setVisibility(View.VISIBLE);
//        mBookMark.setTextColor(getResources().getColor(R.color.catalog_chapter_order_text));
//        tvCatalog.setTextColor(getResources().getColor(R.color.red));
//        if (changeCategoryAdapter != null) {
//            changeCategoryAdapter.setPosition(-1);
//            changeCategoryAdapter.notifyDataSetChanged();
//        }
//        rv_catalog_list = view.findViewById(R.id.rv_catalog_list1);
//        rv_catalog_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        // ts_recyle = view.findViewById(R.id.ts_recyle);
//        progressBar.setVisibility(View.VISIBLE);
//        getCategorys(mNovelUrl);
        // ts_recyle.setLayoutManager(new LinearLayoutManager(this));
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.dp_250));
        //post_textStyle();
        popupWindow.setFocusable(false);
        popupWindow.setAnimationStyle(R.style.dialog_animation);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        backgroundAlpha(0.5f);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.BOTTOM, 0, -location[1]);
        //popupWindow.showAsDropDown(parent, (int) (parent.getWidth() * 0.7), 35);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
    }
    /**
     * 隐藏设置栏
     */
    private void hideSettingBar() {
        Animation bottomExitAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_exit);
        bottomExitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSettingBarCv.setVisibility(View.GONE);
                mIsShowSettingBar = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSettingBarCv.startAnimation(bottomExitAnim);
    }

    /**
     * 显示上下栏
     */
    private void showBar() {
        Animation topAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_top_enter);
        topAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // StatusBarUtil.setDarkColorStatusBar(ReadActivity.this);
                StatusBarUtil.setLightColorStatusBar(WYReadActivity.this);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 结束时重置标记
                mIsShowingOrHidingBar = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation bottomAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_enter);
        rv_read_top_bar.startAnimation(topAnim);
        cv_read_bottom_bar.startAnimation(bottomAnim);
        rv_read_top_bar.setVisibility(View.VISIBLE);
        cv_read_bottom_bar.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏上下栏
     */
    private void hideBar() {
        Animation topExitAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_top_exit);
        topExitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rv_read_top_bar.setVisibility(View.GONE);
                mIsShowingOrHidingBar = false;
                StatusBarUtil.setLightColorStatusBar(WYReadActivity.this);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation bottomExitAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_exit);
        bottomExitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cv_read_bottom_bar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rv_read_top_bar.startAnimation(topExitAnim);
        cv_read_bottom_bar.startAnimation(bottomExitAnim);
    }

    private CollBookBean mCollBook;
    private boolean mIsShowingOrHidingBar, mIsShowSettingBar;
    private boolean mIsSystemBrightness = true;     // 是否为系统亮度
    boolean isChecked = false;
    private boolean isNightMode = false;
    private float mBrightness;  // 屏幕亮度，为 -1 时表示系统亮度
    private VMBookContentInfo mVmContentInfo;
    @Override
    protected void doAfterInit() {
        mPageLoader = txt_page.getPageLoader(mCollBook.isLocal());
        v_title.setBackgroundColor(mPageLoader.getmPageBg());
        tv_textsize.setText(mPageLoader.getmTextSize() + "");
        mPageLoader.setChpter_id(Integer.parseInt(chpter_id));
        mPageLoader.setOnPageChangeListener(new PageLoader.OnPageChangeListener() {
            @Override
            public void onChapterChange(int pos) {
                //setCategorySelect(pos);


            }

            @Override
            public void onLoadChapter(List<TxtChapter> chapters, int pos) {
                mVmContentInfo.setNoval_id(mCollBook.get_id());
                mVmContentInfo.loadContent(mPageLoader.getChpter_id()+"", chapters);
//                setCategorySelect(mPageLoader.getChapterPos());
                if (mPageLoader.getPageStatus() == NetPageLoader.STATUS_LOADING
                        || mPageLoader.getPageStatus() == NetPageLoader.STATUS_ERROR) {
                    //冻结使用
                    //mReadSbChapterProgress.setEnabled(false);
                }

                //隐藏提示
//                mReadTvPageTip.setVisibility(GONE);
//                mReadSbChapterProgress.setProgress(0);
            }

            @Override
            public void onCategoryFinish(List<TxtChapter> chapters) {
//                mTxtChapters.clear();
//                mTxtChapters.addAll(chapters);
//                mReadCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageCountChange(int count) {
//                mReadSbChapterProgress.setEnabled(true);
//                mReadSbChapterProgress.setMax(count - 1);
//                mReadSbChapterProgress.setProgress(0);
            }

            @Override
            public void onPageChange(int pos) {
//                mReadSbChapterProgress.post(() -> {
//                    mReadSbChapterProgress.setProgress(pos);
//                });
            }
        });
        txt_page.setTouchListener(new PageView.TouchListener() {
            @Override
            public void center() {
                //ToastUtil.showToast(WYReadActivity.this,"222");
                //toggleMenu(true);
                if (mIsShowingOrHidingBar == false) {
                    showBar();
                } else {
                    hideBar();
                }
                if (mIsShowSettingBar) {
                    hideSettingBar();
                    return;
                }
            }

            @Override
            public boolean onTouch() {
                hideBar();
                return true;
            }

            @Override
            public boolean prePage() {
                return true;
            }

            @Override
            public boolean nextPage() {
                return true;
            }

            @Override
            public void cancel() {
            }
        });
        mPageLoader.openBook(mCollBook);//chpter_id
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_read_back:
                finish();
                break;
            case R.id.iv_read_menu:
                // showPupowindpw(mMenuIv);
                break;
            case R.id.tv_read_previous_chapter:
//                // 加载上一章节
//                if (mType == 0) {
//                    preNet();
//                } else if (mType == 2) {
//                    preEpub();
//                } else if (mType == 1) {
//                    int o = 0;
//                    for (int j = 0; j < longs.size(); j++) {
//                        if (mPageView.getPosition() < (int) longs.get(0)) {
//                            o = 0;
//                            break;
//                        } else if (mPageView.getPosition() < (int) longs.get(j)) {
//                            o = j - 1;
//                            break;
//                        }
//                    }
//                    if (o > 0) {
//                        mPageView.initDrawText(mNovelContent, longs.get(o - 1));
//                    } else {
//                        showShortToast("this is first");
//                    }
//                }
                break;
            case R.id.tv_book_mark:
//                is_all_one = true;
//                s_line.setVisibility(View.VISIBLE);
//                m_line.setVisibility(View.GONE);
//                mBookMark.setTextColor(getResources().getColor(R.color.red));
//                tvCatalog.setTextColor(getResources().getColor(R.color.catalog_chapter_order_text));
//                if (changeCategoryAdapter != null) {
//                    changeCategoryAdapter.setPosition(mChapterIndex - 1);
//                    changeCategoryAdapter.notifyDataSetChanged();
//                }
                break;
            case R.id.tv_mulu:
//                is_all_one = false;
//                s_line.setVisibility(View.GONE);
//                m_line.setVisibility(View.VISIBLE);
//                mBookMark.setTextColor(getResources().getColor(R.color.catalog_chapter_order_text));
//                tvCatalog.setTextColor(getResources().getColor(R.color.red));
//                if (changeCategoryAdapter != null) {
//                    changeCategoryAdapter.setPosition(-1);
//                    changeCategoryAdapter.notifyDataSetChanged();
//                }
                break;
            case R.id.tv_read_next_chapter:
//                // 加载下一章节
//                if (mType == 0) {
//                    nextNet();
//                } else if (mType == 2) {
//                    nextEpub();
//                } else if (mType == 1) {
//                    int o = 0;
//                    for (int j = 0; j < longs.size(); j++) {
//                        if (mPageView.getPosition() < (int) longs.get(0)) {
//                            o = 0;
//                            break;
//                        } else if (mPageView.getPosition() < (int) longs.get(j)) {
//                            o = j - 1;
//                            break;
//                        }
//                    }
//                    if (o < longs.size()) {
//                        mPageView.initDrawText(mNovelContent, longs.get(o + 1));
//                    } else {
//                        showShortToast("this is last");
//                    }
//                }
                break;
            case R.id.iv_read_catalog:
            case R.id.tv_read_catalog:
//                // 目录
//                if (mType == 0) {
//                    // 跳转到目录页面，并且将自己的引用传递给它
//                    Event<HoldReadActivityEvent> event = new Event<>(EventBusCode.CATALOG_HOLD_READ_ACTIVITY,
//                            new HoldReadActivityEvent(ReadActivity.this));
//                    EventBusUtil.sendStickyEvent(event);
//                    Intent intent = new Intent(ReadActivity.this, CatalogActivity.class);
//                    intent.putExtra(CatalogActivity.KEY_URL, mNovelUrl);    // 传递当前小说的 url
//                    intent.putExtra(CatalogActivity.KEY_NAME, mName);  // 传递当前小说的名字
//                    intent.putExtra(CatalogActivity.KEY_COVER, mCover); // 传递当前小说的封面
//                    intent.putExtra("weigh", weigh);
//                    intent.putExtra("chapter_id", mChapterIndex);
//                    startActivity(intent);
//                } else if (mType == 1) {
//                    // 跳转到目录页面，并且将自己的引用传递给它
//                    Event<HoldReadActivityEvent> event = new Event<>(EventBusCode.CATALOG_HOLD_READ_ACTIVITY,
//                            new HoldReadActivityEvent(ReadActivity.this));
//                    EventBusUtil.sendStickyEvent(event);
//                    Intent intent = new Intent(ReadActivity.this, LocalCatalogActivity.class);
//                    intent.putExtra("file_path", adress);    // 传递当前小说的 url
//                    intent.putExtra(LocalCatalogActivity.KEY_ID, mNovelUrl);    // 传递当前小说的 url
//                    intent.putExtra(LocalCatalogActivity.KEY_NAME, mName);  // 传递当前小说的名字
//                    intent.putExtra(LocalCatalogActivity.KEY_COVER, mCover); // 传递当前小说的封面
//                    intent.putExtra(LocalCatalogActivity.KEY_POSTION, mPageView.getPosition());
//                    startActivity(intent);
//                } else if (mType == 2) {
//                    // 跳转到 epub 目录界面
//                    Event<EpubCatalogInitEvent> event = new Event<>(EventBusCode.EPUB_CATALOG_INIT,
//                            new EpubCatalogInitEvent(ReadActivity.this, mEpubTocList,
//                                    mOpfData, mNovelUrl, mName, mCover));
//                    EventBusUtil.sendStickyEvent(event);
//                    jumpToNewActivity(EpubCatalogActivity.class);
//                }
                break;
            case R.id.tv_textstyle:
                // 隐藏上下栏，并显示亮度栏
//                hideBar();
//                hideSettingBar();
//                //showTextstyle();
//                showPupowindpwTextStyle(mNovelProgressTv);
                break;
            case R.id.iv_read_brightness:
            case R.id.tv_read_brightness:
//                // 隐藏上下栏，并显示亮度栏
//                hideBar();
//                // showBrightnessBar();
//                showPupowindpwChangeWebSite(mMenuIv);
                break;
            case R.id.iv_read_day_and_night_mode:
            case R.id.tv_read_day_and_night_mode:
                if (isNightMode) {
                    isNightMode = false;
                } else {
                    isNightMode = true;
                }
                mPageLoader.setNightMode(isNightMode);
                // toggleNightMode();
                hideBar();
                break;
            case R.id.iv_read_setting:
            case R.id.tv_read_setting:
                // 隐藏上下栏，并显示设置栏
                hideBar();
                showSettingBar();
                break;
            case R.id.iv_read_decrease_font:
//                if (mTextSize == mMinTextSize) {
//                    break;
//                }
//                mTextSize--;
//                tv_textsize.setText((int) mTextSize + "");
//                mPageView.setTextSize(mTextSize);
                break;
            case R.id.iv_read_increase_font:
//                if (mTextSize == mMaxTextSize) {
//                    break;
//                }
//                mTextSize++;
//                tv_textsize.setText((int) mTextSize + "");
//                mPageView.setTextSize(mTextSize);
                break;
            case R.id.iv_read_decrease_row_space:
//                if (mRowSpace == mMinRowSpace) {
//                    break;
//                }
//                mRowSpace--;
//                mPageView.setRowSpace(mRowSpace);
//                tv_jainju.setText((int) mRowSpace + "");
                break;
            case R.id.iv_read_increase_row_space:
//                if (mRowSpace == mMaxRowSpace) {
//                    break;
//                }
//                mRowSpace++;
//                mPageView.setRowSpace(mRowSpace);
//                tv_jainju.setText((int) mRowSpace + "");
                break;
            case R.id.v_read_theme_0:
                if (!isNightMode && mTheme == 0) {
                    break;
                }
                mTheme = 0;
                updateWithTheme();
                break;
            case R.id.v_read_theme_1:
                mTheme = 1;
                updateWithTheme();
                break;
            case R.id.v_read_theme_2:
                mTheme = 2;
                updateWithTheme();
                break;
            case R.id.v_read_theme_3:
                mTheme = 3;
                updateWithTheme();
                break;
            case R.id.v_read_theme_4:
                mTheme = 4;
                updateWithTheme();
                break;
            case R.id.tv_read_turn_normal:
                if (mTurnType != 0) {
                    mTurnType = 0;
                    mTurnNormalTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_white_selected));
                    mTurnNormalTv.setTextColor(getResources().getColor(R.color.red_aa));
                    mTurnRealTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                    mTurnRealTv.setTextColor(getResources().getColor(R.color.black));
                    mPageLoader.setPageMode(PageView.PAGE_MODE_SCROLL);
                }
                break;
            case R.id.tv_read_turn_real:
                if (mTurnType != 1) {
                    mTurnType = 1;
                    mTurnRealTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_white_selected));
                    mTurnRealTv.setTextColor(getResources().getColor(R.color.red_aa));
                    mTurnNormalTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                    mTurnNormalTv.setTextColor(getResources().getColor(R.color.black));
                    //mPageView.setTurnType(PageView.TURN_TYPE.REAL);
                    mPageLoader.setPageMode(PageView.PAGE_MODE_COVER);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mBrightnessObserver);
    }

    @Override
    public void bookChapters(BookChaptersBean bookChaptersBean) {

    }

    @Override
    public void finishChapters() {
        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
            txt_page.post(() -> {
               // mPageLoader.setChapterPos(Integer.parseInt(chpter_id));
                mPageLoader.openChapter();
            });
        }
    }
    private void showPupowindpw(View parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popu_item2, null);
        ListView lv_appointment = (ListView) view.findViewById(R.id.list_view);
        final String[] datas;
//        if (mType == 1) {
//            datas = new String[]{"已缓存", "添加书签"};
//        } else {
        datas = new String[]{"全本缓存", "添加书签"};
//        }
        final Integer[] ints = {R.mipmap.img_load, R.mipmap.icon_bookmark};
        PupoAdapter mainAdapter = null;
        if (datas != null) {
            mainAdapter = new PupoAdapter(datas, ints);
        }
        lv_appointment.setAdapter(mainAdapter);
        // 创建一个PopuWidow对象,设置宽高
        final PopupWindow popupWindow = new PopupWindow(view, (int) (parent.getWidth() * 2), ViewGroup.LayoutParams.WRAP_CONTENT);

        // 使其聚集,可点击
        popupWindow.setFocusable(true);
        backgroundAlpha(0.5f);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        popupWindow.showAsDropDown(parent, -(parent.getWidth() * 2), 15);
        lv_appointment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
//                        if (mIsShowingOrHidingBar) {
//                            return;
//                        }
//                        if (mIsShowBrightnessBar) {
//                            hideBrightnessBar();
//                            return;
//                        }
//                        if (mIsShowtextstyle) {
//                            hideTextstyle();
//                            return;
//                        }
//                        if (mIsShowSettingBar) {
//                            hideSettingBar();
//                            return;
//                        }
//                        mIsShowingOrHidingBar = true;
//                        if (mTopSettingBarRv.getVisibility() != View.VISIBLE) {
//                            // 显示上下栏
//                            showBar();
//                        } else {
//                            // 隐藏上下栏
//                            hideBar();
//                        }
//                        d = 1;
//                        if (datas[position].equals("已缓存")) {
//                            showShortToast("已经缓存");
//                            break;
//                        } else {
//                            tv_load.setVisibility(View.VISIBLE);
//                            if (is_othersite == true) {
//                                new Thread(new ReadActivity.LoadRunable(other_website.get(d - 1).getChapter_url())).start();
//                            } else {
//                                postBooks_che();
//                            }
//                            is_load = true;
//                        }
                        break;
                    case 1:
//                        if (mType == 1) {
//                            boolean isflag = false;
//                            List<BookmarkNovelDbData> bookmarkNovelDbData = mDbManager.queryAllBookmarkNovel(mNovelUrl);
//                            //Log.e("QQQ", "2222: "+bookmarkNovelDbData.size());
//                            for (int i = 0; i < bookmarkNovelDbData.size(); i++) {
//                                // Log.e("QQQ", "onItemClick: "+bookmarkNovelDbData.get(i).getPosition()+" "+mPageView.getPosition());
//                                if (bookmarkNovelDbData.get(i).getPosition() == mPageView.getPosition()) {
//                                    //Log.e("QQQ", "onItemClick: "+111);
//                                    isflag = true;
//                                    break;
//                                }
//                            }
//                            // String progress = mNovelProgressTv.getText().toString().substring(0, mNovelProgressTv.getText().length() - 1);
//                            if (isflag == false) {
//                                String progress = mNovelProgressTv.getText().toString().substring(0, mNovelProgressTv.getText().length() - 1);
//                                Date t = new Date();
//                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                BookmarkNovelDbData dbData = new BookmarkNovelDbData(mNovelUrl, mName,
//                                        mNovelContent.substring(mPageView.getPosition(), mPageView.getPosition() + 23), Float.parseFloat(progress) / 100, mPageView.getPosition(), mType, df.format(t), mChapterIndex + "");
//                                mDbManager.insertBookmarkNovel(dbData);
//                                showShortToast("书签已添加");
//                            } else {
//                                showShortToast("此书签已存在");
//                            }
//                        } else if (mType == 2) {
//                            BookshelfNovelDbData dbData = new BookshelfNovelDbData(mNovelUrl, mName,
//                                    mCover, mChapterIndex, mPageView.getFirstPos(), mType, mPageView.getSecondPos());
//                            mDbManager.insertOrUpdateBook(dbData);
//
//                        } else if (mType == 0) {
//                            boolean isflag = false;
//                            List<BookmarkNovelDbData> bookmarkNovelDbData = mDbManager.queryAllBookmarkNovel(mNovelUrl);
//                            //Log.e("QQQ", "2222: "+bookmarkNovelDbData.size());
//                            for (int i = 0; i < bookmarkNovelDbData.size(); i++) {
//                                // Log.e("QQQ", "onItemClick: "+bookmarkNovelDbData.get(i).getPosition()+" "+mPageView.getPosition());
//                                if (bookmarkNovelDbData.get(i).getPosition() == mPageView.getPosition()) {
//                                    //Log.e("QQQ", "onItemClick: "+111);
//                                    isflag = true;
//                                    break;
//                                }
//                            }
//                            // String progress = mNovelProgressTv.getText().toString().substring(0, mNovelProgressTv.getText().length() - 1);
//                            if (isflag == false) {
//                                Date t = new Date();
//                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                int seletposi = mPageView.getPosition() + 23 <= webContent.length() ? mPageView.getPosition() + 23 : webContent.length() - 1;
//                                BookmarkNovelDbData dbData = new BookmarkNovelDbData(mNovelUrl, webName,
//                                        webContent.substring(mPageView.getPosition(), seletposi), mChapterIndex, mPageView.getPosition(), mType, df.format(t), mChapterIndex + "");
//                                mDbManager.insertBookmarkNovel(dbData);
//                                showShortToast("书签已添加");
//                            } else {
//                                showShortToast("此书签已存在");
//                            }
//                        }
                        break;
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });

    }
    class PupoAdapter extends BaseAdapter {
        LayoutInflater inflater = LayoutInflater.from(WYReadActivity.this);
        String[] strings;
        Integer[] integers;

        public PupoAdapter(String[] strings, Integer[] integers) {
            this.strings = strings;
            this.integers = integers;
        }

        @Override
        public int getCount() {
            return strings.length;
        }

        @Override
        public Object getItem(int position) {
            return strings[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
            ImageView imageView = convertView.findViewById(R.id.img);
            TextView textView = convertView.findViewById(R.id.text);
            textView.setTextColor(getResources().getColor(R.color.black));
            imageView.setImageResource(integers[position]);
            textView.setText(strings[position]);
            return convertView;
        }
    }
    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }
    @Override
    public void errorChapters() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void stopLoading() {

    }
}
