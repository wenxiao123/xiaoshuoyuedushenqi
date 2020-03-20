package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.Intent;
import android.os.Handler;
import com.google.android.material.tabs.TabLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.RankAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePagingLoadAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IRankContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.RankPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.EnhanceTabLayout;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends BaseActivity<RankPresenter> implements IRankContract.View {
    private EnhanceTabLayout mTabLayout;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mRefreshSrv;
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

    int type;
    int new_or_hot;
    int sort;
    int category_id;

    @Override
    protected void initData() {
        type = getIntent().getIntExtra("type", 1);
        new_or_hot = getIntent().getIntExtra("new_or_hot", 1);
        sort = getIntent().getIntExtra("sort", 1);
        category_id = getIntent().getIntExtra("category_id", 1);

    }

    @Override
    protected void initView() {
        mTabLayout = findViewById(R.id.tv_fenlei_tab_layout);
        List<String> mTypeTextList = strings2List(new String[]{"周榜", "月榜", "总榜"});
        for (int i = 0; i < mTypeTextList.size(); i++) {
            mTabLayout.addTab(mTypeTextList.get(i));
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //requestNovels(false, tab.getPosition());
                mPresenter.getRankData(type + "", new_or_hot + "",(sort+tab.getPosition())+ "", category_id + "");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        recyclerView = findViewById(R.id.rv_all_novel_novel_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        mProgressBar = findViewById(R.id.pb_all_novel);

        mRefreshSrv = findViewById(R.id.srv_all_novel_refresh);
        mRefreshSrv.setColorSchemeColors(getResources().getColor(R.color.colorAccent));   //设置颜色
        mRefreshSrv.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新时的操作
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //requestUpdate();
                        mPresenter.getRankData(type + "", new_or_hot + "",sort + "", category_id + "");
                    }
                }, 500);
            }
        });
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
        mPresenter.getRankData(type + "", new_or_hot + "",sort + "", category_id + "");
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    List<Noval_details> noval_detailsList = new ArrayList<>();
    private RankAdapter mNovelAdapter;

    @Override
    public void getDataSuccess(List<Noval_details> novelNameList) {
        mProgressBar.setVisibility(View.GONE);
        mRefreshSrv.setRefreshing(false);
        if (novelNameList.isEmpty()) {
            return;
        }
        if (mNovelAdapter == null) {
            noval_detailsList = novelNameList;
            initAdapter();
        } else {
            noval_detailsList.clear();
            noval_detailsList.addAll(novelNameList);
            mNovelAdapter.notifyDataSetChanged();
        }
    }

    private void initAdapter() {
        mNovelAdapter = new RankAdapter(this, noval_detailsList, new BasePagingLoadAdapter.LoadMoreListener() {
            @Override
            public void loadMore() {

            }
        }, new RankAdapter.NovelListener() {
            @Override
            public void clickItem(int novelName) {
                Intent intent=new Intent(RankingActivity.this,NovelIntroActivity.class);
                intent.putExtra("pid",novelName+"");
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(mNovelAdapter);
    }

    @Override
    public void getDataError(String errorMsg) {

    }
}
