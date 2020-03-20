package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.NovelSourceAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IReakcoredContract;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_Readcored;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.ReadcoredPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.FileUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.fragment.search.SearchResultFragment;
import com.example.administrator.xiaoshuoyuedushenqi.widget.TipDialog;

import java.util.ArrayList;
import java.util.List;

public class ReadrecoderActivity extends BaseActivity<ReadcoredPresenter> implements IReakcoredContract.View {
    private DatabaseManager mDbManager;
    private Login_admin login_admin;
    private RecyclerView recyclerView;
    NovelSourceAdapter novelSourceAdapter;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mRefreshSrv;
    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.bookrecoder;
    }

    @Override
    protected ReadcoredPresenter getPresenter() {
        return new ReadcoredPresenter();
    }

    @Override
    protected void initData() {
        mDbManager = DatabaseManager.getInstance();
        login_admin = (Login_admin) SpUtil.readObject(this);
    }


    @Override
    protected void initView() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.iv_clear_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TipDialog tipDialog = new TipDialog.Builder(ReadrecoderActivity.this)
                        .setContent("是否清空阅读记录")
                        .setCancel("取消")
                        .setEnsure("确定")
                        .setOnClickListener(new TipDialog.OnClickListener() {
                            @Override
                            public void clickEnsure() {
                               if(login_admin!=null){
                                   mPresenter.getDelectReadcoredData(login_admin.getToken(),getIds());
                               }else {
                                   for(int z=0;z<noval_readcoreds.size();z++){
                                       mDbManager.deleteBookReadcoderNovel(noval_readcoreds.get(z).getNovel_id());
                                   }
                               }
                                requestPost();
                            }

                            @Override
                            public void clickCancel() {

                            }
                        })
                        .build();
                tipDialog.show();
            }
        });
        recyclerView = findViewById(R.id.recycle_readcord);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //showSearchResFg();
        mProgressBar = findViewById(R.id.pb_male);
        mRefreshSrv = findViewById(R.id.srv_male_refresh);
        mRefreshSrv.setColorSchemeColors(getResources().getColor(R.color.colorAccent));   //设置颜色
        mRefreshSrv.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新时的操作
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 500);
            }
        });
    }
    public String getIds(){
        StringBuilder sb=new StringBuilder();
        for(int z=0;z<noval_readcoreds.size();z++){
            if(z==noval_readcoreds.size()-1){
                sb.append(noval_readcoreds.get(z).getId());
            }else {
                sb.append(noval_readcoreds.get(z).getId()+",");
            }
        }
        return sb.toString();
    }
    @Override
    protected void doAfterInit() {
        requestPost();

    }
    private void requestPost(){
        mProgressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        if (login_admin != null) {
            mPresenter.getReadcoredData(login_admin.getToken(), "1");
        } else {
            mPresenter.queryAllBook();
        }
    }
    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    List<Noval_Readcored> noval_readcoreds = new ArrayList<>();

    @Override
    public void queryAllBookSuccess(List<Noval_Readcored> novelNameList) {
        mProgressBar.setVisibility(View.GONE);
        mRefreshSrv.setRefreshing(false);
        recyclerView.setVisibility(View.VISIBLE);
        if (novelNameList.isEmpty()) {
            return;
        }
        if (novelSourceAdapter == null) {
            noval_readcoreds = novelNameList;
            initAdapter();
        } else {
            noval_readcoreds.clear();
            noval_readcoreds.addAll(novelNameList);
            novelSourceAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void queryAllBookError(String errorMsg) {
        showShortToast(errorMsg);
    }

    @Override
    public void getReadcoredDataSuccess(List<Noval_Readcored> novelNameList) {
        mProgressBar.setVisibility(View.GONE);
        mRefreshSrv.setRefreshing(false);
        if(novelNameList.size()==0){
            mPresenter.queryAllBook();
        }
        recyclerView.setVisibility(View.VISIBLE);
        if (novelNameList.isEmpty()) {
            return;
        }
        if (novelSourceAdapter == null) {
            noval_readcoreds = novelNameList;
            initAdapter();
        } else {
            noval_readcoreds.clear();
            noval_readcoreds.addAll(novelNameList);
            novelSourceAdapter.notifyDataSetChanged();
        }
    }

    private void initAdapter() {
        novelSourceAdapter = new NovelSourceAdapter(this, noval_readcoreds);
        novelSourceAdapter.setOnNovelSourceListener(new NovelSourceAdapter.NovelSourceListener() {
            @Override
            public void clickItem(int position) {
                Intent intent = new Intent(ReadrecoderActivity.this, ReadActivity.class);
                intent.putExtra(ReadActivity.KEY_NOVEL_URL, noval_readcoreds.get(position).getNovel_id());
                intent.putExtra(ReadActivity.KEY_ZJ_ID, noval_readcoreds.get(position).getChapter_id());
                intent.putExtra(ReadActivity.KEY_NAME, noval_readcoreds.get(position).getTitle());
                intent.putExtra("first_read", 0);
                intent.putExtra("weigh", noval_readcoreds.get(position).getWeigh());//
                intent.putExtra(ReadActivity.KEY_COVER, noval_readcoreds.get(position).getPic());
                intent.putExtra(ReadActivity.KEY_POSITION, 1);
                intent.putExtra(ReadActivity.KEY_CHAPTER_INDEX, position);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void longclickItem(int position) {
                final TipDialog tipDialog = new TipDialog.Builder(ReadrecoderActivity.this)
                        .setContent("是否清除阅读记录")
                        .setCancel("取消")
                        .setEnsure("确定")
                        .setOnClickListener(new TipDialog.OnClickListener() {
                            @Override
                            public void clickEnsure() {
                                if(login_admin!=null){
                                    mPresenter.getDelectReadcoredData(login_admin.getToken(),noval_readcoreds.get(position).getId());
                                }else {
                                    mDbManager.deleteBookReadcoderNovel(noval_readcoreds.get(position).getNovel_id());
                                }
                                requestPost();
                            }

                            @Override
                            public void clickCancel() {

                            }
                        })
                        .build();
                tipDialog.show();
            }
        });
        recyclerView.setAdapter(novelSourceAdapter);
    }

    @Override
    public void getReadcoredDataError(String errorMsg) {
        showShortToast(errorMsg);
    }

    @Override
    public void getDelectReadcoredDataSuccess(String msg) {
        showShortToast(msg);
    }

    @Override
    public void getDelectReadcoredDataError(String errorMsg) {
        showShortToast(errorMsg);
    }
}
