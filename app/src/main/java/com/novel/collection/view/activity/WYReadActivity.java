package com.novel.collection.view.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;


import com.novel.collection.R;
import com.novel.collection.adapter.ChangeCategoryAdapter;
import com.novel.collection.adapter.ChangeOtherCategoryAdapter;
import com.novel.collection.adapter.TextStyleAdapter;
import com.novel.collection.app.App;
import com.novel.collection.base.BaseActivity;
import com.novel.collection.base.BasePresenter;
import com.novel.collection.constant.Constant;
import com.novel.collection.constant.EventBusCode;
import com.novel.collection.db.DatabaseManager;
import com.novel.collection.entity.bean.Cataloginfo;
import com.novel.collection.entity.bean.Categorys_one;
import com.novel.collection.entity.bean.Login_admin;
import com.novel.collection.entity.bean.Noval_Readcored;
import com.novel.collection.entity.bean.Other_chpter;
import com.novel.collection.entity.bean.PersonBean;
import com.novel.collection.entity.bean.Text;
import com.novel.collection.entity.bean.TextStyle;
import com.novel.collection.entity.data.BookmarkNovelDbData;
import com.novel.collection.entity.data.BookshelfNovelDbData;
import com.novel.collection.entity.data.DetailedChapterData;
import com.novel.collection.entity.eventbus.EpubCatalogInitEvent;
import com.novel.collection.entity.eventbus.Event;
import com.novel.collection.entity.eventbus.HoldReadActivityEvent;
import com.novel.collection.entity.eventbus.HoldReadActivityEvent2;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.util.AdsUtils;
import com.novel.collection.util.EventBusUtil;
import com.novel.collection.util.ImageLoaderUtils;
import com.novel.collection.util.LogUtils;
import com.novel.collection.util.ScreenUtil;
import com.novel.collection.util.SpUtil;
import com.novel.collection.util.StatusBarUtil;
import com.novel.collection.weyue.db.entity.BookRecordBean;
import com.novel.collection.weyue.db.entity.CollBookBean;
import com.novel.collection.weyue.model.BookChaptersBean;
import com.novel.collection.weyue.model.VMBookContentInfo;
import com.novel.collection.weyue.utils.ReadSettingManager;
import com.novel.collection.weyue.utils.ScreenUtils;
import com.novel.collection.weyue.utils.ToastUtils;
import com.novel.collection.weyue.widget.IBookChapters;
import com.novel.collection.weyue.widget.page.NetPageLoader;
import com.novel.collection.weyue.widget.page.OtherNetPageLoader;
import com.novel.collection.weyue.widget.page.PageLoader;
import com.novel.collection.weyue.widget.page.PageView;
import com.novel.collection.weyue.widget.page.TxtChapter;
import com.novel.collection.widget.AdmDialog;
import com.novel.collection.widget.TipDialog;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.security.auth.login.LoginException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static android.view.View.LAYER_TYPE_SOFTWARE;
import static com.novel.collection.constant.Constant.text_adress2;
import static com.novel.collection.constant.Constant.text_name1;
import static com.novel.collection.weyue.widget.page.PageLoader.STATUS_FINISH;

public class WYReadActivity extends BaseActivity implements View.OnClickListener, IBookChapters {
    RelativeLayout rv_read_top_bar;
    ConstraintLayout cv_read_bottom_bar;
    private LinearLayout mSettingBarCv;
    FrameLayout mCatalog, mSettingIv, mBrightnessIv, iv_read_day_and_night_mode;
    ImageView iv_read_day_and_night;
    PageView txt_page;
    ImageView v_title;
    private TextView mSys_light;
    private ImageView sys_select;
    private SeekBar mBrightnessProcessSb;
    public static final String EXTRA_COLL_BOOK = "extra_coll_book";
    public static final String EXTRA_IS_COLLECTED = "extra_is_collected";
    public static final String CHPTER_ID = "chpter_id";
    public static final String STATUS = "status";
    public static final String PAGE_ID = "page_id";
    public static final String LOAD_PATH = "load_path";
    private PageLoader mPageLoader;
    /**
     * 阅读背景相关设置
     */
    public FrameLayout mTheme0;
    public FrameLayout mTheme1;
    public FrameLayout mTheme2;
    public FrameLayout mTheme3;
    public FrameLayout mTheme4;
    private ImageView Img_select1, Img_select2, Img_select3, Img_select4, Img_select5;


    private Login_admin login_admin;
    private String mStyle = "";         // 字体样式
    private ImageView tv_autoread;

    //阅读间隙设置
    private ImageView img_space_big, img_space_middle, img_space_samlle;
    private TextView tv_read_next_chapter, tv_read_previous_chapter;
    private SeekBar mReadSbChapterProgress, sb_auto_read_progress;
    ImageView iv_read_decrease_font, iv_read_increase_font;//字体大小增减
    TextView tv_textsize;
    private ImageView iv_read_decrease_row_space, iv_read_increase_row_space;
    private TextView mTurnNormalTv;
    private ImageView tv_textstyle;
    private TextView mTurnRealTv, tv_website;//网站名称
    String chpter_id, load_path, page_id, status;
    TextView tv_title;
    TextView ads_load, txt_ad, txt_adm_content;//底部广告控件
    ImageView img_bottom_ad;//底部广告图片
    RelativeLayout rel_ads;
    LinearLayout adm_lin;
    TextView txt_color;

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
    ImageView img;

    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wyread;
    }

    public void keepScreenLongLight(boolean isOpenLight) {
        if (isOpenLight) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        try {
            mCollBook = (CollBookBean) getIntent().getSerializableExtra(EXTRA_COLL_BOOK);
            chpter_id = getIntent().getStringExtra(CHPTER_ID);
            page_id = getIntent().getStringExtra(PAGE_ID);
            if (page_id == null) {
                page_id = 0 + "";
            }
            status = getIntent().getStringExtra(STATUS);
            if (status == null) {
                status = 0 + "";
            }
            load_path = getIntent().getStringExtra(LOAD_PATH);
            mVmContentInfo = new VMBookContentInfo(getApplicationContext(), this);
            mStyle = SpUtil.getTextStyle();
            mDbManager = DatabaseManager.getInstance();
            isNightMode = ReadSettingManager.getInstance().isNightMode();
            login_admin = (Login_admin) SpUtil.readObject(this);
            List<Cataloginfo> cataloginfos = mDbManager.queryAllCataloginfo(mCollBook.get_id());
            Collections.reverse(cataloginfos);
            mCollBook.setCataloginfos(cataloginfos);
            post_adm(1);
        } catch (Exception ex) {
            finish();
        }
    }

    int read_frist;
    TextView tv_read_day_and_night_mode, tv_right, txt_click;
    ImageView mBackIv;
    LinearLayout l_yingdaoye;
    AnimatorSet animatorSetsuofang;

    //控件大小闪烁效果
    public void ScleAnimtion(TextView tv) {
        if (animatorSetsuofang != null) {
            animatorSetsuofang.cancel();
        }
        animatorSetsuofang = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tv, "scaleX", 1, 1.1f, 1);//后几个参数是放大的倍数
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tv, "scaleY", 1, 1.1f, 1);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);//永久循环
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        animatorSetsuofang.setDuration(1500);//时间
        animatorSetsuofang.play(scaleX).with(scaleY);//两个动画同时开始
        animatorSetsuofang.start();//开始
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNetworkReceiver();
        if (adm_lin.getVisibility() == View.GONE) {
            activity_opening_videoview.pause();
            activity_opening_videoview.suspend();
        }
    }

    @Override
    public void NetConect() {
        super.NetConect();
        if (is_autoRead) {
            txt_page.setmAutoPlayAble(true);
            txt_page.startAutoPlay();
        }
    }

    @Override
    public void DisNetConect() {
        super.NetConect();
        if (is_autoRead) {
            txt_page.setmAutoPlayAble(false);
            txt_page.stopAutoPlay();
        }
    }

    @Override
    protected void initView() {
        App.init(this);
        tv_read_day_and_night_mode = findViewById(R.id.tv_read_day_and_night_mode);
        tv_jainju = findViewById(R.id.tv_jainju);
        tv_title = findViewById(R.id.tv_title);
        txt_click = findViewById(R.id.txt_click);
        l_yingdaoye = findViewById(R.id.l_yingdaoye);
        l_yingdaoye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                l_yingdaoye.setVisibility(View.GONE);
            }
        });
        adm_lin = findViewById(R.id.adm_lin);
        txt_color = findViewById(R.id.txt_color);
        rel_ads = findViewById(R.id.rel_ads);
        img = findViewById(R.id.iv_read_menu);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPupowindpw(img);
            }
        });
        img_bottom_ad = findViewById(R.id.img_bottom_ad);
        ads_load = findViewById(R.id.ads_load);
        txt_ad = findViewById(R.id.txt_ad);
        txt_adm_content = findViewById(R.id.txt_adm_content);
        img_space_big = findViewById(R.id.img_space_big);
        img_space_big.setOnClickListener(this);
        img_space_middle = findViewById(R.id.img_space_middle);
        img_space_middle.setOnClickListener(this);
        img_space_samlle = findViewById(R.id.img_space_samlle);
        img_space_samlle.setOnClickListener(this);
        img_space_samlle.setImageResource(R.mipmap.icon_spacing_small_yes);
        Img_select1 = findViewById(R.id.select1);
        Img_select2 = findViewById(R.id.select2);
        Img_select3 = findViewById(R.id.select3);
        Img_select4 = findViewById(R.id.select4);
        Img_select5 = findViewById(R.id.select5);
        mBackIv = findViewById(R.id.iv_read_back);
        mBackIv.setOnClickListener(this);
        tv_read_real = findViewById(R.id.tv_read_real);
        tv_read_real.setOnClickListener(this);
        txt_page = findViewById(R.id.txt_page);
        mCatalog = findViewById(R.id.iv_read_catalog);
        mCatalog.setOnClickListener(this);
        rv_read_top_bar = findViewById(R.id.rv_read_top_bar);
        cv_read_bottom_bar = findViewById(R.id.cv_read_bottom_bar);
        v_title = findViewById(R.id.v_title);
        mSettingBarCv = findViewById(R.id.cv_read_setting_bar);
//        mCatalogIv = findViewById(R.id.iv_read_catalog);
//        mCatalogIv.setOnClickListener(this);
        mBrightnessIv = findViewById(R.id.iv_read_brightness);
        mBrightnessIv.setOnClickListener(this);
        mSettingIv = findViewById(R.id.iv_read_setting);
        mSettingIv.setOnClickListener(this);
        mSys_light = findViewById(R.id.sys_ligin);
        iv_read_day_and_night=findViewById(R.id.iv_read_day_and_night);
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
        tv_load = findViewById(R.id.tv_load);
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
        tv_autoread = findViewById(R.id.tv_autoread);
        tv_autoread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_autoRead) {
                    is_autoRead = true;
                    keepScreenLongLight(true);
                    tv_autoread.setImageResource(R.mipmap.kaiguan_open);
                    txt_page.setmAutoPlayAble(true);
                    txt_page.startAutoPlay();

                } else {
                    keepScreenLongLight(false);
                    is_autoRead = false;
                    tv_autoread.setImageResource(R.mipmap.icon_auto_close);
                    txt_page.setmAutoPlayAble(false);
                    txt_page.stopAutoPlay();
                }
            }
        });
        img_adm = findViewById(R.id.img_adm);
        adm_title = findViewById(R.id.adm_title);
        activity_opening_videoview = findViewById(R.id.activity_opening_videoview);
        txt_content = findViewById(R.id.txt_content);
        txt_adm_load = findViewById(R.id.txt_adm_load);
        tv_read_next_chapter = findViewById(R.id.tv_read_next_chapter);
        tv_read_next_chapter.setOnClickListener(this);
        tv_read_previous_chapter = findViewById(R.id.tv_read_previous_chapter);
        tv_read_previous_chapter.setOnClickListener(this);
        mTurnNormalTv = findViewById(R.id.tv_read_turn_normal);
        mTurnNormalTv.setOnClickListener(this
        );
        tv_textstyle = findViewById(R.id.tv_textstyle);
        tv_textstyle.setOnClickListener(this);
        mTurnRealTv = findViewById(R.id.tv_read_turn_real);
        mTurnRealTv.setOnClickListener(this);
        tv_website = findViewById(R.id.tv_website);
        String tw = UrlObtainer.GetUrl();
        StringBuilder sb = new StringBuilder(tw);
        sb.replace(7, 8, "");
        tv_website.setText(sb.toString());
        sb_auto_read_progress = findViewById(R.id.sb_auto_read_progress);
        mReadSbChapterProgress = findViewById(R.id.sb_read_novel_progress);
        mTheme = ReadSettingManager.getInstance().getReadBgTheme();
        switch (mTheme) {
            case ReadSettingManager.READ_BG_DEFAULT:
                Img_select1.setVisibility(View.VISIBLE);
                break;
            case ReadSettingManager.READ_BG_1:
                Img_select2.setVisibility(View.VISIBLE);
                break;
            case ReadSettingManager.READ_BG_2:
                Img_select3.setVisibility(View.VISIBLE);
                break;
            case ReadSettingManager.READ_BG_3:
                Img_select4.setVisibility(View.VISIBLE);
                break;
            case ReadSettingManager.READ_BG_4:
                Img_select5.setVisibility(View.VISIBLE);
                break;

        }
        iv_read_decrease_font = findViewById(R.id.iv_read_decrease_font);
        iv_read_decrease_font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int t = Integer.parseInt(tv_textsize.getText().toString());
                t--;
                if (t > 0 && t < mPageLoader.text_size()) {
                    tv_textsize.setText(t + "");
                    mPageLoader.setTextSize(t);
                }
            }
        });
        tv_textsize = findViewById(R.id.tv_textsize);
        iv_read_increase_font = findViewById(R.id.iv_read_increase_font);
        iv_read_increase_font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int t = Integer.parseInt(tv_textsize.getText().toString());
                t++;
                if (t > 0 && t < mPageLoader.text_size()) {
                    tv_textsize.setText(t + "");
                    mPageLoader.setTextSize(t);
                }
            }
        });
//        iv_read_decrease_row_space = findViewById(R.id.iv_read_decrease_row_space);
//        iv_read_decrease_row_space.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int t = mPageLoader.getmTextInterval();
//                t--;
//                if (t > 0 && t < mPageLoader.getInterval()) {
//                    tv_jainju.setText(t + "");
//                    mPageLoader.setmTextInterval(t);
//                }
//            }
//        });
//        iv_read_increase_row_space = findViewById(R.id.iv_read_increase_row_space);
//        iv_read_increase_row_space.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int t = mPageLoader.getmTextInterval();
//                t++;
//                if (t > 0 && t < mPageLoader.getInterval()) {
//                    tv_jainju.setText(t + "");
//                    mPageLoader.setmTextInterval(t);
//                }
//            }
//        });
        txt_adm_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(load_url);
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
                true,
                mBrightnessObserver);
        myReceiver = new MyReceiver();
        // 注册广播接受者
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.read.android");//要接收的广播
        registerReceiver(myReceiver, intentFilter);//注册接收者
    }

    String load_url;
    int z = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                txt_page.abortAnimation();
            } else if (msg.what == 2) {
                mPageLoader.autoNextPage();
                handler.sendEmptyMessageDelayed(z, 3000);
            } else if (msg.what == 7) {
                if (tipDialog != null && tipDialog.isShowing()) {
                    tipDialog.dismiss();
                }
            } else if (msg.what == 5) {
                getOtherchpterCategorysSuccess(other_website);
            }
        }
    };
    int mTheme = 0;
    int mTurnType = 0;
    boolean is_autoRead = false;

    private void initImgSelect() {
        Img_select1.setVisibility(View.GONE);
        Img_select2.setVisibility(View.GONE);
        Img_select3.setVisibility(View.GONE);
        Img_select4.setVisibility(View.GONE);
        Img_select5.setVisibility(View.GONE);

    }

    private void updateWithTheme() {
        initImgSelect();
        mTheme0.setSelected(false);
        mTheme1.setSelected(false);
        mTheme2.setSelected(false);
        mTheme3.setSelected(false);
        mTheme4.setSelected(false);
        switch (mTheme) {
            case 0:
                mTheme0.setSelected(true);
                break;
            case 1:
                mTheme1.setSelected(true);
                break;
            case 2:
                mTheme2.setSelected(true);
                break;
            case 3:
                mTheme3.setSelected(true);
                break;
            case 4:
                mTheme4.setSelected(true);
                break;
        }
        // 设置相关颜色
        mPageLoader.setBgColor(mTheme, v_title, rel_ads);
    }

    /**
     * 显示设置栏
     */
    private void showSettingBar() {
        if (is_autoRead) {
            txt_page.stopAutoPlay();
        }
        mIsShowSettingBar = true;
        Animation bottomAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_enter);
        mSettingBarCv.startAnimation(bottomAnim);
        mSettingBarCv.setVisibility(View.VISIBLE);
    }

    PopupWindow popupWindow;
    boolean is_othersite = false, is_all_one = false;
    ChangeCategoryAdapter changeCategoryAdapter;
    ChangeOtherCategoryAdapter changeOtherCategoryAdapter;
    private TextView tv_jainju, tv_nodata;
    private View s_line, m_line;
    private TextView tvCatalog, mBookMark;
    ProgressBar progressBar;
    TextView tv_load;
    RecyclerView rv_catalog_list;

    @SuppressLint("WrongConstant")
    private void showPupowindpwChangeWebSite(View parent) {
        if (is_autoRead) {
            txt_page.stopAutoPlay();
        }
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popu_changewebsite, null);
        is_all_one = false;
        tv_nodata = view.findViewById(R.id.tv_nodata);
        s_line = view.findViewById(R.id.s_line);
        m_line = view.findViewById(R.id.m_line);
        tvCatalog = view.findViewById(R.id.tv_mulu);
        tvCatalog.setOnClickListener(this);
        progressBar = view.findViewById(R.id.pb_over);
        mBookMark = view.findViewById(R.id.tv_book_mark);
        mBookMark.setOnClickListener(this);
        s_line.setVisibility(View.GONE);
        m_line.setVisibility(View.VISIBLE);
        mBookMark.setTextColor(getResources().getColor(R.color.catalog_chapter_order_text));
        tvCatalog.setTextColor(getResources().getColor(R.color.red));
        if (changeCategoryAdapter != null) {
            changeCategoryAdapter.setPosition(-1);
            changeCategoryAdapter.notifyDataSetChanged();
        }
        rv_catalog_list = view.findViewById(R.id.rv_catalog_list1);
        rv_catalog_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // ts_recyle = view.findViewById(R.id.ts_recyle);
        progressBar.setVisibility(View.VISIBLE);
        getCategorys(mCollBook.get_id());
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.dp_302));
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
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (is_autoRead) {
                    txt_page.startAutoPlay();
                }
                backgroundAlpha(1f);
            }
        });
    }

    List<Categorys_one> categorys_ones = new ArrayList<>();
    List<Other_chpter> other_website = new ArrayList<>();
    List<Other_chpter> other_website_all = new ArrayList<>();
    Gson mGson = new Gson();

    private void getCategorys(String id) {
        String url = UrlObtainer.GetUrl() + "/api/index/hua_book";
        RequestBody requestBody = new FormBody.Builder()
                .add("novel_id", id)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                LogUtils.e(id+" "+json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        if (jsonObject.isNull("data")) {
                            getCategorysError();
                        } else {
                            JSONObject object = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = object.getJSONArray("data");
                            other_website.clear();
                            other_website_all.clear();
                            for (int z = 0; z < jsonArray.length(); z++) {
                                other_website.add(mGson.fromJson(jsonArray.getJSONObject(z).toString(), Other_chpter.class));
                                other_website_all.add(other_website.get(z));
                            }
                            handler.sendEmptyMessage(5);
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    for (int i = 0; i < other_website.size(); i++) {
//                                        Document doc = null;
//                                        try {
//                                                doc = Jsoup.parse(new URL(other_website.get(i).getUrl()), 5000);
//                                                //title = bookChapter.getTitle();
//                                                Elements elements;
//                                                elements = doc.body().getAllElements();//.select(other_website.get(i).getElement());
//                                                //Log.e("QQQ", "onResponse: "+getTextFromHtml(elements.html()));
//                                                if (getTextFromHtml(elements.html()) == null ||getTextFromHtml(elements.html()).trim().equals("")) {
//                                                    //other_website.remove(i);
//                                                    other_website_all.remove(other_website.get(i));
//                                                }
//                                        } catch (IOException e) {
//                                            continue;
//                                        }
//                                    }
//                                    handler.sendEmptyMessage(5);
//                                }
//                            }).start();
                        }
                    } else {
                        getCategorysError();
                    }
                } catch (JSONException e) {
                    getCategorysError();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                getCategorysError();
            }
        });
    }

    private void getCategorys(String id, int position) {
        String url = UrlObtainer.GetUrl() + "/api/index/hua_book";
        RequestBody requestBody = new FormBody.Builder()
                .add("novel_id", id)
                .add("weigh", position + "")
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                LogUtils.e(json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        if (jsonObject.isNull("data")) {
                            getCategorysError();
                        } else {
                            JSONObject object = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = object.getJSONArray("data");
                            other_website.clear();
                            other_website_all.clear();
                            for (int z = 0; z < jsonArray.length(); z++) {
                                other_website.add(mGson.fromJson(jsonArray.getJSONObject(z).toString(), Other_chpter.class));
                                other_website_all.add(other_website.get(z));
                            }
                            handler.sendEmptyMessage(5);
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    for (int i = 0; i < other_website.size(); i++) {
//                                        Document doc = null;
//                                        try {
//                                            doc = Jsoup.parse(new URL(other_website.get(i).getUrl()), 5000);
//                                            //title = bookChapter.getTitle();
//                                            Elements elements;
//                                            elements = doc.body().getAllElements();//.select(other_website.get(i).getElement());
//                                            Log.e("QQQ", "onResponse: "+getTextFromHtml(elements.html()));
//                                            if (i < other_website.size()) {
//                                                if ((other_website.get(i).getChapter_sum() == null || other_website.get(i).getReurl() == null) ||
//                                                        getTextFromHtml(elements.html()) == null || getTextFromHtml(elements.html()).trim().equals("")) {
//                                                    other_website_all.remove(other_website.get(i));
//                                                }
//                                            }
//                                        } catch (IOException e) {
//                                            other_website_all.remove(other_website.get(i));
//                                            continue;
//                                        }
//                                    }
//                                    handler.sendEmptyMessage(5);
//                                }
//                            }).start();
                        }
                    } else {
                        getCategorysError();
                    }
                } catch (JSONException e) {
                    getCategorysError();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                getCategorysError();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ADDRESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] ==PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted 授予权限
                    //处理授权之后逻辑
                    is_load = true;
                    tv_load.setVisibility(View.VISIBLE);
                    if (is_othersite == true) {
                        new Thread(new LoadRunable(categorys_ones.get(0).getText().get(d - 1).getChapter_url())).start();
                    } else {
                        postBooks_che();
                    }
                } else {
                    // Permission Denied 权限被拒绝
                    showShortToast("权限被禁用");
                }

                break;
            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private final int REQUEST_CODE_ADDRESS = 100;

    private void checkPermissioin(){
        int checkCoarse = ContextCompat.checkSelfPermission(WYReadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int checkCoarseFine = ContextCompat.checkSelfPermission(WYReadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (checkCoarse == PackageManager.PERMISSION_GRANTED  && checkCoarseFine == PackageManager.PERMISSION_GRANTED) {
            //已经授权
            is_load = true;
            tv_load.setVisibility(View.VISIBLE);
            if (is_othersite == true) {
                new Thread(new LoadRunable(categorys_ones.get(0).getText().get(d - 1).getChapter_url())).start();
            } else {
                postBooks_che();
            }
        } else {//没有权限
            ActivityCompat.requestPermissions(WYReadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ADDRESS);//申请授权
        }
    }

    String reurl = "", weight;
    TextView tv_read_real;

    /**
     * 获取HTML代码里的内容
     *
     * @param htmlStr
     * @return
     */
    public String getTextFromHtml(String htmlStr) {
        //去除html标签
        htmlStr = delHtmlTags(htmlStr);
        //去除空格" "
        htmlStr = htmlStr.replaceAll(" ", "");
        return htmlStr;
    }

    /**
     * 去除html代码中含有的标签
     *
     * @param htmlStr
     * @return
     */
    public String delHtmlTags(String htmlStr) {
        //定义script的正则表达式，去除js可以防止注入
        String scriptRegex = "<script[^>]*?>[\\s\\S]*?<\\/script>";
        //定义style的正则表达式，去除style样式，防止css代码过多时只截取到css样式代码
        String styleRegex = "<style[^>]*?>[\\s\\S]*?<\\/style>";
        //定义HTML标签的正则表达式，去除标签，只提取文字内容
        String htmlRegex = "<[^>]+>";
        //定义空格,回车,换行符,制表符
        String spaceRegex = "\\s*|\t|\r|\n";

        // 过滤script标签
        htmlStr = htmlStr.replaceAll(scriptRegex, "");
        // 过滤style标签
        htmlStr = htmlStr.replaceAll(styleRegex, "");
        // 过滤&nbsp
        htmlStr = htmlStr.replaceAll("&nbsp", "");
        // 过滤html标签
        htmlStr = htmlStr.replaceAll(htmlRegex, "\r\n");
        // 过滤空格等
        //htmlStr = htmlStr.replaceAll(spaceRegex, "");
        return htmlStr.trim(); // 返回文本字符串
    }

    public void getCategorysSuccess(List<Categorys_one> categorys_one) {
        for (int i = 0; i < categorys_one.size(); i++) {
            if (categorys_one.get(i).getText() == null || categorys_one.get(i).getReurl() == null) {
                categorys_one.remove(i);
            }
        }
        progressBar.setVisibility(View.GONE);
        if (categorys_one.size() == 0) {
            tv_nodata.setVisibility(View.VISIBLE);
            rv_catalog_list.setVisibility(View.GONE);
        } else {
            tv_nodata.setVisibility(View.GONE);
            rv_catalog_list.setVisibility(View.VISIBLE);
            this.categorys_ones = categorys_one;
            for (int i = 0; i < categorys_ones.size(); i++) {
                if (categorys_ones.get(i).getText().size() == 0) {
                    categorys_ones.remove(i);
                }
            }
            changeCategoryAdapter = new ChangeCategoryAdapter(this, categorys_ones);
            changeCategoryAdapter.setOnHistoryAdapterListener(new ChangeCategoryAdapter.HistoryAdapterListener() {
                @Override
                public void clickWord(int word) {
                    if (categorys_ones.get(word).getText() == null) {
                        showShortToast("数据源错误！");
                    } else {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        is_othersite = true;
                        reurl = categorys_ones.get(word).getElement();
                        // ((NetPageLoader) mPageLoader).setCategorys_ones();

                    }
                }
            });
            rv_catalog_list.setAdapter(changeCategoryAdapter);
        }
    }

    public void getOtherchpterCategorysSuccess(List<Other_chpter> categorys_one) {
//        for(int i=0;i<categorys_one.size();i++){
//            if(categorys_one.get(i).getChapter_sum()==null||categorys_one.get(i).getReurl()==null){
//                categorys_one.remove(i);
//            }
//        }
        progressBar.setVisibility(View.GONE);
        if (categorys_one.size() == 0) {
            tv_nodata.setVisibility(View.VISIBLE);
            rv_catalog_list.setVisibility(View.GONE);
        } else {
            tv_nodata.setVisibility(View.GONE);
            rv_catalog_list.setVisibility(View.VISIBLE);
            this.other_website = categorys_one;
//            for(int i=0;i<categorys_ones.size();i++){
//                if(categorys_ones.get(i).getText().size()==0){
//                    categorys_ones.remove(i);
//                }
//            }
            changeOtherCategoryAdapter = new ChangeOtherCategoryAdapter(this, other_website);
            changeOtherCategoryAdapter.setOnHistoryAdapterListener(new ChangeOtherCategoryAdapter.HistoryAdapterListener() {
                @Override
                public void clickWord(int word) {
                    if (other_website.get(word).getId() == null) {
                        showShortToast("数据源错误！");
                    } else {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        is_othersite = true;
                        reurl = other_website.get(word).getElement();
                        String tw = other_website.get(word).getUrl();
                        StringBuilder sb = new StringBuilder(tw);
                        if (sb.length() > 10) {
                            sb.replace(7, 9, "");
                        }
                        tv_website.setText(sb.toString());
                        mPageLoader.make_loading();
                        other_website_id=other_website.get(word).getId();
                        ((NetPageLoader) mPageLoader).setOtherCategorys_ones(other_website.get(word).getId(), is_all_one);
                    }
                }
            });
            rv_catalog_list.setAdapter(changeOtherCategoryAdapter);
        }
    }
    String other_website_id="";
    public void getCategorysError() {
        progressBar.setVisibility(View.GONE);
        tv_nodata.setVisibility(View.VISIBLE);
        rv_catalog_list.setVisibility(View.GONE);
        showShortToast("请求失败");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 隐藏设置栏
     */
    private void hideSettingBar() {
//        if(is_autoRead){
        txt_page.startAutoPlay();
//        }
        mIsShowSettingBar = false;
        Animation bottomExitAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_exit);
        bottomExitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSettingBarCv.setVisibility(View.GONE);
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
        //if(is_autoRead){
        txt_page.stopAutoPlay();
        //}
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
        txt_page.startAutoPlay();
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
        if (isNightMode) {
            iv_read_day_and_night.setImageResource(R.mipmap.icon_day);
            tv_read_day_and_night_mode.setText("日间");
        } else {
            iv_read_day_and_night.setImageResource(R.mipmap.icon_night);
            tv_read_day_and_night_mode.setText("夜间");
        }
        mPageLoader = txt_page.getPageLoader(mCollBook.isLocal(), is_othersite);
        switch (mPageLoader.getmPageMode()) {
            case PageView.PAGE_MODE_COVER:
                mTurnRealTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_white_selected));
                mTurnRealTv.setTextColor(getResources().getColor(R.color.red_aa));
                mTurnNormalTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                mTurnNormalTv.setTextColor(getResources().getColor(R.color.black));
                tv_read_real.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                tv_read_real.setTextColor(getResources().getColor(R.color.black));
                break;
            case PageView.PAGE_MODE_SCROLL:
                mTurnNormalTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_white_selected));
                mTurnNormalTv.setTextColor(getResources().getColor(R.color.red_aa));
                mTurnRealTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                mTurnRealTv.setTextColor(getResources().getColor(R.color.black));
                tv_read_real.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                tv_read_real.setTextColor(getResources().getColor(R.color.black));
                break;
            case PageView.PAGE_MODE_SIMULATION:
                mTurnNormalTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                mTurnNormalTv.setTextColor(getResources().getColor(R.color.black));
                tv_read_real.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_white_selected));
                tv_read_real.setTextColor(getResources().getColor(R.color.red_aa));
                mTurnRealTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                mTurnRealTv.setTextColor(getResources().getColor(R.color.black));
                break;
        }
        v_title.setBackgroundColor(mPageLoader.getmPageBg());
        rel_ads.setBackgroundColor(mPageLoader.getmPageBg());
        tv_textsize.setText(mPageLoader.getmTextSize() + "");
        //tv_jainju.setText(mPageLoader.getmTextInterval() + "");
        mPageLoader.setOnPageChangeListener(new PageLoader.OnPageChangeListener() {
            @Override
            public void onChapterChange(int pos) {
                if (mPageLoader.getWeight() != 0) {
                    tv_title.setText((mPageLoader.getChapterPos() + 1) + "/" + mPageLoader.getWeight());
                } else {
                    tv_title.setText((mPageLoader.getChapterPos() + 1) + "/" + mTxtChapters.size());
                }
                setCategorySelect(pos);
            }

            @Override
            public void closeAutoRead() {
                if (is_autoRead) {
                    keepScreenLongLight(false);
                    is_autoRead = false;
                    tv_autoread.setImageResource(R.mipmap.icon_auto_close);
                    txt_page.setmAutoPlayAble(false);
                    txt_page.stopAutoPlay();
                }
            }

            @Override
            public void onLoadChapter(List<TxtChapter> chapters, int pos) {
                if (pos % Constant.ADM_MAX == 0) {
                    post_adm(1);
                }
                mVmContentInfo.setNoval_id(mCollBook.get_id());
                if (is_othersite == true) {
                    if (is_all_one == true) {
                        is_othersite = false;
                    } else {
                        is_othersite = true;
                    }
                    Log.e("qqw", "onLoadChapter: " + 2);
                    mVmContentInfo.loadContent2(pos, chapters, reurl,other_website_id);
                } else {
                    Log.e("qqw", "onLoadChapter: " + 1);
                    mVmContentInfo.loadContent(pos + "", chapters);
                }
                if (mPageLoader.getPageStatus() == NetPageLoader.STATUS_LOADING
                        || mPageLoader.getPageStatus() == NetPageLoader.STATUS_ERROR) {
                    //冻结使用
                    mReadSbChapterProgress.setEnabled(false);
                }
//                if (mPageLoader.getWeight() != 0) {
//                    tv_title.setText((mPageLoader.getChapterPos() + 1) + "/" + mPageLoader.getWeight());
//                } else {
//                    tv_title.setText((mPageLoader.getChapterPos() + 1) + "/" + mTxtChapters.size());
//                }
                //隐藏提示
//                mReadTvPageTip.setVisibility(GONE);
                mReadSbChapterProgress.setProgress(0);
                last_pos = pos;
            }

            @Override
            public void onCategoryFinish(List<TxtChapter> chapters) {
                mTxtChapters.clear();
                mTxtChapters.addAll(chapters);
            }

            @Override
            public void onPageCountChange(int count) {
                mReadSbChapterProgress.setEnabled(true);
                mReadSbChapterProgress.setMax(count - 1);
                mReadSbChapterProgress.setProgress(0);
            }

            @Override
            public void onPageChange(int pos) {
                mReadSbChapterProgress.post(() -> {
                    mReadSbChapterProgress.setProgress(pos);
                });
            }

            @Override
            public void onShowAdm(boolean pos) {
//                is_adm=pos;
//                if (pos==true) {
//                    Log.e("zzz", "onPageChange: "+((mPageLoader.getmCurPage().getPosition())+" "+(mPageLoader.getmCurPageList().size())));
//                    post_adm(2);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            adm_lin.setVisibility(View.VISIBLE);
//                        }
//                    }, 50);
//                }else {
//                    if(activity_opening_videoview.isPlaying()){
//                        // activity_opening_videoview.stopPlayback();
//                        activity_opening_videoview.suspend();
//                    }
//                    adm_lin.setVisibility(View.GONE);
//                }
            }
        });
        mReadSbChapterProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //进行切换
                int pagePos = mReadSbChapterProgress.getProgress();
                if (pagePos != mPageLoader.getPagePos()) {
                    mPageLoader.skipToPage(pagePos);
                }
                //隐藏提示
                // mReadTvPageTip.setVisibility(GONE);
            }
        });
        sb_auto_read_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (pro > 10) {
                    pro = progress;
                } else {
                    pro = 10;

                }
                double scale = (double) progress / 100f;
                //Log.e("QQQ", "onProgressChanged: "+scale);
                if (is_autoRead) {
                    read_speed = 1000;
                    read_speed = 1000 + (int) (7000 * (1 - scale));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (is_autoRead) {
                    txt_page.stopAutoPlay();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (is_autoRead) {
                    txt_page.setmAutoPlayInterval(read_speed);
                    txt_page.startAutoPlay();
                }
            }
        });
        sb_auto_read_progress.setProgress(pro);
        txt_page.setTouchListener(new PageView.TouchListener() {
            @Override
            public void center() {
                if (adm_lin.getVisibility() == View.VISIBLE) {
                    mPageLoader.autoNextPage();
                }
                if (mIsShowingOrHidingBar == false) {
                    if (!mIsShowSettingBar) {
                        showBar();
                    }
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
                return true;
            }

            @Override
            public boolean prePage() {
                if (mIsShowSettingBar) {
                    hideSettingBar();
                    return true;
                }
                if (mIsShowingOrHidingBar != false) {
                    hideBar();
                }
//                if(adm_lin.getVisibility()==View.VISIBLE){
//                    if (activity_opening_videoview.isPlaying()) {
//                        // activity_opening_videoview.stopPlayback();
//                        activity_opening_videoview.suspend();
//                    }
//                    adm_lin.setVisibility(View.GONE);
//                }
                //Log.e("WWW", "prePage: "+mPageLoader.getPagePos());
//                if(mPageLoader.getPagePos()==0) {
//                    post_adm(2);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            adm_lin.setVisibility(View.VISIBLE);
//                        }
//                    },500);
//                }
                return true;
            }

            @Override
            public boolean nextPage() {
                if (mIsShowSettingBar) {
                    hideSettingBar();
                    return true;
                }
                if (mIsShowingOrHidingBar != false) {
                    hideBar();
                }
                return true;
            }

            @Override
            public void cancel() {
            }
        });
        Log.e("qqq4", "doAfterInit: " + chpter_id + " " + page_id);
        BookRecordBean bookRecordBean = new BookRecordBean(mCollBook.get_id(), Integer.parseInt(chpter_id), Integer.parseInt(page_id));
        mPageLoader.openBook(mCollBook, bookRecordBean);//chpter_id
    }

    int last_pos;
    boolean is_adm = false;
    int pro = 35, read_speed;

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    List<TxtChapter> mTxtChapters = new ArrayList<>();

    private void setCategorySelect(int selectPos) {
        for (int i = 0; i < mTxtChapters.size(); i++) {
            TxtChapter chapter = mTxtChapters.get(i);
            if (i == selectPos) {
                chapter.setSelect(true);
            } else {
                chapter.setSelect(false);
            }
        }
    }

    ImageView img_adm;//广告图片
    TextView adm_title, txt_content, txt_adm_load;
    int t = 0;
    String adm_id = "0";

    public void post_adm(int type) {
        String url = UrlObtainer.GetUrl() + "/api/index/get_adm";
        RequestBody requestBody = new FormBody.Builder()
                .add("type", type + "")
                .add("adm_id", adm_id)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
              //  Log.e("QQQ", "onResponse: " + adm_id + " " + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        String img = object.getString("value");
                        String title = object.getString("title");
                        String href = object.getString("url");
                        String id = object.getString("id");
                        String content = object.getString("content");
                        String is_download = object.getString("is_download");
                        adm_id = id;
                        //String type=img.substring(img.length()-1,img.length()-4);
                        if (img.contains(".png") || img.contains(".jpg") || img.contains(".jpeg")) {
                            String https;
                            if (img.contains("http")) {
                                https = img;
                            } else {
                                https = UrlObtainer.GetFileUrl() + "/" + img;
                            }
                            load_url = href;
                            if (type == 1) {
                                initBottomAdm(https, title, content, href, is_download);
                            } else {
                                AdsUtils.downLoadAds(WYReadActivity.this, https);
                                if (AdsUtils.isAdsLoaded(WYReadActivity.this, https)) {
                                    File file = AdsUtils.getAdsFile(WYReadActivity.this, https);
                                    ImageLoaderUtils.display(WYReadActivity.this, img_adm, file.getAbsolutePath());
                                    activity_opening_videoview.setVisibility(View.GONE);
                                    img_adm.setVisibility(View.VISIBLE);
                                    adm_title.setText(title);
                                    txt_content.setText(content);
                                    ScleAnimtion(txt_adm_load);
                                    //showAdm(id, file.getAbsolutePath(), href, false);
                                }
                            }
                            //showAdm(id, https, href, false);
                        } else if (img.contains(".mp4")) {
                            load_url = href;
                            String https;
                            if (img.contains("http")) {
                                https = img;
                            } else {
                                https = UrlObtainer.GetFileUrl() + "/" + img;
                            }
                            AdsUtils.downLoadAds(WYReadActivity.this, https);
                            if (AdsUtils.isAdsLoaded(WYReadActivity.this, https)) {
                                File file = AdsUtils.getAdsFile(WYReadActivity.this, https);
                                activity_opening_videoview.setVisibility(View.VISIBLE);
                                img_adm.setVisibility(View.GONE);
                                mmr = new MediaMetadataRetriever();
                                mmr.setDataSource(file.getAbsolutePath());
                                MediaController mediaController = new MediaController(WYReadActivity.this);
                                mediaController.setVisibility(View.GONE);//隐藏进度条
                                activity_opening_videoview.setMediaController(mediaController);
                                Uri rawUri = Uri.parse(file.getAbsolutePath());
                                activity_opening_videoview.setVideoURI(rawUri);
                                activity_opening_videoview.requestFocus();
                                activity_opening_videoview.start();
                                activity_opening_videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                    @Override
                                    public boolean onError(MediaPlayer mp, int what, int extra) {
                                        //异常回调
                                        activity_opening_videoview.pause();
                                        return false;//如果方法处理了错误，则为true；否则为false。返回false或根本没有OnErrorListener，将导致调用OnCompletionListener。
                                    }
                                });
                                adm_title.setText(title);
                                txt_content.setText(content);
                                ScleAnimtion(txt_adm_load);
                                //img_adm.setImageBitmap(bmFrame);
                                //showAdm(id, file.getAbsolutePath(), href, true);
                            }
                            //showAdm(id, https, href, true);
                        }
                    } else {
                        return;
                    }
                } catch (JSONException e) {
                    return;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                return;
            }
        });
    }

    MediaMetadataRetriever mmr;

    private void initBottomAdm(String https, String title, String content, String href, String is_download) {
        ImageLoaderUtils.display(this, img_bottom_ad, https);
        if (is_download.equals("2")) {
            ads_load.setVisibility(View.VISIBLE);
            ScleAnimtion(ads_load);
        } else {
            ads_load.setVisibility(View.GONE);
        }
        ads_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(href);
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        if (content != null) {
            txt_adm_content.setText(content);
        }
        txt_ad.setText(title);
    }

    AdmDialog tipDialog;
    VideoView activity_opening_videoview;

    private void showAdm(String id, String href, String url, boolean is_video) {
        //Log.e("QQQ", "showAdm: " + href);
        if (tipDialog == null && ((Activity) this).isDestroyed()) {
            return;
        } else {
            if (is_autoRead) {
                txt_page.stopAutoPlay();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(7);
                    }
                }).start();
            }
            tipDialog = new AdmDialog.Builder(this)
                    .setContent("http://www.cnblogs.com")
                    .setHref(url)
                    .setIs_img(is_video)
                    .setEnsure("继续阅读")
                    .setOnClickListener(new AdmDialog.OnClickListener() {
                        @Override
                        public void clickEnsure() {
                            //mCacheSizeTv.setText(FileUtil.getLocalCacheSize());
                        }

                        @Override
                        public void clickCancel() {

                        }

                        @Override
                        public void clickAddAdm() {
                            post_addadm(id);
                        }
                    })
                    .setImg(href)
                    .build();
            tipDialog.setCanceledOnTouchOutside(false);
            tipDialog.show();
            //backgroundAlpha(0.5f);
            tipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    backgroundAlpha(1f);
                    if (is_autoRead) {
                        txt_page.startAutoPlay();
                    }
                }
            });
        }
    }

    private void post_addadm(String id) {
        String url = UrlObtainer.GetUrl() + "/api/index/add_adm";
        //Log.e("WWW", "post_addadm: "+url);
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                return;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_read_back:
                if (is_load) {
                    if (!WYReadActivity.this.isDestroyed()) {
                        final TipDialog tipDialog = new TipDialog.Builder(WYReadActivity.this)
                                .setContent("正在下载，是否退出？")
                                .setCancel("取消")
                                .setEnsure("确定")
                                .setOnClickListener(new TipDialog.OnClickListener() {
                                    @Override
                                    public void clickEnsure() {
                                        is_loading = true;
                                        finish();
                                    }

                                    @Override
                                    public void clickCancel() {
                                        finish();
                                    }
                                })
                                .build();
                        tipDialog.show();
                    }
                } else {
                    finish();
                }
                break;
            case R.id.iv_read_menu:
                // showPupowindpw(mMenuIv);
                break;
            case R.id.tv_read_previous_chapter:
                if(mPageLoader.mStatus==STATUS_FINISH) {
                    setCategorySelect(mPageLoader.skipPreChapter());
                }
                break;
            case R.id.tv_book_mark:
                progressBar.setVisibility(View.VISIBLE);
                rv_catalog_list.setVisibility(View.GONE);
                is_all_one = true;
                s_line.setVisibility(View.VISIBLE);
                m_line.setVisibility(View.GONE);
                mBookMark.setTextColor(getResources().getColor(R.color.red));
                tvCatalog.setTextColor(getResources().getColor(R.color.catalog_chapter_order_text));
                getCategorys(mCollBook.get_id(), mPageLoader.getChapterPos());
//                if (changeCategoryAdapter != null) {
//                    changeCategoryAdapter.setPosition(mPageLoader.getChapterPos());
//                    changeCategoryAdapter.notifyDataSetChanged();
//                }
                break;
            case R.id.tv_mulu:
                progressBar.setVisibility(View.VISIBLE);
                rv_catalog_list.setVisibility(View.GONE);
                is_all_one = false;
                s_line.setVisibility(View.GONE);
                m_line.setVisibility(View.VISIBLE);
                mBookMark.setTextColor(getResources().getColor(R.color.catalog_chapter_order_text));
                tvCatalog.setTextColor(getResources().getColor(R.color.red));
                getCategorys(mCollBook.get_id());
//                if (changeCategoryAdapter != null) {
//                    changeCategoryAdapter.setPosition(-1);
//                    changeCategoryAdapter.notifyDataSetChanged();
//                }
                break;
            case R.id.img_space_big:
                initInterval();
                mPageLoader.setmTextInterval(mPageLoader.getInterval() - 1);
                img_space_big.setImageResource(R.mipmap.icon_spacing_big_yes);
                break;
            case R.id.img_space_middle:
                initInterval();
                mPageLoader.setmTextInterval(mPageLoader.getInterval() / 2);
                img_space_middle.setImageResource(R.mipmap.icon_spacing_inthe_yes);
                break;
            case R.id.img_space_samlle:
                initInterval();
                mPageLoader.setmTextInterval(2);
                img_space_samlle.setImageResource(R.mipmap.icon_spacing_small_yes);
                break;
            case R.id.tv_read_next_chapter:
                if(mPageLoader.mStatus==STATUS_FINISH) {
                    setCategorySelect(mPageLoader.skipNextChapter());
                }
                break;
            case R.id.tv_read_catalog:
            case R.id.iv_read_catalog:
                // 目录
                if (mCollBook.isLocal() == false) {
                    // 跳转到目录页面，并且将自己的引用传递给它
                    Event<HoldReadActivityEvent> event = new Event<>(EventBusCode.CATALOG_HOLD_READ_ACTIVITY,
                            new HoldReadActivityEvent(WYReadActivity.this));
                    EventBusUtil.sendStickyEvent(event);
                    Intent intent = new Intent(WYReadActivity.this, CatalogActivity.class);
                    intent.putExtra(CatalogActivity.KEY_URL, mCollBook.get_id());    // 传递当前小说的 url
                    intent.putExtra(CatalogActivity.KEY_NAME, mCollBook.getTitle());  // 传递当前小说的名字
                    intent.putExtra(CatalogActivity.KEY_COVER, mCollBook.getCover()); // 传递当前小说的封面
                    intent.putExtra("weigh", mTxtChapters.size());
                    intent.putExtra("chapter_id", mPageLoader.getChapterPos());
                    startActivity(intent);
                } else if (mCollBook.isLocal() == true) {
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("MSPANSCOMMIT", (Serializable)mPageLoader.getmChapterList());
                    // 跳转到目录页面，并且将自己的引用传递给它
                    Event<HoldReadActivityEvent2> event = new Event<>(EventBusCode.CATALOG_HOLD_READ_ACTIVITY,
                            new HoldReadActivityEvent2(WYReadActivity.this, mPageLoader.getmChapterList()));
                    EventBusUtil.sendStickyEvent(event);
                    Intent intent = new Intent(WYReadActivity.this, LocalCatalogActivity.class);
                    intent.putExtra("file_path", mCollBook.get_id());    // 传递当前小说的 url
                    intent.putExtra(LocalCatalogActivity.KEY_ID, mCollBook.get_id());    // 传递当前小说的 url
                    intent.putExtra(LocalCatalogActivity.KEY_NAME, mCollBook.getTitle());  // 传递当前小说的名字
                    intent.putExtra(LocalCatalogActivity.KEY_COVER, mCollBook.getCover()); // 传递当前小说的封面
                    intent.putExtra(LocalCatalogActivity.KEY_POSTION, 0);
                    intent.putExtra("chapter_id", mPageLoader.getChapterPos());
                    //intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.tv_textstyle:
                // 隐藏上下栏，并显示亮度栏
                // hideBar();
                hideSettingBar();
                //showTextstyle();
                showPupowindpwTextStyle();
                break;
            case R.id.iv_read_brightness:
            case R.id.tv_read_brightness:
//                // 隐藏上下栏，并显示亮度栏
                hideBar();
                if(mCollBook.isLocal()==false) {
                     showPupowindpwChangeWebSite(mBrightnessIv);
                }else {
                     showShortToast("已缓存书籍不支持换源");
                }
                break;
            case R.id.iv_read_day_and_night_mode:
            case R.id.tv_read_day_and_night_mode:
                if (isNightMode) {
                    isNightMode = false;
                    int bgColor = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor1);
                    switch (mTheme) {
                        case ReadSettingManager.READ_BG_DEFAULT:
                            //mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_2c);
                            bgColor = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor1);
                            break;
                        case ReadSettingManager.READ_BG_1:
                            //mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_2f332d);
                            bgColor = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor2);
                            break;
                        case ReadSettingManager.READ_BG_2:
                            // mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_92918c);
                            bgColor = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor3);
                            break;
                        case ReadSettingManager.READ_BG_3:
                            //mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_383429);
                            bgColor = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor4);
                            break;
                        case ReadSettingManager.READ_BG_4:
                            // mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_627176);
                            bgColor = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor5);
                            break;
                    }
                    v_title.setBackgroundColor(bgColor);
                    rel_ads.setBackgroundColor(bgColor);
                    iv_read_day_and_night.setImageResource(R.mipmap.icon_night);
                    tv_read_day_and_night_mode.setText("夜间");
                } else {
                    iv_read_day_and_night.setImageResource(R.mipmap.icon_day);
                    tv_read_day_and_night_mode.setText("日间");
                    isNightMode = true;
                    v_title.setBackgroundColor(getResources().getColor(R.color.black));
                    rel_ads.setBackgroundColor(getResources().getColor(R.color.black));
                }//ReadSettingManager.NIGHT_MODE
                mPageLoader.setNightMode(isNightMode);
                hideBar();
                break;
            case R.id.iv_read_setting:
            case R.id.tv_read_setting:
                // 隐藏上下栏，并显示设置栏
                hideBar();
                showSettingBar();
                break;
            case R.id.v_read_theme_0:
                if (!isNightMode && mTheme == 0) {
                    break;
                }
                mTheme = ReadSettingManager.READ_BG_DEFAULT;
                updateWithTheme();
                Img_select1.setVisibility(View.VISIBLE);
                break;
            case R.id.v_read_theme_1:
                mTheme = ReadSettingManager.READ_BG_1;
                updateWithTheme();
                Img_select2.setVisibility(View.VISIBLE);
                break;
            case R.id.v_read_theme_2:
                mTheme = ReadSettingManager.READ_BG_2;
                updateWithTheme();
                Img_select3.setVisibility(View.VISIBLE);
                break;
            case R.id.v_read_theme_3:
                mTheme = ReadSettingManager.READ_BG_3;
                updateWithTheme();
                Img_select4.setVisibility(View.VISIBLE);
                break;
            case R.id.v_read_theme_4:
                mTheme = ReadSettingManager.READ_BG_4;
                updateWithTheme();
                Img_select5.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_read_turn_normal:
                mTurnNormalTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_white_selected));
                mTurnNormalTv.setTextColor(getResources().getColor(R.color.red_aa));
                mTurnRealTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                mTurnRealTv.setTextColor(getResources().getColor(R.color.black));
                tv_read_real.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                tv_read_real.setTextColor(getResources().getColor(R.color.black));
                mPageLoader.setPageMode(PageView.PAGE_MODE_SCROLL);
                break;
            case R.id.tv_read_real:
                mTurnNormalTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                mTurnNormalTv.setTextColor(getResources().getColor(R.color.black));
                tv_read_real.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_white_selected));
                tv_read_real.setTextColor(getResources().getColor(R.color.red_aa));
                mTurnRealTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                mTurnRealTv.setTextColor(getResources().getColor(R.color.black));
                mPageLoader.setPageMode(PageView.PAGE_MODE_SIMULATION);
                break;
            case R.id.tv_read_turn_real:
                mTurnRealTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_white_selected));
                mTurnRealTv.setTextColor(getResources().getColor(R.color.red_aa));
                mTurnNormalTv.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                mTurnNormalTv.setTextColor(getResources().getColor(R.color.black));
                tv_read_real.setBackground(getResources().getDrawable(R.drawable.shape_read_theme_grey_selected));
                tv_read_real.setTextColor(getResources().getColor(R.color.black));
                mPageLoader.setPageMode(PageView.PAGE_MODE_COVER);
                break;
            default:
                break;
        }
    }

    private RecyclerView ts_recyle;
    List<TextStyle> textStyles = new ArrayList<>();

    private void initInterval() {
        img_space_big.setImageResource(R.mipmap.icon_spacing_big_no);
        img_space_middle.setImageResource(R.mipmap.icon_spacing_inthe_no);
        img_space_samlle.setImageResource(R.mipmap.icon_spacing_small_no);
    }

    private void post_textStyle() {
        textStyles.add(new TextStyle(Constant.text_name0, "-1"));
        textStyles.add(new TextStyle(Constant.text_name1, "1"));
        textStyles.add(new TextStyle(Constant.text_name2, "2"));
        textStyles.add(new TextStyle(Constant.text_name3, "3"));
        textStyles.add(new TextStyle(Constant.text_name4, "4"));
        initTextStyle(textStyles);
    }

    private void showPupowindpwTextStyle() {
        if (is_autoRead) {
            txt_page.stopAutoPlay();
        }
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popu_textstyle, null);
        ImageView imageView = view.findViewById(R.id.iv_close);
        ts_recyle = view.findViewById(R.id.ts_recyle);
        progressBar1 = view.findViewById(R.id.pb_novel);
        progressBar1.setVisibility(View.VISIBLE);
        ts_recyle.setLayoutManager(new LinearLayoutManager(this));
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setFocusable(false);
        if (textStyles.size() == 0) {
            post_textStyle();
        } else {
            initTextStyle(textStyles);
        }
        popupWindow.setAnimationStyle(R.style.dialog_animation);
        backgroundAlpha(0.5f);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.BOTTOM, 0, -location[1]);
        //popupWindow.showAsDropDown(parent, (int) (parent.getWidth() * 0.7), 35);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (is_autoRead) {
                    txt_page.startAutoPlay();
                }
                backgroundAlpha(1f);
            }
        });
    }

    ProgressBar progressBar1;

    void initTextStyle(List<TextStyle> textStyles) {
        progressBar1.setVisibility(View.GONE);
        TextStyleAdapter textStyleAdapter = new TextStyleAdapter(this, textStyles);
        if (mStyle.equals("-1")) {
            textStyleAdapter.setPosition(0);
        } else if (mStyle.equals("1")) {
            textStyleAdapter.setPosition(1);
        } else if (mStyle.equals("2")) {
            textStyleAdapter.setPosition(2);
        } else if (mStyle.equals("3")) {
            textStyleAdapter.setPosition(3);
        } else if (mStyle.equals("4")) {
            textStyleAdapter.setPosition(4);
        }
        ts_recyle.setAdapter(textStyleAdapter);
        textStyleAdapter.setmListener(new TextStyleAdapter.ScreenListener() {
            @Override
            public void clickItem(int position) {
                textStyleAdapter.setPosition(position);
                mStyle = textStyles.get(position).getId();
                SpUtil.saveTextStyle(mStyle);
                mPageLoader.setmSype(mStyle);
                textStyleAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        is_autoRead = false;
        if(txt_page!=null) {
            txt_page.setmAutoPlayAble(false);
        }
        try {
            getContentResolver().unregisterContentObserver(mBrightnessObserver);
            unregisterReceiver(myReceiver);
            BookshelfNovelDbData dbData;
            if (mCollBook.isLocal() == true) {
                dbData = mDbManager.selectBookshelfNovel(load_path);
                if (dbData != null) {
                    dbData.setPosition(mPageLoader.getPagePos());
                    dbData.setChapterid(mPageLoader.getmCurChapterPos() + "");
                    dbData.setWeight(mTxtChapters.size());
                    dbData.setType(1);
                    LogUtils.e(mPageLoader.getPagePos() + " " + mPageLoader.getmCurChapterPos() + "");
                    mDbManager.insertOrUpdateBook(dbData);
                }
                Noval_Readcored noval_readcored = new Noval_Readcored(load_path, mPageLoader.getmCurChapterPos() + "", "", mCollBook.getTitle(), mCollBook.getAuthor(), mCollBook.getCover(), "1", mTxtChapters.get(mPageLoader.getmCurChapterPos()).title, mTxtChapters.size() + "");
                mDbManager.insertOrUpdateReadCordeNovel(noval_readcored, 0 + "");
                LogUtils.e(dbData + " " + load_path);
//                else {
//                    dbData = new BookshelfNovelDbData(load_path, mCollBook.getTitle(), mCollBook.getCover(), mPageLoader.getPagePos(), 1, mTxtChapters.size(), mPageLoader.getmCurChapterPos() + "", mTxtChapters.size(), status);
//                    mDbManager.insertOrUpdateBook(dbData);
//                }
            } else if (mCollBook.isLocal() == false) {
                dbData = mDbManager.selectBookshelfNovel(mCollBook.get_id());
                if (dbData != null) {
                    dbData.setPosition(mPageLoader.getPagePos());
                    dbData.setChapterid(mPageLoader.getmCurChapterPos() + "");
                    dbData.setWeight(mTxtChapters.size());
                    mDbManager.insertOrUpdateBook(dbData);
                    //Log.e("QQQ", "onDestroy: "+mPageLoader.getPagePos()+" "+mPageLoader.getmCurChapterPos());
                } else {
                    dbData = new BookshelfNovelDbData(mCollBook.get_id(), mCollBook.getTitle(), mCollBook.getCover(), mPageLoader.getPagePos(), -2, mTxtChapters.size(), mPageLoader.getmCurChapterPos() + "", mTxtChapters.size(), status);
                    mDbManager.insertOrUpdateBook(dbData);
                }
                Noval_Readcored noval_readcored = new Noval_Readcored(mCollBook.get_id(), mPageLoader.getmCurChapterPos() + "", "", mCollBook.getTitle(), mCollBook.getAuthor(), mCollBook.getCover(), "1", mTxtChapters.get(mPageLoader.getmCurChapterPos()).title, mTxtChapters.size() + "");
                mDbManager.insertOrUpdateReadCordeNovel(noval_readcored, 0 + "");
                if (login_admin != null) {
                    if (mPageLoader.getmCurChapterPos() == 0) {
                        setReadRecord(login_admin.getToken(), mCollBook.get_id(), 1 + "");
                    } else {
                        setReadRecord(login_admin.getToken(), mCollBook.get_id(), mPageLoader.getmCurChapterPos() + "");
                    }
                }
            }
            Intent intent_recever = new Intent("com.zhh.android");
            intent_recever.putExtra("position", mCollBook.get_id());
            sendBroadcast(intent_recever);
            Intent recever = new Intent("com.changebackground.android");
            sendBroadcast(recever);
        } catch (Exception ex) {
            finish();
        }
    }

    public void setReadRecord(String token, String novel_id, String chapter_id) {
        String url = UrlObtainer.GetUrl() + "/" + "/api/lookbook/add";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("novel_id", novel_id)
                .add("chapter_id", chapter_id)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                Log.e("QQQ", "onResponse: " + token + " " + novel_id + " " + chapter_id + " " + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        String message = jsonObject.getString("msg");
                        //getReadRecordSuccess(message);
                    } else {
                        showShortToast("请求错误");
                    }
                } catch (JSONException e) {
                    showShortToast("请求错误");
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                showShortToast("请求错误");
            }
        });
    }

    @Override
    public void bookChapters(BookChaptersBean bookChaptersBean) {

    }

    @Override
    public void finishChapters() {
        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
            txt_page.post(() -> {
                //mPageLoader.saveRecord();
                // mPageLoader.setChapterPos(Integer.parseInt(chpter_id));
                mPageLoader.openChapter();
            });
            //mPageLoader.skipToPage(Integer.parseInt(page_id));
            read_frist = SpUtil.getReadfirst();
            if (read_frist == 0) {
                l_yingdaoye.setVisibility(View.VISIBLE);
                //ScleAnimtion(tv_right);
                SpUtil.saveRead_first(1);
            }
        }
    }

    int d = 1;
    @SuppressLint("HandlerLeak")
    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                if (d < mTxtChapters.size()) {
                    new Thread(new LoadRunable(categorys_ones.get(0).getText().get(d - 1).getChapter_url())).start();
                    d++;
                    float pressent = (float) d / mTxtChapters.size() * 100;
                    tv_load.setText("正在缓存：" + (int) pressent + "%");
                    if (d == mTxtChapters.size()) {
                        tv_load.setVisibility(View.GONE);
                        mhandler.sendEmptyMessage(3);
                    }
                }
            } else if (msg.what == 3) {
                BookshelfNovelDbData bookshelfNovelDbData = mDbManager.selectBookshelfNovel(mCollBook.get_id());
                if (bookshelfNovelDbData == null) {
                    bookshelfNovelDbData = new BookshelfNovelDbData(mCollBook.get_id(), mCollBook.getTitle(), mCollBook.getCover(), 1, mTxtChapters.size(), 1 + "");
                }
                bookshelfNovelDbData.setType(1);
                bookshelfNovelDbData.setFuben_id(path + mCollBook.getTitle() + ".txt");
                bookshelfNovelDbData.setChapterid(mPageLoader.getmCurChapterPos() + "");
                mDbManager.insertOrUpdateBook(bookshelfNovelDbData);
                mCollBook.setIsLocal(true);
                is_load = false;
                Intent intent_recever = new Intent("com.zhh.android");
                intent_recever.putExtra("type", 1);
                sendBroadcast(intent_recever);
//                Event event = new Event(EventBusCode.NOVEL_INTRO_INIT);
//                EventBusUtil.sendEvent(event);
            } else if (msg.what == 4) {
                int j = msg.arg1;
                if (post_num * d <= mTxtChapters.size() && (j + 1) == post_num) {
                    d++;
                    if (is_loading == false) {
                        Log.e("WWW", "handleMessage: " + 111);
                        postBooks_che();
                    }
                }
                float pressent = (float) (((d - 1) * post_num + (j + 1))) / (mTxtChapters.size()) * 100;
                tv_load.setText("正在缓存:" + (int) pressent + "%");
                if (pressent >= 100) {
                    tv_load.setVisibility(View.GONE);
                    mhandler.sendEmptyMessage(3);
                }
            }
        }
    };
    int post_num = 5;
    boolean is_loading;

    void postBooks_che() {
        String url = UrlObtainer.GetUrl() + "/api/index/Books_che";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", mCollBook.get_id())
                .add("page", d + "")
                .add("limit", post_num + "")
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = object.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String title = jsonArray.getJSONObject(i).getString("title");
                            String content = jsonArray.getJSONObject(i).getString("content");
                            String load_title = title.replace("&nbsp", " ").replace("</br>", "\n");
                            String load_content = content.replace("&nbsp", " ").replace("</br>", "\n");
                            if (!load_title.contains("第")) {
                                String s = Pattern.compile("[^0-9]").matcher(title).replaceAll("");
                                String title2 = "";
                                if (load_title.contains(s)) {
                                    title2 = load_title.replace(s, "第" + s + "章 ");
                                }
                                addTxtToFileBuffered(title2 + "\n");
                                addTxtToFileBuffered(load_content + "\n");
                            } else {
                                addTxtToFileBuffered(load_title + "\n");
                                addTxtToFileBuffered(load_content + "\n");
                            }
                            Message message = new Message();
                            message.what = 4;
                            message.arg1 = i;
                            mhandler.sendMessage(message);

                        }
                        time_count = 0;
                    }
                } catch (JSONException e) {
                    if (time_count < Constant.TIME_MAX) {
                        postBooks_che();
                    } else {
                        showShortToast("error");
                    }
                    time_count++;
                }

            }

            @Override
            public void onFailure(String errorMsg) {
                if (time_count < Constant.TIME_MAX) {
                    postBooks_che();
                } else {
                    showShortToast(errorMsg);
                }
                time_count++;
            }
        });
    }

    int time_count;
    String path = Constant.BOOK_ADRESS + "/";

    private void addTxtToFileBuffered(String content) {
        //在文本文本中追加内容
        BufferedWriter out = null;
        try {
            File saveFile = new File(path + mCollBook.getTitle() + ".txt");
            if (!saveFile.exists()) {
                File dir = new File(saveFile.getParent());
                dir.mkdirs();
                saveFile.createNewFile();
            }
            //FileOutputStream(file, true),第二个参数为true是追加内容，false是覆盖
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile, true), "gbk"));
            out.newLine();//换行
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class LoadRunable implements Runnable {

        String svrInfo;

        public LoadRunable(String href) {
            this.svrInfo = href;
        }

        @Override

        public void run() {
            try {
                Document doc = Jsoup.connect(svrInfo).get();
                String title = doc.body().select("h1").text();
                Elements elements = doc.body().select(reurl);
                String content = "";
                for (Element link : elements) {
                    content = content + link.text();
                }
                String load_title = title.replace("&nbsp", " ").replace("</br>", "\n");
                String load_content = content.replace("&nbsp", " ").replace("</br>", "\n");
                if (!load_title.contains("第")) {
                    String s = Pattern.compile("[^0-9]").matcher(title).replaceAll("");
                    String title2 = "";
                    if (load_title.contains(s)) {
                        title2 = load_title.replace(s, "第" + s + "章 ");
                    }
                    addTxtToFileBuffered(title2 + "\n");
                    addTxtToFileBuffered(load_content + "\n");
                } else {
                    addTxtToFileBuffered(load_title + "\n");
                    addTxtToFileBuffered(load_content + "\n");
                }
                handler.sendEmptyMessage(2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    boolean is_load;
    private DatabaseManager mDbManager;

    private void showPupowindpw(View parent) {
        if (is_autoRead) {
            txt_page.stopAutoPlay();
        }
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popu_item, null);
        ListView lv_appointment = (ListView) view.findViewById(R.id.list_view);
        final String[] datas;
        datas = new String[]{"全本缓存", "加入书签"};
        final Integer[] ints = {R.mipmap.img_load, R.mipmap.icon_bookmark};
        PupoAdapter mainAdapter = null;
        if (datas != null) {
            mainAdapter = new PupoAdapter(datas, ints);
        }
        lv_appointment.setAdapter(mainAdapter);
        // 创建一个PopuWidow对象,设置宽高
        final PopupWindow popupWindow = new PopupWindow(view, getResources().getDimensionPixelOffset(R.dimen.dp_179), ViewGroup.LayoutParams.WRAP_CONTENT);

        // 使其聚集,可点击
        popupWindow.setFocusable(true);
        backgroundAlpha(0.5f);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        popupWindow.showAsDropDown(parent, -(parent.getWidth() * 2), App.getAppResources().getDimensionPixelOffset(R.dimen.dp_17));
        lv_appointment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        d = 1;
                        if (mIsShowingOrHidingBar) {
                            hideBar();
                        }
                        if (datas[position].equals("已缓存")) {
                            showShortToast("已经缓存");
                            break;
                        } else {
                            checkPermissioin();
                        }
                        break;
                    case 1:
                        if (mCollBook.isLocal() == true) {
                            boolean isflag = false;
                            List<BookmarkNovelDbData> bookmarkNovelDbData = mDbManager.queryAllBookmarkNovel(mCollBook.get_id());
                            for (int i = 0; i < bookmarkNovelDbData.size(); i++) {
                                if (bookmarkNovelDbData.get(i).getContent().equals(mPageLoader.getmCurPageList().get(mPageLoader.getPagePos()).getLines().get(1)) && bookmarkNovelDbData.get(i).getChapterid().equals(mPageLoader.getChapterPos() + "")) {
                                    isflag = true;
                                    break;
                                }
                            }
                            if (isflag == false) {
                                Date t = new Date();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                BookmarkNovelDbData dbData = new BookmarkNovelDbData(mCollBook.get_id(), mCollBook.getTitle(),
                                        mPageLoader.getmCurPageList().get(mPageLoader.getPagePos()).getLines().get(1), mTxtChapters.get(mPageLoader.getPagePos()).getStart(), mPageLoader.getPagePos(), 1, df.format(t), mPageLoader.getChapterPos() + "");
                                mDbManager.insertBookmarkNovel(dbData);
                                showShortToast("书签已添加");
                            } else {
                                showShortToast("此书签已存在");
                            }
                        } else if (mCollBook.isLocal() == false) {
                            boolean isflag = false;
                            List<BookmarkNovelDbData> bookmarkNovelDbData = mDbManager.queryAllBookmarkNovel(mCollBook.get_id());
                            for (int i = 0; i < bookmarkNovelDbData.size(); i++) {
                                if (bookmarkNovelDbData.get(i).getContent().equals(mPageLoader.getmCurPageList().get(mPageLoader.getPagePos()).getLines().get(1)) && bookmarkNovelDbData.get(i).getChapterid().equals(mPageLoader.getChapterPos() + "")) {
                                    isflag = true;
                                    break;
                                }
                            }
                            if (isflag == false) {
                                Date t = new Date();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                BookmarkNovelDbData dbData = new BookmarkNovelDbData(mCollBook.get_id(), mCollBook.getTitle(),
                                        mPageLoader.getmCurPageList().get(mPageLoader.getPagePos()).getLines().get(1), mTxtChapters.get(mPageLoader.getPagePos()).getStart(), mPageLoader.getPagePos(), 0, df.format(t), mPageLoader.getChapterPos() + "");
                                mDbManager.insertBookmarkNovel(dbData);
                                showShortToast("书签已添加");
                            } else {
                                showShortToast("此书签已存在");
                            }
                        }
                        break;
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (is_autoRead) {
                    txt_page.startAutoPlay();
                }
                backgroundAlpha(1f);
            }
        });

    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//       else {
//            return super.onKeyDown(keyCode, event);
//        }
//    }

    @Override
    public void showLoading() {

    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        if (is_load) {
            if (!WYReadActivity.this.isDestroyed()) {
                final TipDialog tipDialog = new TipDialog.Builder(WYReadActivity.this)
                        .setContent("正在下载，是否退出？")
                        .setCancel("取消")
                        .setEnsure("确定")
                        .setOnClickListener(new TipDialog.OnClickListener() {
                            @Override
                            public void clickEnsure() {
                                is_loading = true;
                                finish();
                            }

                            @Override
                            public void clickCancel() {

                            }
                        })
                        .build();
                tipDialog.show();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void stopLoading() {

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
        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
            mPageLoader.chapterError();
        }
    }

    MyReceiver myReceiver;

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int i = intent.getIntExtra("chpter", 0);
            int j = intent.getIntExtra("page_id", 0);
            LogUtils.e(i + " " + j);
            mPageLoader.make_loading();
            if (j == 0) {
                mPageLoader.skipToChapter(i);
            } else if (j == -1) {
                BookRecordBean bookRecordBean = new BookRecordBean(page_id, i, 0);
                mPageLoader.openBook(mCollBook, bookRecordBean);//chpter_id
            } else {
                BookRecordBean bookRecordBean = new BookRecordBean(page_id, i, j);
                mPageLoader.openBook(mCollBook, bookRecordBean);//chpter_id
//                mReadSbChapterProgress.post(() -> {
//                    mReadSbChapterProgress.setProgress(j);
//                });
            }
        }
    }

}
