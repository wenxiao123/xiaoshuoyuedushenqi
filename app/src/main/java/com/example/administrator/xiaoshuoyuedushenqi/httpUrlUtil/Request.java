package com.example.administrator.xiaoshuoyuedushenqi.httpUrlUtil;

import okhttp3.RequestBody;

/**
 * @author
 * Created on 2020/2/18
 */
public class Request {
    String url;
    HttpUrlRequest.METHOD_TYPE methodType; // 请求方式（"GET", "POST" 等）
    int connectTimeout;
    int readTimeout;
    RequestBody body;
    private Request() {}

    public static class Builder {
        private String mUrl;
        private HttpUrlRequest.METHOD_TYPE mMethodType = HttpUrlRequest.METHOD_TYPE.GET;
        private int mConnectTimeout = 8 * 1000;
        private int mReadTimeout = 8 * 1000;
        RequestBody body;

        public Builder setUrl(String url) {
            this.mUrl = url;
            return this;
        }

        public Builder setMethodType(HttpUrlRequest.METHOD_TYPE methodType) {
            this.mMethodType = methodType;
            return this;
        }

        public Builder setConnectTimeout(int connectTimeout) {
            this.mConnectTimeout = connectTimeout;
            return this;
        }

        public Builder setRequestBody(RequestBody body) {
            this.body = body;
            return this;
        }

        public Builder setReadTimeout(int readTimeout) {
            this.mReadTimeout = readTimeout;
            return this;
        }

        public Request build() {
            Request request = new Request();
            request.url = this.mUrl;
            request.methodType = this.mMethodType;
            request.connectTimeout = this.mConnectTimeout;
            request.readTimeout = this.mReadTimeout;
            request.body=this.body;
            return request;
        }
    }

}
