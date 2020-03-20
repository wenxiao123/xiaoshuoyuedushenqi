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
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_Readcored;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.NovelSourceData;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;

import java.util.List;

/**
 * @author
 * Created on 2019/11/9
 */
public class NovelSourceAdapter extends
        RecyclerView.Adapter<NovelSourceAdapter.NovelSourceViewHolder> {

    private static final String TAG = "NovelSourceAdapter";

    private Context mContext;
    private List<Noval_Readcored> mNovelSourceDataList;
    private NovelSourceListener mListener;

    public void setOnNovelSourceListener(NovelSourceListener listener) {
        mListener = listener;
    }

    public interface NovelSourceListener {
        void clickItem(int position);
        void longclickItem(int position);
    }

    public NovelSourceAdapter(Context mContext, List<Noval_Readcored> mNovelSourceDataList) {
        this.mContext = mContext;
        this.mNovelSourceDataList = mNovelSourceDataList;
    }

    @NonNull
    @Override
    public NovelSourceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new NovelSourceViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_novel_source, null));
    }

    @Override
    public void onBindViewHolder(@NonNull NovelSourceViewHolder novelSourceViewHolder, final int i) {
        Glide.with(mContext)
                .load(UrlObtainer.GetUrl()+mNovelSourceDataList.get(i).getPic())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.cover_place_holder)
                        .error(R.drawable.cover_error))
                .into(novelSourceViewHolder.cover);
        novelSourceViewHolder.name.setText(mNovelSourceDataList.get(i).getTitle());
        // 作者可能为空
        String str = mNovelSourceDataList.get(i).getAuthor();
        String author = (str == null || str.equals(""))? "未知" : str;
        if(mNovelSourceDataList.get(i).getStatus().equals("1")){
            novelSourceViewHolder.author.setText(author+" | 已完本 _ "+mNovelSourceDataList.get(i).getWeigh()+"章");
        }else {
            novelSourceViewHolder.author.setText(author+" | 连载中 _ "+mNovelSourceDataList.get(i).getWeigh()+"章");
        }
        novelSourceViewHolder.introduce.setText("阅读至"+mNovelSourceDataList.get(i).getChapter_name());
        //novelSourceViewHolder.webSite.setText(mNovelSourceDataList.get(i).getUrl());
        if(mNovelSourceDataList.get(i).getIs_che().equals("1")){
            novelSourceViewHolder.tv_item_bookshelf.setText("已加入书架");
            novelSourceViewHolder.tv_item_bookshelf.setTextColor(mContext.getResources().getColor(R.color.gray));
            novelSourceViewHolder.tv_item_bookshelf.setBackground(mContext.getResources().getDrawable(R.drawable.bachground_btn));
        }else {
            novelSourceViewHolder.tv_item_bookshelf.setText("加入书架");
            novelSourceViewHolder.tv_item_bookshelf.setTextColor(mContext.getResources().getColor(R.color.red));
            novelSourceViewHolder.tv_item_bookshelf.setBackground(mContext.getResources().getDrawable(R.drawable.bachground_red));
        }
        novelSourceViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clickItem(i);
            }
        });
        novelSourceViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mListener.longclickItem(i);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: size = " + mNovelSourceDataList.size());
        return mNovelSourceDataList.size();
    }

    class NovelSourceViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView name;
        TextView author;
        TextView introduce;
        TextView webSite,tv_item_bookshelf;

        public NovelSourceViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.iv_item_novel_source_cover);
            name = itemView.findViewById(R.id.tv_item_novel_source_name);
            author = itemView.findViewById(R.id.tv_item_novel_source_author);
            introduce = itemView.findViewById(R.id.tv_item_novel_source_introduce);
            webSite = itemView.findViewById(R.id.tv_item_novel_source_web_site);
            tv_item_bookshelf=itemView.findViewById(R.id.tv_item_bookshelf);
        }
    }
}
