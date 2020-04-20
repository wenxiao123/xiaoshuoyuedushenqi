package com.example.administrator.xiaoshuoyuedushenqi.weyue.interfaces;

/**
 * Created by Liang_Lu on 2017/12/3.
 */

public interface NetCallBack<T> {
    void onSuccess(T t);

    void onFail(String reason);
}
