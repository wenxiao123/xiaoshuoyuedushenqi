package com.example.administrator.xiaoshuoyuedushenqi.view.fragment.main;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.adapter.MainRecyleAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseFragment;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constant.EventBusCode;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Version;
import com.example.administrator.xiaoshuoyuedushenqi.entity.eventbus.Event;
import com.example.administrator.xiaoshuoyuedushenqi.util.FileUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.NetUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.VersionUtil;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.AdminSetActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.FeedbackActivity;
import com.example.administrator.xiaoshuoyuedushenqi.widget.ShareDialog;
import com.example.administrator.xiaoshuoyuedushenqi.widget.TipDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * @author WX
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
    RecyclerView recyclerView1, recyclerView2, recyclerView3;
    String[] strings1={"随时赚现金","给个五星好评","随时赚现金","意见反馈","分享给好友"};
    int[] ints1={R.mipmap.bookmark,R.mipmap.bookmark,R.mipmap.bookmark,R.mipmap.bookmark,R.mipmap.bookmark};
    String[] strings2={"夜间模式"};
    int[] ints2={R.mipmap.bookmark};
    String[] strings3={"阅读记录","设置"};
    int[] ints3={R.mipmap.bookmark,R.mipmap.bookmark};
    @Override
    protected void doInOnCreate() {
        StatusBarUtil.setTranslucentStatus(getActivity());
        mainRecyleAdapter1=new MainRecyleAdapter(getContext(),ints1,strings1);
        recyclerView1.setAdapter(mainRecyleAdapter1);

        mainRecyleAdapter1.setOnCatalogListener(new MainRecyleAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        getContext().startActivity(new Intent(getContext(), FeedbackActivity.class));
                        break;
                    case 4:
                        final ShareDialog tipDialog = new ShareDialog.Builder(getActivity())
                                .setContent("是否清除缓存")
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
                switch (position){
                    case 0:
                        break;
                }
            }
        });
        mainRecyleAdapter3=new MainRecyleAdapter(getContext(),ints3,strings3);
        recyclerView3.setAdapter(mainRecyleAdapter3);

        mainRecyleAdapter3.setOnCatalogListener(new MainRecyleAdapter.CatalogListener() {
            @Override
            public void clickItem(int position) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        getContext().startActivity(new Intent(getContext(), AdminSetActivity.class));
                        break;
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_more;
    }

    @Override
    protected void initData() {

    }

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
}
