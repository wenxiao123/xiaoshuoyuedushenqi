package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookshelfNovelDbData;
import com.example.administrator.xiaoshuoyuedushenqi.interfaces.Delet_book_show;
import com.example.administrator.xiaoshuoyuedushenqi.util.FileUtil;

import java.util.List;

/**
 * @author
 * Created on 2020/2/28
 */
public class BookshelfNovelsAdapter extends RecyclerView.Adapter {
    private static final String TAG = "BookshelfNovelsAdapter";

    private Context mContext;
    private List<BookshelfNovelDbData> mDataList;
    private List<Boolean> mCheckedList;
    private BookshelfNovelListener mListener;
    private boolean mIsMultiDelete = false;   // 是否正在进行多选删除
    Delet_book_show show;
    public interface BookshelfNovelListener {
        void clickItem(int position);
        void longClick(int position);
    }

    public BookshelfNovelsAdapter(Context mContext, List<BookshelfNovelDbData> mDataList,
                                  List<Boolean> mCheckedList, BookshelfNovelListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mCheckedList = mCheckedList;
        this.mListener = mListener;
    }

    public BookshelfNovelsAdapter(Context mContext, List<BookshelfNovelDbData> mDataList,
                                  List<Boolean> mCheckedList, BookshelfNovelListener mListener, Delet_book_show show) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mCheckedList = mCheckedList;
        this.mListener = mListener;
        this.show=show;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ContentViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.item_bookshelf_novel, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final ContentViewHolder contentViewHolder = (ContentViewHolder) viewHolder;
        // 多选删除时显示 CheckBox
        if (mIsMultiDelete) {
            contentViewHolder.checkBox.setVisibility(View.VISIBLE);
            // 先设置 CheckBox 的状态，解决 RecyclerView 对 CheckBox 的复用所造成的影响
            if (mCheckedList.get(i)) {
                contentViewHolder.checkBox.setImageResource(R.mipmap.sys_selected);
                contentViewHolder.cover.setAlpha(0.5f);
            } else {
                contentViewHolder.cover.setAlpha(1f);
                contentViewHolder.checkBox.setImageResource(R.mipmap.sys_select);
            }
        } else {
            contentViewHolder.checkBox.setVisibility(View.GONE);
        }
        if(mDataList.get(i).getName()!=null) {
            String name = mDataList.get(i).getName().replace(".txt", "");
            contentViewHolder.name.setText(name);
        }
        if (mDataList.get(i).getType() == 0) {  // 网络小说
            Glide.with(mContext)
                    .load(mDataList.get(i).getCover())
                    .apply(new RequestOptions()
//                           .placeholder(R.drawable.cover_place_holder)
                           .error(R.drawable.cover_error))
                    .into(contentViewHolder.cover);
            contentViewHolder.img_add.setVisibility(View.GONE);
        } else if (mDataList.get(i).getType() == 1){    // 本地 txt 小说
            Glide.with(mContext)
                    .load(R.drawable.local_txt)
                    .apply(new RequestOptions()
//                           .placeholder(R.drawable.cover_place_holder)
                            .error(R.drawable.cover_error))
                    .into(contentViewHolder.cover);
            contentViewHolder.img_add.setVisibility(View.GONE);
            //contentViewHolder.cover.setImageResource(R.drawable.local_txt);
            contentViewHolder.img_add.setVisibility(View.GONE);
        } else if (mDataList.get(i).getType() == 2) {   // 本地 epub 小说
            if (mDataList.get(i).getCover().equals("")) {
                contentViewHolder.cover.setImageResource(R.drawable.local_epub);
            } else {
                String coverPath = mDataList.get(i).getCover();
                Bitmap bitmap = FileUtil.loadLocalPicture(coverPath);
                if (bitmap != null) {
                    contentViewHolder.cover.setImageBitmap(bitmap);
                } else {
                    contentViewHolder.cover.setImageResource(R.drawable.local_epub);
                }
            }
            contentViewHolder.img_add.setVisibility(View.GONE);
        }else if(mDataList.get(i).getType() == -1){
            Glide.with(mContext)
                    .load(R.drawable.grey)
                    .apply(new RequestOptions()
//                           .placeholder(R.drawable.cover_place_holder)
                            .error(R.drawable.cover_error))
                    .into(contentViewHolder.cover);
            //contentViewHolder.cover.setBackgroundResource(R.drawable.bachground_line);
            contentViewHolder.img_add.setVisibility(View.VISIBLE);
        }

        contentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsMultiDelete) {
                    if (contentViewHolder.cover.getAlpha()==0.5f) {
                        contentViewHolder.checkBox.setImageResource(R.mipmap.sys_select);
                        contentViewHolder.cover.setAlpha(1f);
                        mCheckedList.set(i, false);
                    } else {
                        contentViewHolder.checkBox.setImageResource(R.mipmap.sys_selected);
                        contentViewHolder.cover.setAlpha(0.5f);
                        mCheckedList.set(i, true);
                    }
                    int z=0;
                    for(int i=0;i<mCheckedList.size();i++){
                       if(mCheckedList.get(i)==true){
                           z++;
                       }
                    }
                     show.show(z);
                } else {
                    mListener.clickItem(i);
                }
            }
        });

        contentViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mIsMultiDelete) {
                    return false;
                }
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
        ImageView checkBox;
        ImageView cover,img_add;
        TextView name;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cb_item_bookshelf_novel_checked);
            cover = itemView.findViewById(R.id.iv_item_bookshelf_novel_cover);
            img_add=itemView.findViewById(R.id.img_add);
            name = itemView.findViewById(R.id.tv_item_bookshelf_novel_name);
        }
    }

    public void setIsMultiDelete(boolean mIsMultiDelete) {
        this.mIsMultiDelete = mIsMultiDelete;
    }
}
