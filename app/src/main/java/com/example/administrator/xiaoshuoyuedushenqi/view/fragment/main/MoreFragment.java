package com.example.administrator.xiaoshuoyuedushenqi.view.fragment.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.MainRecyleAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseFragment;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Version;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.util.FileUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.MarketTools;
import com.example.administrator.xiaoshuoyuedushenqi.util.NetUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.VersionUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.AdminSetActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.FeedbackActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.LoginActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.ReadrecoderActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.SuishizhuanxianjinActivity;
import com.example.administrator.xiaoshuoyuedushenqi.widget.ShareDialog;
import com.example.administrator.xiaoshuoyuedushenqi.widget.TipDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author
 * Created on 2020/2/20
 */
public class MoreFragment extends BaseFragment implements View.OnClickListener {
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
    String[] strings1={"随时赚现金","给个五星好评","意见反馈","分享给好友"};
    int[] ints1={R.mipmap.img_more1,R.mipmap.img_more2,R.mipmap.img_more3,R.mipmap.img_more4};
    String[] strings2={"夜间模式"};
    int[] ints2={R.mipmap.img_more5};
    String[] strings3={"阅读记录","设置"};
    int[] ints3={R.mipmap.img_more6,R.mipmap.img_more7};
    @Override
    protected void doInOnCreate() {
        StatusBarUtil.setTranslucentStatus(getActivity());
        mainRecyleAdapter1=new MainRecyleAdapter(getContext(),ints1,strings1);
        recyclerView1.setAdapter(mainRecyleAdapter1);

        mainRecyleAdapter1.setOnCatalogListener(new MainRecyleAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                if(login_admin==null){
                  showShortToast("你还未登录，请先登录");
                  return;
                }
                switch (position){
                    case 0:
                        getContext().startActivity(new Intent(getContext(), SuishizhuanxianjinActivity.class));
                        break;
                    case 1:
//                        MarketTools.getTools().startMarket(Context mContext);//打开应用市场，打开当前安装应用的手机应用市场
//                        MarketTools.getTools().startMarket(Context mContext,String appPackageName);//指定应用包名去打开当前安装应用的手机应用市场
//                        MarketTools.getTools().startMarket(Context mContext,String appMarketPackageName);//指定应用包名，指定应用商店包名去打开
                        MarketTools.getTools().startMarket(getContext());
                        break;
                    case 2:
                        getContext().startActivity(new Intent(getContext(), FeedbackActivity.class));
                        break;
                    case 3:
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
                        break;
                }
            }
        });
        mainRecyleAdapter2=new MainRecyleAdapter(getContext(),ints2,strings2);
        recyclerView2.setAdapter(mainRecyleAdapter2);

        mainRecyleAdapter2.setOnCatalogListener(new MainRecyleAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                if(login_admin==null){
                    showShortToast("你还未登录，请先登录");
                    return;
                }
                switch (position){
                    case 0:
                        if(isNight){
                            isNight=false;
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }else {
                            isNight=true;
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }
                        getActivity().recreate();
                        break;
                }
            }
        });
        mainRecyleAdapter3=new MainRecyleAdapter(getContext(),ints3,strings3);
        recyclerView3.setAdapter(mainRecyleAdapter3);

        mainRecyleAdapter3.setOnCatalogListener(new MainRecyleAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                if(login_admin==null){
                    showShortToast("你还未登录，请先登录");
                    return;
                }
                switch (position){
                    case 0:
                        getContext().startActivity(new Intent(getContext(), ReadrecoderActivity.class));
                        break;
                    case 1:
                        getContext().startActivity(new Intent(getContext(), AdminSetActivity.class));
                        break;
                }
            }
        });
    }
    boolean isNight;
    TextView tv_name,tv_content;
    RelativeLayout relativeLayout;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_more;
    }

    Login_admin login_admin;
    @Override
    protected void initData() {
        login_admin= (Login_admin) SpUtil.readObject(getContext());
        if(login_admin!=null) {
            postMessage();
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void initView() {
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

        recyclerView1= getActivity().findViewById(R.id.recycle_part1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL,false));

        recyclerView2= getActivity().findViewById(R.id.recycle_part2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL,false));

        recyclerView3= getActivity().findViewById(R.id.recycle_part3);
        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL,false));
        relativeLayout=getActivity().findViewById(R.id.rel_login);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });
    }

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
                final int currVersionCode = VersionUtil.getVersionCode(getActivity());
                BmobQuery<Version> bmobQuery = new BmobQuery<>();
                bmobQuery.getObject("A7ht0006", new QueryListener<Version>() {
                    @Override
                    public void done(final Version version, BmobException e) {
                        if (version != null) {
                            if (version.getVersionCode() > currVersionCode) {
                                new TipDialog.Builder(getActivity())
                                        .setContent("检测到有新版本，是否进行更新（注意：更新后书架数据将清除）")
                                        .setEnsure("是")
                                        .setCancel("不了")
                                        .setOnClickListener(new TipDialog.OnClickListener() {
                                            @Override
                                            public void clickEnsure() {
                                                try {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setData(Uri.parse(version.getAddr()));
                                                    startActivity(intent);
                                                } catch (NullPointerException e) {
                                                    showShortToast("抱歉，下载地址出错");
                                                }
                                            }

                                            @Override
                                            public void clickCancel() {

                                            }
                                        })
                                        .build()
                                        .show();
                            } else {
                                showShortToast("已经是最新版本");
                            }
                        } else {
                            showShortToast("已经是最新版本");
                        }
                    }
                });
                break;
            case R.id.v_more_clear:
                final TipDialog tipDialog = new TipDialog.Builder(getActivity())
                        .setContent("是否清除缓存")
                        .setCancel("否")
                        .setEnsure("是")
                        .setOnClickListener(new TipDialog.OnClickListener() {
                            @Override
                            public void clickEnsure() {
                                FileUtil.clearLocalCache();
                                mCacheSizeTv.setText(FileUtil.getLocalCacheSize());
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
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(login_admin!=null){
                relativeLayout.setClickable(false);
                iv_title=getActivity().findViewById(R.id.title_img);
                tv_name=getActivity().findViewById(R.id.tv_name);
                tv_content=getActivity().findViewById(R.id.tv_content);
                Glide.with(getActivity())
                        .load(UrlObtainer.GetUrl()+login_admin.getAvatar())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.admin)
                                .error(R.mipmap.admin))
                        .into(iv_title);
                tv_content.setVisibility(View.GONE);
                tv_name.setText(login_admin.getNickname());
            }
        }
    };
    void postMessage(){
        String url = UrlObtainer.GetUrl()+"api/user/index";
        RequestBody requestBody = new FormBody.Builder()
                .add("token", login_admin.getToken())
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                Log.e("asd", "onResponse: "+000);
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        login_admin= (Login_admin) SpUtil.readObject(getContext());
                    }else if(code.equals("100")){
                        login_admin=null;
                        SpUtil.saveObject(getContext(),null);
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
}