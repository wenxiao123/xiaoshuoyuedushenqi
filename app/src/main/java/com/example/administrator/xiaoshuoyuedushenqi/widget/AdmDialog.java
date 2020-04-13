package com.example.administrator.xiaoshuoyuedushenqi.widget;

import android.content.Context;
import android.content.Intent;
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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.administrator.xiaoshuoyuedushenqi.R;
import com.example.administrator.xiaoshuoyuedushenqi.base.BaseDialog2;
import com.example.administrator.xiaoshuoyuedushenqi.http.UrlObtainer;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * @author
 * Created on 2019/11/12
 */
public class AdmDialog extends BaseDialog2 implements View.OnClickListener{

    private OnClickListener mOnClickListener;
    private String mTitle;
    private String mContent;
    private String mEnsure;
    private String mCancel;
    private String mImg;
    private String mHref;
    private boolean is_img;
    public interface OnClickListener {
        void clickEnsure();
        void clickCancel();
        void clickAddAdm();
    }
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //将默认背景设置为透明，否则有白底，看不出圆角
    }

    private AdmDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }
    VideoView videoView;
    @Override
    protected View getCustomView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_adm, null);
        TextView ensure = view.findViewById(R.id.tv_dialog_tip_ensure);
        ensure.setText(mEnsure);
        ensure.setOnClickListener(this);
        ImageView image =view.findViewById(R.id.img);
        videoView=view.findViewById(R.id.activity_opening_videoview);
        if(is_img==true){
            videoView.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            //Uri uri = Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
            Uri uri = Uri.parse(mImg);
            MediaController mediaController = new MediaController(context);
            mediaController.setVisibility(View.GONE);//隐藏进度条
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(uri);
            videoView.requestFocus();
            videoView.start();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            videoView.setOnClickListener(this);
        }else {
            videoView.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
            CornerTransform transformation = new CornerTransform(getContext(), 30);
            //只是绘制左上角和右上角圆角
            transformation.setExceptCorner(false, false, true, true);
            Glide.with(getContext())
                    .load(mImg)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.admin)
                            .error(R.mipmap.admin)
                            .transform(transformation))
                    .into(image);
            image.setOnClickListener(this);
        }
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ImageView imagec =view.findViewById(R.id.icon_close);
        imagec.setOnClickListener(this);
        return view;
    }

    @Override
    protected float getWidthScale() {
        return 0.7f;
    }

    @Override
    protected float getHeightScale() {
        return 0.43f;
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
            case R.id.img:
                dismiss();
//                Intent intent=new Intent();
//                intent.setAction("android.intent.action.VIEW");
//                Uri content_url = Uri.parse("http://www.cnblogs.com");
//                intent.setData(content_url);
//                getContext().startActivity(intent);
                if (mOnClickListener != null){
                    mOnClickListener.clickAddAdm();
                }
                break;
            case R.id.icon_close:
                dismiss();
                break;
            case R.id.activity_opening_videoview:
                dismiss();
//                Intent intent1=new Intent();
//                intent1.setAction("android.intent.action.VIEW");
//                Uri content_url1 = Uri.parse("http://www.cnblogs.com");
//                intent1.setData(content_url1);
//                getContext().startActivity(intent1);
                if (mOnClickListener != null){
                    mOnClickListener.clickAddAdm();
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
        if(videoView!=null){
            videoView.stopPlayback();
        }
    }

    public static class Builder {
        private String title = "! 提示";
        private String content = "提示框内容";
        private String ensure = "是";
        private String cancel = "否";
        private String img;
        private String href;
        private boolean is_img;
        public String getImg() {
            return img;
        }

        public Builder setImg(String img) {
            this.img = img;
            return this;
        }

        public Builder setIs_img(boolean is_img) {
            this.is_img = is_img;
            return this;
        }


        public Builder setHref(String href) {
            this.href = href;
            return this;
        }

        private OnClickListener onClickListener = null;
        private Context context;

        public Builder(Context context) {
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

        public AdmDialog build() {
            AdmDialog tipDialog = new AdmDialog(context);
            tipDialog.mTitle = title;
            tipDialog.mContent = content;
            tipDialog.mEnsure = ensure;
            tipDialog.mCancel = cancel;
            tipDialog.mImg = img;
            tipDialog.mHref = href;
            tipDialog.is_img=is_img;
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
