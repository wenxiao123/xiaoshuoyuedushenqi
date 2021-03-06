package com.novel.collection.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.novel.collection.R;
import com.novel.collection.adapter.NovelResultAdapter;
import com.novel.collection.adapter.NovelSearchAdapter;
import com.novel.collection.base.BaseActivity;
import com.novel.collection.base.BasePagingLoadAdapter;
import com.novel.collection.base.BasePresenter;
import com.novel.collection.constant.EventBusCode;
import com.novel.collection.constract.ISearchResultContract;
import com.novel.collection.db.DatabaseManager;
import com.novel.collection.entity.bean.NovalInfo;
import com.novel.collection.entity.bean.Wheel;
import com.novel.collection.entity.eventbus.Event;
import com.novel.collection.entity.eventbus.SearchUpdateInputEvent;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.presenter.ReadPresenter;
import com.novel.collection.presenter.SearchResultPresenter;
import com.novel.collection.util.EditTextUtil;
import com.novel.collection.util.NetUtil;
import com.novel.collection.util.SoftInputUtil;
import com.novel.collection.util.StatusBarUtil;
import com.novel.collection.view.fragment.search.HistoryFragment;
import com.novel.collection.view.fragment.search.SearchFragment;
import com.novel.collection.view.fragment.search.SearchResultFragment;
import com.google.gson.Gson;
import com.novel.collection.widget.WrapContentLinearLayoutManager;
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
import java.util.Date;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Created on 2019/11/8
 */
public class SearchResultActivity extends BaseActivity<SearchResultPresenter> implements ISearchResultContract.View, View.OnClickListener {

    private static final String TAG = "SearchActivity";
    public static final String KEY_NOVEL_NAME = "key_novel_name";
    private SearchResultPresenter searchResultPresenter;
    private ImageView mBackIv;
    private EditText mSearchBarEt;
    private TextView mSearchTv;
    private ImageView mDeleteSearchTextIv;
    private FrameLayout fv_search_container;
    private boolean mIsShowSearchResFg = false;     // 是否正在显示搜索结果 Fragment
    private DatabaseManager mManager;   // 数据库管理类
    private ProgressBar mProgressBar;
    private RefreshLayout mRefreshSrv;
    private RecyclerView mNovelSourceRv;
    private TextView mNoneTv;       // 没有找到相关小说时显示
    private TextView tv_search_result_none;
    private String mStringContent;
    private NovelResultAdapter mNovelSourceAdapter;

    @Override
    protected void doBeforeSetContentView() {
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);   //隐藏标题栏
        StatusBarUtil.setLightColorStatusBar(this);
        searchResultPresenter = new SearchResultPresenter();
    }

    /*
    /
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_result;
    }

    @Override
    protected SearchResultPresenter getPresenter() {
        return new SearchResultPresenter();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventCome(Event event) {
        switch (event.getCode()) {
            case EventBusCode.SEARCH_UPDATE_INPUT:
                if (event.getData() instanceof SearchUpdateInputEvent) {
                    SearchUpdateInputEvent sEvent = (SearchUpdateInputEvent) event.getData();
                    mSearchBarEt.setText(sEvent.getInput());
                    //issearch=true;
                    // 进行搜索
                    doSearch();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void initData() {
        mManager = DatabaseManager.getInstance();
        mStringContent = getIntent().getStringExtra("searchContent");
    }

    @Override
    protected void initView() {
        mNoneTv = findViewById(R.id.tv_search_result_none);
        mProgressBar = findViewById(R.id.pb_search_result_progress_bar);
        tv_search_result_none = findViewById(R.id.tv_search_result_none);
        mNovelSourceRv = findViewById(R.id.rv_search_result_novel_source_list);
//        mNovelSourceRv.setLayoutManager(new LinearLayoutManager(this));
        fv_search_container = findViewById(R.id.fv_search_container);
        mBackIv = findViewById(R.id.iv_search_back);
        mBackIv.setOnClickListener(this);
        mSearchBarEt = findViewById(R.id.et_search_search_bar);
//        mSearchBarEt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SearchResultActivity.this, SearchActivity.class));
//            }
//        });
        mSearchTv = findViewById(R.id.tv_search_search_text);
        mSearchTv.setOnClickListener(this);

        mDeleteSearchTextIv = findViewById(R.id.iv_search_delete_search_text);
        mDeleteSearchTextIv.setOnClickListener(this);
        mRefreshSrv = findViewById(R.id.srv_novel_refresh);
        mRefreshSrv.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                z=1;
                isRefresh=true;
                mPresenter.getNovelsSource(mStringContent,z);
//                mPresenter.getRankData(type + "", new_or_hot + "",sort + "", category_id + "",z+" ");
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                //refreshlayout.finishLoadMore(false);
            }
        });
        mRefreshSrv.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                z++;
                mPresenter.getNovelsSource(mStringContent,z);
               // mPresenter.getRankData(type + "", new_or_hot + "",sort + "", category_id + "",z+" ");
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
                // refreshlayout.finishLoadMore(false);
            }
        });
        ClassicsHeader classicsHeader=new ClassicsHeader(this);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(new Date());
        classicsHeader.setLastUpdateText("最后更新:"+dateString);
        //refreshLayout.setRefreshHeader(new BezierRadarHeader(getContext()).setEnableHorizontalDrag(true));
        mRefreshSrv.setRefreshHeader(classicsHeader);
        //refreshLayout.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshSrv.setRefreshFooter(new ClassicsFooter(this));
    }

    private String mSearchContent;  // 搜索内容
    int z=1;
    boolean isRefresh;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void doAfterInit() {
        // 更改状态栏颜色
        // StatusBarUtil.setLightColorStatusBar(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.search_bg));
        //String novelName = getIntent().getStringExtra(KEY_NOVEL_NAME);
        // searchResultPresenter.getNovelsSource(mStringContent);
        mPresenter.getNovelsSource(mStringContent,z);
        mSearchBarEt.setText(mStringContent);
        mSearchBarEt.setSelection(mSearchBarEt.getText().length());
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    boolean issearch = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search_back:
                finish();
                break;
            case R.id.tv_search_search_text:
                doSearch();
                break;
            case R.id.iv_search_delete_search_text:
                // 删除 EditText 内容
                mSearchBarEt.setText("");
            default:
                break;
        }
    }

    /**
     * 进行搜索
     */
    private void doSearch() {
        if(mNovelSourceAdapter!=null) {
            mNovelSourceDataList.clear();
            mNovelSourceAdapter.notifyDataSetChanged();
        }
        mStringContent=mSearchBarEt.getText().toString();
        z=1;
        mPresenter.getNovelsSource(mStringContent,z);
        //showShortToast("已搜索当前小说");
    }

    private List<NovalInfo> mNovelSourceDataList = new ArrayList<>(); // 小说源列表

    @Override
    public void getNovelsSourceSuccess(List<NovalInfo> novelSourceDataList) {
        mProgressBar.setVisibility(View.GONE);
        // Log.e("QQQ", "getNovelsSourceSuccess: "+novelSourceDataList.size());
        mProgressBar.setVisibility(View.GONE);
        if(isRefresh){
            mNovelSourceDataList.clear();
            isRefresh=false;
            mNovelSourceAdapter=null;
        }
        mNovelSourceDataList.addAll(novelSourceDataList);
        if(mNovelSourceDataList.size() == 0){
            showShortToast("暂无搜索小说");
            mNoneTv.setVisibility(View.VISIBLE);
            mNovelSourceRv.setVisibility(View.GONE);
        }else {
            mNoneTv.setVisibility(View.GONE);
            mNovelSourceRv.setVisibility(View.VISIBLE);
        }
        WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        int last=linearLayoutManager.findLastVisibleItemPosition();
        if (mNovelSourceAdapter == null) {
            mNovelSourceDataList = novelSourceDataList;
            mNovelSourceRv.setLayoutManager(linearLayoutManager);
            //recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
            initAdapter();
            mNovelSourceRv.setFocusableInTouchMode(false);
            mNovelSourceRv.setScrollingTouchSlop(last);
        } else {
            //mNovelAdapter.notifyDataSetChanged();
            mNovelSourceAdapter.notifyItemChanged(last,0);
        }
        // 列表显示小说源
//        mNovelSourceDataList = novelSourceDataList;
//        initAdapter();
    }

    private void initAdapter() {
        if (mNovelSourceDataList == null || mNovelSourceDataList.isEmpty()) {
            return;
        }

        mNovelSourceAdapter = new NovelResultAdapter(this, mNovelSourceDataList);
        mNovelSourceAdapter.setmListener(new NovelResultAdapter.NovelListener() {
            @Override
            public void clickItem(int novelName) {
                Intent intent = new Intent(SearchResultActivity.this, NovelIntroActivity.class);
                intent.putExtra("pid", novelName + "");
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_enter,
                        R.anim.zoom_exit);
            }
        });

        mNovelSourceRv.setAdapter(mNovelSourceAdapter);
    }

    @Override
    public void getNovelsSourceError(String errorMsg) {
        mProgressBar.setVisibility(View.GONE);
        mNovelSourceDataList.clear();
        showShortToast("暂无搜索小说");
        mNoneTv.setVisibility(View.VISIBLE);

    }
}
