package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bifan.txtreaderlib.R;
import com.bifan.txtreaderlib.bean.TxtChar;
import com.bifan.txtreaderlib.bean.TxtMsg;
import com.bifan.txtreaderlib.interfaces.ICenterAreaClickListener;
import com.bifan.txtreaderlib.interfaces.IChapter;
import com.bifan.txtreaderlib.interfaces.ILoadListener;
import com.bifan.txtreaderlib.interfaces.IPageChangeListener;
import com.bifan.txtreaderlib.interfaces.ISliderListener;
import com.bifan.txtreaderlib.interfaces.ITextSelectListener;
import com.bifan.txtreaderlib.main.TxtConfig;
import com.bifan.txtreaderlib.main.TxtReaderView;
import com.bifan.txtreaderlib.ui.ChapterList;
import com.bifan.txtreaderlib.utils.ELogger;
import com.example.administrator.xiaoshuoyuedushenqi.app.App;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IReadContract;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DetailedChapterData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.epub.EpubData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.epub.EpubTocItem;
import com.example.administrator.xiaoshuoyuedushenqi.entity.epub.OpfData;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.ReadPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.ScreenUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bifan-wei
 * on 2017/12/8.
 */

public class TxtPlayActivity extends BaseActivity<ReadPresenter>
        implements IReadContract.View, View.OnClickListener {
    protected Handler mHandler;
    protected boolean FileExist = false;
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
    String mAuthor = "";
    String adress = "";
    App app;
    String pid;
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
    private String mStyle = "";         // 字体样式
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
    int weigh;
    boolean is_othersite = false, is_all_one = false;

    //    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(getContentViewLayout());
//        FileExist = getIntentData();
//        init();
//        loadFile();
//        registerListener();
//    }
    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hwtxtpaly;
    }

    @Override
    protected ReadPresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        app = (App) getApplication();
        // 从前一个活动传来
        mAuthor = getIntent().getStringExtra(KEY_AUTHOR);
        adress = getIntent().getStringExtra(KEY_NOVEL_URL_FUBEN);
        chpter_id = getIntent().getIntExtra(KEY_CHPATER_ID, 1);
        zj_id = getIntent().getStringExtra(KEY_ZJ_ID);
        first_read = getIntent().getIntExtra("first_read", 0);
        mNovelUrl = getIntent().getStringExtra(KEY_NOVEL_URL);
        pid = mNovelUrl;
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
        if (weigh == 0) {
            BookshelfNovelDbData bookshelfNovelDbData = mDbManager.selectBookshelfNovel(mNovelUrl);
            if (bookshelfNovelDbData != null) {
                weigh = bookshelfNovelDbData.getWeight();
            }
        }
    }

    @Override
    protected void initView() {
        FileExist = getIntentData();
        init();
    }

    @Override
    protected void doAfterInit() {
//
//        loadFile();
//        registerListener();
//        // Log.e("QQQ", "doAfterInit: "+mNovelUrl+" "+chpter_id);
//        if (mType == 0) {
//            // 先通过小说 url 获取所有章节 url 信息
//            //mPresenter.getChapterList(mid);
//            if (first_read == 0) {
//                mPresenter.getDetailedChapterData(zj_id + "");
//            } else if (first_read == 1) {
//                mPresenter.getDetailedChapterData(mNovelUrl + "", 1 + "");
//            } else if (first_read == 2) {
//                mPresenter.getDetailedChapterData(mNovelUrl + "", chpter_id + "");
//            }
//        } else if (mType == 1) {
//            // 通过 FilePath 读取本地小说
//            //Log.e("QQQ", "doAfterInit: "+adress);
//            mPresenter.loadTxt(adress);
//        } else if (mType == 2) {
//            // 先根据 filePath 获得 OpfData
//            mPresenter.getOpfData(mNovelUrl);
//        }
//        if (mBrightness == -1f) {    // 系统亮度
//            //mSystemBrightnessSw.setChecked(true);
//            //mSys_light.setBackground(getResources().getDrawable(R.drawable.bachground_red));
//            sys_select.setImageResource(com.example.administrator.xiaoshuoyuedushenqi.R.mipmap.sys_selected);
//        } else {    // 自定义亮度
//            mBrightnessProcessSb.setProgress((int) (100 * mBrightness));
//            //mSystemBrightnessSw.setChecked(false);
//            //mSys_light.setBackground(getResources().getDrawable(R.drawable.bachground_cricyle));
//            sys_select.setImageResource(com.example.administrator.xiaoshuoyuedushenqi.R.mipmap.sys_select);
//            ScreenUtil.setWindowBrightness(this, mBrightness);
//        }
//        // Log.e("QQQ", "initData: "+mIsNightMode);
//        if (mIsNightMode == true) { // 夜间模式
//            nightMode();
//        } else {    // 日间模式
//            dayMode();
//        }
//        tv_textsize.setText((int) mTextSize + "");
//        //File file = new File(mStyle);
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
//        getContentResolver().registerContentObserver(
//                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
//                true,
//                mBrightnessObserver);
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    protected boolean getIntentData() {
        // Get the intent that started this activity
        Uri uri = getIntent().getData();
        if (uri != null) {
            ELogger.log("getIntentData", "" + uri);
        } else {
            ELogger.log("getIntentData", "uri is null");
        }
        if (uri != null) {
            try {
                String path = getRealPathFromUri(uri);
                if (!TextUtils.isEmpty(path)) {
                    if (path.contains("/storage/")) {
                        path = path.substring(path.indexOf("/storage/"));
                    }
                    ELogger.log("getIntentData", "path:" + path);
                    File file = new File(path);
                    if (file.exists()) {
                        FilePath = path;
                        FileName = file.getName();
                        return true;
                    } else {
                        toast("文件不存在");
                        return false;
                    }
                }
                return false;
            } catch (Exception e) {
                toast("文件出错了");
            }
        }

        FilePath = getIntent().getStringExtra("FilePath");
        FileName = getIntent().getStringExtra("FileName");
        ContentStr = getIntent().getStringExtra("ContentStr");
        if (ContentStr == null) {
            return FilePath != null && new File(FilePath).exists();
        } else {
            return true;
        }

    }

    private String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] pro = {MediaStore.Files.FileColumns.DATA};
            cursor = getContentResolver().query(contentUri, pro, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * @param context  上下文
     * @param FilePath 文本文件路径
     */
    public static void loadTxtFile(Context context, String FilePath) {
        loadTxtFile(context, FilePath, null);
    }

    /**
     * @param context 上下文
     * @param str     文本文内容
     */
    public static void loadStr(Context context, String str) {
        loadTxtStr(context, str, null);
    }

    /**
     * @param context  上下文
     * @param str      文本显示内容
     * @param FileName 显示的书籍或者文件名称
     */
    public static void loadTxtStr(Context context, String str, String FileName) {
        Intent intent = new Intent();
        intent.putExtra("ContentStr", str);
        intent.putExtra("FileName", FileName);
        intent.setClass(context, TxtPlayActivity.class);
        context.startActivity(intent);
    }

    /**
     * @param context  上下文
     * @param FilePath 文本文件路径
     * @param FileName 显示的书籍或者文件名称
     */
    public static void loadTxtFile(Context context, String FilePath, String FileName) {
        Intent intent = new Intent();
        intent.putExtra("FilePath", FilePath);
        intent.putExtra("FileName", FileName);
        intent.setClass(context, TxtPlayActivity.class);
        context.startActivity(intent);
    }

    protected View mTopDecoration, mBottomDecoration;
    protected View mChapterMsgView;
    protected TextView mChapterMsgName;
    protected TextView mChapterMsgProgress;
    protected TextView mChapterNameText;
    protected TextView mChapterMenuText;
    protected TextView mProgressText;
    protected TextView mSettingText;
    protected TextView mSelectedText;
    protected TxtReaderView mTxtReaderView;
    protected View mTopMenu;
    protected View mBottomMenu;
    protected View mCoverView;
    protected View ClipboardView;
    protected String CurrentSelectedText;

    protected ChapterList mChapterListPop;
    protected MenuHolder mMenuHolder = new MenuHolder();

    protected void init() {
        mHandler = new Handler();
        mChapterMsgView = findViewById(R.id.activity_hwtxtplay_chapter_msg);
        mChapterMsgName = (TextView) findViewById(R.id.chapter_name);
        mChapterMsgProgress = (TextView) findViewById(R.id.charpter_progress);
        mTopDecoration = findViewById(R.id.activity_hwtxtplay_top);
        mBottomDecoration = findViewById(R.id.activity_hwtxtplay_bottom);
        mTxtReaderView = (TxtReaderView) findViewById(R.id.activity_hwtxtplay_readerView);
        mChapterNameText = (TextView) findViewById(R.id.activity_hwtxtplay_chaptername);
        mChapterMenuText = (TextView) findViewById(R.id.activity_hwtxtplay_chapter_menutext);
        mProgressText = (TextView) findViewById(R.id.activity_hwtxtplay_progress_text);
        mSettingText = (TextView) findViewById(R.id.activity_hwtxtplay_setting_text);
        mTopMenu = findViewById(R.id.activity_hwtxtplay_menu_top);
        mBottomMenu = findViewById(R.id.activity_hwtxtplay_menu_bottom);
        mCoverView = findViewById(R.id.activity_hwtxtplay_cover);
        ClipboardView = findViewById(R.id.activity_hwtxtplay_Clipboar);
        mSelectedText = (TextView) findViewById(R.id.activity_hwtxtplay_selected_text);

        mMenuHolder.mTitle = (TextView) findViewById(R.id.txtreadr_menu_title);
        mMenuHolder.mPreChapter = (TextView) findViewById(R.id.txtreadr_menu_chapter_pre);
        mMenuHolder.mNextChapter = (TextView) findViewById(R.id.txtreadr_menu_chapter_next);
        mMenuHolder.mSeekBar = (SeekBar) findViewById(R.id.txtreadr_menu_seekbar);
        mMenuHolder.mTextSizeDel = findViewById(R.id.txtreadr_menu_textsize_del);
        mMenuHolder.mTextSize = (TextView) findViewById(R.id.txtreadr_menu_textsize);
        mMenuHolder.mTextSizeAdd = findViewById(R.id.txtreadr_menu_textsize_add);
        mMenuHolder.mBoldSelectedLayout = findViewById(R.id.txtreadr_menu_textsetting1_bold);
        mMenuHolder.mNormalSelectedLayout = findViewById(R.id.txtreadr_menu_textsetting1_normal);
        mMenuHolder.mCoverSelectedLayout = findViewById(R.id.txtreadr_menu_textsetting2_cover);
        mMenuHolder.mShearSelectedLayout = findViewById(R.id.txtreadr_menu_textsetting2_shear);
        mMenuHolder.mTranslateSelectedLayout = findViewById(R.id.txtreadr_menu_textsetting2_translate);

        mMenuHolder.mStyle1 = findViewById(R.id.hwtxtreader_menu_style1);
        mMenuHolder.mStyle2 = findViewById(R.id.hwtxtreader_menu_style2);
        mMenuHolder.mStyle3 = findViewById(R.id.hwtxtreader_menu_style3);
        mMenuHolder.mStyle4 = findViewById(R.id.hwtxtreader_menu_style4);
        mMenuHolder.mStyle5 = findViewById(R.id.hwtxtreader_menu_style5);
    }

    private final int[] StyleTextColors = new int[]{
            Color.parseColor("#4a453a"),
            Color.parseColor("#505550"),
            Color.parseColor("#453e33"),
            Color.parseColor("#8f8e88"),
            Color.parseColor("#27576c")
    };

    protected String ContentStr = null;
    protected String FilePath = null;
    protected String FileName = null;

    protected void loadFile() {
        TxtConfig.savePageSwitchDuration(this, 400);
        if (ContentStr == null) {
            if (TextUtils.isEmpty(FilePath) || !(new File(FilePath).exists())) {
                toast("文件不存在");
                return;
            }

        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //延迟加载避免闪一下的情况出现
                if (ContentStr == null) {
                    loadOurFile();
                } else {
                    loadStr();
                }
            }
        }, 300);


    }

    /**
     *
     */
    protected void loadOurFile() {
        mTxtReaderView.loadTxtFile(FilePath, new ILoadListener() {
            @Override
            public void onSuccess() {
                if (!hasExisted) {
                    onLoadDataSuccess();
                }
            }

            @Override
            public void onFail(final TxtMsg txtMsg) {
                if (!hasExisted) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onLoadDataFail(txtMsg);
                        }
                    });
                }

            }

            @Override
            public void onMessage(String message) {
                //加载过程信息
            }
        });
    }

    /**
     * @param txtMsg txtMsg
     */
    protected void onLoadDataFail(TxtMsg txtMsg) {
        //加载失败信息
        toast(txtMsg + "");
    }

    /**
     *
     */
    protected void onLoadDataSuccess() {
        if (TextUtils.isEmpty(FileName)) {//没有显示的名称，获取文件名显示
            FileName = mTxtReaderView.getTxtReaderContext().getFileMsg().FileName;
        }
        setBookName(FileName);
        initWhenLoadDone();
    }

    private void loadStr() {
        String testText = ContentStr;
        mTxtReaderView.loadText(testText, new ILoadListener() {
            @Override
            public void onSuccess() {
                setBookName("test with str");
                initWhenLoadDone();
            }

            @Override
            public void onFail(TxtMsg txtMsg) {
                //加载失败信息
                toast(txtMsg + "");
            }

            @Override
            public void onMessage(String message) {
                //加载过程信息
            }
        });
    }

    protected void initWhenLoadDone() {
        if (mTxtReaderView.getTxtReaderContext().getFileMsg() != null) {
            FileName = mTxtReaderView.getTxtReaderContext().getFileMsg().FileName;
        }
        mMenuHolder.mTextSize.setText(mTxtReaderView.getTextSize() + "");
        mTopDecoration.setBackgroundColor(mTxtReaderView.getBackgroundColor());
        mBottomDecoration.setBackgroundColor(mTxtReaderView.getBackgroundColor());
        //mTxtReaderView.setLeftSlider(new MuiLeftSlider());//修改左滑动条
        //mTxtReaderView.setRightSlider(new MuiRightSlider());//修改右滑动条
        //字体初始化
        onTextSettingUi(mTxtReaderView.getTxtReaderContext().getTxtConfig().Bold);
        //翻页初始化
        onPageSwitchSettingUi(mTxtReaderView.getTxtReaderContext().getTxtConfig().Page_Switch_Mode);
        //保存的翻页模式
        int pageSwitchMode = mTxtReaderView.getTxtReaderContext().getTxtConfig().Page_Switch_Mode;
        if (pageSwitchMode == TxtConfig.PAGE_SWITCH_MODE_SERIAL) {
            mTxtReaderView.setPageSwitchByTranslate();
        } else if (pageSwitchMode == TxtConfig.PAGE_SWITCH_MODE_COVER) {
            mTxtReaderView.setPageSwitchByCover();
        } else if (pageSwitchMode == TxtConfig.PAGE_SWITCH_MODE_SHEAR) {
            mTxtReaderView.setPageSwitchByShear();
        }
        //章节初始化
        if (mTxtReaderView.getChapters() != null && mTxtReaderView.getChapters().size() > 0) {
            WindowManager m = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            m.getDefaultDisplay().getMetrics(metrics);
            int ViewHeight = metrics.heightPixels - mTopDecoration.getHeight();
            mChapterListPop = new ChapterList(this, ViewHeight, mTxtReaderView.getChapters(), mTxtReaderView.getTxtReaderContext().getParagraphData().getCharNum());
            mChapterListPop.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    IChapter chapter = (IChapter) mChapterListPop.getAdapter().getItem(i);
                    mChapterListPop.dismiss();
                    mTxtReaderView.loadFromProgress(chapter.getStartParagraphIndex(), 0);
                }
            });
            mChapterListPop.setBackGroundColor(mTxtReaderView.getBackgroundColor());
        } else {
            Gone(mChapterMenuText);
        }
    }

    protected void registerListener() {
        mSettingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Show(mTopMenu, mBottomMenu, mCoverView);
            }
        });
        setMenuListener();
        setSeekBarListener();
        setCenterClickListener();
        setPageChangeListener();
        setOnTextSelectListener();
        setStyleChangeListener();
        setExtraListener();
    }

    private void setExtraListener() {
        mMenuHolder.mPreChapter.setOnClickListener(new ChapterChangeClickListener(true));
        mMenuHolder.mNextChapter.setOnClickListener(new ChapterChangeClickListener(false));
        mMenuHolder.mTextSizeAdd.setOnClickListener(new TextChangeClickListener(true));
        mMenuHolder.mTextSizeDel.setOnClickListener(new TextChangeClickListener(false));
        mMenuHolder.mBoldSelectedLayout.setOnClickListener(new TextSettingClickListener(true));
        mMenuHolder.mNormalSelectedLayout.setOnClickListener(new TextSettingClickListener(false));
        mMenuHolder.mTranslateSelectedLayout.setOnClickListener(new SwitchSettingClickListener(TxtConfig.PAGE_SWITCH_MODE_SERIAL));
        mMenuHolder.mCoverSelectedLayout.setOnClickListener(new SwitchSettingClickListener(TxtConfig.PAGE_SWITCH_MODE_COVER));
        mMenuHolder.mShearSelectedLayout.setOnClickListener(new SwitchSettingClickListener(TxtConfig.PAGE_SWITCH_MODE_SHEAR));
    }

    protected void setStyleChangeListener() {
        mMenuHolder.mStyle1.setOnClickListener(new StyleChangeClickListener(ContextCompat.getColor(this, R.color.hwtxtreader_styleclor1), StyleTextColors[0]));
        mMenuHolder.mStyle2.setOnClickListener(new StyleChangeClickListener(ContextCompat.getColor(this, R.color.hwtxtreader_styleclor2), StyleTextColors[1]));
        mMenuHolder.mStyle3.setOnClickListener(new StyleChangeClickListener(ContextCompat.getColor(this, R.color.hwtxtreader_styleclor3), StyleTextColors[2]));
        mMenuHolder.mStyle4.setOnClickListener(new StyleChangeClickListener(ContextCompat.getColor(this, R.color.hwtxtreader_styleclor4), StyleTextColors[3]));
        mMenuHolder.mStyle5.setOnClickListener(new StyleChangeClickListener(ContextCompat.getColor(this, R.color.hwtxtreader_styleclor5), StyleTextColors[4]));
    }

    protected void setOnTextSelectListener() {
        mTxtReaderView.setOnTextSelectListener(new ITextSelectListener() {
            @Override
            public void onTextChanging(TxtChar firstSelectedChar, TxtChar lastSelectedChar) {
                //firstSelectedChar.Top
                //  firstSelectedChar.Bottom
                // 这里可以根据 firstSelectedChar与lastSelectedChar的top与bottom的位置
                //计算显示你要显示的弹窗位置，如果需要的话
            }

            @Override
            public void onTextChanging(String selectText) {
                onCurrentSelectedText(selectText);
            }

            @Override
            public void onTextSelected(String selectText) {
                onCurrentSelectedText(selectText);
            }
        });

        mTxtReaderView.setOnSliderListener(new ISliderListener() {
            @Override
            public void onShowSlider(TxtChar txtChar) {
                //TxtChar 为当前长按选中的字符
                // 这里可以根据 txtChar的top与bottom的位置
                //计算显示你要显示的弹窗位置，如果需要的话
            }

            @Override
            public void onShowSlider(String currentSelectedText) {
                onCurrentSelectedText(currentSelectedText);
                Show(ClipboardView);
            }

            @Override
            public void onReleaseSlider() {
                Gone(ClipboardView);
            }
        });

    }

    protected void setPageChangeListener() {
        mTxtReaderView.setPageChangeListener(new IPageChangeListener() {
            @Override
            public void onCurrentPage(float progress) {
                int p = (int) (progress * 1000);
                mProgressText.setText(((float) p / 10) + "%");
                mMenuHolder.mSeekBar.setProgress((int) (progress * 100));
                IChapter currentChapter = mTxtReaderView.getCurrentChapter();
                if (currentChapter != null) {
                    mChapterNameText.setText((currentChapter.getTitle() + "").trim());
                } else {
                    mChapterNameText.setText("无章节");
                }
            }
        });
    }

    protected void setCenterClickListener() {
        mTxtReaderView.setOnCenterAreaClickListener(new ICenterAreaClickListener() {
            @Override
            public boolean onCenterClick(float widthPercentInView) {
                mSettingText.performClick();
                return true;
            }

            @Override
            public boolean onOutSideCenterClick(float widthPercentInView) {
                if (mBottomMenu.getVisibility() == View.VISIBLE) {
                    mSettingText.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    protected void setMenuListener() {
        mTopMenu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        mBottomMenu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        mCoverView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Gone(mTopMenu, mBottomMenu, mCoverView, mChapterMsgView);
                return true;
            }
        });
        mChapterMenuText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChapterListPop != null) {
                    if (!mChapterListPop.isShowing()) {
                        mChapterListPop.showAsDropDown(mTopDecoration);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                IChapter currentChapter = mTxtReaderView.getCurrentChapter();
                                if (currentChapter != null) {
                                    mChapterListPop.setCurrentIndex(currentChapter.getIndex());
                                    mChapterListPop.notifyDataSetChanged();
                                }
                            }
                        }, 300);
                    } else {
                        mChapterListPop.dismiss();
                    }
                }
            }
        });
        mTopMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChapterListPop.isShowing()) {
                    mChapterListPop.dismiss();
                }
            }
        });
    }

    protected void setSeekBarListener() {

        mMenuHolder.mSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mTxtReaderView.loadFromProgress(mMenuHolder.mSeekBar.getProgress());
                    Gone(mChapterMsgView);
                }
                return false;
            }
        });
        mMenuHolder.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                if (fromUser) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onShowChapterMsg(progress);
                        }
                    });
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Gone(mChapterMsgView);
            }
        });

    }


    private void onShowChapterMsg(int progress) {
        if (mTxtReaderView != null && mChapterListPop != null) {
            IChapter chapter = mTxtReaderView.getChapterFromProgress(progress);
            if (chapter != null) {
                float p = (float) chapter.getStartIndex() / (float) mChapterListPop.getAllCharNum();
                if (p > 1) {
                    p = 1;
                }
                Show(mChapterMsgView);
                mChapterMsgName.setText(chapter.getTitle());
                mChapterMsgProgress.setText((int) (p * 100) + "%");
            }
        }
    }

    private void onCurrentSelectedText(String SelectedText) {
        mSelectedText.setText("选中" + (SelectedText + "").length() + "个文字");
        CurrentSelectedText = SelectedText;
    }

    private void onTextSettingUi(Boolean isBold) {
        if (isBold) {
            mMenuHolder.mBoldSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_selected);
            mMenuHolder.mNormalSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
        } else {
            mMenuHolder.mBoldSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
            mMenuHolder.mNormalSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_selected);
        }
    }

    private void onPageSwitchSettingUi(int pageSwitchMode) {
        if (pageSwitchMode == TxtConfig.PAGE_SWITCH_MODE_SERIAL) {
            mMenuHolder.mTranslateSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_selected);
            mMenuHolder.mCoverSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
            mMenuHolder.mShearSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
        } else if (pageSwitchMode == TxtConfig.PAGE_SWITCH_MODE_COVER) {
            mMenuHolder.mTranslateSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
            mMenuHolder.mCoverSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_selected);
            mMenuHolder.mShearSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
        } else if (pageSwitchMode == TxtConfig.PAGE_SWITCH_MODE_SHEAR) {
            mMenuHolder.mTranslateSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
            mMenuHolder.mCoverSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
            mMenuHolder.mShearSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_selected);
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void getChapterUrlListSuccess(List<String> chapterUrlList, List<String> chapterNameList) {

    }

    @Override
    public void getChapterUrlListError(String errorMsg) {

    }

    @Override
    public void getDetailedChapterDataSuccess(DetailedChapterData data) {

    }

    @Override
    public void getDetailedChapterDataError(String errorMsg) {

    }

    @Override
    public void loadTxtSuccess(String text) {

    }

    @Override
    public void loadTxtError(String errorMsg) {

    }

    @Override
    public void getOpfDataSuccess(OpfData opfData) {

    }

    @Override
    public void getOpfDataError(String errorMsg) {

    }

    @Override
    public void getEpubChapterDataSuccess(List<EpubData> dataList) {

    }

    @Override
    public void getEpubChapterDataError(String errorMsg) {

    }

    @Override
    public void getReadRecordSuccess(String message) {

    }

    @Override
    public void getReadRecordError(String errorMsg) {

    }

    private class TextSettingClickListener implements View.OnClickListener {
        private Boolean Bold;

        public TextSettingClickListener(Boolean bold) {
            Bold = bold;
        }

        @Override
        public void onClick(View view) {
            if (FileExist) {
                mTxtReaderView.setTextBold(Bold);
                onTextSettingUi(Bold);
            }
        }
    }

    private class SwitchSettingClickListener implements View.OnClickListener {
        private int pageSwitchMode;

        public SwitchSettingClickListener(int pageSwitchMode) {
            this.pageSwitchMode = pageSwitchMode;
        }

        @Override
        public void onClick(View view) {
            if (FileExist) {
                if (pageSwitchMode == TxtConfig.PAGE_SWITCH_MODE_COVER) {
                    mTxtReaderView.setPageSwitchByCover();
                } else if (pageSwitchMode == TxtConfig.PAGE_SWITCH_MODE_SERIAL) {
                    mTxtReaderView.setPageSwitchByTranslate();
                }
                if (pageSwitchMode == TxtConfig.PAGE_SWITCH_MODE_SHEAR) {
                    mTxtReaderView.setPageSwitchByShear();
                }
                onPageSwitchSettingUi(pageSwitchMode);
            }
        }
    }


    private class ChapterChangeClickListener implements View.OnClickListener {
        private Boolean Pre;

        public ChapterChangeClickListener(Boolean pre) {
            Pre = pre;
        }

        @Override
        public void onClick(View view) {
            if (Pre) {
                mTxtReaderView.jumpToPreChapter();
            } else {
                mTxtReaderView.jumpToNextChapter();
            }
        }
    }

    private class TextChangeClickListener implements View.OnClickListener {
        private Boolean Add;

        public TextChangeClickListener(Boolean pre) {
            Add = pre;
        }

        @Override
        public void onClick(View view) {
            if (FileExist) {
                int textSize = mTxtReaderView.getTextSize();
                if (Add) {
                    if (textSize + 2 <= TxtConfig.MAX_TEXT_SIZE) {
                        mTxtReaderView.setTextSize(textSize + 2);
                        mMenuHolder.mTextSize.setText(textSize + 2 + "");
                    }
                } else {
                    if (textSize - 2 >= TxtConfig.MIN_TEXT_SIZE) {
                        mTxtReaderView.setTextSize(textSize - 2);
                        mMenuHolder.mTextSize.setText(textSize - 2 + "");
                    }
                }
            }
        }
    }

    private class StyleChangeClickListener implements View.OnClickListener {
        private int BgColor;
        private int TextColor;

        public StyleChangeClickListener(int bgColor, int textColor) {
            BgColor = bgColor;
            TextColor = textColor;
        }

        @Override
        public void onClick(View view) {
            if (FileExist) {
                mTxtReaderView.setStyle(BgColor, TextColor);
                mTopDecoration.setBackgroundColor(BgColor);
                mBottomDecoration.setBackgroundColor(BgColor);
                if (mChapterListPop != null) {
                    mChapterListPop.setBackGroundColor(BgColor);
                }
            }
        }
    }

    protected void setBookName(String name) {
        mMenuHolder.mTitle.setText(name + "");
    }

    protected void Show(View... views) {
        for (View v : views) {
            v.setVisibility(View.VISIBLE);
        }
    }

    protected void Gone(View... views) {
        for (View v : views) {
            v.setVisibility(View.GONE);
        }
    }


    private Toast t;

    protected void toast(final String msg) {
        if (t != null) {
            t.cancel();
        }
        t = Toast.makeText(TxtPlayActivity.this, msg, Toast.LENGTH_SHORT);
        t.show();
    }

    protected class MenuHolder {
        public TextView mTitle;
        public TextView mPreChapter;
        public TextView mNextChapter;
        public SeekBar mSeekBar;
        public View mTextSizeDel;
        public View mTextSizeAdd;
        public TextView mTextSize;
        public View mBoldSelectedLayout;
        public View mNormalSelectedLayout;
        public View mCoverSelectedLayout;
        public View mShearSelectedLayout;
        public View mTranslateSelectedLayout;
        public View mStyle1;
        public View mStyle2;
        public View mStyle3;
        public View mStyle4;
        public View mStyle5;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exist();
    }

    public void BackClick(View view) {
        finish();
    }

    public void onCopyText(View view) {
        if (!TextUtils.isEmpty(CurrentSelectedText)) {
            toast("已经复制到粘贴板");
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(CurrentSelectedText + "");
        }
        onCurrentSelectedText("");
        mTxtReaderView.releaseSelectedState();
        Gone(ClipboardView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        exist();
    }

    protected boolean hasExisted = false;

    protected void exist() {
        if (!hasExisted) {
            ContentStr = null;
            hasExisted = true;
            if (mTxtReaderView != null) {
                mTxtReaderView.saveCurrentProgress();
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mTxtReaderView != null) {
                        mTxtReaderView.getTxtReaderContext().Clear();
                        mTxtReaderView = null;
                    }
                    if (mHandler != null) {
                        mHandler.removeCallbacksAndMessages(null);
                        mHandler = null;
                    }
                    if (mChapterListPop != null) {
                        if (mChapterListPop.isShowing()) {
                            mChapterListPop.dismiss();
                        }
                        mChapterListPop.onDestroy();
                        mChapterListPop = null;
                    }
                    mMenuHolder = null;
                }
            }, 300);

        }
    }
}
