package com.example.administrator.xiaoshuoyuedushenqi.view.fragment.bookstore;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.BookstoreCategoryAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.CategoryAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseFragment;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IExclusiveContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.model.ExclusiveModel;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.ExclusivePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.NetUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.AllNovelActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.FenleiNovelActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.SearchActivity;

import java.util.ArrayList;
import java.util.List;

public class ExclusivelistGFragment extends BaseFragment<ExclusivePresenter> implements IExclusiveContract.View {
    RecyclerView recyclerView;
    private BookstoreCategoryAdapter mCategoryAdapter;
    private List<String> mCategoryNameList = new ArrayList<>();
    private List<String> mMoreList = new ArrayList<>();
    private List<DiscoveryNovelData> mNovelDataList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.exclusivelist;
    }

    @Override
    protected void initData() {
        mCategoryNameList.add("人气排行榜");
        mCategoryNameList.add("推荐排行榜");
        mCategoryNameList.add("新书人气榜");
        mMoreList.add("更多");
        mMoreList.add("更多");
        mMoreList.add("更多");
    }

    @Override
    protected void initView() {
        recyclerView = getActivity().findViewById(R.id.rv_male_category_novel_list1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected void doInOnCreate() {
        mPresenter.getCategoryNovels();
    }

    @Override
    protected ExclusivePresenter getPresenter() {
        return new ExclusivePresenter();
    }


    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Override
    public void getHotRankDataSuccess(List<List<String>> novelNameList) {

    }

    @Override
    public void getHotRankDataError(String errorMsg) {

    }

    @Override
    public void getCategoryNovelsSuccess(List<DiscoveryNovelData> dataList) {
        if (dataList.isEmpty()) {
            return;
        }
        if (mCategoryAdapter == null) {
            mNovelDataList = dataList;
            initCategoryAdapter();
        } else {
            mNovelDataList.clear();
            mNovelDataList.addAll(dataList);
            mCategoryAdapter.notifyDataSetChanged();
        }
    }

    private void initCategoryAdapter() {
        mCategoryAdapter = new BookstoreCategoryAdapter(getActivity(),
                mCategoryNameList, mMoreList, mNovelDataList,
                new BookstoreCategoryAdapter.CategoryListener() {
                    @Override
                    public void clickNovel(String novelName) {
                        if (!NetUtil.hasInternet(getActivity())) {
                            showShortToast("当前无网络，请检查网络后重试");
                            return;
                        }
                        // 跳转到该小说的搜索结果页
                        Intent intent = new Intent(getActivity(), SearchActivity.class);
                        intent.putExtra(SearchActivity.KEY_NOVEL_NAME, novelName);
                        startActivity(intent);
                    }

                    @Override
                    public void clickMore(int position) {
                        if (!NetUtil.hasInternet(getActivity())) {
                            showShortToast("当前无网络，请检查网络后重试");
                            return;
                        }
                        int gender = 0;
                        String major;
                        switch (position) {
                            case 0:
                                major = Constant.CATEGORY_MAJOR_XH;
                                break;
                            case 1:
                                major = Constant.CATEGORY_MAJOR_DS;
                                break;
                            case 2:
                                major = Constant.CATEGORY_MAJOR_WX;
                                break;
                            default:
                                major = Constant.CATEGORY_MAJOR_XH;
                                break;
                        }
                        // 跳转到全部小说页面
                        Intent intent = new Intent(getActivity(), FenleiNovelActivity.class);
                        intent.putExtra(AllNovelActivity.KEY_GENDER, gender);   // 性别
                        intent.putExtra(AllNovelActivity.KEY_MAJOR, major);     // 一级分类
                        startActivity(intent);
                    }
                });
        recyclerView.setAdapter(mCategoryAdapter);
    }

    @Override
    public void getCategoryNovelsError(String errorMsg) {
        showShortToast("" + errorMsg);
    }
}
