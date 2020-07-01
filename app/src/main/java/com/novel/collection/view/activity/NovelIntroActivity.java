package com.novel.collection.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bifan.txtreaderlib.interfaces.IParagraphData;
import com.bifan.txtreaderlib.main.ParagraphData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.novel.collection.R;
import com.novel.collection.adapter.CategoryinfoAdapter;
import com.novel.collection.adapter.CategoryzyAdapter;
import com.novel.collection.app.App;
import com.novel.collection.base.BaseActivity;
import com.novel.collection.base.BasePresenter;
import com.novel.collection.constant.Constant;
import com.novel.collection.constant.EventBusCode;
import com.novel.collection.constract.NovelInfoContract;
import com.novel.collection.db.DatabaseManager;
import com.novel.collection.entity.bean.Cataloginfo;
import com.novel.collection.entity.bean.Chapter;
import com.novel.collection.entity.bean.DownBean;
import com.novel.collection.entity.bean.Login_admin;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.bean.PersonBean;
import com.novel.collection.entity.data.BookshelfNovelDbData;
import com.novel.collection.entity.data.DiscoveryNovelData;
import com.novel.collection.entity.eventbus.Event;
import com.novel.collection.entity.eventbus.HoldReadActivityEvent;
import com.novel.collection.entity.eventbus.HoldReadActivityEvent2;
import com.novel.collection.entity.eventbus.NovelIntroInitEvent;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.presenter.NovelInfoPresenter;
import com.novel.collection.service.CacheService;
import com.novel.collection.util.BlurUtil;
import com.novel.collection.util.EventBusUtil;
import com.novel.collection.util.ImageLoaderUtils;
import com.novel.collection.util.LogUtils;
import com.novel.collection.util.SpUtil;
import com.novel.collection.util.StatusBarUtil;
import com.novel.collection.view.fragment.main.BookshelfFragment;
import com.novel.collection.weyue.db.entity.CollBookBean;
import com.novel.collection.widget.CornerTransform;
import com.novel.collection.widget.TipDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import cn.bmob.v3.util.V;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import q.rorbin.badgeview.QBadgeView;

import static com.novel.collection.app.App.getContext;
import static com.novel.collection.view.activity.LocalCatalogActivity.getCharset;

/**
 * @author Created on 2019/11/15
 */
public class NovelIntroActivity extends BaseActivity implements View.OnClickListener, NovelInfoContract.View {

    private static final String TAG = "NovelIntroActivity";
    private static final int NOVEL_INTRODUCE_MAX_LINES = 3; // 小说简介最多显示多少行

    private ImageView mBackIv;
    private LinearLayout mMenuIv;
    private ImageView mTopBgIv;
    private TextView mNovelNameTv;
    private TextView mNovelAuthorTv,txt_catalog_name;
    private TextView mNovelIntroduceTv;
    private TextView mMoreIntroduceIv;
    private LinearLayout mCatalogTv;
    private RecyclerView recycle_book;
    private NovelInfoPresenter presenter;
    private TextView tv_catalog, tv_close;
    private TextView tv_fonts;
    private TextView tv_status;
    private TextView tv_new_catalog;
    private RelativeLayout re_new_catalog;
    private TextView tv_time, tv_book_add;
    private TextView tv_begain_read;
    private LinearLayout l_all;
    String pid;
    RelativeLayout r_share;
    RelativeLayout title_rel;
    ProgressBar progressBar;
    Login_admin login_admin;
    TextView txt_book_load;
    ImageView img_collect, iv_tuijian;
    private RelativeLayout rel_book_add;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_ADDRESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] ==PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted 授予权限
                    //处理授权之后逻辑
                    if (!txt_book_load.getText().equals("已缓存")) {
                        txt_book_load.setText("缓存中：0%");
                        txt_book_load.setTextColor(getResources().getColor(R.color.yellow));
                        txt_book_load.setEnabled(false);
                        String load_id = App.getInstance().getPosition();
                        if (load_id != null && !load_id.equals(pid)) {
                            txt_book_load.setText("已加入缓存序列");
                            DownBean downBean = new DownBean(weigh, 1, noval_details.getTitle(), noval_details.getPic(), pid,noval_details.getSerialize());
                            myBinder.che(downBean);
                            Is_load = true;
                        } else if (load_id != null && load_id.equals(pid)) {
                            txt_book_load.setText("缓存中...");
                        } else {
                            DownBean downBean = new DownBean(weigh, 1, noval_details.getTitle(), noval_details.getPic(), pid,noval_details.getSerialize());
                            myBinder.che(downBean);
                        }
                    }
                } else {
                    // Permission Denied 权限被拒绝
                    showShortToast("权限被禁用");
                }

                break;
            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private final int REQUEST_CODE_ADDRESS = 100;

    private void checkPermissioin(){
        int checkCoarse = ContextCompat.checkSelfPermission(NovelIntroActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int checkCoarseFine = ContextCompat.checkSelfPermission(NovelIntroActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (checkCoarse == PackageManager.PERMISSION_GRANTED  && checkCoarseFine == PackageManager.PERMISSION_GRANTED) {
            //已经授权
            if (!txt_book_load.getText().equals("已缓存")) {
                txt_book_load.setText("缓存中：0%");
                txt_book_load.setTextColor(getResources().getColor(R.color.yellow));
                txt_book_load.setEnabled(false);
                String load_id = App.getInstance().getPosition();
                if (load_id != null && !load_id.equals(pid)) {
                    txt_book_load.setText("已加入缓存序列");
                    DownBean downBean = new DownBean(weigh, 1, noval_details.getTitle(), noval_details.getPic(), pid,noval_details.getSerialize());
                    myBinder.che(downBean);
                    Is_load = true;
                } else if (load_id != null && load_id.equals(pid)) {
                    txt_book_load.setText("缓存中...");
                } else {
                    DownBean downBean = new DownBean(weigh, 1, noval_details.getTitle(), noval_details.getPic(), pid,noval_details.getSerialize());
                    myBinder.che(downBean);
                }
            }
        } else {//没有权限
            ActivityCompat.requestPermissions(NovelIntroActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ADDRESS);//申请授权
        }
    }
    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_novel_intro_copy;
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
        login_admin = (Login_admin) SpUtil.readObject(this);
    }

    public void startActivity(Class<?> className, Bundle bundle) {
        Intent intent = new Intent(getContext(), className);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //ImageView iv_load;
    TextView tv_novel_intro_novel_name;
    boolean is_checked = false;

    @Override
    protected void initView() {
        title_rel=findViewById(R.id.title_rel);
        l_collect = findViewById(R.id.l_collect);
        recommend=findViewById(R.id.recommend);
        img_collect = findViewById(R.id.img_collect);
        iv_tuijian = findViewById(R.id.iv_tuijian);
        tv_novel_intro_novel_name = findViewById(R.id.tv_novel_intro_novel_name);
        l_all = findViewById(R.id.l_all);
        //relativeLayout = findViewById(R.id.bottom);
        r_share = findViewById(R.id.r_share);
        //constraintLayout = findViewById(R.id.all);
        progressBar = findViewById(R.id.pb_catalog);
        //iv_load = findViewById(R.id.iv_load);
        txt_catalog_name=findViewById(R.id.txt_catalog_name);
        tv_book_add = findViewById(R.id.tv_book_add);
        tv_book_add.setOnClickListener(this);
        tv_begain_read = findViewById(R.id.txt_read);
        tv_begain_read.setOnClickListener(this);
        txt_book_load = findViewById(R.id.txt_book_load);
        txt_book_load.setOnClickListener(this);
        tv_fonts = findViewById(R.id.novel_counts);
        mBackIv = findViewById(R.id.iv_novel_intro_back);
        mBackIv.setOnClickListener(this);
        mMenuIv = findViewById(R.id.l_share);
        mMenuIv.setOnClickListener(this);
        tv_catalog = findViewById(R.id.novel_catalog);

        tv_status = findViewById(R.id.tv_status);
        tv_time = findViewById(R.id.txt_time);
        tv_new_catalog = findViewById(R.id.novel_new_catalog);
        re_new_catalog = findViewById(R.id.r_catalog);
        re_new_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookshelfNovelDbData1 = mDbManager.selectBookshelfNovel(pid);
                if (bookshelfNovelDbData1!=null&&bookshelfNovelDbData1.getType()!=1) {
                    // 跳转到目录页面，并且将自己的引用传递给它
                    Intent intent = new Intent(NovelIntroActivity.this, CatalogActivity.class);
                    intent.putExtra(CatalogActivity.KEY_URL, pid);    // 传递当前小说的 url
                    intent.putExtra("ACTIVITY_TYPE", "NovelIntroActivity");
                    intent.putExtra(CatalogActivity.KEY_NAME, noval_details.getTitle());
                    intent.putExtra(CatalogActivity.KEY_COVER, noval_details.getPic());
                    intent.putExtra("weigh", weigh);
                    intent.putExtra(CatalogActivity.KEY_SERIALIZE, noval_details.getSerialize());
                    intent.putExtra(CatalogActivity.KEY_AUTHOR, noval_details.getAuthor());
                    startActivity(intent);
                } else if (bookshelfNovelDbData1!=null&&bookshelfNovelDbData1.getType()==1) {
                    Intent intent = new Intent(NovelIntroActivity.this, LocalCatalogActivity.class);
                    intent.putExtra("ACTIVITY_TYPE", "NovelIntroActivity");
                    intent.putExtra("file_path", bookshelfNovelDbData1.getFuben_id());    // 传递当前小说的 url
                    intent.putExtra(LocalCatalogActivity.KEY_ID, pid);    // 传递当前小说的 url
                    intent.putExtra(CatalogActivity.KEY_NAME, noval_details.getTitle());
                    intent.putExtra(CatalogActivity.KEY_COVER, noval_details.getPic());
                    intent.putExtra(LocalCatalogActivity.KEY_POSTION, 0);
                    intent.putExtra("chapter_id", "1");
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(NovelIntroActivity.this, CatalogActivity.class);
                    intent.putExtra(CatalogActivity.KEY_URL, pid);    // 传递当前小说的 url
                    intent.putExtra("ACTIVITY_TYPE", "NovelIntroActivity");
                    intent.putExtra(CatalogActivity.KEY_NAME, noval_details.getTitle());
                    intent.putExtra(CatalogActivity.KEY_COVER, noval_details.getPic());
                    intent.putExtra("weigh", weigh);
                    intent.putExtra(CatalogActivity.KEY_SERIALIZE, noval_details.getSerialize());
                    intent.putExtra(CatalogActivity.KEY_AUTHOR, noval_details.getAuthor());
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.rel_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NovelIntroActivity.this, RankingActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("new_or_hot", 3);
                intent.putExtra("category_id", noval_details.getCategory());
                startActivity(intent);
            }
        });
        mTopBgIv = findViewById(R.id.img_cover);
        //mNovelCoverIv = findViewById(R.id.iv_novel_intro_novel_cover);
        mNovelNameTv = findViewById(R.id.novel_name);
        mNovelAuthorTv = findViewById(R.id.novel_author);
        mNovelIntroduceTv = findViewById(R.id.tv_novel_intro_novel_introduce);
        mNovelIntroduceTv.setOnClickListener(this);
        mMoreIntroduceIv = findViewById(R.id.iv_novel_intro_more_introduce);
        mMoreIntroduceIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        mNovelIntroduceTv.post(new Runnable() {
            @Override
            public void run() {
                // 判断是否需要隐藏显示更多按钮
                if (mNovelIntroduceTv.getLayout() != null && mNovelIntroduceTv.getLayout().getLineCount() <= mNovelIntroduceTv.getMaxLines()) {
                    // 隐藏显示更多
                    mMoreIntroduceIv.setVisibility(View.GONE);
                }
            }
        });

        mCatalogTv = findViewById(R.id.l_catalog);
        mCatalogTv.setOnClickListener(this);
        //grid_share = findViewById(R.id.grid_share);
        recycle_book = findViewById(R.id.recycle_book);
        recycle_book.setLayoutManager(new GridLayoutManager(NovelIntroActivity.this, 4));
        //recycle_book.setAdapter(adapter);
        Intent servive_intent = new Intent(NovelIntroActivity.this, CacheService.class);
        bindService(servive_intent, conn, BIND_AUTO_CREATE);
        loadReceiver = new LoadReceiver();
        // 注册广播接受者
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.load.android");//要接收的广播
        registerReceiver(loadReceiver, intentFilter);//注册接收者
        l_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_collect();
            }
        });
        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_Recommend();
            }
        });
    }

    private void showPupowindpw(int parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popu_bottom, null);
        tv_close = view.findViewById(R.id.tv_close);
        final PopupWindow popupWindow = new PopupWindow(view, parent, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 使其聚集,可点击
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.dialog_animation);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        backgroundAlpha(0.5f);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.LEFT | Gravity.BOTTOM, 0, -location[1]);
        //popupWindow.showAsDropDown(parent, (int) (parent.getWidth() * 0.7), 35);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
    }

    boolean mIsShowSettingBar;

    /*
    显示设置栏
       */
    private void showSettingBar() {
        mIsShowSettingBar = true;
        Animation bottomAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_enter);
        r_share.startAnimation(bottomAnim);
        r_share.setVisibility(View.VISIBLE);
        backgroundAlpha(0.5f);
        r_share.setAlpha(1);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
        // constraintLayout.setAlpha(bgAlpha);
    }

    /**
     * 隐藏设置栏
     */
    private void hideSettingBar() {
        Animation bottomExitAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_exit);
        bottomExitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                r_share.setVisibility(View.GONE);
                mIsShowSettingBar = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        r_share.startAnimation(bottomExitAnim);
        backgroundAlpha(1f);
    }

    @Override
    protected void doAfterInit() {
        l_all.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        presenter.getNovels(pid);
        if (mDbManager.isExistInReadCoderNovel(pid)) {
            tv_begain_read.setText("继续阅读");
        } else {
            tv_begain_read.setText("开始阅读");
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
                presenter.getNovels(pid);
                break;
            default:
                break;
        }
    }

    private DatabaseManager mDbManager;

    @Override
    public void onClick(View v) {
        try {
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
                case R.id.l_catalog:
                    bookshelfNovelDbData1 = mDbManager.selectBookshelfNovel(pid);
                    if (bookshelfNovelDbData1!=null&&bookshelfNovelDbData1.getType()!=1) {
                        // 跳转到目录页面，并且将自己的引用传递给它
                        Intent intent = new Intent(NovelIntroActivity.this, CatalogActivity.class);
                        intent.putExtra(CatalogActivity.KEY_URL, pid);    // 传递当前小说的 url
                        intent.putExtra("ACTIVITY_TYPE", "NovelIntroActivity");
                        intent.putExtra(CatalogActivity.KEY_NAME, noval_details.getTitle());
                        intent.putExtra(CatalogActivity.KEY_COVER, noval_details.getPic());
                        intent.putExtra("weigh", weigh);
                        intent.putExtra(CatalogActivity.KEY_SERIALIZE, noval_details.getSerialize());
                        intent.putExtra(CatalogActivity.KEY_AUTHOR, noval_details.getAuthor());
                        startActivity(intent);
                    } else if (bookshelfNovelDbData1!=null&&bookshelfNovelDbData1.getType()==1) {
                        Intent intent = new Intent(NovelIntroActivity.this, LocalCatalogActivity.class);
                        intent.putExtra("file_path", bookshelfNovelDbData1.getFuben_id());    // 传递当前小说的 url
                        intent.putExtra("ACTIVITY_TYPE", "NovelIntroActivity");
                        intent.putExtra(LocalCatalogActivity.KEY_ID, pid);    // 传递当前小说的 url
                        intent.putExtra(CatalogActivity.KEY_NAME, noval_details.getTitle());
                        intent.putExtra(CatalogActivity.KEY_COVER, noval_details.getPic());
                        intent.putExtra(LocalCatalogActivity.KEY_POSTION, 0);
                        intent.putExtra("chapter_id", "1");
                        //intent.putExtras(bundle);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(NovelIntroActivity.this, CatalogActivity.class);
                        intent.putExtra(CatalogActivity.KEY_URL, pid);    // 传递当前小说的 url
                        intent.putExtra("ACTIVITY_TYPE", "NovelIntroActivity");
                        intent.putExtra(CatalogActivity.KEY_NAME, noval_details.getTitle());
                        intent.putExtra(CatalogActivity.KEY_COVER, noval_details.getPic());
                        intent.putExtra("weigh", weigh);
                        intent.putExtra(CatalogActivity.KEY_SERIALIZE, noval_details.getSerialize());
                        intent.putExtra(CatalogActivity.KEY_AUTHOR, noval_details.getAuthor());
                        startActivity(intent);
                    }
                    break;
                case R.id.tv_book_add:
                    BookshelfNovelDbData dbData=mDbManager.getBookshelfNovel(pid);
                     if (dbData!=null&&(dbData.getType()==0||dbData.getType()==1)) {
                            tv_book_add.setText("加入书架");
                            mDbManager.deleteBookshelfNovel(pid.trim());
                            if (login_admin != null) {
                                delectBookshelfadd(login_admin.getToken(), pid);
                            }else {
                                delectBookshelfadd("", pid);
                            }
                        } else {
                            File file=new File(path+".txt");
                            if(file.exists()){
                                dbData = new BookshelfNovelDbData(pid, noval_details.getTitle(),
                                        noval_details.getPic(), 0, 1, 0, 0 + "", weigh, noval_details.getSerialize() + "");
                                dbData.setFuben_id(path+".txt");
                            }
                            else {
                               dbData = new BookshelfNovelDbData(pid, noval_details.getTitle(),
                                        noval_details.getPic(), 0, -2, 0, 0 + "", weigh, noval_details.getSerialize() + "");
                            }
                            mDbManager.insertOrUpdateBook(dbData);
                            if (login_admin != null) {
                                Log.e("zzz", "onClick: "+111);
                                setBookshelfadd(login_admin.getToken(), pid);
                            }else {
                                Log.e("zzz", "onClick: "+222);
                                setBookshelfadd("", pid);
                            }
                        }
                    break;
                case R.id.txt_book_load:
                    checkPermissioin();
                    break;
                case R.id.txt_read:
                    bookshelfNovelDbData1 = mDbManager.selectBookshelfNovel(pid);
                    if (bookshelfNovelDbData1 != null && is_Cache == false) {
                        CollBookBean bookBean = new CollBookBean(pid, noval_details.getTitle(), noval_details.getAuthor(), "",
                                noval_details.getPic(), false, 0, 0,
                                "", "", weigh, "",
                                false, false);
                        List<Cataloginfo> cataloginfos = mDbManager.queryAllCataloginfo(pid);
                        Collections.reverse(cataloginfos);
                        bookBean.setCataloginfos(cataloginfos);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(WYReadActivity.EXTRA_COLL_BOOK, bookBean);
                        bundle.putBoolean(WYReadActivity.EXTRA_IS_COLLECTED, true);
                        bundle.putString(WYReadActivity.STATUS, noval_details.getSerialize() + "");
                        bundle.putString(WYReadActivity.CHPTER_ID, bookshelfNovelDbData1.getChapterid() + "");
                        bundle.putString(WYReadActivity.PAGE_ID, bookshelfNovelDbData1.getPosition() + "");
                        startActivity(WYReadActivity.class, bundle);
                    } else if (bookshelfNovelDbData1 != null && is_Cache == true) {
                        CollBookBean bookBean = new CollBookBean(bookshelfNovelDbData1.getFuben_id(), noval_details.getTitle(), noval_details.getAuthor(), "",
                                noval_details.getPic(), false, 0, 0,
                                "", "", weigh, "",
                                false, true);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(WYReadActivity.EXTRA_COLL_BOOK, bookBean);
                        bundle.putBoolean(WYReadActivity.EXTRA_IS_COLLECTED, true);
                        bundle.putString(WYReadActivity.LOAD_PATH, pid);
                        bundle.putString(WYReadActivity.CHPTER_ID, bookshelfNovelDbData1.getChapterid() + "");
                        bundle.putString(WYReadActivity.PAGE_ID, bookshelfNovelDbData1.getPosition() + "");
                        bundle.putString(WYReadActivity.STATUS, noval_details.getSerialize() + "");
                        startActivity(WYReadActivity.class, bundle);
                    }
                    else {
                        CollBookBean bookBean = new CollBookBean(pid, noval_details.getTitle(), noval_details.getAuthor(), "",
                                noval_details.getPic(), false, 0, 0,
                                "", "", weigh, "",
                                false, false);
                        List<Cataloginfo> cataloginfos = mDbManager.queryAllCataloginfo(pid);
                        Collections.reverse(cataloginfos);
                        bookBean.setCataloginfos(cataloginfos);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(WYReadActivity.EXTRA_COLL_BOOK, bookBean);
                        bundle.putBoolean(WYReadActivity.EXTRA_IS_COLLECTED, true);
                        bundle.putString(WYReadActivity.CHPTER_ID, 0 + "");
                        bundle.putString(WYReadActivity.STATUS, noval_details.getSerialize() + "");
                        startActivity(WYReadActivity.class, bundle);
                    }

                    break;
                case R.id.l_share:
                    WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    DisplayMetrics metric = new DisplayMetrics();
                    wm.getDefaultDisplay().getMetrics(metric);
                    int viewWidth = metric.widthPixels;
                    showPupowindpw(viewWidth);
                    break;
                case R.id.tv_novel_intro_catalog:
                    bookshelfNovelDbData1 = mDbManager.selectBookshelfNovel(pid);
                    if (bookshelfNovelDbData1!=null&&bookshelfNovelDbData1.getType()!=1) {
                        // 跳转到目录页面，并且将自己的引用传递给它
                        Intent intent = new Intent(NovelIntroActivity.this, CatalogActivity.class);
                        intent.putExtra(CatalogActivity.KEY_URL, pid);    // 传递当前小说的 url
                        intent.putExtra("ACTIVITY_TYPE", "NovelIntroActivity");
                        intent.putExtra(CatalogActivity.KEY_NAME, noval_details.getTitle());
                        intent.putExtra(CatalogActivity.KEY_COVER, noval_details.getPic());
                        intent.putExtra("weigh", weigh);
                        intent.putExtra(CatalogActivity.KEY_SERIALIZE, noval_details.getSerialize());
                        intent.putExtra(CatalogActivity.KEY_AUTHOR, noval_details.getAuthor());
                        startActivity(intent);
                    } else if (bookshelfNovelDbData1!=null&&bookshelfNovelDbData1.getType()==1) {
                        Intent intent = new Intent(NovelIntroActivity.this, LocalCatalogActivity.class);
                        intent.putExtra("file_path", path + ".txt");    // 传递当前小说的 url
                        intent.putExtra(LocalCatalogActivity.KEY_ID, pid);    // 传递当前小说的 url
                        intent.putExtra(CatalogActivity.KEY_NAME, noval_details.getTitle());
                        intent.putExtra(CatalogActivity.KEY_COVER, noval_details.getPic());
                        intent.putExtra(LocalCatalogActivity.KEY_POSTION, 0);
                        intent.putExtra("chapter_id", "0");
                        //intent.putExtras(bundle);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(NovelIntroActivity.this, CatalogActivity.class);
                        intent.putExtra(CatalogActivity.KEY_URL, pid);    // 传递当前小说的 url
                        intent.putExtra("ACTIVITY_TYPE", "NovelIntroActivity");
                        intent.putExtra(CatalogActivity.KEY_NAME, noval_details.getTitle());
                        intent.putExtra(CatalogActivity.KEY_COVER, noval_details.getPic());
                        intent.putExtra("weigh", weigh);
                        intent.putExtra(CatalogActivity.KEY_SERIALIZE, noval_details.getSerialize());
                        intent.putExtra(CatalogActivity.KEY_AUTHOR, noval_details.getAuthor());
                    }
                    break;
                default:
                    // hideSettingBar();
                    break;
            }
        }catch (Exception ex){
            finish();
        }
    }
    BookshelfNovelDbData bookshelfNovelDbData1;
    IParagraphData paragraphData = new ParagraphData();

    private Boolean ReadData(String filePath, IParagraphData paragraphData) {
        File file = new File(filePath);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), getCharset(filePath)));
            try {
                String data;
                int index = 0;
                int chapterIndex = 0;
                while ((data = bufferedReader.readLine()) != null) {
                    if (data.length() >= 0) {
                        Chapter chapter = compileChapter(data, paragraphData.getCharNum(), index);
                        paragraphData.addParagraph(data);
                        if (chapter != null) {
                            chapterIndex++;
                        }
                        index++;
                    }
                }
                return true;
            } catch (IOException e) {

            }
        } catch (FileNotFoundException e) {

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    List longs = new ArrayList<>();
    int leng = 0;
    private static final String ChapterPatternStr = "(^.{0,3}\\s*第)(.{1,9})[章节卷集部篇回](\\s*)";

    /**
     * @param data              文本数据
     * @param chapterStartIndex 开始字符在全文中的位置
     * @param ParagraphIndex    段落位置
     * @return 没有识别到章节数据返回null
     */
    private Chapter compileChapter(String data, int chapterStartIndex, int ParagraphIndex) {
        if (data.trim().startsWith("第") || data.contains("第")) {
            Pattern p = Pattern.compile(ChapterPatternStr);
            Matcher matcher = p.matcher(data);
            while (matcher.find()) {
                longs.add(chapterStartIndex + ParagraphIndex);
                String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                ; //表示一个或多个@
                Pattern pat = Pattern.compile(regEx);
                Matcher mat = pat.matcher(data);
                String s = mat.replaceAll("");
                int startIndex = 0;
                int endIndex = data.length();
                Chapter c = new Chapter(chapterStartIndex, 0, data, ParagraphIndex, ParagraphIndex, startIndex, endIndex);
                return c;
            }
        }
        return null;
    }

    String path;
    /**
     * 使用BufferedWriter进行文本内容的追加
     *
     * @param content
     */
    private void addTxtToFileBuffered(String content) {
        //在文本文本中追加内容
        BufferedWriter out = null;
        try {
            File saveFile = new File(path + ".txt");
            if (!saveFile.exists()) {
                File dir = new File(saveFile.getParent());
                dir.mkdirs();
                saveFile.createNewFile();
            }
            //FileOutputStream(file, true),第二个参数为true是追加内容，false是覆盖
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile, true), "gbk"));
            out.newLine();//换行
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    int j = 0;

    @Override
    protected void onResume() {
        super.onResume();
        String load_id=App.getInstance().getPosition();
        if(load_id!=null&&load_id.equals(pid)){
            txt_book_load.setTextColor(getResources().getColor(R.color.yellow));
            txt_book_load.setText("缓存中...");
        }
//        if (mDbManager.isExistInBookshelfNovel(pid)) {
//            BookshelfNovelDbData bookshelfNovelDbData=mDbManager.selectBookshelfNovel(pid);
//            Log.e("WWW1", "doAfterInit: "+bookshelfNovelDbData);
//            if(bookshelfNovelDbData.getType()>=0){
//                tv_book_add.setText("移除书架");
//            }else {
//                tv_book_add.setText("加入书架");
//            }
//        } else {
//            tv_book_add.setText("加入书架");
//        }
    }

    int z = 1;

//    void postBooks_che() {
//        String url = UrlObtainer.GetUrl() + "/api/index/Books_che";
//        RequestBody requestBody = new FormBody.Builder()
//                .add("id", pid)
//                .add("page", z + "")
//                .add("limit", 30 + "")
//                .build();
//        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
//            @Override
//            public void onResponse(String json) {   // 得到 json 数据
//                try {
//                    JSONObject jsonObject = new JSONObject(json);
//                    String code = jsonObject.getString("code");
//                    if (code.equals("1")) {
//                        JSONObject object = jsonObject.getJSONObject("data");
//                        JSONArray jsonArray = object.getJSONArray("data");
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            String title = jsonArray.getJSONObject(i).getString("title");
//                            String content = jsonArray.getJSONObject(i).getString("content");
//                            String load_title = title.replace("&nbsp", " ").replace("</br>", "\n");
//                            String load_content = content.replace("&nbsp", " ").replace("</br>", "\n");
//                            if (!load_title.contains("第")) {
//                                String s = Pattern.compile("[^0-9]").matcher(title).replaceAll("");
//                                String title2 = "";
//                                if (load_title.contains(s)) {
//                                    title2 = load_title.replace(s, "第" + s + "章 ");
//                                }
//                                addTxtToFileBuffered(title2 + "\n");
//                                addTxtToFileBuffered(load_content + "\n");
//                            } else {
//                                addTxtToFileBuffered(load_title + "\n");
//                                addTxtToFileBuffered(load_content + "\n");
//                            }
//                            Message message = new Message();
//                            message.what = 2;
//                            message.arg1 = i;
//                            handler.sendMessage(message);
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                showShortToast(errorMsg);
//            }
//        });
//    }

    void post_collect() {
        if(login_admin==null){
            showShortToast("还未登录，无法进行收藏");
            return;
        }
        String url = UrlObtainer.GetUrl() + "/api/user/user_clt";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", login_admin.getToken())
                .add("novel_id", pid)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        String msg = jsonObject.getString("msg");
                        showShortToast(msg);
                        presenter.getNovels(pid);
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

    void post_Recommend() {
        if(login_admin==null){
            showShortToast("还未登录，无法进行收藏");
            return;
        }
        String url = UrlObtainer.GetUrl() + "/api/user/user_up";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", login_admin.getToken())
                .add("novel_id", pid)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        String msg = jsonObject.getString("msg");
                        showShortToast(msg);
                        presenter.getNovels(pid);
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

//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 1) {
//                tv_book_add.setText("移除书架");
//                if (mDbManager.isExistInBookshelfNovel(pid) || mDbManager.isExistInBookshelfNovel(path + ".txt")) {
//                    BookshelfNovelDbData bookshelfNovelDbData = new BookshelfNovelDbData(path + ".txt", bookname, bookcover, 1, weigh, 1 + "");
//                    mDbManager.updataBookshelfNovel(bookshelfNovelDbData, pid);
//                } else {
//                    BookshelfNovelDbData bookshelfNovelDbData = new BookshelfNovelDbData(path + ".txt", bookname, bookcover, 1, weigh, 1 + "");
//                    bookshelfNovelDbData.setFuben_id(pid);
//                    bookshelfNovelDbData.setChapterid(1 + "");
//                    mDbManager.insertOrUpdateBook(bookshelfNovelDbData);
//                }
//                Intent intent_recever = new Intent("com.zhh.android");
//                intent_recever.putExtra("type",1);
//                sendBroadcast(intent_recever);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ReadData(path + ".txt", paragraphData);
//                    }
//                }).start();
//            } else if (msg.what == 2) {
//                int j = msg.arg1;
//                if (30 * z <= weigh && (j + 1) == 30) {
//                    z++;
//                    postBooks_che();
//                }
//                float pressent = (float) (((z - 1) * 30 + (j + 1))) / (weigh) * 100;
//                txt_book_load.setText("缓存中:" + (int) pressent + "%");
//                if (((z - 1) * 30 + (j + 1)) == weigh) {
//                    txt_book_load.setText("已缓存");
//                    tv_book_add.setText("移除书架");
//                    tv_begain_read.setText("继续阅读");
//                    handler.sendEmptyMessage(1);
//                }
//            }
//        }
//    };
    boolean is_Cache;

    public void setBookshelfadd(String token, String novel_id) {
        String url = UrlObtainer.GetUrl() + "/api/Userbook/add";
        RequestBody requestBody = new FormBody.Builder()
                //.add("token", token)
                .add("type", 2+"")
                .add("novel_id", novel_id)
                .add("chapter_id", 1+"")
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                Log.e("QQW", "onResponse: "+json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        String message = jsonObject.getString("msg");
                        showShortToast(message);
                        tv_book_add.setText("移除书架");
                        //mPresenter.(message);
                    } else {
                        //mPresenter.getReadRecordError("请求错误");
                        //getNovelsError("请求错误");
                    }
                    Intent intent_recever = new Intent("com.zhh.android");
                    intent_recever.putExtra("type", 1);
                    sendBroadcast(intent_recever);
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
               // getNovelsError("请求错误");
                //mPresenter.getReadRecordError(errorMsg);
            }
        });
    }

    public void delectBookshelfadd(String token, String novel_id) {
        if (token == null) {
            return;
        }
        String url = UrlObtainer.GetUrl() + "/api/Userbook/del";
        RequestBody requestBody = new FormBody.Builder()
//                .add("token", token)
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
                        //showShortToast(message);
                    } else {
                        //mPresenter.getReadRecordError("请求错误");
                        //showShortToast("请求错误");
                    }
                    Intent intent_recever = new Intent("com.zhh.android");
                    intent_recever.putExtra("type", 1);
                    sendBroadcast(intent_recever);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                //showShortToast("请求错误");
                //mPresenter.getReadRecordError(errorMsg);
            }
        });
    }

    CategoryzyAdapter adapter;
    private List<DiscoveryNovelData> mNovelDataList = new ArrayList<>();
    Noval_details noval_details;
    int weigh = 0;
    String bookname;
    String bookcover;
    private CacheService binder;
    CacheService.MyBinder myBinder;
    //定义服务链接对象
    final ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = ((CacheService.MyBinder) service).getService();
            myBinder= ((CacheService.MyBinder) service);
//            DownBean downBean=new DownBean(weigh,1,noval_details.getTitle(),noval_details.getPic(),pid);
//            myBinder.che(downBean);
            binder.setCallback(new CacheService.Callback() {
                @Override
                public void onDataChange(String data,String id) {
                    if(id.equals(pid)) {
                        if (is_load == false) {
                            txt_book_load = findViewById(R.id.txt_book_load);
                            txt_book_load.setText(data);
                        }
                        if (data.trim().equals("已缓存")) {
                            txt_book_load.setEnabled(false);
                            is_load = true;
                            tv_book_add.setText("移除书架");
                            is_Cache = true;
                            Is_load = false;
                        }
                    }
                }
            });
            //binder.postBooks_che();
        }
    };
    boolean is_load;
    LinearLayout l_collect,recommend;

    @Override
    public void getNovelsSuccess(Noval_details noval_details, List<Noval_details> novalDetails) {
       if(novalDetails.size()==0){
           title_rel.setVisibility(View.GONE);
       }
        txt_catalog_name.setText("热门"+noval_details.getCategory_name()+"小说");

        if(noval_details.getIs_che()==1){
                tv_book_add.setText("移除书架");
        }else {
                tv_book_add.setText("加入书架");
         }

        if(noval_details.getIs_sc()==1){
            img_collect.setImageResource(R.mipmap.icon_collection_yes);
        }else {
            img_collect.setImageResource(R.mipmap.icon_collection);
        }

        if (j <= 1) {
            l_all.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
        this.noval_details = noval_details;
        if (noval_details != null) {
            if (adapter == null) {
                adapter = new CategoryzyAdapter(this, novalDetails);
                adapter.setOnCategoryNovelListener(new CategoryzyAdapter.CategoryNovelListener() {
                    @Override
                    public void clickItem(int novelName) {
                        Intent intent = new Intent(NovelIntroActivity.this, NovelIntroActivity.class);
                        // 传递小说名，进入搜查页后直接显示该小说的搜查结果
                        intent.putExtra("pid", novalDetails.get(novelName).getId() + "");
                        startActivity(intent);
                        overridePendingTransition(R.anim.zoom_enter,
                            R.anim.zoom_exit);
                    }
                });
            } else {
                adapter.notifyDataSetChanged();
            }
            //BookshelfNovelDbData novelDbData = mDbManager.selectBookshelfNovel(noval_details.getId() + "");
            path = Constant.BOOK_ADRESS + "/"+pid+"/"+ noval_details.getTitle();
//            File file = new File(path + ".txt");
//            try {
//                if (file.exists() && novelDbData.getFuben_id().equals(path + ".txt")&&novelDbData.getType()>=0) {
//                    txt_book_load.setText("已缓存");
//                    tv_book_add.setText("移除书架");
//                    is_Cache = true;
//                    txt_book_load.setClickable(false);
//                }
//            }catch (Exception ex){
//
//            }
            recycle_book.setAdapter(adapter);
            mNovelIntroduceTv.setText(noval_details.getContent());
            if(noval_details.getContent()==null||noval_details.getContent().length()<=12){
                mMoreIntroduceIv.setVisibility(View.GONE);
                mNovelIntroduceTv.setEnabled(false);
            }else {
                mMoreIntroduceIv.setVisibility(View.VISIBLE);
                mNovelIntroduceTv.setEnabled(true);
            }
            mNovelNameTv.setText(noval_details.getTitle());
            tv_novel_intro_novel_name.setText(noval_details.getTitle());
            bookname = noval_details.getTitle();
            mNovelAuthorTv.setText("作者：" + noval_details.getAuthor());
            bookcover = noval_details.getPic();
            tv_catalog.setText("分类：" + noval_details.getCategory_name());
            if (noval_details.getWord() < 10000) {
                tv_fonts.setText("字数：" + noval_details.getWord() + "字");
            } else {
                tv_fonts.setText("字数：" + (noval_details.getWord() / 10000) + "万字");
            }
            if (noval_details.getSerialize() == 0) {
                tv_status.setText("连载中");
            } else {
                tv_status.setText("完本");
            }
//            String url;
//            if (noval_details.getPic().contains("http")) {
//                url = noval_details.getPic();
//            } else {
//                url = UrlObtainer.GetUrl()+"/"+ noval_details.getPic();
//            }
            //iv_tuijian
            new QBadgeView(this).bindTarget(img_collect).setBadgeGravity(Gravity.END | Gravity.TOP).setBadgeBackgroundColor(Color.parseColor("#FF0000")).setBadgeNumber(noval_details.getFavorites());
            new QBadgeView(this).bindTarget(iv_tuijian).setBadgeGravity(Gravity.END | Gravity.TOP).setBadgeBackgroundColor(Color.parseColor("#FF0000")).setBadgeNumber(noval_details.getPosition());
//            CornerTransform transformation = new CornerTransform(this, 10);
//            Glide.with(this)
//                    .load(url)
//                    .apply(new RequestOptions()
//                            .error(R.drawable.cover_error)
//                            .transform(transformation))
//                    .into(mTopBgIv);
            ImageLoaderUtils.display(this,mTopBgIv,noval_details.getPic(),R.drawable.cover_error,R.drawable.cover_error);
            if (noval_details.getChapter() != null) {
                Noval_details.Chapter chapter = noval_details.getChapter();
                tv_new_catalog.setText("最新：" + chapter.getTitle());
                try {
                    tv_time.setText("更新于： " + getTimeFormatText(ConverToDate(chapter.getUpdate_time())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                weigh = chapter.getWeigh();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        unregisterReceiver(loadReceiver);
    }

    public static Date ConverToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.parse(strDate);
    }

    boolean Is_load=false;
    @SuppressLint("WrongConstant")
    @Override
    public void getNovelsError(String errorMsg) {
        l_all.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, errorMsg, 2000).show();
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
    LoadReceiver loadReceiver;
    public class LoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String id=intent.getStringExtra("load_position");
            String pressgress=intent.getStringExtra("load_progresss");
            if(id.equals(pid)) {
                if (txt_book_load != null) {
                    txt_book_load.setText(pressgress);
                }
                if (pressgress.trim().equals("已缓存")) {
                    txt_book_load.setEnabled(false);
                    is_load = true;
                    tv_book_add.setText("移除书架");
                    is_Cache = true;
                    Is_load = false;
                }
            }
        }
    }
}
