package com.example.administrator.xiaoshuoyuedushenqi.http;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.administrator.xiaoshuoyuedushenqi.app.App;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
import com.example.administrator.xiaoshuoyuedushenqi.util.SpUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 对 okhttp3 的简单封装
 *
 * @author
 * Created on 2019/11/17
 */
public class OkhttpUtil {

    //创建OkHttpClient
    private static volatile OkHttpClient okHttpClient;

    private static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (OkhttpUtil.class) {
                if (okHttpClient == null) {
                    //okHttpClient = new OkHttpClient();
                    okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            //.addInterceptor(new ParamsInterceptor())
                            .build();
                }
            }
        }
        return okHttpClient;
    }

    public static void getRequest(String url, final OkhttpCall okhttpCall) {
        //创建Request
        Request request = new Request.Builder()
                .url(url)
                .build();
        //创建Call
        Call call = getOkHttpClient().newCall(request);
        //调用Call的enqueue方法，该方法的回调是在子线程
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                doInUiThread(new Runnable() {
                    @Override
                    public void run() {
                        okhttpCall.onFailure(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    final String data = response.body().string();
                    doInUiThread(new Runnable() {
                        @Override
                        public void run() {
                            okhttpCall.onResponse(data);
                        }
                    });
                } catch (final IOException e) {
                    doInUiThread(new Runnable() {
                        @Override
                        public void run() {
                            okhttpCall.onFailure(e.getMessage());
                        }
                    });
                }
            }
        });
    }

    public static void getpostRequest(String url, RequestBody requestBody, final OkhttpCall okhttpCall) {
        //创建Request
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        //创建Call
        Call call = getOkHttpClient().newCall(request);
        //调用Call的enqueue方法，该方法的回调是在子线程
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                doInUiThread(new Runnable() {
                    @Override
                    public void run() {
                        okhttpCall.onFailure(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    final String data = response.body().string();
                    doInUiThread(new Runnable() {
                        @Override
                        public void run() {
                            okhttpCall.onResponse(data);
                        }
                    });
                } catch (final IOException e) {
                    doInUiThread(new Runnable() {
                        @Override
                        public void run() {
                            okhttpCall.onFailure(e.getMessage());
                        }
                    });
                }
            }
        });
    }

    /**
     * 上传文件
     * @param filePath  本地文件地址
     */
    public static <T> void upLoadFile(String token, String filePath, final OkhttpCall okhttpCall) {
        FormBody paramsBody=new FormBody.Builder()
                .add("token",token)
                .build();
        MediaType type=MediaType.parse("application/octet-stream");//"text/xml;charset=utf-8"
        File file=new File(filePath);
        RequestBody fileBody=RequestBody.create(type,file);

        RequestBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.ALTERNATIVE)
                .addPart(paramsBody)
                .addPart(fileBody)
                .build();

        //创建Request
        final Request request = new Request.Builder().url(UrlObtainer.GetUrl()+"/"+"/api/index/upload").post(multipartBody).build();
        Call call = getOkHttpClient().newCall(request);
        //调用Call的enqueue方法，该方法的回调是在子线程
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                doInUiThread(new Runnable() {
                    @Override
                    public void run() {
                        okhttpCall.onFailure(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    final String data = response.body().string();
                    doInUiThread(new Runnable() {
                        @Override
                        public void run() {
                            okhttpCall.onResponse(data);
                        }
                    });
                } catch (final IOException e) {
                    doInUiThread(new Runnable() {
                        @Override
                        public void run() {
                            okhttpCall.onFailure(e.getMessage());
                        }
                    });
                }
            }
        });
    }

    private static void doInUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
