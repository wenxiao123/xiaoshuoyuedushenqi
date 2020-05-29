package com.novel.collection.view.activity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novel.collection.R;
import com.novel.collection.adapter.AppRecyleAdapter;
import com.novel.collection.base.BaseActivity;
import com.novel.collection.base.BasePresenter;
import com.novel.collection.util.StatusBarUtil;

public class SuishizhuanxianjinActivity extends BaseActivity {

    RecyclerView recyclerView_apps;
    RecyclerView recyclerView_news;
    String[] strings={"邀请好友","装机必备","玩游戏","我的钱包"};
    int[] ints={R.mipmap.invite_goodfriend,R.mipmap.icon_install,R.mipmap.icon_game,R.mipmap.icon_wallet};
    AppRecyleAdapter appRecyleAdapter;
    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.zhaunxianjin;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        recyclerView_apps=findViewById(R.id.recycle_apps);
        recyclerView_apps.setLayoutManager(new GridLayoutManager(this,4));
        recyclerView_news=findViewById(R.id.recycle_news);
    }

    @Override
    protected void doAfterInit() {
        appRecyleAdapter=new AppRecyleAdapter(this,ints,strings);
        recyclerView_apps.setAdapter(appRecyleAdapter);
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }
}
