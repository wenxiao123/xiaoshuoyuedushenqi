package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.TextStyle;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.util.AnyRunnModule;
import com.example.administrator.xiaoshuoyuedushenqi.util.FileDowned;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * @author Created on 2019/12/21
 */
public class TextStyleAdapter extends RecyclerView.Adapter<TextStyleAdapter.ScreenViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<TextStyle> textStyles;
    private int position = 0;

    public void setPosition(int position) {
        this.position = position;
    }


    public void setmListener(ScreenListener mListener) {
        this.mListener = mListener;
    }

    private ScreenListener mListener;

    @Override
    public void onClick(View view) {
        int pos = (int) view.getTag();
        mListener.clickItem(pos);
    }

    public interface ScreenListener {
        void clickItem(int position);
    }
    AnyRunnModule anyRunnModule;
    public TextStyleAdapter(Context mContext, List<TextStyle> textStyles
    ) {
        this.mContext = mContext;
        this.textStyles = textStyles;
        anyRunnModule=new AnyRunnModule(mContext);
    }

    @NonNull
    @Override
    public ScreenViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ScreenViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_textstyle, null));
    }

    String url;

    @Override
    public void onBindViewHolder(@NonNull ScreenViewHolder screenViewHolder, final int i) {
        Typeface tf = null;
        AssetManager mgr = mContext.getAssets();
        if(i==0) {
            tf=Typeface.create("sans-serif-medium",Typeface.NORMAL);
        }else if(i==1) {
            tf = Typeface.createFromAsset(mgr, Constant.text_adress1);
        }else if(i==2) {
            tf = Typeface.createFromAsset(mgr,  Constant.text_adress2);
        }else if(i==3) {
            tf = Typeface.createFromAsset(mgr,  Constant.text_adress3);
        }else if(i==4) {
            tf = Typeface.createFromAsset(mgr,  Constant.text_adress4);
        }
        screenViewHolder.text.setTypeface(tf);
        screenViewHolder.text.setText(textStyles.get(i).getName());
        if (position == i) {
            screenViewHolder.text.setTextColor(mContext.getResources().getColor(R.color.yellow));
            screenViewHolder.img.setVisibility(View.VISIBLE);
        } else {
            screenViewHolder.text.setTextColor(mContext.getResources().getColor(R.color.black));
            screenViewHolder.img.setVisibility(View.GONE);
        }
            screenViewHolder.txt.setVisibility(View.GONE);
//        screenViewHolder.txt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//             //   url=UrlObtainer.GetUrl() + textStyles.get(i).getUrl();
////                long taskId = Aria.download(mContext)
////                        .load(UrlObtainer.GetUrl() + textStyles.get(i).getUrl())     //读取下载地址
////                        .setFilePath(path + textStyles.get(i).getName()+".ttf") //设置文件保存的完整路径
////                        .create();   //创建并启动下载
////                screenViewHolder.txt.setText("下载...");
          //      anyRunnModule.start(url,path + textStyles.get(i).getName()+".ttf",screenViewHolder.txt);
//            }
//        });
        screenViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clickItem(i);
            }
        });
    }

    String path = Constant.FONT_ADRESS + "/Font/";

    @Override
    public int getItemCount() {
        return textStyles.size();
    }

    class ScreenViewHolder extends RecyclerView.ViewHolder {
        TextView text, txt;
        ImageView img;

        public ScreenViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tv_item_screen_text);
            img = itemView.findViewById(R.id.img);
            txt = itemView.findViewById(R.id.txt);
        }
    }

}
