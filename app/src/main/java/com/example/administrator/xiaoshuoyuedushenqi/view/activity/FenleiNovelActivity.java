package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.RequiresApi;

import com.example.administrator.xiaoshuoyuedushenqi.adapter.RankAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.app.App;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.NovelAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePagingLoadAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IListNovelContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.NovalInfo;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.ListNovelPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.EnhanceTabLayout;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.widget.LoadMoreScrollListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FenleiNovelActivity extends BaseActivity<ListNovelPresenter>
        implements View.OnClickListener, IListNovelContract.View {

    private static final String TAG = "AllNovelActivity";

    private ImageView mBackIv;
    private TextView mTitleTv;
    private RecyclerView mNovelListRv;
    private ProgressBar mProgressBar;
    private RefreshLayout mRefreshSrv;
    private EnhanceTabLayout enhanceTabLayout;
    private List<String> mTypeTextList = new ArrayList<>();
    private List<NovalInfo> novalInfos = new ArrayList<>();
    private NovelAdapter mNovelAdapter;
    int category_id;
    TextView tv_all_novel_title;
    String title;
    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fl_novel;
    }

    @Override
    protected ListNovelPresenter getPresenter() {
        return new ListNovelPresenter();
    }

    @Override
    protected void initData() {
        category_id=getIntent().getIntExtra("category_id",1);
        title=getIntent().getStringExtra("category_name");
    }
    int SelectPosition=1;

    @Override
    protected void initView() {
        tv_all_novel_title=findViewById(R.id.tv_all_novel_title);
        tv_all_novel_title.setText(title);
        mBackIv = findViewById(R.id.iv_all_novel_back);
        mBackIv.setOnClickListener(this);
        mTitleTv = findViewById(R.id.tv_all_novel_title);
        mNovelListRv = findViewById(R.id.rv_all_novel_novel_list);
        enhanceTabLayout = findViewById(R.id.tv_fenlei_tab_layout);
        mTypeTextList = strings2List(new String[]{Constant.CATEGORY_TYPE_HOT_TEXT, Constant.CATEGORY_TYPE_NEW_TEXT, Constant.CATEGORY_TYPE_REPUTATION_TEXT,
                Constant.CATEGORY_TYPE_OVER_TEXT});
        for (int i = 0; i < mTypeTextList.size(); i++) {
            enhanceTabLayout.addTab(mTypeTextList.get(i));
        }
        enhanceTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isRefresh=true;
                z=1;
                SelectPosition=tab.getPosition();
                requestNovels();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mNovelListRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mNovelListRv.addOnScrollListener(new LoadMoreScrollListener(new LoadMoreScrollListener.LoadMore() {
            @Override
            public void loadMore() {

            }
        }));
        //mNovelListRv.setAdapter(new EmptyAdapter());

        mProgressBar = findViewById(R.id.pb_all_novel);


        mRefreshSrv = findViewById(R.id.srv_all_novel_refresh);
        mRefreshSrv.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                z=1;
                isRefresh=true;
                requestNovels();
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                //refreshlayout.finishLoadMore(false);
            }
        });
        mRefreshSrv.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                z++;
                requestNovels();
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
    boolean isRefresh;
    private List<String> strings2List(String[] strings) {
        List<String> list = new ArrayList<>();
        for (String s : strings) {
            list.add(s);
        }
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void doAfterInit() {
//        StatusBarUtil.setDarkColorStatusBar(this);
//        getWindow().setStatusBarColor(getResources().getColor(R.color.all_novel_top_bar_bg));
        // 请求小说信息
        requestNovels();
    }

    /**
     * 请求小说信息
     */
    private void requestNovels() {
        mPresenter.getNovels(category_id+"",SelectPosition+"",z+"");
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_all_novel_back:
                finish();
                break;
            case R.id.tv_all_novel_screen_ensure:
                // 查找小说信息
                requestNovels();
                break;
            default:
                break;
        }
    }

    @Override
    public void getNovelsSuccess(List<NovalInfo> dataList) {
        mProgressBar.setVisibility(View.GONE);
//        mNovelAdapter = new NovelAdapter(this, dataList);
//        mNovelListRv.setAdapter(mNovelAdapter);
//        if(SelectPosition==2){
//            mNovelAdapter.setRating(true);
//        }else {
//            mNovelAdapter.setRating(false);
//        }
//        mNovelAdapter.notifyDataSetChanged();
//        mNovelAdapter.setOnCatalogListener(new RankAdapter.CatalogListener() {
//            @Override
//            public void clickItem(int position) {
//                Intent intent = new Intent(FenleiNovelActivity.this, NovelIntroActivity.class);
//                // 传递小说名，进入搜查页后直接显示该小说的搜查结果
//                intent.putExtra("pid", position + "");
//                startActivity(intent);
//            }
//        });
        if (dataList.isEmpty()) {
            return;
        }
        if(isRefresh){
            novalInfos.clear();
            isRefresh=false;
            mNovelAdapter=null;
        }
        novalInfos.addAll(dataList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        int last=linearLayoutManager.findLastVisibleItemPosition();
        if (mNovelAdapter == null) {
            //recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
            mNovelAdapter = new NovelAdapter(this, dataList);
            mNovelListRv.setLayoutManager(linearLayoutManager);
            mNovelListRv.setAdapter(mNovelAdapter);
            if(SelectPosition==2){
                mNovelAdapter.setRating(true);
            }
            mNovelListRv.setFocusableInTouchMode(false);
            mNovelListRv.setScrollingTouchSlop(last);
        } else {
            if(SelectPosition==2){
                mNovelAdapter.setRating(true);
            }
            //mNovelAdapter.notifyDataSetChanged();
            mNovelAdapter.notifyItemChanged(last,0);
        }
                mNovelAdapter.setOnCatalogListener(new RankAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                Intent intent = new Intent(FenleiNovelActivity.this, NovelIntroActivity.class);
                // 传递小说名，进入搜查页后直接显示该小说的搜查结果
                intent.putExtra("pid", position + "");
                startActivity(intent);
            }
        });
    }
    int z=1;
    /**
     * 获取小说信息失败
     */
    @Override
    public void getNovelsError(String errorMsg) {
        mProgressBar.setVisibility(View.GONE);
    }

}
