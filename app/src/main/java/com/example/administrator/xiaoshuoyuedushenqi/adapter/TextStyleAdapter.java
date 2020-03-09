package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.util.ToastUtil;

import java.util.List;

/**
 * @author WX
 * Created on 2019/12/21
 */
public class TextStyleAdapter extends RecyclerView.Adapter<TextStyleAdapter.ScreenViewHolder> implements View.OnClickListener {

    private Context mContext;
    private String[] mContentList;
    private int position=0;

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

    public TextStyleAdapter(Context mContext, String[] mContentList
                           ) {
        this.mContext = mContext;
        this.mContentList = mContentList;
    }

    @NonNull
    @Override
    public ScreenViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ScreenViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_textstyle, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ScreenViewHolder screenViewHolder, final int i) {
        screenViewHolder.text.setText(mContentList[i]);
        if(position==i){
            screenViewHolder.img.setVisibility(View.VISIBLE);
        }else {
            screenViewHolder.img.setVisibility(View.GONE);
        }
        //从asset 读取字体
        AssetManager mgr = mContext.getAssets();
        Typeface tf;
        if(i==0) {
            tf = Typeface.createFromAsset(mgr, "font/fzkatong.ttf");
        }else {
            tf = Typeface.createFromAsset(mgr, "font/qihei.ttf");
        }
        screenViewHolder.text.setTypeface(tf);
        screenViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clickItem(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContentList.length;
    }

    class ScreenViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView img;
        public ScreenViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tv_item_screen_text);
            img=itemView.findViewById(R.id.img);
        }
    }
}
