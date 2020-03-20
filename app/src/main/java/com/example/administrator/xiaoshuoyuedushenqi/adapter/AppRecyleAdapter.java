package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;

/**
 * @author
 * Created on 2019/11/17
 */
public class AppRecyleAdapter extends RecyclerView.Adapter<AppRecyleAdapter.CatalogViewHolder>
        implements View.OnClickListener {
    private static final String TAG = "CatalogAdapter";

    private Context mContext;
    private int[] ints;
    String[] strings;
    private CatalogListener mListener;

    @Override
    public void onClick(View v) {
        int pos = (int) v.getTag();
        mListener.clickItem(pos);
    }

    public interface CatalogListener {
        void clickItem(int position);
    }

    public void setOnCatalogListener(CatalogListener listener) {
        mListener = listener;
    }

    public AppRecyleAdapter(Context mContext, int[] mChapterNameList, String[] strings) {
        this.mContext = mContext;
        this.strings = strings;
        this.ints = mChapterNameList;
    }

    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CatalogViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_app, null));
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder catalogViewHolder, final int i) {
        catalogViewHolder.tv_title.setText(strings[i]);
        catalogViewHolder.iv_title.setImageResource(ints[i]);
        catalogViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.clickItem(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ints.length;
    }

    class CatalogViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        ImageView iv_title;
        public CatalogViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_title = itemView.findViewById(R.id.iv_item_bookshelf_novel_cover1);
            tv_title= itemView.findViewById(R.id.tv_item_bookshelf_novel_name);
        }

    }
}
