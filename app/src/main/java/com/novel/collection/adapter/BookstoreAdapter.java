package com.novel.collection.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.novel.collection.R;
import com.novel.collection.entity.bean.Catagorys;
import com.novel.collection.http.UrlObtainer;
import com.novel.collection.util.ImageLoaderUtils;
import com.novel.collection.widget.CornerTransform;

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
                R.layout.item_bookstore, viewGroup,false));
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
//        String href;
//        if(mDataList.get(i).getIcon()==null){
//            href="";
//        }else if(mDataList.get(i).getIcon().contains("http")){
//            href=mDataList.get(i).getIcon();
//        }else {
//            href=UrlObtainer.GetUrl()+"/"+mDataList.get(i).getIcon();
//        }
//        //Log.e("zzz", "onBindViewHolder: "+href);
//        CornerTransform transformation = new CornerTransform(mContext, 10);
//        Glide.with(mContext).load(href).apply(new RequestOptions()
//                .error(R.drawable.cover_error)
//        .transform(transformation))
//                //.into(simpleTarget);
//        .into(contentViewHolder.cover1);
        ImageLoaderUtils.display(mContext,contentViewHolder.cover1,mDataList.get(i).getIcon(),R.drawable.cover_error,R.drawable.cover_error);
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
