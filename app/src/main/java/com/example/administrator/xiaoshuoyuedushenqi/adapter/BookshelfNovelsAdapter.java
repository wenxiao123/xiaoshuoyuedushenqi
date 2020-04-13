package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.util.Log;
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
import com.example.administrator.xiaoshuoyuedushenqi.widget.CornerTransform;

import java.text.NumberFormat;
import java.util.List;

/**
 * @author Created on 2020/2/28
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
        this.show = show;
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
                contentViewHolder.iv_cover.setVisibility(View.VISIBLE);
            } else {
                contentViewHolder.checkBox.setImageResource(R.mipmap.sys_select);
                contentViewHolder.iv_cover.setVisibility(View.GONE);
            }
        } else {
            contentViewHolder.iv_cover.setVisibility(View.GONE);
            //contentViewHolder.v_background.setVisibility(View.GONE);
            contentViewHolder.checkBox.setVisibility(View.GONE);
        }
        if (mDataList.get(i).getName() != null) {
            String name = mDataList.get(i).getName().replace(".txt", "");
            contentViewHolder.name.setText(name);
        }
        CornerTransform transformation = new CornerTransform(mContext, 10);
        if (mDataList.get(i).getType() == 0) {  // 网络小说
            Glide.with(mContext)
                    .load(mDataList.get(i).getCover())
                    .apply(new RequestOptions()
//                           .placeholder(R.drawable.cover_place_holder)
                            .error(R.drawable.cover_error)
                            .transform(transformation))
                    .into(contentViewHolder.cover);
            contentViewHolder.img_add.setVisibility(View.GONE);
            contentViewHolder.tv_status.setVisibility(View.VISIBLE);
            contentViewHolder.tv_position.setVisibility(View.VISIBLE);
        } else if (mDataList.get(i).getType() == 1) {    // 本地 txt 小说\
            Glide.with(mContext)
                    .load(mDataList.get(i).getCover())
                    .apply(new RequestOptions()
                            .error(R.drawable.local_txt)
                            .transform(transformation))
                    .into(contentViewHolder.cover);
            contentViewHolder.img_add.setVisibility(View.GONE);
            contentViewHolder.tv_status.setVisibility(View.VISIBLE);
            contentViewHolder.tv_position.setVisibility(View.VISIBLE);
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
            contentViewHolder.tv_status.setVisibility(View.VISIBLE);
            contentViewHolder.tv_position.setVisibility(View.VISIBLE);
        } else if (mDataList.get(i).getType() == -1) {
            Glide.with(mContext)
                    .load(R.drawable.grey)
                    .apply(new RequestOptions()
                            .error(R.drawable.cover_error)
                            .transform(transformation))
                    .into(contentViewHolder.cover);
            contentViewHolder.img_add.setVisibility(View.VISIBLE);
            contentViewHolder.tv_status.setVisibility(View.GONE);
            contentViewHolder.tv_position.setVisibility(View.GONE);
        }

        contentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsMultiDelete) {
                    if (mCheckedList.get(i) == true) {
                        contentViewHolder.checkBox.setImageResource(R.mipmap.sys_select);
                        //contentViewHolder.cover.setAlpha(1f);
                        contentViewHolder.iv_cover.setVisibility(View.GONE);
                        mCheckedList.set(i, false);
                    } else {
                        contentViewHolder.iv_cover.setVisibility(View.VISIBLE);
                        contentViewHolder.checkBox.setImageResource(R.mipmap.sys_selected);
                        //contentViewHolder.cover.setAlpha(0.5f);
                        mCheckedList.set(i, true);
                    }
                    int z = 0;
                    for (int i = 0; i < mCheckedList.size(); i++) {
                        if (mCheckedList.get(i) == true) {
                            z++;
                        }
                    }
                    show.show(z);
                } else {
                    mListener.clickItem(i);
                }
            }
        });

        if (mDataList.get(i).getStatus() == null || mDataList.get(i).getStatus().equals("1")) {
            contentViewHolder.tv_status.setText("完结");
        } else {
            contentViewHolder.tv_status.setText("连载");
        }

        if (mDataList.get(i).getType() == 1 && mDataList.get(i).getSecondPosition() != 0) {
            float chpid =  Float.parseFloat(mDataList.get(i).getChapterid());
            float wight = mDataList.get(i).getWeight();
            float prent = (chpid / wight) * 100;
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(2);
            if (mDataList.get(i).getSecondPosition() > 2) {
                contentViewHolder.tv_position.setText("已读 "+nf.format(prent) + "%");
            } else {
                contentViewHolder.tv_position.setText("未读");
            }
//            NumberFormat nf = NumberFormat.getNumberInstance();
//            nf.setMaximumFractionDigits(2);
//            float p = Float.parseFloat(progress.replace("%", "")) / 100;
//            if(weigh!=0) {
//                float v = (((float) mChapterIndex / (float) weigh) * 100 + p * (1 / weigh));
//                mNovelProgressTv.setText(nf.format(v) + "%");
//            }else {
//                mNovelProgressTv.setText(0 + "%");
//            }
        } else {
            contentViewHolder.tv_position.setText("已读 "+0 + "%");
        }

        if (mDataList.get(i).getType() == 0) {
            if (mDataList.get(i).getChapterid() == null || mDataList.get(i).getWeight() == 0) {
                contentViewHolder.tv_position.setText("未读");
            } else {
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(2);
                float chpid = Float.parseFloat(mDataList.get(i).getChapterid());
                float wight = mDataList.get(i).getWeight();
                float prent = (chpid/wight) * 100;
                if (mDataList.get(i).getPosition() > 1) {
                    contentViewHolder.tv_position.setText("已读 "+nf.format(prent) + "%");
                } else if(mDataList.get(i).getPosition()<=1&&mDataList.get(i).getChapterid().equals("1")){
                   // contentViewHolder.tv_position.setText(nf.format(prent) + "%");
                    contentViewHolder.tv_position.setText("未读");
                }else {
                    contentViewHolder.tv_position.setText("已读 "+nf.format(prent) + "%");
                }
            }
        }

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
        ImageView checkBox, iv_cover;
        ImageView cover, img_add;
        TextView name, tv_status, tv_position;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_cover = itemView.findViewById(R.id.iv_cover);
            checkBox = itemView.findViewById(R.id.cb_item_bookshelf_novel_checked);
            cover = itemView.findViewById(R.id.iv_item_bookshelf_novel_cover);
            img_add = itemView.findViewById(R.id.img_add);
            name = itemView.findViewById(R.id.tv_item_bookshelf_novel_name);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_position = itemView.findViewById(R.id.tv_position);
        }
    }

    public void setIsMultiDelete(boolean mIsMultiDelete) {
        this.mIsMultiDelete = mIsMultiDelete;
    }
}
