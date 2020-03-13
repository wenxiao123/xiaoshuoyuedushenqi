package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovels;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.DiscoveryNovelData;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.FenleiNovelActivity;

import java.util.List;

/**
 * @author WX
 * Created on 2019/12/21
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{

    private Context mContext;
    private List<CategoryNovels> mNovelDataList;
    private CategoryListener mListener;

    public interface CategoryListener {
        void clickNovel(String novelName);
        void clickMore(int position);
    }

    public CategoryAdapter(Context mContext, List<CategoryNovels> mNovelDataList,
                           CategoryListener mListener) {
        this.mContext = mContext;
        this.mNovelDataList = mNovelDataList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CategoryViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_category, null));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, final int i) {
        categoryViewHolder.categoryName.setText(mNovelDataList.get(i).getTitle());
        categoryViewHolder.moreTv.setText("更多");
        categoryViewHolder.moreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, FenleiNovelActivity.class));
            }
        });
        categoryViewHolder.novelList.setLayoutManager(new GridLayoutManager(mContext, 4));
        CategoryzyAdapter adapter = new CategoryzyAdapter(mContext,
                mNovelDataList.get(i).getData_list());
        adapter.setOnCategoryNovelListener(new CategoryzyAdapter.CategoryNovelListener() {
            @Override
            public void clickItem(int novelName) {

            }
        });
        categoryViewHolder.novelList.setAdapter(adapter);

        categoryViewHolder.moreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clickMore(i);
            }
        });
        categoryViewHolder.moreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clickMore(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNovelDataList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        TextView moreTv;
        ImageView moreIv;
        RecyclerView novelList;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.tv_item_category_category_name);
            moreTv = itemView.findViewById(R.id.tv_item_category_more);
            moreIv = itemView.findViewById(R.id.iv_item_category_more);
            novelList = itemView.findViewById(R.id.rv_item_category_novel_list);
        }
    }
}
