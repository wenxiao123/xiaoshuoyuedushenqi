package com.novel.collection.banner;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.danikula.videocache.HttpProxyCacheServer;
import com.novel.collection.app.App;
import com.novel.collection.entity.bean.Wheel;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steven on 2018/5/14.
 */

public class Banner extends RelativeLayout {
    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    private ViewPager viewPager;
    private final int UPTATE_VIEWPAGER = 100;
    //图片默认时间间隔
    private int imgDelyed = 2000;
    //每个位置默认时间间隔，因为有视频的原因
    private int delyedTime = 2000;
    //默认显示位置
    private int autoCurrIndex = 0;
    //是否自动播放
    private boolean isAutoPlay = false;
    private Time time;
    private List<BannerModel> bannerModels;
    private List<String> list;
    private List<View> views;
    private BannerViewAdapter mAdapter;

    public Banner(Context context) {
        super(context);
        init();
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Banner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Banner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        time = new Time();
        viewPager = new ViewPager(getContext());
        LinearLayout.LayoutParams vp_param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        viewPager.setLayoutParams(vp_param);
        this.addView(viewPager);
    }


    public void setDataList(List<Wheel> dataList) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        //用于显示的数组
        if (views == null) {
            views = new ArrayList<>();
        } else {
            views.clear();
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        //数据大于一条，才可以循环
        if (dataList.size() > 1) {
            autoCurrIndex = 1;
            //循环数组，将首位各加一条数据
            for (int i = 0; i < dataList.size() + 2; i++) {
                String url;
                if (i == 0) {
                    url = dataList.get(dataList.size() - 1).getPicpath();
                } else if (i == dataList.size() + 1) {
                    url = dataList.get(0).getPicpath();
                } else {
                    url = dataList.get(i - 1).getPicpath();
                }

                if (MimeTypeMap.getFileExtensionFromUrl(url).equals("mp4")) {
                    if (url.contains("http:")) {
                        url = url;
                    } else {
                        url = UrlObtainer.GetFileUrl() + url;
                    }
                    HttpProxyCacheServer proxy = App.getProxy(getContext());
                    String proxyUrl = proxy.getProxyUrl(url);
                    MVideoView videoView = new MVideoView(getContext());
                    videoView.setLayoutParams(lp);
                    videoView.setVideoURI(Uri.parse(proxyUrl));
                    videoView.start();
                    views.add(videoView);
                    List<Wheel> finalDataList3 = dataList;
                    videoView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //ToastUtil.showToast(getContext(), finalDataList3.get(0).getNovel_id()+"");
                        }
                    });
                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            videoView.stopPlayback(); //播放异常，则停止播放，防止弹窗使界面阻塞
                            return true;
                        }
                    });
                } else {
                    if (url.contains("http:")) {
                        url = url;
                    } else {
                        url = UrlObtainer.GetFileUrl() + url;
                    }
                    ImageView imageView = new ImageView(getContext());
                    imageView.setLayoutParams(lp);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(getContext()).load(url).apply(options.dontAnimate()).into(imageView);
                    views.add(imageView);
                    List<Wheel> finalDataList2 = dataList;
                    imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //ToastUtil.showToast(getContext(), finalDataList2.get(0).getNovel_id()+"");
                        }
                    });
                }
            }
        } else if (dataList.size() == 1) {
            autoCurrIndex = 0;
            String url = dataList.get(0).getPicpath();
            if (MimeTypeMap.getFileExtensionFromUrl(url).equals("mp4")) {
                if (url.contains("http:")) {
                    url = url;
                } else {
                    url = UrlObtainer.GetFileUrl() + url;
                }
                HttpProxyCacheServer proxy = App.getProxy(getContext());
                String proxyUrl = proxy.getProxyUrl(url);
                MVideoView videoView = new MVideoView(getContext());
                videoView.setLayoutParams(lp);
                videoView.setVideoURI(Uri.parse(proxyUrl));
                videoView.start();
                //监听视频播放完的代码
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mPlayer) {
                        mPlayer.start();
                        mPlayer.setLooping(true);
                    }
                });
                views.add(videoView);
                List<Wheel> finalDataList = dataList;
                videoView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ToastUtil.showToast(getContext(), finalDataList.get(0).getNovel_id()+"");
                    }
                });
            } else {
                if (url.contains("http:")) {
                    url = url;
                } else {
                    url = UrlObtainer.GetFileUrl() + url;
                }
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(lp);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(getContext()).load(url).apply(options.dontAnimate()).into(imageView);
                views.add(imageView);
                List<Wheel> finalDataList1 = dataList;
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       //ToastUtil.showToast(getContext(), finalDataList1.get(0).getNovel_id()+"");
                    }
                });
            }
        }
    }


    public void setImgDelyed(int imgDelyed) {
        this.imgDelyed = imgDelyed;
    }

    public void startBanner() {
        mAdapter = new BannerViewAdapter(views);
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setCurrentItem(autoCurrIndex);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("TAG", "position:" + position);
                //当前位置
                autoCurrIndex = position;
                getDelayedTime(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("TAG", "" + state);

                //移除自动计时
                mHandler.removeCallbacks(runnable);

                //ViewPager跳转
                int pageIndex = autoCurrIndex;
                if (autoCurrIndex == 0) {
                    pageIndex = views.size() - 2;
                } else if (autoCurrIndex == views.size() - 1) {
                    pageIndex = 1;
                }
                if (pageIndex != autoCurrIndex) {
                    //无滑动动画，直接跳转
                    viewPager.setCurrentItem(pageIndex, false);
                }

                //停止滑动时，重新自动倒计时
                if (state == 0 && isAutoPlay && views.size() > 1) {
                    View view1 = views.get(pageIndex);
                    if (view1 instanceof VideoView) {
                        final VideoView videoView = (VideoView) view1;
                        int current = videoView.getCurrentPosition();
                        int duration = videoView.getDuration();
                        delyedTime = duration - current;
                        //某些时候，某些视频，获取的时间无效，就延时10秒，重新获取
                        if (delyedTime <= 0) {
                            time.getDelyedTime(videoView, runnable);
                            mHandler.postDelayed(time, imgDelyed);
                        } else {
                            mHandler.postDelayed(runnable, delyedTime);
                        }
                    } else {
                        delyedTime = imgDelyed;
                        mHandler.postDelayed(runnable, delyedTime);
                    }
                    Log.d("TAG", "" + pageIndex + "--" + autoCurrIndex);
                }
            }
        });
    }

    //开启自动循环
    public void startAutoPlay() {
        isAutoPlay = true;
        if (views.size() > 1) {
            getDelayedTime(autoCurrIndex);
            if (delyedTime <= 0) {
                mHandler.postDelayed(time, imgDelyed);
            } else {
                mHandler.postDelayed(runnable, delyedTime);
            }
        }
    }

    /**
     * 发消息，进行循环
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(UPTATE_VIEWPAGER);
        }
    };

    /**
     * 这个类，恩，获取视频长度，以及已经播放的时间
     */
    private class Time implements Runnable {

        private VideoView videoView;
        private Runnable runnable;

        public void getDelyedTime(VideoView videoView, Runnable runnable) {
            this.videoView = videoView;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            int current = videoView.getCurrentPosition();
            int duration = videoView.getDuration();
            int delyedTime = duration - current;
            mHandler.postDelayed(runnable, delyedTime);
        }
    }

    //接受消息实现轮播
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPTATE_VIEWPAGER:
                    viewPager.setCurrentItem(autoCurrIndex + 1);
                    break;
            }
        }
    };

    private class BannerModel {
        public String url;
        public int playTime;
        public int type = 0;
    }

    /**
     * 获取delyedTime
     *
     * @param position 当前位置
     */
    private void getDelayedTime(int position) {
        View view1 = views.get(position);
        if (view1 instanceof VideoView) {
            VideoView videoView = (VideoView) view1;
            videoView.start();
            videoView.seekTo(0);
            delyedTime = videoView.getDuration();
            time.getDelyedTime(videoView, runnable);
        } else {
            delyedTime = imgDelyed;
        }
    }

    public void stopVideo() {
       if(views!=null) {
           for (int i = 0; i < views.size(); i++) {
               View view1 = views.get(i);
               if (view1 instanceof VideoView) {
                   VideoView videoView = (VideoView) view1;
                   videoView.pause();
               }
           }
       }
    }

    public void stratVideo() {
        try {
            for (int i = 0; i < views.size(); i++) {
                View view1 = views.get(i);
                if (view1 instanceof VideoView) {
                    VideoView videoView = (VideoView) view1;
                    if (!videoView.isPlaying()) {
                        videoView.start();
                        videoView.seekTo(0);
                    }
                }
            }
        } catch (Exception ex) {
            return;
        }

    }

//    public void dataChange(List<String> list){
//        if (list != null && list.size()>0)
//        {
//            //改变资源时要重新开启循环，否则会把视频的时长赋给图片，或者相反
//            //因为delyedTime也要改变，所以要重新获取delyedTime
//            mHandler.removeCallbacks(runnable);
//            setDataList(list);
//            mAdapter.setDataList(views);
//            mAdapter.notifyDataSetChanged();
//            viewPager.setCurrentItem(autoCurrIndex,false);
//            //开启循环
//            if (isAutoPlay && views.size() > 1){
//                getDelayedTime(autoCurrIndex);
//                if (delyedTime <= 0){
//                    mHandler.postDelayed(time,imgDelyed);
//                }else {
//                    mHandler.postDelayed(runnable,delyedTime);
//                }
//            }
//        }
//    }

    public void destroy() {
        if(mHandler!=null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        time = null;
        runnable = null;
        if (views != null) {
            views.clear();
        }
        views = null;
        viewPager = null;
        mAdapter = null;
    }
}
