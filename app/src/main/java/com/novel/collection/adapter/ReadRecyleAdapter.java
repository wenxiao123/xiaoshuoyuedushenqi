package com.novel.collection.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.novel.collection.R;
import com.novel.collection.util.SpUtil;
import com.novel.collection.widget.PageView;
import com.novel.collection.widget.Page_item;
import com.novel.collection.widget.RealPageView;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.util.List;

/**
 * @author
 * Created on 2019/11/17
 */
public class ReadRecyleAdapter extends RecyclerView.Adapter<ReadRecyleAdapter.CatalogViewHolder> implements View.OnClickListener{
    private static final String TAG = "CatalogAdapter";

    private Context mContext;
    private int[] ints;
    List<String> strings;
    private CatalogListener mListener;

    @Override
    public void onClick(View v) {
        int pos = (int) v.getTag();
        mListener.clickItem(pos);
    }

    public interface CatalogListener {
        void clickItem(int position);
    }

    public ReadRecyleAdapter(Context mContext,  List<String> strings) {
        this.mContext = mContext;
        this.strings = strings;
    }
    public void setOnCatalogListener(CatalogListener listener) {
        mListener = listener;
    }
    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CatalogViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.page_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder catalogViewHolder, final int i) {
        catalogViewHolder.tv_title.setText(strings.get(i));
        if(strings.get(i).equals("")){
            catalogViewHolder.tv_title.setVisibility(View.GONE);
        }
        //int  bgColor = mContext.getResources().getColor(R.color.read_theme_0_bg);
        float mTextSize = SpUtil.getTextSize();
                Typeface tf=Typeface.create("sans-serif-medium",Typeface.NORMAL);
        catalogViewHolder.tv_title.setTypeface(tf);
        catalogViewHolder.tv_title.setTextSize(mTextSize);
        float  mRowSpace = SpUtil.getRowSpace();
        int mTheme = SpUtil.getTheme();
        int bgColor =mContext.getResources().getColor(R.color.read_theme_0_bg);
        int textColor = mContext.getResources().getColor(R.color.read_theme_0_text);
        switch (mTheme) {
            case 0:
                bgColor =mContext. getResources().getColor(R.color.read_theme_0_bg);
                textColor = mContext.getResources().getColor(R.color.read_theme_0_text);
                break;
            case 1:
                bgColor = mContext.getResources().getColor(R.color.read_theme_1_bg);
                textColor =mContext. getResources().getColor(R.color.read_theme_1_text);
                break;
            case 2:
                bgColor = mContext.getResources().getColor(R.color.read_theme_2_bg);
                textColor =mContext. getResources().getColor(R.color.read_theme_2_text);
                break;
            case 3:
                bgColor = mContext.getResources().getColor(R.color.read_theme_3_bg);
                textColor = mContext.getResources().getColor(R.color.read_theme_3_text);
                break;
            case 4:
                bgColor =mContext. getResources().getColor(R.color.read_theme_4_bg);
                textColor = mContext.getResources().getColor(R.color.read_theme_4_text);
                break;
        }

//        catalogViewHolder.tv_title.setBackgroundColor(bgColor);
        catalogViewHolder.tv_title.setTextColor(textColor);
        catalogViewHolder.tv_title.setLineSpacing(mRowSpace,1f);
        catalogViewHolder.tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.clickItem(i);
            }
        });
    }
    int dpToPx(int dps) {
        return Math.round(mContext.getResources().getDisplayMetrics().density * dps);
    }
    @Override
    public int getItemCount() {
        return strings.size();
    }

    class CatalogViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        public CatalogViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title= itemView.findViewById(R.id.page_item);
        }

    }
}
