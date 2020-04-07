package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bifan.txtreaderlib.interfaces.IParagraphData;
import com.bifan.txtreaderlib.main.ParagraphData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.CategoryinfoAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.CategoryzyAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.app.App;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.constract.NovelInfoContract;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Chapter;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.NovelIntroInitEvent;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.presenter.NovelInfoPresenter;
import com.example.administrator.xiaoshuoyuedushenqi.service.CacheService;
import com.example.administrator.xiaoshuoyuedushenqi.util.BlurUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.fragment.main.BookshelfFragment;

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
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.example.administrator.xiaoshuoyuedushenqi.view.activity.LocalCatalogActivity.getCharset;

/**
 * @author Created on 2019/11/15
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
    //private GridView grid_share;
    private TextView tv_catalog, tv_close;
    private TextView tv_fonts;
    private TextView tv_status;
    private TextView tv_new_catalog;
    private TextView tv_time, tv_book_add;
    private TextView tv_begain_read;
    String pid;
    RelativeLayout relativeLayout, r_share;
    ConstraintLayout constraintLayout;
    ProgressBar progressBar;
    Login_admin login_admin;
    private RelativeLayout rel_book_load, rel_book_add;

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
        login_admin = (Login_admin) SpUtil.readObject(this);
    }

    ImageView iv_load;

    @Override
    protected void initView() {
        relativeLayout = findViewById(R.id.bottom);
        r_share = findViewById(R.id.r_share);
        constraintLayout = findViewById(R.id.all);
        progressBar = findViewById(R.id.pb_catalog);
        tv_che = findViewById(R.id.tv_che);
        iv_load = findViewById(R.id.iv_load);
        tv_book_add = findViewById(R.id.tv_book_add);
        tv_begain_read = findViewById(R.id.tv_begain_read);
        tv_begain_read.setOnClickListener(this);
        rel_book_load = findViewById(R.id.rel_book_load);
        rel_book_load.setOnClickListener(this);
        rel_book_add = findViewById(R.id.rel_book_add);
        rel_book_add.setOnClickListener(this);
        tv_fonts = findViewById(R.id.tv_fonts);
        mBackIv = findViewById(R.id.iv_novel_intro_back);
        mBackIv.setOnClickListener(this);
        mMenuIv = findViewById(R.id.iv_novel_intro_menu);
        mMenuIv.setOnClickListener(this);
        tv_catalog = findViewById(R.id.tv_catalog);
        tv_close = findViewById(R.id.tv_close);
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSettingBar();
            }
        });
        tv_status = findViewById(R.id.tv_status);
        tv_time = findViewById(R.id.tv_time);
        tv_new_catalog = findViewById(R.id.tv_new_catalog);
        tv_new_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(!mDbManager.isExistInBookshelfNovel(path+".txt")) {
//                    Intent read_intent = new Intent(NovelIntroActivity.this, ReadActivity.class);
//                    read_intent.putExtra(ReadActivity.KEY_NAME, noval_details.getTitle());
//                    read_intent.putExtra(ReadActivity.KEY_COVER, noval_details.getPic());
//                    read_intent.putExtra(ReadActivity.KEY_CHPATER_ID, weigh);
//                    read_intent.putExtra("weigh", weigh);
//                    read_intent.putExtra("first_read", 2);
//                    read_intent.putExtra(ReadActivity.KEY_SERIALIZE, noval_details.getSerialize());
//                    read_intent.putExtra(ReadActivity.KEY_AUTHOR, noval_details.getAuthor());
//                    read_intent.putExtra(ReadActivity.KEY_NOVEL_URL, noval_details.getId() + "");
//                    startActivity(read_intent);
//                }else {
//                    Intent intent = new Intent(NovelIntroActivity.this, ReadActivity.class);
//                    // 小说 url
//                    intent.putExtra(ReadActivity.KEY_NOVEL_URL, path+".txt");
//                    // 小说名
//                    intent.putExtra(ReadActivity.KEY_NAME, noval_details.getTitle());
//                    // 小说封面 url
//                    intent.putExtra(ReadActivity.KEY_COVER, noval_details.getPic());
//                    // 小说类型
//                    intent.putExtra(ReadActivity.KEY_TYPE, 1);
//                    intent.putExtra(ReadActivity.KEY_IS_CATALOG, 1);
//                    // 开始阅读的位置
//                    intent.putExtra(ReadActivity.Catalog_start_Position, (int) longs.get(longs.size() - 1));
//                    startActivity(intent);
//                }
                if (!mDbManager.isExistInBookshelfNovel(path + ".txt")) {
                    Intent intent = new Intent(NovelIntroActivity.this, CatalogActivity.class);
                    intent.putExtra(CatalogActivity.KEY_NAME, noval_details.getTitle());
                    intent.putExtra(CatalogActivity.KEY_COVER, noval_details.getPic());
                    intent.putExtra("weigh", weigh);
                    intent.putExtra(CatalogActivity.KEY_SERIALIZE, noval_details.getSerialize());
                    intent.putExtra(CatalogActivity.KEY_AUTHOR, noval_details.getAuthor());
                    intent.putExtra(CatalogActivity.KEY_URL, pid);
                    startActivity(intent);
                } else {
                    BookshelfNovelDbData bookshelfNovelDbData = mDbManager.selectBookshelfNovel(path + ".txt");
                    Intent intent = new Intent(NovelIntroActivity.this, LocalCatalogActivity.class);
                    intent.putExtra("file_path", path + ".txt");    // 传递当前小说的 url
                    intent.putExtra(LocalCatalogActivity.KEY_NAME, noval_details.getTitle());  // 传递当前小说的名字
                    intent.putExtra(LocalCatalogActivity.KEY_COVER, noval_details.getPic()); // 传递当前小说的封面
                    intent.putExtra(LocalCatalogActivity.KEY_POSTION, bookshelfNovelDbData.getPosition());
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.tv_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mDbManager.isExistInBookshelfNovel(path + ".txt")) {
                    Intent intent = new Intent(NovelIntroActivity.this, CatalogActivity.class);
                    intent.putExtra(CatalogActivity.KEY_NAME, noval_details.getTitle());
                    intent.putExtra(CatalogActivity.KEY_COVER, noval_details.getPic());
                    intent.putExtra("weigh", weigh);
                    intent.putExtra(CatalogActivity.KEY_SERIALIZE, noval_details.getSerialize());
                    intent.putExtra(CatalogActivity.KEY_AUTHOR, noval_details.getAuthor());
                    intent.putExtra(CatalogActivity.KEY_URL, pid);
                    startActivity(intent);
                } else {
                    BookshelfNovelDbData bookshelfNovelDbData = mDbManager.selectBookshelfNovel(path + ".txt");
                    Intent intent = new Intent(NovelIntroActivity.this, LocalCatalogActivity.class);
                    intent.putExtra("file_path", path + ".txt");    // 传递当前小说的 url
                    intent.putExtra(LocalCatalogActivity.KEY_NAME, noval_details.getTitle());  // 传递当前小说的名字
                    intent.putExtra(LocalCatalogActivity.KEY_COVER, noval_details.getPic()); // 传递当前小说的封面
                    intent.putExtra(LocalCatalogActivity.KEY_POSTION, bookshelfNovelDbData.getPosition());
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
        mTopBgIv = findViewById(R.id.iv_novel_intro_top_image_bg);
        mNovelCoverIv = findViewById(R.id.iv_novel_intro_novel_cover);
        mNovelNameTv = findViewById(R.id.tv_novel_intro_novel_name);
        mNovelAuthorTv = findViewById(R.id.tv_novel_intro_novel_author);
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

        mCatalogTv = findViewById(R.id.tv_novel_intro_catalog);
        mCatalogTv.setOnClickListener(this);
        //grid_share = findViewById(R.id.grid_share);
        recycle_book = findViewById(R.id.recycle_book);
        recycle_book.setLayoutManager(new GridLayoutManager(NovelIntroActivity.this, 4));
        //recycle_book.setAdapter(adapter);
    }

    private void showPupowindpw(View parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popu_bottom, null);
        tv_close =view.findViewById(R.id.tv_close);
        final PopupWindow popupWindow = new PopupWindow(view, (int) (parent.getWidth()), ViewGroup.LayoutParams.WRAP_CONTENT);
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
        constraintLayout.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        presenter.getNovels(pid);
        if (mDbManager.isExistInBookshelfNovel(pid + "")) {
            tv_begain_read.setText("继续阅读");
        } else {
            tv_begain_read.setText("开始阅读");
        }
        if (mDbManager.isExistInBookshelfNovel(pid + "")) {
            tv_book_add.setText("移出书架");
        } else {
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

    private TextView tv_che;
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
                if (mDbManager.isExistInBookshelfNovel(path + ".txt")) {
                    tv_book_add.setText("加入书架");
                    File file = new File(path + ".txt");
                    file.delete();
                    mDbManager.deleteBookshelfNovel(path + ".txt");
                } else {
                    if (mDbManager.isExistInBookshelfNovel(pid + "")) {
                        tv_book_add.setText("加入书架");
                        mDbManager.deleteBookshelfNovel(pid);
                        if (login_admin != null) {
                            delectBookshelfadd(login_admin.getToken(), pid);
                        }
                    } else {
                        tv_book_add.setText("移出书架");
                        //String novelUrl, String name, String cover, int position, int type, int secondPosition, String chapterid, int weight, String status
                        BookshelfNovelDbData dbData = new BookshelfNovelDbData(pid, noval_details.getTitle(),
                                noval_details.getPic(), 0, 0, 0, 1 + "", weigh, noval_details.getSerialize() + "");
                        mDbManager.insertBookshelfNovel(dbData);
                        if (login_admin != null) {
                            setBookshelfadd(login_admin.getToken(), pid);
                        }
                    }
                }
                Intent intent_recever = new Intent("com.zhh.android");
                sendBroadcast(intent_recever);
                break;
            case R.id.rel_book_load:
                Intent servive_intent = new Intent(NovelIntroActivity.this, CacheService.class);
                servive_intent.putExtra("bookname", noval_details.getTitle());
                servive_intent.putExtra("id", pid);
                servive_intent.putExtra("bookcover", noval_details.getPic());
                servive_intent.putExtra("weigh", weigh);
                bindService(servive_intent, conn, BIND_AUTO_CREATE);
                iv_load.setVisibility(View.GONE);
                tv_che.setText("正在缓存：0%");
                BookshelfNovelDbData bookshelf = new BookshelfNovelDbData(pid,noval_details.getTitle(),noval_details.getPic(),1,weigh,1+"");
                bookshelf.setFuben_id(path+".txt");
                bookshelf.setChapterid(1+"");
                mDbManager.insertOrUpdateBook(bookshelf);
                Intent recever = new Intent("com.zhh.android");
                sendBroadcast(recever);
                break;
            case R.id.tv_begain_read:
                BookshelfNovelDbData bookshelfNovelDbData1 = mDbManager.selectBookshelfNovel(pid);
                if (bookshelfNovelDbData1!=null&&is_Cache==false) {
                    Intent read_intent = new Intent(NovelIntroActivity.this, ReadActivity.class);
                    read_intent.putExtra(ReadActivity.KEY_NAME, noval_details.getTitle());
                    read_intent.putExtra(ReadActivity.KEY_COVER, noval_details.getPic());
                    read_intent.putExtra("first_read", 2);
                    read_intent.putExtra(ReadActivity.KEY_CHPATER_ID, Integer.parseInt(bookshelfNovelDbData1.getChapterid()));
                    read_intent.putExtra(ReadActivity.KEY_SERIALIZE, noval_details.getSerialize());
                    read_intent.putExtra(ReadActivity.KEY_TYPE, 0);
                    read_intent.putExtra(ReadActivity.KEY_AUTHOR, noval_details.getAuthor());
                    read_intent.putExtra("weigh", weigh);
                    read_intent.putExtra(ReadActivity.KEY_NOVEL_URL, pid);
                    startActivity(read_intent);
                } else if (bookshelfNovelDbData1!=null&&is_Cache==true) {
                    //BookshelfNovelDbData bookshelfNovelDbData = mDbManager.selectBookshelfNovel(pid);
                    Intent intent = new Intent(NovelIntroActivity.this, ReadActivity.class);
                    // 小说 url
                    intent.putExtra(ReadActivity.KEY_NOVEL_URL, pid);
                    intent.putExtra(ReadActivity.KEY_NOVEL_URL_FUBEN, bookshelfNovelDbData1.getFuben_id());
                    // 小说名
                    intent.putExtra(ReadActivity.KEY_NAME, noval_details.getTitle());
                    // 小说封面 url
                    intent.putExtra(ReadActivity.KEY_COVER, noval_details.getPic());
                    // 小说封面 url
                    intent.putExtra(ReadActivity.KEY_POSITION, bookshelfNovelDbData1.getPosition());
                    // 小说类型
                    intent.putExtra(ReadActivity.KEY_TYPE, 1);
                    if (bookshelfNovelDbData1 != null) {
                        // 开始阅读的位置
                        intent.putExtra(ReadActivity.KEY_POSITION, bookshelfNovelDbData1.getPosition());
                    } else {
                        intent.putExtra(ReadActivity.KEY_POSITION, 1);
                    }
                    startActivity(intent);
                } else {
                    Intent read_intent = new Intent(NovelIntroActivity.this, ReadActivity.class);
                    read_intent.putExtra(ReadActivity.KEY_NAME, noval_details.getTitle());
                    read_intent.putExtra(ReadActivity.KEY_COVER, noval_details.getPic());
                    read_intent.putExtra("first_read", 1);
                    read_intent.putExtra("weigh", weigh);
                    read_intent.putExtra(ReadActivity.KEY_SERIALIZE, noval_details.getSerialize());
                    read_intent.putExtra(ReadActivity.KEY_AUTHOR, noval_details.getAuthor());
                    read_intent.putExtra(ReadActivity.KEY_NOVEL_URL, pid);
                    startActivity(read_intent);
                }

                break;
            case R.id.iv_novel_intro_menu:
                showPupowindpw(constraintLayout);
                break;
            case R.id.tv_novel_intro_catalog:
                if (!mDbManager.isExistInBookshelfNovel(path + ".txt")) {
                    Intent intent = new Intent(NovelIntroActivity.this, CatalogActivity.class);
                    intent.putExtra(CatalogActivity.KEY_NAME, noval_details.getTitle());
                    intent.putExtra(CatalogActivity.KEY_COVER, noval_details.getPic());
                    intent.putExtra("weigh", weigh);
                    intent.putExtra(CatalogActivity.KEY_SERIALIZE, noval_details.getSerialize());
                    intent.putExtra(CatalogActivity.KEY_AUTHOR, noval_details.getAuthor());
                    intent.putExtra(CatalogActivity.KEY_URL, pid);
                    startActivity(intent);
                } else {
                    BookshelfNovelDbData bookshelfNovelDbData = mDbManager.selectBookshelfNovel(path + ".txt");
                    Intent intent = new Intent(NovelIntroActivity.this, LocalCatalogActivity.class);
                    intent.putExtra("file_path", path + ".txt");    // 传递当前小说的 url
                    intent.putExtra(LocalCatalogActivity.KEY_NAME, noval_details.getTitle());  // 传递当前小说的名字
                    intent.putExtra(LocalCatalogActivity.KEY_COVER, noval_details.getPic()); // 传递当前小说的封面
                    intent.putExtra(LocalCatalogActivity.KEY_POSTION, bookshelfNovelDbData.getPosition());
                    startActivity(intent);
                }
                break;
            default:
                // hideSettingBar();
                break;
        }
    }
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
                            //chapters.add(chapter);
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
//    /**
//     * * 保存文件
//     * * @param toSaveString
//     * * @param filePath
//     *
//     */
//    public  void saveFile(String toSaveString) {
//        try {
//            if (!saveFile.exists()) {
//                File dir = new File(saveFile.getParent());
//                dir.mkdirs();
//                saveFile.createNewFile();
//            }
//            FileOutputStream outStream = new FileOutputStream(saveFile);
//            outStream.write(toSaveString.getBytes());
//            outStream.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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
//        App app = (App) getApplication();
//        if (app.isNight() == true) {
//            app.setNight(false);
//            finish();
//            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//            AppCompatDelegate.setDefaultNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
//                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
//            Intent intent = new Intent(this, this.getClass());
//            intent.putExtra("pid", pid);
//            startActivity(intent);
//            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
//           // pid = getIntent().getStringExtra("pid");
//            //presenter.getCategoryNovels();
////            mDbManager = DatabaseManager.getInstance();
////            login_admin = (Login_admin) SpUtil.readObject(this);
//        } else
           // {
            j++;
            if (j != 1) {
                presenter.getNovels(pid);
            }
       // }

    }

    int z = 1;

    void postBooks_che() {
        String url = UrlObtainer.GetUrl() + "api/index/Books_che";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", pid)
                .add("page", z + "")
                .add("limit", 30 + "")
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = object.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String title = jsonArray.getJSONObject(i).getString("title");
                            String content = jsonArray.getJSONObject(i).getString("content");
                            String load_title = title.replace("&nbsp", " ").replace("</br>", "\n");
                            String load_content = content.replace("&nbsp", " ").replace("</br>", "\n");
                            if (!load_title.contains("第")) {
                                String s = Pattern.compile("[^0-9]").matcher(title).replaceAll("");
                                String title2 = "";
                                if (load_title.contains(s)) {
                                    title2 = load_title.replace(s, "第" + s + "章 ");
                                }
                                addTxtToFileBuffered(title2 + "\n");
                                addTxtToFileBuffered(load_content + "\n");
                            } else {
                                addTxtToFileBuffered(load_title + "\n");
                                addTxtToFileBuffered(load_content + "\n");
                            }
                            Message message = new Message();
                            message.what = 2;
                            message.arg1 = i;
                            handler.sendMessage(message);
                        }
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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                tv_book_add.setText("移除书架");
                if (mDbManager.isExistInBookshelfNovel(pid) || mDbManager.isExistInBookshelfNovel(path + ".txt")) {
                    BookshelfNovelDbData bookshelfNovelDbData = new BookshelfNovelDbData(path + ".txt", bookname, bookcover, 1, weigh, 1 + "");
                    mDbManager.updataBookshelfNovel(bookshelfNovelDbData, pid);
                } else {
                    BookshelfNovelDbData bookshelfNovelDbData = new BookshelfNovelDbData(path + ".txt", bookname, bookcover, 1, weigh, 1 + "");
                    bookshelfNovelDbData.setFuben_id(pid);
                    bookshelfNovelDbData.setChapterid(1 + "");
                    mDbManager.insertBookshelfNovel(bookshelfNovelDbData);
                }
                Intent intent_recever = new Intent("com.zhh.android");
                sendBroadcast(intent_recever);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ReadData(path + ".txt", paragraphData);
                    }
                }).start();
            } else if (msg.what == 2) {
                int j = msg.arg1;
                Log.e("AA2", "handleMessage: " + ((z - 1) * 30 + (j + 1)));
                if (30 * z <= weigh && (j + 1) == 30) {
                    z++;
                    postBooks_che();
                }
                float pressent = (float) (((z - 1) * 30 + (j + 1))) / (weigh) * 100;
                tv_che.setText("正在缓存:" + (int) pressent + "%");
                if (((z - 1) * 30 + (j + 1)) == weigh) {
                    tv_che.setText("已缓存");
                    tv_book_add.setText("移除书架");
                    tv_begain_read.setText("继续阅读");
                    handler.sendEmptyMessage(1);
                }
            }
        }
    };
    boolean is_Cache;

    public void setBookshelfadd(String token, String novel_id) {
        String url = UrlObtainer.GetUrl() + "api/Userbook/add";
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
                        getNovelsError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                getNovelsError("请求错误");
                //mPresenter.getReadRecordError(errorMsg);
            }
        });
    }

    public void delectBookshelfadd(String token, String novel_id) {
        String url = UrlObtainer.GetUrl() + "api/Userbook/index";
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
                        getNovelsError("删除成功");
                    } else {
                        //mPresenter.getReadRecordError("请求错误");
                        getNovelsError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                getNovelsError("请求错误");
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
    //定义服务链接对象
    final ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = ((CacheService.MyBinder) service).getService();
            //binder = ((CacheService.MyBinder)service).getService();
            binder.setCallback(new CacheService.Callback() {
                @Override
                public void onDataChange(String data) {
                    tv_che.setText(data);
                    if(data.equals("已缓存")){
                        tv_book_add.setText("移除书架");
                        binder.stopSelf();
                    }
                }
            });
            //binder.postBooks_che();
        }
    };

    @Override
    public void getNovelsSuccess(Noval_details noval_details, List<Noval_details> novalDetails) {
        constraintLayout.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        this.noval_details = noval_details;
        if (noval_details != null) {
//            categoryViewHolder.novelList.setLayoutManager(new GridLayoutManager(mContext, 4));
//            CategoryzyAdapter adapter = new CategoryzyAdapter(mContext,
//                    mNovelDataList.get(i).getData_list());
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
            BookshelfNovelDbData novelDbData=mDbManager.selectBookshelfNovel(noval_details.getId()+"");
           // Log.e("QQQ", "getNovelsSuccess: "+noval_details.getId()+" "+novelDbData);
            path = Constant.BOOK_ADRESS + "/" + noval_details.getTitle();
            File file = new File(path + ".txt");
            if (novelDbData!=null&&file.exists()&&novelDbData.getNovelUrl().equals(noval_details.getId()+"")) {
                tv_che.setText("已缓存");
                tv_book_add.setText("移除书架");
                is_Cache = true;
                rel_book_load.setClickable(false);
                if (mDbManager.isExistInBookshelfNovel(path + ".txt")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ReadData(path + ".txt", paragraphData);
                        }
                    }).start();
                }
            }
            recycle_book.setAdapter(adapter);
            mNovelIntroduceTv.setText(noval_details.getContent());
            mNovelNameTv.setText(noval_details.getTitle());
            bookname = noval_details.getTitle();
            mNovelAuthorTv.setText(noval_details.getAuthor());
            bookcover = noval_details.getPic();
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
            String url;
            if (noval_details.getPic().contains("http")) {
                url = noval_details.getPic();
            } else {
                url = UrlObtainer.GetUrl() + noval_details.getPic();
            }
            Glide.with(this)
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.cover_place_holder)
                            .error(R.drawable.cover_error))
                    .into(mNovelCoverIv);
            Glide.with(this)
                    .load(url)
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

    public static Date ConverToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.parse(strDate);
    }

    @Override
    public void getNovelsError(String errorMsg) {
        constraintLayout.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
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
