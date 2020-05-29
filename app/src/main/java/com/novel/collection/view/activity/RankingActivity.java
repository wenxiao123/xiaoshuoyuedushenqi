package com.novel.collection.view.activity;

import android.content.Intent;
import android.os.Handler;

import com.novel.collection.widget.LoadMoreScrollListener;
import com.google.android.material.tabs.TabLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.novel.collection.R;
import com.novel.collection.adapter.RankAdapter;
import com.novel.collection.base.BaseActivity;
import com.novel.collection.base.BasePagingLoadAdapter;
import com.novel.collection.constract.IRankContract;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.presenter.RankPresenter;
import com.novel.collection.util.EnhanceTabLayout;
import com.novel.collection.util.StatusBarUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RankingActivity extends BaseActivity<RankPresenter> implements IRankContract.View {
    private EnhanceTabLayout mTabLayout;
    private ImageView mSearchView;
    private ProgressBar mProgressBar;
    private RefreshLayout mRefreshSrv;
    private RecyclerView recyclerView;

    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rank_novel;
    }

    @Override
    protected RankPresenter getPresenter() {
        return new RankPresenter();
    }

    int type;//获取类型 1 精选 2男 3女 4完本
    int new_or_hot;//排序 1最热 2最新 ，评分 4完结
    int sort;//1 周榜 2月榜 3总榜
    int category_id;
    int z=1;
    @Override
    protected void initData() {
        type = getIntent().getIntExtra("type", 1);
        new_or_hot = getIntent().getIntExtra("new_or_hot", 1);
        sort = getIntent().getIntExtra("sort", 1);
        category_id = getIntent().getIntExtra("category_id", 1);
    }
    /**
     * 跳转到活动
     *
     * @param activity 新活动.class
     */
    protected void jump2Activity(Class activity) {
        startActivity(new Intent(RankingActivity.this, activity));
    }
    @Override
    protected void initView() {
        mSearchView = findViewById(R.id.iv_discovery_search_icon1);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump2Activity(SearchActivity.class);
            }
        });
        mTabLayout = findViewById(R.id.tv_fenlei_tab_layout);
        List<String> mTypeTextList = strings2List(new String[]{"周榜", "月榜", "总榜"});
        for (int i = 0; i < mTypeTextList.size(); i++) {
            mTabLayout.addTab(mTypeTextList.get(i));
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //requestNovels(false, tab.getPosition());
                isRefresh=true;
                z=1;
                //Log.e("zzz", "onTabSelected: "+type + " "+new_or_hot + " "+(tab.getPosition()+1)+ " "+category_id + " "+z+" ");
                mPresenter.getRankData(type + "", new_or_hot + "",(tab.getPosition()+1)+ "", category_id + "",z+" ");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        recyclerView = findViewById(R.id.rv_novel_novel_list);
        mProgressBar = findViewById(R.id.pb_novel);

        mRefreshSrv = findViewById(R.id.srv_novel_refresh);
        mRefreshSrv.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                z=1;
                isRefresh=true;
                mPresenter.getRankData(type + "", new_or_hot + "",sort + "", category_id + "",z+" ");
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                //refreshlayout.finishLoadMore(false);
            }
        });
        mRefreshSrv.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                z++;
                mPresenter.getRankData(type + "", new_or_hot + "",sort + "", category_id + "",z+" ");
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
        findViewById(R.id.iv_all_novel_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private List<String> strings2List(String[] strings) {
        List<String> list = new ArrayList<>();
        for (String s : strings) {
            list.add(s);
        }
        return list;
    }

    @Override
    protected void doAfterInit() {
        //mProgressBar.setVisibility(View.VISIBLE);
        mPresenter.getRankData(type + "", new_or_hot + "",sort + "", category_id + "",z+" ");
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    List<Noval_details> noval_detailsList = new ArrayList<>();
    private RankAdapter mNovelAdapter;
    boolean isRefresh;
    @Override
    public void getDataSuccess(List<Noval_details> novelNameList) {
        mProgressBar.setVisibility(View.GONE);
        if (novelNameList.isEmpty()) {
            return;
        }
        if(isRefresh){
            noval_detailsList.clear();
            isRefresh=false;
            mNovelAdapter=null;
        }
        noval_detailsList.addAll(novelNameList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        int last=linearLayoutManager.findLastVisibleItemPosition();
        if (mNovelAdapter == null) {
            noval_detailsList = novelNameList;
            recyclerView.setLayoutManager(linearLayoutManager);
            //recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
            initAdapter();
            recyclerView.setFocusableInTouchMode(false);
            recyclerView.setScrollingTouchSlop(last);
        } else {
            //mNovelAdapter.notifyDataSetChanged();
            mNovelAdapter.notifyItemChanged(last,0);
        }
    }

    private void initAdapter() {
        mNovelAdapter = new RankAdapter(this, noval_detailsList);
        mNovelAdapter.setOnCatalogListener(new RankAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                Intent intent=new Intent(RankingActivity.this,NovelIntroActivity.class);
                intent.putExtra("pid",position+"");
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_enter,
                        R.anim.zoom_exit);
            }
        });
        recyclerView.setAdapter(mNovelAdapter);
    }

    @Override
    public void getDataError(String errorMsg) {
        showShortToast(errorMsg);
        mProgressBar.setVisibility(View.GONE);
    }
}
