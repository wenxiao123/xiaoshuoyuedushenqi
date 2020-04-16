package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

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
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
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
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookmarkNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by bifan-wei
 * on 2017/12/8.
 */

public class TxtPlayActivity extends AppCompatActivity {
    protected Handler mHandler;
    protected boolean FileExist = false;

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
        });
    }
    String title,name;
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
            mMenuHolder.mNormalSelectedLayout.setBackgroundResource(R.drawable.shape_menu_textsetting_selected);
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
