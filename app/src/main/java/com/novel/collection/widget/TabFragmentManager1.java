package com.novel.collection.widget;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Iterator;
import java.util.List;

import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.widget.TabView;

public class TabFragmentManager1 {
    private FragmentManager mManager;
    private int mContainerResid;
    private List<Fragment> mFragments;
    private VerticalTabLayout1 mTabLayout;
    private VerticalTabLayout1.OnTabSelectedListener mListener;

    public TabFragmentManager1(FragmentManager manager, List<Fragment> fragments, VerticalTabLayout1 tabLayout) {
        this.mManager = manager;
        this.mFragments = fragments;
        this.mTabLayout = tabLayout;
        this.mListener = (VerticalTabLayout1.OnTabSelectedListener) new OnFragmentTabSelectedListener();
        this.mTabLayout.addOnTabSelectedListener(this.mListener);
    }

    public TabFragmentManager1(FragmentManager manager, int containerResid, List<Fragment> fragments, VerticalTabLayout1 tabLayout) {
        this(manager, fragments, tabLayout);
        this.mContainerResid = containerResid;
        this.changeFragment();
    }

    public void changeFragment() {
        FragmentTransaction ft = this.mManager.beginTransaction();
        int position = this.mTabLayout.getSelectedTabPosition();
        List<Fragment> addedFragments = this.mManager.getFragments();

        for(int i = 0; i < this.mFragments.size(); ++i) {
            Fragment fragment = (Fragment)this.mFragments.get(i);
            if ((addedFragments == null || !addedFragments.contains(fragment)) && this.mContainerResid != 0) {
                ft.add(this.mContainerResid, fragment);
            }

            if ((this.mFragments.size() <= position || i != position) && (this.mFragments.size() > position || i != this.mFragments.size() - 1)) {
                ft.hide(fragment);
            } else {
                ft.show(fragment);
            }
        }

        ft.commit();
        this.mManager.executePendingTransactions();
    }

    public void detach() {
        FragmentTransaction ft = this.mManager.beginTransaction();
        Iterator var2 = this.mFragments.iterator();

        while(var2.hasNext()) {
            Fragment fragment = (Fragment)var2.next();
            ft.remove(fragment);
        }

        ft.commit();
        this.mManager.executePendingTransactions();
        this.mManager = null;
        this.mFragments = null;
        this.mTabLayout.removeOnTabSelectedListener(this.mListener);
        this.mListener = null;
        this.mTabLayout = null;
    }

    private class OnFragmentTabSelectedListener implements VerticalTabLayout.OnTabSelectedListener {
        private OnFragmentTabSelectedListener() {
        }

        public void onTabSelected(TabView tab, int position) {
            TabFragmentManager1.this.changeFragment();
        }

        public void onTabReselected(TabView tab, int position) {
        }
    }
}
