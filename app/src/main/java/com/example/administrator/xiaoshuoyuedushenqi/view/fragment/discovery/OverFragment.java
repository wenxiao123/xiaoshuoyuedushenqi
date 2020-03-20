package com.example.administrator.xiaoshuoyuedushenqi.view.fragment.discovery;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.PagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.CategoryAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.CategoryinfoAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.CategoryzyAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.HotRankAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseTabFragment;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IMaleContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovels;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Wheel;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.MalePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.NetUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.AllNovelActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.NovelIntroActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.RankingActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.SearchActivity;
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
    private HotRankAdapter mHotRankAdapter;
    private List<CategoryNovels> mHotRankNovelNameList;

    private CategoryAdapter mCategoryAdapter;
    private List<String> mCategoryNameList = new ArrayList<>();
    private List<String> mMoreList = new ArrayList<>();
    private List<Noval_details> mNovelDataList = new ArrayList<>();

    private boolean mIsVisited = false;
    private boolean mIsLoadedData = false;
    private boolean mIsCreatedView = false;
    private RelativeLayout rel_click_more;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_over;
    }

    private UltraViewPager mViewPager4;
    private UIndicator uIndicator4;
    int mess;

    @Override
    protected void initData() {
        mCategoryNameList.add("热血玄幻");
        mCategoryNameList.add("都市生活");
        mCategoryNameList.add("武侠世界");
        mMoreList.add("更多玄幻小说");
        mMoreList.add("更多都市小说");
        mMoreList.add("更多武侠小说");
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
        mHotRankRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mCategoryNovelRv = getActivity().findViewById(R.id.rv_over_category_novel_list);
        mCategoryNovelRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNewRv=getActivity().findViewById(R.id.rv_over_new_novel_list);
        mNewRv.setLayoutManager(new GridLayoutManager(getContext(), 4));

        rel_click_more = getActivity().findViewById(R.id.click_more_over);
        mProgressBar = getActivity().findViewById(R.id.pb_over);

        mRefreshSrv = getActivity().findViewById(R.id.srv_over_refresh);
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
        frameLayout_banner = getActivity().findViewById(R.id.banner_over);
        mViewPager4 = getActivity().findViewById(R.id.ultra_viewpager_over);

        uIndicator4 = getActivity().findViewById(R.id.indicator_over);
//        DemoPagerAdapter mAdapter = new DemoPagerAdapter(getList());
//        mViewPager4.setAdapter(mAdapter);
//        uIndicator4.attachToViewPager(mViewPager4.getViewPager());
    }

    public List<String> getList() {
        List<String> list = new ArrayList<>();
        list.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2324525330,735217211&fm=26&gp=0.jpg");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583493412540&di=599f980a65f0504e8a4b5ba8" +
                "aa1467d8&imgtype=0&src=http%3A%2F%2Fku.90sjimg.com%2Felement_origin_min_pic%2F00%2F00%2F07%2F095780b22836645.jpg");

        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583493412540&di=1a38d9fd56edff5397da03f150312dc1&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2Fe668b9fa8c1dd23e9cc39f0796eee9ab16de303b7129-NtiFlA_fw658");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583493412540&di=51a2a1f3111997801803bdca4b8d36da&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F059e265ab33640251cbf8eae08ea64ee53dcec21339cc-kNnnqu_fw658");

        return list;
    }

    public class DemoPagerAdapter extends PagerAdapter {
        private List<Wheel> views;

        public DemoPagerAdapter(List<Wheel> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //View view = views.get(position);
            ImageView imageView = new ImageView(getContext());
            SimpleTarget<Drawable> simpleTarget = new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    imageView.setBackground(resource);
                }
            };
            Glide.with(getContext()).load(UrlObtainer.GetUrl()+views.get(position).getPicpath())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.cover_place_holder)
                            .error(R.mipmap.admin))
                    .into(simpleTarget);
            container.addView(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), NovelIntroActivity.class);
                    // 传递小说名，进入搜查页后直接显示该小说的搜查结果
                    intent.putExtra("pid", views.get(position).getId() + "");
                    startActivity(intent);
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) instantiateItem(container, position));
        }
    }

    @Override
    protected void doInOnCreate() {
        requestUpdate();
        mProgressBar.setVisibility(View.VISIBLE);
        frameLayout_banner.setVisibility(View.GONE);
        mIsLoadedData = true;

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

    private void initAdapter() {
        CategoryinfoAdapter adapter = new CategoryinfoAdapter(getContext(),
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
    private void initnewAdapter() {
        CategoryzyAdapter adapter = new CategoryzyAdapter(getContext(),
                noval_detailsList);
        adapter.setOnCategoryNovelListener(new CategoryzyAdapter.CategoryNovelListener() {
            @Override
            public void clickItem(int novelName) {
                Intent intent = new Intent(getContext(), NovelIntroActivity.class);
                // 传递小说名，进入搜查页后直接显示该小说的搜查结果
                intent.putExtra("pid", noval_detailsList.get(novelName).getId() + "");
                startActivity(intent);
            }
        });
        mNewRv.setAdapter(adapter);
    }

    List<CategoryNovels> novelNameList = new ArrayList<>();
    List<Noval_details> noval_detailsList = new ArrayList<>();

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

    @Override
    public void getNewDataError(String errorMsg) {

    }

    /**
     * 获取热门排行榜数据成功
     */
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

    /**
     * 获取热门排行榜数据失败
     */
    @Override
    public void getHotRankDataError(String errorMsg) {

    }

    /**
     * 获取分类小说数据成功
     */
    @Override
    public void getCategoryNovelsSuccess(List<Noval_details> dataList) {
        mProgressBar.setVisibility(View.GONE);
        mRefreshSrv.setRefreshing(false);
        frameLayout_banner.setVisibility(View.VISIBLE);
        if (dataList.isEmpty()) {
            return;
        }
        if (mCategoryAdapter == null) {
            mNovelDataList = dataList;
            initAdapter();
        } else {
            mNovelDataList.clear();
            mNovelDataList.addAll(dataList);
            mCategoryAdapter.notifyDataSetChanged();
        }

        rel_click_more.setVisibility(View.VISIBLE);
        rel_click_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AllNovelActivity.class);
                intent.putExtra(AllNovelActivity.KEY_GENDER, 0);   // 性别
                intent.putExtra(AllNovelActivity.KEY_MAJOR, Constant.CATEGORY_MAJOR_ );     // 一级分类
                startActivity(intent);
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
            DemoPagerAdapter mAdapter = new DemoPagerAdapter(novalDetailsList);
            mViewPager4.setAdapter(mAdapter);
            uIndicator4.attachToViewPager(mViewPager4.getViewPager());
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
}
