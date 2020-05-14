package com.example.administrator.xiaoshuoyuedushenqi.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.xiaoshuoyuedushenqi.R;

import java.io.File;

/**

 Description : 图片加载工具类 使用glide框架封装
 */
public class ImageLoaderUtils {

    public static void display(Context context, ImageView imageView, String url, int placeholder, int error) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url).apply(new RequestOptions()
                .placeholder(placeholder)
                .error(error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)).into(imageView);
    }
    public static void display(Context context, ImageView imageView, String url) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)).into(imageView);
    }
}