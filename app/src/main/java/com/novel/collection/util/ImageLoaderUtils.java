package com.novel.collection.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.novel.collection.R;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.widget.CornerTransform;

import java.io.File;

/**

 Description : 图片加载工具类 使用glide框架封装
 */
public class ImageLoaderUtils {

    public static void display(Context context, ImageView imageView, String url, int placeholder, int error) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        String img_http;
        if(!url.contains("http")){
            img_http= UrlObtainer.GetUrl()+url;
        }else {
            img_http=url;
        }
        CornerTransform transformation = new CornerTransform(context, 10);
        Glide.with(context).load(img_http).apply(new RequestOptions()
                .placeholder(placeholder)
                .error(error)
                .transform(transformation).dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)).into(imageView);
    }
    public static void display(Context context, ImageView imageView, String url) {
        CornerTransform transformation = new CornerTransform(context, 10);
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        String img_http;
        if(!url.contains("http")){
            img_http= UrlObtainer.GetUrl()+url;
        }else {
            img_http=url;
        }
        try {
            Glide.with(context).load(img_http).apply(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).transform(transformation)).into(imageView);
        }catch (Exception ex){
            return;
        }
    }
}