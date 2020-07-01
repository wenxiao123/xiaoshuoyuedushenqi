package com.novel.collection.view.fragment.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bifan.txtreaderlib.main.TxtConfig;
import com.novel.collection.R;
import com.novel.collection.adapter.BookshelfNovelsAdapter;
import com.novel.collection.base.BaseFragment;
import com.novel.collection.constant.EventBusCode;
import com.novel.collection.constract.IBookshelfContract;
import com.novel.collection.db.DatabaseManager;
import com.novel.collection.entity.bean.Cataloginfo;
import com.novel.collection.entity.bean.Login_admin;
import com.novel.collection.entity.bean.Noval_Readcored;
import com.novel.collection.entity.bean.PersonBean;
import com.novel.collection.entity.data.BookshelfNovelDbData;
import com.novel.collection.entity.epub.OpfData;
import com.novel.collection.entity.eventbus.Event;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.presenter.BookshelfPresenter;
import com.novel.collection.util.FileUtil;
import com.novel.collection.util.LogUtils;
import com.novel.collection.util.NetUtil;
import com.novel.collection.util.SpUtil;
import com.novel.collection.view.activity.BookCataloActivity;
import com.novel.collection.view.activity.MainActivity;
import com.novel.collection.view.activity.MyBookshelfActivity;
import com.novel.collection.view.activity.ReadActivity;
import com.novel.collection.view.activity.ReadrecoderActivity;
import com.novel.collection.view.activity.SearchActivity;
import com.novel.collection.view.activity.TxtPlayActivity;
import com.novel.collection.view.activity.WYReadActivity;
import com.novel.collection.weyue.db.entity.CollBookBean;
import com.novel.collection.widget.TipDialog;
import com.google.gson.Gson;
import com.novel.collection.widget.WrapContentLinearGridManager;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.LoginException;

//import cn.bmob.v3.http.bean.Collect;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 书架模块
 */
public class BookshelfFragment extends BaseFragment<BookshelfPresenter>
        implements View.OnClickListener, IBookshelfContract.View {

    private static final String TAG = "BookshelfFragment";

    private RecyclerView mBookshelfNovelsRv;
    private TextView mLocalAddTv;
    private ImageView mLocalAddIv;
    private ImageView mBookshelfMore;
    private RelativeLayout mLoadingRv;
    private LinearLayout l_emputy;
    private RelativeLayout mMultiDeleteRv;
    private TextView mSelectAllTv;
    private TextView mCancelTv;
    private TextView mDeleteTv;
    private RefreshLayout mRefreshSrv;
    private List<BookshelfNovelDbData> mDataList = new ArrayList<>();
    private List<BookshelfNovelDbData> mDataList_all = new ArrayList<>();
    private List<BookshelfNovelDbData> mDataList1 = new ArrayList<>();
    private BookshelfNovelDbData bookshelfNovelDbData = new BookshelfNovelDbData("", "", "", 0, 2, -1);
    private List<Boolean> mCheckedList = new ArrayList<>();
    private BookshelfNovelsAdapter mBookshelfNovelsAdapter;
    private boolean mIsDeleting = false;
    PersonBean personBean;

    public void setPersonBean(PersonBean personBean,boolean is_add) {
        this.is_add = is_add;
        this.personBean = personBean;
    }

    private DatabaseManager mDbManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void doInOnCreate() {
        //if (login_admin == null) {
           //mPresenter.queryAllBook();
            int first= SpUtil.getIsfirst();
            Log.e("DDD", "doInOnCreate: "+first);
            if(first==0){
                queryallBook_First("");
            }else {
                queryallBook("");
            }
//        } else {
//            queryallBook(login_admin.getToken());
//        }
    }

    boolean is_add;
    int select_position;
    public void updata2(boolean flag,int position) {
        is_add = flag;
        if(position>=0) {
            select_position = position;
        }
        login_admin= (Login_admin) SpUtil.readObject(getContext());
        if (mPresenter != null) {
//            if (login_admin == null) {
//                mPresenter.queryAllBook();
//            } else {
                queryallBook("");
//            }
        }
    }

    @Override
    protected BookshelfPresenter getPresenter() {
        return new BookshelfPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bookshelf;
    }

    @Override
    protected void initData() {
        mDbManager = DatabaseManager.getInstance();
        login_admin = (Login_admin) SpUtil.readObject(getContext());
    }

    @Override
    protected void initView() {
        mBookshelfNovelsRv = getActivity().findViewById(R.id.rv_bookshelf_bookshelf_novels_list);
        final WrapContentLinearGridManager gridLayoutManager = new WrapContentLinearGridManager(getActivity(), 4);
        mBookshelfNovelsRv.setLayoutManager(gridLayoutManager);
        l_emputy = getActivity().findViewById(R.id.l_emputy);
        btn_jingxuan=getActivity().findViewById(R.id.btn_jingxuan);

        mLocalAddTv = getActivity().findViewById(R.id.tv_bookshelf_add);
        mLocalAddTv.setOnClickListener(this);

        mLocalAddIv = getActivity().findViewById(R.id.iv_bookshelf_add);
        mLocalAddIv.setOnClickListener(this);

        mBookshelfMore = getActivity().findViewById(R.id.iv_bookshelf_more);
        mBookshelfMore.setOnClickListener(this);

        mLoadingRv = getActivity().findViewById(R.id.rv_bookshelf_loading);

        mMultiDeleteRv = getActivity().findViewById(R.id.rv_bookshelf_multi_delete_bar);
        mSelectAllTv = getActivity().findViewById(R.id.tv_bookshelf_multi_delete_select_all);
        mSelectAllTv.setOnClickListener(this);
        mCancelTv = getActivity().findViewById(R.id.tv_bookshelf_multi_delete_cancel);
        mCancelTv.setOnClickListener(this);
        mDeleteTv = getActivity().findViewById(R.id.tv_bookshelf_multi_delete_delete);
        mDeleteTv.setOnClickListener(this);
        btn_jingxuan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).checked();
            }
        });
        mRefreshSrv =getActivity().findViewById(R.id.srv_novel_refresh);
//        mRefreshSrv.setEnableRefresh(false);
//        mRefreshSrv.setEnableLoadMore(false);
        mRefreshSrv.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                z=1;
                isRefresh=true;
                queryallBook("");
                //mPresenter.getRankData(type + "", new_or_hot + "",sort + "", category_id + "",z+" ");
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                //refreshlayout.finishLoadMore(false);
            }
        });
        mRefreshSrv.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                z++;
                queryallBook("");
                //mPresenter.getRankData(type + "", new_or_hot + "",sort + "", category_id + "",z+" ");
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
                // refreshlayout.finishLoadMore(false);
            }
        });
        ClassicsHeader classicsHeader=new ClassicsHeader(getContext());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(new Date());
        classicsHeader.setLastUpdateText("最后更新:"+dateString);
        //refreshLayout.setRefreshHeader(new BezierRadarHeader(getContext()).setEnableHorizontalDrag(true));
        mRefreshSrv.setRefreshHeader(classicsHeader);
        //refreshLayout.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshSrv.setRefreshFooter(new ClassicsFooter(getContext()));
        receiver = new MyReceiver();
        // 注册广播接受者
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.zhh.android");//要接收的广播
        getActivity().registerReceiver(receiver, intentFilter);//注册接收者
    }
    int z=1;
    boolean isRefresh=false;
    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventCome(Event event) {
        switch (event.getCode()) {
            case EventBusCode.BOOKSHELF_UPDATE_LIST:
                mDataList.clear();
//                if (login_admin == null) {
//                    mPresenter.queryAllBook();
//                } else {
                    //queryallBook("");
//                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bookshelf_more:
                showPupowindpw(mBookshelfMore);
                break;
            case R.id.iv_bookshelf_add:
                // 导入本机小说
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_bookshelf_multi_delete_select_all:
                // 全选
                for (int i = 0; i < mCheckedList.size(); i++) {
                    mCheckedList.set(i, true);
                }
                mBookshelfNovelsAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_bookshelf_multi_delete_cancel:
                // 取消多选删除
                cancelMultiDelete();
                break;
            case R.id.tv_bookshelf_multi_delete_delete:
                // 删除操作
//                if (!deleteCheck()) {
//                    break;
//                }
//                if(!getActivity().isDestroyed()) {
//                    new TipDialog.Builder(getActivity())
//                            .setContent("确定要删除这些小说吗？")
//                            .setCancel("不了")
//                            .setEnsure("确定")
//                            .setOnClickListener(new TipDialog.OnClickListener() {
//                                @Override
//                                public void clickEnsure() {
//                                    multiDelete();
//                                }
//
//                                @Override
//                                public void clickCancel() {
//
//                                }
//                            })
//                            .build()
//                            .show();
//                }
                break;
            default:
                break;
        }
    }

    /**
     * 取消多选删除
     */
    private void cancelMultiDelete() {
        for (int i = 0; i < mCheckedList.size(); i++) {
            mCheckedList.set(i, false);
        }
        mBookshelfNovelsAdapter.setIsMultiDelete(false);
        mBookshelfNovelsAdapter.notifyDataSetChanged();
        mMultiDeleteRv.setVisibility(View.GONE);
    }

//    /**
//     * 多选删除
//     */
//    private void multiDelete() {
//        mIsDeleting = true;
//        for (int i = mDataList.size() - 1; i >= 0; i--) {
//            if (mCheckedList.get(i)) {
//                // 从数据库中删除该小说
//                mDbManager.deleteBookshelfNovel(mDataList.get(i).getNovelUrl());
//                mDataList.remove(i);
//            }
//        }
//        mCheckedList.clear();
//        for (int i = 0; i < mDataList.size(); i++) {
//            mCheckedList.add(false);
//        }
//        mBookshelfNovelsAdapter.setIsMultiDelete(false);
//        mBookshelfNovelsAdapter.notifyDataSetChanged();
//        mMultiDeleteRv.setVisibility(View.GONE);
//        mIsDeleting = false;
//    }
//
//    private boolean deleteCheck() {
//        for (int i = 0; i < mCheckedList.size(); i++) {
//            if (mCheckedList.get(i)) {
//                return true;
//            }
//        }
//        showShortToast("请先选定要删除的小说");
//        return false;
//    }

    private void showPupowindpw(View parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popu_item, null);
        ListView lv_appointment = (ListView) view.findViewById(R.id.list_view);
        final String[] datas = {getString(R.string.manger_bookshelf), getString(R.string.read_reder)};//, getString(R.string.load_local_book)};
        final Integer[] ints = {R.mipmap.bookshelf, R.mipmap.read_recoder};//, R.mipmap.load_book};
        PupoAdapter mainAdapter = null;
        if (datas != null) {
            mainAdapter = new PupoAdapter(datas, ints);
        }
        lv_appointment.setAdapter(mainAdapter);
        // 创建一个PopuWidow对象,设置宽高
        final PopupWindow popupWindow = new PopupWindow(view, getResources().getDimensionPixelOffset(R.dimen.dp_179), ViewGroup.LayoutParams.WRAP_CONTENT);

        // 使其聚集,可点击
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        backgroundAlpha(0.5f);
        popupWindow.showAsDropDown(parent, -280, 35);
        lv_appointment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent bookShelfintent = new Intent(getContext(), MyBookshelfActivity.class);
                        startActivity(bookShelfintent);
                        break;
                    case 1:
                        Intent readRecordintent = new Intent(getContext(), ReadrecoderActivity.class);
                        startActivity(readRecordintent);
                        break;
                    case 2:
                        Intent intent = new Intent(getContext(), BookCataloActivity.class);
                        startActivity(intent);
                        break;
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) { // 选择了才继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String[] pros = {MediaStore.Files.FileColumns.DATA};
            try {
                Cursor cursor = getActivity().managedQuery(uri, pros, null, null, null);
                int actual_txt_column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String filePath = cursor.getString(actual_txt_column_index);
//            Uri uri = data.getData();
//            String filePath = FileUtil.uri2FilePath(getActivity(), uri);
                File file = new File(filePath);
                String fileName = file.getName();
                Log.d(TAG, "onActivityResult: fileLen = " + file.length());
                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);  // 后缀名
                if (suffix.equals("txt")) {
                    if (mDbManager.isExistInBookshelfNovel(filePath)) {
                        showShortToast("该小说已导入");
                        return;
                    }
                    if (FileUtil.getFileSize(file) > 100) {
                        showShortToast("文件过大");
                        return;
                    }
                    // 将该小说的数据存入数据库
                    BookshelfNovelDbData dbData = new BookshelfNovelDbData(filePath, file.getName(),
                            "", 0, 0, 1);
                    mDbManager.insertOrUpdateBook(dbData);
                    // 更新列表
                    mPresenter.queryAllBook();
                } else if (suffix.equals("epub")) {
                    if (mDbManager.isExistInBookshelfNovel(filePath)) {
                        showShortToast("该小说已导入");
                        return;
                    }
                    if (FileUtil.getFileSize(file) > 100) {
                        showShortToast("文件过大");
                        return;
                    }
                    // 在子线程中解压该 epub 文件
                    mLoadingRv.setVisibility(View.VISIBLE);
                    mPresenter.unZipEpub(filePath);
                } else {
                    showShortToast("不支持该类型");
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "选择出错了", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    public void upload(PersonBean personBean) {
        if (mDbManager == null) {
            mDbManager = DatabaseManager.getInstance();
        }
        if (personBean.getFiletype().equals("txt")) {
            if (mDbManager.isExistInBookshelfNovel(personBean.getFilepath())) {
                showShortToast("该小说已导入");
                return;
            }
            if (FileUtil.getFileSize(new File(personBean.getFilepath())) > 100) {
                showShortToast("文件过大");
                return;
            }
            // 将该小说的数据存入数据库
            BookshelfNovelDbData dbData = new BookshelfNovelDbData(personBean.getFilepath(), personBean.getName(),
                    "", 0, 0, 1); //personBean.getFilepath()
            dbData.setFuben_id(personBean.getFilepath());
            dbData.setPosition(1);
            dbData.setChapterid(1+"");
            // Log.e("QQQ", "upload: "+personBean.getFilepath());
            mDbManager.insertOrUpdateBook(dbData);
            // 更新列表
            mPresenter.queryAllBook();
        } else if (personBean.getFiletype().equals("epub")) {
            if (mDbManager.isExistInBookshelfNovel(personBean.getFilepath())) {
                showShortToast("该小说已导入");
                return;
            }
            if (FileUtil.getFileSize(new File(personBean.getFilepath())) > 100) {
                showShortToast("文件过大");
                return;
            }
            // 在子线程中解压该 epub 文件
            mLoadingRv.setVisibility(View.VISIBLE);
            mPresenter.unZipEpub(personBean.getFilepath());
        } else {
            showShortToast("不支持该类型");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (personBean != null) {
            LogUtils.e(personBean.getFiletype());
            upload(personBean);
            personBean = null;
        }
       // Log.e("QQQ8", "onResume: "+999);
    }

    Login_admin login_admin;
    private List<BookshelfNovelDbData> bookshelfNovelDbDataList = new ArrayList<>();

    /**
     * 查询所有书籍信息成功
     */
    @Override
    public void queryAllBookSuccess(List<BookshelfNovelDbData> dataList) {
        bookshelfNovelDbDataList.clear();
            for(int j=0;j<mDataList_all.size();j++){
                boolean isExit=false;
                BookshelfNovelDbData novelDbData=mDataList_all.get(j);
                Log.e("ZXZ", "queryAllBookSuccess: "+novelDbData);
                for(int i=0;i<dataList.size();i++){
                 if(novelDbData.getNovelUrl().equals(dataList.get(i).getNovelUrl())){
                    isExit=true;
                    if(dataList.get(i).getType()>=0) {
                        bookshelfNovelDbDataList.add(dataList.get(i));
                    }else {
                        BookshelfNovelDbData dbData=dataList.get(i);
                        dbData.setType(0);
                        bookshelfNovelDbDataList.add(dataList.get(i));
                    }
                    break;
                }
            }
                if(isExit==false){
                    bookshelfNovelDbDataList.add(novelDbData);
                }
        }
        //mDataList_all.clear();
        if(z==1) {
            mDataList.clear();
            isRefresh=false;
        }
        mLoadingRv.setVisibility(View.GONE);
        if (mBookshelfNovelsAdapter == null) {
            mDataList.addAll(bookshelfNovelDbDataList);
            mCheckedList.clear();
            for (int i = 0; i < mDataList.size(); i++) {
                mCheckedList.add(false);
            }
            initAdapter();
            mBookshelfNovelsRv.setAdapter(mBookshelfNovelsAdapter);
        } else {
            mDataList.addAll(bookshelfNovelDbDataList);
            mCheckedList.clear();
            if(bookshelfNovelDbDataList.size()<20&&isFull==false) {
                mDataList.add(bookshelfNovelDbData);
                mCheckedList.add(false);
                isFull=true;
            }else  if(z==1&&bookshelfNovelDbDataList.size()<20) {
                mDataList.add(bookshelfNovelDbData);
                mCheckedList.add(false);
            }
            mCheckedList.add(false);
            if (mDataList.size() == 1) {
                l_emputy.setVisibility(View.VISIBLE);
                mBookshelfNovelsRv.setVisibility(View.GONE);
            } else {
                l_emputy.setVisibility(View.GONE);
                mBookshelfNovelsRv.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < mDataList.size(); i++) {
                mCheckedList.add(false);
            }
            if (is_add == false) {
                mBookshelfNovelsAdapter.notifyItemChanged(select_position, mBookshelfNovelsAdapter.
                           NOTIFY_ET);
            } else {
                mBookshelfNovelsAdapter.notifyDataSetChanged();
            }
        }
    }
    //将不重复的书籍添加进数据库中
    public void queryAllBookSuccess2(List<BookshelfNovelDbData> dataList) {
//        if(isRefresh){
//            mDataList_all.clear();
//            isRefresh=false;
//        }
        mDataList_all.clear();
        mDataList_all.addAll(dataList);
        //mDbManager.clear_Book();
        for (int i = 0; i < mDataList_all.size(); i++) {
            Log.e("ZXZ2", "queryAllBookSuccess: "+mDataList_all.get(i));
            BookshelfNovelDbData bookshelfNovelDbData=mDbManager.getBookshelfNovel(mDataList_all.get(i).getNovelUrl());
            if (bookshelfNovelDbData==null) {
                mDbManager.insertOrUpdateBook(new BookshelfNovelDbData(mDataList_all.get(i).getNovelUrl(),
                        mDataList_all.get(i).getName(), mDataList_all.get(i).getCover(), 0, 0, mDataList_all.get(i).getWeight(), 0 + "", mDataList_all.get(i).getWeight(), mDataList_all.get(i).getStatus()));
            }else if((bookshelfNovelDbData!=null&&bookshelfNovelDbData.getType()==-2)){
                bookshelfNovelDbData.setType(0);
                mDbManager.insertOrUpdateBook(bookshelfNovelDbData);
            }
        }
        mPresenter.queryAllBook();
    }

    List<Noval_Readcored> noval_readcoreds = new ArrayList<>();

    private void queryallBook(String token) {
        if(token!=null){
        Gson mGson = new Gson();
        String url = UrlObtainer.GetUrl() + "/api/Userbook/index";
        RequestBody requestBody = new FormBody.Builder()
//                .add("token", token)
                .add("type", 0 + "")
                .add("page", z + "")
                .add("limit", 20 + "")
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                LogUtils.e(url + " " + token + " " + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        if (jsonObject.isNull("data")) {
                            showShortToast("请求数据失败");
                        } else {
                            JSONObject object = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = object.getJSONArray("data");
                            mDataList1.clear();
                            noval_readcoreds.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                noval_readcoreds.add(mGson.fromJson(jsonArray.getJSONObject(i).toString(), Noval_Readcored.class));
                                mDataList1.add(new BookshelfNovelDbData(noval_readcoreds.get(i).getNovel_id(), noval_readcoreds.get(i).getTitle(), noval_readcoreds.get(i).getPic()
                                        , 0, 0, 0, noval_readcoreds.get(i).getChapter_id(), noval_readcoreds.get(i).getChapter_count(), noval_readcoreds.get(i).getSerialize()));
                            }
                            queryAllBookSuccess2(mDataList1);
                        }
                    } else {
                        showShortToast("请求数据失败");
                    }
                } catch (JSONException e) {
                    queryAllBookSuccess2(mDataList1);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                queryAllBookSuccess2(mDataList1);
                showShortToast(errorMsg);
            }
        });
        }
    }
    private void queryallBook_First(String token) {
        if(token!=null){
            Gson mGson = new Gson();
            String url = UrlObtainer.GetUrl() + "/api/Userbook/index";
            RequestBody requestBody = new FormBody.Builder()
//                .add("token", token)
                    .add("type", 0 + "")
                    .add("page", 1 + "")
                    .add("limit", 20 + "")
                    .build();
            OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
                @Override
                public void onResponse(String json) {   // 得到 json 数据
                    LogUtils.e(url + " " + token + " " + json);
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String code = jsonObject.getString("code");
                        if (code.equals("1")) {
                            if (jsonObject.isNull("data")) {
                                showShortToast("请求数据失败");
                            } else {
                                JSONObject object = jsonObject.getJSONObject("data");
                                JSONArray jsonArray = object.getJSONArray("data");
                                mDataList1.clear();
                                noval_readcoreds.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    noval_readcoreds.add(mGson.fromJson(jsonArray.getJSONObject(i).toString(), Noval_Readcored.class));
                                    mDataList1.add(new BookshelfNovelDbData(noval_readcoreds.get(i).getNovel_id(), noval_readcoreds.get(i).getTitle(), noval_readcoreds.get(i).getPic()
                                            , 0, 0, 0, noval_readcoreds.get(i).getChapter_id(), noval_readcoreds.get(i).getChapter_count(), noval_readcoreds.get(i).getSerialize()));
                                }
                                queryAllBookSuccess2(mDataList1);
                                SpUtil.saveIs_first(1);
                            }
                        } else {
                            showShortToast("请求数据失败");
                        }
                    } catch (JSONException e) {
                        queryAllBookSuccess2(mDataList1);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String errorMsg) {
                    queryAllBookSuccess2(mDataList1);
                    showShortToast(errorMsg);
                }
            });
        }
    }
    boolean isFull=false;
    private void initAdapter() {
        if(z==1&&mDataList.size()<20) {
            mDataList.add(bookshelfNovelDbData);
            mCheckedList.add(false);
            isFull=true;
        }
        mBookshelfNovelsAdapter = new BookshelfNovelsAdapter(getActivity(), mDataList, mCheckedList,
                new BookshelfNovelsAdapter.BookshelfNovelListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void clickItem(int position) {
                        Log.e("QQQ", "clickItem: "+position);
                        try {
                            if (position == mDataList.size() - 1) {
                                ((MainActivity) getActivity()).checked();
                            } else {
                                if (mIsDeleting || (mDataList.size() > 0 && mDataList.get(position).getType() == -1)) {
                                    return;
                                }
                                if (mDataList.get(position).getType() == 0) {
                                    if (!NetUtil.hasInternet(getActivity())) {
                                        showShortToast("当前无网络，请检查网络后重试");
                                        return;
                                    }
                                    CollBookBean bookBean = new CollBookBean(mDataList.get(position).getNovelUrl(), mDataList.get(position).getName(), "", "",
                                            mDataList.get(position).getCover(), false, 0, 0,
                                            "", "", mDataList.get(position).getWeight(), "",
                                            false, false);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(WYReadActivity.EXTRA_COLL_BOOK, bookBean);
                                    bundle.putBoolean(WYReadActivity.EXTRA_IS_COLLECTED, true);
                                    bundle.putString(WYReadActivity.LOAD_PATH, mDataList.get(position).getNovelUrl());
                                    bundle.putString(WYReadActivity.CHPTER_ID, mDataList.get(position).getChapterid());
                                    bundle.putString(WYReadActivity.PAGE_ID, mDataList.get(position).getPosition()+"");
                                    LogUtils.e(mDataList.get(position).getPosition()+" "+mDataList.get(position).getChapterid());
                                    startActivity(WYReadActivity.class, bundle);
                                } else {
                                    CollBookBean bookBean = new CollBookBean(mDataList.get(position).getFuben_id(), mDataList.get(position).getName(), "", "",
                                            mDataList.get(position).getCover(), false, 0, 0,
                                            "", "", mDataList.get(position).getWeight(), "",
                                            false, true);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(WYReadActivity.EXTRA_COLL_BOOK, bookBean);
                                    bundle.putBoolean(WYReadActivity.EXTRA_IS_COLLECTED, true);//
                                    bundle.putString(WYReadActivity.LOAD_PATH, mDataList.get(position).getNovelUrl());
                                    bundle.putString(WYReadActivity.CHPTER_ID, mDataList.get(position).getChapterid());
                                    bundle.putString(WYReadActivity.PAGE_ID, mDataList.get(position).getPosition()+"");
                                    LogUtils.e(mDataList.get(position).getPosition()+" "+mDataList.get(position).getChapterid());
                                    startActivity(WYReadActivity.class, bundle);
                                }
                            }
                        } catch (Exception ex) {

                        }
                    }

                    @Override
                    public void longClick(int position) {

                    }
                });
        if (mDataList.size() == 1) {
            l_emputy.setVisibility(View.VISIBLE);
            mBookshelfNovelsRv.setVisibility(View.GONE);
        } else {
            l_emputy.setVisibility(View.GONE);
            mBookshelfNovelsRv.setVisibility(View.VISIBLE);
        }
    }

    TextView btn_jingxuan;

    /**
     * 查询所有书籍信息失败
     */
    @Override
    public void queryAllBookError(String errorMsg) {
        showShortToast(errorMsg);
        mLoadingRv.setVisibility(View.GONE);
    }

    public void startActivity(Class<?> className, Bundle bundle) {
        Intent intent = new Intent(getContext(), className);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 解压 epub 文件成功
     */
    @Override
    public void unZipEpubSuccess(String filePath, OpfData opfData) {
        // 将书籍信息写入数据库
        File file = new File(filePath);
        BookshelfNovelDbData dbData = new BookshelfNovelDbData(filePath, file.getName(),
                opfData.getCover(), 0, 0, 2);
        mDbManager.insertOrUpdateBook(dbData);
        // 更新列表
        mPresenter.queryAllBook();

        mLoadingRv.setVisibility(View.GONE);
        Log.d(TAG, "unZipEpubSuccess: opfData = " + opfData);
        showShortToast("导入成功");
    }

    /**
     * 解压 epub 文件失败
     */
    @Override
    public void unZipEpubError(String errorMsg) {
        mLoadingRv.setVisibility(View.GONE);
        Log.d(TAG, "unZipEpubError: " + errorMsg);
        showShortToast("导入失败");
    }

    class PupoAdapter extends BaseAdapter {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        String[] strings;
        Integer[] integers;

        public PupoAdapter(String[] strings, Integer[] integers) {
            this.strings = strings;
            this.integers = integers;
        }

        @Override
        public int getCount() {
            return strings.length;
        }

        @Override
        public Object getItem(int position) {
            return strings[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
            ImageView imageView = convertView.findViewById(R.id.img);
            TextView textView = convertView.findViewById(R.id.text);
            imageView.setImageResource(integers[position]);
            textView.setText(strings[position]);
            return convertView;
        }
    }
    MyReceiver receiver;
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            isRefresh=true;
            int i=intent.getIntExtra("type",0);
            String position=intent.getStringExtra("position");
            int z = -1;
            if(position!=null) {
                for (int j = 0; j < mDataList.size(); j++) {
                    if (mDataList.get(j).getType()==0&&mDataList.get(j).getNovelUrl().equals(position)) {
                        z = j;
                    }else if(mDataList.get(j).getType()==1&&mDataList.get(j).getFuben_id().equals(position)){
                        z = j;
                    }
                }
            }
            Log.e("WWW3", "onReceive: "+i);
            if(i==0) {
                updata2(false,z);
            }else if(i==1){
                mLoadingRv.setVisibility(View.VISIBLE);
                mBookshelfNovelsRv.setVisibility(View.GONE);
                updata2(true,z);
            }
            }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
}
