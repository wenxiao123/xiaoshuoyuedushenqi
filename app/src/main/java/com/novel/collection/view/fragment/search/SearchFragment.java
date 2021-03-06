package com.novel.collection.view.fragment.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novel.collection.R;
import com.novel.collection.adapter.NovelSearchAdapter;
import com.novel.collection.base.BaseFragment;
import com.novel.collection.constract.ISearchResultContract;
import com.novel.collection.entity.bean.NovalInfo;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.bean.Wheel;
import com.novel.collection.entity.data.NovelSourceData;
import com.novel.collection.presenter.SearchResultPresenter;
import com.novel.collection.view.activity.NovelIntroActivity;

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
   // private TextView tv_search_result_none;
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
        //mNovelSourceRv.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void doInOnCreate() {
        mPresenter.getNovelsSource(mSearchContent,1);
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
        //tv_search_result_none=getActivity().findViewById(R.id.tv_search_result_none);
    }

    @Override
    public void getNovelsSourceSuccess(List<NovalInfo> novelSourceDataList) {
        if(novelSourceDataList.size()==0){
//            tv_search_result_none.setVisibility(View.GONE);
//            mNovelSourceRv.setVisibility(View.VISIBLE);
            mNovelSourceRv.setVisibility(View.GONE);
        }
        // 列表显示小说源
        for(int i=0;i<novelSourceDataList.size();i++){
            if(novelSourceDataList.get(i).getTitle()!=null) {
                mNovelSourceDataList.add(new Wheel(novelSourceDataList.get(i).getId(),novelSourceDataList.get(i).getPic(),novelSourceDataList.get(i).getTitle(),novelSourceDataList.get(i).getAuthor()));
            }
        }
        initAdapter();
    }

    @Override
    public void getNovelsSourceError(String errorMsg) {
        //tv_search_result_none.setVisibility(View.VISIBLE);
        mNovelSourceRv.setVisibility(View.GONE);
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
        mPresenter.getNovelsSource(novelName,1);
    }
}
