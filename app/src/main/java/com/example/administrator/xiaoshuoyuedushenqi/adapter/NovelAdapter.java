package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;

import java.util.List;

/**
 * @author
 * Created on 2019/12/21
 */
public class NovelAdapter extends BasePagingLoadAdapter<NovalInfo> {

    private NovelListener mListener;

    public NovelAdapter(Context mContext, List<NovalInfo> mList,
                        LoadMoreListener loadMoreListener, NovelListener novelListener) {
        super(mContext, mList, loadMoreListener);
        mListener = novelListener;
    }
    boolean isRating;

    public void setRating(boolean rating) {
        isRating = rating;
    }

    public interface NovelListener {
        void clickItem(int novelName);
    }

    @Override
    protected int getPageCount() {
        return Constant.NOVEL_PAGE_NUM;
    }

    @Override
    protected RecyclerView.ViewHolder setItemViewHolder(ViewGroup parent, int viewType) {
        return new NovelViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_novel, null));
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        NovelViewHolder novelViewHolder = (NovelViewHolder) holder;
        novelViewHolder.title.setText(mList.get(position).getTitle());
        novelViewHolder.author.setText(mList.get(position).getAuthor());
        novelViewHolder.shortInfo.setText(mList.get(position).getContent());
        Glide.with(mContext)
                .load(UrlObtainer.GetUrl()+mList.get(position).getPic())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.cover_place_holder)
                        .error(R.drawable.cover_error))
                .into(novelViewHolder.cover);
        novelViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clickItem(mList.get(position).getId());
            }
        });
        novelViewHolder.cata.setText(" | "+mList.get(position).getCategory_name());
        if (isRating){
            novelViewHolder.tv_item_rating.setVisibility(View.VISIBLE);
            novelViewHolder.tv_item_rating.setText(mList.get(position).getRating()+"分");
        }else {
            novelViewHolder.tv_item_rating.setVisibility(View.GONE);
        }
    }

    class NovelViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title;
        TextView author,cata;
        TextView shortInfo,tv_item_rating;

        public NovelViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.iv_item_novel_cover);
            title = itemView.findViewById(R.id.tv_item_novel_title);
            author = itemView.findViewById(R.id.tv_item_novel_author);
            shortInfo = itemView.findViewById(R.id.tv_item_novel_short_info);
            cata=itemView.findViewById(R.id.tv_item_novel_cata);
            tv_item_rating=itemView.findViewById(R.id.tv_item_rating);
        }
    }
}
