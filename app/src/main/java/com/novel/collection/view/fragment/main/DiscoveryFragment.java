package com.novel.collection.view.fragment.main;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.novel.collection.R;
import com.novel.collection.base.BaseFragment;
import com.novel.collection.base.BasePresenter;
import com.novel.collection.util.EnhanceTabLayout;
import com.novel.collection.util.NetUtil;
import com.novel.collection.view.activity.MainActivity;
import com.novel.collection.view.activity.SearchActivity;
import com.novel.collection.view.fragment.discovery.FemaleFragment;
import com.novel.collection.view.fragment.discovery.MaleFragment;
import com.novel.collection.view.fragment.discovery.OverFragment;
import com.novel.collection.view.fragment.discovery.PressFragment;

import java.util.ArrayList;
import java.util.List;

//精选模块

public class DiscoveryFragment extends BaseFragment implements View.OnClickListener{

    private static final String TAG = "fzh";
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
        return R.layout.fragment_discovery;
    }

    @Override
    protected void initData() {
        mPageTitleList.add(getString(R.string.discovery_select));
        mPageTitleList.add(getString(R.string.discovery_male));
        mPageTitleList.add(getString(R.string.discovery_female));
        mPageTitleList.add(getString(R.string.discovery_press));
    }

    int select_position=0;
    @Override
    protected void initView() {
        mSearchView = getActivity().findViewById(R.id.iv_discovery_search_icon);
        mSearchView.setOnClickListener(this);
        mAllBookTv = getActivity().findViewById(R.id.tv_discovery_all_book);
        mAllBookTv.setOnClickListener(this);

        mTabLayout = getActivity().findViewById(R.id.tv_discovery_tab_layout);
        for(int i=0;i<mPageTitleList.size();i++){
            mTabLayout.addTab(mPageTitleList.get(i));
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeFragment(tab.getPosition()+1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        changeFragment(1);
//        mViewPager.setPageTransformer(false, new DiscoveryPageTransformer(mTabLayout.getmTabLayout()));
    }
    private FragmentManager mFragmentManager ;
    Fragment mCurrFragment;
    private Fragment mBookshelfFragment;//书架
    private Fragment mDiscoveryFragment;//发现
    private Fragment mBookstorFragment;//发现
    private Fragment mBookoverFragment;//发现
    public void changeFragment(int i) {
        MainActivity mainActivity= (MainActivity) getActivity();
        mFragmentManager =mainActivity.getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment showFragment = null;
        switch (i) {
            case 1:
                if (mBookshelfFragment == null) {
                    MaleFragment maleFragment=new MaleFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("data",1);
                    maleFragment.setArguments(bundle);
                    mBookshelfFragment = maleFragment;
                    ft.add(R.id.fv_main_fragment_container1, mBookshelfFragment);
                }
                showFragment = mBookshelfFragment;
                break;
            case 2:
                if (mDiscoveryFragment == null) {
                    PressFragment maleFragment=new PressFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("data",2);
                    maleFragment.setArguments(bundle);
                    mDiscoveryFragment = maleFragment;
                    ft.add(R.id.fv_main_fragment_container1, mDiscoveryFragment);
                }
                showFragment = mDiscoveryFragment;
                break;
                case 3:
                if (mBookstorFragment == null) {
                    FemaleFragment maleFragment=new FemaleFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("data",3);
                    maleFragment.setArguments(bundle);
                    mBookstorFragment = maleFragment;
                    ft.add(R.id.fv_main_fragment_container1, mBookstorFragment);
                }
                showFragment = mBookstorFragment;
                break;
            case 4:
                if (mBookoverFragment == null) {
                    OverFragment maleFragment=new OverFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("data",4);
                    maleFragment.setArguments(bundle);
                    mBookoverFragment = maleFragment;
                    ft.add(R.id.fv_main_fragment_container1, mBookoverFragment);
                }
                showFragment = mBookoverFragment;
                break;
            default:
                break;
        }
        // 隐藏当前的 Fragment，显示新的 Fragment
        if (mCurrFragment != null) {
            ft.hide(mCurrFragment);
        }
        if (showFragment != null) {
            ft.show(showFragment);
        }
        mCurrFragment = showFragment;

        ft.commitAllowingStateLoss();
    }
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //if(hidden){
            mCurrFragment.onHiddenChanged(hidden);
            //banner.stopVideo();
//        }else {
//            //banner.stratVideo();
//        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            mTabLayout.getmTabLayout().getTabAt(select_position).select();
        }
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
            case R.id.iv_discovery_search_icon:
                jump2Activity(SearchActivity.class);
                break;
            case R.id.tv_discovery_all_book:
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
