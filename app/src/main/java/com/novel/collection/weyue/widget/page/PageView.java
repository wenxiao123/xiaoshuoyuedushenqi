package com.novel.collection.weyue.widget.page;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;


import com.gyf.immersionbar.ImmersionBar;
import com.novel.collection.app.App;
import com.novel.collection.util.NetUtil;
import com.novel.collection.util.ToastUtil;
import com.novel.collection.weyue.utils.ScreenUtils;
import com.novel.collection.weyue.widget.animation.CoverPageAnim;
import com.novel.collection.weyue.widget.animation.HorizonPageAnim;
import com.novel.collection.weyue.widget.animation.NonePageAnim;
import com.novel.collection.weyue.widget.animation.PageAnimation;
import com.novel.collection.weyue.widget.animation.ScrollPageAnim2;
import com.novel.collection.weyue.widget.animation.SimulationAnimation4;
import com.novel.collection.weyue.widget.animation.SlidePageAnim;
import java.lang.ref.WeakReference;


/**
 * 绘制页面显示内容的类
 */
public class PageView extends View {

    public final static int PAGE_MODE_SIMULATION = 0;
    public final static int PAGE_MODE_COVER = 1;
    public final static int PAGE_MODE_SLIDE = 2;
    public final static int PAGE_MODE_NONE = 3;
    //滚动效果
    public final static int PAGE_MODE_SCROLL = 4;

    private final static String TAG = "BookPageWidget";

    private int mViewWidth = 0; // 当前View的宽
    private int mViewHeight = 0; // 当前View的高

    private int mStartX = 0;
    private int mStartY = 0;
    private boolean isMove = false;
    //初始化参数
    private int mBgColor = 0xFFCEC29C;
    private int mPageMode = PAGE_MODE_COVER;

    //是否允许点击
    private boolean canTouch = true;
    //判断是否初始化完成
    private boolean isPrepare = false;
    //唤醒菜单的区域
    private RectF mCenterRect = null;

    //动画类
    private PageAnimation mPageAnim;
    private Context mContext;
    public PageAnimation getmPageAnim() {
        return mPageAnim;
    }

    public void setmPageAnim(PageAnimation mPageAnim) {
        this.mPageAnim = mPageAnim;
    }

    //动画监听类
    private PageAnimation.OnPageChangeListener mPageAnimListener = new PageAnimation.OnPageChangeListener() {
        @Override
        public boolean hasPrev() {
            return PageView.this.hasPrev();
        }

        @Override
        public boolean hasNext() {
            return PageView.this.hasNext();
        }

        @Override
        public void pageCancel(){
            mTouchListener.cancel();
            mPageLoader.pageCancel();
        }
    };

    //点击监听
    private TouchListener mTouchListener;
    //内容加载器
    private PageLoader mPageLoader;

    public PageView(Context context) {
        this(context,null);

    }

    public PageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        //重置图片的大小,由于w,h可能比原始的Bitmap更大，所以如果使用Bitmap.setWidth/Height()是会报错的。
        //所以最终还是创建Bitmap的方式。这种方式比较消耗性能，暂时没有找到更好的方法。
        mPageLoader.NO_TOUTCH_HIGHT= ImmersionBar.getNotchHeight((Activity) mContext);
        setPageMode(mPageMode);

        mPageLoader.setContext(mContext);
        //重置页面加载器的页
//        if(mPageLoader.NO_TOUTCH_HIGHT==0){
//            mPageLoader.NO_TOUTCH_HIGHT=getStatusBarHeight(mContext);
//        }
//        Log.e("WWW", "onSizeChanged: "+ImmersionBar.getNotchHeight((Activity) mContext)+" "+getStatusBarHeight(mContext));
        mPageLoader.setDisplaySize(w,h);

        //初始化完成
        isPrepare = true;
        mAutoPlayTask = new AutoPlayTask(this);
        startAutoPlay();
    }
    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
    //设置翻页的模式
    public void setPageMode(int pageMode){
        mPageMode = pageMode;
        //视图未初始化的时候，禁止调用
        if (mViewWidth == 0 || mViewHeight == 0) return;

        switch (pageMode){
            case PAGE_MODE_SIMULATION:
                mPageAnim = new SimulationAnimation4(mViewWidth, mViewHeight,this,mPageAnimListener);
                break;
            case PAGE_MODE_COVER:
                mPageAnim = new CoverPageAnim(mViewWidth, mViewHeight,this,mPageAnimListener);
                break;
            case PAGE_MODE_SLIDE:
                mPageAnim = new SlidePageAnim(mViewWidth, mViewHeight,this,mPageAnimListener);
                break;
            case PAGE_MODE_NONE:
                mPageAnim = new NonePageAnim(mViewWidth, mViewHeight,this,mPageAnimListener);
                break;
            case PAGE_MODE_SCROLL:
//                mPageAnim = new ScrollPageAnim2(mViewWidth, mViewHeight, 0,
//                        ScreenUtils.dpToPx(PageLoader.DEFAULT_MARGIN_HEIGHT),this,mPageAnimListener);
                mPageAnim = new ScrollPageAnim2(mViewWidth, mViewHeight-mPageLoader.NO_TOUTCH_HIGHT, 0,
                        ScreenUtils.dpToPx(PageLoader.DEFAULT_MARGIN_HEIGHT),this,mPageAnimListener,mPageLoader.NO_TOUTCH_HIGHT);
                break;
            default:
                mPageAnim = new SimulationAnimation4(mViewWidth, mViewHeight,this,mPageAnimListener);
        }
    }

    public Bitmap getNextPage(){
        if (mPageAnim == null) return null;
        return mPageAnim.getNextBitmap();
    }

    public Bitmap getBgBitmap(){
        if (mPageAnim == null) return null;
        return mPageAnim.getBgBitmap();
    }


    public boolean autoPrevPage(){
        //滚动暂时不支持自动翻页
        if (mPageAnim instanceof ScrollPageAnim2){
            return false;
        }
        else {
            startPageAnim(PageAnimation.Direction.PRE);
            return true;
        }
    }

    public boolean autoNextPage(){
//        if (mPageAnim instanceof ScrollPageAnim){
//            return false;
//        }
//        else {
        startPageAnim(PageAnimation.Direction.NEXT);
        return true;
//        }
    }

    private void startPageAnim(PageAnimation.Direction direction){
        if (mTouchListener == null) return;
        //是否正在执行动画
        abortAnimation();

        if (mPageMode == PageView.PAGE_MODE_SCROLL && direction == PageAnimation.Direction.NEXT) {
            int x = mViewWidth;
            int y = 0;
            //初始化动画
            mPageAnim.setStartPoint(x, y);
            //设置点击点
            mPageAnim.setTouchPoint(x, y);
            ((ScrollPageAnim2) mPageAnim).startAutoRead(mTextSize+mTextInterval);
            this.postInvalidate();
            return;
        }
        if (direction == PageAnimation.Direction.NEXT){
            int x = mViewWidth;
            int y = mViewHeight;
            //设置点击点
            mPageAnim.setTouchPoint(x,y);
            //初始化动画
            mPageAnim.setStartPoint(x,y);
            //设置方向
            Boolean hasNext = hasNext();

            mPageAnim.setDirection(direction);
            if (!hasNext) {
                return;
            }
        }
        else{
            int x = 0;
            int y = mViewHeight;
            //初始化动画
            mPageAnim.setStartPoint(x,y);
            //设置点击点
            mPageAnim.setTouchPoint(x,y);
            mPageAnim.setDirection(direction);
            //设置方向方向
            Boolean hashPrev = hasPrev();
            if (!hashPrev) {
                return;
            }
        }
        mPageAnim.startAnim();
        this.postInvalidate();
    }

    public void setBgColor(int color){
        mBgColor = color;
    }

    public void canTouchable(boolean touchable){
        canTouch = touchable;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //绘制背景
        canvas.drawColor(mBgColor);

        //绘制动画
        mPageAnim.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (!canTouch && event.getAction() != MotionEvent.ACTION_DOWN) return true;

        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                isMove = false;
                canTouch = mTouchListener.onTouch();
                mPageAnim.onTouchEvent(event);
                stopAutoPlay();
                break;
            case MotionEvent.ACTION_MOVE:
                //判断是否大于最小滑动值。
                int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (!isMove){
                    isMove = Math.abs(mStartX - event.getX()) > slop || Math.abs(mStartY - event.getY()) > slop;
                }

                //如果滑动了，则进行翻页。
                if(isMove){
                    mPageAnim.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                startAutoPlay();
                if (!isMove){
                    //设置中间区域范围
                    if (mCenterRect == null){
                        mCenterRect = new RectF(mViewWidth/4,0,
                                mViewWidth*3/4,mViewHeight);
                    }

                    //是否点击了中间
                    if (mCenterRect.contains(x,y)) {
                        if (mTouchListener != null) {
                            if(mTouchListener.onTouch()==true) {
                                mTouchListener.center();
                            }
                        }
                        return true;
                    }
                }
                mPageAnim.onTouchEvent(event);
                break;
        }
        return true;
    }

    //判断是否下一页存在
    private boolean hasNext(){
        Boolean hasNext = false;
        if (mTouchListener != null){
            hasNext = mTouchListener.nextPage();
            //加载下一页
            if (hasNext){
                hasNext = mPageLoader.next();
            }
        }
        return hasNext;
    }

    //判断是否存在上一页
    private boolean hasPrev(){
        Boolean hasPrev = false;
        if (mTouchListener != null){
            hasPrev = mTouchListener.prePage();
            //加载下一页
            if (hasPrev){
                hasPrev = mPageLoader.prev();
            }
        }
        return hasPrev;
    }

    @Override
    public void computeScroll() {
        //进行滑动
        mPageAnim.scrollAnim();
        super.computeScroll();
    }

    //如果滑动状态没有停止就取消状态，重新设置Anim的触碰点
    public void abortAnimation() {
        mPageAnim.abortAnim();
    }

    public boolean isPrepare(){
        return isPrepare;
    }

    public boolean isRunning(){
        return mPageAnim.isRunning();
    }

    public void setTouchListener(TouchListener mTouchListener){
        this.mTouchListener = mTouchListener;
    }

    public void drawNextPage(){
        if (mPageAnim instanceof HorizonPageAnim){
            ((HorizonPageAnim) mPageAnim).changePage();
        }
//        if(adm_bitmap!=null){
//            drawAdminPage(adm_bitmap);
//        }else {
        mPageLoader.onDraw(getNextPage(), false);
//        }
    }

    public void drawAdminPage(Bitmap bitmap){
        mPageLoader.onDraw(getNextPage(),bitmap, false);
    }
    public Bitmap getAdm_bitmap() {
        return adm_bitmap;
    }

    public void setAdm_bitmap(Bitmap adm_bitmap) {
        this.adm_bitmap = adm_bitmap;
    }

    Bitmap adm_bitmap;
    /**
     * 刷新当前页(主要是为了ScrollAnimation)
     *
     */
    public void refreshPage(){
        if (mPageAnim instanceof ScrollPageAnim2){
            ((ScrollPageAnim2) mPageAnim).refreshBitmap();
        }
        drawCurPage(false);
    }
    //refreshPage和drawCurPage容易造成歧义,后面需要修改
    /**
     * 绘制当前页。
     * @param isUpdate
     */
    public void drawCurPage(boolean isUpdate){
        //       if (!isPrepare) return;
//        if (!isUpdate) {
//            if (mPageAnim instanceof ScrollPageAnim2) {
//                ((ScrollPageAnim2) mPageAnim).resetBitmap();
//            }
//        }
        mPageLoader.onDraw(getNextPage(), isUpdate);
    }

    //获取PageLoader
    public PageLoader getPageLoader(boolean isLocal,boolean isother){
        if (isLocal){
            mPageLoader = new LocalPageLoader(this);
        }
        else {
                if(isother==false) {
             mPageLoader = new NetPageLoader(this);
                }else {
                    mPageLoader = new OtherNetPageLoader(this);
                }
        }
        return mPageLoader;
    }

    public interface TouchListener{
        void center();
        boolean onTouch();
        boolean prePage();
        boolean nextPage();
        void cancel();
    }

    /**
     * 自动阅读逻辑
     */
    //字体的大小
    private int mTextSize=60;
    //行间距
    private int mTextInterval=30;
    private AutoPlayTask mAutoPlayTask;
    //自动阅读间隔时间
    private int mAutoPlayInterval = 1000;
    //是否开启自动阅读
    private static boolean mAutoPlayAble = false;

    public void startAutoPlay() {
        stopAutoPlay();
        Log.e("auto","--------"+mAutoPlayAble);
        if (mAutoPlayAble) {
            if(mPageAnim instanceof CoverPageAnim || mPageAnim instanceof SimulationAnimation4){
                mPageAnim.is_auto=true;
            }
            postDelayed(mAutoPlayTask, mAutoPlayInterval);
        }
    }

    public void stopAutoPlay() {
        if (mAutoPlayTask != null) {
            if(mPageAnim instanceof CoverPageAnim || mPageAnim instanceof SimulationAnimation4){
                mPageAnim.is_auto=false;
            }
            removeCallbacks(mAutoPlayTask);
        }
    }


    public int getmTextSize() {
        return mTextSize;
    }

    public void setmTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }

    public int getmTextInterval() {
        return mTextInterval;
    }

    public void setmTextInterval(int mTextInterval) {
        this.mTextInterval = mTextInterval;
    }

    public int getmAutoPlayInterval() {
        return mAutoPlayInterval;
    }

    public void setmAutoPlayInterval(int mAutoPlayInterval) {
        this.mAutoPlayInterval = mAutoPlayInterval;
    }

    public boolean ismAutoPlayAble() {
        return mAutoPlayAble;
    }

    public void setmAutoPlayAble(boolean mAutoPlayAble) {
        this.mAutoPlayAble = mAutoPlayAble;
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            startAutoPlay();
        } else if (visibility == INVISIBLE || visibility == GONE) {
            stopAutoPlay();
        }
    }
    private static class AutoPlayTask implements Runnable {
        private final WeakReference<PageView> mPageView;

        private AutoPlayTask(PageView pageView) {
            mPageView = new WeakReference<>(pageView);
        }

        @Override
        public void run() {
            Log.e("auto","------------------------2");
            PageView pageView = mPageView.get();
            if (pageView != null) {
                pageView.autoNextPage();
                pageView.startAutoPlay();
            }
            if (!NetUtil.hasInternet(App.getContext())) {
                ToastUtil.showToast(App.getContext(),"当前无网络，请检查网络后重试");
                mAutoPlayAble=false;
                pageView.stopAutoPlay();
                return;
            }
        }
    }
}
