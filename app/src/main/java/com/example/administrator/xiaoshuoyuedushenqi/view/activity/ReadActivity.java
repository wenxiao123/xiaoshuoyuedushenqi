package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.provider.Settings;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import android.widget.Switch;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.TextStyleAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IReadContract;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_Readcored;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.TextStyle;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookmarkNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DetailedChapterData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.epub.EpubData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.epub.EpubTocItem;
import com.example.administrator.xiaoshuoyuedushenqi.entity.epub.OpfData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.EpubCatalogInitEvent;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.HoldReadActivityEvent;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.ReadPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.EpubUtils;
import com.example.administrator.xiaoshuoyuedushenqi.util.EventBusUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.ScreenUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.widget.PageView;
import com.example.administrator.xiaoshuoyuedushenqi.widget.RealPageView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 小说阅读界面
 *
 * @author Created on 2019/11/25
 */
public class ReadActivity extends BaseActivity<ReadPresenter>
        implements IReadContract.View, View.OnClickListener {
    private static final String TAG = "ReadActivity";
    private static final String LOADING_TEXT = "正在加载中…";

    public static final String KEY_NOVEL_URL = "read_key_novel_url";
    public static final String KEY_CHPATER_ID = "chpter_id";
    public static final String KEY_SERIALIZE = "serialize";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_ZJ_ID = "ZJ_id";
    public static final String KEY_NAME = "read_key_name";
    public static final String KEY_COVER = "read_key_cover";
    public static final String KEY_CHAPTER_INDEX = "read_key_chapter_index";
    public static final String Catalog_start_Position = "Catalog_start_Position";
    public static final String KEY_POSITION = "read_key_position";
    public static final String KEY_IS_REVERSE = "read_key_is_reverse";
    public static final String KEY_TYPE = "read_key_type";
    public static final String KEY_IS_CATALOG = "is_catalog";
    public static final String KEY_SECOND_POSITION = "read_key_second_position";

    private RealPageView mPageView;
    private TextView mNovelTitleTv, tv_textstyle;
    private TextView mNovelProgressTv;
    private TextView mStateTv;

    private RelativeLayout mTopSettingBarRv;
    private ConstraintLayout mBottomBarCv;
    private LinearLayout mBrightnessBarCv;
    private LinearLayout mSettingBarCv;
    private LinearLayout set_textstyle;
    private TextView mSys_light;
    private ImageView mBackIv;
    private ImageView mMenuIv;
    private TextView mPreviousChapterTv;
    private SeekBar mNovelProcessSb;
    private TextView mCatalogProgressTv;
    private TextView mNextChapterTv;
    private ImageView mCatalogIv;
    private ImageView mBrightnessIv;
    private ImageView mDayAndNightModeIv;
    private ImageView mSettingIv;
    private TextView mCatalogTv;
    private TextView mBrightnessTv;
    private TextView mDayAndNightModeTv;
    private TextView mSettingTv;
    private TextView tvCatalog, mBookMark;
    private View s_line, m_line;

    private SeekBar mBrightnessProcessSb;
    private Switch mSystemBrightnessSw;

    private TextView mDecreaseFontIv;
    private TextView mIncreaseFontIv;
    private ImageView mDecreaseRowSpaceIv;
    private ImageView mIncreaseRowSpaceIv;
    private ImageView tv_autoread;
    private RecyclerView ts_recyle;
    private View mTheme0;
    private View mTheme1;
    private View mTheme2;
    private View mTheme3;
    private View mTheme4;
    private TextView mTurnNormalTv;
    private TextView mTurnRealTv;

    // 章节 url 列表（通过网络请求获取）
    private List<String> mChapterUrlList = new ArrayList<>();
    // Epub Opf 文件数据
    private OpfData mOpfData;
    // Epub 文件的目录
    private List<EpubTocItem> mEpubTocList = new ArrayList<>();
    List<String> testBean;
    // 图片的父目录，为 opf 文件的父目录
    private String mParentPath = "";
    // 网络小说目录
    private List<String> mNetCatalogList = new ArrayList<>();
    // 当前小说阅读进度（本地 txt 用）
    private float mTxtNovelProgress;
    // 小说内容（本地 txt 用）
    private String mNovelContent;
    // 小说进度（本地 txt 用）
    private String mNovelProgress = "";

    // 以下内容通过 Intent 传入
    private String mNovelUrl;   // 小说 url，本地小说为 filePath
    private String mName;   // 小说名
    private String mCover;  // 小说封面
    private int mType, mCatalog;      // 小说类型，0 为网络小说， 1 为本地 txt 小说, 2 为本地 epub 小说
    private int mChapterIndex;   // 当前阅读的章节索引
    private int mCatalog_posotion;   // 当前阅读的位置
    private int mPosition;  // 文本开始读取位置
    private boolean mIsReverse; // 是否需要将章节列表倒序
    private int mSecondPosition; // epub 用
    private TextView tv_textsize;
    private ImageView sys_select;
    private DatabaseManager mDbManager;
    private boolean mIsLoadingChapter = false;  // 是否正在加载具体章节
    private boolean mIsShowingOrHidingBar = false;  // 是否正在显示或隐藏上下栏
    private boolean mIsShowBrightnessBar = false;   // 是否正在显示亮度栏
    private boolean mIsShowtextstyle = false;   // 是否正在显示亮度栏
    private boolean mIsSystemBrightness = true;     // 是否为系统亮度
    private boolean mIsShowSettingBar = false;      // 是否正在显示设置栏
    private boolean mIsNeedWrite2Db = true;         // 活动结束时是否需要将书籍信息写入数据库
    private boolean mIsUpdateChapter = false;   // 是否更新章节

    // 从 sp 中读取
    private float mTextSize;    // 字体大小
    private float mRowSpace;    // 行距
    private int mTheme;         // 阅读主题
    private String mStyle;         // 字体样式
    private float mBrightness;  // 屏幕亮度，为 -1 时表示系统亮度
    private boolean mIsNightMode;           // 是否为夜间模式
    private int mTurnType;      // 翻页模式：0 为正常，1 为仿真
    private String[] strings = {"方正卡通", "方正旗黑", "方正华隶"};
    private float mMinTextSize = 36f;
    private float mMaxTextSize = 76f;
    private float mMinRowSpace = 0f;
    private float mMaxRowSpace = 48f;
    private int chpter_id;
    private int first_read;
    private int serialize;
    private Login_admin login_admin;
    private String zj_id;
    private TextView tv_jainju;
    // 监听系统亮度的变化
    private ContentObserver mBrightnessObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (mIsSystemBrightness) {
                // 屏幕亮度更新为新的系统亮度
                ScreenUtil.setWindowBrightness(ReadActivity.this,
                        (float) ScreenUtil.getSystemBrightness() / ScreenUtil.getBrightnessMax());
            }
        }
    };

    @Override
    protected void doBeforeSetContentView() {
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);   //隐藏标题栏
        //StatusBarUtil.setLightColorStatusBar(this);
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_read;
    }

    @Override
    protected ReadPresenter getPresenter() {
        return new ReadPresenter();
    }

    String mAuthor = "";

    @Override
    protected void initData() {
        // 从前一个活动传来
        mAuthor = getIntent().getStringExtra(KEY_AUTHOR);
        chpter_id = getIntent().getIntExtra(KEY_CHPATER_ID, 1);
        zj_id = getIntent().getStringExtra(KEY_ZJ_ID);
        first_read = getIntent().getIntExtra("first_read", 0);
        mNovelUrl = getIntent().getStringExtra(KEY_NOVEL_URL);
        mName = getIntent().getStringExtra(KEY_NAME);
        mCover = getIntent().getStringExtra(KEY_COVER);
        weigh = getIntent().getIntExtra("weigh", 0);
        serialize = getIntent().getIntExtra(KEY_SERIALIZE, 0);
        //mChapterIndex = getIntent().getIntExtra(KEY_CHAPTER_INDEX, 0);
        mCatalog_posotion = getIntent().getIntExtra(Catalog_start_Position, 0);
        mPosition = getIntent().getIntExtra(KEY_POSITION, 0);
        mIsReverse = getIntent().getBooleanExtra(KEY_IS_REVERSE, false);
        mType = getIntent().getIntExtra(KEY_TYPE, 0);
        mCatalog = getIntent().getIntExtra(KEY_IS_CATALOG, 0);
        mSecondPosition = getIntent().getIntExtra(KEY_SECOND_POSITION, 0);
        // 从 SP 得到
        mTextSize = SpUtil.getTextSize();
        mRowSpace = SpUtil.getRowSpace();
        mTheme = SpUtil.getTheme();
        mStyle = SpUtil.getTextStyle();
        mBrightness = SpUtil.getBrightness();
        mIsNightMode = SpUtil.getIsNightMode();
        mTurnType = SpUtil.getTurnType();

        // 其他
        mDbManager = DatabaseManager.getInstance();
        login_admin = (Login_admin) SpUtil.readObject(this);
    }

    boolean isChecked = false;

    @Override
    protected void initView() {
        tv_jainju = findViewById(R.id.tv_jainju);
        tv_jainju.setText(mRowSpace + "");
        mTopSettingBarRv = findViewById(R.id.rv_read_top_bar);
        mBottomBarCv = findViewById(R.id.cv_read_bottom_bar);
        mBrightnessBarCv = findViewById(R.id.cv_read_brightness_bar);
        mSettingBarCv = findViewById(R.id.cv_read_setting_bar);
        set_textstyle = findViewById(R.id.set_textstyle);
        mPageView = findViewById(R.id.pv_read_page_view);
        mPageView.setPageViewListener(new PageView.PageViewListener() {
            @Override
            public void updateProgress(String progress) {
                mNovelProgress = progress;
                mNovelProgressTv.setText(progress);
            }

            @Override
            public void next() {
                if (mType == 0) {
                    nextNet();
                } else if (mType == 1) {
                    showShortToast("已经到最后了");
                } else if (mType == 2) {
                    nextEpub();
                }
            }

            @Override
            public void pre() {
                if (mType == 0) {
                    preNet();
                } else if (mType == 1) {
                    showShortToast("已经到最前了");
                } else if (mType == 2) {
                    preEpub();
                }
            }

            @Override
            public void nextPage() {
                if (mType == 1) {
                    updateChapterProgress();
                }
            }

            @Override
            public void prePage() {
                if (mType == 1) {
                    updateChapterProgress();
                }
            }

            @Override
            public void showOrHideSettingBar() {
                if (mIsShowingOrHidingBar) {
                    return;
                }
                if (mIsShowBrightnessBar) {
                    hideBrightnessBar();
                    return;
                }
                if (mIsShowtextstyle) {
                    hideTextstyle();
                    return;
                }
                if (mIsShowSettingBar) {
                    hideSettingBar();
                    return;
                }
                mIsShowingOrHidingBar = true;
                if (mTopSettingBarRv.getVisibility() != View.VISIBLE) {
                    // 显示上下栏
                    showBar();
                } else {
                    // 隐藏上下栏
                    hideBar();
                }
            }
        });
        tv_textsize = findViewById(R.id.tv_textsize);
        mNovelTitleTv = findViewById(R.id.tv_read_novel_title);
        tv_textstyle = findViewById(R.id.tv_textstyle);
//        tv_textstyle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mStyle == 1) {
//                    mStyle = 0;
//                    tv_textstyle.setText("方正卡通");
//                } else {
//                    mStyle = 1;
//                    tv_textstyle.setText("方正旗黑");
//                }
//                mPageView.setmSype(mStyle);
//            }
//        });
        tv_textstyle.setOnClickListener(this);
        mNovelProgressTv = findViewById(R.id.tv_read_novel_progress);
        mStateTv = findViewById(R.id.tv_read_state);

        mBackIv = findViewById(R.id.iv_read_back);
        mBackIv.setOnClickListener(this);

        mSys_light = findViewById(R.id.sys_ligin);
        mSys_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChecked) {
                    // 变为系统亮度
                    mIsSystemBrightness = true;
                    mBrightness = -1f;
                    // 将屏幕亮度设置为系统亮度
                    ScreenUtil.setWindowBrightness(ReadActivity.this,
                            (float) ScreenUtil.getSystemBrightness() / ScreenUtil.getBrightnessMax());
                    //mSys_light.setBackground(getResources().getDrawable(R.drawable.bachground_red));
                    sys_select.setImageResource(R.mipmap.sys_selected);
                    isChecked = false;
                } else {
                    // 变为自定义亮度
                    mIsSystemBrightness = false;
                    // 将屏幕亮度设置为自定义亮度
                    mBrightness = (float) mBrightnessProcessSb.getProgress() / 100;
                    ScreenUtil.setWindowBrightness(ReadActivity.this, mBrightness);
                    //mSys_light.setBackground(getResources().getDrawable(R.drawable.bachground_cricyle));
                    sys_select.setImageResource(R.mipmap.sys_select);
                    isChecked = true;
                }
            }


        });

        mMenuIv = findViewById(R.id.iv_read_menu);
        mMenuIv.setOnClickListener(this);
        mPreviousChapterTv = findViewById(R.id.tv_read_previous_chapter);
        mPreviousChapterTv.setOnClickListener(this);
        mNextChapterTv = findViewById(R.id.tv_read_next_chapter);
        mNextChapterTv.setOnClickListener(this);
        mCatalogIv = findViewById(R.id.iv_read_catalog);
        mCatalogIv.setOnClickListener(this);
        mBrightnessIv = findViewById(R.id.iv_read_brightness);
        mBrightnessIv.setOnClickListener(this);
        mDayAndNightModeIv = findViewById(R.id.iv_read_day_and_night_mode);
        mDayAndNightModeIv.setOnClickListener(this);
        mSettingIv = findViewById(R.id.iv_read_setting);
        mSettingIv.setOnClickListener(this);
        mCatalogTv = findViewById(R.id.tv_read_catalog);
        mCatalogTv.setOnClickListener(this);
        mBrightnessTv = findViewById(R.id.tv_read_brightness);
        sys_select = findViewById(R.id.sys_select);
        sys_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChecked) {
                    // 变为系统亮度
                    mIsSystemBrightness = true;
                    mBrightness = -1f;
                    // 将屏幕亮度设置为系统亮度
                    ScreenUtil.setWindowBrightness(ReadActivity.this,
                            (float) ScreenUtil.getSystemBrightness() / ScreenUtil.getBrightnessMax());
                    //mSys_light.setBackground(getResources().getDrawable(R.drawable.bachground_red));
                    sys_select.setImageResource(R.mipmap.sys_selected);
                    isChecked = false;
                } else {
                    // 变为自定义亮度
                    mIsSystemBrightness = false;
                    // 将屏幕亮度设置为自定义亮度
                    mBrightness = (float) mBrightnessProcessSb.getProgress() / 100;
                    ScreenUtil.setWindowBrightness(ReadActivity.this, mBrightness);
                    //mSys_light.setBackground(getResources().getDrawable(R.drawable.bachground_cricyle));
                    sys_select.setImageResource(R.mipmap.sys_select);
                    isChecked = true;
                }
            }
        });
        mBrightnessTv.setOnClickListener(this);
        mDayAndNightModeTv = findViewById(R.id.tv_read_day_and_night_mode);
        mDayAndNightModeTv.setOnClickListener(this);
        mSettingTv = findViewById(R.id.tv_read_setting);
        mSettingTv.setOnClickListener(this);

        tvCatalog = findViewById(R.id.tv_mulu);
        tvCatalog.setOnClickListener(this);

        mBookMark = findViewById(R.id.tv_book_mark);
        mBookMark.setOnClickListener(this);

        s_line = findViewById(R.id.s_line);
        m_line = findViewById(R.id.m_line);

        mNovelProcessSb = findViewById(R.id.sb_read_novel_progress);
        mNovelProcessSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double scale = (double) progress / 100f;
                if (mIsUpdateChapter) {
                    if (mType == 0) {   // 网络小说
                        mChapterIndex = (int) ((mNetCatalogList.size() - 1) * scale);
                        mCatalogProgressTv.setText(mNetCatalogList.get(mChapterIndex));
                    } else if (mType == 1) {    // 本地 txt
                        mTxtNovelProgress = (float) scale;
                        String s = String.valueOf(scale * 100);
                        mCatalogProgressTv.setText(s.substring(0, Math.min(5, s.length())) + "%");
                    } else if (mType == 2) {    // 本地 epub
                        mChapterIndex = (int) ((mEpubTocList.size() - 1) * scale);
                        mCatalogProgressTv.setText(mEpubTocList.get(mChapterIndex).getTitle());
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUpdateChapter = true;
                mCatalogProgressTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUpdateChapter = false;
                mCatalogProgressTv.setVisibility(View.GONE);
//                if (mType == 0 || mType == 2) {
                showChapter();
//                } else if (mType == 1) {
//                    mPageView.jumpWithProgress(mTxtNovelProgress);
//                }
            }
        });
        mCatalogProgressTv = findViewById(R.id.tv_read_catalog_progress);

        mBrightnessProcessSb = findViewById(R.id.sb_read_brightness_bar_brightness_progress);
        mBrightnessProcessSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!mIsSystemBrightness) {
                    // 调整亮度
                    mBrightness = (float) progress / 100;
                    ScreenUtil.setWindowBrightness(ReadActivity.this, mBrightness);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        mSystemBrightnessSw = findViewById(R.id.sw_read_system_brightness_switch);
//        mSystemBrightnessSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    // 变为系统亮度
//                    mIsSystemBrightness = true;
//                    mBrightness = -1f;
//                    // 将屏幕亮度设置为系统亮度
//                    ScreenUtil.setWindowBrightness(ReadActivity.this,
//                            (float) ScreenUtil.getSystemBrightness() / ScreenUtil.getBrightnessMax());
//                } else {
//                    // 变为自定义亮度
//                    mIsSystemBrightness = false;
//                    // 将屏幕亮度设置为自定义亮度
//                    mBrightness = (float) mBrightnessProcessSb.getProgress() / 100;
//                    ScreenUtil.setWindowBrightness(ReadActivity.this, mBrightness);
//                }
//            }
//        });

        mDecreaseFontIv = findViewById(R.id.iv_read_decrease_font);
        mDecreaseFontIv.setOnClickListener(this);
        mIncreaseFontIv = findViewById(R.id.iv_read_increase_font);
        mIncreaseFontIv.setOnClickListener(this);
        mDecreaseRowSpaceIv = findViewById(R.id.iv_read_decrease_row_space);
        mDecreaseRowSpaceIv.setOnClickListener(this);
        mIncreaseRowSpaceIv = findViewById(R.id.iv_read_increase_row_space);
        tv_autoread = findViewById(R.id.tv_autoread);
        ts_recyle = findViewById(R.id.ts_recyle);
        ts_recyle.setLayoutManager(new LinearLayoutManager(this));
        mIncreaseRowSpaceIv.setOnClickListener(this);
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
                mTurnNormalTv.setSelected(true);
                mPageView.setTurnType(PageView.TURN_TYPE.NORMAL);
                break;
            case 1:
                mTurnRealTv.setSelected(true);
                mPageView.setTurnType(PageView.TURN_TYPE.REAL);
                break;
        }
        tv_autoread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_autoRead) {
                    starttime();
                    is_autoRead = true;
                    tv_autoread.setImageResource(R.mipmap.kaiguan_open);

                } else {
                    stopTime();
                    is_autoRead = false;
                    tv_autoread.setImageResource(R.mipmap.kaiguan_close);
                }
            }
        });
    }

    ScheduledThreadPoolExecutor exec;
    int z = 0;
    boolean is_autoRead = false;

    void starttime() {
        exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                z = z + 10;
                mPageView.initDrawText(mNovelContent, mPageView.getPosition() + z);
            }
        }, 1000, 2000, TimeUnit.MILLISECONDS);

    }

    void stopTime() {
        if (exec != null) {
            exec.shutdownNow();
            exec = null;
        }
    }

    @Override
    protected void doAfterInit() {
        if (mType == 0) {
            // 先通过小说 url 获取所有章节 url 信息
            //mPresenter.getChapterList(mid);
            if (first_read == 0) {
                mPresenter.getDetailedChapterData(zj_id + "");
            } else if (first_read == 1) {
                mPresenter.getDetailedChapterData(mNovelUrl + "", 1 + "");
            } else if (first_read == 2) {
                mPresenter.getDetailedChapterData(mNovelUrl + "", chpter_id + "");
            }
        } else if (mType == 1) {
            // 通过 FilePath 读取本地小说
            mPresenter.loadTxt(mNovelUrl);
        } else if (mType == 2) {
            // 先根据 filePath 获得 OpfData
            mPresenter.getOpfData(mNovelUrl);
        }
//        TextStyleAdapter textStyleAdapter = new TextStyleAdapter(this, strings);
//        ts_recyle.setAdapter(textStyleAdapter);
//        textStyleAdapter.setmListener(new TextStyleAdapter.ScreenListener() {
//            @Override
//            public void clickItem(int position) {
//                if (position == 0) {
//                    tv_textstyle.setText("方正卡通");
//                } else {
//                    tv_textstyle.setText("方正旗黑");
//                }
//                textStyleAdapter.setPosition(position);
//                textStyleAdapter.notifyDataSetChanged();
//                mPageView.setmSype(position);
//                mStyle = position;
//            }
//        });
        if (mBrightness == -1f) {    // 系统亮度
            //mSystemBrightnessSw.setChecked(true);
            //mSys_light.setBackground(getResources().getDrawable(R.drawable.bachground_red));
            sys_select.setImageResource(R.mipmap.sys_selected);
        } else {    // 自定义亮度
            mBrightnessProcessSb.setProgress((int) (100 * mBrightness));
            //mSystemBrightnessSw.setChecked(false);
            //mSys_light.setBackground(getResources().getDrawable(R.drawable.bachground_cricyle));
            sys_select.setImageResource(R.mipmap.sys_select);
            ScreenUtil.setWindowBrightness(this, mBrightness);
        }

        if (mIsNightMode) { // 夜间模式
            nightMode();
        } else {    // 日间模式
            dayMode();
        }
        tv_textsize.setText((int) mTextSize + "");
        File file = new File(mStyle);
        if (!mStyle.equals("")) {
            tv_textstyle.setText(file.getName().replace(".ttf", ""));
        } else {
            tv_textstyle.setText("系统字体");
        }

//        textStyleAdapter.setPosition(mStyle);
//        textStyleAdapter.notifyDataSetChanged();
        // 监听系统亮度的变化
        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
                true,
                mBrightnessObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsNeedWrite2Db) {
            // 将书籍信息存入数据库
            mDbManager.deleteBookshelfNovel(mNovelUrl);
            if (mIsReverse) {   // 如果倒置了目录的话，需要倒置章节索引
                mChapterIndex = mChapterUrlList.size() - 1 - mChapterIndex;
            }
            if (mType == 1) {
                BookshelfNovelDbData dbData = new BookshelfNovelDbData(mNovelUrl, mName,
                        mCover, mChapterIndex, mPageView.getPosition(), mType);
                mDbManager.insertBookshelfNovel(dbData);
            } else if (mType == 0) {
                if (!mDbManager.isExistInBookshelfNovel(mNovelUrl + "")) {
                    BookshelfNovelDbData dbData;
                    dbData = new BookshelfNovelDbData(mNovelUrl + "", mName,
                            mCover, mPageView.getPosition(), mType, mPageView.getSecondPos(), mChapterIndex + "", weigh, serialize + "");

                    mDbManager.insertBookshelfNovel(dbData);
                    Noval_Readcored noval_readcored = new Noval_Readcored(mNovelUrl + "", id + "", serialize + " ", mName, mAuthor, mCover, 0 + "", mTitle, weigh + "");
                    mDbManager.insertReadCordeNovel(noval_readcored, mType + "");

                } else {
                    BookshelfNovelDbData dbData;
                    dbData = new BookshelfNovelDbData(mNovelUrl + "", mName,
                            mCover, mPageView.getPosition(), mType, mPageView.getSecondPos(), mChapterIndex + "", weigh);
                    mDbManager.updataBookshelfNovel(dbData, mNovelUrl + "");
                    Noval_Readcored noval_readcored = new Noval_Readcored(mNovelUrl + "", id + "", mName, mCover, 0 + "", mTitle, weigh + "");
                    mDbManager.updataReadCordeNovel(noval_readcored, mType + "", mNovelUrl + "");
                }
                if (login_admin != null) {
                    mPresenter.setReadRecord(login_admin.getToken(), mNovelUrl, id + "");
                    mPresenter.setBookshelfadd(login_admin.getToken(), mNovelUrl);
                }
            } else if (mType == 2) {
                BookshelfNovelDbData dbData = new BookshelfNovelDbData(mNovelUrl, mName,
                        mCover, mChapterIndex, mPageView.getFirstPos(), mType, mPageView.getSecondPos());
                mDbManager.insertBookshelfNovel(dbData);
            }
        }

        // 更新书架页面数据
        Event event = new Event(EventBusCode.BOOKSHELF_UPDATE_LIST);
        EventBusUtil.sendEvent(event);

        // 将相关数据存入 SP
        SpUtil.saveTextStyle(mStyle);
        SpUtil.saveTextSize(mTextSize);
        SpUtil.saveRowSpace(mRowSpace);
        SpUtil.saveTheme(mTheme);
        SpUtil.saveBrightness(mBrightness);
        SpUtil.saveIsNightMode(mIsNightMode);
        SpUtil.saveTurnType(mTurnType);

        // 解除监听
        getContentResolver().unregisterContentObserver(mBrightnessObserver);
    }

    public void setBookshelfadd(String token, String novel_id) {
        String url = UrlObtainer.GetUrl() + "api/Userbook/add";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("novel_id", novel_id)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        String message = jsonObject.getString("msg");
                        //mPresenter.(message);
                    } else {
                        //mPresenter.getReadRecordError("请求错误");
                        getChapterUrlListError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                getChapterUrlListError("请求错误");
                //mPresenter.getReadRecordError(errorMsg);
            }
        });
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    /**
     * 获取章节目录成功
     */
    @Override
    public void getChapterUrlListSuccess(List<String> chapterUrlList, List<String> chapterNameList) {
        if (chapterUrlList == null || chapterUrlList.isEmpty() ||
                chapterNameList == null || chapterNameList.isEmpty()) {
            mStateTv.setText("获取章节目录信息失败");
            return;
        }
        mChapterUrlList = chapterUrlList;
        mNetCatalogList = chapterNameList;
        if (mIsReverse) {
            Collections.reverse(mChapterUrlList);
            Collections.reverse(mNetCatalogList);
        }
        // 获取具体章节信息
        if (mChapterUrlList.get(mChapterIndex) != null) {
            mIsLoadingChapter = true;
            mPresenter.getDetailedChapterData(UrlObtainer
                    .getDetailedChapter(mChapterUrlList.get(mChapterIndex)));
        } else {
            mStateTv.setText("获取章节信息失败");
        }
    }

    /**
     * 获取章节目录失败
     */
    @Override
    public void getChapterUrlListError(String errorMsg) {
        mStateTv.setText("获取失败，请检查网络后重新加载");
    }

    String webContent;
    String webName;
    String mTitle;
    String id, weight;

    /**
     * 获取具体章节信息成功
     */
    @Override
    public void getDetailedChapterDataSuccess(DetailedChapterData data) {
        mIsLoadingChapter = false;
        if (data == null) {
            mStateTv.setText("获取不到相关数据，请查看其他章节");
            return;
        }
        id = data.getId();
        weight = data.getWeigh();
        mTitle = data.getTitle();
        mChapterIndex = Integer.parseInt(weight);
        webContent = data.getContent();
        webName = data.getTitle();
        mStateTv.setVisibility(View.GONE);
        if (mCatalog == 1) { //
            mPageView.initDrawText(data.getContent(), mCatalog_posotion);
        } else {
            mPageView.initDrawText(data.getContent(), mPosition);
        }
        mNovelTitleTv.setText(data.getTitle());
        updateChapterProgress();
    }

    /**
     * 获取具体章节信息失败
     */
    @Override
    public void getDetailedChapterDataError(String errorMsg) {
        mIsLoadingChapter = false;
        mStateTv.setText("获取失败，请检查网络后重新加载");
    }

    /**
     * 加载本地 txt 成功
     */
    @Override
    public void loadTxtSuccess(String text) {
        mNovelContent = text;
        mStateTv.setVisibility(View.GONE);
        mPageView.initDrawText(text, mPosition);
        if (mCatalog == 1) {
            mPageView.initDrawText(text, mCatalog_posotion);
        }
        mNovelTitleTv.setText(mName);
        updateChapterProgress();
    }

    /**
     * 加载本地 txt 失败
     */
    @Override
    public void loadTxtError(String errorMsg) {
        if (errorMsg.equals(Constant.NOT_FOUND_FROM_LOCAL)) {
            // 该文件已从本地删除
            mStateTv.setText("该文件已从本地删除");
            mIsNeedWrite2Db = false;
            // 从数据库中删除该条记录
            mDbManager.deleteBookshelfNovel(mNovelUrl);
            // 更新书架页面
            Event event = new Event(EventBusCode.BOOKSHELF_UPDATE_LIST);
            EventBusUtil.sendEvent(event);
            return;
        }
        mStateTv.setText(errorMsg);
    }

    /**
     * 获取 Epub 的 Opf 文件数据成功
     */
    @Override
    public void getOpfDataSuccess(OpfData opfData) {
        if (opfData == null) {
            mStateTv.setText("读取失败");
            return;
        }
        mParentPath = opfData.getParentPath();
        // 解析 ncx 文件，得到小说目录
        try {
            mEpubTocList = EpubUtils.getTocData(opfData.getNcx());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            mStateTv.setText("读取失败");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            mStateTv.setText("读取失败");
            return;
        }
        mOpfData = opfData;
        // 获取具体章节数据
        mPresenter.getEpubChapterData(mParentPath, mOpfData.getSpine().get(mChapterIndex));
    }

    /**
     * 获取 Epub 的 Opf 文件数据失败
     */
    @Override
    public void getOpfDataError(String errorMsg) {
        mStateTv.setText("读取失败");
    }

    /**
     * 获取 Epub 的章节数据成功
     */
    @Override
    public void getEpubChapterDataSuccess(List<EpubData> dataList) {
        mIsLoadingChapter = false;
        if (dataList == null || dataList.isEmpty()) {
            mStateTv.setText("本章无数据，请查看其他章节");
            return;
        }
        mStateTv.setVisibility(View.GONE);
        // 通知 PageView 绘制章节数据
        mPageView.initDrawEpub(dataList, mPosition, mSecondPosition);
        // 设置该节的名称
        String title = dataList.get(0).getType() == EpubData.TYPE.TITLE ?
                dataList.get(0).getData() : "";
        mNovelTitleTv.setText(title);
        updateChapterProgress();
    }

    /**
     * 获取 Epub 的章节数据失败
     */
    @Override
    public void getEpubChapterDataError(String errorMsg) {
        mIsLoadingChapter = false;
        mStateTv.setText("读取失败");
    }

    @Override
    public void getReadRecordSuccess(String message) {

    }

    @Override
    public void getReadRecordError(String errorMsg) {

    }

    int weigh;

    /**
     * 点击上一页/下一页后加载具体章节
     */
    private void showChapter() {
        if (mIsLoadingChapter) {    // 已经在加载了
            return;
        }
        Log.e("AAA", "showChapter: " + mChapterIndex);
        if (mType == 0) {   // 显示网络小说
            if (mChapterIndex >= weigh) {
                showShortToast("当前显示为最后一章");
                return;
            } else if (mChapterIndex == 0) {
                showShortToast("当前显示为第一章");
                return;
            } else {
                mPosition = 0;     // 归零
                mPageView.clear();              // 清除当前文字
                mStateTv.setVisibility(View.VISIBLE);
                mStateTv.setText(LOADING_TEXT);
                mIsLoadingChapter = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.getDetailedChapterData(mNovelUrl, mChapterIndex + "");
                    }
                }, 200);
            }
        } else if (mType == 1) {
            // 记得归零！！！
            mPosition = 0;
            mPageView.clear();              // 清除当前文字或图片
            mPageView.initDrawText(mNovelContent, (int) (mNovelContent.length() * mTxtNovelProgress));
        } else if (mType == 2) {    // 显示 epub 小说
            // 记得归零！！！
            mPosition = 0;
            mSecondPosition = 0;
            mPageView.clear();              // 清除当前文字或图片
            mStateTv.setVisibility(View.VISIBLE);
            mStateTv.setText(LOADING_TEXT);
            mIsLoadingChapter = true;
            if (mOpfData != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.getEpubChapterData(mParentPath, mOpfData.getSpine().get(mChapterIndex));
                    }
                }, 200);
            } else {
                mStateTv.setText("加载失败");
                mIsLoadingChapter = false;
            }
        }
        updateChapterProgress();
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
                StatusBarUtil.setLightColorStatusBar(ReadActivity.this);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 结束时重置标记
                mIsShowingOrHidingBar = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation bottomAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_enter);
        mTopSettingBarRv.startAnimation(topAnim);
        mBottomBarCv.startAnimation(bottomAnim);
        mTopSettingBarRv.setVisibility(View.VISIBLE);
        mBottomBarCv.setVisibility(View.VISIBLE);
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
                mTopSettingBarRv.setVisibility(View.GONE);
                mIsShowingOrHidingBar = false;
                StatusBarUtil.setLightColorStatusBar(ReadActivity.this);
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
                mBottomBarCv.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mTopSettingBarRv.startAnimation(topExitAnim);
        mBottomBarCv.startAnimation(bottomExitAnim);
    }

    /**
     * 显示亮度栏
     */
    private void showBrightnessBar() {
        if (mType == 0) {
            mIsShowBrightnessBar = true;
            Animation bottomAnim = AnimationUtils.loadAnimation(
                    this, R.anim.read_setting_bottom_enter);
            mBrightnessBarCv.startAnimation(bottomAnim);
            mBrightnessBarCv.setVisibility(View.VISIBLE);
        } else {
            showShortToast("非网络小说不支持该功能");
        }
    }

    /**
     * 显示亮度栏
     */
    private void showTextstyle() {
        post_textStyle();
        mIsShowtextstyle = true;
        Animation bottomAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_enter);
        set_textstyle.startAnimation(bottomAnim);
        set_textstyle.setVisibility(View.VISIBLE);
    }
    List<TextStyle> textStyles = new ArrayList<>();;
    private void post_textStyle() {
        Gson mGson = new Gson();
        String url = UrlObtainer.GetUrl() + "api/index/get_wordes";
        RequestBody requestBody = new FormBody.Builder()
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
                        textStyles.clear();
                        textStyles.add(new TextStyle("系统字体", true));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String name = jsonArray.getJSONObject(i).getString("name");
                            TextStyle style = mGson.fromJson(jsonArray.getJSONObject(i).toString(), TextStyle.class);
                            style.setLoad(isLoad(style, name));
                            textStyles.add(style);
                        }
                        initTextStyle(textStyles);
                    } else {
                        showShortToast("请求失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                showShortToast(errorMsg);
            }
        });
    }

    void initTextStyle(List<TextStyle> textStyles) {
        File file = new File(mStyle);
        int w = 0;
        for (int z = 0; z < textStyles.size(); z++) {
            if (file.getName().contains(textStyles.get(z).getName())) {
                w = z;
                break;
            }
        }
        TextStyleAdapter textStyleAdapter = new TextStyleAdapter(this, textStyles);
        textStyleAdapter.setPosition(w);
        ts_recyle.setAdapter(textStyleAdapter);
        textStyleAdapter.setmListener(new TextStyleAdapter.ScreenListener() {
            @Override
            public void clickItem(int position) {
                textStyleAdapter.setPosition(position);
                if(textStyles.get(position).getUrl()==null){
                    mStyle=null;
                }else {
                    mStyle = textStyles.get(position).getUrl();
                }
                Log.e("ZZZ", "clickItem: "+mStyle);
                mPageView.setmSype(mStyle);
                textStyleAdapter.notifyDataSetChanged();
            }
        });

    }
    public void listText(){
        for(int z=0;z<textStyles.size();z++){
            isLoad(textStyles.get(z),textStyles.get(z).getName());
        }
    }
    boolean isLoad(TextStyle style, String textStyle) {
        File file = new File(Constant.FONT_ADRESS + "/Font/");
        File[] subFile = file.listFiles();
        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName().replace(".ttf", "");
                if (filename.equals(textStyle)) {
                    style.setUrl(subFile[iFileLength].getPath());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 显示亮度栏
     */
    private void hideTextstyle() {
        Animation bottomExitAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_exit);
        bottomExitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                set_textstyle.setVisibility(View.GONE);
                mIsShowtextstyle = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        set_textstyle.startAnimation(bottomExitAnim);
    }

    /**
     * 隐藏亮度栏
     */
    private void hideBrightnessBar() {
        Animation bottomExitAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_exit);
        bottomExitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBrightnessBarCv.setVisibility(View.GONE);
                mIsShowBrightnessBar = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBrightnessBarCv.startAnimation(bottomExitAnim);
    }

    /**
     * 显示设置栏
     */
    private void showSettingBar() {
        mIsShowSettingBar = true;
        Animation bottomAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_enter);
        mSettingBarCv.startAnimation(bottomAnim);
        mSettingBarCv.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_read_back:
                finish();
                break;
            case R.id.iv_read_menu:
                showPupowindpw(mMenuIv);
                break;
            case R.id.tv_read_previous_chapter:
                // 加载上一章节
                if (mType == 0) {
                    preNet();
                } else if (mType == 2) {
                    preEpub();
                } else if (mType == 1) {

                }
                break;
            case R.id.tv_book_mark:
                s_line.setVisibility(View.VISIBLE);
                m_line.setVisibility(View.GONE);
                mBookMark.setTextColor(getResources().getColor(R.color.red));
                tvCatalog.setTextColor(getResources().getColor(R.color.catalog_chapter_order_text));
                break;
            case R.id.tv_mulu:
                s_line.setVisibility(View.GONE);
                m_line.setVisibility(View.VISIBLE);
                mBookMark.setTextColor(getResources().getColor(R.color.catalog_chapter_order_text));
                tvCatalog.setTextColor(getResources().getColor(R.color.red));
                break;
            case R.id.tv_read_next_chapter:
                // 加载下一章节
                if (mType == 0) {
                    nextNet();
                } else if (mType == 2) {
                    nextEpub();
                } else if (mType == 1) {

                }
                break;
            case R.id.iv_read_catalog:
            case R.id.tv_read_catalog:
                // 目录
                if (mType == 0) {
                    // 跳转到目录页面，并且将自己的引用传递给它
                    Event<HoldReadActivityEvent> event = new Event<>(EventBusCode.CATALOG_HOLD_READ_ACTIVITY,
                            new HoldReadActivityEvent(ReadActivity.this));
                    EventBusUtil.sendStickyEvent(event);
                    Intent intent = new Intent(ReadActivity.this, CatalogActivity.class);
                    intent.putExtra(CatalogActivity.KEY_URL, mNovelUrl);    // 传递当前小说的 url
                    intent.putExtra(CatalogActivity.KEY_NAME, mName);  // 传递当前小说的名字
                    intent.putExtra(CatalogActivity.KEY_COVER, mCover); // 传递当前小说的封面
                    intent.putExtra("weigh", weigh);
                    startActivity(intent);
                } else if (mType == 1) {
                    // 跳转到目录页面，并且将自己的引用传递给它
                    Event<HoldReadActivityEvent> event = new Event<>(EventBusCode.CATALOG_HOLD_READ_ACTIVITY,
                            new HoldReadActivityEvent(ReadActivity.this));
                    EventBusUtil.sendStickyEvent(event);
                    Intent intent = new Intent(ReadActivity.this, LocalCatalogActivity.class);
                    intent.putExtra("file_path", mNovelUrl);    // 传递当前小说的 url
                    intent.putExtra(LocalCatalogActivity.KEY_NAME, mName);  // 传递当前小说的名字
                    intent.putExtra(LocalCatalogActivity.KEY_COVER, mCover); // 传递当前小说的封面
                    intent.putExtra(LocalCatalogActivity.KEY_POSTION, mPageView.getPosition());
                    startActivity(intent);
                } else if (mType == 2) {
                    // 跳转到 epub 目录界面
                    Event<EpubCatalogInitEvent> event = new Event<>(EventBusCode.EPUB_CATALOG_INIT,
                            new EpubCatalogInitEvent(ReadActivity.this, mEpubTocList,
                                    mOpfData, mNovelUrl, mName, mCover));
                    EventBusUtil.sendStickyEvent(event);
                    jumpToNewActivity(EpubCatalogActivity.class);
                }
                break;
            case R.id.tv_textstyle:
                // 隐藏上下栏，并显示亮度栏
                hideBar();
                hideSettingBar();
                showTextstyle();
                break;
            case R.id.iv_read_brightness:
            case R.id.tv_read_brightness:
                // 隐藏上下栏，并显示亮度栏
                hideBar();
                showBrightnessBar();
                break;
            case R.id.iv_read_day_and_night_mode:
            case R.id.tv_read_day_and_night_mode:
                if (!mIsNightMode) {    // 进入夜间模式
                    nightMode();
                } else {    // 进入日间模式
                    dayMode();
                }
                hideBar();
                break;
            case R.id.iv_read_setting:
            case R.id.tv_read_setting:
                // 隐藏上下栏，并显示设置栏
                hideBar();
                showSettingBar();
                break;
            case R.id.iv_read_decrease_font:
                if (mTextSize == mMinTextSize) {
                    break;
                }
                mTextSize--;
                tv_textsize.setText((int) mTextSize + "");
                mPageView.setTextSize(mTextSize);
                break;
            case R.id.iv_read_increase_font:
                if (mTextSize == mMaxTextSize) {
                    break;
                }
                mTextSize++;
                tv_textsize.setText((int) mTextSize + "");
                mPageView.setTextSize(mTextSize);
                break;
            case R.id.iv_read_decrease_row_space:
                if (mRowSpace == mMinRowSpace) {
                    break;
                }
                mRowSpace--;
                mPageView.setRowSpace(mRowSpace);
                tv_jainju.setText(mRowSpace + "");
                break;
            case R.id.iv_read_increase_row_space:
                if (mRowSpace == mMaxRowSpace) {
                    break;
                }
                mRowSpace++;
                mPageView.setRowSpace(mRowSpace);
                tv_jainju.setText(mRowSpace + "");
                break;
            case R.id.v_read_theme_0:
                if (!mIsNightMode && mTheme == 0) {
                    break;
                }
                mTheme = 0;
                updateWithTheme();
                break;
            case R.id.v_read_theme_1:
                if (!mIsNightMode && mTheme == 1) {
                    break;
                }
                mTheme = 1;
                updateWithTheme();
                break;
            case R.id.v_read_theme_2:
                if (!mIsNightMode && mTheme == 2) {
                    break;
                }
                mTheme = 2;
                updateWithTheme();
                break;
            case R.id.v_read_theme_3:
                if (!mIsNightMode && mTheme == 3) {
                    break;
                }
                mTheme = 3;
                updateWithTheme();
                break;
            case R.id.v_read_theme_4:
                if (!mIsNightMode && mTheme == 4) {
                    break;
                }
                mTheme = 4;
                updateWithTheme();
                break;
            case R.id.tv_read_turn_normal:
                if (mTurnType != 0) {
                    mTurnType = 0;
                    mTurnNormalTv.setSelected(true);
                    mTurnRealTv.setSelected(false);
                    mPageView.setTurnType(PageView.TURN_TYPE.NORMAL);
                }
                break;
            case R.id.tv_read_turn_real:
                if (mTurnType != 1) {
                    mTurnType = 1;
                    mTurnRealTv.setSelected(true);
                    mTurnNormalTv.setSelected(false);
                    mPageView.setTurnType(PageView.TURN_TYPE.REAL);
                }
                break;
            default:
                break;
        }
    }

    private void showPupowindpw(View parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popu_item2, null);
        ListView lv_appointment = (ListView) view.findViewById(R.id.list_view);
        final String[] datas = {"全本缓存", "添加书签"};
        final Integer[] ints = {R.mipmap.download, R.mipmap.bookmark};
        PupoAdapter mainAdapter = null;
        if (datas != null) {
            mainAdapter = new PupoAdapter(datas, ints);
        }
        lv_appointment.setAdapter(mainAdapter);
        // 创建一个PopuWidow对象,设置宽高
        final PopupWindow popupWindow = new PopupWindow(view, (int) (parent.getWidth() * 2), ViewGroup.LayoutParams.WRAP_CONTENT);

        // 使其聚集,可点击
        popupWindow.setFocusable(true);
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

                        break;
                    case 1:
                        if (mType == 1) {
                            String progress = mNovelProgressTv.getText().toString().substring(0, mNovelProgressTv.getText().length() - 1);
                            Date t = new Date();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            BookmarkNovelDbData dbData = new BookmarkNovelDbData(mNovelUrl, mName,
                                    mNovelContent.substring(mPageView.getPosition(), mPageView.getPosition() + 23), Float.parseFloat(progress) / 100, mPageView.getPosition(), mType, df.format(t), mChapterIndex + "");
                            mDbManager.insertBookmarkNovel(dbData);
                        } else if (mType == 2) {
                            BookshelfNovelDbData dbData = new BookshelfNovelDbData(mNovelUrl, mName,
                                    mCover, mChapterIndex, mPageView.getFirstPos(), mType, mPageView.getSecondPos());
                            mDbManager.insertBookshelfNovel(dbData);
                        } else if (mType == 0) {
                            Log.e(TAG, "onItemClick: " + mChapterIndex + "");
                            String progress = mNovelProgressTv.getText().toString().substring(0, mNovelProgressTv.getText().length() - 1);
                            Date t = new Date();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            BookmarkNovelDbData dbData = new BookmarkNovelDbData(mNovelUrl, webName,
                                    webContent.substring(mPageView.getPosition(), mPageView.getPosition() + 23), mChapterIndex, mPageView.getPosition(), mType, df.format(t), mChapterIndex + "");
                            mDbManager.insertBookmarkNovel(dbData);
                        }
                        showShortToast("书签已添加");
                        break;
                }
                popupWindow.dismiss();
            }
        });

    }

    class PupoAdapter extends BaseAdapter {
        LayoutInflater inflater = LayoutInflater.from(ReadActivity.this);
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
     * 进入夜间模式
     */
    private void nightMode() {
        mIsNightMode = true;
        // 取消主题
        mTheme0.setSelected(false);
        mTheme1.setSelected(false);
        mTheme2.setSelected(false);
        mTheme3.setSelected(false);
        mTheme4.setSelected(false);
        // 设置图标和文字
        mDayAndNightModeIv.setImageResource(R.drawable.read_day);
        mDayAndNightModeTv.setText(getResources().getString(R.string.read_day_mode));
        // 设置相关颜色
        mNovelTitleTv.setTextColor(getResources().getColor(R.color.read_night_mode_title));
        mNovelProgressTv.setTextColor(getResources().getColor(R.color.read_night_mode_title));
        mStateTv.setTextColor(getResources().getColor(R.color.read_night_mode_text));
        mPageView.setBgColor(getResources().getColor(R.color.read_night_mode_bg));
        mPageView.setTextColor(getResources().getColor(R.color.read_night_mode_text));
        mPageView.setBackBgColor(getResources().getColor(R.color.read_night_mode_back_bg));
        mPageView.setBackTextColor(getResources().getColor(R.color.read_night_mode_back_text));
        mPageView.post(new Runnable() {
            @Override
            public void run() {
                mPageView.updateBitmap();
            }
        });
    }

    /**
     * 进入白天模式
     */
    private void dayMode() {
        mIsNightMode = false;
        // 设置图标和文字
        mDayAndNightModeIv.setImageResource(R.drawable.read_night);
        mDayAndNightModeTv.setText(getResources().getString(R.string.read_night_mode));
        // 根据主题进行相关设置
        mPageView.post(new Runnable() {
            @Override
            public void run() {
                updateWithTheme();
            }
        });
    }

    /**
     * 根据主题更新阅读界面
     */
    private void updateWithTheme() {
        if (mIsNightMode) {
            // 退出夜间模式
            mDayAndNightModeIv.setImageResource(R.drawable.read_night);
            mDayAndNightModeTv.setText(getResources().getString(R.string.read_night_mode));
            mIsNightMode = false;
        }
        mTheme0.setSelected(false);
        mTheme1.setSelected(false);
        mTheme2.setSelected(false);
        mTheme3.setSelected(false);
        mTheme4.setSelected(false);
        int bgColor = getResources().getColor(R.color.read_theme_0_bg);
        int textColor = getResources().getColor(R.color.read_theme_0_text);
        int backBgColor = getResources().getColor(R.color.read_theme_0_back_bg);
        int backTextColor = getResources().getColor(R.color.read_theme_0_back_text);
        switch (mTheme) {
            case 0:
                mTheme0.setSelected(true);
                bgColor = getResources().getColor(R.color.read_theme_0_bg);
                textColor = getResources().getColor(R.color.read_theme_0_text);
                backBgColor = getResources().getColor(R.color.read_theme_0_back_bg);
                backTextColor = getResources().getColor(R.color.read_theme_0_back_text);
                break;
            case 1:
                mTheme1.setSelected(true);
                bgColor = getResources().getColor(R.color.read_theme_1_bg);
                textColor = getResources().getColor(R.color.read_theme_1_text);
                backBgColor = getResources().getColor(R.color.read_theme_1_back_bg);
                backTextColor = getResources().getColor(R.color.read_theme_1_back_text);
                break;
            case 2:
                mTheme2.setSelected(true);
                bgColor = getResources().getColor(R.color.read_theme_2_bg);
                textColor = getResources().getColor(R.color.read_theme_2_text);
                backBgColor = getResources().getColor(R.color.read_theme_2_back_bg);
                backTextColor = getResources().getColor(R.color.read_theme_2_back_text);
                break;
            case 3:
                mTheme3.setSelected(true);
                bgColor = getResources().getColor(R.color.read_theme_3_bg);
                textColor = getResources().getColor(R.color.read_theme_3_text);
                backBgColor = getResources().getColor(R.color.read_theme_3_back_bg);
                backTextColor = getResources().getColor(R.color.read_theme_3_back_text);
                break;
            case 4:
                mTheme4.setSelected(true);
                bgColor = getResources().getColor(R.color.read_theme_4_bg);
                textColor = getResources().getColor(R.color.read_theme_4_text);
                backBgColor = getResources().getColor(R.color.read_theme_4_back_bg);
                backTextColor = getResources().getColor(R.color.read_theme_4_back_text);
                break;
        }
        // 设置相关颜色
        mNovelTitleTv.setTextColor(textColor);
        mNovelProgressTv.setTextColor(textColor);
        mStateTv.setTextColor(textColor);
        mPageView.setTextColor(textColor);
        mPageView.setBgColor(bgColor);
        mPageView.setBackTextColor(backTextColor);
        mPageView.setBackBgColor(backBgColor);
        mPageView.updateBitmap();
        if (PageView.IS_TEST) {
            mPageView.setBackgroundColor(bgColor);
            mPageView.invalidate();
        }
    }

    /**
     * 网络小说加载上一章节
     */
    private void preNet() {
        if (mChapterIndex == 0) {
            showShortToast("已经到最前了");
            return;
        }
        // 加载上一章节
        mChapterIndex--;
        showChapter();
    }

    /**
     * Epub 小说加载上一章节
     */
    private void preEpub() {
        if (mChapterIndex == 0) {
            showShortToast("已经到最前了");
            return;
        }
        // 加载上一章节
        mChapterIndex--;
        showChapter();
    }

    /**
     * 网络小说加载下一章节
     */
    private void nextNet() {
        if (mChapterIndex == mChapterUrlList.size() - 1) {
            showShortToast("已经到最后了");
            return;
        }
        // 加载下一章节
        mChapterIndex++;
        showChapter();
    }

    /**
     * Epub 小说加载下一章节
     */
    private void nextEpub() {
        if (mChapterIndex == mOpfData.getSpine().size() - 1) {
            showShortToast("已经到最后了");
            return;
        }
        // 加载下一章节
        mChapterIndex++;
        showChapter();
    }

    /**
     * 更新章节进度条的进度
     */
    private void updateChapterProgress() {
        int progress = 0;
        if (mType == 0) {   // 网络小说
            if (!mNetCatalogList.isEmpty()) {
                progress = (int) (100 * ((float) mChapterIndex / (mNetCatalogList.size() - 1)));
            }
        } else if (mType == 1) {    // 本地 txt
            if (mNovelProgress.equals("")) {
                if (mNovelContent.length() != 0) {
                    progress = (int) (100 * (float) mPosition / (mNovelContent.length() - 1));
                }
            } else {
                //mTxtNovelProgress = (float) scale;
                String s = String.valueOf(mTxtNovelProgress * 100);
                String str = s.substring(0, Math.min(5, s.length()));
                int f = (int) Float.parseFloat(str);
                progress = f;
//                try {
////                    progress = (int) Float.parseFloat(
////                            mNovelProgress.substring(0, mNovelProgress.length()-1));
//                    progress=(int) (mNovelContent.length() * mTxtNovelProgress)/100;
//                } catch (NumberFormatException e) {
//                    e.printStackTrace();
//                }
            }
        } else if (mType == 2) {    // 本地 epub
            if (!mEpubTocList.isEmpty()) {
                progress = (int) (100 * ((float) mChapterIndex / (mEpubTocList.size() - 1)));
            }
        }

        mNovelProcessSb.setProgress(progress);
    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
//            Intent intent=new Intent(ReadActivity.this,MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }
//        return false;
//    }
}