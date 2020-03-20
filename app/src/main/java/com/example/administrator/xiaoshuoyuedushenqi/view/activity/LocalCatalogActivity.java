package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
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
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.BookMarkAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.CatalogAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.constract.ICatalogContract;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Cataloginfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Chapter;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookmarkNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.HoldReadActivityEvent;
import com.example.administrator.xiaoshuoyuedushenqi.interfaces.IChapter;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.CatalogPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.EnhanceTabLayout;
import com.example.administrator.xiaoshuoyuedushenqi.util.NetUtil;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalCatalogActivity extends BaseActivity<CatalogPresenter>
        implements ICatalogContract.View, View.OnClickListener {
    private static final String TAG = "LocalCatalogActivity";
    private static final String ORDER_POSITIVE = "↑正序";
    private static final String ORDER_REVERSE = "↓倒序";
    public static final String KEY_URL = "file_path";
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
    private CatalogAdapter mCatalogAdapter;
    private BookMarkAdapter bookAdapter;
    private String file_path, mName, mCover;
    private int mPosition;
    private ImageView shuaxiniv, paixuiv;
    private DatabaseManager mDbManager;
    /*
     * 如果是在 ReadActivity 通过点击目录跳转过来，那么持有该 ReadActivity 的引用，
     * 之后如果跳转到新的章节时，利用该引用结束旧的 ReadActivity
     */
    private ReadActivity mReadActivity;

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
            mIsRefreshing = false;
            mProgressBar.setVisibility(View.GONE);
            initAdapter();
            mChapterCountTv.setText("共" + count + "章");
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

    @Override
    protected void initData() {
        file_path = getIntent().getStringExtra(KEY_URL);
        mName = getIntent().getStringExtra(KEY_NAME);
        mCover = getIntent().getStringExtra(KEY_COVER);
        mPosition = getIntent().getIntExtra(KEY_POSTION, 0);
        queryBookMarks(file_path);
    }

    @Override
    protected void initView() {
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
    private static final String ChapterPatternStr = "(^.{0,3}\\s*第)(.{1,9})[章节卷集部篇回](\\s*)";

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

    private void queryBookMarks(String bookname) {
        bookmarkNovelDbDatas = mDbManager.queryAllBookmarkNovel(bookname);
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

    List<Chapter> chapter = new ArrayList<>();

    @Override
    protected void doAfterInit() {
        //formatText(loadText(file_path));
        new Thread(new Runnable() {
            @Override
            public void run() {
                IParagraphData paragraphData = new ParagraphData();
                ReadData(file_path, paragraphData, chapter);
                handler.sendEmptyMessage(2);
            }
        }).start();
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyEventBusCome(Event event) {
        switch (event.getCode()) {
            case EventBusCode.CATALOG_HOLD_READ_ACTIVITY:
                if (event.getData() instanceof HoldReadActivityEvent) {
                    HoldReadActivityEvent holdReadActivityEvent = (HoldReadActivityEvent) event.getData();
                    mReadActivity = holdReadActivityEvent.getReadActivity();
                }
                break;
            default:
                break;
        }
    }

    private void initAdapter() {
        mCatalogAdapter = new CatalogAdapter(this, mChapterNameList);
        mCatalogAdapter.setOnCatalogListener(new CatalogAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                if (!NetUtil.hasInternet(LocalCatalogActivity.this)) {
                    showShortToast("当前无网络，请检查网络后重试");
                    return;
                }
                // 点击 item，跳转到相应小说阅读页
                Intent intent = new Intent(LocalCatalogActivity.this, ReadActivity.class);
                // 小说 url（本地小说为 filePath），参数类型为 String
                intent.putExtra(ReadActivity.KEY_NOVEL_URL, file_path);
                // 小说名，参数类型为 String
                intent.putExtra(ReadActivity.KEY_NAME, mName);
                // 小说封面 url，参数类型为 String
                intent.putExtra(ReadActivity.KEY_COVER, mCover);
//                 小说类型，0 为网络小说， 1 为本地 txt 小说，2 为本地 epub 小说
//                 参数类型为 int（非必需，不传的话默认为 0）
                intent.putExtra(ReadActivity.KEY_TYPE, 1);
                intent.putExtra(ReadActivity.KEY_IS_CATALOG, 1);
                intent.putExtra("catalog", (Serializable) mChapterNameList);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // 开始阅读的章节索引，参数类型为 int（非必需，不传的话默认为 0）
                intent.putExtra(ReadActivity.KEY_CHAPTER_INDEX, position);
                if (paixuiv.getRotation()==0||paixuiv.getRotation()==360) {
                    intent.putExtra(ReadActivity.Catalog_start_Position, (int) longs.get(position));
                } else {
                    intent.putExtra(ReadActivity.Catalog_start_Position, (int) longs.get(longs.size() - 1 - position));
                }

                startActivity(intent);
                // 跳转后活动结束
                if (mReadActivity != null) {
                    mReadActivity.finish();
                }
                finish();
            }
        });

    }

    private void initBookMarkAdapter() {
        bookAdapter = new BookMarkAdapter(this, bookmarkNovelDbDatas, mChapterNameList, longs);
        bookAdapter.setOnCatalogListener(new BookMarkAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                if (!NetUtil.hasInternet(LocalCatalogActivity.this)) {
                    showShortToast("当前无网络，请检查网络后重试");
                    return;
                }
                // 点击 item，跳转到相应小说阅读页
                Intent intent = new Intent(LocalCatalogActivity.this, ReadActivity.class);
                // 小说 url（本地小说为 filePath），参数类型为 String
                intent.putExtra(ReadActivity.KEY_NOVEL_URL, file_path);
                // 小说名，参数类型为 String
                intent.putExtra(ReadActivity.KEY_NAME, mName);
                // 小说封面 url，参数类型为 String
                intent.putExtra(ReadActivity.KEY_COVER, mCover);
//                 小说类型，0 为网络小说， 1 为本地 txt 小说，2 为本地 epub 小说
//                 参数类型为 int（非必需，不传的话默认为 0）
                intent.putExtra(ReadActivity.KEY_TYPE, 1);
                intent.putExtra(ReadActivity.KEY_IS_CATALOG, 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // 开始阅读的章节索引，参数类型为 int（非必需，不传的话默认为 0）
                intent.putExtra(ReadActivity.KEY_CHAPTER_INDEX, position);
                intent.putExtra(ReadActivity.Catalog_start_Position, bookmarkNovelDbDatas.get(position).getPosition());
                startActivity(intent);
                // 跳转后活动结束
                if (mReadActivity != null) {
                    mReadActivity.finish();
                }
                finish();
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
    public void getCatalogDataSuccess(List<Cataloginfo> catalogData) {
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
                if (mIsReversing || mIsRefreshing) {
                    return;
                }
                if (mIsReverse) {
                    paixuiv.setRotation(360);
                    // 正序显示章节
                    mIsReversing = true;
                    Collections.reverse(mChapterNameList);
                    Collections.reverse(mChapterUrlList);
                    mCatalogAdapter.setPosition(z);
                    mCatalogAdapter.notifyDataSetChanged();
                    mCatalogListRv.scrollToPosition(z);
                    mIsReverse = false;
                    mIsReversing = false;
                } else {
                    paixuiv.setRotation(180);
                    // 倒序显示章节
                    mIsReversing = true;
                    Collections.reverse(mChapterNameList);
                    Collections.reverse(mChapterUrlList);
                    mCatalogAdapter.setPosition(mCatalogAdapter.getItemCount()-1-z);
                    mCatalogAdapter.notifyDataSetChanged();
                    mCatalogListRv.scrollToPosition(mCatalogAdapter.getItemCount()-z);
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
        if (mIsRefreshing) {    // 已经在刷新了
            return;
        }
        mIsRefreshing = true;
        mProgressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                IParagraphData paragraphData = new ParagraphData();
                ReadData(file_path, paragraphData, chapter);
                handler.sendEmptyMessage(2);
            }
        }, 300);
    }
}
