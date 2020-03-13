package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.EmptyAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.NovelAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.ScreenAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePagingLoadAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IAllNovelContract;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IListNovelContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.NovalInfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.ANNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.RequestCNData;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.AllNovelPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.ListNovelPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.EnhanceTabLayout;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.widget.LoadMoreScrollListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FenleiNovelActivity extends BaseActivity<ListNovelPresenter>
        implements View.OnClickListener, IListNovelContract.View {

    private static final String TAG = "AllNovelActivity";

    private ImageView mBackIv;
    private TextView mTitleTv;
    private RecyclerView mNovelListRv;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mRefreshSrv;
    private EnhanceTabLayout enhanceTabLayout;
    private List<String> mTypeTextList = new ArrayList<>();
    private List<NovalInfo> novalInfos = new ArrayList<>();
    private NovelAdapter mNovelAdapter;

    @Override
    protected void doBeforeSetContentView() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
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
    }


    @Override
    protected void initView() {
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
                requestNovels(false, tab.getPosition());
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
        mRefreshSrv.setColorSchemeColors(getResources().getColor(R.color.colorAccent));   //设置颜色
        mRefreshSrv.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新时的操作
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 请求小说信息
                        requestNovels(false);
                    }
                }, 500);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void doAfterInit() {
        StatusBarUtil.setDarkColorStatusBar(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.all_novel_top_bar_bg));
        // 请求小说信息
        requestNovels(true);
    }

    /**
     * 请求小说信息
     */
    private void requestNovels(boolean showProgressBar) {
        RequestCNData requestCNData = new RequestCNData();

        mPresenter.getNovels(requestCNData);
    }

    /**
     * 请求小说信息
     */
    private void requestNovels(boolean showProgressBar, int stype) {
        RequestCNData requestCNData = new RequestCNData();

        mPresenter.getNovels(requestCNData);
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
                requestNovels(true);
                break;
            default:
                break;
        }
    }

//    /**
//     * 获取小说信息成功
//     */
//    @Override
//    public void getNovelsSuccess(List<ANNovelData> dataList, boolean isEnd) {
//        mProgressBar.setVisibility(View.GONE);
//        mRefreshSrv.setRefreshing(false);
//
////        // 更新列表数据
////        if (mIsLoadingMore) {   // 加载更多
////            mDataList.addAll(dataList);
////            if (isEnd) {
////                mNovelAdapter.setLastedStatus();
////            }
////            mNovelAdapter.updateList();
////            mIsLoadingMore = false;
////        } else {    // 第一次加载
////            if (mNovelAdapter == null) {
////                novalInfos = dataList;
////                mNovelAdapter = new NovelAdapter(this, novalInfos,
////                        new BasePagingLoadAdapter.LoadMoreListener() {
////                            @Override
////                            public void loadMore() {
////
////                                requestNovels(true);
////                            }
////                        },
////                        new NovelAdapter.NovelListener() {
////                            @Override
////                            public void clickItem(String novelName) {
////                                if (mRefreshSrv.isRefreshing()) {
////                                    return;
////                                }
////
////                                Intent intent = new Intent(FenleiNovelActivity.this, SearchActivity.class);
////                                // 传递小说名，进入搜查页后直接显示该小说的搜查结果
////                                intent.putExtra(SearchActivity.KEY_NOVEL_NAME, novelName);
////                                startActivity(intent);
////                            }
////                        });
////                mNovelListRv.setAdapter(mNovelAdapter);
////            } else {
////                mDataList.clear();
////                mDataList.addAll(dataList);
////                mNovelAdapter.notifyDataSetChanged();
////            }
////        }
//    }

    @Override
    public void getNovelsSuccess(List<NovalInfo> dataList) {
        mProgressBar.setVisibility(View.GONE);
        mRefreshSrv.setRefreshing(false);
        mNovelAdapter = new NovelAdapter(this, dataList, new BasePagingLoadAdapter.LoadMoreListener() {
            @Override
            public void loadMore() {
                requestNovels(true);
            }
        }, new NovelAdapter.NovelListener() {
            @Override
            public void clickItem(int positon) {
                if (mRefreshSrv.isRefreshing()) {
                    return;
                }
                Intent intent = new Intent(FenleiNovelActivity.this, NovelIntroActivity.class);
                // 传递小说名，进入搜查页后直接显示该小说的搜查结果
                intent.putExtra("pid", positon + "");
                startActivity(intent);
            }
        });
        mNovelListRv.setAdapter(mNovelAdapter);
    }

    /**
     * 获取小说信息失败
     */
    @Override
    public void getNovelsError(String errorMsg) {
        mProgressBar.setVisibility(View.GONE);
        mRefreshSrv.setRefreshing(false);

    }

}
