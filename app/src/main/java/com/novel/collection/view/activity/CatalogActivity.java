package com.novel.collection.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.novel.collection.app.App;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.util.StatusBarUtil;
import com.novel.collection.weyue.db.entity.CollBookBean;
import com.novel.collection.widget.WrapContentLinearLayoutManager;
import com.google.android.material.tabs.TabLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.novel.collection.R;
import com.novel.collection.adapter.BookMarkAdapter;
import com.novel.collection.adapter.CatalogAdapter;
import com.novel.collection.base.BaseActivity;
import com.novel.collection.constant.Constant;
import com.novel.collection.constant.EventBusCode;
import com.novel.collection.constract.ICatalogContract;
import com.novel.collection.db.DatabaseManager;
import com.novel.collection.entity.bean.Cataloginfo;
import com.novel.collection.entity.data.BookmarkNovelDbData;
import com.novel.collection.entity.eventbus.Event;
import com.novel.collection.entity.eventbus.HoldReadActivityEvent;
import com.novel.collection.presenter.CatalogPresenter;
import com.novel.collection.util.EnhanceTabLayout;
import com.novel.collection.util.NetUtil;
import com.novel.collection.widget.TipDialog;
import com.google.gson.JsonSyntaxException;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.LoginException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.novel.collection.app.App.getContext;

  //在线书籍目录

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
    private TextView text;
    int weigh;//书籍总章数
    int chapter_id=-1;//当前阅读书籍目录
    /*
     * 如果是在 ReadActivity 通过点击目录跳转过来，那么持有该 ReadActivity 的引用，
     * 之后如果跳转到新的章节时，利用该引用结束旧的 ReadActivity
     */
    private WYReadActivity mReadActivity;

    private List<String> mChapterNameList = new ArrayList<>();
    private List<String> mChapterUrlList = new ArrayList<>();

    private boolean mIsReverse = false;     // 是否倒序显示章节
    private boolean mIsReversing = false;   // 是否正在倒置，正在倒置时倒置操作无效
    private boolean mIsRefreshing = true;
    private RefreshLayout mRefreshSrv;

    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
        mDbManager = DatabaseManager.getInstance();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_catalog;
    }

    int type = 1;

    @Override
    protected CatalogPresenter getPresenter() {
        return new CatalogPresenter();
    }

    int serialize;
    App app;
    String activity_type;

    @Override
    protected void initData() {
        serialize = getIntent().getIntExtra(KEY_SERIALIZE, 0);
        mAuthor = getIntent().getStringExtra(KEY_AUTHOR);
        activity_type = getIntent().getStringExtra("ACTIVITY_TYPE");
        mUrl = getIntent().getStringExtra(KEY_URL);
        mName = getIntent().getStringExtra(KEY_NAME);
        mCover = getIntent().getStringExtra(KEY_COVER);
        //weigh = getIntent().getIntExtra("weigh", 0);
        chapter_id = getIntent().getIntExtra("chapter_id", -1);
        queryBookMarks(mUrl);
        app = (App) getApplication();
        catalogDataAll = mDbManager.queryAllCataloginfo(mUrl);
        Collections.reverse(catalogDataAll);
        databaseManager = DatabaseManager.getInstance();
    }
    boolean is_frist;
    String[] sTitle = {"目录", "书签"};

    @Override
    protected void initView() {
        text = findViewById(R.id.title);
        mBackIv = findViewById(R.id.iv_catalog_back);
        mBackIv.setOnClickListener(this);
        mRefreshIv = findViewById(R.id.shuaxin);
        mRefreshIv.setOnClickListener(this);

        mChapterCountTv = findViewById(R.id.tv_catalog_chapter_count);

        mChapterOrderTv = findViewById(R.id.paixu);
        mChapterOrderTv.setOnClickListener(this);

        mCatalogListRv = findViewById(R.id.rv_catalog_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mCatalogListRv.setLayoutManager(linearLayoutManager);

        mProgressBar = findViewById(R.id.pb_catalog);

        mErrorPageTv = findViewById(R.id.tv_catalog_error_page);

        mBookMarkListRv = findViewById(R.id.rv_book_mark_list);
        mBookMarkListRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        initBookMarkAdapter();
        mBookMarkListRv.setAdapter(bookAdapter);

        EnhanceTabLayout mEnhanceTabLayout = findViewById(R.id.enhance_tab_layout);
        mEnhanceTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    mCatalogListRv.setVisibility(View.VISIBLE);
                    mBookMarkListRv.setVisibility(View.GONE);
                } else {
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
        for (int i = 0; i < sTitle.length; i++) {
            mEnhanceTabLayout.addTab(sTitle[i]);
        }
        mEnhanceTabLayout.getmTabLayout().getTabAt(0).select();
        mRefreshSrv = findViewById(R.id.srv_male_refresh);
        mRefreshSrv.setEnableRefresh(false);
        mRefreshSrv.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                z++;
                mPresenter.getCatalogData(mUrl, z, type);
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
        ClassicsHeader classicsHeader = new ClassicsHeader(this);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(new Date());
        classicsHeader.setLastUpdateText("最后更新:" + dateString);
        mRefreshSrv.setRefreshHeader(classicsHeader);
        mRefreshSrv.setRefreshFooter(new ClassicsFooter(this));
    }

    int z = 1, h = 1;

    @Override
    protected void doAfterInit() {
        z = 1;
        if (catalogDataAll.size() == 0) {
            is_frist=true;
            mPresenter.getCatalogData(mUrl, z, type);
        } else {
            is_frist=false;
            mRefreshSrv.setEnableLoadMore(false);
            mProgressBar.setVisibility(View.GONE);
            mErrorPageTv.setVisibility(View.GONE);
            mChapterOrderTv.setVisibility(View.VISIBLE);
            initAdapter();
//            if (activity_type != null && activity_type.equals("NovelIntroActivity")) {
//                mCatalogAdapter.setPosition(-1);
//            }else {
                mCatalogAdapter.setPosition(chapter_id);
//            }
            mCatalogListRv.scrollToPosition(chapter_id);
            mChapterCountTv.setText("共" + catalogDataAll.size() + "章");
        }
        //
        text.setText(mName);
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
                String s_id = bookmarkNovelDbDatas.get(position).getChapterid();
                String page_id = (bookmarkNovelDbDatas.get(position).getPosition()) + "";
                if (activity_type != null && activity_type.equals("NovelIntroActivity")) {
//                    // 点击 item，跳转到相应小说阅读页
//                    // 跳转后活动结束
                    CollBookBean bookBean = new CollBookBean(mUrl, mName, "", "",
                            "", false, 0, 0,
                            "", "", Integer.parseInt(s_id) - 1, "",
                            false, false);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(WYReadActivity.EXTRA_COLL_BOOK, bookBean);
                    bundle.putBoolean(WYReadActivity.EXTRA_IS_COLLECTED, true);
                    bundle.putString(WYReadActivity.CHPTER_ID, s_id);
                    bundle.putString(WYReadActivity.PAGE_ID, page_id);
                    startActivity(WYReadActivity.class, bundle);
                    finish();
                } else {
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
                if (!CatalogActivity.this.isDestroyed()) {
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
            }
        });
    }

    public void startActivity(Class<?> className, Bundle bundle) {
        Intent intent = new Intent(getContext(), className);
        intent.putExtras(bundle);
        startActivity(intent);
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
        mCatalogAdapter = new CatalogAdapter(this, catalogDataAll);
        mCatalogAdapter.setOnCatalogListener(new CatalogAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                if (!NetUtil.hasInternet(CatalogActivity.this)) {
                    showShortToast("当前无网络，请检查网络后重试");
                    return;
                }
                if (activity_type != null && activity_type.equals("NovelIntroActivity")) {
                    String s_id = catalogDataAll.get(position).getWeigh() + "";
                    // 跳转后活动结束
                    CollBookBean bookBean = new CollBookBean(mUrl, mName, "", "",
                            "", false, 0, 0,
                            "", "", Integer.parseInt(s_id) - 1, "",
                            false, false);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(WYReadActivity.EXTRA_COLL_BOOK, bookBean);
                    bundle.putBoolean(WYReadActivity.EXTRA_IS_COLLECTED, true);
                   // bundle.putString(WYReadActivity.CHPTER_ID, s_id);
                    if (mIsReverse == false) {
                       // intent_recever.putExtra("chpter",position);
                        bundle.putString(WYReadActivity.CHPTER_ID, position + "");
                    } else {
                        if(weigh==0){
                            weigh=catalogDataAll.size();
                        }
                        Log.e("SSS", "clickItem: "+weigh);
                        //intent_recever.putExtra("chpter",(catalogDataAll.size() - position - 1));
                        bundle.putString(WYReadActivity.CHPTER_ID, (weigh - position - 1) + "");
                    }
                    startActivity(WYReadActivity.class, bundle);
                    if (mReadActivity != null) {
                        mReadActivity.finish();
                    }
                    finish();
                    Log.e("QQQQ", "clickItem: "+3333);
                } else {
                    Intent intent_recever = new Intent("com.read.android");
                    if (mIsReverse == false) {
                        intent_recever.putExtra("chpter",position);
                        //bundle.putString(WYReadActivity.CHPTER_ID, position + "");
                    } else {
                        if(weigh==0){
                            weigh=catalogDataAll.size();
                        }
                        intent_recever.putExtra("chpter",(weigh - position - 1));
                        //bundle.putString(WYReadActivity.CHPTER_ID, (txtChapters.size() - position - 1) + "");
                    }
                       //intent_recever.putExtra("chpter",position);//catalogDataAll.get(position).getWeigh() - 1
                    sendBroadcast(intent_recever);
                    finish();
                }
            }
        });
        mCatalogListRv.setAdapter(mCatalogAdapter);
    }

    List<Cataloginfo> catalogDataAll = new ArrayList<>();

    /**
     * 获取目录数据成功
     */
    @Override
    public void getCatalogDataSuccess(List<Cataloginfo> catalogData, int weight) {
        mIsRefreshing = false;
        mProgressBar.setVisibility(View.GONE);
        mChapterOrderTv.setVisibility(View.GONE);
        mChapterOrderTv.setVisibility(View.VISIBLE);
        mChapterCountTv.setText("共" + weight+ "章");
        this.weigh=weight;
        catalogDataAll.addAll(catalogData);//new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        int last = linearLayoutManager.findLastVisibleItemPosition();
        if (mCatalogAdapter == null) {
            mCatalogListRv.setLayoutManager(linearLayoutManager);
            initAdapter();
//            if (activity_type != null && activity_type.equals("NovelIntroActivity")) {
//                mCatalogAdapter.setPosition(-1);
//            }
            mCatalogListRv.setFocusableInTouchMode(false);
            mCatalogListRv.setScrollingTouchSlop(last);
        } else {
            mCatalogAdapter.notifyItemChanged(last, 0);

        }
    }
    DatabaseManager databaseManager;

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
                if(is_frist==false) {
                    if (mCatalogAdapter == null) {
                        return;
                    } else {
                        if (mIsReverse) {
                            // 正序显示章节
                            mChapterOrderTv.setRotation(360);
                            mIsReversing = true;
                            Collections.reverse(catalogDataAll);
                            mCatalogAdapter.notifyDataSetChanged();
                            mCatalogAdapter.setPosition(chapter_id);
                           // mCatalogListRv.scrollToPosition(chapter_id);
                            mIsReverse = false;
                            mIsReversing = false;
                        } else {
                            mChapterOrderTv.setRotation(180);
                            mIsReversing = true;
                            Collections.reverse(catalogDataAll);
                            mCatalogAdapter.notifyDataSetChanged();
                            mCatalogAdapter.setPosition(mCatalogAdapter.getItemCount()-chapter_id-1);
                            //mCatalogListRv.scrollToPosition(mCatalogAdapter.getItemCount()-chapter_id-1);
                            mIsReverse = true;
                            mIsReversing = false;
                        }
                    }
                }else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    catalogDataAll.clear();
                    mCatalogAdapter.notifyDataSetChanged();
                    if (mIsReverse) {
                        mIsReverse = false;
                        // 正序显示章节
                       type=1;
                       z=1;
                        mPresenter.getCatalogData(mUrl, z, type);
                    } else {
                        mIsReverse = true;
                        type=2;
                        z=1;
                        mPresenter.getCatalogData(mUrl, z, type);
                    }
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
        catalogDataAll.clear();
        mCatalogAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.VISIBLE);
        //catalogDataAll = mDbManager.queryAllCataloginfo(mUrl);
        z = 1;
        mPresenter.getCatalogData(mUrl, z, type);
        mRefreshSrv.setEnableLoadMore(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshSrv.setEnableRefresh(false);
            }
        },3000);
    }
}
