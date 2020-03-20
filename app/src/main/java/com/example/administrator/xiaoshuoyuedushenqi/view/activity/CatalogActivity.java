package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.Intent;
import android.os.Handler;

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

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.BookMarkAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.CatalogAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.constract.ICatalogContract;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Cataloginfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookmarkNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.HoldReadActivityEvent;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.CatalogPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.EnhanceTabLayout;
import com.example.administrator.xiaoshuoyuedushenqi.util.NetUtil;
import com.example.administrator.xiaoshuoyuedushenqi.widget.TipDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatalogActivity extends BaseActivity<CatalogPresenter>
        implements ICatalogContract.View, View.OnClickListener {
    private static final String TAG = "CatalogActivity";
    private static final String ORDER_POSITIVE = "↑正序";
    private static final String ORDER_REVERSE = "↓倒序";
    public static final String KEY_URL = "catalog_key_url";
    public static final String KEY_NAME = "catalog_key_name";
    public static final String KEY_COVER = "catalog_key_cover";
    public static final String KEY_SERIALIZE = "serialize";
    public static final String KEY_AUTHOR = "author";
    private ImageView mBackIv;
    private ImageView mRefreshIv;
    private TextView mChapterCountTv;
    private ImageView mChapterOrderTv;
    private BookMarkAdapter bookAdapter;
    private RecyclerView mCatalogListRv, mBookMarkListRv;
    private ProgressBar mProgressBar;
    private TextView mErrorPageTv;

    private CatalogAdapter mCatalogAdapter;
    private String mUrl;
    private String mName;
    private String mCover;
    private String mAuthor;
    int weigh;
    /*
     * 如果是在 ReadActivity 通过点击目录跳转过来，那么持有该 ReadActivity 的引用，
     * 之后如果跳转到新的章节时，利用该引用结束旧的 ReadActivity
     */
    private ReadActivity mReadActivity;

    private List<String> mChapterNameList = new ArrayList<>();
    private List<String> mChapterUrlList = new ArrayList<>();

    private boolean mIsReverse = false;     // 是否倒序显示章节
    private boolean mIsReversing = false;   // 是否正在倒置，正在倒置时倒置操作无效
    private boolean mIsRefreshing = true;

    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
        mDbManager = DatabaseManager.getInstance();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_catalog;
    }

    @Override
    protected CatalogPresenter getPresenter() {
        return new CatalogPresenter();
    }
    int serialize;
    @Override
    protected void initData() {
        serialize = getIntent().getIntExtra(KEY_SERIALIZE, 0);
        mAuthor=getIntent().getStringExtra(KEY_AUTHOR);
        mUrl = getIntent().getStringExtra(KEY_URL);
        mName = getIntent().getStringExtra(KEY_NAME);
        mCover = getIntent().getStringExtra(KEY_COVER);
        weigh=getIntent().getIntExtra("weigh",0);
        queryBookMarks(mUrl);
    }
    String[]  sTitle={"目录","书签"};
    @Override
    protected void initView() {
        mBackIv = findViewById(R.id.iv_catalog_back);
        mBackIv.setOnClickListener(this);
        mRefreshIv = findViewById(R.id.shuaxin);
        mRefreshIv.setOnClickListener(this);

        mChapterCountTv = findViewById(R.id.tv_catalog_chapter_count);

        mChapterOrderTv = findViewById(R.id.paixu);
        mChapterOrderTv.setOnClickListener(this);

        mCatalogListRv = findViewById(R.id.rv_catalog_list);
        mCatalogListRv.setLayoutManager(new LinearLayoutManager(this));

        mProgressBar = findViewById(R.id.pb_catalog);

        mErrorPageTv = findViewById(R.id.tv_catalog_error_page);

        mBookMarkListRv = findViewById(R.id.rv_book_mark_list);
        mBookMarkListRv.setLayoutManager(new LinearLayoutManager(this));
        initBookMarkAdapter();
        mBookMarkListRv.setAdapter(bookAdapter);

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
    int z=1;
    @Override
    protected void doAfterInit() {
//        StatusBarUtil.setLightColorStatusBar(this);
//        getWindow().setStatusBarColor(getResources().getColor(R.color.catalog_bg));
      mPresenter.getCatalogData(mUrl,z);
    }
    private List<BookmarkNovelDbData> bookmarkNovelDbDatas = new ArrayList<>();
    private DatabaseManager mDbManager;
    private void queryBookMarks(String bookname) {
        bookmarkNovelDbDatas = mDbManager.queryAllBookmarkNovel(bookname);
    }
    private void initBookMarkAdapter() {
        bookAdapter = new BookMarkAdapter(this, bookmarkNovelDbDatas, mChapterNameList, new ArrayList());
        bookAdapter.setOnCatalogListener(new BookMarkAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                if (!NetUtil.hasInternet(CatalogActivity.this)) {
                    showShortToast("当前无网络，请检查网络后重试");
                    return;
                }
                // 点击 item，跳转到相应小说阅读页
              /*  Intent intent = new Intent(CatalogActivity.this, ReadActivity.class);
                // 小说 url（本地小说为 filePath），参数类型为 String
                intent.putExtra(ReadActivity.KEY_NOVEL_URL, mUrl);
                // 小说名，参数类型为 String
                intent.putExtra(ReadActivity.KEY_NAME, mName);
                // 小说封面 url，参数类型为 String
                intent.putExtra(ReadActivity.KEY_COVER, mCover);
//                 小说类型，0 为网络小说， 1 为本地 txt 小说，2 为本地 epub 小说
//                 参数类型为 int（非必需，不传的话默认为 0）
                intent.putExtra(ReadActivity.KEY_TYPE, 0);
                intent.putExtra(ReadActivity.KEY_IS_CATALOG, 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // 开始阅读的章节索引，参数类型为 int（非必需，不传的话默认为 0）
                intent.putExtra(ReadActivity.KEY_CHAPTER_INDEX, (int) bookmarkNovelDbDatas.get(position).getChapterIndex());
                intent.putExtra(ReadActivity.Catalog_start_Position, bookmarkNovelDbDatas.get(position).getPosition());
                startActivity(intent);*/
                // 点击 item，跳转到相应小说阅读页
                String s_id=bookmarkNovelDbDatas.get(position).getChapterid();
                Intent intent = new Intent(CatalogActivity.this, ReadActivity.class);
                intent.putExtra(ReadActivity.KEY_NOVEL_URL, mUrl);
                intent.putExtra(ReadActivity.KEY_CHPATER_ID, Integer.parseInt(s_id));
                intent.putExtra(ReadActivity.KEY_NAME, mName);
                intent.putExtra("first_read",2);
                intent.putExtra("weigh",weigh);//
                intent.putExtra(ReadActivity.KEY_COVER, mCover);
                intent.putExtra(ReadActivity.KEY_SERIALIZE,serialize);
                intent.putExtra(ReadActivity.KEY_AUTHOR,mAuthor);
                intent.putExtra(ReadActivity.KEY_POSITION, bookmarkNovelDbDatas.get(position).getPosition());
                intent.putExtra(ReadActivity.KEY_CHAPTER_INDEX, position);
                intent.putExtra(ReadActivity.KEY_IS_REVERSE, mIsReverse);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                // 跳转后活动结束
                if (mReadActivity != null) {
                    mReadActivity.finish();
                }
                finish();
            }
        });
        bookAdapter.setOnCatalogLongListener(new BookMarkAdapter.CatalogLongListener() {
            @Override
            public void clickItem(int position) {
                final TipDialog tipDialog = new TipDialog.Builder(CatalogActivity.this)
                        .setContent("是否删除书签")
                        .setCancel("取消")
                        .setEnsure("确定")
                        .setOnClickListener(new TipDialog.OnClickListener() {
                            @Override
                            public void clickEnsure() {
                                mDbManager.deleteBookmarkNovel(bookmarkNovelDbDatas.get(position).getTime());
                                queryBookMarks(mUrl);
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
        });
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
                if (!NetUtil.hasInternet(CatalogActivity.this)) {
                    showShortToast("当前无网络，请检查网络后重试");
                    return;
                }
                // 点击 item，跳转到相应小说阅读页
                Intent intent = new Intent(CatalogActivity.this, ReadActivity.class);
                intent.putExtra(ReadActivity.KEY_NOVEL_URL, mUrl);
                intent.putExtra(ReadActivity.KEY_CHPATER_ID, catalogData.get(position).getWeigh());
                intent.putExtra(ReadActivity.KEY_NAME, mName);
                intent.putExtra("first_read",2);
                intent.putExtra("weigh",weigh);
                intent.putExtra(ReadActivity.KEY_COVER, mCover);
                intent.putExtra(ReadActivity.KEY_CHAPTER_INDEX, position);
                intent.putExtra(ReadActivity.KEY_IS_REVERSE, mIsReverse);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                // 跳转后活动结束
                if (mReadActivity != null) {
                    mReadActivity.finish();
                }
                finish();
            }
        });
    }
    List<Cataloginfo> catalogData=new ArrayList<>();
    /**
     * 获取目录数据成功
     */
    @Override
    public void getCatalogDataSuccess(List<Cataloginfo> catalogData) {
        this.catalogData=catalogData;
        mIsRefreshing = false;
        mProgressBar.setVisibility(View.GONE);
        mErrorPageTv.setVisibility(View.GONE);
        mChapterOrderTv.setVisibility(View.VISIBLE);
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
//        mChapterNameList = catalogData.getChapterNameList();
//        mChapterUrlList = catalogData.getChapterUrlList();
        if (mIsReverse) {   // 如果是倒序显示的话需要先倒置
            Collections.reverse(mChapterNameList);
            Collections.reverse(mChapterUrlList);
        }

        int count = mChapterUrlList.size();
        mChapterCountTv.setText("共" + weigh + "章");
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
        mChapterOrderTv.setVisibility(View.GONE);
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
                    // 正序显示章节
                    mChapterOrderTv.setRotation(360);
                    mIsReversing = true;
                    Collections.reverse(mChapterNameList);
                    Collections.reverse(mChapterUrlList);
                    mCatalogAdapter.notifyDataSetChanged();
                    mIsReverse = false;
                    mIsReversing = false;
                } else {
                    // 倒序显示章节
                    mChapterOrderTv.setRotation(180);
                    mIsReversing = true;
                    Collections.reverse(mChapterNameList);
                    Collections.reverse(mChapterUrlList);
                    mCatalogAdapter.notifyDataSetChanged();
                    mIsReverse = true;
                    mIsReversing = false;
                }
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
                mPresenter.getCatalogData(mUrl,z);
            }
        }, 300);
    }
}
