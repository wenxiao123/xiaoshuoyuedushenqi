package com.example.administrator.xiaoshuoyuedushenqi.view.fragment.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.NovelSearchAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseFragment;
import com.example.administrator.xiaoshuoyuedushenqi.constract.ISearchResultContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.NovalInfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Wheel;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.NovelSourceData;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.SearchResultPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.NovelIntroActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * Created on 2019/11/9
 */
public class SearchFragment extends BaseFragment<SearchResultPresenter>
        implements ISearchResultContract.View {
    private static final String TAG = "SearchResultFragment";
    private static final String KEY_SEARCH_CONTENT = "tag_search_content";
    private RecyclerView mNovelSourceRv;

    private NovelSearchAdapter mNovelSourceAdapter;

    private String mSearchContent;  // 搜索内容
    private List<Wheel> mNovelSourceDataList=new ArrayList<>(); // 小说源列表

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
    public void getNovelsSourceSuccess(List<NovalInfo> novelSourceDataList) {
        // 列表显示小说源
        for(int i=0;i<novelSourceDataList.size();i++){
            if(novelSourceDataList.get(i).getTitle()!=null) {
                mNovelSourceDataList.add(new Wheel(novelSourceDataList.get(i).getId(),novelSourceDataList.get(i).getPic(),novelSourceDataList.get(i).getTitle()));
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
                Intent intent = new Intent(getContext(), NovelIntroActivity.class);
                // 传递小说名，进入搜查页后直接显示该小说的搜查结果
                intent.putExtra("pid", mNovelSourceDataList.get(position).getId() + "");
                startActivity(intent);
            }
        });
        mNovelSourceRv.setAdapter(mNovelSourceAdapter);
    }

    public void update(String novelName) {
        mPresenter.getNovelsSource(novelName);
    }
}