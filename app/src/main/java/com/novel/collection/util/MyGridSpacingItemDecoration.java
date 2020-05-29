package com.novel.collection.util;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class MyGridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int column;
    private final int space;

    public MyGridSpacingItemDecoration(int column, int space) {
        this.column = column;
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildAdapterPosition(view);
        int mod = pos % column;
        //LogUtil.v("getItemOffsets", "pos:" + pos + ", mod:" + mod + ", space:" + space);
        outRect.bottom = 30;
    }
}

