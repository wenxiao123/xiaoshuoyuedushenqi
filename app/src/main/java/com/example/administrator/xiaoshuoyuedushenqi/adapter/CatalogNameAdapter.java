package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Cataloginfo;
import com.example.administrator.xiaoshuoyuedushenqi.weyue.widget.page.TxtChapter;

import java.util.List;

/**
 * @author
 * Created on 2019/11/17
 */
public class CatalogNameAdapter extends RecyclerView.Adapter<CatalogNameAdapter.CatalogViewHolder>
    implements View.OnClickListener{
    private static final String TAG = "CatalogAdapter";

    private Context mContext;
    private List<TxtChapter> mChapterNameList;

    private CatalogListener mListener;
    public void setPosition(int position) {
        this.position = position;
    }

    int position;
    @Override
    public void onClick(View v) {
        int pos = (int) v.getTag();
        Log.d(TAG, "onClick: pos = " + pos);
        mListener.clickItem(pos);
    }

    public interface CatalogListener {
        void clickItem(int position);
    }

    public void setOnCatalogListener(CatalogListener listener) {
        mListener = listener;
    }

    public CatalogNameAdapter(Context mContext, List<TxtChapter> txtChapters) {
        this.mContext = mContext;
        this.mChapterNameList = txtChapters;
    }

    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CatalogViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_catalog, null));
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder catalogViewHolder, final int i) {
        catalogViewHolder.chapterName.setText(mChapterNameList.get(i).getTitle());
//        catalogViewHolder.chapterName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.clickItem(i);
//            }
//        });
        if(position==i){
            catalogViewHolder.chapterName.setTextColor(mContext.getResources().getColor(R.color.red));
        }else {
            catalogViewHolder.chapterName.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        catalogViewHolder.chapterName.setOnClickListener(this);
        catalogViewHolder.chapterName.setTag(i);
    }

    @Override
    public int getItemCount() {
        return mChapterNameList.size();
    }

    class CatalogViewHolder extends RecyclerView.ViewHolder{
        TextView chapterName;

        public CatalogViewHolder(@NonNull View itemView) {
            super(itemView);
            chapterName = itemView.findViewById(R.id.tv_item_catalog_chapter_name);
        }

    }
}
