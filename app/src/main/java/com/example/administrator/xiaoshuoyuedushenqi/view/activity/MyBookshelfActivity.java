package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.Intent;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.BookshelfNovelsAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_Readcored;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.interfaces.Delet_book_show;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.BookshelfPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.FpShadowLayout;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.widget.TipDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MyBookshelfActivity extends BaseActivity implements Delet_book_show, View.OnClickListener {
    private RecyclerView mBookshelfNovelsRv;
    private FpShadowLayout fpShadowLayout;
    private TextView mSelectAllTv, mDeleteTv, iv_bookshelf_more;
    BookshelfPresenter bookshelfPresenter = new BookshelfPresenter() {
        @Override
        public void queryAllBookSuccess(List<BookshelfNovelDbData> dataList) {
            if (mBookshelfNovelsAdapter == null) {
                mDataList = dataList;
                mCheckedList.clear();
                for (int i = 0; i < mDataList.size(); i++) {
                    mCheckedList.add(false);
                }
                initAdapter();
                if (mIsDeleting) {
                    return;
                }
                //mBookshelfNovelsAdapter.setIsMultiDelete(true);
                mBookshelfNovelsRv.setAdapter(mBookshelfNovelsAdapter);
            } else {
                mDataList.clear();
                mDataList.addAll(dataList);
                mCheckedList.clear();
                for (int i = 0; i < mDataList.size(); i++) {
                    mCheckedList.add(false);
                }
                mBookshelfNovelsAdapter.notifyDataSetChanged();
            }
            if(dataList.size()==0&&login_admin!=null){
                queryallBook(login_admin.getToken());
            }
        }
    };
    Login_admin login_admin;
    List<Noval_Readcored> noval_readcoreds=new ArrayList<>();
    private void queryallBook(String token){
        Gson mGson=new Gson();
        String url = UrlObtainer.GetUrl() + "/api/Userbook/index";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("page", 1+"")
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        if(jsonObject.isNull("data")){
                            showShortToast("请求数据失败");
                        }else {
                            JSONObject object = jsonObject.getJSONObject("data");
                            JSONArray jsonArray=object.getJSONArray("data");
                            if(jsonArray.length()==0){
                                return;
                            }
                            mDataList1.clear();
                            for(int i=0;i<jsonArray.length();i++){
                                //String novelUrl, String name, String cover,  int position, int type, int secondPosition, String chapterid, int weight
                                noval_readcoreds.add(mGson.fromJson(jsonArray.getJSONObject(i).toString(), Noval_Readcored.class));
                                mDataList1.add(new BookshelfNovelDbData(noval_readcoreds.get(i).getNovel_id(),noval_readcoreds.get(i).getTitle(),noval_readcoreds.get(i).getPic()
                                        ,0,0,0,noval_readcoreds.get(i).getChapter_id(),noval_readcoreds.get(i).getSerialize()));
                            }
                            queryAllBookSuccess2(mDataList1);
                        }
                    } else {
                        showShortToast("请求数据失败");
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
    public void queryAllBookSuccess2(List<BookshelfNovelDbData> dataList) {
        if (mBookshelfNovelsAdapter == null) {
            mDataList = dataList;
            mCheckedList.clear();
            for (int i = 0; i < mDataList.size(); i++) {
                mCheckedList.add(false);
            }
            initAdapter();
            if (mIsDeleting) {
                return;
            }
            //mBookshelfNovelsAdapter.setIsMultiDelete(true);
            mBookshelfNovelsRv.setAdapter(mBookshelfNovelsAdapter);
        } else {
            mDataList.clear();
            mDataList.addAll(dataList);
            mCheckedList.clear();
            for (int i = 0; i < mDataList.size(); i++) {
                mCheckedList.add(false);
            }
            mBookshelfNovelsAdapter.notifyDataSetChanged();
        }
        if(dataList.size()==0){

        }
    }
    private void initAdapter() {
        mBookshelfNovelsAdapter = new BookshelfNovelsAdapter(this, mDataList, mCheckedList,
                new BookshelfNovelsAdapter.BookshelfNovelListener() {
                    @Override
                    public void clickItem(int position) {

                    }

                    @Override
                    public void longClick(int position) {
                    }
                },this);
        mBookshelfNovelsAdapter.setIsMultiDelete(false);
        mBookshelfNovelsAdapter.notifyDataSetChanged();
        iv_bookshelf_more.setText("编辑");
       // rv_bookshelf_multi_delete_bar.setVisibility(View.GONE);
    }

    private List<BookshelfNovelDbData> mDataList = new ArrayList<>();
    private List<BookshelfNovelDbData> mDataList1 = new ArrayList<>();
    private List<Boolean> mCheckedList = new ArrayList<>();
    private BookshelfNovelsAdapter mBookshelfNovelsAdapter;
    private boolean mIsDeleting = false;
   // private LinearLayout rv_bookshelf_multi_delete_bar;
    @Override
    protected void doBeforeSetContentView() {
        bookshelfPresenter.queryAllBook();
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mybookshelf;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    private DatabaseManager mDbManager;

    @Override
    protected void initData() {
        mDbManager = DatabaseManager.getInstance();
        login_admin= (Login_admin) SpUtil.readObject(this);
    }

    public void delectBookshelfadd(String token, String novel_id) {
        if(novel_id==null){
            return;
        }
        String url = UrlObtainer.GetUrl()+"/"+"api/Userbook/del";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("novel_id", novel_id)
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        String message=jsonObject.getString("msg");
                        //mPresenter.(message);
                        //showShortToast("删除成功");
                    }else {
                        //mPresenter.getReadRecordError("请求错误");
                        showShortToast("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                showShortToast("请求错误");
                //mPresenter.getReadRecordError(errorMsg);
            }
        });
    }
    /**
     * 多选删除
     */
    private void multiDelete() {
        mIsDeleting = true;
        for (int i = mDataList.size() - 1; i >= 0; i--) {
            if (mCheckedList.get(i)) {
                // 从数据库中删除该小说
                if(mDataList.get(i).getType()==1){
                File file=new File((mDataList.get(i).getFuben_id()));
                if(file.exists()){
                    file.delete();
                }
                }
                mDbManager.deleteBookshelfNovel(mDataList.get(i).getNovelUrl());
                mDbManager.deleteBookCalotaNovel(mDataList.get(i).getNovelUrl());
                if(login_admin!=null){
                  delectBookshelfadd(login_admin.getToken(), mDataList.get(i).getNovelUrl());
                }
                  mDataList.remove(i);
            }
        }
        mCheckedList.clear();
        for (int i = 0; i < mDataList.size(); i++) {
            mCheckedList.add(false);
        }
        //mBookshelfNovelsAdapter.setIsMultiDelete(false);
        mBookshelfNovelsAdapter.notifyDataSetChanged();
        mIsDeleting = false;
        if(mBookshelfNovelsAdapter.getItemCount()==0){
//            Intent intent = new Intent(MyBookshelfActivity.this, MainActivity.class);
//            intent.putExtra("islaod", "2");
//            startActivity(intent);
            finish();

        }
    }

    private boolean deleteCheck() {
        for (int i = 0; i < mCheckedList.size(); i++) {
            if (mCheckedList.get(i)) {
                return true;
            }
        }
        showShortToast("请先选定要删除的小说");
        return false;
    }

    @Override
    protected void initView() {
        fpShadowLayout=findViewById(R.id.v_main_bottom_bar_separator);
        mBookshelfNovelsRv = findViewById(R.id.rv_bookshelf_bookshelf_novels_list);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mBookshelfNovelsRv.setLayoutManager(gridLayoutManager);

        mSelectAllTv = findViewById(R.id.tv_bookshelf_multi_delete_select_all);
        mSelectAllTv.setOnClickListener(this);

        mDeleteTv = findViewById(R.id.tv_bookshelf_multi_delete_delete);
        mDeleteTv.setOnClickListener(this);

        iv_bookshelf_more = findViewById(R.id.iv_bookshelf_more);
        iv_bookshelf_more.setOnClickListener(this);

       // rv_bookshelf_multi_delete_bar=findViewById(R.id.rv_bookshelf_multi_delete_bar);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void doAfterInit() {
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Override
    public void show(int num) {
        if(num!=0) {
            mDeleteTv.setText("删除(" + num + ")");
        }else {
            mDeleteTv.setText("删除");
        }
    }
    boolean is_allselect;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_bookshelf_multi_delete_select_all:
                // 全选
                int z = 0;
                for (int i = 0; i < mCheckedList.size(); i++) {
                    if(mCheckedList.get(i)==true) {
                        z++;
                    }
                }
                if(z==mCheckedList.size()){
                    is_allselect=false;
                    for (int i = 0; i < mCheckedList.size(); i++) {
                        mCheckedList.set(i, false);
                    }
                    mDeleteTv.setText("删除");
                }else {
                if(!is_allselect) {
                    int num = 0;
                    for (int i = 0; i < mCheckedList.size(); i++) {
                        mCheckedList.set(i, true);
                        num++;
                    }
                    is_allselect=true;
                    if (num != 0) {
                        mDeleteTv.setText("删除(" + num + ")");
                    }
                }else {
                   is_allselect=false;
                    for (int i = 0; i < mCheckedList.size(); i++) {
                        mCheckedList.set(i, false);
                    }
                    mDeleteTv.setText("删除");
                }}
                for (int i = 0; i < mCheckedList.size(); i++) {
                    mBookshelfNovelsAdapter.notifyItemChanged(i,mBookshelfNovelsAdapter.NOTIFY_IV);
                }
                //mBookshelfNovelsAdapter.notifyDataSetChanged();
                break;

            case R.id.iv_bookshelf_more:
                for (int i = 0; i < mCheckedList.size(); i++) {
                    mCheckedList.set(i, false);
                }
                if(iv_bookshelf_more.getText().equals("编辑")){
                    mBookshelfNovelsAdapter.setIsMultiDelete(true);
                    iv_bookshelf_more.setText("取消");
                    fpShadowLayout.setVisibility(View.VISIBLE);
                }else {
                    mBookshelfNovelsAdapter.setIsMultiDelete(false);
                    iv_bookshelf_more.setText("编辑");
                    fpShadowLayout.setVisibility(View.GONE);
                    // 全选
                }
                mDeleteTv.setText("删除");
                for (int i = 0; i < mCheckedList.size(); i++) {
                    mBookshelfNovelsAdapter.notifyItemChanged(i,mBookshelfNovelsAdapter.NOTIFY_IV);
                }
                break;

            case R.id.tv_bookshelf_multi_delete_delete:
                // 删除操作
                if (!deleteCheck()) {
                    break;
                }
                if(!this.isDestroyed()) {
                    new TipDialog.Builder(this)
                            .setContent("确定要删除已选择的书籍吗？")
                            .setCancel("取消")
                            .setEnsure("确定")
                            .setOnClickListener(new TipDialog.OnClickListener() {
                                @Override
                                public void clickEnsure() {
                                    multiDelete();
                                    mDeleteTv.setText("删除");
                                    Intent recever = new Intent("com.zhh.android");
                                    recever.putExtra("type",1);
                                    sendBroadcast(recever);
                                }

                                @Override
                                public void clickCancel() {

                                }
                            })
                            .build()
                            .show();
                }
                break;
            default:
                break;
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            //code........
//            Intent intent = new Intent(MyBookshelfActivity.this, MainActivity.class);
//            intent.putExtra("islaod", "2");
//            startActivity(intent);
            finish();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Intent recever = new Intent("com.zhh.android");
//        recever.putExtra("type",1);
//        sendBroadcast(recever);
    }
}
