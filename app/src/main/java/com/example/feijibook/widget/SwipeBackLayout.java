package com.example.feijibook.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.swipeback_interface.AnimUpdateListener;

/**
 * Created by 你是我的 on 2019/3/13
 */

// 右滑退出
public class SwipeBackLayout extends FrameLayout {
    // 页面边缘阴影的宽度默认值
    private static final int SHADOW_WIDTH = 16;
    private Activity mActivity;
    Scroller mScroller;
    // 页面边缘的阴影图
    private Drawable mLeftShadow;
    // 页面边缘阴影的宽度
    private int mShadowWidth;
    private int mInterceptDownX;
    private int mLastInterceptX;
    private int mLastInterceptY;
    private int mTouchDownX;
    private int mLastTouchX;
    private int mLastTouchY;
    private boolean isConsumed = false;
    private boolean isSlide = true;// 该层是否可以用手滑动
    private boolean isMoveState = false;

    public SwipeBackLayout(@NonNull Context context) {
        this(context, null);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mScroller = new Scroller(context);
        mLeftShadow = ContextCompat.getDrawable(context, R.drawable.left_shadow);
        int density = (int) getResources().getDisplayMetrics().density;
        mShadowWidth = SHADOW_WIDTH * density;// 通过密度来制定阴影宽度
    }

    // 绑定Activity
    public void bindActivity(Activity activity, boolean isSlide) {
        this.isSlide = isSlide;
        mActivity = activity;
        ViewGroup decorView = (ViewGroup) mActivity.getWindow().getDecorView();
        View child = decorView.getChildAt(0);
        decorView.removeView(child);// 移除子view
        addView(child);// 将该子view添加到该布局中
        decorView.addView(this);// 再将该布局添加到根布局
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isSlide) {
            return super.onInterceptTouchEvent(ev);
        }
        boolean intercept = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercept = false;
                mInterceptDownX = x;
                mLastInterceptX = x;
                mLastInterceptY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastInterceptX;
                int deltaY = y - mLastInterceptY;
                // 手指处于屏幕边缘，且横向滑动距离大于纵向滑动距离时，拦截事件
                intercept = mInterceptDownX < (getWidth() / 10) && Math.abs(deltaX) > Math.abs(deltaY);
                mLastInterceptY = y;
                mLastInterceptX = x;
                break;
            case MotionEvent.ACTION_UP:
                mInterceptDownX = mLastInterceptX = mLastInterceptY = 0;
                break;
            default:
        }
        return intercept;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isSlide) {// 判断该层是否可以滑动
            return super.onTouchEvent(event);
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = x;
                mLastTouchX = x;
                mLastTouchY = y;
                performClick();
                break;
            case MotionEvent.ACTION_MOVE:
                // 开始滑动时，返回滑动监听
                if (!isMoveState && mMoveListener != null) {
                    mMoveListener.startMove();
                }
                isMoveState = true;
                int deltaX = x - mLastTouchX;
                int deltaY = y - mLastTouchY;

                if (!isConsumed && mTouchDownX < (getWidth() / 10) && Math.abs(deltaX) > Math.abs(deltaY)) {
                    isConsumed = true;
                }

                if (isConsumed) {
                    // 左滑大于0，右滑小于0
                    int rightMoveX = mLastTouchX - (int) event.getX();
                    if (getScrollX() + rightMoveX >= 0) {
                        scrollTo(0, 0);// 左滑且已经到最左边，界面与屏幕左侧紧靠
                    } else {
                        scrollBy((int) (rightMoveX / 1.06), 0);// 界面随滑动距离而滑动

                        sUpdate.moveAnim(rightMoveX);// 通知下层活动同步滑动
                    }
                }
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            case MotionEvent.ACTION_UP:
                isMoveState = false;
                isConsumed = false;
                mTouchDownX = mLastTouchX = mLastTouchY = 0;
                // 根据手指释放时的位置决定回弹还是关闭
                if (-getScrollX() < getWidth() / 2) {
                    scrollBack();// 右滑距离小于屏幕一半，界面返回原始状态
                    // 该层恢复到初始状态时，下层activity一次性移动到屏幕左侧外边
                    sUpdate.initAnim();
                } else {
                    scrollClose();// 右滑距离大于屏幕一半，界面从右侧退出，销毁当前活动
                }

                break;
            default:
        }
        return true;
    }

    // 滑动关闭，从右侧退出，销毁当前活动
    private void scrollClose() {
        // 在finish当前活动之前，下层活动界面同步对其屏幕
        sUpdate.finishAnim();
        int startX = getScrollX();
        int dx = -getScrollX() - getWidth();
        mScroller.startScroll(startX, 0, dx, 0, 170);
        invalidate();
    }

    // 滑动返回，恢复原始状态
    private void scrollBack() {
        int startX = getScrollX();
        int dx = -getScrollX();
        mScroller.startScroll(startX, 0, dx, 0, 170);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();

        } else if (-getScrollX() >= getWidth() && isSlide) {// 右滑距离大于屏幕宽度，则销毁当前活动
            mActivity.finish();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawShadow(canvas);
    }

    // 绘制边缘阴影
    private void drawShadow(Canvas canvas) {
        mLeftShadow.setBounds(0, 0, mShadowWidth, getHeight()); // 绘制阴影的范围
        canvas.save();
        canvas.translate(-mShadowWidth, 0);// 画布左移
        mLeftShadow.draw(canvas);
        canvas.restore();
    }


    public void move(final int moveDistance) {
        scrollBy((int) (moveDistance / 2.7), 0);// 界面随滑动距离而滑动
    }

    public void moveToFront() {// 活动显示时，下层活动滚动到左侧屏幕外
        scrollTo(400, 0);
    }

    public void finish() {// 活动finish之前，下层活动与屏幕对其动画
        int startX = getScrollX();
        int dx = -getScrollX();
        mScroller.startScroll(startX, 0, dx, 0, 170);
        invalidate();
    }

    public void init() {// 没有关闭当前活动，则底层活动初始化到之前的状态（滚动到左侧屏幕之外）
        int startX = getScrollX();
        int dx = -getScrollX();
        mScroller.startScroll(startX, 0, dx + 400, 0, 170);
        invalidate();
    }

    /**
     * 监听滑动状态
     */
    public interface MoveListener {
        /**
         * 开始滑动
         */
        void startMove();
    }

    MoveListener mMoveListener;

    public void setMoveListener(MoveListener moveListener) {
        mMoveListener = moveListener;
    }

    static AnimUpdateListener sUpdate;

    public static void setUpdate(AnimUpdateListener update) {
        sUpdate = update;
    }
}
