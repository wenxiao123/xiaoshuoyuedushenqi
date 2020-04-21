package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.entity.data.BookmarkNovelDbData;

import java.util.List;

/**
 * @author
 * Created on 2019/11/17
 */
public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.CatalogViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "CatalogAdapter";

    private Context mContext;
    private List<BookmarkNovelDbData> mChapterNameList;
    List<String> strings;
    List longs;
    private CatalogListener mListener;
    private CatalogLongListener mLongListener;
    @Override
    public void onClick(View v) {
        int pos = (int) v.getTag();
        Log.d(TAG, "onClick: pos = " + pos);
        mListener.clickItem(pos);
    }

    @Override
    public boolean onLongClick(View v) {
        int pos = (int) v.getTag();
        Log.d(TAG, "onClick: pos = " + pos);
        mLongListener.clickItem(pos);
        return true;
    }

    public interface CatalogListener {
        void clickItem(int position);
    }

    public interface CatalogLongListener {
        void clickItem(int position);
    }

    public void setOnCatalogListener(CatalogListener listener) {
        mListener = listener;
    }

    public void setOnCatalogLongListener(CatalogLongListener listener) {
        mLongListener = listener;
    }

    public BookMarkAdapter(Context mContext, List<BookmarkNovelDbData> mChapterNameList, List<String> strings, List longs) {
        this.mContext = mContext;
        this.strings = strings;
        this.longs = longs;
        this.mChapterNameList = mChapterNameList;
    }

    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CatalogViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_bookmark, null));
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder catalogViewHolder, final int i) {
        if(mChapterNameList.get(i).getType()==1) {
            int z = 0;
            for (int j = 0; j < longs.size(); j++) {
                if (mChapterNameList.get(i).getPosition() <= (int) longs.get(0)) {
                    z = 0;
                    break;
                } else if (mChapterNameList.get(i).getPosition() < (int) longs.get(j)) {
                    z = j - 1;
                    break;
                }
            }
            String title = mChapterNameList.get(i).getContent();
            //catalogViewHolder.chapterName.setText((i + 1) + ". " + title.substring(3, title.length()));
            catalogViewHolder.chapterName.setText(mChapterNameList.get(i).getName());
            catalogViewHolder.chapterName.setTag(i);
        }else if(mChapterNameList.get(i).getType()==0){
            catalogViewHolder.chapterName.setText(mChapterNameList.get(i).getName());
            catalogViewHolder.chapterName.setTag(i);
        }
        catalogViewHolder.content.setText(mChapterNameList.get(i).getContent());
        catalogViewHolder.time.setText(mChapterNameList.get(i).getTime());
        catalogViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.clickItem(i);
            }
        });
        catalogViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mLongListener.clickItem(i);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mChapterNameList.size();
    }

    class CatalogViewHolder extends RecyclerView.ViewHolder {
        TextView chapterName, content, time;

        public CatalogViewHolder(@NonNull View itemView) {
            super(itemView);
            chapterName = itemView.findViewById(R.id.tv_item_catalog_chapter_name);
            content = itemView.findViewById(R.id.tv_content);
            time = itemView.findViewById(R.id.tv_time);
        }

    }
}
