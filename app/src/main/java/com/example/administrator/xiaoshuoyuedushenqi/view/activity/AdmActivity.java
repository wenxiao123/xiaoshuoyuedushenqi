package com.example.administrator.xiaoshuoyuedushenqi.view.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.arialyy.aria.core.Aria;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseActivity;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.db.DatabaseManager;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Url_gg;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Website;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpCall;
import com.example.administrator.xiaoshuoyuedushenqi.http.OkhttpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.util.AnyRunnModule;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.StatusBarUtil;
import com.example.administrator.xiaoshuoyuedushenqi.util.ToastUtil;
import com.example.administrator.xiaoshuoyuedushenqi.widget.CircleProgressbar;
import com.example.administrator.xiaoshuoyuedushenqi.widget.CornerTransform;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.example.administrator.xiaoshuoyuedushenqi.app.App.getContext;


public class AdmActivity extends BaseActivity implements View.OnClickListener {
    public final int MSG_FINISH_LAUNCHERACTIVITY = 500;
    DatabaseManager databaseManager;
    //private TextView mCountDownTextView;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH_LAUNCHERACTIVITY:
                    //跳转到MainActivity，并结束当前的LauncherActivity
                    Intent intent = new Intent(AdmActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                default:
                    break;
            }
        }

        ;
    };
    CircleProgressbar circleProgressbar;

    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setTranslucentStatus(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_adm;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        databaseManager = DatabaseManager.getInstance();
    }

    private MyCountDownTimer mCountDownTimer;

    @Override
    protected void initView() {
        circleProgressbar = findViewById(R.id.tv_red_skip);
        videoView = findViewById(R.id.activity_opening_videoview);
        image = findViewById(R.id.img);
        guodu=findViewById(R.id.guodu);
        anyRunnModule = new AnyRunnModule(this);
        //mCountDownTextView = (TextView) findViewById(R.id.start_skip_count_down);
    }

    @Override
    protected void doAfterInit() {
        circleProgressbar.setOutLineColor(getResources().getColor(R.color.grey_a1));
        circleProgressbar.setProgressColor(getResources().getColor(R.color.red_aa));
        boolean is_wifi = isWifiActive(this);
        if (is_wifi == true) {
            getCategoryNovels();
        } else {
            File file=new File(path);
            File[] files=file.listFiles();
            if (files.length>0) {
                Url_gg gg= (Url_gg) SpUtil.readObject2(this);
                showAdm(gg.getTime(),files[0].getAbsolutePath(),gg.getUrl(),true);
            }else {
                Intent intent = new Intent(AdmActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        circleProgressbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdmActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    AnyRunnModule anyRunnModule;

    public void getCategoryNovels() {
        String url = UrlObtainer.GetUrl() + "api/index/getqadm";
        RequestBody requestBody = new FormBody.Builder()
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
               // Log.e("QQQ", "onResponse: " + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (code.equals("1")) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        String img = object.getString("value");
                        String href = object.getString("url");
                        String time = object.getString("time");
                        //String type=img.substring(img.length()-1,img.length()-4);
                        if (img.contains(".png") || img.contains(".jpg") || img.contains(".jpeg")) {
                            String https;
                            if (img.contains("http")) {
                                https = img;
                            } else {
                                https = UrlObtainer.GetUrl() + img;
                            }
                            showAdm(time, https, href, false);
                        } else if (img.contains(".mp4")) {
                            String https;
                            if (img.contains("http")) {
                                https = img;
                            } else {
                                https = UrlObtainer.GetUrl() + img;
                            }
                            Url_gg gg=new Url_gg(href,time,https);
                            //Log.e("QQQ", "onResponse: "+gg);
                            SpUtil.saveObject2(AdmActivity.this,gg);
                            load_video(https);
                            showAdm(time, https, href, true);
                        }
                    }
//                    if (true) {
//                        // mCountDownTextView.setText("5s 跳过");
//                        //创建倒计时类
//                        mCountDownTimer = new MyCountDownTimer(15000, 1000);
//                        mCountDownTimer.start();
//                        //这是一个 Handler 里面的逻辑是从 Splash 界面跳转到 Main 界面，这里的逻辑每个公司基本上一致
//                        mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, 15000);
//                    } else {
//                        // mCountDownTextView.setText("1s 跳过");
//                        mCountDownTimer = new MyCountDownTimer(1000, 1000);
//                        mCountDownTimer.start();
//                        mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, 1000);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, 15000);
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                getCategoryNovelsError(errorMsg);
                mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, 15000);
            }
        });
    }

    VideoView videoView;
    ImageView image,guodu;

    private void showAdm(String time, String https, String href, boolean b) {
        int s = Integer.parseInt(time);
        if (b == true) {
            videoView.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            Uri uri = Uri.parse(https);
            MediaController mediaController = new MediaController(this);
            mediaController.setVisibility(View.GONE);//隐藏进度条
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(uri);
            videoView.requestFocus();
            videoView.start();
            videoView.seekTo(2);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                    guodu.setVisibility(View.GONE);
                    circleProgressbar.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, s * 10000);
                    circleProgressbar.setTimeMillis(s * 10000);
                    circleProgressbar.start();
                    circleProgressbar.setText(s + "s 跳过");
                    //创建倒计时类
                    mCountDownTimer = new MyCountDownTimer(s * 10000, 1000);
                    mCountDownTimer.start();
                }
            });
            videoView.setOnClickListener(this);
        } else {
            circleProgressbar.setVisibility(View.VISIBLE);
            guodu.setVisibility(View.GONE);
            mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, s * 10000);
            circleProgressbar.setTimeMillis(s * 10000);
            circleProgressbar.setText(s + "s 跳过");
            circleProgressbar.start();
            mCountDownTimer = new MyCountDownTimer(s * 10000, 1000);
            mCountDownTimer.start();
            videoView.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
            CornerTransform transformation = new CornerTransform(getContext(), 30);
            //只是绘制左上角和右上角圆角
            transformation.setExceptCorner(false, false, true, true);
            Glide.with(getContext())
                    .load(https)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.admin)
                            .error(R.mipmap.admin)
                            .transform(transformation))
                    .into(image);
            image.setOnClickListener(this);
        }
        //这是一个 Handler 里面的逻辑是从 Splash 界面跳转到 Main 界面，这里的逻辑每个公司基本上一致
        //mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, 3000);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    String path = Constant.FONT_ADRESS + "/video/";

    private void load_video(String https) {
        File file=new File(path);
        File[] files=file.listFiles();
        file.delete();
        String[] str = https.split("\\/");
        String name = str[str.length - 1];
        if(files!=null&&files.length!=0&&!files[0].getName().contains(name)) {
            anyRunnModule.start(https, path + name);
        }
    }

    private void getCategoryNovelsError(String errorMsg) {
        showShortToast(errorMsg);
    }

    // 判断当前是否使用的是 WIFI网络
    public static boolean isWifiActive(Context icontext) {
        Context context = icontext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info;
        if (connectivity != null) {
            info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img:

                break;
            case R.id.activity_opening_videoview:
                Intent intent1 = new Intent();
                intent1.setAction("android.intent.action.VIEW");
                Uri content_url1 = Uri.parse("http://www.cnblogs.com");
                intent1.setData(content_url1);
                getContext().startActivity(intent1);
                break;

            default:
                break;
        }
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }

    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    表示以「 毫秒 」为单位倒计时的总数
         *                          例如 millisInFuture = 1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick()
         *                          例如: countDownInterval = 1000 ; 表示每 1000 毫秒调用一次 onTick()
         */

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }


        public void onFinish() {
            circleProgressbar.setText("0s 跳过");
        }

        public void onTick(long millisUntilFinished) {
            circleProgressbar.setText(millisUntilFinished / 1000 + "s 跳过");
        }

    }

    @Override
    protected void onDestroy() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mHandler.removeMessages(MSG_FINISH_LAUNCHERACTIVITY);
        super.onDestroy();

    }

}
