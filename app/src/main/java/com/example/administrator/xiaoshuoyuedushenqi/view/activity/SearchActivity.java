package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.NovelSearchAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.NovalInfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Wheel;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.SearchUpdateInputEvent;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
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

import cn.bmob.v3.util.V;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
 * Created on 2019/11/8
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "SearchActivity";
    public static final String KEY_NOVEL_NAME = "key_novel_name";

    private ImageView mBackIv;
    private EditText mSearchBarEt;
    private TextView mSearchTv;
    private ImageView mDeleteSearchTextIv;
    private FrameLayout fv_search_container;
    private HistoryFragment mHistoryFragment;
    private SearchFragment mSearchFragment;
    private SearchResultFragment mSearchResultFragment;
    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private boolean mIsShowSearchResFg = false;     // 是否正在显示搜索结果 Fragment
    private String mLastSearch = "";        // 记录上一搜索词
    private DatabaseManager mManager;   // 数据库管理类
    private RecyclerView mNovelSourceRv;
    private TextView tv_search_result_none;
    private NovelSearchAdapter mNovelSourceAdapter;
    @Override
    protected void doBeforeSetContentView() {
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);   //隐藏标题栏
        StatusBarUtil.setLightColorStatusBar(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        mManager = DatabaseManager.getInstance();
    }

    @Override
    protected void initView() {
        tv_search_result_none=findViewById(R.id.tv_search_result_none);
        mNovelSourceRv =findViewById(R.id.rv_search_result_novel_source_list);
        mNovelSourceRv.setLayoutManager(new LinearLayoutManager(this));
        fv_search_container=findViewById(R.id.fv_search_container);
        mBackIv = findViewById(R.id.iv_search_back);
        mBackIv.setOnClickListener(this);
        mSearchBarEt = findViewById(R.id.et_search_search_bar);
        // 监听内容变化
        mSearchBarEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    // 隐藏删除 icon
                    mDeleteSearchTextIv.setVisibility(View.GONE);
                    fv_search_container.setVisibility(View.VISIBLE);
                    tv_search_result_none.setVisibility(View.GONE);
                    // 如果此时正在显示搜索结果 Fg，移除它
                    if (mIsShowSearchResFg) {
                        removeSearchResFg();
                    }
                    // 显示软键盘
                    EditTextUtil.focusAndShowSoftKeyboard(SearchActivity.this, mSearchBarEt);
                } else {
                    // 显示删除 icon
                    mDeleteSearchTextIv.setVisibility(View.VISIBLE);
                }
                mSearchContent=s.toString();
                //showSearchFg(s.toString());
                //if(issearch==true) {
                    getNovelsSource(mSearchContent);
                    issearch=false;
                //}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // 监听软键盘
        mSearchBarEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 点击“完成”或者“下一项”
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_NEXT) {
                    // 进行搜索操作
                    doSearch();
                }
                return false;
            }
        });

        mSearchTv = findViewById(R.id.tv_search_search_text);
        mSearchTv.setOnClickListener(this);

        mDeleteSearchTextIv = findViewById(R.id.iv_search_delete_search_text);
        mDeleteSearchTextIv.setOnClickListener(this);
    }
    private String mSearchContent;  // 搜索内容
    private List<Wheel> mNovelSourceDataList=new ArrayList<>(); // 小说源列表
    public void getNovelsSource(String novelName) {
        Gson mGson=new Gson();
        String url = UrlObtainer.GetUrl()+"/"+"/api/index/book_cke";
        RequestBody requestBody = new FormBody.Builder()
                .add("name", novelName)
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONObject object=jsonObject.getJSONObject("data");
                        JSONArray jsonArray=object.getJSONArray("data");
                        List<NovalInfo> novalDetailsList=new ArrayList<>();
                        for(int i=0;i<jsonArray.length();i++){
                            novalDetailsList.add(mGson.fromJson(jsonArray.getJSONObject(i).toString(),NovalInfo.class));
                        }
                        getNovelsSourceSuccess(novalDetailsList);
                    }else {
                       getNovelsSourceError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                getNovelsSourceError(errorMsg);
            }
        });
    }


    public void getNovelsSourceSuccess(List<NovalInfo> novelSourceDataList) {
        mNovelSourceDataList.clear();
        if(novelSourceDataList.size()==0){
//            tv_search_result_none.setVisibility(View.GONE);
//            mNovelSourceRv.setVisibility(View.VISIBLE);
            mNovelSourceRv.setVisibility(View.GONE);
            tv_search_result_none.setVisibility(View.VISIBLE);
            //mNovelSourceRv.setVisibility(View.GONE);
        }else {
           // mNovelSourceRv.setVisibility(View.VISIBLE);
            mNovelSourceRv.setVisibility(View.VISIBLE);
            fv_search_container.setVisibility(View.GONE);
            tv_search_result_none.setVisibility(View.GONE);
        }
        // 列表显示小说源
        for(int i=0;i<novelSourceDataList.size();i++){
            if(novelSourceDataList.get(i).getTitle()!=null) {
                mNovelSourceDataList.add(new Wheel(novelSourceDataList.get(i).getId(),novelSourceDataList.get(i).getPic(),novelSourceDataList.get(i).getTitle()));
            }
        }
        initAdapter();
    }

    public void getNovelsSourceError(String errorMsg) {
        //tv_search_result_none.setVisibility(View.VISIBLE);
        mNovelSourceRv.setVisibility(View.GONE);
        fv_search_container.setVisibility(View.VISIBLE);
        // 其他错误
        //  showShortToast(errorMsg);

    }

    private void initAdapter() {
        if (mNovelSourceDataList == null || mNovelSourceDataList.isEmpty()) {
            return;
        }

        mNovelSourceAdapter = new NovelSearchAdapter(this, mNovelSourceDataList,mSearchContent);
        mNovelSourceAdapter.setOnNovelSourceListener(new NovelSearchAdapter.NovelSourceListener() {
            @Override
            public void clickItem(int position) {
                if (mNovelSourceDataList.isEmpty()) {
                    return;
                }
//                Event<NovelIntroInitEvent> event = new Event<>(EventBusCode.NOVEL_INTRO_INIT,
//                        new NovelIntroInitEvent(mNovelSourceDataList.get(position)));
//                EventBusUtil.sendStickyEvent(event);
//                jump2Activity(NovelIntroActivity.class);
                Intent intent = new Intent(SearchActivity.this, NovelIntroActivity.class);
                // 传递小说名，进入搜查页后直接显示该小说的搜查结果
                intent.putExtra("pid", mNovelSourceDataList.get(position).getId() + "");
                startActivity(intent);
            }
        });
        mNovelSourceRv.setAdapter(mNovelSourceAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void doAfterInit() {
        // 更改状态栏颜色
        StatusBarUtil.setLightColorStatusBar(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.search_bg));

        String novelName = getIntent().getStringExtra(KEY_NOVEL_NAME);
        if (novelName != null) {
            // 说明是通过点击列表小说跳转过来的，直接显示该小说的搜查结果
            mSearchBarEt.setText(novelName);
            doSearch();
        } else {
            // EditText 获得焦点并显示软键盘
            EditTextUtil.focusAndShowSoftKeyboard(this, mSearchBarEt);
            // 显示历史搜索页面
            showHistoryFg();
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }
    boolean issearch=false;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search_back:
                finish();
                break;
            case R.id.tv_search_search_text:
                //doSearch();
                updateHistoryDb(mSearchBarEt.getText().toString());
                Intent intent=new Intent(this,SearchResultActivity.class);
                intent.putExtra("searchContent",mSearchBarEt.getText().toString());
                startActivity(intent);
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
        // 点击搜索后隐藏软键盘
        SoftInputUtil.hideSoftInput(SearchActivity.this);
        if (!NetUtil.hasInternet(this)) {
            showShortToast("当前无网络，请检查网络后重试");
            return;
        }
        // 当前搜索词
        final String searchText = mSearchBarEt.getText().toString().trim();
        if (searchText.equals("")) {
            showShortToast("输入不能为空");
            return; // 不能为空
        }
        if (mIsShowSearchResFg) {
            // 如果此时已经是搜索结果 Fg，就直接更新它
            // 搜索同一个词时不用管
            if (!searchText.equals(mLastSearch)) {
                mSearchResultFragment.update(searchText);
            }

        } else {
            showSearchResFg();
        }
        mLastSearch = searchText;
        // 更新历史记录
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateHistoryDb(searchText);
            }
        }, 500);
    }

    /**
     * 第一次进入搜索页面时，显示历史搜索 Fragment
     */
    private void showHistoryFg() {
        if (mHistoryFragment == null) {
            mHistoryFragment = new HistoryFragment();
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.fv_search_container, mHistoryFragment);
        ft.show(mHistoryFragment);
        ft.commit();
    }

    /**
     * 第一次进入搜索页面时，显示历史搜索 Fragment
     */
    private void showSearchFg(String stringContent) {
//        if (mSearchFragment == null) {
            mSearchFragment = SearchFragment.newInstance(stringContent);
//        }else {
//            return;
//        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.fv_search_container, mSearchFragment);
        if (mHistoryFragment != null) {
            ft.hide(mHistoryFragment);
        }
        if (mSearchResultFragment != null) {
            ft.hide(mSearchResultFragment);
        }
        ft.show(mSearchFragment);
        ft.commit();
    }

    /**
     * 隐藏历史搜索 Fg，显示搜索结果 Fg
     */
    private void showSearchResFg() {
       // Log.e("QQQ", "showSearchResFg: ", );
        if (mSearchResultFragment != null) {
            return;
        }
        mSearchResultFragment = SearchResultFragment.newInstance(
                mSearchBarEt.getText().toString());
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.fv_search_container, mSearchResultFragment,"tag");
        if (mHistoryFragment != null) {
            ft.hide(mHistoryFragment);
        }
        ft.show(mSearchResultFragment);
        ft.commit();
        mIsShowSearchResFg = true;
    }

    /**
     * 从容器中移除搜索结果 Fg，显示历史搜索 Fg
     */
    private void removeSearchResFg() {
        if (mSearchResultFragment == null) {
            return;
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.remove(mSearchResultFragment);
        mSearchResultFragment = null;
        if (mHistoryFragment == null) {
            mHistoryFragment = new HistoryFragment();
            ft.add(R.id.fv_search_container, mHistoryFragment);
        }
        ft.show(mHistoryFragment);
        ft.commit();
        mIsShowSearchResFg = false;
    }

    /**
     * 更新历史记录数据库
     *
     * @param word 新输入的词语
     */
    private void updateHistoryDb(String word) {
        if(word.trim().equals("")){
            return;
        }
        mManager.deleteHistory(word);
        mManager.insertHistory(word);
        // 通知历史页面更新历史记录
        if (mHistoryFragment != null) {
            Log.d(TAG, "updateHistoryDb: run");
            mHistoryFragment.updateHistory();
        }
    }
}
