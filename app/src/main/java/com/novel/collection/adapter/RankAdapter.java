package com.novel.collection.adapter;

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
import com.novel.collection.R;
import com.novel.collection.base.BasePagingLoadAdapter;
import com.novel.collection.constant.Constant;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.util.ImageLoaderUtils;
import com.novel.collection.widget.CornerTransform;

import java.util.List;

/**
 * @author
 * Created on 2019/12/21
 */
public class RankAdapter extends RecyclerView.Adapter<RankAdapter.NovelViewHolder> {

    private Context mContext;
    List<Noval_details> mList;
    CatalogListener mListener;
    public RankAdapter(Context mContext, List<Noval_details> mList) {
      this.mContext=mContext;
      this.mList=mList;
    }
    public interface CatalogListener {
        void clickItem(int position);
    }


    public void setOnCatalogListener(CatalogListener listener) {
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
        novelViewHolder.shortInfo.setText(mList.get(position).getContent());
        if(position==0){
            novelViewHolder.iv_rank.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(R.mipmap.no_1))
                    .apply(new RequestOptions()
                            )
                    .into(novelViewHolder.iv_rank);
        }else if(position==1){
            novelViewHolder.iv_rank.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(R.mipmap.no_2))
                    .apply(new RequestOptions()
                            )
                    .into(novelViewHolder.iv_rank);
        }else if(position==2){
            novelViewHolder.iv_rank.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(R.mipmap.no_3))
                    .apply(new RequestOptions()
                            )
                    .into(novelViewHolder.iv_rank);
        }else {
            novelViewHolder.iv_rank.setVisibility(View.GONE);
        }
//        String href;
//        if(mList.get(position).getPic().contains("http")){
//            href=mList.get(position).getPic();
//        }else {
//            href=UrlObtainer.GetUrl()+"/"+mList.get(position).getPic();
//        }
//        CornerTransform transformation = new CornerTransform(mContext, 10);
//        Glide.with(mContext)
//                .load(href)
//                .apply(new RequestOptions()
//                        .error(R.drawable.cover_error)
//                .transform(transformation))
//                .into(novelViewHolder.cover);
        ImageLoaderUtils.display(mContext,novelViewHolder.cover,mList.get(position).getPic(),R.drawable.cover_error,R.drawable.cover_error);
        novelViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clickItem(mList.get(position).getId());
            }
        });
        novelViewHolder.author.setText(mList.get(position).getCategory_name());
        novelViewHolder.cata.setText(" | "+mList.get(position).getAuthor());
        novelViewHolder.tv_item_rating.setVisibility(View.GONE);
        if(position==0){
            novelViewHolder.v_line.setVisibility(View.GONE);
        }else {
            novelViewHolder.v_line.setVisibility(View.VISIBLE);
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
        ImageView cover,iv_rank;
        TextView title;
        TextView author,cata;
        View v_line;
        TextView shortInfo,tv_item_rating;

        public NovelViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.iv_item_novel_cover);
            iv_rank = itemView.findViewById(R.id.iv_rank);
            tv_item_rating=itemView.findViewById(R.id.tv_item_rating);
            title = itemView.findViewById(R.id.tv_item_novel_title);
            author = itemView.findViewById(R.id.tv_item_novel_author);
            shortInfo = itemView.findViewById(R.id.tv_item_novel_short_info);
            cata=itemView.findViewById(R.id.tv_item_novel_cata);
            v_line=itemView.findViewById(R.id.v_line);
        }
    }
}
