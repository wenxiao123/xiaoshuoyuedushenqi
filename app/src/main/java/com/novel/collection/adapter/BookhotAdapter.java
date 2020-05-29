package com.novel.collection.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novel.collection.R;
import com.novel.collection.constant.EventBusCode;
import com.novel.collection.entity.bean.Wheel;
import com.novel.collection.entity.eventbus.Event;
import com.novel.collection.entity.eventbus.SearchUpdateInputEvent;
import com.novel.collection.util.EventBusUtil;

import java.util.List;

/**
 * @author
 * Created on 2020/2/28
 */
public class BookhotAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<Wheel> mDataList;
    public BookhotAdapter(Context mContext, List<Wheel> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

        @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view;
            view = LayoutInflater.from(mContext).inflate(R.layout.item_hot_book, viewGroup, false);
            return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final ContentViewHolder contentViewHolder = (ContentViewHolder) viewHolder;
        final String name=mDataList.get(i).getTitle();
        contentViewHolder.name.setText(name);
        contentViewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event<SearchUpdateInputEvent> event = new Event<>(EventBusCode.SEARCH_UPDATE_INPUT,
                        new SearchUpdateInputEvent(name));
                EventBusUtil.sendEvent(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_hot_rank_rank_name);
        }
    }
}
