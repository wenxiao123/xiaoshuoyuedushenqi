package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.bifan.txtreaderlib.interfaces.IParagraphData;
import com.bifan.txtreaderlib.main.ParagraphData;
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.ChangeCategoryAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.TextStyleAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.app.App;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IReadContract;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovels;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Categorys_one;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Chapter;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_Readcored;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Text;
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
import com.example.administrator.xiaoshuoyuedushenqi.interfaces.IChapter;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.ReadPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.EpubUtils;
import com.example.administrator.xiaoshuoyuedushenqi.util.EventBusUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.FileUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.ScreenUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.widget.AdmDialog;
import com.example.administrator.xiaoshuoyuedushenqi.widget.PageView;
import com.example.administrator.xiaoshuoyuedushenqi.widget.RealPageView;
import com.example.administrator.xiaoshuoyuedushenqi.widget.ShareDialog;
import com.example.administrator.xiaoshuoyuedushenqi.widget.TipDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.example.administrator.xiaoshuoyuedushenqi.view.activity.LocalCatalogActivity.getCharset;

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
    public static final String KEY_NOVEL_URL_FUBEN = "read_key_novel_url_fuben";
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
    private TextView mNovelTitleTv,mNovelTitleTv1, tv_textstyle;
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
    private SeekBar mNovelProcessSb,sb_auto_read_progress;
    private TextView mCatalogProgressTv;
    private TextView mNextChapterTv;
    private ImageView mCatalogIv;
    private ImageView mBrightnessIv;
    private ImageView mDayAndNightModeIv;
    private ImageView mSettingIv;
    private TextView mCatalogTv;
    private TextView mBrightnessTv;
    private TextView mDayAndNightModeTv;
    private TextView mSettingTv, tv_website;
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
    RecyclerView rv_catalog_list;
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
    private String mStyle="";         // 字体样式
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
    private TextView tv_jainju,tv_nodata;
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

    /**
     *
     */
    String mAuthor = "";
    String adress = "";
    App app;
    String pid;
    @Override
    protected void initData() {
        app= (App) getApplication();
        // 从前一个活动传来
        mAuthor = getIntent().getStringExtra(KEY_AUTHOR);
        adress=getIntent().getStringExtra(KEY_NOVEL_URL_FUBEN);
        chpter_id = getIntent().getIntExtra(KEY_CHPATER_ID, 1);
        zj_id = getIntent().getStringExtra(KEY_ZJ_ID);
        first_read = getIntent().getIntExtra("first_read", 0);
        mNovelUrl = getIntent().getStringExtra(KEY_NOVEL_URL);
        pid=mNovelUrl;
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
       // Log.e("qqq1", "initData: "+mIsNightMode);
        mTurnType = SpUtil.getTurnType();
        // 其他
        mDbManager = DatabaseManager.getInstance();
        login_admin = (Login_admin) SpUtil.readObject(this);
        if(weigh==0){
            BookshelfNovelDbData bookshelfNovelDbData=mDbManager.selectBookshelfNovel(mNovelUrl);
            if(bookshelfNovelDbData!=null){
                weigh=bookshelfNovelDbData.getWeight();
            }
        }
    }

    boolean isChecked = false;

    @Override
    protected void initView() {
        App.init(this);
        tv_nodata=findViewById(R.id.tv_nodata);
        tv_jainju = findViewById(R.id.tv_jainju);
        tv_jainju.setText((int)mRowSpace + "");
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
                if (mType == 0) {
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMaximumFractionDigits(2);
                    float p = Float.parseFloat(progress.replace("%", "")) / 100;
                    if(weigh!=0) {
                        float v = (((float) mChapterIndex / (float) weigh) * 100 );//+ p * (1 / weigh));
                        mNovelProgressTv.setText(nf.format(v) + "%");
                    }else {
                        mNovelProgressTv.setText(0 + "%");
                    }
                } else {
                    float chpid = mPageView.getPosition();
                    float wight = mNovelContent.length();
                    float prent = (chpid / wight) * 100;
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMaximumFractionDigits(2);
//                    if(mDataList.get(i).getSecondPosition()>2) {
//                        contentViewHolder.tv_position.setText(nf.format(prent) + "%");
//                    }
                    mNovelProgressTv.setText(nf.format(prent) + "%");
                    float p = Float.parseFloat(progress.replace("%", "")) / 100;
                    //Log.e("QQQ", "loadTxtSuccess: "+mPageView.getPosition()+" "+text.length());
                    int o=0;
                    for (int j = 0; j < longs.size(); j++) {
                        if (mPageView.getPosition() < (int) longs.get(0)) {
                            o = 0;
                            break;
                        } else if (mPageView.getPosition() < (int) longs.get(j)) {
                            o = j - 1;
                            break;
                        }
                    }
                    mNovelTitleTv1.setText(o+"/"+weigh);
                    mNovelTitleTv.setText(mName+" / "+mChapterNameList.get(o).substring(3));
                }
            }

            @Override
            public void next() {
                if (mType == 0) {
                   nextNet();
                } else if (mType == 1) {
                    showShortToast("已经到最后了");
//                    mPresenter.loadTxt(adress);
//                    mCatalog=1;
//                    mCatalog_posotion=mPageView.getPosition()+1;
                } else if (mType == 2) {
                    nextEpub();
                }
            }

            @Override
            public void pre() {
                if (mType == 0) {
                    preNet();
                } else if (mType == 1) {
                    //showShortToast("已经到最前了");
                    mPresenter.loadTxt(adress);
                    mCatalog=1;
                    mCatalog_posotion=mPageView.getPosition()-100;
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
                    return ;
                }
                if (mIsShowBrightnessBar) {
                    hideBrightnessBar();
                    return ;
                }
                if (mIsShowtextstyle) {
                    hideTextstyle();
                    return ;
                }
                if (mIsShowSettingBar) {
                    hideSettingBar();
                    return ;
                }
                mIsShowingOrHidingBar = true;
                if (mTopSettingBarRv.getVisibility() != View.VISIBLE) {
                    // 显示上下栏
                    showBar();
                } else {
                    // 隐藏上下栏
                    hideBar();
                    return ;
                }
                return ;
            }

            @Override
            public boolean isshowSettingBar() {
                if (mBrightnessBarCv.getVisibility()==View.VISIBLE) {
                    hideBrightnessBar();
                    return true;
                }
                if (set_textstyle.getVisibility()==View.VISIBLE) {
                    hideTextstyle();
                    return true;
                }
                if (mSettingBarCv.getVisibility()==View.VISIBLE) {
                    hideSettingBar();
                    return true;
                }
                if (mBottomBarCv.getVisibility()==View.VISIBLE) {
                    // 隐藏上下栏
                    hideBar();
                    return true;
                }
                return false;
            }
        });
        int theme=SpUtil.getTheme();
        int bgColor;
        switch (theme) {
            case 0:
                bgColor = getResources().getColor(R.color.read_theme_0_bg);
                break;
            case 1:
                bgColor = getResources().getColor(R.color.read_theme_1_bg);
                break;
            case 2:
                bgColor = getResources().getColor(R.color.read_theme_2_bg);
                break;
            case 3:
                bgColor = getResources().getColor(R.color.read_theme_3_bg);
                break;
            case 4:
                bgColor = getResources().getColor(R.color.read_theme_4_bg);
                break;
            default:
                bgColor = getResources().getColor(R.color.read_theme_0_bg);
                break;
        }
        mPageView.setBackgroundColor(bgColor);
        if(mIsNightMode==true){
            mPageView.setBackgroundColor(getResources().getColor(R.color.read_night_mode_back_bg));
        }
        tv_textsize = findViewById(R.id.tv_textsize);
        mNovelTitleTv = findViewById(R.id.tv_read_novel_title);
        mNovelTitleTv1 = findViewById(R.id.tv_read_novel_title1);
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
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideTextstyle();
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
        tv_website = findViewById(R.id.tv_website);
        tv_website.setText(UrlObtainer.GetUrl());
        mNovelProcessSb = findViewById(R.id.sb_read_novel_progress);
        sb_auto_read_progress=findViewById(R.id.sb_auto_read_progress);
        mNovelProcessSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double scale = (double) progress / 100f;
                if (mIsUpdateChapter) {
                    if (mType == 0) {   // 网络小说
                        mChapterIndex = (int) ((weigh - 1) * scale);
                        mTxtNovelProgress = (float) scale;
                        mCatalogProgressTv.setText((int) (mTxtNovelProgress * 100) + "%");
                        //mNovelProgressTv.setText((int)(scale*100)+"%");
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
        sb_auto_read_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               pro=progress;
                double scale = (double) progress / 100f;
                //Log.e("QQQ", "onProgressChanged: "+scale);
                if (is_autoRead) {
                    read_speed=1000;
                    read_speed=1000+(int) (7000*(1-scale));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (is_autoRead) {
                    stopTime();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (is_autoRead) {
                    z = 0;
                    starttime();
                }
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
                //Log.e("aaa", "onStartTrackingTouch: "+111);
               // mBrightnessProcessSb.setFocusable(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Log.e("aaa", "onStartTrackingTouch: "+111);
                //mBrightnessProcessSb.setFocusable(false);
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
//        ts_recyle = findViewById(R.id.ts_recyle);
//        ts_recyle.setLayoutManager(new LinearLayoutManager(this));
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
        tv_load = findViewById(R.id.tv_load);
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
                    sb_auto_read_progress.setProgress(pro);
                    is_autoRead = true;
                    tv_autoread.setImageResource(R.mipmap.kaiguan_open);

                } else {
                    stopTime();
                    is_autoRead = false;
                    tv_autoread.setImageResource(R.mipmap.icon_auto_close);
                }
            }
        });
    }
    ProgressBar progressBar1;
    private void showPupowindpwTextStyle(View parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popu_textstyle, null);
        ImageView imageView = view.findViewById(R.id.iv_close);
        ts_recyle = view.findViewById(R.id.ts_recyle);
        progressBar1=view.findViewById(R.id.pb_novel);
        progressBar1.setVisibility(View.VISIBLE);
        ts_recyle.setLayoutManager(new LinearLayoutManager(this));
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        post_textStyle();
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
    PopupWindow popupWindow;
    @SuppressLint("WrongConstant")
    private void showPupowindpwChangeWebSite(View parent) {
        getCategorys(mNovelUrl);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popu_changewebsite, null);
        is_all_one = false;
        s_line = view.findViewById(R.id.s_line);
        m_line = view.findViewById(R.id.m_line);
        tvCatalog = view.findViewById(R.id.tv_mulu);
        tvCatalog.setOnClickListener(this);
        progressBar =view.findViewById(R.id.pb_over);
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
        rv_catalog_list =view.findViewById(R.id.rv_catalog_list1);
        rv_catalog_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // ts_recyle = view.findViewById(R.id.ts_recyle);
        progressBar.setVisibility(View.VISIBLE);
        // ts_recyle.setLayoutManager(new LinearLayoutManager(this));
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.dp_230));
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
    int pro=66;
    /**
     * 使用BufferedWriter进行文本内容的追加
     *
     * @param content
     */
    String path = Constant.BOOK_ADRESS + "/";

    private void addTxtToFileBuffered(String content) {
        //在文本文本中追加内容
        BufferedWriter out = null;
        try {
            File saveFile = new File(path + mName + ".txt");
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

    ScheduledThreadPoolExecutor exec;
    int z = 0;
    boolean is_autoRead = false;
    int read_speed=3000;
    void starttime() {
        exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                z = z + 10;
                if (mPageView.getPosition() <= mNovelContent.length() - 100) {
                    String content=mNovelContent.replace("</br>","\n").replace("&nbsp"," ");
                    mPageView.initDrawText(content, mPageView.getPosition() + z);
                } else {
                    handler1.sendEmptyMessage(1);
                }
            }
        }, 1000, read_speed, TimeUnit.MILLISECONDS);
        //Log.e("QQQ", "starttime: "+read_speed);

    }

    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            stopTime();
//            is_autoRead = false;
//            tv_autoread.setImageResource(R.mipmap.kaiguan_close);
            if (mType == 1) {
                showShortToast("你已看完小说");
            } else {
                mChapterIndex++;
                showChapter();
            }
        }
    };

    void stopTime() {
        if (exec != null) {
            exec.shutdownNow();
            exec = null;
        }
    }

    public  void post_adm(){
        String url = UrlObtainer.GetUrl()+"api/index/get_adm";
        RequestBody requestBody = new FormBody.Builder()
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONObject object=jsonObject.getJSONObject("data");
                        String img=object.getString("value");
                        String href=object.getString("url");
                        //String type=img.substring(img.length()-1,img.length()-4);
                        if(img.contains(".png")||img.contains(".jpg")||img.contains(".jpeg")){
                            String https;
                            if(img.contains("http")){
                                https=img;
                            }else {
                                https=UrlObtainer.GetUrl()+img;
                            }
                            showAdm(https,href,false);
                        }else if(img.contains(".mp4")){
                            String https;
                            if(img.contains("http")){
                                https=img;
                            }else {
                                https=UrlObtainer.GetUrl()+img;
                            }
                            showAdm(https,href,true);
                        }
                    }else {
                        return;
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

    private void showAdm(String href,String url,boolean is_video) {
        final AdmDialog tipDialog = new AdmDialog.Builder(this)
                .setContent("www.baidu.com")
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
                })
                .setImg(href)
                .build();
        tipDialog.setCanceledOnTouchOutside(false);
        tipDialog.show();
        backgroundAlpha(0.5f);
        tipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                backgroundAlpha(1f);
            }
        });
    }

    @Override
    protected void doAfterInit() {
       // Log.e("QQQ", "doAfterInit: "+mNovelUrl+" "+chpter_id);
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
            //Log.e("QQQ", "doAfterInit: "+adress);
            mPresenter.loadTxt(adress);
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
       // Log.e("QQQ", "initData: "+mIsNightMode);
        if (mIsNightMode==true) { // 夜间模式
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
        app.setCatalogDataAll(new ArrayList<>());
        if (mIsNeedWrite2Db) {
            // 将书籍信息存入数据库
            //mDbManager.deleteBookshelfNovel(mNovelUrl);
            if (mIsReverse) {   // 如果倒置了目录的话，需要倒置章节索引
                mChapterIndex = mChapterUrlList.size() - 1 - mChapterIndex;
            }
            if (mType == 1) {
//                if (!mDbManager.isExistInBookshelfNovel(mNovelUrl + "")) {
//                    BookshelfNovelDbData dbData = new BookshelfNovelDbData(mNovelUrl, mName,
//                            mCover, mPageView.getPosition(), mType, mNovelContent.length(), mChapterIndex + "", serialize + "");
//                    dbData.setFuben_id(pid);
//                    dbData.setWeight(weigh);
//                    Log.e("QQQ1", "onDestroy: "+dbData);
//                    mDbManager.insertBookshelfNovel(dbData);
//                } else {
                BookshelfNovelDbData dbData =mDbManager.selectBookshelfNovel(pid);
                if(dbData!=null) {
//                    BookshelfNovelDbData dbData = new BookshelfNovelDbData(mNovelUrl, mName,
//                            mCover, mChapterIndex, mPageView.getPosition(), mType, mNovelContent.length());
//                    dbData.setFuben_id(pid);
//                    dbData.setWeight(weigh);
//                    Log.e("QQQ2", "onDestroy: "+dbData);
                   // Log.e("QQQ", "onDestroy: "+mPageView.getPosition());
                    dbData.setPosition(mPageView.getPosition());
                    dbData.setSecondPosition(mNovelContent.length());
                    mDbManager.insertOrUpdateBook(dbData);
                }
//                }
            } else if (mType == 0) {
                BookshelfNovelDbData dbData =mDbManager.selectBookshelfNovel(pid);
                if(dbData!=null) {
//                    BookshelfNovelDbData dbData = new BookshelfNovelDbData(mNovelUrl, mName,
//                            mCover, mChapterIndex, mPageView.getPosition(), mType, mNovelContent.length());
//                    dbData.setFuben_id(pid);
//                    dbData.setWeight(weigh);
//                    Log.e("QQQ2", "onDestroy: "+dbData);
                    dbData.setPosition(mPageView.getPosition());
                    dbData.setChapterid(mChapterIndex + "");
                    dbData.setWeight(weigh);
                    mDbManager.insertOrUpdateBook(dbData);
                }
//                if (!mDbManager.isExistInBookshelfNovel(mNovelUrl + "")) {
//                    BookshelfNovelDbData dbData;
//                    dbData = new BookshelfNovelDbData(mNovelUrl + "", mName,
//                            mCover, mPageView.getPosition(), mType, mPageView.getSecondPos(), mChapterIndex + "", weigh, serialize + "");
//                    mDbManager.insertBookshelfNovel(dbData);
//                    Noval_Readcored noval_readcored = new Noval_Readcored(mNovelUrl + "", id + "", serialize + " ", mName, mAuthor, mCover, 0 + "", mTitle, weigh + "");
//                    mDbManager.insertReadCordeNovel(noval_readcored, mType + "");
//                    Log.e("QQQ3", "onDestroy: "+dbData);
//                } else {
//                    BookshelfNovelDbData dbData;
//                    dbData = new BookshelfNovelDbData(mNovelUrl + "", mName,
//                            mCover, mPageView.getPosition(), mType, mPageView.getSecondPos(), mChapterIndex + "", weigh);
//                    mDbManager.updataBookshelfNovel(dbData, pid);
//                    Noval_Readcored noval_readcored = new Noval_Readcored(mNovelUrl + "", id + "", mName, mCover, 0 + "", mTitle, weigh + "");
//                    mDbManager.updataReadCordeNovel(noval_readcored, mType + "", mNovelUrl + "");
//                    Log.e("QQQ4", "onDestroy: "+dbData);
//                }
                if (login_admin != null) {
                    mPresenter.setReadRecord(login_admin.getToken(), mNovelUrl, id + "");
                    //mPresenter.setBookshelfadd(login_admin.getToken(), mNovelUrl);
                }
            } else if (mType == 2) {
//                BookshelfNovelDbData dbData = new BookshelfNovelDbData(mNovelUrl, mName,
//                        mCover, mChapterIndex, mPageView.getFirstPos(), mType, mPageView.getSecondPos());
//                mDbManager.insertBookshelfNovel(dbData);
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
        SpUtil.saveTurnType(mTurnType);
        SpUtil.saveIsNightMode(mIsNightMode);
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
    int t=0;
    /**
     * 获取具体章节信息成功
     */
    @Override
    public void getDetailedChapterDataSuccess(DetailedChapterData data) {
        t++;
        if(t%3==0){
            post_adm();
        }
        mIsLoadingChapter = false;
        if (data == null) {
            mStateTv.setText("获取不到相关数据，请查看其他章节");
            return;
        }
        id = data.getId();
        weigh=data.getMax_num();
       // Log.e("WWW", "getDetailedChapterDataSuccess: "+weigh);
        mNovelContent = data.getContent();
        weight = data.getWeigh();
        mTitle = data.getTitle();
        mChapterIndex = Integer.parseInt(weight);
        webContent = Html.fromHtml(data.getContent().replace("&nbsp"," ")).toString();//data.getContent().replace("&nbsp"," ").replace("</br>","\n");
        webName = data.getTitle();
        mStateTv.setVisibility(View.GONE);
        if (mCatalog == 1) { //
            mPageView.initDrawText(webContent, mCatalog_posotion);
        } else {
            mPageView.initDrawText(webContent, mPosition);
        }
        mNovelTitleTv.setText(mName+" / "+data.getTitle());
        mNovelTitleTv1.setText(mChapterIndex+"/"+weigh);
        updateChapterProgress();
        if(is_autoRead){
            starttime();
        }
    }

    /**
     * 获取具体章节信息失败
     */
    @Override
    public void getDetailedChapterDataError(String errorMsg) {
        mIsLoadingChapter = false;
        mStateTv.setText("获取失败，请检查网络后重新加载");
    }
    List<Chapter> chapter = new ArrayList<>();
    /**
     * 加载本地 txt 成功
     */
    @Override
    public void loadTxtSuccess(String text) {
        mNovelContent = text;
        mStateTv.setVisibility(View.GONE);
        mPageView.initDrawText(text, mPosition);
        if (mCatalog == 1&&mCatalog_posotion<=text.length()&&mCatalog_posotion>0) {
            mPageView.initDrawText(text, mCatalog_posotion);
        }else if(mCatalog == 1&&mCatalog_posotion<=0) {
            showShortToast("已经到开头了");
            return;
        }else if(mCatalog_posotion>text.length()) {
            showShortToast("已经到最后了");
            return;
        }
        IParagraphData paragraphData = new ParagraphData();
        ReadData(adress, paragraphData, chapter);
        int o=0;
        for (int j = 0; j < longs.size(); j++) {
            if (mPageView.getPosition() < (int) longs.get(0)) {
                o = 0;
                break;
            } else if (mPageView.getPosition() < (int) longs.get(j)) {
                o = j - 1;
                break;
            }
        }
        mNovelTitleTv.setText(mName+" / "+mChapterNameList.get(o));
        updateChapterProgress();
    }
    private static final String ChapterPatternStr = "(^.{0,3}\\s*第)(.{1,9})[章节卷集部篇回](\\s*)";
    List longs = new ArrayList<>();
    int leng = 0;
    private Boolean ReadData(String filePath, IParagraphData paragraphData, List<Chapter> chapters) {
        File file = new File(filePath);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), getCharset(filePath)));
            try {
                String data;
                int index = 0;
                int chapterIndex = 0;
                while ((data = bufferedReader.readLine()) != null) {
                    if (data.length() >= 0) {
                        leng = leng + data.length();
                        Chapter chapter = compileChapter(data, paragraphData.getCharNum(), index, chapterIndex);
                        paragraphData.addParagraph(data);
                        if (chapter != null) {
                            chapterIndex++;
                            chapters.add(chapter);
                        }
                        index++;
                    }
//                        if (data.trim().startsWith("第") || data.contains("第")) {
//                            Pattern p = Pattern.compile(ChapterPatternStr);
//                            Matcher matcher = p.matcher(data);
//                            while (matcher.find()) {
//                                count++;
//                                String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";; //表示一个或多个@
//                                Pattern pat=Pattern.compile(regEx);
//                                Matcher mat=pat.matcher(data);
//                                String s=mat.replaceAll("");
//                                mChapterNameList.add(s);
//                            }
//                        }
//                        index++;
                    //   }
                }
                initChapterEndIndex(chapters, paragraphData.getParagraphNum());
                return true;
            } catch (IOException e) {

            }
        } catch (FileNotFoundException e) {

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    int count = 0;
    /* @param chapters
     * @param paragraphNum
     */
    private void initChapterEndIndex(List<Chapter> chapters, int paragraphNum) {
        if (chapters != null && chapters.size() > 0) {
            for (int i = 0, sum = chapters.size(); i < sum; i++) {
                int nextIndex = i + 1;
                IChapter chapter = chapters.get(i);
                if (nextIndex < sum) {
                    int startIndex = chapter.getStartParagraphIndex();
                    int endIndex = chapters.get(nextIndex).getEndParagraphIndex() - 1;
                    if (endIndex < startIndex) {
                        endIndex = startIndex;
                    }
                    chapter.setEndParagraphIndex(endIndex);
                } else {
                    int endIndex = paragraphNum - 1;
                    endIndex = endIndex < 0 ? 0 : endIndex;
                    chapter.setEndParagraphIndex(endIndex);
                }
            }
        }
    }
    private List<String> mChapterNameList = new ArrayList<>();
    /**
     * @param data              文本数据
     * @param chapterStartIndex 开始字符在全文中的位置
     * @param ParagraphIndex    段落位置
     * @param chapterIndex      章节位置
     * @return 没有识别到章节数据返回null
     */
    private Chapter compileChapter(String data, int chapterStartIndex, int ParagraphIndex, int chapterIndex) {
        if (data.trim().startsWith("第") || data.contains("第")) {
            Pattern p = Pattern.compile(ChapterPatternStr);
            Matcher matcher = p.matcher(data);
            while (matcher.find()) {
                longs.add(chapterStartIndex + ParagraphIndex);
                count++;
                String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                ; //表示一个或多个@
                Pattern pat = Pattern.compile(regEx);
                Matcher mat = pat.matcher(data);
                String s = mat.replaceAll("");
                mChapterNameList.add(count + ". " + s);
                int startIndex = 0;
                int endIndex = data.length();
                Chapter c = new Chapter(chapterStartIndex, chapterIndex, data, ParagraphIndex, ParagraphIndex, startIndex, endIndex);
                chapter.add(c);
                return c;
            }
        }
        return null;
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

    ProgressBar progressBar;
    List<Categorys_one> categorys_ones = new ArrayList<>();
    List<Text> other_website = new ArrayList<>();
    Gson mGson = new Gson();

    private void getCategorys(String id) {
        String url = UrlObtainer.GetUrl() + "api/index/hua_book";
        RequestBody requestBody = new FormBody.Builder()
                .add("novel_id", id)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
               // Log.e("QQQ", "onResponse: "+json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        if (jsonObject.isNull("data")) {
                            getCategorysError();
                        } else {
                            JSONObject object = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = object.getJSONArray("data");
                            categorys_ones.clear();
                            for (int z = 0; z < jsonArray.length(); z++) {
                                categorys_ones.add(mGson.fromJson(jsonArray.getJSONObject(z).toString(), Categorys_one.class));
                            }
                            getCategorysSuccess(categorys_ones);
                        }
                    } else {
                        getCategorysError();
                        //mPresenter.getDetailedChapterDataError("请求数据失败");
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

    int weigh;
    boolean is_othersite = false, is_all_one = false;
    ChangeCategoryAdapter changeCategoryAdapter;

    public void getCategorysSuccess(List<Categorys_one> categorys_one) {
        if(categorys_one.size()==0){
            tv_nodata.setVisibility(View.VISIBLE);
            rv_catalog_list.setVisibility(View.GONE);
        }else {
            tv_nodata.setVisibility(View.GONE);
        }
        this.categorys_ones = categorys_one;
        progressBar.setVisibility(View.GONE);
        changeCategoryAdapter = new ChangeCategoryAdapter(this, categorys_ones);
        changeCategoryAdapter.setOnHistoryAdapterListener(new ChangeCategoryAdapter.HistoryAdapterListener() {
            @Override
            public void clickWord(int word) {
                if (is_all_one == false) {
                    is_othersite = true;
                } else {
                    is_othersite = false;
                }
                tv_website.setText(categorys_ones.get(word).getUrl());
                other_website = categorys_ones.get(word).getText();
                String href = other_website.get(Integer.parseInt(weight) - 1).getChapter_url();
//                weigh = Integer.parseInt(categorys_ones.get(word).getChapter_sum());
//                mNovelTitleTv1.setText(mChapterIndex+"/"+weigh);
                reurl=categorys_ones.get(word).getDiv();
                new Thread(new sendValueToServer(href, weight + "",reurl)).start();
            }
        });
        rv_catalog_list.setAdapter(changeCategoryAdapter);
    }
    String reurl="";
    TextView tv_load;
    int d = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
               // if (mBrightnessBarCv.getVisibility() == View.VISIBLE) {
                    //hideBrightnessBar();
                    if(popupWindow.isShowing()){
                        popupWindow.dismiss();
                    }
               // }
            } else if (msg.what == 2) {
                if (d < weigh) {
                    new Thread(new LoadRunable(other_website.get(d - 1).getChapter_url())).start();
                    d++;
                    float pressent = (float) d / weigh * 100;
                    tv_load.setText("正在缓存：" + (int) pressent + "%");
                    if (d == weigh) {
                        tv_load.setVisibility(View.GONE);
                        handler.sendEmptyMessage(3);
                    }
                }
            } else if (msg.what == 3) {
//                if (mDbManager.isExistInBookshelfNovel(mNovelUrl) || mDbManager.isExistInBookshelfNovel(path + ".txt")) {
//                    //BookshelfNovelDbData bookshelfNovelDbData = new BookshelfNovelDbData(path + mName + ".txt", mName, mCover, 1, weigh, 1 + "");
//                    BookshelfNovelDbData bookshelfNovelDbData = new BookshelfNovelDbData(pid, mName, mCover, 1, weigh, 1 + "");
//                    bookshelfNovelDbData.setFuben_id(path + mName + ".txt");
//                    mDbManager.insertOrUpdateBook(bookshelfNovelDbData);
//                } else {
//                   // BookshelfNovelDbData bookshelfNovelDbData = new BookshelfNovelDbData(path + mName + ".txt", mName, mCover, 1, weigh, 1 + "");
                    BookshelfNovelDbData bookshelfNovelDbData = new BookshelfNovelDbData(pid, mName, mCover, 1, weigh, 1 + "");
                    bookshelfNovelDbData.setFuben_id(path + mName + ".txt");
                    bookshelfNovelDbData.setChapterid(mChapterIndex+"");
                    mDbManager.insertOrUpdateBook(bookshelfNovelDbData);
//                }
                //mNovelUrl = path + mName + ".txt";
                mType = 1;
                Intent intent_recever = new Intent("com.zhh.android");
                sendBroadcast(intent_recever);
                is_load=false;
            } else if (msg.what == 4) {
                int j = msg.arg1;
                if (30 * d <= weigh && (j + 1) == 30) {
                    d++;
                    postBooks_che();
                }
                float pressent = (float) (((d - 1) * 30 + (j + 1))) / (weigh) * 100;
                tv_load.setText("正在缓存:" + (int) pressent + "%");
                if (pressent >= 100) {
                    tv_load.setVisibility(View.GONE);
                    handler.sendEmptyMessage(3);
                }
            }else if(msg.what==5){
                //showPupowindpwTextStyle(mMenuIv);
            }
        }
    };

    void postBooks_che() {
        String url = UrlObtainer.GetUrl() + "api/index/Books_che";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", mNovelUrl)
                .add("page", d + "")
                .add("limit", 30 + "")
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
                            String load_title=title.replace("&nbsp"," ").replace("</br>","\n");
                            String load_content=content.replace("&nbsp"," ").replace("</br>","\n");
                            if(!load_title.contains("第")){
                                String s = Pattern.compile("[^0-9]").matcher(title).replaceAll("");
//                                String str =title;
//                                String regex = "\\d*";
//                                Pattern p = Pattern.compile(regex);
//                                Matcher m = p.matcher(str);
//                                while (m.find()) {
//                                    if (!"".equals(m.group()))
                                String title2 = "";
                                if(load_title.contains(s)) {
                                    title2=load_title.replace(s, "第" + s + "章 ");
                                    //Log.e("QQQ", "onResponse: " + s + " " + title2);
                                }
//                                }
                                addTxtToFileBuffered( title2+ "\n");
                                addTxtToFileBuffered( load_content+ "\n");
                            }else {
                                addTxtToFileBuffered( load_title+ "\n");
                                addTxtToFileBuffered( load_content+ "\n");
                            }
                            Message message = new Message();
                            message.what = 4;
                            message.arg1 = i;
                            handler.sendMessage(message);
                        }
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

    class sendValueToServer implements Runnable {

        String svrInfo;
        String reurl;
        String chapter_sum;

        public sendValueToServer(String href, String chapter_sum,String reurl) {
            this.svrInfo = href;
            this.reurl=reurl;
            this.chapter_sum = chapter_sum;
        }

        @Override

        public void run() {
            try {
                Analysisbiquge(svrInfo,reurl);
                handler.sendEmptyMessage(1);
                //loadTxtSuccess(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private void Analysisbiquge(String svrInfo,String div) throws IOException {
      //  Log.e("QQQ", "Analysisbiquge: "+div);
        Document doc = Jsoup.connect(svrInfo).get();
//        Elements typoElements = doc.body().select("div.content_read");
//        Elements select = doc.body().select("div.content_read").select("div.bookname");
        String title = doc.body().select("h1").text();
        Elements elements;
//        if(div==null||div.trim().equals("")) {
//           elements = doc.body().select("div#content");
//        }else {
            elements = doc.body().select(div);
//        }
        String content = "";
        for (Element link : elements) {
            content = content + link.text();
        }
       // Log.e("QQQ", "Analysisbiquge: "+title+" "+content);
        getDetailedChapterDataSuccess(new DetailedChapterData(pid,title, content, mChapterIndex + "", weigh));
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
//                Elements typoElements = doc.body().select("div.content_read");
//                Elements select = doc.body().select("div.content_read").select("div.bookname");
                String title = doc.body().select("h1").text();
                Elements elements = doc.body().select("div#content");
                String content = "";
                for (Element link : elements) {
                    content = content + link.text();
                }
                String load_title=title.replace("&nbsp"," ").replace("</br>","\n");
                String load_content=content.replace("&nbsp"," ").replace("</br>","\n");
                if(!load_title.contains("第")){
                    String s = Pattern.compile("[^0-9]").matcher(title).replaceAll("");
//                                String str =title;
//                                String regex = "\\d*";
//                                Pattern p = Pattern.compile(regex);
//                                Matcher m = p.matcher(str);
//                                while (m.find()) {
//                                    if (!"".equals(m.group()))
                    String title2 = "";
                    if(load_title.contains(s)) {
                        title2=load_title.replace(s, "第" + s + "章 ");
                        //Log.e("QQQ", "onResponse: " + s + " " + title2);
                    }
//                                }
                    addTxtToFileBuffered( title2+ "\n");
                    addTxtToFileBuffered( load_content+ "\n");
                }else {
                    addTxtToFileBuffered( load_title+ "\n");
                    addTxtToFileBuffered( load_content+ "\n");
                }
//                addTxtToFileBuffered(title + "\n");
//                addTxtToFileBuffered(content + "\n");
                //getDetailedChapterDataSuccess(new DetailedChapterData(title, content, "", mChapterIndex + ""));
                handler.sendEmptyMessage(2);
                //loadTxtSuccess(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void getCategorysError() {
        progressBar.setVisibility(View.GONE);
        tv_nodata.setVisibility(View.VISIBLE);
        rv_catalog_list.setVisibility(View.GONE);
        showShortToast("请求失败");
    }


    /**
     * 点击上一页/下一页后加载具体章节
     */
    private void showChapter() {
        if (mIsLoadingChapter) {    // 已经在加载了
            return;
        }
        if (mType == 0) {   // 显示网络小说
            if (mChapterIndex >= weigh) {
                showShortToast("当前显示为最后一章");
                return;
            } else if (mChapterIndex == 0) {
                showShortToast("当前显示为第一章");
                return;
            } else {
                if (is_othersite == true) {
                    new Thread(new sendValueToServer(other_website.get(mChapterIndex - 1).getChapter_url(), weight + "",reurl)).start();
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
        //backgroundAlpha(0.5f);
        if (mType == 0) {
            mIsShowBrightnessBar = true;
            Animation bottomAnim = AnimationUtils.loadAnimation(
                    this, R.anim.read_setting_bottom_enter);
            mBrightnessBarCv.startAnimation(bottomAnim);
            mBrightnessBarCv.setVisibility(View.VISIBLE);
//            getCategorys(mNovelUrl);
//            is_all_one = false;
//            s_line.setVisibility(View.GONE);
//            m_line.setVisibility(View.VISIBLE);
//            mBookMark.setTextColor(getResources().getColor(R.color.catalog_chapter_order_text));
//            tvCatalog.setTextColor(getResources().getColor(R.color.red));
//            if (changeCategoryAdapter != null) {
//                changeCategoryAdapter.setPosition(-1);
//                changeCategoryAdapter.notifyDataSetChanged();
//            }
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

    List<TextStyle> textStyles = new ArrayList<>();
    ;

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
                        progressBar.setVisibility(View.GONE);
                    }
                    handler.sendEmptyMessage(5);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                showShortToast(errorMsg);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    void initTextStyle(List<TextStyle> textStyles) {
        progressBar1.setVisibility(View.GONE);
        if(mStyle==null){
          return;
        }
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
                if (textStyles.get(position).getUrl() == null) {
                    mStyle = null;
                } else {
                    mStyle = textStyles.get(position).getUrl();
                }
                //Log.e("ZZZ", "clickItem: " + mStyle);
                mPageView.setmSype(mStyle);
                textStyleAdapter.notifyDataSetChanged();
            }
        });

    }

    public void listText() {
        for (int z = 0; z < textStyles.size(); z++) {
            if (z == 0) {
                textStyles.get(z).setLoad(true);
            } else {
                textStyles.get(z).setLoad(isLoad(textStyles.get(z), textStyles.get(z).getName()));
            }
        }
        initTextStyle(textStyles);
    }

    boolean isLoad(TextStyle style, String textStyle) {
        File file = new File(Constant.FONT_ADRESS + "/Font/");
        File[] subFile = file.listFiles();
        if(file.exists()&&subFile.length>0) {
            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!subFile[iFileLength].isDirectory()) {
                    String filename = subFile[iFileLength].getName().replace(".ttf", "");
                    if (filename.equals(textStyle)) {
                        style.setUrl(subFile[iFileLength].getPath());
                        return true;
                    }
                } else if (!subFile[iFileLength].exists()) {
                    return false;
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
                is_all_one = true;
                s_line.setVisibility(View.VISIBLE);
                m_line.setVisibility(View.GONE);
                mBookMark.setTextColor(getResources().getColor(R.color.red));
                tvCatalog.setTextColor(getResources().getColor(R.color.catalog_chapter_order_text));
                if (changeCategoryAdapter != null) {
                    changeCategoryAdapter.setPosition(mChapterIndex - 1);
                    changeCategoryAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.tv_mulu:
                is_all_one = false;
                s_line.setVisibility(View.GONE);
                m_line.setVisibility(View.VISIBLE);
                mBookMark.setTextColor(getResources().getColor(R.color.catalog_chapter_order_text));
                tvCatalog.setTextColor(getResources().getColor(R.color.red));
                if (changeCategoryAdapter != null) {
                    changeCategoryAdapter.setPosition(-1);
                    changeCategoryAdapter.notifyDataSetChanged();
                }
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
                    intent.putExtra("chapter_id",mChapterIndex);
                    startActivity(intent);
                } else if (mType == 1) {
                    // 跳转到目录页面，并且将自己的引用传递给它
                    Event<HoldReadActivityEvent> event = new Event<>(EventBusCode.CATALOG_HOLD_READ_ACTIVITY,
                            new HoldReadActivityEvent(ReadActivity.this));
                    EventBusUtil.sendStickyEvent(event);
                    Intent intent = new Intent(ReadActivity.this, LocalCatalogActivity.class);
                    intent.putExtra("file_path", adress);    // 传递当前小说的 url
                    intent.putExtra(LocalCatalogActivity.KEY_ID, mNovelUrl);    // 传递当前小说的 url
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
                //showTextstyle();
                showPupowindpwTextStyle(mNovelProgressTv);
                break;
            case R.id.iv_read_brightness:
            case R.id.tv_read_brightness:
                // 隐藏上下栏，并显示亮度栏
                hideBar();
               // showBrightnessBar();
                showPupowindpwChangeWebSite(mMenuIv);
                break;
            case R.id.iv_read_day_and_night_mode:
            case R.id.tv_read_day_and_night_mode:
                if (!mIsNightMode) {    // 进入夜间模式
                    nightMode();
                } else {    // 进入日间模式
                    dayMode();
                }
                hideBar();
                app.setNight(true);
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
                tv_jainju.setText((int)mRowSpace + "");
                break;
            case R.id.iv_read_increase_row_space:
                if (mRowSpace == mMaxRowSpace) {
                    break;
                }
                mRowSpace++;
                mPageView.setRowSpace(mRowSpace);
                tv_jainju.setText((int)mRowSpace + "");
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
        final String[] datas;
        if (mType == 1) {
            datas = new String[]{"已缓存", "添加书签"};
        } else {
            datas = new String[]{"全本缓存", "添加书签"};
        }
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
                        d = 1;
                        if (datas[position].equals("已缓存")) {
                            showShortToast("已经缓存");
                            break;
                        } else {
                            tv_load.setVisibility(View.VISIBLE);
                            if (is_othersite == true) {
                                new Thread(new LoadRunable(other_website.get(d - 1).getChapter_url())).start();
                            } else {
                                postBooks_che();
                            }
                            is_load=true;
                        }
                        break;
                    case 1:
                        if (mType == 1) {
                            boolean isflag=false;
                            List<BookmarkNovelDbData> bookmarkNovelDbData=mDbManager.queryAllBookmarkNovel(mNovelUrl);
                            //Log.e("QQQ", "2222: "+bookmarkNovelDbData.size());
                            for(int i=0;i<bookmarkNovelDbData.size();i++){
                               // Log.e("QQQ", "onItemClick: "+bookmarkNovelDbData.get(i).getPosition()+" "+mPageView.getPosition());
                                if(bookmarkNovelDbData.get(i).getPosition()==mPageView.getPosition()){
                                    //Log.e("QQQ", "onItemClick: "+111);
                                    isflag=true;
                                    break;
                                }
                            }
                            // String progress = mNovelProgressTv.getText().toString().substring(0, mNovelProgressTv.getText().length() - 1);
                            if(isflag==false) {
                                String progress = mNovelProgressTv.getText().toString().substring(0, mNovelProgressTv.getText().length() - 1);
                                Date t = new Date();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                BookmarkNovelDbData dbData = new BookmarkNovelDbData(mNovelUrl, mName,
                                        mNovelContent.substring(mPageView.getPosition(), mPageView.getPosition() + 23), Float.parseFloat(progress) / 100, mPageView.getPosition(), mType, df.format(t), mChapterIndex + "");
                                mDbManager.insertBookmarkNovel(dbData);
                                showShortToast("书签已添加");
                            }else {
                                showShortToast("此书签已存在");
                            }
                        } else if (mType == 2) {
                            BookshelfNovelDbData dbData = new BookshelfNovelDbData(mNovelUrl, mName,
                                    mCover, mChapterIndex, mPageView.getFirstPos(), mType, mPageView.getSecondPos());
                            mDbManager.insertBookshelfNovel(dbData);

                        } else if (mType == 0) {
                            boolean isflag = false;
                            List<BookmarkNovelDbData> bookmarkNovelDbData=mDbManager.queryAllBookmarkNovel(mNovelUrl);
                            //Log.e("QQQ", "2222: "+bookmarkNovelDbData.size());
                            for(int i=0;i<bookmarkNovelDbData.size();i++){
                               // Log.e("QQQ", "onItemClick: "+bookmarkNovelDbData.get(i).getPosition()+" "+mPageView.getPosition());
                               if(bookmarkNovelDbData.get(i).getPosition()==mPageView.getPosition()){
                                   //Log.e("QQQ", "onItemClick: "+111);
                                   isflag=true;
                                   break;
                               }
                            }
                           // String progress = mNovelProgressTv.getText().toString().substring(0, mNovelProgressTv.getText().length() - 1);
                            if(isflag==false) {
                                Date t = new Date();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                int seletposi = mPageView.getPosition() + 23 <= webContent.length() ? mPageView.getPosition() + 23 : webContent.length() - 1;
                                BookmarkNovelDbData dbData = new BookmarkNovelDbData(mNovelUrl, webName,
                                        webContent.substring(mPageView.getPosition(), seletposi), mChapterIndex, mPageView.getPosition(), mType, df.format(t), mChapterIndex + "");
                                mDbManager.insertBookmarkNovel(dbData);
                                showShortToast("书签已添加");
                            }else {
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
                backgroundAlpha(1f);
            }
        });

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
    boolean is_load;
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
        mDayAndNightModeIv.setImageResource(R.mipmap.icon_day);
        mDayAndNightModeTv.setText(getResources().getString(R.string.read_day_mode));
        // 设置相关颜色
        //mNovelTitleTv.setTextColor(getResources().getColor(R.color.read_night_mode_title));
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
        mDayAndNightModeIv.setImageResource(R.mipmap.icon_night);
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
            mDayAndNightModeIv.setImageResource(R.mipmap.icon_night);
            mDayAndNightModeTv.setText(getResources().getString(R.string.read_night_mode));
            mIsNightMode = false;
        }
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
        int bgColor = getResources().getColor(R.color.read_theme_0_bg);
        int textColor = getResources().getColor(R.color.read_theme_0_text);
        int backBgColor = getResources().getColor(R.color.read_theme_0_back_bg);
        int backTextColor = getResources().getColor(R.color.read_theme_0_back_text);
        switch (mTheme) {
            case 0:
                mTheme0.setSelected(true);
                mTheme0.setScaleY(1.2F);
                mTheme0.setScaleX(1.2F);
                bgColor = getResources().getColor(R.color.read_theme_0_bg);
                textColor = getResources().getColor(R.color.read_theme_0_text);
                backBgColor = getResources().getColor(R.color.read_theme_0_back_bg);
                backTextColor = getResources().getColor(R.color.read_theme_0_back_text);
                break;
            case 1:
                mTheme1.setSelected(true);
                mTheme1.setScaleY(1.2F);
                mTheme1.setScaleX(1.2F);
                bgColor = getResources().getColor(R.color.read_theme_1_bg);
                textColor = getResources().getColor(R.color.read_theme_1_text);
                backBgColor = getResources().getColor(R.color.read_theme_1_back_bg);
                backTextColor = getResources().getColor(R.color.read_theme_1_back_text);
                break;
            case 2:
                mTheme2.setSelected(true);
                mTheme2.setScaleY(1.2F);
                mTheme2.setScaleX(1.2F);
                bgColor = getResources().getColor(R.color.read_theme_2_bg);
                textColor = getResources().getColor(R.color.read_theme_2_text);
                backBgColor = getResources().getColor(R.color.read_theme_2_back_bg);
                backTextColor = getResources().getColor(R.color.read_theme_2_back_text);
                break;
            case 3:
                mTheme3.setSelected(true);
                mTheme3.setScaleY(1.2F);
                mTheme3.setScaleX(1.2F);
                bgColor = getResources().getColor(R.color.read_theme_3_bg);
                textColor = getResources().getColor(R.color.read_theme_3_text);
                backBgColor = getResources().getColor(R.color.read_theme_3_back_bg);
                backTextColor = getResources().getColor(R.color.read_theme_3_back_text);
                break;
            case 4:
                mTheme4.setSelected(true);
                mTheme4.setScaleY(1.2F);
                mTheme4.setScaleX(1.2F);
                bgColor = getResources().getColor(R.color.read_theme_4_bg);
                textColor = getResources().getColor(R.color.read_theme_4_text);
                backBgColor = getResources().getColor(R.color.read_theme_4_back_bg);
                backTextColor = getResources().getColor(R.color.read_theme_4_back_text);
                break;
        }
        // 设置相关颜色
        //mNovelTitleTv.setTextColor(textColor);
        mNovelProgressTv.setTextColor(textColor);
        mStateTv.setTextColor(textColor);
        mPageView.setTextColor(textColor);
        mPageView.setBgColor(bgColor);
        mPageView.setBackgroundColor(bgColor);
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
        Log.e("sss", "nextNet: "+weigh);
        if (mChapterIndex == weigh - 1) {
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
            if (weigh != 0) {
                progress = (int) (100 * ((float) mChapterIndex / (weigh)));
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(is_load){
            final TipDialog tipDialog = new TipDialog.Builder(ReadActivity.this)
                    .setContent("正在下载，是否退出？")
                    .setCancel("取消")
                    .setEnsure("确定")
                    .setOnClickListener(new TipDialog.OnClickListener() {
                        @Override
                        public void clickEnsure() {
                            finish();
                        }

                        @Override
                        public void clickCancel() {

                        }
                    })
                    .build();
            tipDialog.show();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
