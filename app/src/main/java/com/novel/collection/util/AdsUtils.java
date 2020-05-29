package com.novel.collection.util;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AdsUtils {
    private static final String BasePicUrl="";
    //下载广告
    public static void downLoadAds(Context mContext,String adsUrl) {
        if (TextUtils.isEmpty(adsUrl)) return;
        String url = adsUrl.replaceAll("\"", "");

        if (!TextUtils.isEmpty(url)) {
            if (!url.contains("http")) {
                url =BasePicUrl + url;
            }
        }

        String finalUrl = url;
        new Thread(new Runnable() {
            @Override
            public void run() {
                downLoadFile(mContext,finalUrl);
            }
        }).start();


    }

    //下载文件
    private static void downLoadFile(Context mContext,String url) {
        File adsFile = getAdsFile(mContext, url);
        if (!adsFile.exists()) {
            InputStream is = null;
            HttpURLConnection conn = null;
            RandomAccessFile aaf = null;
            try {
                URL Url = new URL(url);
                conn = (HttpURLConnection) Url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5 * 1000000);
                conn.setReadTimeout(5 * 1000000);
                conn.setRequestProperty("Referer", url.toString());
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setAllowUserInteraction(true);
                conn.connect();
                is = conn.getInputStream();
                aaf = new RandomAccessFile(adsFile, "rwd");
                aaf.seek(0);
                int len;

                byte[] bytes = new byte[5120];
                while ((len = is.read(bytes, 0, bytes.length)) != -1) {
                    aaf.write(bytes, 0, len);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();


            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (aaf != null) {
                        aaf.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    }

    /**
     * 获取广告文件
     *
     * @param mContext
     * @return
     */
    public static File getAdsFile(Context mContext, String url) {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        File appDir = new File(mContext.getExternalCacheDir(), "ads");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        return file;
    }

//    public void setAdvertising(AdvertiseBean advertiseBean) {
//
//        //获取缓存的广告文件
//        File ads = FileUtil.getAdsFile(this, advertiseBean.url);
//
//        if (ads.exists()) {
//            if (advertiseBean.type == 2) {
//                //当广告类型为视频文件时播放缓存视频
//                player = new MediaPlayer();
//                player.setVolume(0, 0);
//
//                try {
//                    player.setDataSource(ads.getAbsolutePath());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                    @Override
//                    public boolean onError(MediaPlayer mp, int what, int extra) {
//                        if (surfaceView != null) {
//                            surfaceView.setVisibility(View.GONE);
//                        }
//                        return false;
//                    }
//                });
//                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        int videoWidth = mp.getVideoWidth();
//                        int videoHeight = mp.getVideoHeight();
//
//                        DisplayMetrics dm = new DisplayMetrics();
//                        getWindowManager().getDefaultDisplay().getMetrics(dm);
//                        int mSurfaceViewWidth = dm.widthPixels;
//                        int mSurfaceViewHeight = dm.heightPixels;
//
//                        int w = mSurfaceViewHeight * videoWidth / videoHeight;
//                        int margin = (mSurfaceViewWidth - w) / 2;
//                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//                        lp.setMargins(margin, 0, margin, 0);
//                        surfaceView.setLayoutParams(lp);
//                        player.start();
//                    }
//                });
//
//                surfaceView.setVisibility(View.VISIBLE);
//                surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
//                    @Override
//                    public void surfaceCreated(SurfaceHolder holder) {
//                        player.setDisplay(holder);
//                        player.prepareAsync();
//                    }
//
//                    @Override
//                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//                    }
//
//                    @Override
//                    public void surfaceDestroyed(SurfaceHolder holder) {
//
//                    }
//                });
//            } else if (advertiseBean.url.endsWith("gif")) {
//                try {
//                    GifDrawable gifDrawable = new GifDrawable(FileUtil.getAdsFile(this, advertiseBean.url).getAbsolutePath());
//                    img.setImageDrawable(gifDrawable);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    img.setImageBitmap(BitmapFactory.decodeFile(FileUtil.getAdsFile(this, advertiseBean.url).getAbsolutePath()));
//                }
//
//
//
//            } else {
//                img.setImageBitmap(BitmapFactory.decodeFile(FileUtil.getAdsFile(this, advertiseBean.url).getAbsolutePath()));
//            }
//        } else {
//            Intent intent = new Intent(GuideAdvertisingActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//
//
//    }

    public static boolean isAdsLoaded(Context mContext,String adsUrl) {
        return  getAdsFile(mContext, adsUrl).exists();

    }
}
