package com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.page;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.app.App;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.util.AdsUtils;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.WYReadActivity;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.db.entity.BookRecordBean;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.db.entity.CollBookBean;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.db.helper.BookRecordHelper;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.IOUtils;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.ReadSettingManager;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.ScreenUtils;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.StringUtils;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.ToastUtils;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.utils.rxhelper.RxUtils;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.animation.ScrollPageAnim;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.animation.ScrollPageAnim2;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;


import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.example.administrator.xiaoshuoyuedushenqi.app.App.getAppResources;
import static com.example.administrator.xiaoshuoyuedushenqi.app.App.getContext;

/**
 * Created by newbiechen on 17-7-1.
 */

public abstract class PageLoader {
    private static final String TAG = "PageLoader";

    //当前页面的状态
    public static final int STATUS_LOADING = 1;  //正在加载
    public static final int STATUS_FINISH = 2;   //加载完成
    public static final int STATUS_ERROR = 3;    //加载错误 (一般是网络加载情况)
    public static final int STATUS_EMPTY = 4;    //空数据
    public static final int STATUS_PARSE = 5;    //正在解析 (一般用于本地数据加载)
    public static final int STATUS_PARSE_ERROR = 6; //本地文件解析错误(暂未被使用)

    static final int DEFAULT_MARGIN_HEIGHT = 28;
    static final int DEFAULT_MARGIN_WIDTH = 12;

    //默认的显示参数配置
    private static final int DEFAULT_TIP_SIZE = 12;
    private static final int EXTRA_TITLE_SIZE = 4;

    public List<TxtChapter> getmChapterList() {
        return mChapterList;
    }

    public void setmChapterList(List<TxtChapter> mChapterList) {
        this.mChapterList = mChapterList;
    }

    //当前章节列表
    protected List<TxtChapter> mChapterList;

    //当前章节列表
    protected List<TxtChapter> mOrigChapterList;

    //当前章节列表
    protected List<TxtChapter> mOtherChapterList;

    //书本对象
    protected CollBookBean mCollBook;
    //监听器
    protected OnPageChangeListener mPageChangeListener;

    //页面显示类
    public PageView mPageView;

    public TxtPage getmCurPage() {
        return mCurPage;
    }

    public void setmCurPage(TxtPage mCurPage) {
        this.mCurPage = mCurPage;
    }

    //当前显示的页
    private TxtPage mCurPage;
    //上一章的页面列表缓存
    private WeakReference<List<TxtPage>> mWeakPrePageList;
    //当前章节的页面列表
    private List<TxtPage> mCurPageList;

    public List<TxtPage> getmCurPageList() {
        if(mCurPageList==null){
            return new ArrayList<TxtPage>();
        }
        return mCurPageList;
    }

    public void setmCurPageList(List<TxtPage> mCurPageList) {
        this.mCurPageList = mCurPageList;
    }

    //下一章的页面列表缓存
    private List<TxtPage> mNextPageList;

    //下一页绘制缓冲区，用户缓解卡顿问题。
    private Bitmap mNextBitmap;

    public int getmTextInterval() {
        return text_interval;
    }

    int text_interval;

    public void setmTextInterval(int mTextInterval) {
        try {
            if (!isBookOpen) return;
            int interval = intervals[mTextInterval];
            this.mTextInterval = App.getAppResources().getDimensionPixelOffset(interval);
            mTitleSize = mTextSize + getContext().getResources().getDimensionPixelOffset(R.dimen.dp_4);//ScreenUtils.spToPx(EXTRA_TITLE_SIZE);

            //mTitleInterval = mTitleInterval;
            mTitlePara = mTitleSize;

            //设置画笔的字体大小
            mTextPaint.setTextSize(mTextSize);
            //设置标题的字体大小
            mTitlePaint.setTextSize(mTitleSize);
            text_interval = mTextInterval;
            //存储状态
            mSettingManager.setTextRow(mTextInterval);
            //取消缓存
            mWeakPrePageList = null;
            mNextPageList = null;
            //如果当前为完成状态。
            if (mStatus == STATUS_FINISH) {
                //重新计算页面
                mCurPageList = loadPageList(mCurChapterPos);

                //防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
                if (mCurPage.position >= mCurPageList.size()) {
                    mCurPage.position = mCurPageList.size() - 1;
                }
            }
            //重新设置文章指针的位置
            mCurPage = getCurPage(mCurPage.position);
            //绘制
            mPageView.refreshPage();
        }catch (Exception ex){

        }
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    int weight;
    //行间距
    private int mTextInterval;
    //绘制电池的画笔
    private Paint mBatteryPaint;
    //绘制提示的画笔
    private Paint mTipPaint;
    //绘制标题的画笔
    private Paint mTitlePaint;
    //绘制背景颜色的画笔(用来擦除需要重绘的部分)
    private Paint mBgPaint;
    //绘制小说内容的画笔
    private TextPaint mTextPaint;
    //阅读器的配置选项
    private ReadSettingManager mSettingManager;
    //被遮盖的页，或者认为被取消显示的页
    private TxtPage mCancelPage;
    //存储阅读记录类
    public BookRecordBean mBookRecord;
    /*****************params**************************/
    //当前的状态
    protected int mStatus = STATUS_LOADING;

    public int getmCurChapterPos() {
        return mCurChapterPos;
    }

    public void setmCurChapterPos(int mCurChapterPos) {
        this.mCurChapterPos = mCurChapterPos;
    }

    //当前章
    protected int mCurChapterPos = 0;
    //书本是否打开
    protected boolean isBookOpen = false;

    private Disposable mPreLoadDisp;
    //上一章的记录
    private int mLastChapter = 0;
    //书籍绘制区域的宽高
    private int mVisibleWidth;
    private int mVisibleHeight;
    //应用的宽高
    private int mDisplayWidth;
    private int mDisplayHeight;
    //间距
    private int mMarginWidth;
    private int mMarginHeight;
    //字体的颜色
    private int mTextColor;
    //标题的大小
    private int mTitleSize;

    public int getmTextSize() {
        return text_index;
    }

    int text_index = 0;

    public void setmTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }

    public int getChpter_id() {
        return chpter_id;
    }

    public void setChpter_id(int chpter_id) {
        this.chpter_id = chpter_id;
    }

    private int chpter_id;
    //字体的大小
    private int mTextSize;

    //字体的大小
    private String mTextStyle;

    //标题的行间距
    private int mTitleInterval;
    //段落距离(基于行间距的额外距离)
    private int mTextPara;
    private int mTitlePara;
    //电池的百分比
    private int mBatteryLevel;

    public int getmPageMode() {
        return mPageMode;
    }

    //页面的翻页效果模式
    private int mPageMode;
    //加载器的颜色主题
    private int mBgTheme;

    public int getmPageBg() {
        return mPageBg;
    }

    //当前页面的背景
    private int mPageBg;
    //当前是否是夜间模式
    private boolean isNightMode;

    /*****************************init params*******************************/
    public PageLoader(PageView pageView) {
        mPageView = pageView;
        try {
            //初始化数据
            initData();
            //初始化画笔
            initPaint();
            //初始化PageView
            initPageView();
        }catch (Exception ex){

        }
    }

    private void initData() {
        mSettingManager = ReadSettingManager.getInstance();
        text_index = mSettingManager.getTextSize();
        if (text_index >= ints.length) {
            text_index = 1;
        }
        text_interval = mSettingManager.getTextRow();
        if (text_interval >= intervals.length) {
            text_interval = 1;
        }
        mTextSize = App.getAppResources().getDimensionPixelSize(ints[text_index]);
        mTextInterval = App.getAppResources().getDimensionPixelSize(intervals[text_interval]);
        ;
        mTextStyle = mSettingManager.getTextStyle();
        mTitleSize = mTextSize + App.getAppResources().getDimensionPixelOffset(R.dimen.dp_4);//EXTRA_TITLE_SIZE;
        mPageMode = mSettingManager.getPageMode();
        isNightMode = mSettingManager.isNightMode();
        mBgTheme = mSettingManager.getReadBgTheme();
        if (isNightMode) {
            setBgColor(ReadSettingManager.NIGHT_MODE);
        } else {
            setBgColor(mBgTheme);
        }

        //初始化参数
        mMarginWidth = ScreenUtils.dpToPx(DEFAULT_MARGIN_WIDTH);
        mMarginHeight = ScreenUtils.dpToPx(DEFAULT_MARGIN_HEIGHT);
        //mTextInterval = mTextSize / 2;
        mTitleInterval = mTitleSize / 2;
        mTextPara = mTextSize; //段落间距由 text 的高度决定。
        mTitlePara = mTitleSize;
    }

    private void initPaint() {
        //绘制提示的画笔
        mTipPaint = new Paint();
        mTipPaint.setColor(mTextColor);
        mTipPaint.setTextAlign(Paint.Align.LEFT);//绘制的起始点
        mTipPaint.setTextSize(App.getAppResources().getDimensionPixelSize(R.dimen.dp_13));//Tip默认的字体大小
        mTipPaint.setAntiAlias(true);
        mTipPaint.setSubpixelText(true);

        //绘制页面内容的画笔
        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);

        //绘制标题的画笔
        mTitlePaint = new TextPaint();
        mTitlePaint.setColor(mTextColor);
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTitlePaint.setAntiAlias(true);

        //绘制背景的画笔
        mBgPaint = new Paint();
        mBgPaint.setColor(mPageBg);

        mBatteryPaint = new Paint();
        mBatteryPaint.setAntiAlias(true);
        mBatteryPaint.setDither(true);
        if (isNightMode) {
            mBatteryPaint.setColor(Color.WHITE);
        } else {
            mBatteryPaint.setColor(Color.BLACK);
        }
        Typeface tf = null;
        if (mTextStyle != null && !mTextStyle.equals("")) {
            AssetManager mgr = getContext().getAssets();
            //根据路径得到Typeface
            if (mTextStyle.equals("1")) {
                tf = Typeface.createFromAsset(mgr, Constant.text_adress1);
            } else if (mTextStyle.equals("2")) {
                tf = Typeface.createFromAsset(mgr, Constant.text_adress2);
            } else if (mTextStyle.equals("3")) {
                tf = Typeface.createFromAsset(mgr, Constant.text_adress3);
            } else if (mTextStyle.equals("4")) {
                tf = Typeface.createFromAsset(mgr, Constant.text_adress4);
            } else if (mTextStyle.equals("-1")) {
                tf = Typeface.create("sans-serif-medium", Typeface.NORMAL);
            }
            mTitlePaint.setTypeface(tf);
            mTextPaint.setTypeface(tf);
        } else {
            tf = Typeface.create("sans-serif-medium", Typeface.NORMAL);
            mTitlePaint.setTypeface(tf);
            mTextPaint.setTypeface(tf);
        }
    }

    private void initPageView() {
        //配置参数
        mPageView.setPageMode(mPageMode);
        mPageView.setBgColor(mPageBg);
    }

    /****************************** public method***************************/
    //跳转到上一章
    public int skipPreChapter() {
        if (!isBookOpen) {
            return mCurChapterPos;
        }

        //载入上一章。
        if (prevChapter()) {
            mCurPage = getCurPage(0);
            mPageView.refreshPage();
        }
        return mCurChapterPos;
    }

    //跳转到下一章
    public int skipNextChapter() {
        if (!isBookOpen) {
            return mCurChapterPos;
        }

        //判断是否达到章节的终止点
        if (nextChapter()) {
            mCurPage = getCurPage(0);
            mPageView.refreshPage();
        }
        return mCurChapterPos;
    }

    //跳转到指定章节
    public void skipToChapter(int pos) {
        //正在加载
        mStatus = STATUS_LOADING;
        //绘制当前的状态
        mCurChapterPos = pos;
        //将上一章的缓存设置为null
        mWeakPrePageList = null;

        //如果当前下一章缓存正在执行，则取消
        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }
        //将下一章缓存设置为null
        mNextPageList = null;

        if (mPageChangeListener != null) {
            mPageChangeListener.onChapterChange(mCurChapterPos);
        }

        if (mCurPage != null) {
            //重置position的位置，防止正在加载的时候退出时候存储的位置为上一章的页码
            mCurPage.position = 0;
        }

        //需要对ScrollAnimation进行重新布局
        mPageView.refreshPage();
    }

    //跳转到指定章节
    public void skipToChapter(int pos, int index) {
        //正在加载
        mStatus = STATUS_LOADING;
        //绘制当前的状态
        mCurChapterPos = pos;
        //将上一章的缓存设置为null
        mWeakPrePageList = null;

        //如果当前下一章缓存正在执行，则取消
        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }
        //将下一章缓存设置为null
        mNextPageList = null;

        if (mPageChangeListener != null) {
            mPageChangeListener.onChapterChange(mCurChapterPos);
        }
        if (mCurPage != null) {
            //重置position的位置，防止正在加载的时候退出时候存储的位置为上一章的页码
            mCurPage.position = 0;
        }

        //需要对ScrollAnimation进行重新布局
        mPageView.refreshPage();
    }

    //跳转到具体的页
    public void skipToPage(int pos) {
        if (pos < mCurPageList.size()) {
            mCurPage = getCurPage(pos);
            mPageView.refreshPage();
        }
    }

    //自动翻到上一章
    public boolean autoPrevPage() {
        if (!isBookOpen) return false;
        return mPageView.autoPrevPage();
    }

    //自动翻到下一章
    public boolean autoNextPage() {
        if (!isBookOpen) return false;
        return mPageView.autoNextPage();
    }

    //更新时间
    public void updateTime() {
        if (mPageView.isPrepare() && !mPageView.isRunning()) {
            mPageView.drawCurPage(true);
        }
    }

    //更新电量
    public void updateBattery(int level) {
        mBatteryLevel = level;
        if (mPageView.isPrepare() && !mPageView.isRunning()) {
            mPageView.drawCurPage(true);
        }
    }

    int[] ints = {R.dimen.dp_14, R.dimen.dp_16, R.dimen.dp_18, R.dimen.dp_20, R.dimen.dp_22, R.dimen.dp_24, R.dimen.dp_26, R.dimen.dp_28, R.dimen.dp_29, R.dimen.dp_30, R.dimen.dp_32, R.dimen.dp_34, R.dimen.dp_36, R.dimen.dp_38, R.dimen.dp_39};
    int[] intervals = {R.dimen.dp_2, R.dimen.dp_4, R.dimen.dp_6, R.dimen.dp_8, R.dimen.dp_10, R.dimen.dp_12, R.dimen.dp_14, R.dimen.dp_16, R.dimen.dp_18, R.dimen.dp_20};

    public int text_size() {
        return ints.length;
    }

    public int getInterval() {
        return intervals.length;
    }

    //设置文字大小
    public void setTextSize(int textSize) {
        try {
            if (!isBookOpen) return;
            int position = ints[textSize];
            //设置textSize
            mTextSize = App.getAppResources().getDimensionPixelSize(position);
            //mTextInterval = mTextSize / 2;
            mTextPara = mTextSize;

            mTitleSize = mTextSize + App.getAppResources().getDimensionPixelOffset(R.dimen.dp_4);
            mTitleInterval = mTitleInterval / 2;
            mTitlePara = mTitleSize;

            //设置画笔的字体大小
            mTextPaint.setTextSize(mTextSize);
            //设置标题的字体大小
            mTitlePaint.setTextSize(mTitleSize);
            text_index = textSize;
            //存储状态
            mSettingManager.setTextSize(text_index);
            //取消缓存
            mWeakPrePageList = null;
            mNextPageList = null;
            //如果当前为完成状态。
            if (mStatus == STATUS_FINISH) {
                //重新计算页面
                mCurPageList = loadPageList(mCurChapterPos);

                //防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
                if (mCurPage.position >= mCurPageList.size()) {
                    mCurPage.position = mCurPageList.size() - 1;
                }
            }
            //重新设置文章指针的位置
            mCurPage = getCurPage(mCurPage.position);
            //绘制
            mPageView.refreshPage();
        }catch (Exception ex){

        }
    }

    public void setmSype(String type) {
        try {
            if (!isBookOpen) return;

            //mTextInterval = mTextSize / 2;
            mTextPara = mTextSize;
            mTextStyle = type;
            mTitleSize = mTextSize + App.getAppResources().getDimensionPixelOffset(R.dimen.dp_4);
            mTitleInterval = mTitleInterval / 2;
            mTitlePara = mTitleSize;
            mSettingManager.setTextStyle(mTextStyle);
            //设置画笔的字体大小
            mTextPaint.setTextSize(mTextSize);
            //设置标题的字体大小
            mTitlePaint.setTextSize(mTitleSize);
            Typeface tf = null;
            if (type != null && !type.equals("")) {
                //从asset 读取字体
                AssetManager mgr = getContext().getAssets();
//根据路径得到Typeface
                if (type.equals("1")) {
                    tf = Typeface.createFromAsset(mgr, Constant.text_adress1);
                } else if (type.equals("2")) {
                    tf = Typeface.createFromAsset(mgr, Constant.text_adress2);
                } else if (type.equals("3")) {
                    tf = Typeface.createFromAsset(mgr, Constant.text_adress3);
                } else if (type.equals("4")) {
                    tf = Typeface.createFromAsset(mgr, Constant.text_adress4);
                } else if (type.equals("-1")) {
                    tf = Typeface.create("sans-serif-medium", Typeface.NORMAL);
                }
                mTitlePaint.setTypeface(tf);
                mTextPaint.setTypeface(tf);
            } else {
                tf = Typeface.create("sans-serif-medium", Typeface.NORMAL);
                mTitlePaint.setTypeface(tf);
                mTextPaint.setTypeface(tf);
            }
            //存储状态
            mSettingManager.setTextSize(text_index);
            //取消缓存
            mWeakPrePageList = null;
            mNextPageList = null;
            //如果当前为完成状态。
            if (mStatus == STATUS_FINISH) {
                //重新计算页面
                mCurPageList = loadPageList(mCurChapterPos);

                //防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
                if (mCurPage.position >= mCurPageList.size()) {
                    mCurPage.position = mCurPageList.size() - 1;
                }
            }
            //重新设置文章指针的位置
            mCurPage = getCurPage(mCurPage.position);
            //绘制
            mPageView.refreshPage();
        }catch (Exception ex){

        }
    }

    //设置夜间模式
    public void setNightMode(boolean nightMode) {
        isNightMode = nightMode;
        if (isNightMode) {
            mBatteryPaint.setColor(Color.WHITE);
            setBgColor(ReadSettingManager.NIGHT_MODE);
            // view.setBackgroundColor(ReadSettingManager.NIGHT_MODE);
        } else {
            mBgTheme = mSettingManager.getReadBgTheme();
            mBatteryPaint.setColor(Color.BLACK);
            setBgColor(mBgTheme);
            //view.setBackgroundColor(mBgTheme);
        }
        mSettingManager.setNightMode(nightMode);
    }

    //绘制背景
    public void setBgColor(int theme) {
        if (isNightMode && theme == ReadSettingManager.NIGHT_MODE) {
            mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_fff_99);
            mPageBg = ContextCompat.getColor(App.getAppContext(), R.color.black);
            isbackground = false;
        } else if (isNightMode) {
            mBgTheme = theme;
            mSettingManager.setReadBackground(theme);
        } else {
            mSettingManager.setReadBackground(theme);
            switch (theme) {
                case ReadSettingManager.READ_BG_DEFAULT:
                    mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_2c);
                    mPageBg = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor1);
                    break;
                case ReadSettingManager.READ_BG_1:
                    mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_2c);
                    mPageBg = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor2);
                    break;
                case ReadSettingManager.READ_BG_2:
                    mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_2c);
                    mPageBg = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor3);
                    break;
                case ReadSettingManager.READ_BG_3:
                    mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_2c);
                    mPageBg = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor4);
                    break;
                case ReadSettingManager.READ_BG_4:
                    mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_2c);
                    mPageBg = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor5);
                    break;
            }
        }

        if (isBookOpen) {
            //设置参数
            mPageView.setBgColor(mPageBg);
            mTextPaint.setColor(mTextColor);
            mTitlePaint.setColor(mTextColor);
            //重绘
            mPageView.refreshPage();
        }
    }

    public void setBgColor(int theme, View v, View tv) {
//        if (isNightMode && theme == ReadSettingManager.NIGHT_MODE) {
//            mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_fff_99);
//            mPageBg = ContextCompat.getColor(App.getAppContext(), R.color.black);
//        } else if (isNightMode) {
//            mBgTheme = theme;
//            mSettingManager.setReadBackground(theme);
//        } else {
        mSettingManager.setReadBackground(theme);
        mSettingManager.setNightMode(false);
        switch (theme) {
            case ReadSettingManager.READ_BG_DEFAULT:
                mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_2c);
                mPageBg = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor1);
                break;
            case ReadSettingManager.READ_BG_1:
                mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_2c);
                mPageBg = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor2);
                break;
            case ReadSettingManager.READ_BG_2:
                mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_2c);
                mPageBg = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor3);
                break;
            case ReadSettingManager.READ_BG_3:
                mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_2c);
                mPageBg = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor4);
                break;
            case ReadSettingManager.READ_BG_4:
                mTextColor = ContextCompat.getColor(App.getAppContext(), R.color.color_2c);
                mPageBg = ContextCompat.getColor(App.getAppContext(), R.color.hwtxtreader_styleclor5);
                break;
        }
//        }

        if (isBookOpen) {
//            if(mPageBg==ContextCompat.getColor(App.getAppContext(), R.color.color_cec29c)){
//                isbackground=true;
//                v.setBackgroundColor(mPageBg);
//                v.setBackground(getAppResources().getDrawable(R.mipmap.img_background2));
//            }else {
//                isbackground=false;
            v.setBackgroundColor(mPageBg);
            tv.setBackgroundColor(mPageBg);
//            }
            //设置参数
            mPageView.setBgColor(mPageBg);
            mTextPaint.setColor(mTextColor);
            mTitlePaint.setColor(mTextColor);
            //重绘
            mPageView.refreshPage();
        }
    }

    //翻页动画
    public void setPageMode(int pageMode) {
        mPageMode = pageMode;
        mPageView.setPageMode(mPageMode);
        mSettingManager.setPageMode(mPageMode);
        //重绘
        mPageView.drawCurPage(false);
    }

    //设置页面切换监听
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mPageChangeListener = listener;
    }

    //获取当前页的状态
    public int getPageStatus() {
        return mStatus;
    }

    //获取当前章节的章节位置
    public int getChapterPos() {
        return mCurChapterPos;
    }

    //获取当前页的页码
    public int getPagePos() {
        if (mCurPage == null) {
            return 1;
        } else {
            return mCurPage.position;
        }
    }

    //保存阅读记录
    public void saveRecord() {
        try {
            //书没打开，就没有记录
            if (!isBookOpen) return;

            mBookRecord.setBookId(mCollBook.get_id());
            mBookRecord.setChapter(mCurChapterPos);
            mBookRecord.setPagePos(mCurPage.position);

            //存储到数据库
            BookRecordHelper.getsInstance().saveRecordBook(mBookRecord);
        }catch (Exception ex){

        }
    }

    //打开书本，初始化书籍
    public void openBook(CollBookBean collBook, BookRecordBean bookRecordBean) {
        mCollBook = collBook;
        mBookRecord = bookRecordBean;
        mCurChapterPos = mBookRecord.getChapter();
        mLastChapter = mCurChapterPos;
    }

    public void make_loading() {
        mStatus = STATUS_LOADING;
    }

    //  打开具体章节
    public void openChapter() {
//        mCurPageList = loadPageList(mCurChapterPos);
//        if(mCurPageList!=null&&mCurPageList.size()>0) {
//            //进行预加载
//            preLoadNextChapter();
//            //加载完成
//            mStatus = STATUS_FINISH;
//            //Log.e("QQQ", "openChapter: "+mCurChapterPos+" "+isBookOpen);
//            //获取制定页面
//            if (!isBookOpen) {
//                isBookOpen = true;
//                //可能会出现当前页的大小大于记录页的情况。
//                int position = mBookRecord.getPagePos();
//                if (position >= mCurPageList.size()) {
//                    position = mCurPageList.size() - 1;
//                }
//                mCurPage = getCurPage(position);
//                mCancelPage = mCurPage;
//                if (mPageChangeListener != null) {
//                    mPageChangeListener.onChapterChange(mCurChapterPos);
//                }
//                mCurPage = getCurPage(0);
//            } else {
//                if (mCurPageList != null && mCurPageList.size() != 0) {
//                    mCurPage = getCurPage(0);
//                } else {
//                    chapterError();
//                }
//            }
//        }else {
//            mStatus=STATUS_ERROR;
//        }
//        mPageView.drawCurPage(false);
        Log.e("WWW", "openChapter: " + mCurChapterPos);
        mCurPageList = loadPageList(mCurChapterPos);
        if(mCurPageList==null){
           return;
        }
        //进行预加载
        preLoadNextChapter();
        //加载完成
        mStatus = STATUS_FINISH;
        //获取制定页面
        if (!isBookOpen) {
            isBookOpen = true;
            //可能会出现当前页的大小大于记录页的情况。
            int position = mBookRecord.getPagePos();
            if (position >= mCurPageList.size()) {
                position = mCurPageList.size() - 1;
            }
            mCurPage = getCurPage(position);
            mCancelPage = mCurPage;
            if (mPageChangeListener != null) {
                mPageChangeListener.onChapterChange(mCurChapterPos);
            }
        } else {
            mCurPage = getCurPage(0);
        }
        Log.e("www", "openChapter: " + 222);
        mPageView.drawCurPage(false);
    }

    public void chapterError() {
        //加载错误
        mStatus = STATUS_ERROR;
        //显示加载错误
        mPageView.drawCurPage(false);
    }

    //清除记录，并设定是否缓存数据
    public void closeBook() {
        isBookOpen = false;
        mPageView = null;
        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }
    }

    /*******************************abstract method***************************************/
    //设置章节
    // public abstract void setChapterList(List<BookChapterBean> bookChapters);
    @Nullable
    protected abstract List<TxtPage> loadPageList(int chapter);

    /***********************************default method***********************************************/
    //通过流获取Page的方法
    List<TxtPage> loadPages(TxtChapter chapter, BufferedReader br, int position) {
        //生成的页面
        List<TxtPage> pages = new ArrayList<>();
        //使用流的方式加载
        List<String> lines = new ArrayList<>();
        int rHeight = mVisibleHeight; //由于匹配到最后，会多删除行间距，所以在这里多加个行间距
        int titleLinesCount = 0;
        int line_total = 0;
        boolean isTitle = true; //不存在没有 Title 的情况，所以默认设置为 true。
        String paragraph = chapter.getTitle();//默认展示标题
        try {
            while (isTitle || (paragraph = br.readLine()) != null) {

                //重置段落
                if (!isTitle) {
                    paragraph = paragraph.replaceAll("\\s", "");
                    //如果只有换行符，那么就不执行
                    if (paragraph.equals("")) continue;
                    paragraph = StringUtils.halfToFull("  " + paragraph + "\n");
                } else {
                    //设置 title 的顶部间距
                    rHeight -= mTitlePara;
                }

                int wordCount = 0;
                String subStr = null;
                while (paragraph.length() > 0) {
                    //当前空间，是否容得下一行文字
                    if (isTitle) {
                        rHeight -= mTitlePaint.getTextSize();
                    } else {
                        rHeight -= mTextPaint.getTextSize();
                    }

                    //一页已经填充满了，创建 TextPage
                    if (rHeight < mTextInterval / 3) {
                        line_total = lines.size();
                        //创建Page
                        TxtPage page = new TxtPage();
                        page.position = pages.size();
                        page.title = chapter.getTitle();
                        page.lines = new ArrayList<>(lines);
                        page.titleLines = titleLinesCount;
                        pages.add(page);
                        //重置Lines
                        lines.clear();
                        rHeight = mVisibleHeight;
                        titleLinesCount = 0;
                        continue;
                    }

                    //测量一行占用的字节数
                    if (isTitle) {
                        wordCount = mTitlePaint.breakText(paragraph, true, mVisibleWidth, null);
                    } else {
                        wordCount = mTextPaint.breakText(paragraph, true, mVisibleWidth, null);
                    }

                    subStr = paragraph.substring(0, wordCount);
                    if (!subStr.equals("\n")) {
                        //将一行字节，存储到lines中
                        lines.add(subStr);

                        //设置段落间距
                        if (isTitle) {
                            titleLinesCount += 1;
                            rHeight -= mTitleInterval;
                        } else {
                            rHeight -= mTextInterval;
                        }
                    }
                    //裁剪
                    paragraph = paragraph.substring(wordCount);
                }

                //增加段落的间距
                if (!isTitle && lines.size() != 0) {
                    rHeight = rHeight - mTextPara + mTextInterval;
                }

                if (isTitle) {
                    rHeight = rHeight - mTitlePara + mTitleInterval;
                    isTitle = false;
                }
            }

            if (lines.size() != 0) {
//                if(lines.size()<line_total){
//                    for(int i=0;i<line_total;i++){
//                        lines.add("qqqqqqqq");
//                    }
//                }
                //创建Page
                TxtPage page = new TxtPage();
                page.position = pages.size();
                page.title = chapter.getTitle();
                page.lines = new ArrayList<>(lines);
                page.titleLines = titleLinesCount;
                pages.add(page);
                //重置Lines
                lines.clear();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(br);
        }
        //可能出现内容为空的情况
        if (pages.size() == 0) {
            TxtPage page = new TxtPage();
            page.lines = new ArrayList<>(1);
            pages.add(page);

            mStatus = STATUS_EMPTY;
        }

        //提示章节数量改变了。
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageCountChange(pages.size());
        }

//        if (position >= 0 && !(mPageView.getmPageAnim() instanceof ScrollPageAnim2) && position % Constant.ADM_MAX == 0) {
//            TxtPage txtPage = pages.get(pages.size() - 1);
//            if (txtPage.lines.size() > 4) {
//                TxtPage page = new TxtPage();
//                page.position = pages.size();
//                page.title = chapter.getTitle();
//                page.lines = new ArrayList<>();
//                page.titleLines = 0;
//                pages.add(page);
//            }
//        }
//        if (position >= 0&&(mPageView.getmPageAnim() instanceof ScrollPageAnim2)) {
//            TxtPage txtPage=pages.get(pages.size()-1);
//            if(txtPage.lines.size()<lines.size()) {
//                TxtPage page = new TxtPage();
//                page.position = pages.size();
//                page.title = chapter.getTitle();
//                page.lines = new ArrayList<>();
//                page.titleLines = 0;
//                pages.add(page);
//            }
//        }
        return pages;
    }

    void onDraw(Bitmap bitmap, boolean isUpdate) {
        try {
            if (mChapterList != null && mChapterList.size() != 0 && mCurChapterPos >= mChapterList.size()) {
                mCurChapterPos = mChapterList.size() - 1;
            }
            drawBackground(mPageView.getBgBitmap(), isUpdate);
            if (!isUpdate) {
                drawContent(bitmap);
            }
            //更新绘制
            mPageView.invalidate();
        }catch (Exception ex){

        }
    }

    void onDraw(Bitmap bitmap, Bitmap adm_bitmap, boolean isUpdate) {
        if (mChapterList != null && mChapterList.size() != 0 && mCurChapterPos >= mChapterList.size()) {
            mCurChapterPos = mChapterList.size() - 1;
        }
        drawBackground(mPageView.getBgBitmap(), isUpdate);
        if (!isUpdate) {
            drawContent(bitmap, adm_bitmap);
        }
        //更新绘制
        mPageView.invalidate();
    }

    boolean isbackground;

    void drawBackground(Bitmap bitmap, boolean isUpdate) {
        Canvas canvas = new Canvas(bitmap);
        int tipMarginHeight = ScreenUtils.dpToPx(3);
        if (!isUpdate) {
            /****绘制背景****/
            canvas.drawColor(mPageBg);
            /*****初始化标题的参数********/
            //需要注意的是:绘制text的y的起始点是text的基准线的位置，而不是从text的头部的位置
            float tipTop = tipMarginHeight - mTipPaint.getFontMetrics().top;
            //根据状态不一样，数据不一样
            if (mStatus != STATUS_FINISH) {
                if (mChapterList != null && mChapterList.size() != 0) {
                    mTipPaint.setColor(App.getAppResources().getColor(R.color.word_color));
                    mTipPaint.setColor(App.getAppResources().getColor(R.color.word_color));
                    String s = mChapterList.get(mCurChapterPos).getTitle() + "/" + mCollBook.getTitle();
                    if (s.length() > 14) {
                        s = s.substring(0, 14) + "...";
                    }
                    canvas.drawText(s, mMarginWidth, tipTop, mTipPaint);
                }
                mTipPaint.setColor(mTextColor);
            } else {
                if(mChapterList != null&&mCurPage!=null) {
                    mTipPaint.setColor(App.getAppResources().getColor(R.color.word_color));
                    String s = mChapterList.get(mCurChapterPos).getTitle() + "/" + mCollBook.getTitle();
                    if (s.length() > 14) {
                        s = s.substring(0, 14) + "...";
                    }
                    canvas.drawText(s, mMarginWidth, tipTop, mTipPaint);
//                String sth=(getChapterPos()+1)+"/"+mChapterList.size();
//                canvas.drawText(sth, mVisibleWidth-mMarginWidth-sth.length(), tipTop, mTipPaint);
                    mTipPaint.setColor(mTextColor);

            /******绘制页码********/
            //底部的字显示的位置Y
            float y = mDisplayHeight - mTipPaint.getFontMetrics().bottom - tipMarginHeight;
            //只有finish的时候采用页码
            if (mStatus == STATUS_FINISH&&mCurPageList!=null) {
                String percent = (mCurPage.position + 1) + "/" + mCurPageList.size();
                canvas.drawText(percent, mMarginWidth, y, mTipPaint);
            }
               }
            }
        } else {
            //擦除区域
            mBgPaint.setColor(mPageBg);
            canvas.drawRect(mDisplayWidth / 2, mDisplayHeight - mMarginHeight + ScreenUtils.dpToPx(2), mDisplayWidth, mDisplayHeight, mBgPaint);
        }
        /******绘制电池********/

//        int visibleRight = mDisplayWidth - mMarginWidth;
//        int visibleBottom = mDisplayHeight - tipMarginHeight;
//
//        int outFrameWidth = (int) mTipPaint.measureText("xxx");
//        int outFrameHeight = (int) mTipPaint.getTextSize();
//
//        int polarHeight = ScreenUtils.dpToPx(6);
//        int polarWidth = ScreenUtils.dpToPx(2);
//        int border = 1;
//        int innerMargin = 1;

        //电极的制作
//        int polarLeft = visibleRight - polarWidth;
//        int polarTop = visibleBottom - (outFrameHeight + polarHeight) / 2;
//        Rect polar = new Rect(polarLeft, polarTop, visibleRight,
//                polarTop + polarHeight - ScreenUtils.dpToPx(2));
//
//        mBatteryPaint.setStyle(Paint.Style.FILL);
//        canvas.drawRect(polar, mBatteryPaint);
//
//        //外框的制作
//        int outFrameLeft = polarLeft - outFrameWidth;
//        int outFrameTop = visibleBottom - outFrameHeight;
//        int outFrameBottom = visibleBottom - ScreenUtils.dpToPx(2);
//        Rect outFrame = new Rect(outFrameLeft, outFrameTop, polarLeft, outFrameBottom);
//
//        mBatteryPaint.setStyle(Paint.Style.STROKE);
//        mBatteryPaint.setStrokeWidth(border);
//        canvas.drawRect(outFrame, mBatteryPaint);
//
//        //内框的制作
//        float innerWidth = (outFrame.width() - innerMargin * 2 - border) * (mBatteryLevel / 100.0f);
//        RectF innerFrame = new RectF(outFrameLeft + border + innerMargin, outFrameTop + border + innerMargin,
//                outFrameLeft + border + innerMargin + innerWidth, outFrameBottom - border - innerMargin);
//
//        mBatteryPaint.setStyle(Paint.Style.FILL);
//        canvas.drawRect(innerFrame, mBatteryPaint);

        /******绘制当前时间********/
        //底部的字显示的位置Y
//        float y = mDisplayHeight - mTipPaint.getFontMetrics().bottom - tipMarginHeight;
//        String time = StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_TIME);
//        float x = outFrameLeft - mTipPaint.measureText(time) - ScreenUtils.dpToPx(4);
//        canvas.drawText(time, x, y, mTipPaint);
    }

    void drawContent(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        if (mPageMode == PageView.PAGE_MODE_SCROLL) {
            canvas.drawColor(mPageBg);
        }
       // mPageChangeListener.onShowAdm(true);
        /******绘制内容****/
        if (mStatus != STATUS_FINISH) {
            //绘制字体
            String tip = "";
            switch (mStatus) {
                case STATUS_LOADING:
                    tip = "正在拼命加载中...";
                    break;
                case STATUS_ERROR:
                    tip = "加载失败(点击边缘重试)";
                    break;
                case STATUS_EMPTY:
                    tip = "文章内容为空";
                    break;
                case STATUS_PARSE:
                    tip = "正在排版请等待...";
                    break;
                case STATUS_PARSE_ERROR:
                    tip = "文件解析错误";
                    break;
            }
            //将提示语句放到正中间
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float textHeight = fontMetrics.top - fontMetrics.bottom;
            float textWidth = mTextPaint.measureText(tip);
            float pivotX = (mDisplayWidth - textWidth) / 2;
            float pivotY = (mDisplayHeight - textHeight) / 2;
            canvas.drawText(tip, pivotX, pivotY, mTextPaint);
        } else {
            float top;

            if (mPageMode == PageView.PAGE_MODE_SCROLL) {
                top = -mTextPaint.getFontMetrics().top;
            } else {
                top = mMarginHeight - mTextPaint.getFontMetrics().top;
            }

            //设置总距离
            int interval = mTextInterval + (int) mTextPaint.getTextSize();
            int para = mTextPara + (int) mTextPaint.getTextSize();
            int titleInterval = mTitleInterval + (int) mTitlePaint.getTextSize();
            int titlePara = mTitlePara + (int) mTextPaint.getTextSize();
            String str = null;
            if (mCurPage!=null) {
                //对标题进行绘制
                for (int i = 0; i < mCurPage.titleLines; ++i) {
                    str = mCurPage.lines.get(i);

                    //设置顶部间距
                    if (i == 0) {
                        top += mTitlePara;
                    }

                    //计算文字显示的起始点
                    int start = (int) (mDisplayWidth - mTitlePaint.measureText(str)) / 2;
                    //进行绘制
                    canvas.drawText(str, start, top, mTitlePaint);

                    //设置尾部间距
                    if (i == mCurPage.titleLines - 1) {
                        top += titlePara;
                    } else {
                        //行间距
                        top += titleInterval;
                    }
                }
                if (mCurPage.lines != null && mCurPage.lines.size() > 0) {
                    //对内容进行绘制
                    for (int i = mCurPage.titleLines; i < mCurPage.lines.size(); ++i) {
                        str = mCurPage.lines.get(i);
                        canvas.drawText(str, mMarginWidth, top, mTextPaint);
                        if (str.endsWith("\n")) {
                            top += para;
                        } else {
                            top += interval;
                        }
                    }
                    mPageChangeListener.onShowAdm(false);
                } else {
                    mPageChangeListener.onShowAdm(true);
                }
            }
        }
    }

    void drawContent(Bitmap bitmap, Bitmap adm_bitmap) {
        Canvas canvas = new Canvas(bitmap);
        if (mPageMode == PageView.PAGE_MODE_SCROLL) {
            canvas.drawColor(mPageBg);
        }
        /******绘制内容****/
        if (mStatus != STATUS_FINISH) {
            //绘制字体
            String tip = "";
            switch (mStatus) {
                case STATUS_LOADING:
                    tip = "正在拼命加载中...";
                    break;
                case STATUS_ERROR:
                    tip = "加载失败(点击边缘重试)";
                    break;
                case STATUS_EMPTY:
                    tip = "文章内容为空";
                    break;
                case STATUS_PARSE:
                    tip = "正在排版请等待...";
                    break;
                case STATUS_PARSE_ERROR:
                    tip = "文件解析错误";
                    break;
            }

            //将提示语句放到正中间
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float textHeight = fontMetrics.top - fontMetrics.bottom;
            float textWidth = mTextPaint.measureText(tip);
            float pivotX = (mDisplayWidth - textWidth) / 2;
            float pivotY = (mDisplayHeight - textHeight) / 2;
            canvas.drawText(tip, pivotX, pivotY, mTextPaint);
        } else {
            float top;
            if (mPageMode == PageView.PAGE_MODE_SCROLL) {
                top = -mTextPaint.getFontMetrics().top;
            } else {
                top = mMarginHeight - mTextPaint.getFontMetrics().top;
            }

            //设置总距离
            int interval = mTextInterval + (int) mTextPaint.getTextSize();
            int para = mTextPara + (int) mTextPaint.getTextSize();
            int titleInterval = mTitleInterval + (int) mTitlePaint.getTextSize();
            int titlePara = mTitlePara + (int) mTextPaint.getTextSize();
            String str = null;

            //对标题进行绘制
            for (int i = 0; i < mCurPage.titleLines; ++i) {
                str = mCurPage.lines.get(i);

                //设置顶部间距
                if (i == 0) {
                    top += mTitlePara;
                }

                //计算文字显示的起始点
                int start = (int) (mDisplayWidth - mTitlePaint.measureText(str)) / 2;
                //进行绘制
                canvas.drawText(str, start, top, mTitlePaint);

                //设置尾部间距
                if (i == mCurPage.titleLines - 1) {
                    top += titlePara;
                } else {
                    //行间距
                    top += titleInterval;
                }
            }
            if (mCurPage.lines.size() > 0) {
                //对内容进行绘制
                for (int i = mCurPage.titleLines; i < mCurPage.lines.size(); ++i) {
                    str = mCurPage.lines.get(i);
                    canvas.drawText(str, mMarginWidth, top, mTextPaint);
                    if (str.endsWith("\n")) {
                        top += para;
                    } else {
                        top += interval;
                    }
                }
                //if(mCurPage.lines.size()==0) {
//                   if(mPageView.getmPageAnim() instanceof ScrollPageAnim2){

//                   }
                //mPageChangeListener.onShowAdm(true);
                // }
                //else {
                // mPageChangeListener.onShowAdm(false);
                // }
            } else {
                canvas.drawBitmap(adm_bitmap, mDisplayWidth / 2 - adm_bitmap.getWidth() / 2, mVisibleHeight / 2 - adm_bitmap.getHeight() / 2, null);
                canvas.drawText("小说宝支持正版", mDisplayWidth / 2 - adm_bitmap.getWidth() / 2, mVisibleHeight / 2 - adm_bitmap.getHeight() + 20, mTitlePaint);
                //mPageChangeListener.onShowAdm(true);
            }
        }
    }

    void setDisplaySize(int w, int h) {
        //获取PageView的宽高
        mDisplayWidth = w;
        mDisplayHeight = h;

        //获取内容显示位置的大小
        mVisibleWidth = mDisplayWidth - mMarginWidth * 2;
        mVisibleHeight = mDisplayHeight - mMarginHeight * 2;

        //创建用来缓冲的 Bitmap
        mNextBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        //如果章节已显示，那么就重新计算页面
        if (mStatus == STATUS_FINISH) {
            mCurPageList = loadPageList(mCurChapterPos);
            //重新设置文章指针的位置
            mCurPage = getCurPage(mCurPage.position);
        }

        mPageView.drawCurPage(false);
    }

    //翻阅上一页
    boolean prev() {
        try {
        if (!checkStatus()) return false;
        //判断是否达到章节的起始点
        TxtPage prevPage = getPrevPage();
        if (prevPage == null) {
            //载入上一章。

            if (!prevChapter()) {
                return false;
            } else {
                mCancelPage = mCurPage;
                if(getPrevLastPage()!=null) {
                    mCurPage = getPrevLastPage();
                }
                mPageView.drawNextPage();
                return true;
            }
        }

        mCancelPage = mCurPage;
        mCurPage = prevPage;

        mPageView.drawNextPage();
        return true;
        }catch (Exception ex){
            return false;
        }
    }

    //加载上一章
    boolean prevChapter() {
        if (mCurChapterPos - 1 < 0) {
            ToastUtils.show("已经没有上一章了");
            //mPageChangeListener.closeAutoRead();
            return false;
        }

        //加载上一章数据
        int prevChapter = mCurChapterPos - 1;
        //当前章变成下一章
        mNextPageList = mCurPageList;

        //判断上一章缓存是否存在，如果存在则从缓存中获取数据。
//        if (mWeakPrePageList != null && mWeakPrePageList.get() != null) {
//            mCurPageList = mWeakPrePageList.get();
//            mWeakPrePageList = null;
//        }
//        //如果不存在则加载数据
//        else {
        mCurPageList = loadPageList(prevChapter);
//        }

        mLastChapter = mCurChapterPos;
        mCurChapterPos = prevChapter;

        if (mCurPageList != null) {
            mStatus = STATUS_FINISH;
        }
        //如果当前章不存在，则表示在加载中
        else {
            mStatus = STATUS_LOADING;
            //重置position的位置，防止正在加载的时候退出时候存储的位置为上一章的页码
            mCurPage.position = 0;
            mPageView.drawNextPage();
        }

        if (mPageChangeListener != null) {
            mPageChangeListener.onChapterChange(mCurChapterPos);
        }

        return true;
    }

    //翻阅下一页
    boolean next() {
        try {
        if (!checkStatus()) return false;
        //判断是否到最后一页了
        TxtPage nextPage = getNextPage();

        if (nextPage == null) {
            if (!nextChapter()) {
                return false;
            } else {
                //return nextChapter();
                mCancelPage = mCurPage;
                mCurPage = getCurPage(0);
                mPageView.drawNextPage();
                return true;
            }
        }
        mCancelPage = mCurPage;
        mCurPage = nextPage;
        mPageView.drawNextPage();

        //为下一页做缓冲

        //加载下一页的文章

        return true;

        }catch (Exception ex){
            return false;
        }
    }

    //缓存下一个要显示的页面
    //TODO:解决上下滑动卡顿问题
    private void cacheNextBitmap() {

    }
    public boolean is_website;
    boolean nextChapter() {
        try {
            if (mChapterList == null) {
                return false;
            }
            // post_adm();
            //加载一章
            if (mCurChapterPos + 1 >= mChapterList.size()) {
                ToastUtils.show("已经没有下一章了");
                mPageChangeListener.closeAutoRead();
                return false;
            }

            //如果存在下一章，则存储当前Page列表为上一章
            if (mCurPageList != null) {
                mWeakPrePageList = new WeakReference<List<TxtPage>>(new ArrayList<>(mCurPageList));
            }

            int nextChapter = mCurChapterPos + 1;
            //如果存在下一章预加载章节。
            if (mNextPageList != null) {
                mCurPageList = mNextPageList;
                mNextPageList = null;
            } else {
                //这个PageList可能为 null，可能会造成问题。
                mCurPageList = loadPageList(nextChapter);
            }

            mLastChapter = mCurChapterPos;
            mCurChapterPos = nextChapter;

            //如果存在当前章，预加载下一章
            if (mCurPageList != null&&is_website==false) {
                mStatus = STATUS_FINISH;
                preLoadNextChapter();
            }
            //如果当前章不存在，则表示在加载中
            else {
                //Log.e("WWW", "nextChapter: "+"   TTT");
                mStatus = STATUS_LOADING;
                //重置position的位置，防止正在加载的时候退出时候存储的位置为上一章的页码
                mCurPage.position = 0;
                mPageView.drawNextPage();
            }

            if (mPageChangeListener != null) {
                mPageChangeListener.onChapterChange(mCurChapterPos);
            }
            return true;
        }catch (Exception ex){
            return  false;
        }
    }

    //预加载下一章
    private void preLoadNextChapter() {
        if(mChapterList==null){
           return ;
        }
        //判断是否存在下一章
        if (mCurChapterPos + 1 >= mChapterList.size()) {
            return;
        }
        //判断下一章的文件是否存在
        int nextChapter = mCurChapterPos + 1;

        //如果之前正在加载则取消
        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }

        //调用异步进行预加载加载
        Observable.create(new ObservableOnSubscribe<List<TxtPage>>() {
            @Override
            public void subscribe(ObservableEmitter<List<TxtPage>> e) throws Exception {
                e.onNext(loadPageList(nextChapter));
            }
        }).compose(RxUtils::toSimpleSingle).subscribe(new Observer<List<TxtPage>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mPreLoadDisp = d;
            }

            @Override
            public void onNext(List<TxtPage> pages) {
                mNextPageList = pages;
            }


            @Override
            public void onError(Throwable e) {
                //无视错误
            }

            @Override
            public void onComplete() {

            }
        });

        //调用异步进行预加载加载
//        Single.create(new SingleOnSubscribe<List<TxtPage>>() {
//            @Override
//            public void subscribe(SingleEmitter<List<TxtPage>> e) throws Exception {
//                e.onSuccess(loadPageList(nextChapter));
//            }
//        }).compose(RxUtils::toSimpleSingle)
//                .subscribe(new SingleObserver<List<TxtPage>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        mPreLoadDisp = d;
//                    }
//
//                    @Override
//                    public void onSuccess(List<TxtPage> pages) {
//                        mNextPageList = pages;
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        //无视错误
//                    }
//                });
    }

    //取消翻页 (这个cancel有点歧义，指的是不需要看的页面)
    void pageCancel() {
        try {
            //加载到下一章取消了
            if (mCurPage.position == 0 && mCurChapterPos > mLastChapter) {
                prevChapter();
            }
            //加载上一章取消了 (可能有点小问题)
            else if (mCurPageList == null ||
                    (mCurPage.position == mCurPageList.size() - 1 && mCurChapterPos < mLastChapter)) {
                nextChapter();
            }
            //假设加载到下一页了，又取消了。那么需要重新装载的问题
            mCurPage = mCancelPage;
        }catch (Exception ex){

        }
    }

    /**
     * @return:获取初始显示的页面
     */
    TxtPage getCurPage(int pos) {
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos);
        }
        if (mCurPageList == null) {
            return new TxtPage();
        }
        return mCurPageList.get(pos);
    }


    /**************************************private method********************************************/

    /**
     * @return:获取上一个页面
     */
    private TxtPage getPrevPage() {
        if(mCurPage==null){
          return null;
        }
        int pos = mCurPage.position - 1;
        if (pos < 0) {
            return null;
        }
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos);
        }
        if(mCurPageList==null){
            return null;
        }
        return mCurPageList.get(pos);
    }

    /**
     * @return:获取下一的页面
     */
    private TxtPage getNextPage() {
        if (null != mCurPage&&mCurPageList!=null) {
            int pos = mCurPage.position + 1;
            if (pos > mCurPageList.size()) {
                return null;
            } else if (pos == mCurPageList.size()) {
                //mPageView.get.
                return null;
            }
            if (mPageChangeListener != null) {
                mPageChangeListener.onPageChange(pos);
            }
            position = pos;
            return mCurPageList.get(pos);
        }
        return new TxtPage();
    }

    int position;

    /**
     * @return:获取上一个章节的最后一页
     */
    private TxtPage getPrevLastPage() {
        if(mCurPageList==null){
            return null;
        }
        int pos = mCurPageList.size() - 1;
        return mCurPageList.get(pos);
    }

    /**
     * 检测当前状态是否能够进行加载章节数据
     *
     * @return
     */
    private boolean checkStatus() {
        if (mStatus == STATUS_LOADING) {
            ToastUtils.show("正在加载中，请稍等");
            return false;
        } else if (mStatus == STATUS_ERROR) {
            //点击重试
            mStatus = STATUS_LOADING;
            mPageView.drawCurPage(false);
            return false;
        }
        //由于解析失败，让其退出
        return true;
    }

    /*****************************************interface*****************************************/

    public interface OnPageChangeListener {
        void onChapterChange(int pos);

        void closeAutoRead();
        //请求加载回调
        void onLoadChapter(List<TxtChapter> chapters, int pos);

        //当目录加载完成的回调(必须要在创建的时候，就要存在了)
        void onCategoryFinish(List<TxtChapter> chapters);

        //页码改变
        void onPageCountChange(int count);

        //页面改变
        void onPageChange(int pos);

        //页面改变
        void onShowAdm(boolean isflag);
    }
}

