package com.novel.collection.view.fragment.discovery;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.PagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.danikula.videocache.HttpProxyCacheServer;
import com.novel.collection.R;
import com.novel.collection.adapter.CategoryAdapter;
import com.novel.collection.adapter.CategoryinfoAdapter;
import com.novel.collection.adapter.CategoryzyAdapter;
import com.novel.collection.adapter.HotRankAdapter;
import com.novel.collection.app.App;
import com.novel.collection.banner.Banner;
import com.novel.collection.base.BaseTabFragment;
import com.novel.collection.constant.Constant;
import com.novel.collection.constract.IMaleContract;
import com.novel.collection.entity.bean.CategoryNovels;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.bean.Wheel;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.presenter.MalePresenter;
import com.novel.collection.util.ImageLoaderUtils;
import com.novel.collection.util.NetUtil;
import com.novel.collection.view.activity.NovelIntroActivity;
import com.novel.collection.view.activity.RankingActivity;
import com.novel.collection.view.activity.SearchActivity;
import com.tmall.ultraviewpager.UltraViewPager;
import com.wzh.viewpager.indicator.UIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 发现页面的女生页
 *
 * @author
 * Created on 2019/11/4
 */
public class OverFragment extends BaseTabFragment<MalePresenter>
        implements IMaleContract.View {
    private RecyclerView mHotRankRv;
    private RecyclerView mCategoryNovelRv;
    private RecyclerView mNewRv;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mRefreshSrv;
    private FrameLayout frameLayout_banner;

    private CategoryAdapter mCategoryAdapter;
    private List<Noval_details> mNovelDataList = new ArrayList<>();
    private RelativeLayout rel_click_more;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_over;
    }

    private Banner banner;
    private UIndicator uIndicator4;
    int mess;
    RelativeLayout rel_hot,rv_new;
    @Override
    protected void initData() {
        Bundle bundle = this.getArguments();//得到从Activity传来的数据
        if (bundle != null) {
            mess = bundle.getInt("data");
        }

    }

    //private RollPagerView rollPagerView;

    @Override
    protected void initView() {
        TextView tv_hot_more=getActivity().findViewById(R.id.tv_over_category_more);
        tv_hot_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), RankingActivity.class);
                intent.putExtra("type",4);
                intent.putExtra("new_or_hot",1);
                getContext().startActivity(intent);
            }
        });
        rel_hot=getActivity().findViewById(R.id.rel_hot_over);
        rv_new=getActivity().findViewById(R.id.rv_new_over);
        getActivity().findViewById(R.id.iv_over_category_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), RankingActivity.class);
                intent.putExtra("type",4);
                intent.putExtra("new_or_hot",1);
                getContext().startActivity(intent);
            }
        });
        TextView tv_item_new_more=getActivity().findViewById(R.id.tv_over_new_more);
        tv_item_new_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), RankingActivity.class);
                intent.putExtra("type",4);
                intent.putExtra("new_or_hot",2);
                getContext().startActivity(intent);
            }
        });
        mHotRankRv = getActivity().findViewById(R.id.rv_over_hot_rank_recycler_view);
        mHotRankRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mCategoryNovelRv = getActivity().findViewById(R.id.rv_over_category_novel_list);
        mCategoryNovelRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNewRv=getActivity().findViewById(R.id.rv_over_new_novel_list);
        mNewRv.setLayoutManager(new GridLayoutManager(getContext(), 4));

        rel_click_more = getActivity().findViewById(R.id.click_more_over);
        mProgressBar = getActivity().findViewById(R.id.pb_over);

        mRefreshSrv = getActivity().findViewById(R.id.srv_over_refresh);
        mRefreshSrv.setColorSchemeColors(getResources().getColor(R.color.red_aa));   //设置颜色
        mRefreshSrv.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新时的操作
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                        requestUpdate();
//                    }
//                }, 500);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //if(mRefreshSrv.isRefreshing()) {
                            mRefreshSrv.setRefreshing(false);
                        //}
                    }
                }, 3000);
            }
        });
        frameLayout_banner = getActivity().findViewById(R.id.banner_over);
        banner = getActivity().findViewById(R.id.ultra_viewpager_over);

        uIndicator4 = getActivity().findViewById(R.id.indicator_over);
//        DemoPagerAdapter mAdapter = new DemoPagerAdapter(getList());
//        mViewPager4.setAdapter(mAdapter);
//        uIndicator4.attachToViewPager(mViewPager4.getViewPager());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            banner.stopVideo();
        }else {
            banner.stratVideo();
        }
    }

    @Override
    protected void doInOnCreate() {
        requestUpdate();
        mProgressBar.setVisibility(View.VISIBLE);
        frameLayout_banner.setVisibility(View.GONE);
    }

    private void requestUpdate() {
        mPresenter.getHotRankData(mess + "");
        mPresenter.getCategoryNovels(mess + "");
        mPresenter.getNewRankData(mess + "");
        mPresenter.getListImage(mess + "");
    }

    @Override
    protected MalePresenter getPresenter() {
        return new MalePresenter();
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Override
    protected int getPosition() {
        return 1;
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
                        intent.putExtra("type", 4);
                        intent.putExtra("new_or_hot", 3);
                        intent.putExtra("category_id", novelNameList.get(position).getId());
                        startActivity(intent);
                    }
                });
        mCategoryAdapter.setType(4);
        mCategoryNovelRv.setAdapter(mCategoryAdapter);
    }
    CategoryinfoAdapter adapter;
    private void initAdapter() {
        adapter = new CategoryinfoAdapter(getContext(),
                mNovelDataList);
        adapter.setOnCategoryNovelListener(new CategoryinfoAdapter.CategoryNovelListener() {
            @Override
            public void clickItem(int novelName) {
                Intent intent = new Intent(getContext(), NovelIntroActivity.class);
                // 传递小说名，进入搜查页后直接显示该小说的搜查结果
                intent.putExtra("pid", mNovelDataList.get(novelName).getId() + "");
                startActivity(intent);
            }
        });
        mHotRankRv.setAdapter(adapter);
    }
    CategoryzyAdapter categoryzyAdapter;
    private void initnewAdapter() {
        categoryzyAdapter = new CategoryzyAdapter(getContext(),
                noval_detailsList);
        categoryzyAdapter.setOnCategoryNovelListener(new CategoryzyAdapter.CategoryNovelListener() {
            @Override
            public void clickItem(int position) {
                if(position<noval_detailsList.size()) {
                    Intent intent = new Intent(getContext(), NovelIntroActivity.class);
                    // 传递小说名，进入搜查页后直接显示该小说的搜查结果
                    intent.putExtra("pid", noval_detailsList.get(position).getId() + "");
                    startActivity(intent);
                }
            }
        });
        mNewRv.setAdapter(categoryzyAdapter);
    }

    List<CategoryNovels> novelNameList = new ArrayList<>();
    List<Noval_details> noval_detailsList = new ArrayList<>();

    @Override
    public void getNewDataSuccess(List<Noval_details> novelNameList) {
        Log.e("QQQ", "getNewDataSuccess: "+novelNameList.size());
        if(novelNameList.size()==0){
            rv_new.setVisibility(View.GONE);
        }else {
            rv_new.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(View.GONE);
        mRefreshSrv.setRefreshing(false);
        if (novelNameList.isEmpty()) {
            return;
        }
        noval_detailsList = novelNameList;
        if (categoryzyAdapter == null) {
            noval_detailsList = novelNameList;
            initnewAdapter();
        } else {
            noval_detailsList.clear();
            noval_detailsList.addAll(novelNameList);
            categoryzyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void getNewDataError(String errorMsg) {
        mProgressBar.setVisibility(View.GONE);
        mRefreshSrv.setRefreshing(false);
    }

    /**
     * 获取热门排行榜数据成功
     */
    List<CategoryNovels> categoryNovelsList=new ArrayList<>();
    @Override
    public void getHotRankDataSuccess(List<CategoryNovels> novelNameList_one) {
        categoryNovelsList.clear();
        for(int i=0;i<novelNameList_one.size();i++){
            if(novelNameList_one.get(i).getData_list().size()!=0){
                categoryNovelsList.add(novelNameList_one.get(i));
            }
        }
        mProgressBar.setVisibility(View.GONE);
        mRefreshSrv.setRefreshing(false);
        if (categoryNovelsList.isEmpty()) {
            return;
        }
        this.novelNameList = categoryNovelsList;
        if (mCategoryAdapter == null) {
            initCategoryAdapter();
        } else {
            mCategoryAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取热门排行榜数据失败
     */
    @Override
    public void getHotRankDataError(String errorMsg) {
        mProgressBar.setVisibility(View.GONE);
        mRefreshSrv.setRefreshing(false);
    }

    /**
     * 获取分类小说数据成功
     */
    @Override
    public void getCategoryNovelsSuccess(List<Noval_details> dataList) {
        if(dataList.size()==0){
            rel_hot.setVisibility(View.GONE);
        }else {
            rel_hot.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(View.GONE);
        mRefreshSrv.setRefreshing(false);
        frameLayout_banner.setVisibility(View.VISIBLE);
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

        rel_click_more.setVisibility(View.VISIBLE);
        rel_click_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), RankingActivity.class);
                intent.putExtra("type",4);
                intent.putExtra("new_or_hot",1);
                getContext().startActivity(intent);
            }
        });
    }

    /**
     * 获取分类小说数据失败
     */
    @Override
    public void getCategoryNovelsError(String errorMsg) {
        mProgressBar.setVisibility(View.GONE);
        mRefreshSrv.setRefreshing(false);
        frameLayout_banner.setVisibility(View.GONE);
    }

    @Override
    public void getListImageSuccess(List<Wheel> novalDetailsList) {
        if(novalDetailsList.size()==0){
            return;
        }else {
//            DemoPagerAdapter mAdapter = new DemoPagerAdapter(novalDetailsList);
//            mViewPager4.setAdapter(mAdapter);
//            uIndicator4.attachToViewPager(mViewPager4.getViewPager());
            initData1(novalDetailsList);
            initView1();
        }
    }

    @Override
    public void getListImageError(String errorMsg) {

    }

    private void jump2Search(String name) {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        // 传递小说名，进入搜查页后直接显示该小说的搜查结果
        intent.putExtra(SearchActivity.KEY_NOVEL_NAME, name);
        startActivity(intent);
    }

    List<Wheel> list;
    void initData1(List<Wheel> novalDetailsList){
        list = novalDetailsList;
//        for(int i=0;i<novalDetailsList.size();i++) {
//            String url;
//            if(novalDetailsList.get(i).getPicpath().contains("http:")){
//                url=novalDetailsList.get(i).getPicpath();
//            }else {
//                url=UrlObtainer.GetUrl()+"/"+novalDetailsList.get(i).getPicpath();
//            }
//            if(novalDetailsList.get(i).getTypes().equals("2")) {
//                HttpProxyCacheServer proxy = App.getProxy(getContext());
//                String proxyUrl = proxy.getProxyUrl(url);
//                list.add(proxyUrl);
//            }else {
//                list.add(url);
//            }
//        }
    }

    void initView1(){
        banner.setDataList(list);
        banner.setImgDelyed(3000);
        banner.startBanner();
        uIndicator4.attachToViewPager(banner.getViewPager());
        banner.startAutoPlay();
    }

    @Override
    public void onDestroy() {
        banner.destroy();
        super.onDestroy();
    }
}
