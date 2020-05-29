package com.novel.collection.http;


import com.novel.collection.app.App;
import com.novel.collection.entity.bean.Login_admin;
import com.novel.collection.util.LogUtils;
import com.novel.collection.util.SpUtil;

import java.io.IOException;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.novel.collection.http.OkhttpUtil.stringToMD5;

public class CommonParametersInterceptorHead implements Interceptor {
    private String TAG = "CommonParametersInterceptor";
    private static final String METHOD_GET = "GET";
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        HttpUrl.Builder urlBuilder = oldRequest.url().newBuilder();
        if (METHOD_GET.equals(oldRequest.method())) { // GET方法
            // 这里可以添加一些公共get参数
            HttpUrl httpUrl = urlBuilder.build();
            Set<String> paramKeys = httpUrl.queryParameterNames();
            for (String key : paramKeys) {
                String value = httpUrl.queryParameter(key);
                LogUtils.e(key+" "+value);
            }
        }else {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            // 把已有的post参数添加到新的构造器
            if (oldRequest.body() instanceof FormBody) {
                FormBody formBody = (FormBody) oldRequest.body();
                for (int i = 0; i < formBody.size(); i++) {
                    bodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                }
            }
            FormBody newBody = bodyBuilder.build();

            // 打印所有post参数
            for (int i = 0; i < newBody.size(); i++) {
                // Log.d("TEST", newBody.name(i) + " " + newBody.value(i));
               LogUtils.e(newBody.name(i) + " " + newBody.value(i));
            }
        }
        Response response = null;
        // 新的请求,添加参数
        Request addParamRequest = addParam(oldRequest);
        response = chain.proceed(addParamRequest);
        return response;
    }

    /**
     * 添加公共参数
     *
     * @param oldRequest
     * @return
     */
    private Request addParam(Request oldRequest) {
        Login_admin login_admin= (Login_admin) SpUtil.readObject(App.getContext());
        String token=null;
        if(login_admin!=null){
            token=login_admin.getToken();
        }
        HttpUrl.Builder builder;
        LogUtils.e(stringToMD5("af1020a25f48ds4g55r6y."));
        if(token!=null) {
             builder = oldRequest.url()
                    .newBuilder()
                    .setEncodedQueryParameter("sign", stringToMD5("af1020a25f48ds4g55r6y."))
                    .setEncodedQueryParameter("token", token);
        }else {
            builder = oldRequest.url()
                    .newBuilder()
                    .setEncodedQueryParameter("sign", stringToMD5("af1020a25f48ds4g55r6y."));
        }
        Request newRequest = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body())
                .url(builder.build())
                .build();
        return newRequest;
    }


    /**
     * 添加请求头
     *
     * @param oldRequest
     * @return
     */
    public Request addHeader(Request oldRequest) {
        Request.Builder builder = oldRequest.newBuilder().addHeader("user-agent", "Android-APP").addHeader("Connection","close");
        return builder.build();
    }
}
