package com.novel.collection.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.novel.collection.adapter.CatalogNameAdapter;
import com.novel.collection.entity.eventbus.HoldReadActivityEvent2;
import com.novel.collection.util.StatusBarUtil;
import com.novel.collection.weyue.db.entity.CollBookBean;
import com.novel.collection.weyue.utils.Charset;
import com.novel.collection.weyue.utils.FileUtils;
import com.novel.collection.weyue.utils.IOUtils;
import com.novel.collection.weyue.widget.page.TxtChapter;
import com.novel.collection.widget.TipDialog;
import com.google.android.material.tabs.TabLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bifan.txtreaderlib.interfaces.IParagraphData;
import com.bifan.txtreaderlib.main.ParagraphData;
import com.novel.collection.R;
import com.novel.collection.adapter.BookMarkAdapter;
import com.novel.collection.adapter.CatalogAdapter;
import com.novel.collection.base.BaseActivity;
import com.novel.collection.constant.Constant;
import com.novel.collection.constant.EventBusCode;
import com.novel.collection.constract.ICatalogContract;
import com.novel.collection.db.DatabaseManager;
import com.novel.collection.entity.bean.Cataloginfo;
import com.novel.collection.entity.bean.Chapter;
import com.novel.collection.entity.data.BookmarkNovelDbData;
import com.novel.collection.entity.eventbus.Event;
import com.novel.collection.entity.eventbus.HoldReadActivityEvent;
import com.novel.collection.interfaces.IChapter;
import com.novel.collection.presenter.CatalogPresenter;
import com.novel.collection.util.EnhanceTabLayout;
import com.novel.collection.util.NetUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.novel.collection.app.App.getContext;

//本地书籍目录

public class LocalCatalogActivity extends BaseActivity<CatalogPresenter>
        implements ICatalogContract.View, View.OnClickListener {
    private static final String TAG = "LocalCatalogActivity";
    private static final String ORDER_POSITIVE = "↑正序";
    private static final String ORDER_REVERSE = "↓倒序";
    public static final String KEY_URL = "file_path";
    public static final String KEY_ID = "key_id";
    public static final String KEY_NAME = "catalog_key_name";
    public static final String KEY_COVER = "catalog_key_cover";
    public static final String KEY_POSTION = "catalog_key_postion";
    private ImageView mBackIv;
    private TextView mRefreshIv, mCatalog, mBookMark;
    private View s_line, m_line;
    private TextView mChapterCountTv, mText;
    private RecyclerView mCatalogListRv, mBookMarkListRv;
    private ProgressBar mProgressBar;
    private TextView mErrorPageTv;
    private CatalogNameAdapter mCatalogAdapter;
    private BookMarkAdapter bookAdapter;
    private String file_path, mName, mCover,pid;
    private int mPosition;
    private ImageView shuaxiniv, paixuiv;
    private DatabaseManager mDbManager;
    /*
     * 如果是在 ReadActivity 通过点击目录跳转过来，那么持有该 ReadActivity 的引用，
     * 之后如果跳转到新的章节时，利用该引用结束旧的 ReadActivity
     */
    private WYReadActivity mReadActivity;

    private List<String> mChapterNameList = new ArrayList<>();
    private List<BookmarkNovelDbData> bookmarkNovelDbDatas = new ArrayList<>();
    private List<String> mChapterUrlList = new ArrayList<>();

    private boolean mIsReverse = false;     // 是否倒序显示章节
    private boolean mIsReversing = false;   // 是否正在倒置，正在倒置时倒置操作无效
    private boolean mIsRefreshing = true;

    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
        mDbManager = DatabaseManager.getInstance();
    }

    int count = 0;
    int z = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //findContents(mParagraphList);
            try {
                mIsRefreshing = false;
                mProgressBar.setVisibility(View.GONE);
                initAdapter();
                mChapterCountTv.setText("共" + txtChapters.size() + "章");
                mCatalogListRv.setAdapter(mCatalogAdapter);
                for (int j = 0; j < longs.size(); j++) {
                    if (mPosition < (int) longs.get(0)) {
                        z = 0;
                        break;
                    } else if (mPosition < (int) longs.get(j)) {
                        z = j - 1;
                        break;
                    }
                }
                mCatalogAdapter.setPosition(z);
                mCatalogAdapter.notifyDataSetChanged();
                mCatalogListRv.scrollToPosition(z);
            }catch (Exception ex){

            }
        }
    };
    String[]  sTitle={"目录","书签"};
    @Override
    protected int getLayoutId() {
        return R.layout.activity_catalog;
    }

    @Override
    protected CatalogPresenter getPresenter() {
        return new CatalogPresenter();
    }
    List<TxtChapter> txtChapters;
    @Override
    protected void initData() {
        file_path = getIntent().getStringExtra(KEY_URL);
        mName = getIntent().getStringExtra(KEY_NAME);
        mCover = getIntent().getStringExtra(KEY_COVER);
        activity_type = getIntent().getStringExtra("ACTIVITY_TYPE");
        pid=getIntent().getStringExtra(KEY_ID);
        chapter_id = getIntent().getIntExtra("chapter_id", 0);
        mPosition = getIntent().getIntExtra(KEY_POSTION, 0);
        queryBookMarks(pid);
        mCharset = FileUtils.getCharset(file_path);
        //txtChapters = (List<TxtChapter>) getIntent().getSerializableExtra("MSPANSCOMMIT");
    }
    private RefreshLayout mRefreshSrv;
    @Override
    protected void initView() {
        mRefreshSrv = findViewById(R.id.srv_male_refresh);
        mRefreshSrv.setEnableRefresh(false);
        mBackIv = findViewById(R.id.iv_catalog_back);
        mBackIv.setOnClickListener(this);
        mCatalog = findViewById(R.id.tv_mulu);
        mCatalog.setOnClickListener(this);

        mBookMark = findViewById(R.id.tv_book_mark);
        mBookMark.setOnClickListener(this);

        s_line = findViewById(R.id.s_line);
        m_line = findViewById(R.id.m_line);

        shuaxiniv = findViewById(R.id.shuaxin);
        shuaxiniv.setOnClickListener(this);

        paixuiv = findViewById(R.id.paixu);
        paixuiv.setOnClickListener(this);

        mChapterCountTv = findViewById(R.id.tv_catalog_chapter_count);
        mText = findViewById(R.id.title);
        mText.setText(mName.replace(".txt", ""));
        mCatalogListRv = findViewById(R.id.rv_catalog_list);
        mCatalogListRv.setLayoutManager(new LinearLayoutManager(this));

        mBookMarkListRv = findViewById(R.id.rv_book_mark_list);
        mBookMarkListRv.setLayoutManager(new LinearLayoutManager(this));

        initBookMarkAdapter();
        mBookMarkListRv.setAdapter(bookAdapter);

        mProgressBar = findViewById(R.id.pb_catalog);

        mErrorPageTv = findViewById(R.id.tv_catalog_error_page);

        EnhanceTabLayout mEnhanceTabLayout = findViewById(R.id.enhance_tab_layout);
        mEnhanceTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0) {
                    mCatalogListRv.setVisibility(View.VISIBLE);
                    mBookMarkListRv.setVisibility(View.GONE);
                }else {
                    mCatalogListRv.setVisibility(View.GONE);
                    mBookMarkListRv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        for(int i=0;i<sTitle.length;i++){
            mEnhanceTabLayout.addTab(sTitle[i]);
        }
        mEnhanceTabLayout.getmTabLayout().getTabAt(0).select();
    }

    List longs = new ArrayList<>();
    int leng = 0;
    private static final String ChapterPatternStr = "(^.{0,3}\\s*第)(.{0,9})[章节卷集部篇回](\\s*)";
    // "序(章)|前言"
    private final static Pattern mPreChapterPattern = Pattern.compile("^(\\s{0,10})((\u5e8f[\u7ae0\u8a00]?)|(\u524d\u8a00)|(\u6954\u5b50))(\\s{0,10})$", Pattern.MULTILINE);

    //正则表达式章节匹配模式
    // "(第)([0-9零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,10})([章节回集卷])(.*)"
    private static final String[] CHAPTER_PATTERNS = new String[]{"^(.{0,8})(\u7b2c)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\u7ae0\u8282\u56de\u96c6\u5377])(.{0,30})$",
            "^(\\s{0,4})([\\(\u3010\u300a]?(\u5377)?)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\\.:\uff1a\u0020\f\t])(.{0,30})$",
            "^(\\s{0,4})([\\(\uff08\u3010\u300a])(.{0,30})([\\)\uff09\u3011\u300b])(\\s{0,2})$",
            "^(\\s{0,4})(\u6b63\u6587)(.{0,20})$",
            "(^.{0,3}\\s*第)(.{0,9})[章节卷集部篇回](\\s*)",
            "^(.{0,4})(Chapter|chapter)(\\s{0,4})([0-9]{1,4})(.{0,30})$"};

    private Boolean ReadData(String filePath, IParagraphData paragraphData, List<Chapter> chapters) {
        txtChapters.clear();
        if(filePath==null){
           return false;
        }
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
    private final static int BUFFER_SIZE = 512 * 1024;
    private final static int MAX_LENGTH_WITH_NO_CHAPTER = 10 * 1024;
    private void queryBookMarks(String bookname) {
        bookmarkNovelDbDatas = mDbManager.queryAllBookmarkNovel(bookname);
    }
    private Pattern mChapterPattern = null;
    private Charset mCharset;
    /**
     * 1. 检查文件中是否存在章节名
     * 2. 判断文件中使用的章节名类型的正则表达式
     *
     * @return 是否存在章节名
     */
    private boolean checkChapterType(RandomAccessFile bookStream) throws IOException {
        //首先获取128k的数据
        byte[] buffer = new byte[BUFFER_SIZE / 4];
        int length = bookStream.read(buffer, 0, buffer.length);
        //进行章节匹配
        for (String str : CHAPTER_PATTERNS) {
            Pattern pattern = Pattern.compile(str, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(new String(buffer, 0, length, mCharset.getName()));
            //如果匹配存在，那么就表示当前章节使用这种匹配方式
            if (matcher.find()) {
                mChapterPattern = pattern;
                //重置指针位置
                bookStream.seek(0);
                return true;
            }
        }

        //重置指针位置
        bookStream.seek(0);
        return false;
    }
//    /**
//     * 未完成的部分:
//     * 1. 序章的添加
//     * 2. 章节存在的书本的虚拟分章效果
//     *
//     * @throws IOException
//     */
//    private void loadChapters() throws IOException {
//        List<TxtChapter> chapters = new ArrayList<>();
//        //获取文件流
//        RandomAccessFile bookStream = new RandomAccessFile(file_path, "r");
//        //寻找匹配文章标题的正则表达式，判断是否存在章节名
//        boolean hasChapter = checkChapterType(bookStream);
//        //加载章节
//        byte[] buffer = new byte[BUFFER_SIZE];
//        //获取到的块起始点，在文件中的位置
//        long curOffset = 0;
//        //block的个数
//        int blockPos = 0;
//        //读取的长度
//        int length;
//
//        //获取文件中的数据到buffer，直到没有数据为止
//        while ((length = bookStream.read(buffer, 0, buffer.length)) > 0) {
//            ++blockPos;
//            //如果存在Chapter
//            if (hasChapter) {
//                //将数据转换成String
//                String blockContent = new String(buffer, 0, length, mCharset.getName());
//                //当前Block下使过的String的指针
//                int seekPos = 0;
//                //进行正则匹配
//                Matcher matcher = mChapterPattern.matcher(blockContent);
//                //如果存在相应章节
//                while (matcher.find()) {
//                    //获取匹配到的字符在字符串中的起始位置
//                    int chapterStart = matcher.start();
//
//                    //如果 seekPos == 0 && nextChapterPos != 0 表示当前block处前面有一段内容
//                    //第一种情况一定是序章 第二种情况可能是上一个章节的内容
//                    if (seekPos == 0 && chapterStart != 0) {
//                        //获取当前章节的内容
//                        String chapterContent = blockContent.substring(seekPos, chapterStart);
//                        //设置指针偏移
//                        seekPos += chapterContent.length();
//
//                        //如果当前对整个文件的偏移位置为0的话，那么就是序章
//                        if (curOffset == 0) {
//                            //创建序章
//                            TxtChapter preChapter = new TxtChapter();
//                            preChapter.title = "序章";
//                            preChapter.start = 0;
//                            preChapter.end = chapterContent.getBytes(mCharset.getName()).length; //获取String的byte值,作为最终值
//
//                            //如果序章大小大于30才添加进去
//                            if (preChapter.end - preChapter.start > 30) {
//                                chapters.add(preChapter);
//                            }
//
//                            //创建当前章节
//                            TxtChapter curChapter = new TxtChapter();
//                            curChapter.title = matcher.group();
//                            curChapter.start = preChapter.end;
//                            chapters.add(curChapter);
//                        }
//                        //否则就block分割之后，上一个章节的剩余内容
//                        else {
//                            //获取上一章节
//                            TxtChapter lastChapter = chapters.get(chapters.size() - 1);
//                            //将当前段落添加上一章去
//                            lastChapter.end += chapterContent.getBytes(mCharset.getName()).length;
//
//                            //如果章节内容太小，则移除
//                            if (lastChapter.end - lastChapter.start < 30) {
//                                chapters.remove(lastChapter);
//                            }
//
//                            //创建当前章节
//                            TxtChapter curChapter = new TxtChapter();
//                            curChapter.title = matcher.group();
//                            curChapter.start = lastChapter.end;
//                            chapters.add(curChapter);
//                        }
//                    } else {
//                        //是否存在章节
//                        if (chapters.size() != 0) {
//                            //获取章节内容
//                            String chapterContent = blockContent.substring(seekPos, matcher.start());
//                            seekPos += chapterContent.length();
//
//                            //获取上一章节
//                            TxtChapter lastChapter = chapters.get(chapters.size() - 1);
//                            lastChapter.end = lastChapter.start + chapterContent.getBytes(mCharset.getName()).length;
//
//                            //如果章节内容太小，则移除
//                            if (lastChapter.end - lastChapter.start < 30) {
//                                chapters.remove(lastChapter);
//                            }
//
//                            //创建当前章节
//                            TxtChapter curChapter = new TxtChapter();
//                            curChapter.title = matcher.group();
//                            curChapter.start = lastChapter.end;
//                            chapters.add(curChapter);
//                        }
//                        //如果章节不存在则创建章节
//                        else {
//                            TxtChapter curChapter = new TxtChapter();
//                            curChapter.title = matcher.group();
//                            curChapter.start = 0;
//                            chapters.add(curChapter);
//                        }
//                    }
//                }
//            }
//            //进行本地虚拟分章
//            else {
//                //章节在buffer的偏移量
//                int chapterOffset = 0;
//                //当前剩余可分配的长度
//                int strLength = length;
//                //分章的位置
//                int chapterPos = 0;
//
//                while (strLength > 0) {
//                    ++chapterPos;
//                    //是否长度超过一章
//                    if (strLength > MAX_LENGTH_WITH_NO_CHAPTER) {
//                        //在buffer中一章的终止点
//                        int end = length;
//                        //寻找换行符作为终止点
//                        for (int i = chapterOffset + MAX_LENGTH_WITH_NO_CHAPTER; i < length; ++i) {
//                            if (buffer[i] == Charset.BLANK) {
//                                end = i;
//                                break;
//                            }
//                        }
//                        TxtChapter chapter = new TxtChapter();
//                        chapter.title = "第" + blockPos + "章" + "(" + chapterPos + ")";
//                        chapter.start = curOffset + chapterOffset + 1;
//                        chapter.end = curOffset + end;
//                        chapters.add(chapter);
//                        //减去已经被分配的长度
//                        strLength = strLength - (end - chapterOffset);
//                        //设置偏移的位置
//                        chapterOffset = end;
//                    } else {
//                        TxtChapter chapter = new TxtChapter();
//                        chapter.title = "第" + blockPos + "章" + "(" + chapterPos + ")";
//                        chapter.start = curOffset + chapterOffset + 1;
//                        chapter.end = curOffset + length;
//                        chapters.add(chapter);
//                        strLength = 0;
//                    }
//                }
//            }
//
//            //block的偏移点
//            curOffset += length;
//
//            if (hasChapter) {
//                //设置上一章的结尾
//                TxtChapter lastChapter = chapters.get(chapters.size() - 1);
//                lastChapter.end = curOffset;
//            }
//
//            //当添加的block太多的时候，执行GC
//            if (blockPos % 15 == 0) {
//                System.gc();
//                System.runFinalization();
//            }
//        }
//
//        txtChapters = chapters;
//        IOUtils.close(bookStream);
//
//        System.gc();
//        System.runFinalization();
//    }

    /**
     * 未完成的部分:
     * 1. 序章的添加
     * 2. 章节存在的书本的虚拟分章效果
     *
     * @throws IOException
     */
    private void loadChapters() throws IOException {
        List<TxtChapter> chapters = new ArrayList<>();
        //获取文件流
        RandomAccessFile bookStream = new RandomAccessFile(file_path, "r");
        //寻找匹配文章标题的正则表达式，判断是否存在章节名
        boolean hasChapter = checkChapterType(bookStream);
        //加载章节
        byte[] buffer = new byte[BUFFER_SIZE];
        //获取到的块起始点，在文件中的位置
        long curOffset = 0;
        //block的个数
        int blockPos = 0;
        //读取的长度
        int length;

        //获取文件中的数据到buffer，直到没有数据为止
        while ((length = bookStream.read(buffer, 0, buffer.length)) > 0) {
            ++blockPos;
            //如果存在Chapter
            if (hasChapter) {
                //将数据转换成String
                String blockContent = new String(buffer, 0, length, mCharset.getName());
                //当前Block下使过的String的指针
                int seekPos = 0;
                //进行正则匹配
                Matcher matcher = mChapterPattern.matcher(blockContent);
                //如果存在相应章节
                while (matcher.find()) {
                    //获取匹配到的字符在字符串中的起始位置
                    int chapterStart = matcher.start();

                    //如果 seekPos == 0 && nextChapterPos != 0 表示当前block处前面有一段内容
                    //第一种情况一定是序章 第二种情况可能是上一个章节的内容
                    if (seekPos == 0 && chapterStart != 0) {
                        //获取当前章节的内容
                        String chapterContent = blockContent.substring(seekPos, chapterStart);
                        //设置指针偏移
                        seekPos += chapterContent.length();

                        //如果当前对整个文件的偏移位置为0的话，那么就是序章
                        if (curOffset == 0) {
                            //创建序章
                            TxtChapter preChapter = new TxtChapter();
                            preChapter.title = "序章";
                            preChapter.start = 0;
                            preChapter.end = chapterContent.getBytes(mCharset.getName()).length; //获取String的byte值,作为最终值

                            //如果序章大小大于30才添加进去
                            if (preChapter.end - preChapter.start > 30) {
                                chapters.add(preChapter);
                            }

                            //创建当前章节
                            TxtChapter curChapter = new TxtChapter();
                            curChapter.title = matcher.group();
                            curChapter.start = preChapter.end;
                            chapters.add(curChapter);
                        }
                        //否则就block分割之后，上一个章节的剩余内容
                        else {
                            //获取上一章节
                            TxtChapter lastChapter = chapters.get(chapters.size() - 1);
                            //将当前段落添加上一章去
                            lastChapter.end += chapterContent.getBytes(mCharset.getName()).length;

                            //如果章节内容太小，则移除
                            if (lastChapter.end - lastChapter.start < 30) {
                                chapters.remove(lastChapter);
                            }

                            //创建当前章节
                            TxtChapter curChapter = new TxtChapter();
                            curChapter.title = matcher.group();
                            curChapter.start = lastChapter.end;
                            chapters.add(curChapter);
                        }
                    } else {
                        //是否存在章节
                        if (chapters.size() != 0) {
                            //获取章节内容
                            String chapterContent = blockContent.substring(seekPos, matcher.start());
                            seekPos += chapterContent.length();

                            //获取上一章节
                            TxtChapter lastChapter = chapters.get(chapters.size() - 1);
                            lastChapter.end = lastChapter.start + chapterContent.getBytes(mCharset.getName()).length;

                            //如果章节内容太小，则移除
                            if (lastChapter.end - lastChapter.start < 30) {
                                chapters.remove(lastChapter);
                            }

                            //创建当前章节
                            TxtChapter curChapter = new TxtChapter();
                            curChapter.title = matcher.group();
                            curChapter.start = lastChapter.end;
                            chapters.add(curChapter);
                        }
                        //如果章节不存在则创建章节
                        else {
                            TxtChapter curChapter = new TxtChapter();
                            curChapter.title = matcher.group();
                            curChapter.start = 0;
                            chapters.add(curChapter);
                        }
                    }
                }
            }
            //进行本地虚拟分章
            else {
                //章节在buffer的偏移量
                int chapterOffset = 0;
                //当前剩余可分配的长度
                int strLength = length;
                //分章的位置
                int chapterPos = 0;

                while (strLength > 0) {
                    ++chapterPos;
                    //是否长度超过一章
                    if (strLength > MAX_LENGTH_WITH_NO_CHAPTER) {
                        //在buffer中一章的终止点
                        int end = length;
                        //寻找换行符作为终止点
                        for (int i = chapterOffset + MAX_LENGTH_WITH_NO_CHAPTER; i < length; ++i) {
                            if (buffer[i] == Charset.BLANK) {
                                end = i;
                                break;
                            }
                        }
                        TxtChapter chapter = new TxtChapter();
                        chapter.title = "第" + blockPos + "章" + "(" + chapterPos + ")";
                        chapter.start = curOffset + chapterOffset + 1;
                        chapter.end = curOffset + end;
                        chapters.add(chapter);
                        //减去已经被分配的长度
                        strLength = strLength - (end - chapterOffset);
                        //设置偏移的位置
                        chapterOffset = end;
                    } else {
                        TxtChapter chapter = new TxtChapter();
                        chapter.title = "第" + blockPos + "章" + "(" + chapterPos + ")";
                        chapter.start = curOffset + chapterOffset + 1;
                        chapter.end = curOffset + length;
                        chapters.add(chapter);
                        strLength = 0;
                    }
                }
            }

            //block的偏移点
            curOffset += length;

            if (hasChapter) {
                //设置上一章的结尾
                TxtChapter lastChapter = chapters.get(chapters.size() - 1);
                lastChapter.end = curOffset;
            }

            //当添加的block太多的时候，执行GC
            if (blockPos % 15 == 0) {
                System.gc();
                System.runFinalization();
            }
        }

        txtChapters = chapters;
        IOUtils.close(bookStream);

        System.gc();
        System.runFinalization();
    }

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

    /**
     * @param data              文本数据
     * @param chapterStartIndex 开始字符在全文中的位置
     * @param ParagraphIndex    段落位置
     * @param chapterIndex      章节位置
     * @return 没有识别到章节数据返回null
     */
    private Chapter compileChapter(String data, int chapterStartIndex, int ParagraphIndex, int chapterIndex) {
       // if (data.trim().startsWith("第") || data.contains("第")) {
            for (String str : CHAPTER_PATTERNS) {
           // Pattern p = Pattern.compile(ChapterPatternStr);
            Pattern p = Pattern.compile(str, Pattern.MULTILINE);
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
     * 获取文件编码
     *
     * @param filePath
     * @return
     */
    public static String getCharset(String filePath) {
        BufferedInputStream bis = null;
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            bis = new BufferedInputStream(new FileInputStream(filePath));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.mark(0);
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                            // (0x80 - 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return charset;
    }
    int chapter_id;
    List<Chapter> chapter = new ArrayList<>();

    @Override
    protected void doAfterInit() {
        //formatText(loadText(file_path));
        if(txtChapters==null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    IParagraphData paragraphData = new ParagraphData();
                    //ReadData(file_path, paragraphData, chapter);
                    try {
                        loadChapters();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(2);
                }
            }).start();
        }else {
            mProgressBar.setVisibility(View.GONE);
            mErrorPageTv.setVisibility(View.GONE);
            mCatalogListRv.setVisibility(View.VISIBLE);
            initAdapter();
            mCatalogAdapter.setPosition(chapter_id);
            mCatalogListRv.scrollToPosition(chapter_id);
            mCatalogListRv.setAdapter(mCatalogAdapter);
            mChapterCountTv.setText("共" + txtChapters.size() + "章");
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyEventBusCome(Event event) {
        switch (event.getCode()) {
            case EventBusCode.CATALOG_HOLD_READ_ACTIVITY:
                if (event.getData() instanceof HoldReadActivityEvent2) {
                    HoldReadActivityEvent2 holdReadActivityEvent = (HoldReadActivityEvent2) event.getData();
                    mReadActivity = holdReadActivityEvent.getReadActivity();
                    txtChapters=holdReadActivityEvent.getmChapterList();
                    Log.e("QWW", "onStickyEventBusCome: "+txtChapters.size());
                }
                break;
            default:
                break;
        }
    }

    private void initAdapter() {
        if(txtChapters==null){
            return;
        }
        mCatalogAdapter = new CatalogNameAdapter(this, txtChapters);
        mCatalogAdapter.setOnCatalogListener(new CatalogNameAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                if (!NetUtil.hasInternet(LocalCatalogActivity.this)) {
                    showShortToast("当前无网络，请检查网络后重试");
                    return;
                }
                if (activity_type != null && activity_type.equals("NovelIntroActivity")) {
                    CollBookBean bookBean = new CollBookBean(file_path, mName, "", "",
                            mCover, false, 0, 0,
                            "", "", count, "",
                            false, true);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(WYReadActivity.EXTRA_COLL_BOOK, bookBean);
                    bundle.putBoolean(WYReadActivity.EXTRA_IS_COLLECTED, true);
                    bundle.putString(WYReadActivity.LOAD_PATH, pid);
                    Log.e("QQQ", "clickItem: " + position);
                    if (mIsReverse == false) {
                        bundle.putString(WYReadActivity.CHPTER_ID, position + "");
                    } else {
                        bundle.putString(WYReadActivity.CHPTER_ID, (txtChapters.size() - position - 1) + "");
                    }
                    //bundle.putString(WYReadActivity.CHPTER_ID,(position)+"");
                    startActivity(WYReadActivity.class, bundle);
                    // 点击 item，跳转到相应小说阅读页
                    // 跳转后活动结束
                if (mReadActivity != null) {
                    mReadActivity.finish();
                }
                finish();
                }else {
                    Intent intent_recever = new Intent("com.read.android");
                    if (mIsReverse == false) {
                        intent_recever.putExtra("chpter",position);
                        //bundle.putString(WYReadActivity.CHPTER_ID, position + "");
                    } else {
                        intent_recever.putExtra("chpter",(txtChapters.size() - position - 1));
                        //bundle.putString(WYReadActivity.CHPTER_ID, (txtChapters.size() - position - 1) + "");
                    }
                    intent_recever.putExtra("page_id", -1);
                    sendBroadcast(intent_recever);
                    finish();
                }
            }
        });

    }
    public void startActivity(Class<?> className, Bundle bundle) {
        Intent intent = new Intent(getContext(), className);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    String activity_type;
    private void initBookMarkAdapter() {
        bookAdapter = new BookMarkAdapter(this, bookmarkNovelDbDatas, mChapterNameList, longs);
        bookAdapter.setOnCatalogListener(new BookMarkAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                if (!NetUtil.hasInternet(LocalCatalogActivity.this)) {
                    showShortToast("当前无网络，请检查网络后重试");
                    return;
                }
                String s_id = bookmarkNovelDbDatas.get(position).getChapterid();
                String page_id = (bookmarkNovelDbDatas.get(position).getPosition()) + "";
                if (activity_type != null && activity_type.equals("NovelIntroActivity")) {
                // 点击 item，跳转到相应小说阅读页
                CollBookBean bookBean=new CollBookBean(file_path, mName, "", "",
                        mCover, false, 0,0,
                        "", "", count, "",
                        false, true);
                Bundle bundle = new Bundle();
                bundle.putSerializable(WYReadActivity.EXTRA_COLL_BOOK, bookBean);
                bundle.putBoolean(WYReadActivity.EXTRA_IS_COLLECTED, true);
                bundle.putString(WYReadActivity.LOAD_PATH,pid);
//                if(mIsReverse==false) {
                    bundle.putString(WYReadActivity.CHPTER_ID, bookmarkNovelDbDatas.get(position).getChapterid() + "");
//                }else {
//                    bundle.putString(WYReadActivity.CHPTER_ID, (mChapterNameList.size()-position-1)+ "");
//                }
                //bundle.putString(WYReadActivity.CHPTER_ID,(position)+"");
                startActivity(WYReadActivity.class, bundle);
                // 跳转后活动结束
                if (mReadActivity != null) {
                    mReadActivity.finish();
                }
                finish();
            }else {
                    Intent intent_recever = new Intent("com.read.android");
                    intent_recever.putExtra("chpter", Integer.parseInt(s_id));
                    intent_recever.putExtra("page_id", Integer.parseInt(page_id));
                    sendBroadcast(intent_recever);
                    finish();
                }
            }
        });
        bookAdapter.setOnCatalogLongListener(new BookMarkAdapter.CatalogLongListener() {
            @Override
            public void clickItem(int position) {
                if(!LocalCatalogActivity.this.isDestroyed()) {
                    final TipDialog tipDialog = new TipDialog.Builder(LocalCatalogActivity.this)
                            .setContent("是否删除书签")
                            .setCancel("取消")
                            .setEnsure("确定")
                            .setOnClickListener(new TipDialog.OnClickListener() {
                                @Override
                                public void clickEnsure() {
                                    mDbManager.deleteBookmarkNovel(bookmarkNovelDbDatas.get(position).getTime());
                                    queryBookMarks(pid);
                                    initBookMarkAdapter();
                                    mBookMarkListRv.setAdapter(bookAdapter);
                                }

                                @Override
                                public void clickCancel() {

                                }
                            })
                            .build();
                    tipDialog.show();
                }
            }
        });
    }

    //空两格
    private String mSpace = "\t\t\t\t\t\t";
    //格式化文本，将文本以段落为单位保存
    private List<String> mParagraphList;

    //格式化文本，将文本以段落为单位保存
    private void formatText(String text) {
        boolean isFirstParas = true;
        String paragraph = "";

        //按段落切分文本
        String[] paragraphs = text.split("\\s{2,}");
        //格式化段落
        for (int i = 0; i < paragraphs.length; i++) {
            if (paragraphs[i].isEmpty()) {
                continue;
            }
            if (isFirstParas) {
                paragraph = mSpace + paragraphs[i];
                isFirstParas = false;
            } else {
                paragraph = "\n" + mSpace + paragraphs[i];

            }

            mParagraphList.add(paragraph);

        }

    }
    private void findContents(List<String> paraList) {
        //字符串匹配模式
        String patternString = "第\\S{2,4}\\s\\S{2,}";
        Pattern pattern = Pattern.compile(patternString);

        for (String para : paraList) {

            Matcher matcher = pattern.matcher(para);

            if (matcher.find()) {

                //除去段首多余空格
                int start = matcher.start();
                int end = matcher.end();
                String subString = para.substring(start, end);

                mChapterNameList.add(subString);   //目录
                //mContentParaIndexs.add(paraList.indexOf(para)); //目录对应的在段落集合中的索引

            }

        }

    }

    /**
     * 获取目录数据成功
     */
    @Override
    public void getCatalogDataSuccess(List<Cataloginfo> catalogData,int weight) {
        mIsRefreshing = false;
        mProgressBar.setVisibility(View.GONE);
        mErrorPageTv.setVisibility(View.GONE);
        if (catalogData == null) {
            String s = "网络请求失败，请确认网络连接正常后，刷新页面";
            mErrorPageTv.setText(s);
            mErrorPageTv.setVisibility(View.VISIBLE);
            return;
        }
        mChapterNameList.clear();
        mChapterUrlList.clear();
        for(int i=0;i<catalogData.size();i++){
            mChapterNameList.add(catalogData.get(i).getTitle());
            mChapterUrlList.add(catalogData.get(i).getReurl());
        }
        if (mIsReverse) {   // 如果是倒序显示的话需要先倒置
            Collections.reverse(mChapterNameList);
            Collections.reverse(mChapterUrlList);
        }

        int count = mChapterUrlList.size();
        mChapterCountTv.setText("共" + count + "章");
        initAdapter();
        mCatalogListRv.setAdapter(mCatalogAdapter);
    }

    /**
     * 获取目录数据失败
     */
    @Override
    public void getCatalogDataError(String errorMsg) {
        mIsRefreshing = false;
        mProgressBar.setVisibility(View.GONE);
        if (errorMsg.equals(Constant.NOT_FOUND_CATALOG_INFO)
                || errorMsg.equals(Constant.JSON_ERROR)) {
            String s = "很抱歉，该小说链接已失效，请阅读其他源";
            mErrorPageTv.setText(s);
        } else {
            Log.d(TAG, "getCatalogDataError: errorMsg = " + errorMsg);
            String s = "网络请求失败，请确认网络连接正常后，刷新页面";
            mErrorPageTv.setText(s);
        }
        mErrorPageTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_catalog_back:
                finish();
                break;
            case R.id.shuaxin:
                refresh();
                break;
            case R.id.paixu:
                if (mIsReverse) {
                    paixuiv.setRotation(360);
                    // 正序显示章节
                    mIsReversing = true;
                    Collections.reverse(txtChapters);
                    Collections.reverse(mChapterUrlList);
                    mCatalogAdapter.notifyDataSetChanged();
                    mCatalogAdapter.setPosition(chapter_id);
                    mCatalogListRv.scrollToPosition(chapter_id);
                    mIsReverse = false;
                    mIsReversing = false;
                } else {
                    paixuiv.setRotation(180);
                    // 倒序显示章节
                    mIsReversing = true;
                    Collections.reverse(txtChapters);
                    Collections.reverse(mChapterUrlList);
                    mCatalogAdapter.notifyDataSetChanged();
                    mCatalogAdapter.setPosition(mCatalogAdapter.getItemCount()-chapter_id-1);
                    mCatalogListRv.scrollToPosition(mCatalogAdapter.getItemCount()-chapter_id-1);
                    mIsReverse = true;
                    mIsReversing = false;
                }
                break;
            case R.id.tv_book_mark:
                s_line.setVisibility(View.VISIBLE);
                m_line.setVisibility(View.GONE);
                mCatalogListRv.setVisibility(View.GONE);
                mBookMarkListRv.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_mulu:
                s_line.setVisibility(View.GONE);
                m_line.setVisibility(View.VISIBLE);
                mCatalogListRv.setVisibility(View.VISIBLE);
                mBookMarkListRv.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /**
     * 刷新页面
     */
    private void refresh() {
        mProgressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                IParagraphData paragraphData = new ParagraphData();
                //ReadData(file_path, paragraphData, chapter);
                try {
                    loadChapters();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(2);
            }
        }, 300);
    }
}
