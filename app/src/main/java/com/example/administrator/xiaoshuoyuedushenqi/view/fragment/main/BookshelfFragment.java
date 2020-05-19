package com.example.administrator.xiaoshuoyuedushenqi.view.fragment.main;

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
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.BookshelfNovelsAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseFragment;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IBookshelfContract;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Cataloginfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_Readcored;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.PersonBean;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.epub.OpfData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.BookshelfPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.FileUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.LogUtils;
import com.example.administrator.xiaoshuoyuedushenqi.util.NetUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.BookCataloActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.MainActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.MyBookshelfActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.ReadActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.ReadrecoderActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.SearchActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.TxtPlayActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.WYReadActivity;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.db.entity.CollBookBean;
import com.example.administrator.xiaoshuoyuedushenqi.widget.TipDialog;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.security.auth.login.LoginException;

//import cn.bmob.v3.http.bean.Collect;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Created on 2020/2/20
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

    private List<BookshelfNovelDbData> mDataList = new ArrayList<>();
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
        if (login_admin == null) {
            mPresenter.queryAllBook();
        } else {
            queryallBook(login_admin.getToken());
        }
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
            if (login_admin == null) {
                mPresenter.queryAllBook();
            } else {
                queryallBook(login_admin.getToken());
            }
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
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
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
        receiver = new MyReceiver();
        // 注册广播接受者
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.zhh.android");//要接收的广播
        getActivity().registerReceiver(receiver, intentFilter);//注册接收者
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventCome(Event event) {
        switch (event.getCode()) {
            case EventBusCode.BOOKSHELF_UPDATE_LIST:
                mDataList.clear();
                if (login_admin == null) {
                    mPresenter.queryAllBook();
                } else {
                    queryallBook(login_admin.getToken());
                }
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
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");      // 最近文件（任意类型）
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(intent, 1);
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
                if (!deleteCheck()) {
                    break;
                }
                if(!getActivity().isDestroyed()) {
                    new TipDialog.Builder(getActivity())
                            .setContent("确定要删除这些小说吗？")
                            .setCancel("不了")
                            .setEnsure("确定")
                            .setOnClickListener(new TipDialog.OnClickListener() {
                                @Override
                                public void clickEnsure() {
                                    multiDelete();
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

    /**
     * 多选删除
     */
    private void multiDelete() {
        mIsDeleting = true;
        for (int i = mDataList.size() - 1; i >= 0; i--) {
            if (mCheckedList.get(i)) {
                // 从数据库中删除该小说
                mDbManager.deleteBookshelfNovel(mDataList.get(i).getNovelUrl());
                mDataList.remove(i);
            }
        }
        mCheckedList.clear();
        for (int i = 0; i < mDataList.size(); i++) {
            mCheckedList.add(false);
        }
        mBookshelfNovelsAdapter.setIsMultiDelete(false);
        mBookshelfNovelsAdapter.notifyDataSetChanged();
        mMultiDeleteRv.setVisibility(View.GONE);
        mIsDeleting = false;
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
    }

    Login_admin login_admin;

    /**
     * 查询所有书籍信息成功
     */
    @Override
    public void queryAllBookSuccess(List<BookshelfNovelDbData> dataList) {
        Log.e("QQQ", "queryAllBookSuccess: "+dataList.size());
        mDataList.clear();
        mLoadingRv.setVisibility(View.GONE);
        if (mBookshelfNovelsAdapter == null) {
            mDataList = dataList;
            mCheckedList.clear();
            for (int i = 0; i < mDataList.size(); i++) {
                mCheckedList.add(false);
            }
            initAdapter();
            mBookshelfNovelsRv.setAdapter(mBookshelfNovelsAdapter);
        } else {
            mDataList.clear();
            mDataList.addAll(dataList);
            mCheckedList.clear();
            mDataList.add(bookshelfNovelDbData);
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
//                for (int i = 0; i < mBookshelfNovelsAdapter.getItemCount(); i++) {
//                    mBookshelfNovelsAdapter.notifyItemChanged(i, mBookshelfNovelsAdapter.
//                            NOTIFY_ET);
//                }
                mBookshelfNovelsAdapter.notifyItemChanged(select_position, mBookshelfNovelsAdapter.
                           NOTIFY_ET);
            } else {
                mBookshelfNovelsAdapter.notifyDataSetChanged();
            }
        }
        if(login_admin!=null){
            for(int i=0;i<dataList.size();i++){
                try {
                    setBookshelfadd(login_admin.getToken(),dataList.get(i).getNovelUrl());
                }catch (Exception ex){
                    continue;
                }
            }
        }
    }

    public void setBookshelfadd(String token, String novel_id) {
        String url = UrlObtainer.GetUrl() + "/api/Userbook/add";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("novel_id", novel_id)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        String message = jsonObject.getString("msg");
                        //mPresenter.(message);
                    } else {
                        //mPresenter.getReadRecordError("请求错误");
                        //getChapterUrlListError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                //getChapterUrlListError("请求错误");
                //mPresenter.getReadRecordError(errorMsg);
            }
        });
    }

    public void queryAllBookSuccess2(List<BookshelfNovelDbData> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            Log.e("OOO", "queryAllBookSuccess2: "+dataList.get(i).getName());
            if (!mDbManager.isExistInBookshelfNovel(dataList.get(i).getNovelUrl())) {
                mDbManager.insertOrUpdateBook(new BookshelfNovelDbData(dataList.get(i).getNovelUrl(),
                        dataList.get(i).getName(), dataList.get(i).getCover(), 0, 0, dataList.get(i).getWeight(), 0 + "", dataList.get(i).getWeight(), dataList.get(i).getStatus()));
            }
        }
        mPresenter.queryAllBook();
    }

    List<Noval_Readcored> noval_readcoreds = new ArrayList<>();

    private void queryallBook(String token) {
        Gson mGson = new Gson();
        String url = UrlObtainer.GetUrl() + "/api/Userbook/index";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("page", 1 + "")
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                LogUtils.e(url+" "+token+" ");
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

    private void initAdapter() {
        mDataList.add(bookshelfNovelDbData);
        mCheckedList.add(false);
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
                                    bundle.putString(WYReadActivity.CHPTER_ID, mDataList.get(position).getChapterid());
                                    bundle.putString(WYReadActivity.PAGE_ID, mDataList.get(position).getPosition()+"");
                                    LogUtils.e(mDataList.get(position).getPosition()+" "+mDataList.get(position).getChapterid());
                                    startActivity(WYReadActivity.class, bundle);
                                    Log.e("QQQ", "clickItem: "+mDataList.get(position).getChapterid()+" "+mDataList.get(position).getPosition());
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
            if(i==0) {
                updata2(false,z);
            }else if(i==1){
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
