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
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;
import com.example.administrator.xiaoshuoyuedushenqi.widget.CornerTransform;

import java.util.List;

/**
 * @author
 * Created on 2019/12/21
 */
public class CategoryinfoAdapter extends RecyclerView.Adapter<CategoryinfoAdapter.CategoryNovelViewHolder> {

    private Context mContext;
    List<Noval_details> novalDetails;
    private CategoryNovelListener mListener;

    public interface CategoryNovelListener {
        void clickItem(int novelName);
    }

    public void setOnCategoryNovelListener(CategoryNovelListener listener) {
        mListener = listener;
    }

    public CategoryinfoAdapter(Context mContext,  List<Noval_details> novalDetails) {
        this.mContext = mContext;
        this.novalDetails = novalDetails;

    }

    @NonNull
    @Override
    public CategoryNovelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CategoryNovelViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_category_novel, null));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryNovelViewHolder categoryNovelViewHolder, final int i) {
        String url;
        if(novalDetails.get(i).getPic().contains("http")){
            url=novalDetails.get(i).getPic();
        }else {
            url=UrlObtainer.GetUrl() +"/"+novalDetails.get(i).getPic();
        }
        CornerTransform transformation = new CornerTransform(mContext, 10);
        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions()
                        .error(R.drawable.cover_error)
                .transform(transformation).dontAnimate())
                .into(categoryNovelViewHolder.cover);
        categoryNovelViewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clickItem(i);
            }
        });
        categoryNovelViewHolder.name.setText(novalDetails.get(i).getTitle());
        categoryNovelViewHolder.author.setText(novalDetails.get(i).getAuthor());
    }

    @Override
    public int getItemCount() {
        return novalDetails.size();
    }

    class CategoryNovelViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView name,author;

        public CategoryNovelViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.iv_item_category_novel_cover);
            name = itemView.findViewById(R.id.tv_item_category_novel_name);
            author=itemView.findViewById(R.id.tv_item_category_author);
        }
    }
}
