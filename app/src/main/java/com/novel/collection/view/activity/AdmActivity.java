package com.novel.collection.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.arialyy.aria.core.Aria;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.danikula.videocache.HttpProxyCacheServer;
import com.novel.collection.R;
import com.novel.collection.app.App;
import com.novel.collection.base.BaseActivity;
import com.novel.collection.base.BaseActivity_other;
import com.novel.collection.base.BasePresenter;
import com.novel.collection.constant.Constant;
import com.novel.collection.db.DatabaseManager;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.entity.bean.Url_gg;
import com.novel.collection.entity.bean.Website;
import com.novel.collection.entity.data.BookshelfNovelDbData;
import com.novel.collection.http.OkhttpCall;
import com.novel.collection.http.OkhttpUtil;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.util.AdsUtils;
import com.novel.collection.util.AnyRunnModule;
import com.novel.collection.util.LogUtils;
import com.novel.collection.util.SpUtil;
import com.novel.collection.util.StatusBarUtil;
import com.novel.collection.util.ToastUtil;
import com.novel.collection.widget.CircleProgressbar;
import com.novel.collection.widget.CornerTransform;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.novel.collection.app.App.getAppContext;
import static com.novel.collection.app.App.getContext;

//广告界面
public class AdmActivity extends BaseActivity_other implements View.OnClickListener {
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
        getCategoryNovels();
    }

    private MyCountDownTimer mCountDownTimer;

    @Override
    protected void initView() {
        circleProgressbar = findViewById(R.id.tv_red_skip);
        videoView = findViewById(R.id.activity_opening_videoview);
        image = findViewById(R.id.img);
        guodu = findViewById(R.id.guodu);
        anyRunnModule = new AnyRunnModule(this);
        //mCountDownTextView = (TextView) findViewById(R.id.start_skip_count_down);
    }

    @Override
    protected void doAfterInit() {
        circleProgressbar.setOutLineColor(getResources().getColor(R.color.grey_a1));
        circleProgressbar.setProgressColor(getResources().getColor(R.color.red_aa));
        Url_gg gg = (Url_gg) SpUtil.readObject2(this);
        if(gg!=null&&AdsUtils.isAdsLoaded(AdmActivity.this,gg.getHttps())){
            File file = AdsUtils.getAdsFile(AdmActivity.this,gg.getHttps());
            if (file.getAbsolutePath().endsWith(".mp4")) {
                showAdm(gg.getTime(), file.getAbsolutePath(), gg.getUrl(), true);
            } else {
                showAdm(gg.getTime(), file.getAbsolutePath(), gg.getUrl(), false);
            }
        }else {
            Intent intent = new Intent(AdmActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        // }
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
        String url = UrlObtainer.GetUrl() + "/api/index/getqadm";
        RequestBody requestBody = new FormBody.Builder()
                .build();
        OkhttpUtil.getpostRequest(url, requestBody, new OkhttpCall() {
            @Override
            public void onResponse(String json) {   // 得到 json 数据
                Log.e("QQQ", "onResponse: "+json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if (!jsonObject.isNull("data")) {
                        if (code.equals("1")) {
                            JSONObject object = jsonObject.getJSONObject("data");
                            String img = object.getString("value");
                            String href = object.getString("url");
                            String time = object.getString("time");
                            String types = object.getString("types");
                            //String type=img.substring(img.length()-1,img.length()-4);
                            if (types.equals("1") && (img.contains(".png") || img.contains(".jpg") || img.contains(".jpeg"))) {
                                String https;
                                if (img.contains("http")) {
                                    https = img;
                                } else {
                                    https = UrlObtainer.GetFileUrl() + "/" + img;
                                }
                                Url_gg gg = new Url_gg(href, time, https);
                                SpUtil.saveObject2(AdmActivity.this, gg);
                                AdsUtils.downLoadAds(AdmActivity.this,https);
                            } else if (img.contains(".mp4")) {
                                String https;
                                if (img.contains("http")) {
                                    https = img;
                                } else {
                                    https = UrlObtainer.GetFileUrl() + "/" + img;
                                }
                                Url_gg gg = new Url_gg(href, time, https);
                                SpUtil.saveObject2(AdmActivity.this, gg);
                                AdsUtils.downLoadAds(AdmActivity.this,https);
                            }
                        }
                    } else {
                        Intent intent = new Intent(AdmActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, 1500);
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
    ImageView image, guodu;
    private String proxyUrl;
    private HttpProxyCacheServer proxy;

    //显示广告
    private void showAdm(String time, String https, String href, boolean b) {
        int s = Integer.parseInt(time);
        if (b == true) {
            videoView.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            //Uri uri = Uri.parse(https);
            MediaController mediaController = new MediaController(this);
            mediaController.setVisibility(View.GONE);//隐藏进度条
            videoView.setMediaController(mediaController);
            Uri rawUri = Uri.parse(https);
            //String netPlayUrl="http://baobab.wdjcdn.com/145076769089714.mp4";
            proxy = App.getProxy(getAppContext());
            proxyUrl = proxy.getProxyUrl(https);
//            videoView.setVideoPath(proxyUrl);//播放的是代理服务器返回的url，已经进行了封装处理
            videoView.setVideoURI(rawUri);
            videoView.requestFocus();
            videoView.start();
            videoView.seekTo(2);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // mp.setLooping(true);
                    guodu.setVisibility(View.GONE);
                    circleProgressbar.setVisibility(View.VISIBLE);
                    //mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, s * 10000);
                    circleProgressbar.setTimeMillis(s * 1000);
                    circleProgressbar.start();
                    circleProgressbar.setText(s + "s 跳过");
                    //创建倒计时类
                    mCountDownTimer = new MyCountDownTimer(s * 1000, 1000);
                    mCountDownTimer.start();
                }
            });
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    videoView.stopPlayback(); //播放异常，则停止播放，防止弹窗使界面阻塞
                    return true;
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, 100);
                }
            });
            videoView.setOnClickListener(this);
        } else {
            circleProgressbar.setVisibility(View.VISIBLE);
            guodu.setVisibility(View.GONE);
            mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, s * 1000);
            circleProgressbar.setTimeMillis(s * 1000);
            circleProgressbar.setText(s + "s 跳过");
            circleProgressbar.start();
            mCountDownTimer = new MyCountDownTimer(s * 1000, 1000);
            mCountDownTimer.start();
            videoView.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            CornerTransform transformation = new CornerTransform(getContext(), 30);
            //只是绘制左上角和右上角圆角
            transformation.setExceptCorner(false, false, true, true);
            Glide.with(getContext())
                    .load(https)
                    .apply(new RequestOptions()
                            .placeholder(R.color.trans)
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
        File file = new File(path);
        File[] files = file.listFiles();
        file.delete();
        String[] str = https.split("\\/");
        String name = str[str.length - 1];
        if (files != null && files.length != 0 && !files[0].getName().contains(name)) {
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

    /**
     * 保存位图到本地
     *
     * @param bitmap
     * @param other_path   本地路径
     * @return void
     */
    public void SavaImage(Bitmap bitmap, String other_path) {
        File fil = new File(path);
        fil.delete();
        File file = new File(other_path);
        FileOutputStream fileOutputStream = null;
        //文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            fileOutputStream = new FileOutputStream(other_path + "/" + System.currentTimeMillis() + ".png");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Bitmap bitmap;

    /**
     * 异步线程下载图片
     */
    class Task extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            bitmap = GetImageInputStream((String) params[0]);
            SavaImage(bitmap, path);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Message message = new Message();
            message.what = 0x123;
            //handler.sendMessage(message);
        }

    }

    /**
     * 获取网络图片
     *
     * @param imageurl 图片网络地址
     * @return Bitmap 返回位图
     */
    public Bitmap GetImageInputStream(String imageurl) {
        URL url;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            url = new URL(imageurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
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
            circleProgressbar.setText("跳过");
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
