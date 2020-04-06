package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.NovelSourceAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.app.App;
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
import com.example.administrator.xiaoshuoyuedushenqi.widget.WrapContentLinearLayoutManager;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReadrecoderActivity extends BaseActivity<ReadcoredPresenter> implements IReakcoredContract.View {
    private DatabaseManager mDbManager;
    private Login_admin login_admin;
    private RecyclerView recyclerView;
    NovelSourceAdapter novelSourceAdapter;
    private ProgressBar mProgressBar;
    private RefreshLayout mRefreshSrv;
    private TextView tv_nodata;
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
        //Log.e("ZZZ", "initData: "+login_admin);
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
                               }
                                mDbManager.deleteReadCoder();
//                               if(login_admin==null) {
                                   requestPost();
//                               }
                            }

                            @Override
                            public void clickCancel() {

                            }
                        })
                        .build();
                tipDialog.show();
            }
        });
        tv_nodata=findViewById(R.id.tv_nodata);
        recyclerView = findViewById(R.id.recycle_readcord);
      // recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
      //  recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //showSearchResFg();
        mProgressBar = findViewById(R.id.pb_male);
        mRefreshSrv = findViewById(R.id.srv_male_refresh);

        mRefreshSrv.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                z=1;
                isRefresh=true;
                requestPost();
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                //refreshlayout.finishLoadMore(false);
            }
        });
        mRefreshSrv.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                z++;
                requestPost();
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
                // refreshlayout.finishLoadMore(false);
            }
        });
        ClassicsHeader classicsHeader=new ClassicsHeader(this);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(new Date());
        classicsHeader.setLastUpdateText("最后更新:"+dateString);
        //refreshLayout.setRefreshHeader(new BezierRadarHeader(getContext()).setEnableHorizontalDrag(true));
        mRefreshSrv.setRefreshHeader(classicsHeader);
        //refreshLayout.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshSrv.setRefreshFooter(new ClassicsFooter(this));
    }
    int z=1;
    boolean isRefresh;
    public String getIds(){
        StringBuilder sb=new StringBuilder();
        for(int z=0;z<noval_readcoreds.size();z++){
            if(z==noval_readcoreds.size()-1){
                sb.append(noval_readcoreds.get(z).getNovel_id());
            }else {
                sb.append(noval_readcoreds.get(z).getNovel_id()+",");
            }
        }
        return sb.toString();
    }
    @Override
    protected void doAfterInit() {
        mProgressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        requestPost();

    }

    private void requestPost(){
        if (login_admin != null) {
            mPresenter.getReadcoredData(login_admin.getToken(), z+"");
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
        recyclerView.setVisibility(View.VISIBLE);
        if(novelNameList.size()==0){
            tv_nodata.setVisibility(View.VISIBLE);
        }else {
            tv_nodata.setVisibility(View.GONE);
        }
//        if (novelNameList.isEmpty()) {
//            return;
//        }
        noval_readcoreds.clear();
        setRecyclerViewData(novelNameList);
    }
    public void setRecyclerViewData(List<Noval_Readcored> novelNameList){
        if(isRefresh==true){
            noval_readcoreds.clear();
            isRefresh=false;
            novelSourceAdapter=null;
        }
        noval_readcoreds.addAll(novelNameList);//new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        int last=linearLayoutManager.findLastVisibleItemPosition();
        if (novelSourceAdapter == null) {
            noval_readcoreds = novelNameList;
            recyclerView.setLayoutManager(linearLayoutManager);
            //recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
            initAdapter();
            recyclerView.setFocusableInTouchMode(false);
            recyclerView.setScrollingTouchSlop(last);
        } else {
            //mNovelAdapter.notifyDataSetChanged();
            novelSourceAdapter.notifyItemChanged(last,0);
        }
    }
    @Override
    public void queryAllBookError(String errorMsg) {
        showShortToast(errorMsg);
    }

    @Override
    public void getReadcoredDataSuccess(List<Noval_Readcored> novelNameList) {
        mProgressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
       if(novelNameList.size()==0&&z==1) {
            mPresenter.queryAllBook();
        }else {
            setRecyclerViewData(novelNameList);
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
                String weigh=noval_readcoreds.get(position).getWeigh();
                intent.putExtra("weigh",Integer.parseInt(weigh));//
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
                                    mPresenter.getDelectReadcoredData(login_admin.getToken(),noval_readcoreds.get(position).getNovel_id());
                                }

                                if(mDbManager.isExistInReadCoderNovel(noval_readcoreds.get(position).getNovel_id())) {
                                    mDbManager.deleteBookReadcoderNovel(noval_readcoreds.get(position).getNovel_id());
                                }

                                if(login_admin==null){
                                    requestPost();
                                }
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
        isRefresh=true;
        requestPost();
    }

    @Override
    public void getDelectReadcoredDataError(String errorMsg) {
        showShortToast(errorMsg);
        isRefresh=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        App app= (App) getApplication();
        //App.init(this);
        if(app.isNight()==true) {
            app.setNight(false);
//            App.updateNightMode(SpUtil.getIsNightMode());
           finish();
            //SpUtil.saveIsNightMode(SpUtil.getIsNightMode());
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            AppCompatDelegate.setDefaultNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            Intent intent = new Intent(this, this.getClass());
            //intent.putExtra("is_naghit", "2");
            startActivity(intent);
            overridePendingTransition(R.anim.activity_in,R.anim.activity_out);
        }
    }
}
