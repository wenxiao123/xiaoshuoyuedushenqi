package com.example.administrator.xiaoshuoyuedushenqi.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseDialog2;

/**
 * @author
 * Created on 2019/11/12
 */
public class ShareDialog extends BaseDialog2 implements View.OnClickListener{

    private OnClickListener mOnClickListener;
    private String mTitle;
    private String mContent;
    private String mEnsure;
    private String mCancel;

    public interface OnClickListener {
        void clickEnsure();
        void clickCancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //将默认背景设置为透明，否则有白底，看不出圆角
    }

    private ShareDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected View getCustomView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_share, null);
        TextView content = view.findViewById(R.id.tv_dialog_tip_content);
        String titl=content.getText().toString()+mContent;
        //content.setText(mContent);
        SpannableString ss=new SpannableString(titl);
        int color=getContext().getResources().getColor(R.color.blue);
        ForegroundColorSpan colorSpan=new ForegroundColorSpan(color);
        ss.setSpan(colorSpan,titl.length()-mContent.length(),titl.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.setText(ss);
        TextView ensure = view.findViewById(R.id.tv_dialog_tip_ensure);
        ensure.setText(mEnsure);
        ensure.setOnClickListener(this);
        TextView cancel = view.findViewById(R.id.tv_dialog_tip_cancel);
        cancel.setText(mCancel);
        cancel.setOnClickListener(this);
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.img_share); // 先从资源中把背景图获取出来
        Bitmap roundBitmap = getRoundCornerImage(bitmap, 10, HalfType.TOP); // 将图片的上半部分圆弧化。
        ImageView image =view.findViewById(R.id.img);
        Drawable dw = new BitmapDrawable(getContext().getResources(),roundBitmap);
        image.setBackgroundDrawable(dw); // 设置背景。API>=16的話，可以直接用setBackground方法

        return view;
    }

    @Override
    protected float getWidthScale() {
        return 0.75f;
    }

    @Override
    protected float getHeightScale() {
        return 0.46f;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialog_tip_ensure:
                dismiss();
                if (mOnClickListener != null){
                    mOnClickListener.clickEnsure();
                }
                break;
            case R.id.tv_dialog_tip_cancel:
                dismiss();
                if (mOnClickListener != null){
                    mOnClickListener.clickCancel();
                }
                break;
            default:
                break;
        }
    }

    public static class Builder {
        private String title = "! 提示";
        private String content = "提示框内容";
        private String ensure = "是";
        private String cancel = "否";
        private OnClickListener onClickListener = null;
        private Context context;

        public Builder(Context context) {
            if(((Activity)context).isDestroyed()){
                return ;
            }
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setEnsure(String ensure) {
            this.ensure = ensure;
            return this;
        }

        public Builder setCancel(String cancel) {
            this.cancel = cancel;
            return this;
        }

        public Builder setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }

        public ShareDialog build() {
            ShareDialog tipDialog = new ShareDialog(context);
            tipDialog.mTitle = title;
            tipDialog.mContent = content;
            tipDialog.mEnsure = ensure;
            tipDialog.mCancel = cancel;
            tipDialog.mOnClickListener = onClickListener;
            tipDialog.init();

            return tipDialog;
        }
    }

    /**
     * 图片圆角规则 eg. TOP：上半部分
     */
    public enum HalfType {
        LEFT, // 左上角 + 左下角
        RIGHT, // 右上角 + 右下角
        TOP, // 左上角 + 右上角
        BOTTOM, // 左下角 + 右下角
        ALL // 四角
    }

    /**
     * 将图片的四角圆弧化
     *
     * @param bitmap      原图
     * @param roundPixels 弧度
     * @param half        （上/下/左/右）半部分圆角
     * @return
     */
    public static Bitmap getRoundCornerImage(Bitmap bitmap, int roundPixels, HalfType half) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap roundConcerImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);//创建一个和原始图片一样大小的位图
        Canvas canvas = new Canvas(roundConcerImage);//创建位图画布
        Paint paint = new Paint();//创建画笔

        Rect rect = new Rect(0, 0, width, height);//创建一个和原始图片一样大小的矩形
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);// 抗锯齿

        canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);//画一个基于前面创建的矩形大小的圆角矩形
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//设置相交模式
        canvas.drawBitmap(bitmap, null, rect, paint);//把图片画到矩形去

        switch (half) {
            case LEFT:
                return Bitmap.createBitmap(roundConcerImage, 0, 0, width - roundPixels, height);
            case RIGHT:
                return Bitmap.createBitmap(roundConcerImage, width - roundPixels, 0, width - roundPixels, height);
            case TOP: // 上半部分圆角化 “- roundPixels”实际上为了保证底部没有圆角，采用截掉一部分的方式，就是截掉和弧度一样大小的长度
                return Bitmap.createBitmap(roundConcerImage, 0, 0, width, height - roundPixels);
            case BOTTOM:
                return Bitmap.createBitmap(roundConcerImage, 0, height - roundPixels, width, height - roundPixels);
            case ALL:
                return roundConcerImage;
            default:
                return roundConcerImage;
        }
    }
}
