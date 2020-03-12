package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.NovelSourceData;

import java.util.List;

/**
 * @author WX
 * Created on 2019/11/9
 */
public class NovelSearchAdapter extends
        RecyclerView.Adapter<NovelSearchAdapter.NovelSourceViewHolder> {

    private static final String TAG = "NovelSourceAdapter";

    private Context mContext;
    private List<String> mNovelSourceDataList;
    private NovelSourceListener mListener;
    private String name_search;
    public void setOnNovelSourceListener(NovelSourceListener listener) {
        mListener = listener;
    }

    public interface NovelSourceListener {
        void clickItem(int position);
    }

    public NovelSearchAdapter(Context mContext, List<String> mNovelSourceDataList,String content) {
        this.mContext = mContext;
        this.mNovelSourceDataList = mNovelSourceDataList;
        name_search=content;
    }

    @NonNull
    @Override
    public NovelSourceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new NovelSourceViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_search, null));
    }

    @Override
    public void onBindViewHolder(@NonNull NovelSourceViewHolder novelSourceViewHolder, final int i) {
        novelSourceViewHolder.name.setText(mNovelSourceDataList.get(i));
        setColor(novelSourceViewHolder.name,mNovelSourceDataList.get(i),name_search,mContext.getResources().getColor(R.color.red_aa));
        novelSourceViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clickItem(i);
            }
        });
    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: size = " + mNovelSourceDataList.size());
        return mNovelSourceDataList.size();
    }

    class NovelSourceViewHolder extends RecyclerView.ViewHolder {
        TextView name;


        public NovelSourceViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_search);
        }
    }
    private void setColor(TextView view, String fulltext, String subtext, int color) {
        view.setText(fulltext, TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable) view.getText();
        int i = fulltext.indexOf(subtext);
        str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);}
}
