package com.example.feijibook.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;

import com.example.feijibook.R;

/**
 * MyNestedScrollView3
 *
 * @author PengFei Yue
 * @date 2019/8/22
 * @description 自定义NestedScrollView，有两种模式：
 * 1.滑动到顶部下滑或底部上滑时只移动布局
 * 2.滑动到顶部下滑放大缩放View，滑动到底部上滑移动布局
 */
public class MyNestedScrollView extends NestedScrollView {
    private View innerView;
    /**
     * 此坐标为点击后，移动布局的前一次滑动坐标
     */
    private float mY;
    private Rect mRect = new Rect();
    /**
     * 是否时回弹状态中
     */
    private boolean reBound = false;
    /**
     * 是否是恢复缩放状态
     */
    private boolean reZoom = false;
    /**
     * 是否是移动布局前的一次滑动
     */
    private boolean isMoveBefore = true;

    /**
     * 顶部随滑动缩放的View
     */
    private View mZoomView;
    private int mZoomViewWidth;
    private int mZoomViewHeight;

    /**
     * 是否是顶部缩放view的模式,默认为不是缩放view的模式
     * 有两种模式：
     * 1.滑动到顶部下滑或底部上滑时只移动布局
     * 2.滑动到顶部下滑放大缩放View，滑动到底部上滑移动布局
     */
    private boolean mIsZoomMode;

    public MyNestedScrollView(@NonNull Context context) {
        this(context, null);
    }

    public MyNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyNestedScrollView,
                defStyleAttr, 0);
        // 获取布局模式属性，默认为不缩放View模式
        mIsZoomMode = typedArray.getBoolean(R.styleable.MyNestedScrollView_zoomable, false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        innerView = getChildAt(0);
        if (mIsZoomMode) {
            ViewGroup viewGroup = (ViewGroup) innerView;
            if (viewGroup.getChildAt(0) != null) {
                // NestedScrollView内部的包裹所有子view的父view中第一个子view为缩放的View
                mZoomView = viewGroup.getChildAt(0);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (innerView == null) {
            return;
        }
        // 存储原布局 位置
        mRect.set(innerView.getLeft(), innerView.getTop(),
                innerView.getRight(), innerView.getBottom());
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        performClick();
        if (innerView != null) {
            return disposeTouch(ev);
        }
        return super.onTouchEvent(ev);
    }

    private boolean disposeTouch(MotionEvent ev) {
        if (mIsZoomMode && mZoomViewWidth <= 0 && mZoomViewHeight <= 0) {
            mZoomViewWidth = mZoomView.getMeasuredWidth();
            mZoomViewHeight = mZoomView.getMeasuredHeight();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // 没有滚动到顶部或底部，不响应滚动布局
                if (!isScrollToTop() && !isScrollToBottom()) {
                    mY = ev.getY();
                    break;
                }// 如果滚动到顶部且底部，则获取第一次滑动的坐标
                else if (isMoveBefore) {
                    mY = ev.getY();
                    isMoveBefore = false;
                }
                // 下拉时：deltaY > 0   上拉时：deltaY < 0
                int deltaY = (int) (ev.getY() - mY);

                // 到顶部时，上拉不移动布局，到底部时，下拉不移动布局
                if (isScrollToTop() && deltaY <= 0 && !isScrollToBottom()) {
                    break;
                } else if (isScrollToBottom() && deltaY >= 0 && !isScrollToTop()) {
                    break;
                }

                // 阻尼效果
                int offset = 0;
                if (deltaY > 0) {
                    offset = (int) Math.pow(Math.abs(deltaY), 0.8);
                } else if (deltaY < 0) {
                    offset = (int) Math.pow(Math.abs(deltaY), 0.8) * -1;
                }

                // 滑动到顶部下滑或底部上滑时只移动布局模式
                if (!mIsZoomMode) {
                    // 滑动布局
                    innerView.layout(mRect.left, mRect.top + offset, mRect.right, mRect.bottom + offset);
                    // 设置可回弹
                    reBound = true;
                }// 滑动到顶部下滑放大缩放View，滑动到底部上滑移动布局
                else {
                    if (isScrollToTop() && deltaY > 0) {
                        int distance = (int) (deltaY * 0.6);
                        setZoomView(1 + distance);
                        reZoom = true;

                    } else if (isScrollToBottom() && deltaY < 0) {
                        // 滑动布局
                        innerView.layout(mRect.left, mRect.top + offset, mRect.right, mRect.bottom + offset);
                        // 设置可回弹
                        reBound = true;
                    }
                }

                // 滑动布局时消费滑动事件，不传递到下面的RecyclerView
                return true;
            case MotionEvent.ACTION_UP:
                replyLayout();
                replyZoomView();
                isMoveBefore = true;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 是否已滚动到底部
     */
    private boolean isScrollToBottom() {
        return innerView.getHeight() <= getHeight() + getScrollY();
    }

    /**
     * 是否已滚动到顶部
     */
    private boolean isScrollToTop() {
        return getScrollY() == 0;
    }

    /**
     * 恢复移动布局为初始状态位置
     */
    private void replyLayout() {
        if (!reBound) {
            return;
        }
        // 恢复原布局的回弹动画
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0,
                innerView.getTop(), mRect.top);
        translateAnimation.setDuration(200);
        innerView.startAnimation(translateAnimation);
        // 回弹结束后，设置会原布局的位置
        innerView.layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
        reBound = false;

    }

    /**
     * 恢复缩放View为初始状态大小
     */
    private void replyZoomView() {
        if (!reZoom) {
            return;
        }
        final float distance = mZoomView.getMeasuredWidth() - mZoomViewWidth;
        // 设置不断的缩小动画
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0.0f, 1.0f).setDuration((long) (distance * 0.7));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (float) animation.getAnimatedValue();
                setZoomView(distance - ((distance) * cVal));
            }
        });
        valueAnimator.start();
        reZoom = false;
    }

    /**
     * 设置缩放View的大小实现缩放View
     *
     * @param i 缩放的大小
     */
    private void setZoomView(float i) {
        if (mZoomViewHeight <= 0 && mZoomViewWidth <= 0) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = mZoomView.getLayoutParams();
        layoutParams.width = (int) (mZoomViewWidth + i);
        layoutParams.height = (int) (mZoomViewHeight * (mZoomViewWidth + i) / mZoomViewWidth);
        mZoomView.setLayoutParams(layoutParams);
    }
}
