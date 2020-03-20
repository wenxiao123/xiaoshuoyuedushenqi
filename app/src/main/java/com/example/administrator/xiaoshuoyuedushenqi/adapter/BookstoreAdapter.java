package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Catagorys;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;

import java.util.List;

/**
 * @author
 * Created on 2020/2/28
 */
public class BookstoreAdapter extends RecyclerView.Adapter {
    private static final String TAG = "BookshelfNovelsAdapter";

    private Context mContext;
    private List<Catagorys> mDataList;
    private BookshelfNovelListener mListener;
    public interface BookshelfNovelListener {
        void clickItem(int position);
        void longClick(int position);
    }

    public BookstoreAdapter(Context mContext, List<Catagorys> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    public BookstoreAdapter(Context mContext, List<Catagorys> mDataList,
                            BookshelfNovelListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ContentViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.item_bookstore, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final ContentViewHolder contentViewHolder = (ContentViewHolder) viewHolder;
        if(mDataList.get(i).getTitle()!=null) {
            String name = mDataList.get(i).getTitle().replace(".txt", "");
            contentViewHolder.name.setText(name);
        }
        SimpleTarget<Drawable> simpleTarget = new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                contentViewHolder.cover1.setBackground(resource);
            }
        };
        Glide.with(mContext).load(UrlObtainer.GetUrl()+mDataList.get(i).getIcon()).apply(new RequestOptions()
                .placeholder(R.drawable.cover_place_holder)
                .error(R.drawable.cover_error))
                //.into(simpleTarget);
        .into(contentViewHolder.cover1);
        contentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clickItem(i);
            }
        });

        contentViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.longClick(i);
                return true;
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        ImageView cover1;
        TextView name;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            cover1 = itemView.findViewById(R.id.iv_item_bookshelf_novel_cover1);
            name = itemView.findViewById(R.id.tv_item_bookshelf_novel_name);
        }
    }
}
