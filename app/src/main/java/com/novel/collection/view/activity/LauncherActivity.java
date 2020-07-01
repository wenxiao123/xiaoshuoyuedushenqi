package com.novel.collection.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.novel.collection.R;
import com.novel.collection.app.App;
import com.novel.collection.base.BaseActivity;
import com.novel.collection.base.BaseActivity_other;
import com.novel.collection.base.BasePresenter;
import com.novel.collection.db.DatabaseManager;
import com.novel.collection.entity.bean.DownBean;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.data.BookshelfNovelDbData;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.util.CarOnlyIdUtils;
import com.novel.collection.util.LogUtils;
import com.novel.collection.util.SpUtil;
import com.novel.collection.util.StatusBarUtil;
import com.novel.collection.util.ToastUtil;
import com.novel.collection.widget.CircleProgressbar;
import com.novel.collection.widget.CornerTransform;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferencesFactory;
import com.novel.collection.entity.bean.Website;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.novel.collection.app.App.getContext;
import static com.novel.collection.http.OkhttpUtil.stringToMD5;


public class LauncherActivity extends BaseActivity_other {
    public final int MSG_FINISH_LAUNCHERACTIVITY = 500;
    DatabaseManager databaseManager;
    private TextView mCountDownTextView;
    VideoView videoView;
    ImageView image;
    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH_LAUNCHERACTIVITY:
                    //跳转到MainActivity，并结束当前的LauncherActivity
                    Intent intent = new Intent(LauncherActivity.this, AdmActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                default:
                    break;
            }
        };
    };
    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_launcher;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        databaseManager=DatabaseManager.getInstance();
        websites_lrc=databaseManager.queryAllWebsite();
        if(websites_lrc.size()==0){
            websites_lrc.add(new Website("http://hsa.4jvr2g68.com:36213/",1));
            websites_lrc.add(new Website("http://ges.atkskjx4.com:36213/",0));
            websites_lrc.add(new Website("http://jos.6ajpz3qu.com:36213/",0));
        }
    }
    private MyCountDownTimer mCountDownTimer;
    @Override
    protected void initView() {
        mCountDownTextView = (TextView) findViewById(R.id.start_skip_count_down);
        circleProgressbar=findViewById(R.id.tv_red_skip);
        videoView=findViewById(R.id.activity_opening_videoview);
        image =findViewById(R.id.img);
    }

    @Override
    protected void doAfterInit() {
        String website=SpUtil.getWebsite();
        if(!website.trim().equals("")){
            UrlObtainer.setHref(website);
        }
        checkPermissioin();
//        checkNetIsReal();
    }

    CircleProgressbar circleProgressbar;
    public void getCategoryNovels() {
        Gson mGson=new Gson();
        String url = UrlObtainer.GetUrl()+"/"+"api/Rmlist/Rem_List";
        RequestBody requestBody = new FormBody.Builder()
                .add("type", "1")
                .add("limit", "3")
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONObject object=jsonObject.getJSONObject("data");
                        JSONArray jsonArray=object.getJSONArray("data");
                        List<Noval_details> novalDetailsList=new ArrayList<>();
                        for(int i=0;i<jsonArray.length();i++){
                            novalDetailsList.add(mGson.fromJson(jsonArray.getJSONObject(i).toString(),Noval_details.class));
                        }
                        getCategoryNovelsSuccess(novalDetailsList);
                    }else {
                        getCategoryNovelsError("请求错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                getCategoryNovelsError(errorMsg);
            }
        });
    }

    private void getCategoryNovelsError(String errorMsg) {
        showShortToast(errorMsg);
    }
    public void setBookshelfadd(String token, String novel_id) {
        String Diviceid = CarOnlyIdUtils.getOnlyID(App.getAppContext());
        String url = UrlObtainer.GetUrl() + "/api/Userbook/add";
        RequestBody requestBody = new FormBody.Builder()
//                .add("token", token)
                .add("type", 2+"")
                .add("novel_id", novel_id)
                .add("chapter_id", 1+"")
                .add("uuid",Diviceid)
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                Log.e("WWW", "onResponse: "+Diviceid+" "+json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        String message = jsonObject.getString("msg");
                        Log.e("WWW", "onResponse: "+message );
                        //showShortToast(message);
                        //mPresenter.(message);
                    } else {
                        //mPresenter.getReadRecordError("请求错误");
                        //getNovelsError("请求错误");
                    }
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
    private void getCategoryNovelsSuccess(List<Noval_details> novalDetailsList) {
        SpUtil.saveWebsite(UrlObtainer.GetUrl());
        for(int i=0;i<novalDetailsList.size();i++){
            Noval_details noval_details=novalDetailsList.get(i);
            BookshelfNovelDbData dbData = new BookshelfNovelDbData(noval_details.getId()+"", noval_details.getTitle(),
                    noval_details.getPic(), 0, 0, 0, 0 + "", 20, noval_details.getSerialize() + "");
            databaseManager.insertOrUpdateBook(dbData);
            setBookshelfadd("",noval_details.getId()+"");
        }
        if (true) {
            mCountDownTextView.setText("3s 跳过");
            //创建倒计时类
            mCountDownTimer = new MyCountDownTimer(3000, 1000);
            mCountDownTimer.start();
            //这是一个 Handler 里面的逻辑是从 Splash 界面跳转到 Main 界面，这里的逻辑每个公司基本上一致
            mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, 3000);
        } else {
            mCountDownTextView.setText("1s 跳过");
            mCountDownTimer = new MyCountDownTimer(1000, 1000);
            mCountDownTimer.start();
            mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, 1000);
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture
         *      表示以「 毫秒 」为单位倒计时的总数
         *      例如 millisInFuture = 1000 表示1秒
         *
         * @param countDownInterval
         *      表示 间隔 多少微秒 调用一次 onTick()
         *      例如: countDownInterval = 1000 ; 表示每 1000 毫秒调用一次 onTick()
         *
         */

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }


        public void onFinish() {
            mCountDownTextView.setText("0s 跳过");
        }

        public void onTick(long millisUntilFinished) {
            mCountDownTextView.setText( millisUntilFinished / 1000 + "s 跳过");
        }

    }

    @Override
    protected void onDestroy() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        super.onDestroy();

    }

    /**
     * 检查可用的网址
     */
    public void checkNetIsReal() {
        Gson mGson=new Gson();
        String url = UrlObtainer.GetUrl()+"/api/index/domain";
        RequestBody requestBody = new FormBody.Builder()
                .build();
        OkhttpUtil.getpostRequest(url,requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                LogUtils.e(url+" "+json);
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String code=jsonObject.getString("code");
                    if(code.equals("1")){
                        JSONArray object=jsonObject.getJSONArray("data");
                        websites.clear();
                        for(int i=0;i<object.length();i++){
                            websites.add(mGson.fromJson(object.getJSONObject(i).toString(),Website.class));
                        }
                        getWebsiteSuccess(websites);
                    }else {
                        ToastUtil.showToast(LauncherActivity.this,"请求错误");
                    }
                } catch (JSONException e) {
                    if(z<websites_lrc.size()) {
                        LogUtils.e(websites_lrc.get(z).getUrl());
                        UrlObtainer.setHref(websites_lrc.get(z).getUrl());
                        checkNetIsReal();
                        z++;
                    }else {
                        showShortToast("网络数据接口有误");
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                LogUtils.e(url);
                if(z<websites_lrc.size()) {
                    UrlObtainer.setHref(websites_lrc.get(z).getUrl());
                    checkNetIsReal();
                    z++;
                }else {
                    showShortToast("网络数据接口有误");
                    finish();
                }
            }
        });
    }
    int z=0;

    private void getWebsiteSuccess(List<Website> websites) {
        if(websites.size()>0) {
            databaseManager.deleteWebsite();
        }
        for(int i=0;i<websites.size();i++){
          databaseManager.insertWebsite(websites.get(i));
        }
//        int first= SpUtil.getIsfirst();
//        if(first==0) {
//            getCategoryNovels();
//        }else {
            if (true) {
                mCountDownTextView.setText("3s 跳过");
                //创建倒计时类
                mCountDownTimer = new MyCountDownTimer(3000, 1000);
                mCountDownTimer.start();
                //这是一个 Handler 里面的逻辑是从 Splash 界面跳转到 Main 界面，这里的逻辑每个公司基本上一致
                mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, 3000);
            } else {
                mCountDownTextView.setText("1s 跳过");
                mCountDownTimer = new MyCountDownTimer(1000, 1000);
                mCountDownTimer.start();
                mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, 1000);
            }
        //}
    }

    List<Website> websites=new ArrayList<>();
    List<Website> websites_lrc=new ArrayList<>();

    private void checkPermissioin(){
        int checkCoarse = ContextCompat.checkSelfPermission(LauncherActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int checkCoarseFine = ContextCompat.checkSelfPermission(LauncherActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (checkCoarse == PackageManager.PERMISSION_GRANTED  && checkCoarseFine == PackageManager.PERMISSION_GRANTED) {
            //已经授权
            checkNetIsReal();
        } else {//没有权限
            ActivityCompat.requestPermissions(LauncherActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ADDRESS);//申请授权
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_ADDRESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] ==PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted 授予权限
                    //处理授权之后逻辑
                    checkNetIsReal();
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
}
