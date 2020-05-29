package com.novel.collection.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novel.collection.R;
import com.novel.collection.util.ToastUtil;

import java.util.List;

/**
 * @author
 * Created on 2019/11/11
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>  {

    private Context mContext;
    private List<String> mContentList;
    private HistoryAdapterListener mListener;

    public interface HistoryAdapterListener {
        void clickWord(int word);    // 点击历史搜索词语
        void longclick(int word);
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
        HistoryViewHolder historyViewHolder=holder;
        String s = mContentList.get(position);
        // 如果超过 8 个字符，只取前 8 个
        if (s.length() > 8) {
            s = s.substring(0, 8) + "...";
        }
        historyViewHolder.content.setText(s);
        historyViewHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.clickWord(position);
                }
            }
        });
        historyViewHolder.content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null) {
                    mListener.longclick(position);
                }
                return false;
            }
        });
        if(position==mContentList.size()-1){
            historyViewHolder.v_line.setVisibility(View.GONE);
        }else {
            historyViewHolder.v_line.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mContentList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        ImageView img;
        View v_line;
        public HistoryViewHolder(View itemView) {
            super(itemView);
            img= itemView.findViewById(R.id.img);
            content = itemView.findViewById(R.id.tv_item_history_content);
            v_line=itemView.findViewById(R.id.v_line);
        }
    }

    public void updateList(List<String> list) {
        mContentList = list;
    }
}
