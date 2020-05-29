package com.novel.collection.httpUrlUtil;

/**
 * @author
 * Created on 2020/2/24
 */
public interface Response {
    void success(String response);  // 请求成功，返回 json 数据
    void error(String errorMsg);  // 请求失败，返回原因
}
