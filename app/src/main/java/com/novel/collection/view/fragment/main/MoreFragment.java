package com.novel.collection.view.fragment.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.novel.collection.R;
import com.novel.collection.adapter.MainRecyleAdapter;
import com.novel.collection.app.App;
import com.novel.collection.base.BaseFragment;
import com.novel.collection.base.BasePresenter;
import com.novel.collection.constant.EventBusCode;
import com.novel.collection.entity.bean.Login_admin;
import com.novel.collection.entity.bean.Version;
import com.novel.collection.entity.eventbus.Event;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.util.FileUtil;
import com.novel.collection.util.GlideCacheUtil;
import com.novel.collection.util.ImageLoaderUtils;
import com.novel.collection.util.MarketTools;
import com.novel.collection.util.NetUtil;
import com.novel.collection.util.DayNightManager;
import com.novel.collection.util.SpUtil;
import com.novel.collection.util.StatusBarUtil;
import com.novel.collection.util.VersionUtil;
import com.novel.collection.view.activity.AdminSetActivity;
import com.novel.collection.view.activity.FeedbackActivity;
import com.novel.collection.view.activity.LoginActivity;
import com.novel.collection.view.activity.MainActivity;
import com.novel.collection.view.activity.ReadrecoderActivity;
import com.novel.collection.weyue.utils.ReadSettingManager;
import com.novel.collection.widget.ShareDialog;
import com.novel.collection.widget.TipDialog;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

//import cn.bmob.v3.BmobQuery;
//import cn.bmob.v3.exception.BmobException;
//import cn.bmob.v3.listener.QueryListener;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 我的模块
 */
public class MoreFragment extends BaseFragment implements View.OnClickListener ,DayNightManager.OnThemeChangeListener{
    private static final String TAG = "MoreFragment";

    private View mCheckUpdateV;
    private TextView mVersionTv;
    private View mClearV;
    private TextView mCacheSizeTv;
    private View mAboutV;
    MainRecyleAdapter mainRecyleAdapter1;
    MainRecyleAdapter mainRecyleAdapter2;
    MainRecyleAdapter mainRecyleAdapter3;
    ImageView iv_title;
    RecyclerView recyclerView1, recyclerView2, recyclerView3;
    String[] strings1 = {"随时赚现金",  "意见反馈", "分享给好友"};
    int[] ints1 = {R.mipmap.img_more1,  R.mipmap.img_more3, R.mipmap.img_more4};
    String[] strings2 = {"夜间模式"};
    int[] ints2 = {R.mipmap.img_more5};
    String[] strings3 = {"阅读记录", "设置"};
    int[] ints3 = {R.mipmap.img_more6, R.mipmap.img_more7};
    boolean isNight;
    LinearLayout l_login;
    @Override
    protected void doInOnCreate() {
        StatusBarUtil.setTranslucentStatus(getActivity());
        mainRecyleAdapter1 = new MainRecyleAdapter(getContext(), ints1, strings1);
        recyclerView1.setAdapter(mainRecyleAdapter1);
        isNight = ReadSettingManager.getInstance().isNightMode();
        mainRecyleAdapter1.setOnCatalogListener(new MainRecyleAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                if (login_admin == null) {
                    showShortToast("你还未登录，请先登录");
                    return;
                }
                switch (position) {
                    case 0:
                        showShortToast("暂未开放");
                        //getContext().startActivity(new Intent(getContext(), SuishizhuanxianjinActivity.class));
                        break;
//                    case 1:
////                        MarketTools.getTools().startMarket(Context mContext);//打开应用市场，打开当前安装应用的手机应用市场
////                        MarketTools.getTools().startMarket(Context mContext,String appPackageName);//指定应用包名去打开当前安装应用的手机应用市场
////                        MarketTools.getTools().startMarket(Context mContext,String appMarketPackageName);//指定应用包名，指定应用商店包名去打开
//                        MarketTools.getTools().startMarket(getContext());
//                        break;
                    case 1:
                        getContext().startActivity(new Intent(getContext(), FeedbackActivity.class));
                        break;
                    case 2:
                        if(!getActivity().isDestroyed()) {
                            final ShareDialog tipDialog = new ShareDialog.Builder(getActivity())
                                    .setContent("www.baidu.com")
                                    .setCancel("下次再说")
                                    .setEnsure("去分享")
                                    .setOnClickListener(new ShareDialog.OnClickListener() {
                                        @Override
                                        public void clickEnsure() {
                                            mCacheSizeTv.setText(FileUtil.getLocalCacheSize());
                                        }

                                        @Override
                                        public void clickCancel() {

                                        }
                                    })
                                    .build();
                            tipDialog.show();
                        }
                        break;
                }
            }
        });
        if (!isNight) {
            strings2[0] = "夜间模式";
        } else {
            strings2[0] = "白天模式";
        }
        mainRecyleAdapter2 = new MainRecyleAdapter(getContext(), ints2, strings2);
        recyclerView2.setAdapter(mainRecyleAdapter2);

        mainRecyleAdapter2.setOnCatalogListener(new MainRecyleAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                switch (position) {
                    case 0:
                        isNight = ReadSettingManager.getInstance().isNightMode();
                        Intent recever = new Intent("com.changebackground.android");
                        getActivity().sendBroadcast(recever);
                        ReadSettingManager.getInstance().setNightMode(!isNight);
                        isNight = ReadSettingManager.getInstance().isNightMode();
                        if (!isNight) {
                            strings2[0] = "夜间模式";
                        } else {
                            strings2[0] = "白天模式";
                        }
                        mainRecyleAdapter2.notifyDataSetChanged();
                        break;
                }
            }
        });
        mainRecyleAdapter3 = new MainRecyleAdapter(getContext(), ints3, strings3);
        recyclerView3.setAdapter(mainRecyleAdapter3);

        mainRecyleAdapter3.setOnCatalogListener(new MainRecyleAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                switch (position) {
                    case 0:
                        if (login_admin == null) {
                            showShortToast("你还未登录，请先登录");
                            return;
                        } else {
                            getContext().startActivity(new Intent(getContext(), ReadrecoderActivity.class));
                        }
                        break;
                    case 1:
                        getContext().startActivity(new Intent(getContext(), AdminSetActivity.class));
                        break;
                }
            }
        });
        receiver = new MyReceiver();
        // 注册广播接受者
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.name.android");//要接收的广播
        getActivity().registerReceiver(receiver, intentFilter);//注册接收者
    }
    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp =getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }
    TextView tv_name, tv_content,tv_load;
    RelativeLayout relativeLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_more;
    }

    Login_admin login_admin;

    @Override
    protected void initData() {
        login_admin = (Login_admin) SpUtil.readObject(getContext());
        // Log.e("WWW", "initData: "+login_admin.getToken());
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void initView() {
        l_login= getActivity().findViewById(R.id.l_login);
        tv_load= getActivity().findViewById(R.id.tv_load);
        mCheckUpdateV = getActivity().findViewById(R.id.v_more_check_update);
        mCheckUpdateV.setOnClickListener(this);
        mVersionTv = getActivity().findViewById(R.id.tv_more_version);
        mVersionTv.setText(VersionUtil.getVersionName(getActivity()));

        mClearV = getActivity().findViewById(R.id.v_more_clear);
        mClearV.setOnClickListener(this);
        mCacheSizeTv = getActivity().findViewById(R.id.tv_more_cache_size);
        mCacheSizeTv.setText(FileUtil.getLocalCacheSize());

        mAboutV = getActivity().findViewById(R.id.v_more_about);
        mAboutV.setOnClickListener(this);

        recyclerView1 = getActivity().findViewById(R.id.recycle_part1);
        recyclerView1.setLayoutManager(layoutManager);

        recyclerView2 = getActivity().findViewById(R.id.recycle_part2);
        recyclerView2.setLayoutManager(layoutManager1);

        recyclerView3 = getActivity().findViewById(R.id.recycle_part3);
        recyclerView3.setLayoutManager(layoutManager2);
        relativeLayout = getActivity().findViewById(R.id.rel_login);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });
        mCacheSizeTv.setText(GlideCacheUtil.getInstance().getCacheSize(getActivity()));
        if(login_admin==null){
            l_login.setVisibility(View.VISIBLE);
            tv_load.setVisibility(View.GONE);
        }
    }

    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()){
        @Override
        public boolean canScrollVertically() {
            return false;
        }
    };
    LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext()){
        @Override
        public boolean canScrollVertically() {
            return false;
        }
    };
    LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext()){
        @Override
        public boolean canScrollVertically() {
            return false;
        }
    };

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventCome(Event event) {
        switch (event.getCode()) {
            case EventBusCode.MORE_INTO:
                mCacheSizeTv.setText(FileUtil.getLocalCacheSize());
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.v_more_check_update:
                if (!NetUtil.hasInternet(getActivity())) {
                    showShortToast("当前无网络，请检查网络后重试");
                    break;
                }
                break;
            case R.id.v_more_clear:
                final TipDialog tipDialog = new TipDialog.Builder(getActivity())
                        .setContent("是否清除缓存")
                        .setCancel("否")
                        .setEnsure("是")
                        .setOnClickListener(new TipDialog.OnClickListener() {
                            @Override
                            public void clickEnsure() {
                                // FileUtil.clearLocalCache();
                                Glide.get(getActivity()).clearDiskCache();
                                Glide.get(getActivity()).clearMemory();
                            }

                            @Override
                            public void clickCancel() {

                            }
                        })
                        .build();
                tipDialog.show();
                break;
            case R.id.v_more_about:
                break;
            default:
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
            if (login_admin != null) {
                if(getActivity()!=null) {
                    //登陆后，展示基本数据
                    relativeLayout.setClickable(false);
                    iv_title = getActivity().findViewById(R.id.title_img);
                    tv_name = getActivity().findViewById(R.id.tv_name);
                    tv_content = getActivity().findViewById(R.id.tv_content);
//                    Glide.with(getActivity())
//                            .load(UrlObtainer.GetUrl() +"/"+ login_admin.getAvatar())
//                            .apply(new RequestOptions()
//                                    .placeholder(R.mipmap.admin)
//                                    .error(R.mipmap.admin))
//                            .into(iv_title);
                    ImageLoaderUtils.display(getActivity(),iv_title,login_admin.getAvatar(),R.mipmap.admin,R.mipmap.admin);
                    tv_content.setVisibility(View.GONE);
                    tv_name.setText(login_admin.getNickname());
                }
            }
        }else if(msg.what==2){
                //未登陆时，展示基本数据
                relativeLayout.setClickable(true);
                iv_title = getActivity().findViewById(R.id.title_img);
                tv_name = getActivity().findViewById(R.id.tv_name);
                tv_content = getActivity().findViewById(R.id.tv_content);
                tv_content.setVisibility(View.VISIBLE);
                Glide.with(getActivity())
                        .load(R.mipmap.admin)
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.admin)
                                .error(R.mipmap.admin))
                        .into(iv_title);
                tv_content.setText("登录后获得更好体验");
                tv_name.setText("登录/注册");
            }
            l_login.setVisibility(View.VISIBLE);
            tv_load.setVisibility(View.GONE);
        }
    };

    /**
     * 请求登陆后基本数据
     */

    void postMessage() {
        if(login_admin==null){
            handler.sendEmptyMessage(2);
           return;
        }
        Gson gson = new Gson();
        String url = UrlObtainer.GetUrl() + "/api/user/index";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", login_admin.getToken())
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                Log.e("asd", "onResponse: " + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        login_admin = gson.fromJson(jsonObject.getJSONObject("data").toString(), Login_admin.class);
                        SpUtil.saveObject(getContext(), login_admin);
                    } else if (code.equals("100")) {
                        login_admin = null;
                        SpUtil.saveObject(getContext(), null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onFailure(String errorMsg) {
                showShortToast(errorMsg);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (login_admin != null) {
            postMessage();
        }
    }

    @Override
    public void onThemeChanged() {

    }
    MyReceiver receiver;
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            login_admin = (Login_admin) SpUtil.readObject(context);
            postMessage();
        }
    }
}
