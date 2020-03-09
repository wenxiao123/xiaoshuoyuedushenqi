package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.util.ScreenUtil;
import com.example.administrator.xiaoshuoyuedushenqi.widget.FlowLayout;

import java.util.List;

/**
 * @author WX
 * Created on 2019/11/11
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context mContext;
    private List<String> mContentList;
    private HistoryAdapterListener mListener;

    public interface HistoryAdapterListener {
        void clickWord(String word);    // 点击历史搜索词语
    }

    public void setOnHistoryAdapterListener(HistoryAdapterListener listener) {
        mListener = listener;
    }

    public HistoryAdapter(Context mContext, List<String> mContentList) {
        this.mContext = mContext;
        this.mContentList = mContentList;
    }

//    @Override
//    public HistoryViewHolder onCreateViewHolder(ViewGroup parent) {
//        View view = LayoutInflater.from(mContext)
//                .inflate(R.layout.item_history, parent, false);
//        // 给 View 设置 margin
//        ViewGroup.MarginLayoutParams mlp = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
//        int margin = ScreenUtil.dip2px(mContext, 5);
//        mlp.setMargins(margin, margin, margin, margin);
//        view.setLayoutParams(mlp);
//
//        return new HistoryViewHolder(view);
//    }
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HistoryViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_history, null));
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, final int position) {
        String s = mContentList.get(position);
        // 如果超过 8 个字符，只取前 8 个
        if (s.length() > 8) {
            s = s.substring(0, 8) + "...";
        }
        holder.content.setText(s);
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.clickWord(mContentList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContentList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView content;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.tv_item_history_content);
        }
    }

    public void updateList(List<String> list) {
        mContentList = list;
    }
}
