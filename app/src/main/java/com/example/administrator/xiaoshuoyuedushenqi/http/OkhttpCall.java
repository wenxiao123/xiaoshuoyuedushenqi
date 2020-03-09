package com.example.administrator.xiaoshuoyuedushenqi.http;

/**
 * @author WX
 * Created on 2019/11/17
 */
public interface OkhttpCall {
    void onResponse(String json);
    void onFailure(String errorMsg);
}
