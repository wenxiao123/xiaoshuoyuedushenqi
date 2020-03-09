package com.example.administrator.xiaoshuoyuedushenqi.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.example.administrator.xiaoshuoyuedushenqi.R;

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

