package com.example.administrator.xiaoshuoyuedushenqi.view.fragment.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bifan.txtreaderlib.main.TxtConfig;
import com.bifan.txtreaderlib.ui.HwTxtPlayActivity;
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.BookshelfNovelsAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseFragment;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IBookshelfContract;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.PersonBean;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.epub.OpfData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.BookshelfPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.FileUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.NetUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.BookCataloActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.MainActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.MyBookshelfActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.ReadActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.ReadrecoderActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.SearchActivity;
import com.example.administrator.xiaoshuoyuedushenqi.widget.TipDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author WX
 * Created on 2020/2/20
 */
public class BookshelfFragment extends BaseFragment<BookshelfPresenter>
        implements View.OnClickListener, IBookshelfContract.View {

    private static final String TAG = "BookshelfFragment";

    private RecyclerView mBookshelfNovelsRv;
    private TextView mLocalAddTv;
    private ImageView mLocalAddIv;
    private ImageView mBookshelfMore;
    private RelativeLayout mLoadingRv;

    private RelativeLayout mMultiDeleteRv;
    private TextView mSelectAllTv;
    private TextView mCancelTv;
    private TextView mDeleteTv;

    private List<BookshelfNovelDbData> mDataList = new ArrayList<>();
    private BookshelfNovelDbData bookshelfNovelDbData=new BookshelfNovelDbData("","","",0,2,-1);
    private List<Boolean> mCheckedList = new ArrayList<>();
    private BookshelfNovelsAdapter mBookshelfNovelsAdapter;
    private boolean mIsDeleting = false;
    PersonBean personBean;

    public void setPersonBean(PersonBean personBean) {
        this.personBean = personBean;
    }

    private DatabaseManager mDbManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void doInOnCreate() {
        //StatusBarUtil.setLightColorStatusBar(getActivity());
        //StatusBarUtil.setDarkColorStatusBar(getActivity());
        //StatusBarUtil.setStatusBarColor(getActivity(),getActivity().getColor(R.color.colorAccent2));
        mPresenter.queryAllBook();
    }
    public void  updata2(){
        //mPresenter.queryAllBook();
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
    }

    @Override
    protected void initView() {
        mBookshelfNovelsRv = getActivity().findViewById(R.id.rv_bookshelf_bookshelf_novels_list);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mBookshelfNovelsRv.setLayoutManager(gridLayoutManager);

        mLocalAddTv = getActivity().findViewById(R.id.tv_bookshelf_add);
        mLocalAddTv.setOnClickListener(this);

        mLocalAddIv = getActivity().findViewById(R.id.iv_bookshelf_add);
        mLocalAddIv.setOnClickListener(this);

        mBookshelfMore=getActivity().findViewById(R.id.iv_bookshelf_more);
        mBookshelfMore.setOnClickListener(this);

        mLoadingRv = getActivity().findViewById(R.id.rv_bookshelf_loading);

        mMultiDeleteRv = getActivity().findViewById(R.id.rv_bookshelf_multi_delete_bar);
        mSelectAllTv = getActivity().findViewById(R.id.tv_bookshelf_multi_delete_select_all);
        mSelectAllTv.setOnClickListener(this);
        mCancelTv = getActivity().findViewById(R.id.tv_bookshelf_multi_delete_cancel);
        mCancelTv.setOnClickListener(this);
        mDeleteTv = getActivity().findViewById(R.id.tv_bookshelf_multi_delete_delete);
        mDeleteTv.setOnClickListener(this);
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventCome(Event event) {
        switch (event.getCode()) {
            case EventBusCode.BOOKSHELF_UPDATE_LIST:
                mPresenter.queryAllBook();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bookshelf_more:
                showPupowindpw(mLocalAddTv);
                break;
            case R.id.iv_bookshelf_add:
                // 导入本机小说
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");      // 最近文件（任意类型）
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(intent, 1);
                Intent intent=new Intent(getContext(),SearchActivity.class);
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
        for (int i = mDataList.size()-1; i >= 0; i--) {
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
    private void showPupowindpw(View parent){
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popu_item, null);
        ListView lv_appointment = (ListView) view.findViewById(R.id.list_view);
        final String[] datas={getString(R.string.manger_bookshelf),getString(R.string.read_reder),getString(R.string.load_local_book)};
        final Integer[] ints={R.mipmap.bookshelf,R.mipmap.read_recoder,R.mipmap.load_book};
        PupoAdapter mainAdapter=null;
        if (datas != null) {
            mainAdapter = new PupoAdapter(datas,ints);
        }
        lv_appointment.setAdapter(mainAdapter);
        // 创建一个PopuWidow对象,设置宽高
        final PopupWindow popupWindow = new PopupWindow(view, (int) (parent.getWidth()*1.35), ViewGroup.LayoutParams.WRAP_CONTENT);

        // 使其聚集,可点击
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        WindowManager windowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        backgroundAlpha(0.5f);
        popupWindow.showAsDropDown(parent,(int) (parent.getWidth()*0.7),35);
        lv_appointment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent bookShelfintent=new Intent(getContext(),MyBookshelfActivity.class);
                        startActivity(bookShelfintent);
                        break;
                    case 1:
                        Intent readRecordintent=new Intent(getContext(),ReadrecoderActivity.class);
                        startActivity(readRecordintent);
                        break;
                    case 2:
                        Intent intent=new Intent(getContext(),BookCataloActivity.class);
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
                mDbManager.insertBookshelfNovel(dbData);
                // 更新列表
                mPresenter.queryAllBook();
            }
            else if (suffix.equals("epub")) {
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
            }
            else {
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

    public void upload(PersonBean personBean){
        if(mDbManager==null) {
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
                    "", 0, 0, 1);
            mDbManager.insertBookshelfNovel(dbData);
            // 更新列表
            mPresenter.queryAllBook();
        }
        else if (personBean.getFiletype().equals("epub")) {
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
        }
        else {
            showShortToast("不支持该类型");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //mPresenter.queryAllBook();
//       if(mDataList.size()>0&&mDataList.get(mDataList.size()-1).getType()!=-1){
//           mDataList.add(bookshelfNovelDbData);
//           mCheckedList.add(false);
        if(mBookshelfNovelsAdapter!=null) {
            //mPresenter.queryAllBook();
        }
        if(personBean!=null){
            upload(personBean);
            personBean=null;
        }
//       }
    }

    /**
     * 查询所有书籍信息成功
     */
    @Override
    public void queryAllBookSuccess(List<BookshelfNovelDbData> dataList) {
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
            for (int i = 0; i < mDataList.size(); i++) {
                mCheckedList.add(false);
            }
            mDataList.add(bookshelfNovelDbData);
            mCheckedList.add(false);
            mBookshelfNovelsAdapter.notifyDataSetChanged();
        }
    }

    private void initAdapter() {
        mDataList.add(bookshelfNovelDbData);
        mCheckedList.add(false);
        mBookshelfNovelsAdapter = new BookshelfNovelsAdapter(getActivity(), mDataList, mCheckedList,
                new BookshelfNovelsAdapter.BookshelfNovelListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void clickItem(int position) {
                        if(position==mDataList.size()-1){
                            ((MainActivity)getActivity()).checked();
                        }else {
                            if (!NetUtil.hasInternet(getActivity())) {
                                showShortToast("当前无网络，请检查网络后重试");
                                return;
                            }
                            if (mIsDeleting || mDataList.get(position).getType() == -1) {
                                return;
                            }
//                        if(mDataList.get(position).getType()==1){
//                            TxtConfig.saveIsOnVerticalPageMode(getContext(),false);
//                            HwTxtPlayActivity.loadTxtFile(getContext(), mDataList.get(position).getNovelUrl());
//                        }else {
                            Intent intent = new Intent(getActivity(), ReadActivity.class);
                            // 小说 url
                            intent.putExtra(ReadActivity.KEY_NOVEL_URL, mDataList.get(position).getNovelUrl());
                            // 小说名
                            intent.putExtra(ReadActivity.KEY_NAME, mDataList.get(position).getName());
                            // 小说封面 url
                            intent.putExtra(ReadActivity.KEY_COVER, mDataList.get(position).getCover());
                            // 小说类型
                            intent.putExtra(ReadActivity.KEY_TYPE, mDataList.get(position).getType());
                            // 开始阅读的位置
                            intent.putExtra(ReadActivity.KEY_CHAPTER_INDEX, mDataList.get(position).getChapterIndex());
                            intent.putExtra(ReadActivity.KEY_POSITION, mDataList.get(position).getPosition());
                            intent.putExtra(ReadActivity.KEY_SECOND_POSITION, mDataList.get(position).getSecondPosition());
                            startActivity(intent);
                        }
                       // }
                    }

                    @Override
                    public void longClick(int position) {
//                        if (mIsDeleting) {
//                            return;
//                        }
//                        mBookshelfNovelsAdapter.setIsMultiDelete(true);
//                        mBookshelfNovelsAdapter.notifyDataSetChanged();
//                        mMultiDeleteRv.setVisibility(View.VISIBLE);
                        Intent bookShelfintent=new Intent(getContext(),MyBookshelfActivity.class);
                        startActivity(bookShelfintent);
                    }
                });
    }

    /**
     * 查询所有书籍信息失败
     */
    @Override
    public void queryAllBookError(String errorMsg) {

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
        mDbManager.insertBookshelfNovel(dbData);
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
        LayoutInflater inflater=LayoutInflater.from(getContext());
        String [] strings;
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
            convertView=inflater.inflate(R.layout.grid_item,parent,false);
            ImageView imageView=convertView.findViewById(R.id.img);
            TextView textView=convertView.findViewById(R.id.text);
            imageView.setImageResource(integers[position]);
            textView.setText(strings[position]);
            return convertView;
        }
    }

}
