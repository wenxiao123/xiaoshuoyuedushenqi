package com.novel.collection.view.activity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bifan.txtreaderlib.bean.TxtChar;
import com.bifan.txtreaderlib.bean.TxtMsg;
import com.bifan.txtreaderlib.interfaces.ICenterAreaClickListener;
import com.bifan.txtreaderlib.interfaces.IChapter;
import com.bifan.txtreaderlib.interfaces.ILoadListener;
import com.bifan.txtreaderlib.interfaces.IPageChangeListener;
import com.bifan.txtreaderlib.interfaces.IPageEdgeListener;
import com.bifan.txtreaderlib.interfaces.ISliderListener;
import com.bifan.txtreaderlib.interfaces.ITextSelectListener;
import com.bifan.txtreaderlib.main.TxtConfig;
import com.bifan.txtreaderlib.main.TxtReaderView;
import com.bifan.txtreaderlib.ui.ChapterList;
import com.bifan.txtreaderlib.utils.ELogger;
import com.novel.collection.R;
import com.novel.collection.adapter.ChangeCategoryAdapter;
import com.novel.collection.constract.IReadContract;
import com.novel.collection.entity.bean.Categorys_one;
import com.novel.collection.entity.bean.Text;
import com.novel.collection.entity.data.BookmarkNovelDbData;
import com.novel.collection.entity.data.BookshelfNovelDbData;
import com.novel.collection.entity.data.DetailedChapterData;
import com.novel.collection.entity.epub.EpubData;
import com.novel.collection.entity.epub.OpfData;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.presenter.ReadPresenter;
import com.novel.collection.util.ScreenUtil;
import com.novel.collection.util.SpUtil;
import com.novel.collection.util.StatusBarUtil;
import com.novel.collection.util.ToastUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.novel.collection.view.activity.ReadActivity.KEY_CHPATER_ID;
import static com.novel.collection.view.activity.ReadActivity.KEY_NOVEL_URL;
import static com.novel.collection.view.activity.ReadActivity.KEY_TYPE;
import static com.novel.collection.view.activity.ReadActivity.KEY_ZJ_ID;

/**
 * Created by bifan-wei
 * on 2017/12/8.
 */

public class TxtPlayActivity extends AppCompatActivity {
    protected Handler mHandler;
    protected boolean FileExist = false;
    ReadPresenter presenter=new ReadPresenter(){
        @Override
        public void getChapterUrlListSuccess(List<String> chapterUrlList, List<String> chapterNameList) {

        }

        @Override
        public void getChapterUrlListError(String errorMsg) {

        }

        @Override
        public void getDetailedChapterDataSuccess(DetailedChapterData data) {
            //Log.e("QQQ", "getDetailedChapterDataSuccess: "+data.getContent());
            //TxtPlayActivity.loadStr(TxtPlayActivity.this,data.getContent());
           // TxtConfig.saveIsOnVerticalPageMode(TxtPlayActivity.this,false);
            //TxtPlayActivity.loadTxtStr(TxtPlayActivity.this, data.getContent(),"aa");
//            mTxtReaderView.loadText(data.getContent().replace("&nbsp"," ").replace("</br>","\n"), new ILoadListener() {
//                @Override
//                public void onSuccess() {
//
//                }
//
//                @Override
//                public void onFail(TxtMsg txtMsg) {
//
//                }
//
//                @Override
//                public void onMessage(String message) {
//
//                }
//            });
            weigh=Integer.parseInt(data.getWeigh());
            ContentStr=data.getContent().replace("&nbsp"," ").replace("</br>","\n");
            loadStr();
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
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucentStatus(this);
        setContentView(getContentViewLayout());
        FileExist = getIntentData();
        init();
        loadFile();
        registerListener();
    }

    protected int getContentViewLayout() {
        return R.layout.activity_txtpaly;
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
        mType = getIntent().getIntExtra(KEY_TYPE, 0);
        chpter_id = getIntent().getIntExtra(KEY_CHPATER_ID, 1);
        zj_id = getIntent().getStringExtra(KEY_ZJ_ID);
        first_read = getIntent().getIntExtra("first_read", 0);
        mNovelUrl = getIntent().getStringExtra(KEY_NOVEL_URL);
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
    int mType;
    protected void init() {
        iv_read_day_and_night_mode=findViewById(R.id.iv_read_day_and_night_mode);
        iv_read_day_and_night_mode.setOnClickListener(new StyleChangeClickListener(ContextCompat.getColor(this, R.color.hwtxtreader_styleclor4), StyleTextColors[3]));
        mHandler = new Handler();
        mChapterMsgView = findViewById(R.id.activity_hwtxtplay_chapter_msg);
        mChapterMsgName = (TextView) findViewById(R.id.chapter_name);
        mChapterMsgProgress = (TextView) findViewById(R.id.charpter_progress);
        mTopDecoration = findViewById(R.id.activity_hwtxtplay_top);
        mBottomDecoration = findViewById(R.id.activity_hwtxtplay_bottom);
        tv_website = findViewById(R.id.tv_website);
        tv_website.setText(UrlObtainer.GetUrl());
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

        mMenuHolder.mTitle =  findViewById(R.id.iv_read_menu);
        mMenuHolder.mPreChapter = (TextView) findViewById(R.id.txtreadr_menu_chapter_pre);
        mMenuHolder.mNextChapter = (TextView) findViewById(R.id.txtreadr_menu_chapter_next);
        mMenuHolder.mSeekBar = (SeekBar) findViewById(R.id.txtreadr_menu_seekbar);
        mMenuHolder.mTextSizeDel = findViewById(R.id.txtreadr_menu_textsize_del);
        mMenuHolder.mTextSize = (TextView) findViewById(R.id.txtreadr_menu_textsize);
        mMenuHolder.mTextSizeAdd = findViewById(R.id.txtreadr_menu_textsize_add);

        mMenuHolder.mTextCowDel = findViewById(R.id.iv_read_decrease_row_space);
        mMenuHolder.mTextCow = (TextView) findViewById(R.id.tv_jainju);
        mMenuHolder.mTextCowAdd = findViewById(R.id.iv_read_increase_row_space);

        //mMenuHolder.mBoldSelectedLayout = findViewById(R.id.txtreadr_menu_textsetting1_bold);
        //mMenuHolder.mNormalSelectedLayout = findViewById(R.id.txtreadr_menu_textsetting1_normal);
        mMenuHolder.mCoverSelectedLayout = findViewById(R.id.txtreadr_menu_textsetting2_cover);
        //mMenuHolder.mShearSelectedLayout = findViewById(R.id.txtreadr_menu_textsetting2_shear);
        mMenuHolder.mTranslateSelectedLayout = findViewById(R.id.txtreadr_menu_textsetting2_translate);
        mSettingBarCv = findViewById(R.id.cv_read_setting_bar);
        tv_textstyle = findViewById(R.id.tv_textstyle);
        mMenuHolder.mStyle1 = findViewById(R.id.hwtxtreader_menu_style1);
        mMenuHolder.mStyle2 = findViewById(R.id.hwtxtreader_menu_style2);
        mMenuHolder.mStyle3 = findViewById(R.id.hwtxtreader_menu_style3);
        mMenuHolder.mStyle4 = findViewById(R.id.hwtxtreader_menu_style4);
        mMenuHolder.mStyle5 = findViewById(R.id.hwtxtreader_menu_style5);
        mBrightnessProcessSb = findViewById(R.id.sb_read_brightness_bar_brightness_progress);
        mBrightnessProcessSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!mIsSystemBrightness) {
                    // 调整亮度
                    mBrightness = (float) progress / 100;
                    ScreenUtil.setWindowBrightness(TxtPlayActivity.this, mBrightness);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sb_auto_read_progress = findViewById(R.id.sb_auto_read_progress);
        sb_auto_read_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pro = progress;
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
        mSys_light = findViewById(R.id.sys_ligin);
        mSys_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChecked) {
                    // 变为系统亮度
                    mIsSystemBrightness = true;
                    mBrightness = -1f;
                    // 将屏幕亮度设置为系统亮度
                    ScreenUtil.setWindowBrightness(TxtPlayActivity.this,
                            (float) ScreenUtil.getSystemBrightness() / ScreenUtil.getBrightnessMax());
                    //mSys_light.setBackground(getResources().getDrawable(R.drawable.bachground_red));
                    sys_select.setImageResource(R.mipmap.sys_selected);
                    isChecked = false;
                } else {
                    // 变为自定义亮度
                    mIsSystemBrightness = false;
                    // 将屏幕亮度设置为自定义亮度
                    mBrightness = (float) mBrightnessProcessSb.getProgress() / 100;
                    ScreenUtil.setWindowBrightness(TxtPlayActivity.this, mBrightness);
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
                    ScreenUtil.setWindowBrightness(TxtPlayActivity.this,
                            (float) ScreenUtil.getSystemBrightness() / ScreenUtil.getBrightnessMax());
                    //mSys_light.setBackground(getResources().getDrawable(R.drawable.bachground_red));
                    sys_select.setImageResource(R.mipmap.sys_selected);
                    isChecked = false;
                } else {
                    // 变为自定义亮度
                    mIsSystemBrightness = false;
                    // 将屏幕亮度设置为自定义亮度
                    mBrightness = (float) mBrightnessProcessSb.getProgress() / 100;
                    ScreenUtil.setWindowBrightness(TxtPlayActivity.this, mBrightness);
                    //mSys_light.setBackground(getResources().getDrawable(R.drawable.bachground_cricyle));
                    sys_select.setImageResource(R.mipmap.sys_select);
                    isChecked = true;
                }
            }
        });
        mMenuHolder.mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPupowindpw(mMenuHolder.mTitle);
            }
        });
        ImageView  iv_read_setting=findViewById(R.id.iv_read_setting);
        iv_read_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gone(mTopMenu, mBottomMenu, mCoverView, mChapterMsgView);
                showSettingBar();
            }
        });
        tv_autoread = findViewById(R.id.tv_autoread);
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
    @SuppressLint("WrongConstant")
    boolean is_othersite = false, is_all_one = false;
    ChangeCategoryAdapter changeCategoryAdapter;
    private TextView tv_nodata;
    private View s_line, m_line;
    private TextView tvCatalog, mBookMark;
    ProgressBar progressBar;
    List<Categorys_one> categorys_ones = new ArrayList<>();
    List<Text> other_website = new ArrayList<>();
    String weight;
    TextView tv_website;
    RecyclerView rv_catalog_list;
    Gson mGson = new Gson();

    private void getCategorys(String id) {
        String url = UrlObtainer.GetUrl() + "/api/index/hua_book";
        RequestBody requestBody = new FormBody.Builder()
                .add("novel_id", id)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                // Log.e("QQQ", "onResponse: " + json);
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
                            // Log.e("qqq", "onResponse: " + categorys_ones.size());
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

    public void getCategorysError() {
        progressBar.setVisibility(View.GONE);
        tv_nodata.setVisibility(View.VISIBLE);
        rv_catalog_list.setVisibility(View.GONE);
        ToastUtil.showToast(this,"请求失败");
    }
    public void getCategorysSuccess(List<Categorys_one> categorys_one) {
        progressBar.setVisibility(View.GONE);
        if (categorys_one.size() == 0) {
            //Log.e("QQQ1", "getCategorysSuccess: " + tv_nodata.getVisibility());
            tv_nodata.setVisibility(View.VISIBLE);
            rv_catalog_list.setVisibility(View.GONE);
        } else {
            tv_nodata.setVisibility(View.GONE);
            rv_catalog_list.setVisibility(View.VISIBLE);
            this.categorys_ones = categorys_one;
            changeCategoryAdapter = new ChangeCategoryAdapter(this, categorys_ones);
            changeCategoryAdapter.setOnHistoryAdapterListener(new ChangeCategoryAdapter.HistoryAdapterListener() {
                @Override
                public void clickWord(int word) {
                    if (categorys_ones.get(word).getText() == null) {
                        ToastUtil.showToast(TxtPlayActivity.this,"数据源错误！");
                    } else {
//                        mPageView.clear();              // 清除当前文字
//                        mStateTv.setVisibility(View.VISIBLE);
//                        mStateTv.setText(LOADING_TEXT);
                        title = "";
                        content = "";
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        if (is_all_one == false) {
                            is_othersite = true;
                        } else {
                            is_othersite = false;
                        }
                        //  Log.e("WWW", "clickWord: " + categorys_ones.get(word));
                        tv_website.setText(categorys_ones.get(word).getUrl());
                        other_website = categorys_ones.get(word).getText();
                        String href = other_website.get(Integer.parseInt(weight) - 1).getChapter_url();
//                weigh = Integer.parseInt(categorys_ones.get(word).getChapter_sum());
//                mNovelTitleTv1.setText(mChapterIndex+"/"+weigh);
                        reurl = categorys_ones.get(word).getElement();
                        Message message=new Message();
                        message.what=2;
                        message.obj=href;
                        handler1.sendMessage(message);
                    }
                }
            });
            rv_catalog_list.setAdapter(changeCategoryAdapter);
        }
    }
    String mNovelUrl;
    PopupWindow popupWindow;
    private void showPupowindpwChangeWebSite() {
        if (mType == 1) {
            ToastUtil.showToast(this,"本地缓存小说不支持换源功能");
            return;
        }
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popu_changewebsite, null);
        is_all_one = false;
        tv_nodata = view.findViewById(R.id.tv_nodata);
        s_line = view.findViewById(R.id.s_line);
        m_line = view.findViewById(R.id.m_line);
        tvCatalog = view.findViewById(R.id.tv_mulu);
        tvCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        progressBar = view.findViewById(R.id.pb_over);
        mBookMark = view.findViewById(R.id.tv_book_mark);
        mBookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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
        getCategorys(mNovelUrl);
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
    void starttime() {
        exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                z = z + 10;
//                if (chapter.getStartParagraphIndex() <= chapter.getEndCharIndex() - 100) {
//                    //String content = mNovelContent.replace("</br>", "\n").replace("&nbsp", " ");
//                    //mPageView.initDrawText(content, mPageView.getPosition() + z);
//                    mTxtReaderView.loadFromProgress(chapter.getStartParagraphIndex()+z, 0);
//                } else {
//                    handler1.sendEmptyMessage(1);
//                }
//                int charNum = readerContext.getParagraphData().getCharNum();
//                int charIndex = (int) (p * charNum);
//                int paragraphNum = readerContext.getParagraphData().getParagraphNum();
//                int paragraphIndex = readerContext.getParagraphData().findParagraphIndexByCharIndex(charIndex);

                mTxtReaderView.loadFromProgress(z,0);
            }
        }, 1000, read_speed, TimeUnit.MILLISECONDS);
        //Log.e("QQQ", "starttime: "+read_speed);

    }

    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1) {
                stopTime();
//                if (mType == 1) {
//                    showShortToast("你已看完小说");
//                } else {
//                    mChapterIndex++;
//                    showChapter();
//                }
            }else if(msg.what==2){
//                String href= (String) msg.obj;
//                new Thread(new ReadActivity.sendValueToServer(href, weight + "", reurl)).start();
            }
        }
    };

    void stopTime() {
        if (exec != null) {
            exec.shutdownNow();
            exec = null;
        }
    }
    int pro = 66;
    ScheduledThreadPoolExecutor exec;
    int z = 0;
    private ImageView tv_autoread;
    boolean is_autoRead = false;
    int read_speed = 3000;
    private ImageView sys_select;
    boolean isChecked = false;
    private TextView mSys_light;
    float mBrightness=70f;
    private boolean mIsSystemBrightness = true;     // 是否为系统亮度
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
        LayoutInflater inflater = LayoutInflater.from(TxtPlayActivity.this);
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
//

        if (mType == 0) {
            // 先通过小说 url 获取所有章节 url 信息
            //mPresenter.getChapterList(mid);
            if (first_read == 0) {
                presenter.getDetailedChapterData(zj_id + "");
            } else if (first_read == 1) {
                presenter.getDetailedChapterData(mNovelUrl + "", 1 + "");
            } else if (first_read == 2) {
              //  Log.e("QQQ", "getIntentData: "+mNovelUrl + ""+chpter_id);
                presenter.getDetailedChapterData(mNovelUrl + "", chpter_id + "");
            }
        }
        else if (mType == 1) {
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
            // 通过 FilePath 读取本地小说
            //Log.e("QQQ", "doAfterInit: "+adress);
            //presenter.loadTxt(adress);
        }
        else if (mType == 2) {
            // 先根据 filePath 获得 OpfData
            presenter.getOpfData(mNovelUrl);
        }
    }
    private int chpter_id,weigh;
    String zj_id;
    private int first_read;
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
//        mMenuHolder.mTextSize.setText(mTxtReaderView.getTextSize() + "");
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
        if (pageSwitchMode==TxtConfig.PAGE_SWITCH_MODE_SERIAL) {
            mTxtReaderView.setPageSwitchByTranslate();
        } else if (pageSwitchMode==TxtConfig.PAGE_SWITCH_MODE_COVER){
            mTxtReaderView.setPageSwitchByCover();
        }else if (pageSwitchMode==TxtConfig.PAGE_SWITCH_MODE_SHEAR){
            mTxtReaderView.setPageSwitchByShear();
        }
        //章节初始化
        if (mTxtReaderView.getChapters() != null && mTxtReaderView.getChapters().size() > 0) {
            WindowManager m = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            m.getDefaultDisplay().getMetrics(metrics);
            int ViewHeight = metrics.heightPixels - mTopDecoration.getHeight();
            mChapterListPop = new ChapterList(this, ViewHeight, mTxtReaderView.getChapters(), mTxtReaderView.getTxtReaderContext().getParagraphData().getCharNum());
            chapter=(IChapter) mChapterListPop.getAdapter().getItem(0);
            mChapterListPop.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    chapter = (IChapter) mChapterListPop.getAdapter().getItem(i);
                    mChapterListPop.dismiss();
                    mTxtReaderView.loadFromProgress(chapter.getStartParagraphIndex(), 0);
                }
            });
            mChapterListPop.setBackGroundColor(mTxtReaderView.getBackgroundColor());
        } else {
            Gone(mChapterMenuText);
        }
    }
    IChapter chapter;
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
        //setPageEdgeListener();
        //setOnTextSelectListener();
        setStyleChangeListener();
        setExtraListener();
    }

    private void setExtraListener() {
        mMenuHolder.mPreChapter.setOnClickListener(new ChapterChangeClickListener(true));
        mMenuHolder.mNextChapter.setOnClickListener(new ChapterChangeClickListener(false));
        mMenuHolder.mTextSizeAdd.setOnClickListener(new TextChangeClickListener(true));
        mMenuHolder.mTextSizeDel.setOnClickListener(new TextChangeClickListener(false));
        mMenuHolder.mTextCowAdd.setOnClickListener(new TextChangeClickListener(true));
        mMenuHolder.mTextCowDel.setOnClickListener(new TextChangeClickListener(false));
        //mMenuHolder.mBoldSelectedLayout.setOnClickListener(new TextSettingClickListener(true));
//        mMenuHolder.mNormalSelectedLayout.setOnClickListener(new TextSettingClickListener(false));
        mMenuHolder.mTranslateSelectedLayout.setOnClickListener(new SwitchSettingClickListener(TxtConfig.PAGE_SWITCH_MODE_SERIAL));
        mMenuHolder.mCoverSelectedLayout.setOnClickListener(new SwitchSettingClickListener(TxtConfig.PAGE_SWITCH_MODE_COVER));
        //mMenuHolder.mShearSelectedLayout.setOnClickListener(new SwitchSettingClickListener(TxtConfig.PAGE_SWITCH_MODE_SHEAR));
    }
    private SeekBar mBrightnessProcessSb,sb_auto_read_progress;
    protected void setStyleChangeListener() {
        mMenuHolder.mStyle1.setScaleX(1.2f);
        mMenuHolder.mStyle1.setScaleY(1.2f);
        mMenuHolder.mStyle1.setSelected(true);
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
                    if(name==null) {
                        mChapterNameText.setText((currentChapter.getTitle() + "").trim());
                    }else {
                        mChapterNameText.setText((currentChapter.getTitle() + "/"+name).trim());
                    }
                    title=currentChapter.getTitle();
                } else {
                    mChapterNameText.setText("无章节");
                }
            }

            @Override
            public void nextPage() {

                weigh++;
                presenter.getDetailedChapterData(mNovelUrl, weigh+"");
            }

            @Override
            public void prePage() {

                weigh++;
                presenter.getDetailedChapterData(mNovelUrl, weigh+"");
            }
        });
    }

//    protected void setPageEdgeListener() {
//        mTxtReaderView.setOnPageEdgeListener(new IPageEdgeListener() {
//            @Override
//            public void onCurrentFirstPage() {
//                Log.e("QQQ", "onCurrentFirstPage: "+mTxtReaderView.isFirstPage());
////                weigh--;
////                presenter.getDetailedChapterData(mNovelUrl + "", weigh + "");
//            }
//
//            @Override
//            public void onCurrentLastPage() {
//                Log.e("QQQ", "onCurrentLastPage: "+mTxtReaderView.isLastPage());
////                weigh++;
////                presenter.getDetailedChapterData(mNovelUrl + "", weigh + "");
//            }
//        });
//    }
    String title,name,content,reurl;
    protected void setCenterClickListener() {
        mTxtReaderView.setOnCenterAreaClickListener(new ICenterAreaClickListener() {
            @Override
            public boolean onCenterClick(float widthPercentInView) {
                mSettingText.performClick();
                if (mSettingBarCv.getVisibility() == View.VISIBLE) {
                    hideSettingBar();
                }
                return true;
            }

            @Override
            public boolean onOutSideCenterClick(float widthPercentInView) {
                if (mBottomMenu.getVisibility() == View.VISIBLE) {
                    mSettingText.performClick();
                    return true;
                }
                if (mSettingBarCv.getVisibility() == View.VISIBLE) {
                    hideSettingBar();
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
            //mMenuHolder.mBoldSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_selected);
//            mMenuHolder.mNormalSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
        } else {
            //mMenuHolder.mBoldSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
           // mMenuHolder.mNormalSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_selected);
        }
    }

    private void onPageSwitchSettingUi(int pageSwitchMode) {
        if (pageSwitchMode==TxtConfig.PAGE_SWITCH_MODE_SERIAL) {
            mMenuHolder.mTranslateSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_selected);
            mMenuHolder.mCoverSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
            //mMenuHolder.mShearSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
        } else if (pageSwitchMode==TxtConfig.PAGE_SWITCH_MODE_COVER){
            mMenuHolder.mTranslateSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
            mMenuHolder.mCoverSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_selected);
            // mMenuHolder.mShearSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
        }else if (pageSwitchMode==TxtConfig.PAGE_SWITCH_MODE_SHEAR){
            mMenuHolder.mTranslateSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
            mMenuHolder.mCoverSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_unselected);
            //mMenuHolder.mShearSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_selected);
        }
    }
    private String mStyle = "";         // 字体样式
    private LinearLayout mSettingBarCv;
    private TextView tv_textstyle;
    /**
     * 显示设置栏
     */
    private void showSettingBar() {
        mStyle = SpUtil.getTextStyle();
        Typeface tf = null;
        AssetManager mgr = getAssets();
        if(mStyle.equals("1")) {
            tf = Typeface.createFromAsset(mgr, "font/方正卡通简体.ttf");
            tv_textstyle.setTypeface(tf);
            tv_textstyle.setText("方正卡通简体");
        }else if(mStyle.equals("2")){
            tf = Typeface.createFromAsset(mgr, "font/方正楷体.ttf");
            tv_textstyle.setTypeface(tf);
            tv_textstyle.setText("方正楷体");
        }else if(mStyle.equals("3")){
            tf = Typeface.createFromAsset(mgr, "font/流行体简体.ttf");
            tv_textstyle.setTypeface(tf);
            tv_textstyle.setText("流行体简体");
        }else {
            tf=Typeface.create("sans-serif-medium",Typeface.NORMAL);
            tv_textstyle.setTypeface(tf);
            tv_textstyle.setText("系统字体");
        }
        //mIsShowSettingBar = true;
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
                //mIsShowSettingBar = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSettingBarCv.startAnimation(bottomExitAnim);
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
                if (pageSwitchMode==TxtConfig.PAGE_SWITCH_MODE_COVER) {
                    mTxtReaderView.setPageSwitchByCover();
                } else   if (pageSwitchMode==TxtConfig.PAGE_SWITCH_MODE_SERIAL){
                    mTxtReaderView.setPageSwitchByTranslate();
                }  if (pageSwitchMode==TxtConfig.PAGE_SWITCH_MODE_SHEAR){
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
                int textSize = mTxtReaderView.getCowSize();
                if (Add) {
                    if (textSize + 2 <= TxtConfig.MAX_COW_SIZE) {
                        mTxtReaderView.setCowSize(textSize + 2);
                        mMenuHolder.mTextCow.setText(textSize + 2 + "");
                    }
                } else {
                    if (textSize - 2 >= TxtConfig.MIN_COW_SIZE) {
                        mTxtReaderView.setCowSize(textSize - 2);
                        mMenuHolder.mTextCow.setText(textSize - 2 + "");
                    }
                }
            }
        }
    }

    private class CowChangeClickListener implements View.OnClickListener {
        private Boolean Add;

        public CowChangeClickListener(Boolean pre) {
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
    ImageView iv_read_day_and_night_mode;
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
                mMenuHolder.mStyle1.setSelected(false);
                mMenuHolder.mStyle1.setScaleY(1F);
                mMenuHolder.mStyle1.setScaleX(1F);
                mMenuHolder.mStyle2.setSelected(false);
                mMenuHolder.mStyle2.setScaleY(1F);
                mMenuHolder.mStyle2.setScaleX(1F);
                mMenuHolder.mStyle3.setSelected(false);
                mMenuHolder.mStyle3.setScaleY(1F);
                mMenuHolder.mStyle3.setScaleX(1F);
                mMenuHolder.mStyle4.setSelected(false);
                mMenuHolder.mStyle4.setScaleY(1F);
                mMenuHolder.mStyle4.setScaleX(1F);
                mMenuHolder.mStyle5.setSelected(false);
                mMenuHolder.mStyle5.setScaleY(1F);
                mMenuHolder.mStyle5.setScaleX(1F);
                view.setScaleX(1.2f);
                view.setScaleY(1.2f);
                view.setSelected(true);
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
        this.name=name;
        if(title==null) {
            mChapterNameText.setText(name + "");
        }else {
            mChapterNameText.setText(title + "/"+name);
        }
        //mMenuHolder.mTitle.setText(name + "");
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
        public ImageView mTitle;
        public TextView mPreChapter;
        public TextView mNextChapter;
        public SeekBar mSeekBar;
        public TextView mTextSizeDel,mTextCow;
        public TextView mTextSizeAdd;
        public ImageView mTextCowDel;
        public ImageView mTextCowAdd;
        public TextView mTextSize;
        public View mBoldSelectedLayout;
        public View mNormalSelectedLayout;
        public TextView mCoverSelectedLayout;
        public TextView mShearSelectedLayout;
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
