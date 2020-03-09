package com.example.administrator.xiaoshuoyuedushenqi.view.fragment.search;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.BookhotAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.HistoryAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseFragment;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.SearchUpdateInputEvent;
import com.example.administrator.xiaoshuoyuedushenqi.util.EventBusUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.ToastUtil;
import com.example.administrator.xiaoshuoyuedushenqi.widget.FlowLayout;
import com.example.administrator.xiaoshuoyuedushenqi.widget.TipDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WX
 * Created on 2019/11/9
 */
public class HistoryFragment extends BaseFragment implements View.OnClickListener{
    private static final String TAG = "HistoryFragment";
    private static final int MAX_LINES = 3;     // 最多显示的行数

    private RecyclerView mHistoryListFv;
    private TextView mClearAllIv;

    private DatabaseManager mManager;
    private HistoryAdapter mHistoryAdapter;
    private List<String> mContentList = new ArrayList<>();  // 搜索内容集合
    private RecyclerView recyclerView_hot_book;
    private BookhotAdapter bookhotAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_history;
    }

    @Override
    protected void initData() {
        mManager = DatabaseManager.getInstance();
        mContentList = mManager.queryAllHistory();
    }

    @Override
    protected void initView() {
        mHistoryListFv = getActivity().findViewById(R.id.fv_history_history_list);
        // 设置 Adapter
        mHistoryAdapter = new HistoryAdapter(getActivity(), mContentList);
        mHistoryAdapter.setOnHistoryAdapterListener(new HistoryAdapter.HistoryAdapterListener() {
            @Override
            public void clickWord(String word) {
                // 通知 SearchActivity 进行搜索
                Event<SearchUpdateInputEvent> event = new Event<>(EventBusCode.SEARCH_UPDATE_INPUT,
                        new SearchUpdateInputEvent(word));
                EventBusUtil.sendEvent(event);
            }
        });

        recyclerView_hot_book=getActivity().findViewById(R.id.recycle_hot_book);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView_hot_book.setLayoutManager(gridLayoutManager);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        mHistoryListFv.setLayoutManager(layoutManager);
        mHistoryListFv.setAdapter(mHistoryAdapter);
        bookhotAdapter=new BookhotAdapter(getContext(),mContentList);
        recyclerView_hot_book.setAdapter(bookhotAdapter);

        // 设置最多显示的行数
        //mHistoryListFv.setMaxLines(MAX_LINES);
        // 获取显示的 item 数
        mHistoryListFv.post(new Runnable() {
            @Override
            public void run() {
                int count = 5;
                if (count < mContentList.size() / 2) {
                    // 从数据库删除多余的历史记录
                    mManager.deleteHistories(mContentList.size() - count);
                }
            }
        });

        mClearAllIv = getActivity().findViewById(R.id.iv_history_clear_all);
        mClearAllIv.setOnClickListener(this);
        if (mContentList.size() != 0) {
            mClearAllIv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void doInOnCreate() {

    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    /**
     * 更新搜索历史
     */
    public void updateHistory() {
        if (mHistoryAdapter == null) {
            return;
        }
        // 更新历史搜索视图
        mContentList = mManager.queryAllHistory();
        if (mContentList.size() != 0) {
            mClearAllIv.setVisibility(View.VISIBLE);
        }
        mHistoryAdapter.updateList(mContentList);   // 记得要先更新 Adapter 的数据
        //mHistoryListFv.updateView();
        mHistoryAdapter.notifyDataSetChanged();// 重新根据 Adapter 的 数据来设置视图

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_history_clear_all:
                // 弹出 Dialog 再次确认是否清空
                TipDialog dialog = new TipDialog.Builder(getActivity())
                        .setContent("是否清空历史搜索")
                        .setEnsure("是")
                        .setCancel("不了")
                        .setOnClickListener(new TipDialog.OnClickListener() {
                            @Override
                            public void clickEnsure() {
                                // 清空数据库，并更新页面
                                mManager.deleteAllHistories();
                                mContentList.clear();
                                //mHistoryListFv.updateView();
                                mHistoryAdapter.notifyDataSetChanged();
                                // 图标隐藏
                                mClearAllIv.setVisibility(View.GONE);
                            }

                            @Override
                            public void clickCancel() {

                            }
                        })
                        .build();
                dialog.show();
                break;
            default:
                break;
        }
    }
}
