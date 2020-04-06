package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.NovelResultAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.NovelSearchAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePagingLoadAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.constract.ISearchResultContract;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.NovalInfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Wheel;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.SearchUpdateInputEvent;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.ReadPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.SearchResultPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.EditTextUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.NetUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.SoftInputUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.fragment.search.HistoryFragment;
import com.example.administrator.xiaoshuoyuedushenqi.view.fragment.search.SearchFragment;
import com.example.administrator.xiaoshuoyuedushenqi.view.fragment.search.SearchResultFragment;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
 * Created on 2019/11/8
 */
public class SearchResultActivity extends BaseActivity<SearchResultPresenter> implements ISearchResultContract.View, View.OnClickListener{

    private static final String TAG = "SearchActivity";
    public static final String KEY_NOVEL_NAME = "key_novel_name";
    private SearchResultPresenter searchResultPresenter;
    private ImageView mBackIv;
    private EditText mSearchBarEt;
    private TextView mSearchTv;
    private ImageView mDeleteSearchTextIv;
    private FrameLayout fv_search_container;
    private boolean mIsShowSearchResFg = false;     // 是否正在显示搜索结果 Fragment
    private DatabaseManager mManager;   // 数据库管理类
    private ProgressBar mProgressBar;
    private RecyclerView mNovelSourceRv;
    private TextView mNoneTv;       // 没有找到相关小说时显示
    private TextView tv_search_result_none;
    private String mStringContent;
    private NovelResultAdapter mNovelSourceAdapter;
    @Override
    protected void doBeforeSetContentView() {
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);   //隐藏标题栏
        StatusBarUtil.setTranslucentStatus(this);
        searchResultPresenter=new SearchResultPresenter();
    }
/*
/
 */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_result;
    }

    @Override
    protected SearchResultPresenter getPresenter() {
        return new SearchResultPresenter();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventCome(Event event) {
        switch (event.getCode()) {
            case EventBusCode.SEARCH_UPDATE_INPUT:
                if (event.getData() instanceof SearchUpdateInputEvent) {
                    SearchUpdateInputEvent sEvent = (SearchUpdateInputEvent) event.getData();
                    mSearchBarEt.setText(sEvent.getInput());
                    //issearch=true;
                    // 进行搜索
                    doSearch();
                }
                break;
            default:
                break;
        }
    }
    @Override
    protected void initData() {
        mManager = DatabaseManager.getInstance();
        mStringContent=getIntent().getStringExtra("searchContent");
    }

    @Override
    protected void initView() {
        mNoneTv = findViewById(R.id.tv_search_result_none);
        mProgressBar = findViewById(R.id.pb_search_result_progress_bar);
        tv_search_result_none=findViewById(R.id.tv_search_result_none);
        mNovelSourceRv =findViewById(R.id.rv_search_result_novel_source_list);
        mNovelSourceRv.setLayoutManager(new LinearLayoutManager(this));
        fv_search_container=findViewById(R.id.fv_search_container);
        mBackIv = findViewById(R.id.iv_search_back);
        mBackIv.setOnClickListener(this);
        mSearchBarEt = findViewById(R.id.et_search_search_bar);
        // 监听内容变化
//        mSearchBarEt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() == 0) {
//                    // 隐藏删除 icon
//                    mDeleteSearchTextIv.setVisibility(View.GONE);
//                    fv_search_container.setVisibility(View.VISIBLE);
//                    tv_search_result_none.setVisibility(View.GONE);
//                    // 如果此时正在显示搜索结果 Fg，移除它
//                    if (mIsShowSearchResFg) {
//
//                    }
//                    // 显示软键盘
//                    EditTextUtil.focusAndShowSoftKeyboard(SearchResultActivity.this, mSearchBarEt);
//                } else {
//                    // 显示删除 icon
//                    mDeleteSearchTextIv.setVisibility(View.VISIBLE);
//                }
//                mSearchContent=s.toString();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        // 监听软键盘
//        mSearchBarEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                // 点击“完成”或者“下一项”
//                if (actionId == EditorInfo.IME_ACTION_DONE ||
//                        actionId == EditorInfo.IME_ACTION_NEXT) {
//                    // 进行搜索操作
//                    doSearch();
//                }
//                return false;
//            }
//        });
        mSearchBarEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchResultActivity.this,SearchActivity.class));
            }
        });
        mSearchTv = findViewById(R.id.tv_search_search_text);
        mSearchTv.setOnClickListener(this);

        mDeleteSearchTextIv = findViewById(R.id.iv_search_delete_search_text);
        mDeleteSearchTextIv.setOnClickListener(this);
    }
    private String mSearchContent;  // 搜索内容
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void doAfterInit() {
        // 更改状态栏颜色
       // StatusBarUtil.setLightColorStatusBar(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.search_bg));
        //String novelName = getIntent().getStringExtra(KEY_NOVEL_NAME);
       // searchResultPresenter.getNovelsSource(mStringContent);
        mPresenter.getNovelsSource(mStringContent);
        mSearchBarEt.setText(mStringContent);
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }
    boolean issearch=false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search_back:
                finish();
                break;
            case R.id.tv_search_search_text:
                doSearch();
                break;
            case R.id.iv_search_delete_search_text:
                // 删除 EditText 内容
                mSearchBarEt.setText("");
            default:
                break;
        }
    }

    /**
     * 进行搜索
     */
    private void doSearch() {
        //mPresenter.getNovelsSource(mStringContent);
        showShortToast("已搜索当前小说");
    }
    private List<NovalInfo> mNovelSourceDataList=new ArrayList<>(); // 小说源列表
    @Override
    public void getNovelsSourceSuccess(List<NovalInfo> novelSourceDataList) {
       // Log.e("QQQ", "getNovelsSourceSuccess: "+novelSourceDataList.size());
        mProgressBar.setVisibility(View.GONE);
        mNovelSourceDataList.clear();
        if(novelSourceDataList.size()==0){
            showShortToast("暂无搜索小说");
            mNoneTv.setVisibility(View.VISIBLE);
        }else {
            mNoneTv.setVisibility(View.GONE);
        }
        // 列表显示小说源
        mNovelSourceDataList = novelSourceDataList;
        initAdapter();
    }
    private void initAdapter() {
        if (mNovelSourceDataList == null || mNovelSourceDataList.isEmpty()) {
            return;
        }

        mNovelSourceAdapter = new NovelResultAdapter(this, mNovelSourceDataList, new BasePagingLoadAdapter.LoadMoreListener() {
            @Override
            public void loadMore() {

            }
        }, new NovelResultAdapter.NovelListener() {
            @Override
            public void clickItem(int novelName) {
                Intent intent=new Intent(SearchResultActivity.this,NovelIntroActivity.class);
                intent.putExtra("pid",novelName+"");
                startActivity(intent);
            }
        });
        mNovelSourceRv.setAdapter(mNovelSourceAdapter);
    }
    @Override
    public void getNovelsSourceError(String errorMsg) {
        mProgressBar.setVisibility(View.GONE);
        mNovelSourceDataList.clear();
        showShortToast("暂无搜索小说");
        mNoneTv.setVisibility(View.VISIBLE);

    }
}
