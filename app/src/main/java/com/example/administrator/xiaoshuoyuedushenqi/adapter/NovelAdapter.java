package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.base.BasePagingLoadAdapter;
import com.example.administrator.xiaoshuoyuedushenqi.constant.Constant;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.NovalInfo;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.widget.CornerTransform;

import java.util.List;

/**
 * @author Created on 2019/12/21
 */
public class NovelAdapter extends RecyclerView.Adapter<NovelAdapter.NovelViewHolder>{

    private Context mContext;
    List<NovalInfo> mList;
    RankAdapter.CatalogListener mListener;
    public NovelAdapter(Context mContext, List<NovalInfo> mList) {
        this.mList=mList;
        this.mContext=mContext;
    }

    boolean isRating;

    public void setRating(boolean rating) {
        isRating = rating;
    }
    public interface CatalogListener {
        void clickItem(int position);
    }


    public void setOnCatalogListener(RankAdapter.CatalogListener listener) {
        mListener = listener;
    }
    @NonNull
    @Override
    public NovelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NovelViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_novel, null));
    }

    @Override
    public void onBindViewHolder(@NonNull NovelViewHolder holder, int position) {
        NovelViewHolder novelViewHolder = (NovelViewHolder) holder;
        novelViewHolder.title.setText(mList.get(position).getTitle());
        novelViewHolder.author.setText(mList.get(position).getAuthor());
        novelViewHolder.shortInfo.setText(mList.get(position).getContent());
        String href;
        if (mList.get(position).getPic().contains("http")) {
            href=mList.get(position).getPic();
        } else {
            href=UrlObtainer.GetUrl() +"/"+ mList.get(position).getPic();
        }
        CornerTransform transformation = new CornerTransform(mContext, 10);
        Glide.with(mContext)
                .load(href)
                .apply(new RequestOptions()
                .error(R.drawable.cover_error)
                .transform(transformation))
                .into(novelViewHolder.cover);
        novelViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clickItem(mList.get(position).getId());
            }
        });
       // Log.e("QQQ", "onBindViewHolder: "+mList.get(position));
        novelViewHolder.cata.setText(" | " + mList.get(position).getCategory_name());
        if (isRating) {
            novelViewHolder.tv_item_rating.setVisibility(View.VISIBLE);
            novelViewHolder.tv_item_rating.setText(mList.get(position).getRating() + "分");
        } else {
            novelViewHolder.tv_item_rating.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface NovelListener {
        void clickItem(int novelName);
    }
    class NovelViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title;
        TextView author, cata;
        TextView shortInfo, tv_item_rating;

        public NovelViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.iv_item_novel_cover);
            title = itemView.findViewById(R.id.tv_item_novel_title);
            author = itemView.findViewById(R.id.tv_item_novel_author);
            shortInfo = itemView.findViewById(R.id.tv_item_novel_short_info);
            cata = itemView.findViewById(R.id.tv_item_novel_cata);
            tv_item_rating = itemView.findViewById(R.id.tv_item_rating);
        }
    }
}
