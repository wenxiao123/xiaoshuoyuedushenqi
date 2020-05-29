package com.novel.collection.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.novel.collection.R;

public class TakePhotosDialog extends Dialog {




    public TakePhotosDialog(Context context) {
        super(context);
        this.show();
    }

    public TakePhotosDialog(Context context, int theme) {
        super(context, theme);
        this.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diolog);
    }


}

