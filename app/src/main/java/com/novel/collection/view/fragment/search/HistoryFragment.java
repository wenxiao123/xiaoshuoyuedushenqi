package com.novel.collection.view.fragment.search;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.novel.collection.R;
import com.novel.collection.adapter.BookhotAdapter;
import com.novel.collection.adapter.HistoryAdapter;
import com.novel.collection.base.BaseFragment;
import com.novel.collection.base.BasePresenter;
import com.novel.collection.constant.EventBusCode;
import com.novel.collection.db.DatabaseManager;
import com.novel.collection.entity.bean.Login_admin;
import com.novel.collection.entity.bean.Wheel;
import com.novel.collection.entity.eventbus.Event;
import com.novel.collection.entity.eventbus.SearchUpdateInputEvent;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.util.EventBusUtil;
import com.novel.collection.view.activity.SearchResultActivity;
import com.novel.collection.widget.LineBreakLayout;
import com.novel.collection.widget.TipDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
 * Created on 2019/11/9
 */
public class HistoryFragment extends BaseFragment implements View.OnClickListener{
    private static final String TAG = "HistoryFragment";
    private static final int MAX_LINES = 3;     // 最多显示的行数

    private RecyclerView mHistoryListFv;
    private TextView mClearAllIv;
    private ImageView delect_all;
    private DatabaseManager mManager;
    private HistoryAdapter mHistoryAdapter;
    private List<String> mContentList = new ArrayList<>();  // 搜索内容集合
    private RecyclerView recyclerView_hot_book;
    private LineBreakLayout lineBreakLayout;
    private BookhotAdapter bookhotAdapter;
    private TextView txt_title;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_history;
    }

    @Override
    protected void initData() {
        mManager = DatabaseManager.getInstance();
        mContentList = mManager.queryAllHistory();
    }
    List<Wheel> wheelList;
    void postHotbook(){
        Gson mGson=new Gson();
        String url = UrlObtainer.GetUrl() + "/api/index/book_name";
        RequestBody requestBody = new FormBody.Builder()
                .add("limit", 7+"")
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
                         wheelList=new ArrayList<>();
                        for(int i=0;i<jsonArray.length();i++){
                            wheelList.add(mGson.fromJson(jsonArray.getJSONObject(i).toString(),Wheel.class));
                        }
                        initAdapter(wheelList);
                    }else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                showShortToast(errorMsg);
            }
        });
    }
   void initAdapter(List<Wheel> wheelList){
        if(wheelList.size()==0){
            txt_title.setVisibility(View.GONE);
        }else {
            txt_title.setVisibility(View.VISIBLE);
        }
       lineBreakLayout.setLables(wheelList, true);
   }
    @Override
    protected void initView() {
        txt_title=getActivity().findViewById(R.id.txt_title);
        mHistoryListFv = getActivity().findViewById(R.id.fv_history_history_list);
        // 设置 Adapter
        mHistoryAdapter = new HistoryAdapter(getActivity(), mContentList);
        mHistoryAdapter.setOnHistoryAdapterListener(new HistoryAdapter.HistoryAdapterListener() {
            @Override
            public void clickWord(int word) {
                // 通知 SearchActivity 进行搜索
//                Event<SearchUpdateInputEvent> event = new Event<>(EventBusCode.SEARCH_UPDATE_INPUT,
//                        new SearchUpdateInputEvent(mContentList.get(word)));
//                EventBusUtil.sendEvent(event);
                Intent intent=new Intent(getContext(),SearchResultActivity.class);
                intent.putExtra("searchContent",mContentList.get(word));
                startActivity(intent);
            }

            @Override
            public void longclick(int word) {
                if(!getActivity().isDestroyed()) {
                    final TipDialog tipDialog = new TipDialog.Builder(getActivity())
                            .setContent("是否清除搜索历史？")
                            .setCancel("否")
                            .setEnsure("是")
                            .setOnClickListener(new TipDialog.OnClickListener() {
                                @Override
                                public void clickEnsure() {
                                    mManager.deleteHistory(mContentList.get(word));
                                    updateHistory();
                                }

                                @Override
                                public void clickCancel() {
                                    //tipDialog.dismiss();
                                }
                            })
                            .build();
                    tipDialog.show();
                }
            }
        });
        delect_all=getActivity().findViewById(R.id.iv_delect_all);
        recyclerView_hot_book=getActivity().findViewById(R.id.recycle_hot_book);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView_hot_book.setLayoutManager(gridLayoutManager);

        @SuppressLint("WrongConstant") LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        mHistoryListFv.setLayoutManager(layoutManager);
        mHistoryListFv.setAdapter(mHistoryAdapter);
        //mHistoryListFv.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

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
            delect_all.setVisibility(View.VISIBLE);
        }
        lineBreakLayout=getActivity().findViewById(R.id.lineBreak);
    }

    @Override
    protected void doInOnCreate() {
        postHotbook();
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
            delect_all.setVisibility(View.VISIBLE);
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
                if(!getActivity().isDestroyed()) {
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
                                    delect_all.setVisibility(View.GONE);
                                }

                                @Override
                                public void clickCancel() {

                                }
                            })
                            .build();
                    dialog.show();
                }
                break;
            default:
                break;
        }
    }
}
