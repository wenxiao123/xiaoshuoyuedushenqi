package com.example.administrator.xiaoshuoyuedushenqi.view.fragment.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.NormalViewPagerAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseFragment;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.EnhanceTabLayout;
import com.example.administrator.xiaoshuoyuedushenqi.util.NetUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.AllNovelActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.SearchActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.fragment.discovery.FemaleFragment;
import com.example.administrator.xiaoshuoyuedushenqi.view.fragment.discovery.MaleFragment;
import com.example.administrator.xiaoshuoyuedushenqi.view.fragment.discovery.PressFragment;
import com.example.administrator.xiaoshuoyuedushenqi.widget.DiscoveryPageTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WX
 * Created on 2020/2/20
 */
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
        MaleFragment maleFragment1=new MaleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("data",1);
        maleFragment1.setArguments(bundle);
        mFragmentList.add(maleFragment1);

        MaleFragment maleFragment2=new MaleFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("data",2);
        maleFragment2.setArguments(bundle2);
        mFragmentList.add(maleFragment2);

        MaleFragment maleFragment3=new MaleFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putInt("data",3);
        maleFragment3.setArguments(bundle3);
        mFragmentList.add(maleFragment3);

        //mFragmentList.add(new PressFragment());

        mPageTitleList.add(getString(R.string.discovery_select));
        mPageTitleList.add(getString(R.string.discovery_male));
        mPageTitleList.add(getString(R.string.discovery_female));
        //mPageTitleList.add(getString(R.string.discovery_press));
    }

    int select_position=0;
    @Override
    protected void initView() {
        mSearchView = getActivity().findViewById(R.id.iv_discovery_search_icon);
        mSearchView.setOnClickListener(this);
        mAllBookTv = getActivity().findViewById(R.id.tv_discovery_all_book);
        mAllBookTv.setOnClickListener(this);

        mTabLayout = getActivity().findViewById(R.id.tv_discovery_tab_layout);
        // TabLayout + ViewPager
        mViewPager = getActivity().findViewById(R.id.vp_discovery_view_pager);
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
                select_position=i;
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
                jump2Activity(AllNovelActivity.class);
                break;
            default:
                break;
        }
    }
}
