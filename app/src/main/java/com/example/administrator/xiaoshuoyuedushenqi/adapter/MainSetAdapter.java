package com.example.administrator.xiaoshuoyuedushenqi.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;

/**
 * @author
 * Created on 2019/11/17
 */
public class MainSetAdapter extends RecyclerView.Adapter<MainSetAdapter.CatalogViewHolder>
        implements View.OnClickListener {
    private static final String TAG = "CatalogAdapter";

    private Context mContext;
    private String[] strings;
    String[] strings2;
    private CatalogListener mListener;

    @Override
    public void onClick(View v) {
        int pos = (int) v.getTag();
        mListener.clickItem(pos);
    }

    public interface CatalogListener {
        void clickItem(int position);
    }

    public void setOnCatalogListener(CatalogListener listener) {
        mListener = listener;
    }

    public MainSetAdapter(Context mContext, String[] strings2, String[] strings) {
        this.mContext = mContext;
        this.strings = strings;
        this.strings2 = strings2;
    }

    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CatalogViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.set_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder catalogViewHolder, final int i) {
        catalogViewHolder.tv_title.setText(strings[i]);
        if (strings2[i].equals("")) {
            catalogViewHolder.iv_back.setVisibility(View.VISIBLE);
            catalogViewHolder.iv_title.setVisibility(View.GONE);
        } else {
            catalogViewHolder.iv_back.setVisibility(View.GONE);
            catalogViewHolder.iv_title.setVisibility(View.VISIBLE);
            catalogViewHolder.iv_title.setText(strings2[i]);
        }
        catalogViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.clickItem(i);
            }
        });
        if (i == strings.length - 1) {
            catalogViewHolder.view.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return strings2.length;
    }

    class CatalogViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, iv_title;
        ImageView iv_back;
        View view;

        public CatalogViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            iv_title = itemView.findViewById(R.id.img_title);
            iv_back = itemView.findViewById(R.id.iv_right);
            view = itemView.findViewById(R.id.line);
        }

    }
}
