package com.novel.collection.view.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.novel.collection.R;
import com.novel.collection.adapter.NovelSourceAdapter;
import com.novel.collection.app.App;
import com.novel.collection.base.BaseActivity;
import com.novel.collection.base.BasePresenter;
import com.novel.collection.constract.IReakcoredContract;
import com.novel.collection.db.DatabaseManager;
import com.novel.collection.entity.bean.Login_admin;
import com.novel.collection.entity.bean.Noval_Readcored;
import com.novel.collection.entity.data.BookshelfNovelDbData;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.presenter.ReadcoredPresenter;
import com.novel.collection.util.FileUtil;
import com.novel.collection.util.SpUtil;
import com.novel.collection.util.StatusBarUtil;
import com.novel.collection.view.fragment.search.SearchResultFragment;
import com.novel.collection.widget.TipDialog;
import com.novel.collection.widget.WrapContentLinearLayoutManager;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ReadrecoderActivity extends BaseActivity<ReadcoredPresenter> implements IReakcoredContract.View {
    private DatabaseManager mDbManager;
    private Login_admin login_admin;
    private RecyclerView recyclerView;
    NovelSourceAdapter novelSourceAdapter;
    private ProgressBar mProgressBar;
    private RefreshLayout mRefreshSrv;
    private TextView tv_nodata;
    private LinearLayout l_emputy;
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
        l_emputy=findViewById(R.id.l_emputy);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_jingxuan=findViewById(R.id.btn_jingxuan);
        findViewById(R.id.iv_clear_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TipDialog tipDialog = new TipDialog.Builder(ReadrecoderActivity.this)
                        .setContent("确定要清空阅读记录吗?")
                        .setCancel("取消")
                        .setEnsure("确定")
                        .setOnClickListener(new TipDialog.OnClickListener() {
                            @Override
                            public void clickEnsure() {
                                loading();
                                if(noval_readcoreds.size()==0){
                                    showShortToast("阅读记录已为空");
                                }else {
                                    mDbManager.deleteReadCoder();
                                    if (login_admin != null) {
                                        mPresenter.getDelectReadcoredData(login_admin.getToken(), 2);
                                    }else {
                                        requestPost();
                                    }
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
        tv_nodata = findViewById(R.id.tv_nodata);
        recyclerView = findViewById(R.id.recycle_readcord);
        mProgressBar = findViewById(R.id.pb_male);
        mRefreshSrv = findViewById(R.id.srv_male_refresh);

        mRefreshSrv.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                z = 1;
                isRefresh = true;
                requestPost();
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        mRefreshSrv.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                z++;
                requestPost();
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
        ClassicsHeader classicsHeader = new ClassicsHeader(this);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(new Date());
        classicsHeader.setLastUpdateText("最后更新:" + dateString);
        mRefreshSrv.setRefreshHeader(classicsHeader);
        mRefreshSrv.setRefreshFooter(new ClassicsFooter(this));
        btn_jingxuan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Intent intent_recever = new Intent("com.tiaozhuan.android");
                intent_recever.putExtra("type",3);
                sendBroadcast(intent_recever);
                finish();
            }
        });
    }
    TextView btn_jingxuan;
    int z = 1;
    boolean isRefresh;

    public String getIds() {
        StringBuilder sb = new StringBuilder();
        for (int z = 0; z < noval_readcoreds.size(); z++) {
            if (z == noval_readcoreds.size() - 1) {
                sb.append(noval_readcoreds.get(z).getNovel_id());
            } else {
                sb.append(noval_readcoreds.get(z).getNovel_id() + ",");
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

    public void requestPost() {
        if (login_admin != null) {
            mPresenter.getReadcoredData(login_admin.getToken(), z + "");
        } else {
            mPresenter.getReadcoredData("", z + "");
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
        noval_readcoreds.clear();
        setRecyclerViewData(novelNameList);
    }

    public void queryAllBookSuccess2(List<Noval_Readcored> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            if (!mDbManager.isExistInReadCoderNovel(dataList.get(i).getNovel_id())) {
                mDbManager.insertReadCordeNovel(dataList.get(i),0+"");
            }
        }
        mPresenter.queryAllBook();
    }
    public void loading(){
        mProgressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    public void setReadRecord(String token, String novel_id, String chapter_id) {
        String url = UrlObtainer.GetUrl() + "/" + "/api/lookbook/add";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("novel_id", novel_id)
                .add("chapter_id", chapter_id)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        String message = jsonObject.getString("msg");
                        //getReadRecordSuccess(message);
                    } else {
                        showShortToast("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                showShortToast("请求错误");
            }
        });
    }

    public void setRecyclerViewData(List<Noval_Readcored> novelNameList) {
        if (isRefresh == true) {
            noval_readcoreds.clear();
            isRefresh = false;
            novelSourceAdapter = null;
        }
        noval_readcoreds.addAll(novelNameList);//new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        int last = linearLayoutManager.findLastVisibleItemPosition();
        if (novelSourceAdapter == null) {
            noval_readcoreds = novelNameList;
            recyclerView.setLayoutManager(linearLayoutManager);
            //recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
            initAdapter();
            recyclerView.setFocusableInTouchMode(false);
            recyclerView.setScrollingTouchSlop(last);
        } else {
            if(noval_readcoreds.size()==0){
                l_emputy.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else {
                l_emputy.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            novelSourceAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void queryAllBookError(String errorMsg) {
        showShortToast(errorMsg);
    }

    @Override
    public void getReadcoredDataSuccess(List<Noval_Readcored> novelNameList) {
//        if (novelNameList.size() == 0 && z == 1) {
//            mPresenter.queryAllBook();
//        } else {
//            //setRecyclerViewData(novelNameList);
            queryAllBookSuccess2(novelNameList);
//        }
    }

    private void initAdapter() {
        novelSourceAdapter = new NovelSourceAdapter(this, noval_readcoreds);
        novelSourceAdapter.setOnNovelSourceListener(new NovelSourceAdapter.NovelSourceListener() {
            @Override
            public void clickItem(int position) {

                    Intent intent = new Intent(ReadrecoderActivity.this, NovelIntroActivity.class);
                    // 传递小说名，进入搜查页后直接显示该小说的搜查结果
                    intent.putExtra("pid", noval_readcoreds.get(position).getNovel_id());
                    startActivity(intent);
            }

            @Override
            public void longclickItem(int position) {
            }
        });
        recyclerView.setAdapter(novelSourceAdapter);
        if(noval_readcoreds.size()==0){
            l_emputy.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            l_emputy.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getReadcoredDataError(String errorMsg) {
        showShortToast(errorMsg);
    }

    @Override
    public void getDelectReadcoredDataSuccess(String msg) {
        isRefresh = true;
        requestPost();
    }

    @Override
    public void getDelectReadcoredDataError(String errorMsg) {
        showShortToast(errorMsg);
        isRefresh = true;
    }

}
