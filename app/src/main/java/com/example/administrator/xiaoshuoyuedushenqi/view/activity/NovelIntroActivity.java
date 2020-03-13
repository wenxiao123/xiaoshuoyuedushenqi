package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.CategoryNovelAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.CategoryinfoAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.constract.NovelInfoContract;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Chapter;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.ANNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.NovelSourceData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.NovelIntroInitEvent;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.NovelInfoPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.util.BlurUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author WX
 * Created on 2019/11/15
 */
public class NovelIntroActivity extends BaseActivity implements View.OnClickListener, NovelInfoContract.View {

    private static final String TAG = "NovelIntroActivity";
    private static final int NOVEL_INTRODUCE_MAX_LINES = 3; // 小说简介最多显示多少行

    private ImageView mBackIv;
    private ImageView mMenuIv;
    private ImageView mTopBgIv;
    private ImageView mNovelCoverIv;
    private TextView mNovelNameTv;
    private TextView mNovelAuthorTv;
    private TextView mNovelIntroduceTv;
    private ImageView mMoreIntroduceIv;
    private TextView mCatalogTv;
    private RecyclerView recycle_book;
    private NovelInfoPresenter presenter;
    private GridView grid_share;
    private TextView tv_catalog;
    private TextView tv_fonts;
    private TextView tv_status;
    private TextView tv_new_catalog;
    private TextView tv_time,tv_book_add;
    private TextView tv_begain_read;
    String pid;
    private RelativeLayout rel_book_load,rel_book_add;
    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_novel_intro;
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter = new NovelInfoPresenter();
    }

    @Override
    protected void initData() {
        pid = getIntent().getStringExtra("pid");
        //presenter.getCategoryNovels();
        mDbManager = DatabaseManager.getInstance();
    }

    @Override
    protected void initView() {
        tv_book_add=findViewById(R.id.tv_book_add);
        tv_begain_read=findViewById(R.id.tv_begain_read);
        tv_begain_read.setOnClickListener(this);
        rel_book_load = findViewById(R.id.rel_book_load);
        rel_book_load.setOnClickListener(this);
        rel_book_add= findViewById(R.id.rel_book_add);
        rel_book_add.setOnClickListener(this);
        tv_fonts = findViewById(R.id.tv_fonts);
        mBackIv = findViewById(R.id.iv_novel_intro_back);
        mBackIv.setOnClickListener(this);
        mMenuIv = findViewById(R.id.iv_novel_intro_menu);
        mMenuIv.setOnClickListener(this);
        tv_catalog = findViewById(R.id.tv_catalog);
        tv_status = findViewById(R.id.tv_status);
        tv_time = findViewById(R.id.tv_time);
        tv_new_catalog = findViewById(R.id.tv_new_catalog);
        tv_new_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent read_intent = new Intent(NovelIntroActivity.this, ReadActivity.class);
                read_intent.putExtra(ReadActivity.KEY_NAME,noval_details.getTitle());
                read_intent.putExtra(ReadActivity.KEY_COVER,noval_details.getPic());
                read_intent.putExtra(ReadActivity.KEY_CHPATER_ID, weigh);
                read_intent.putExtra("weigh",weigh);
                read_intent.putExtra("first_read",2);
                read_intent.putExtra(ReadActivity.KEY_NOVEL_URL,noval_details.getId()+"");
                startActivity(read_intent);
            }
        });
        mTopBgIv = findViewById(R.id.iv_novel_intro_top_image_bg);
        mNovelCoverIv = findViewById(R.id.iv_novel_intro_novel_cover);
        mNovelNameTv = findViewById(R.id.tv_novel_intro_novel_name);
        mNovelAuthorTv = findViewById(R.id.tv_novel_intro_novel_author);
        mNovelIntroduceTv = findViewById(R.id.tv_novel_intro_novel_introduce);
        mNovelIntroduceTv.setOnClickListener(this);
        mMoreIntroduceIv = findViewById(R.id.iv_novel_intro_more_introduce);
        mNovelIntroduceTv.post(new Runnable() {
            @Override
            public void run() {
                // 判断是否需要隐藏显示更多按钮
                if (mNovelIntroduceTv.getLayout().getLineCount() <= mNovelIntroduceTv.getMaxLines()) {
                    // 隐藏显示更多
                    mMoreIntroduceIv.setVisibility(View.GONE);
                }
            }
        });

        mCatalogTv = findViewById(R.id.tv_novel_intro_catalog);
        mCatalogTv.setOnClickListener(this);
        grid_share = findViewById(R.id.grid_share);
        recycle_book = findViewById(R.id.recycle_book);
        recycle_book.setLayoutManager(new GridLayoutManager(NovelIntroActivity.this, 3));
        //recycle_book.setAdapter(adapter);
    }

    @Override
    protected void doAfterInit() {
        presenter.getNovels(pid);
        if(mDbManager.isExistInBookshelfNovel(pid + "")){
            tv_begain_read.setText("继续阅读");
        }else {
            tv_begain_read.setText("开始阅读");
        }
        if(mDbManager.isExistInBookshelfNovel(pid + "")){
            tv_book_add.setText("移出书架");
        }else {
            tv_book_add.setText("加入书架");
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyEventCome(Event event) {
        switch (event.getCode()) {
            case EventBusCode.NOVEL_INTRO_INIT:
                if (event.getData() instanceof NovelIntroInitEvent) {
                    NovelIntroInitEvent novelIntroInitEvent = (NovelIntroInitEvent) event.getData();
                }
                break;
            default:
                break;
        }
    }
    private DatabaseManager mDbManager;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_novel_intro_novel_introduce:
                // 是否显示小说简介的全部内容
                if (mMoreIntroduceIv.getVisibility() != View.VISIBLE &&
                        mNovelIntroduceTv.getMaxLines() != Integer.MAX_VALUE) {
                    return;
                }
                if (mNovelIntroduceTv.getMaxLines() != Integer.MAX_VALUE) {
                    mMoreIntroduceIv.setVisibility(View.GONE);
                    mNovelIntroduceTv.setMaxLines(Integer.MAX_VALUE);
                } else {
                    mNovelIntroduceTv.setMaxLines(NOVEL_INTRODUCE_MAX_LINES);
                    mMoreIntroduceIv.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_novel_intro_back:
                finish();
                break;
            case R.id.rel_book_add:
                if(mDbManager.isExistInBookshelfNovel(pid + "")){
                    tv_book_add.setText("加入书架");
                    mDbManager.deleteBookshelfNovel(pid);
                }else {
                    tv_book_add.setText("移出书架");
                    BookshelfNovelDbData dbData = new BookshelfNovelDbData(pid, noval_details.getTitle(),
                            noval_details.getPic(), 0, 1, 0);
                    mDbManager.insertBookshelfNovel(dbData);
                }
                Intent intent_recever = new Intent("com.zhh.android");
                sendBroadcast(intent_recever);
                break;
            case R.id.rel_book_load:

                break;
            case R.id.tv_begain_read:
                if(mDbManager.isExistInBookshelfNovel(pid + "")){
                    Intent read_intent = new Intent(NovelIntroActivity.this, ReadActivity.class);
                    read_intent.putExtra(ReadActivity.KEY_NAME,noval_details.getTitle());
                    read_intent.putExtra(ReadActivity.KEY_COVER,noval_details.getPic());
                    read_intent.putExtra("first_read",2);
                    read_intent.putExtra(ReadActivity.KEY_CHPATER_ID, weigh);
                    read_intent.putExtra("weigh",weigh);
                    read_intent.putExtra(ReadActivity.KEY_NOVEL_URL,pid);
                    startActivity(read_intent);
                }else {
                    Intent read_intent = new Intent(NovelIntroActivity.this, ReadActivity.class);
                    read_intent.putExtra(ReadActivity.KEY_NAME,noval_details.getTitle());
                    read_intent.putExtra(ReadActivity.KEY_COVER,noval_details.getPic());
                    read_intent.putExtra("first_read",1);
                    read_intent.putExtra("weigh",weigh);
                    read_intent.putExtra(ReadActivity.KEY_NOVEL_URL,pid);
                    startActivity(read_intent);
                }

                break;
            case R.id.iv_novel_intro_menu:
                PopupMenu popupMenu = new PopupMenu(NovelIntroActivity.this, mMenuIv);
                popupMenu.getMenuInflater().inflate(R.menu.menu_novel_intro, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_novel_intro_show_in_browser:
                                // 在浏览器显示

                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                break;
            case R.id.tv_novel_intro_catalog:
                Intent intent = new Intent(NovelIntroActivity.this, CatalogActivity.class);
                intent.putExtra(CatalogActivity.KEY_NAME,noval_details.getTitle());
                intent.putExtra(CatalogActivity.KEY_COVER,noval_details.getPic());
                intent.putExtra("weigh",weigh);
                intent.putExtra(CatalogActivity.KEY_URL,pid);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    CategoryinfoAdapter adapter;
    private List<DiscoveryNovelData> mNovelDataList = new ArrayList<>();
    Noval_details noval_details;
    int weigh=0;
    @Override
    public void getNovelsSuccess(Noval_details noval_details,List<Noval_details> novalDetails) {
        this.noval_details = noval_details;
        if (noval_details != null) {

            adapter = new CategoryinfoAdapter(this, novalDetails);
            adapter.setOnCategoryNovelListener(new CategoryinfoAdapter.CategoryNovelListener() {
                @Override
                public void clickItem(int novelName) {
                    // mListener.clickNovel(novelName);
                    //jump2Search(novelName);
                }
            });
            recycle_book.setAdapter(adapter);
            mNovelIntroduceTv.setText(noval_details.getContent());
            mNovelNameTv.setText(noval_details.getTitle());
            mNovelAuthorTv.setText(noval_details.getAuthor());
            tv_catalog.setText(noval_details.getCategory_name());
            if (noval_details.getWord() < 10000) {
                tv_fonts.setText(noval_details.getWord() + "字");
            } else {
                tv_fonts.setText((noval_details.getWord() / 10000) + "万字");
            }
            if (noval_details.getSerialize() == 0) {
                tv_status.setText("连载中");
            } else {
                tv_status.setText("完本");
            }
            Glide.with(this)
                    .load(UrlObtainer.GetUrl() + noval_details.getPic())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.cover_place_holder)
                            .error(R.drawable.cover_error))
                    .into(mNovelCoverIv);
            Glide.with(this)
                    .load(UrlObtainer.GetUrl() + noval_details.getPic())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.cover_place_holder)
                            .error(R.drawable.cover_error)
                            .transform(new BitmapTransformation() {
                                @Override
                                protected Bitmap transform(@NonNull BitmapPool pool,
                                                           @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                                    // 对得到的 Bitmap 进行虚化处理
                                    return BlurUtil.blurBitmap(NovelIntroActivity.this,
                                            toTransform, 5, 8);
                                }

                                @Override
                                public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

                                }
                            }))
                    .into(mTopBgIv);

            if (noval_details.getChapter() != null) {
                Noval_details.Chapter chapter = noval_details.getChapter();
                tv_new_catalog.setText(chapter.getTitle());
                try {
                    tv_time.setText(getTimeFormatText(ConverToDate(chapter.getUpdate_time())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                weigh=chapter.getWeigh();
            }
        } else {
            showShortToast("获取信息失败");
        }
    }

    /**
     * 时间差
     *
     * @param date
     * @return
     */
    public static String getTimeFormatText(Date date) {
        long minute = 60 * 1000;// 1分钟
        long hour = 60 * minute;// 1小时
        long day = 24 * hour;// 1天
        long month = 31 * day;// 月
        long year = 12 * month;// 年

        if (date == null) {
            return null;
        }
        long diff = new Date().getTime() - date.getTime();
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }

    public static Date ConverToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.parse(strDate);
    }

    @Override
    public void getNovelsError(String errorMsg) {
        showShortToast(errorMsg);
    }

    @Override
    public void getCategoryNovelsSuccess(List<DiscoveryNovelData> dataList) {
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        mNovelDataList.clear();
        mNovelDataList.addAll(dataList);

    }

    @Override
    public void getCategoryNovelsError(String errorMsg) {

    }
}
