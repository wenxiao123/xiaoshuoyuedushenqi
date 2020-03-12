package com.example.administrator.xiaoshuoyuedushenqi.view.fragment.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.NovelSearchAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.NovelSourceAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseFragment;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.constract.ISearchResultContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.NovelSourceData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.NovelIntroInitEvent;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.SearchResultPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.EventBusUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.NovelIntroActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WX
 * Created on 2019/11/9
 */
public class SearchFragment extends BaseFragment<SearchResultPresenter>
        implements ISearchResultContract.View {
    private static final String TAG = "SearchResultFragment";
    private static final String KEY_SEARCH_CONTENT = "tag_search_content";
    private RecyclerView mNovelSourceRv;

    private NovelSearchAdapter mNovelSourceAdapter;

    private String mSearchContent;  // 搜索内容
    private List<String> mNovelSourceDataList=new ArrayList<>(); // 小说源列表

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mNovelSourceRv = getActivity().findViewById(R.id.rv_search_result_novel_source_list);
        mNovelSourceRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNovelSourceRv.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void doInOnCreate() {
        mPresenter.getNovelsSource(mSearchContent);
    }

    @Override
    protected SearchResultPresenter getPresenter() {
        return new SearchResultPresenter();
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    public static SearchFragment newInstance(String searchContent) {
        SearchFragment searchResultFragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SEARCH_CONTENT, searchContent);
        searchResultFragment.setArguments(bundle);

        return searchResultFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSearchContent = getArguments().getString(KEY_SEARCH_CONTENT);
        }
    }

    @Override
    public void getNovelsSourceSuccess(List<NovelSourceData> novelSourceDataList) {
        // 列表显示小说源
        for(int i=0;i<novelSourceDataList.size();i++){
            if(novelSourceDataList.get(i).getName()!=null) {
                mNovelSourceDataList.add(novelSourceDataList.get(i).getName());
            }
        }
        initAdapter();
    }

    @Override
    public void getNovelsSourceError(String errorMsg) {

            // 其他错误
          //  showShortToast(errorMsg);

    }

    private void initAdapter() {
        if (mNovelSourceDataList == null || mNovelSourceDataList.isEmpty()) {
            return;
        }

        mNovelSourceAdapter = new NovelSearchAdapter(getActivity(), mNovelSourceDataList,mSearchContent);
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
            }
        });
        mNovelSourceRv.setAdapter(mNovelSourceAdapter);
    }

    public void update(String novelName) {
        mPresenter.getNovelsSource(novelName);
    }
}
