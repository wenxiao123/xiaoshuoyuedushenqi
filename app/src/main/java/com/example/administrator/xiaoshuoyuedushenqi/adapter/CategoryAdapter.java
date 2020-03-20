package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.CategoryNovels;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.FenleiNovelActivity;
import com.example.administrator.xiaoshuoyuedushenqi.view.activity.NovelIntroActivity;

import java.util.List;
import java.util.Random;

/**
 * @author
 * Created on 2019/12/21
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context mContext;
    private List<CategoryNovels> mNovelDataList;
    private CategoryListener mListener;
    int type=1;

    public void setType(int type) {
        this.type = type;
    }

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
        if(type==6) {
            categoryViewHolder.categoryName.setText(mNovelDataList.get(i).getTitle()+"榜");
        }
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
                Intent intent = new Intent(mContext, NovelIntroActivity.class);
                // 传递小说名，进入搜查页后直接显示该小说的搜查结果
                intent.putExtra("pid", mNovelDataList.get(i).getData_list().get(novelName).getId() + "");
                mContext.startActivity(intent);
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
        Random myRandom = new Random();
        int ranColor = 0xff000000 | myRandom.nextInt(0x00ffffff);
        categoryViewHolder.colorv.setBackgroundColor(ranColor);
    }

    @Override
    public int getItemCount() {
        return mNovelDataList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        TextView moreTv;
        ImageView moreIv;
        View colorv;
        RecyclerView novelList;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.tv_item_category_category_name);
            moreTv = itemView.findViewById(R.id.tv_item_category_more);
            moreIv = itemView.findViewById(R.id.iv_item_category_more);
            novelList = itemView.findViewById(R.id.rv_item_category_novel_list);
            colorv = itemView.findViewById(R.id.color);
        }
    }
}
