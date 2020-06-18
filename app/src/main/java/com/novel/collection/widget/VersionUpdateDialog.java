package com.novel.collection.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.novel.collection.R;
import com.novel.collection.base.BaseDialog;
import com.novel.collection.util.DensityUtils;
import com.novel.collection.weyue.utils.ScreenUtils;

import org.greenrobot.greendao.annotation.NotNull;

public class VersionUpdateDialog extends Dialog {
    private Context mContext;
    private int width = 0;
    private int height = 0;

    private OnVersionClick onVersionClick;
    private View mView;
    public interface OnVersionClick {
        void onCancel();

        void onUpdate();
    }

    public VersionUpdateDialog(@NotNull Context context, OnVersionClick onVersionClick) {
        super(context);
        this.mContext = context;
        this.onVersionClick = onVersionClick;
        mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_version_update, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mView);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        getWindow().setBackgroundDrawable(getRoundRectDrawable(DensityUtils.dp2px(mContext, 8), 0));
        width = (int) (getScreenMetrics(mContext) * 0.7);
        height = WindowManager.LayoutParams.WRAP_CONTENT;
        setWidthHeight();
        mView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onVersionClick.onCancel();
            }
        });
        mView.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onVersionClick.onUpdate();
            }
        });

    }
    public static int getScreenMetrics(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return new Point(screenWidth, screenHeight).x;
    }
    public void setContent(String content) {
        TextView textView = mView.findViewById(R.id.content);
        textView.setText(content);
    }

    private void setWidthHeight() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = width;
        lp.height = height;
        dialogWindow.setAttributes(lp);
    }

    private static GradientDrawable getRoundRectDrawable(int radius, int color) {
        float[] radiuss = {radius, radius, radius, radius, radius, radius, radius, radius};
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadii(radiuss);
        drawable.setColor(color != 0 ? color : Color.TRANSPARENT);
        return drawable;
    }

}
