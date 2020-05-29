package com.novel.collection.http;

import android.util.Log;

import com.novel.collection.app.App;
import com.novel.collection.entity.bean.Login_admin;
import com.novel.collection.util.SpUtil;
import com.novel.collection.weyue.utils.LogUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public  class ParamsInterceptor implements Interceptor {
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String HEADER_KEY_USER_AGENT = "User-Agent";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        HttpUrl.Builder urlBuilder = request.url().newBuilder();
        Login_admin login_admin= (Login_admin) SpUtil.readObject(App.getContext());
        String token=null;
        if(login_admin!=null){
            token=login_admin.getToken();
        }
        if (METHOD_GET.equals(request.method())) { // GET方法
            // 这里可以添加一些公共get参数
            HttpUrl httpUrl = urlBuilder.build();
             boolean is_cover=false;
            // 打印所有get参数
            Set<String> paramKeys = httpUrl.queryParameterNames();
            for (String key : paramKeys) {
                String value = httpUrl.queryParameter(key);
                if(key.equals("token")){
                    is_cover=true;
                }
                //LogUtils.e(key+" "+value);
            }
            if(token!=null&&is_cover==false) {
                urlBuilder.addEncodedQueryParameter("token", token);
            }
            urlBuilder.addEncodedQueryParameter("sign", stringToMD5("af1020a25f48ds4g55r6y."));
            // 将最终的url填充到request中
            requestBuilder.url(httpUrl);
        } else if (METHOD_POST.equals(request.method())) { // POST方法
            // FormBody和url不太一样，若需添加公共参数，需要新建一个构造器
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            // 把已有的post参数添加到新的构造器
            if (request.body() instanceof FormBody) {
                FormBody formBody = (FormBody) request.body();
                for (int i = 0; i < formBody.size(); i++) {
                    bodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                }
            }
            boolean is_cover=false;
            FormBody newBody = bodyBuilder.build();

            // 打印所有post参数
            for (int i = 0; i < newBody.size(); i++) {
                if(newBody.name(i).equals("token")){
                    is_cover=true;
                }
               // Log.d("TEST", newBody.name(i) + " " + newBody.value(i));
                //LogUtils.e(newBody.name(i) + " " + newBody.value(i));
            }
            if(token!=null&&is_cover==false) {
                // 这里可以添加一些公共post参数
                bodyBuilder.addEncoded("token", token);
            }
            bodyBuilder.addEncoded("sign", stringToMD5("af1020a25f48ds4g55r6y."));
            // 将最终的表单body填充到request中
            requestBuilder.post(newBody);
        }

        // 这里我们可以添加header
        //requestBuilder.addHeader(HEADER_KEY_USER_AGENT, getUserAgent()); // 举例，调用自己业务的getUserAgent方法
        return chain.proceed(requestBuilder.build());
    }

    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }
}
