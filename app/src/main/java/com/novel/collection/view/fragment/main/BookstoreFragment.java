package com.novel.collection.view.fragment.main;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.novel.collection.R;
import com.novel.collection.adapter.NormalViewPagerAdapter;
import com.novel.collection.base.BaseFragment;
import com.novel.collection.base.BasePresenter;
import com.novel.collection.util.EnhanceTabLayout;
import com.novel.collection.util.NetUtil;
import com.novel.collection.view.activity.SearchActivity;
import com.novel.collection.view.fragment.bookstore.ExclusivelistGFragment;
import com.novel.collection.view.fragment.bookstore.FemaleLikeFragment;
import com.novel.collection.view.fragment.bookstore.MaleLikeFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 书库模块
 * Created on 2020/2/20
 */
public class BookstoreFragment extends BaseFragment implements View.OnClickListener{
    private ImageView mSearchView;
    private TextView mAllBookTv;
    private EnhanceTabLayout mTabLayout;
    private ViewPager mViewPager;

    private List<Fragment> mFragmentList = new ArrayList<>();   // 碎片集合
    private List<String> mPageTitleList = new ArrayList<>();    // tab 的标题集合

    @Override
    protected void doInOnCreate() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bookstore;
    }

    @Override
    protected void initData() {
        mFragmentList.add(new MaleLikeFragment());
        mFragmentList.add(new FemaleLikeFragment());
        mFragmentList.add(new ExclusivelistGFragment());

        mPageTitleList.add("男生喜欢");
        mPageTitleList.add("女生喜欢");
        mPageTitleList.add("专属榜单");
    }

    @Override
    protected void initView() {
        mSearchView = getActivity().findViewById(R.id.iv_discovery_search_icon1);
        mSearchView.setOnClickListener(this);
        mAllBookTv = getActivity().findViewById(R.id.tv_discovery_all_book1);
        mAllBookTv.setOnClickListener(this);

        mTabLayout = getActivity().findViewById(R.id.tv_discovery_tab_layout1);
        // TabLayout + ViewPager
        mViewPager = getActivity().findViewById(R.id.vp_discovery_view_pager1);
        // 在 Fragment 中只能使用 getChildFragmentManager() 获取 FragmentManager 来处理子 Fragment
        mViewPager.setAdapter(new NormalViewPagerAdapter(getChildFragmentManager(),
                mFragmentList, mPageTitleList));
        // 缓存左右两侧的两个页面（很重要！！！，不设置这个切换到前两个的时候就会重新加载数据）
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mTabLayout.getmTabLayout().getTabAt(i).select();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        for(int i=0;i<mPageTitleList.size();i++){
            mTabLayout.addTab(mPageTitleList.get(i));
        }
//        mTabLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                setScale(0, DiscoveryPageTransformer.MAX_SCALE);
//            }
//        });
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getmTabLayout().getTabAt(0).select();
//        mViewPager.setPageTransformer(false, new DiscoveryPageTransformer(mTabLayout.getmTabLayout()));
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_discovery_search_icon1:
                jump2Activity(SearchActivity.class);
                break;
            case R.id.tv_discovery_all_book1:
                if (!NetUtil.hasInternet(getActivity())) {
                    showShortToast("当前无网络，请检查网络后重试");
                    return;
                }
                break;
            default:
                break;
        }
    }

}
