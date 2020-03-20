package com.example.administrator.xiaoshuoyuedushenqi.view.fragment.bookstore;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.BookstoreCategoryAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.CategoryAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.CategoryinfoAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.CategoryzyAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.HotRankAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseFragment;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IExclusiveContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovels;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.ExclusivePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.NetUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.AllNovelActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.FenleiNovelActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.NovelIntroActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.RankingActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.SearchActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExclusivelistGFragment extends BaseFragment<ExclusivePresenter> implements IExclusiveContract.View {
    RecyclerView recyclerView1, recyclerView2, recyclerView3, recyclerView4;
    private CategoryAdapter mCategoryAdapter;
    private List<String> mCategoryNameList = new ArrayList<>();
    private List<String> mMoreList = new ArrayList<>();
    private List<Noval_details> mNovelDataList = new ArrayList<>();
    private List<Noval_details> mNovelDataList2 = new ArrayList<>();
    List<CategoryNovels> novelNameList = new ArrayList<>();
    List<Noval_details> noval_detailsList = new ArrayList<>();
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mRefreshSrv;
    private TextView tv_item_renqi_more;
    private TextView tv_item_new_more1;
    private TextView tv_item_new_renqi_more;
    @Override
    protected int getLayoutId() {
        return R.layout.exclusivelist;
    }

    @Override
    protected void initData() {
        mCategoryNameList.add("人气排行榜");
        mCategoryNameList.add("推荐排行榜");
        mCategoryNameList.add("新书人气榜");
        mMoreList.add("更多");
        mMoreList.add("更多");
        mMoreList.add("更多");
    }

    @Override
    protected void initView() {
        tv_item_renqi_more=getActivity().findViewById(R.id.tv_item_renqi_more);
        tv_item_renqi_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), RankingActivity.class);
                intent.putExtra("type",1);
                intent.putExtra("new_or_hot",1);
                intent.putExtra("sort",3);
                getContext().startActivity(intent);
            }
        });
        tv_item_new_more1=getActivity().findViewById(R.id.tv_item_new_more1);
        tv_item_new_more1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), RankingActivity.class);
                intent.putExtra("type",1);
                intent.putExtra("new_or_hot",1);
                intent.putExtra("sort",1);
                getContext().startActivity(intent);
            }
        });
        tv_item_new_renqi_more=getActivity().findViewById(R.id.tv_item_new_renqi_more);
        tv_item_new_renqi_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), RankingActivity.class);
                intent.putExtra("type",1);
                intent.putExtra("new_or_hot",2);
                getContext().startActivity(intent);
            }
        });
        recyclerView1 = getActivity().findViewById(R.id.rv_male_new_novel_list1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));
        recyclerView2 = getActivity().findViewById(R.id.rv_male_hot_rank_recycler_view1);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));
        recyclerView3 = getActivity().findViewById(R.id.rv_new_hot_rank_recycler_view1);
        recyclerView3.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));
        recyclerView4 = getActivity().findViewById(R.id.rv_male_category_novel_list1);
        recyclerView4.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProgressBar = getActivity().findViewById(R.id.pb_male);

        mRefreshSrv = getActivity().findViewById(R.id.srv_male_refresh);
        mRefreshSrv.setColorSchemeColors(getResources().getColor(R.color.colorAccent));   //设置颜色
        mRefreshSrv.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新时的操作
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestUpdate();
                    }
                }, 500);
            }
        });
    }

    @Override
    protected void doInOnCreate() {
        requestUpdate();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void requestUpdate() {
        mPresenter.getHotRankData(1 + "");
        mPresenter.getCategoryNovels(1 + "");
        mPresenter.getCategoryNovels2(1 + "");
        mPresenter.getNewRankData(1 + "");
    }

    @Override
    protected ExclusivePresenter getPresenter() {
        return new ExclusivePresenter();
    }


    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Override
    public void getNewDataSuccess(List<Noval_details> novelNameList) {
        if (novelNameList.isEmpty()) {
            return;
        }
        noval_detailsList = novelNameList;
        if (mHotRankAdapter == null) {
            noval_detailsList = novelNameList;
            initnewAdapter();
        } else {
            noval_detailsList.clear();
            noval_detailsList.addAll(novelNameList);
            mHotRankAdapter.notifyDataSetChanged();
        }
    }
    CategoryzyAdapter mHotRankAdapter;
    private void initnewAdapter() {
        mHotRankAdapter = new CategoryzyAdapter(getContext(),
                noval_detailsList);
        mHotRankAdapter.setOnCategoryNovelListener(new CategoryzyAdapter.CategoryNovelListener() {
            @Override
            public void clickItem(int novelName) {
                Intent intent = new Intent(getContext(), NovelIntroActivity.class);
                // 传递小说名，进入搜查页后直接显示该小说的搜查结果
                intent.putExtra("pid", noval_detailsList.get(novelName).getId() + "");
                startActivity(intent);
            }
        });
        recyclerView1.setAdapter(mHotRankAdapter);
    }

    @Override
    public void getNewDataError(String errorMsg) {
        showShortToast(errorMsg);
    }
    private List<CategoryNovels> mHotRankNovelNameList;
    @Override
    public void getHotRankDataSuccess(List<CategoryNovels> novelNameList) {
        if (novelNameList.isEmpty()) {
            return;
        }
        this.novelNameList = novelNameList;
        if (mHotRankAdapter == null) {
            mHotRankNovelNameList = novelNameList;
            initCategoryAdapter();
        } else {
            mHotRankNovelNameList.clear();
            mHotRankNovelNameList.addAll(novelNameList);
            mHotRankAdapter.notifyDataSetChanged();
        }
    }
    private void initCategoryAdapter() {
        mCategoryAdapter = new CategoryAdapter(getActivity(),
                novelNameList,
                new CategoryAdapter.CategoryListener() {
                    @Override
                    public void clickNovel(String novelName) {
                        if (mRefreshSrv.isRefreshing()) {
                            return;
                        }
                        if (!NetUtil.hasInternet(getActivity())) {
                            showShortToast("当前无网络，请检查网络后重试");
                            return;
                        }
                        // 跳转到该小说的搜索结果页
                        Intent intent = new Intent(getActivity(), SearchActivity.class);
                        intent.putExtra(SearchActivity.KEY_NOVEL_NAME, novelName);
                        startActivity(intent);
                    }

                    @Override
                    public void clickMore(int position) {
                        if (mRefreshSrv.isRefreshing()) {
                            return;
                        }
                        if (!NetUtil.hasInternet(getActivity())) {
                            showShortToast("当前无网络，请检查网络后重试");
                            return;
                        }
                        Intent intent = new Intent(getContext(), RankingActivity.class);
                        intent.putExtra("type", 1);
                        intent.putExtra("new_or_hot", 3);
                        intent.putExtra("category_id", novelNameList.get(position).getId());
                        startActivity(intent);
                    }
                });
        mCategoryAdapter.setType(6);
        recyclerView4.setAdapter(mCategoryAdapter);
    }
    @Override
    public void getHotRankDataError(String errorMsg) {

    }

    @Override
    public void getCategoryNovelsSuccess(List<Noval_details> dataList) {
        mProgressBar.setVisibility(View.GONE);
        mRefreshSrv.setRefreshing(false);
        if (dataList.isEmpty()) {
            return;
        }
        if (adapter == null) {
            mNovelDataList = dataList;
            initAdapter();
        } else {
            mNovelDataList.clear();
            mNovelDataList.addAll(dataList);
            adapter.notifyDataSetChanged();
        }

    }
    CategoryzyAdapter adapter,adapter2;
    private void initAdapter() {
        adapter = new CategoryzyAdapter(getContext(),
                mNovelDataList);
        adapter.setOnCategoryNovelListener(new CategoryzyAdapter.CategoryNovelListener() {
            @Override
            public void clickItem(int novelName) {
                Intent intent = new Intent(getContext(), NovelIntroActivity.class);
                // 传递小说名，进入搜查页后直接显示该小说的搜查结果
                intent.putExtra("pid", mNovelDataList.get(novelName).getId() + "");
                startActivity(intent);
            }
        });
        recyclerView3.setAdapter(adapter);
    }

    private void initAdapter2() {
        adapter2 = new CategoryzyAdapter(getContext(),
                mNovelDataList2);
        adapter2.setOnCategoryNovelListener(new CategoryzyAdapter.CategoryNovelListener() {
            @Override
            public void clickItem(int novelName) {
                Intent intent = new Intent(getContext(), NovelIntroActivity.class);
                // 传递小说名，进入搜查页后直接显示该小说的搜查结果
                intent.putExtra("pid", mNovelDataList2.get(novelName).getId() + "");
                startActivity(intent);
            }
        });
        recyclerView2.setAdapter(adapter2);
    }
    @Override
    public void getCategoryNovelsError(String errorMsg) {
        mProgressBar.setVisibility(View.GONE);
        mRefreshSrv.setRefreshing(false);
        showShortToast("" + errorMsg);
    }

    @Override
    public void getCategoryNovels2Success(List<Noval_details> dataList) {
        if (dataList.isEmpty()) {
            return;
        }
        if (adapter2 == null) {
            mNovelDataList2 = dataList;
            initAdapter2();
        } else {
            mNovelDataList2.clear();
            mNovelDataList2.addAll(dataList);
            adapter2.notifyDataSetChanged();
        }
    }

    @Override
    public void getCategoryNovels2Error(String errorMsg) {

    }
}