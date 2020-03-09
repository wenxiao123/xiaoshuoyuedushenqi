package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.SortAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.FileInfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.PersonBean;
import com.example.administrator.xiaoshuoyuedushenqi.util.PinyinUtils;
import com.example.administrator.xiaoshuoyuedushenqi.util.SDCardHelper;
import com.example.administrator.xiaoshuoyuedushenqi.util.SideBar;
import com.example.administrator.xiaoshuoyuedushenqi.view.fragment.search.SearchResultFragment;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ReadrecoderActivity extends BaseActivity {
    private DatabaseManager mDbManager;
    @Override
    protected void doBeforeSetContentView() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.bookrecoder;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        mDbManager = DatabaseManager.getInstance();
    }


    @Override
    protected void initView() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        showSearchResFg();
    }

    @Override
    protected void doAfterInit() {

    }
    Fragment mSearchResultFragment;
    private FragmentManager mFragmentManager = getSupportFragmentManager();
    /**
     * 隐藏历史搜索 Fg，显示搜索结果 Fg
     */
    private void showSearchResFg() {
        if (mSearchResultFragment != null) {
            return;
        }
        mSearchResultFragment = SearchResultFragment.newInstance(
                "神级");
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.fv_search_container, mSearchResultFragment);
        ft.show(mSearchResultFragment);
        ft.commit();
    }
    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }
}
