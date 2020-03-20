package com.example.administrator.xiaoshuoyuedushenqi.view.fragment.search;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.NovelResultAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.NovelSourceAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseFragment;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePagingLoadAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.constract.ISearchResultContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.NovalInfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.NovelSourceData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.NovelIntroInitEvent;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.SearchResultPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.EventBusUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.NovelIntroActivity;

import java.util.List;

/**
 * @author
 * Created on 2019/11/9
 */
public class SearchResultFragment extends BaseFragment<SearchResultPresenter>
        implements ISearchResultContract.View {
    private static final String TAG = "SearchResultFragment";
    private static final String KEY_SEARCH_CONTENT = "tag_search_content";

    private ProgressBar mProgressBar;
    private RecyclerView mNovelSourceRv;
    private TextView mNoneTv;       // 没有找到相关小说时显示

    private NovelResultAdapter mNovelSourceAdapter;

    private String mSearchContent;  // 搜索内容
    private List<NovalInfo> mNovelSourceDataList; // 小说源列表

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_result;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mNoneTv = getActivity().findViewById(R.id.tv_search_result_none);
        mProgressBar = getActivity().findViewById(R.id.pb_search_result_progress_bar);

        mNovelSourceRv = getActivity().findViewById(R.id.rv_search_result_novel_source_list);
        mNovelSourceRv.setLayoutManager(new LinearLayoutManager(getActivity()));
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

    public static SearchResultFragment newInstance(String searchContent) {
        SearchResultFragment searchResultFragment = new SearchResultFragment();
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
    public void getNovelsSourceSuccess(List<NovalInfo> novelSourceDataList) {
        mNoneTv.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        // 列表显示小说源
        mNovelSourceDataList = novelSourceDataList;
        initAdapter();
    }

    @Override
    public void getNovelsSourceError(String errorMsg) {
        mProgressBar.setVisibility(View.GONE);
        if (errorMsg.equals(Constant.NOT_FOUND_NOVELS)) {
            // 没有找到相关小说，设置状态页
            mNoneTv.setVisibility(View.VISIBLE);
        } else {
            // 其他错误
            showShortToast(errorMsg);
        }
    }

    private void initAdapter() {
        if (mNovelSourceDataList == null || mNovelSourceDataList.isEmpty()) {
            return;
        }

        mNovelSourceAdapter = new NovelResultAdapter(getActivity(), mNovelSourceDataList, new BasePagingLoadAdapter.LoadMoreListener() {
            @Override
            public void loadMore() {

            }
        }, new NovelResultAdapter.NovelListener() {
            @Override
            public void clickItem(int novelName) {
                Intent intent=new Intent(getContext(),NovelIntroActivity.class);
                intent.putExtra("pid",novelName+"");
                getContext().startActivity(intent);
            }
        });
        mNovelSourceRv.setAdapter(mNovelSourceAdapter);
    }

    public void update(String novelName) {
        mPresenter.getNovelsSource(novelName);
        mProgressBar.setVisibility(View.VISIBLE);
    }
}
