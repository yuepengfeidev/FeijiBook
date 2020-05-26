package com.example.feijibook.widget.my_recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.Objects;

/**
 * Created by 你是我的 on 2019/3/11
 */
public class MyRecyclerView extends RecyclerView {
    private static final int INVALID_POSITION = -1; // 触摸到的点不在子View范围内
    private static final int INVALID_CHILD_WIDTH = -1;  // 子ItemView不含两个子View
    private static final int SNAP_VELOCITY = 600;   // 最小滑动速度

    private VelocityTracker mVelocityTracker;   // 速度追踪器
    private int mTouchSlop; // 认为是滑动的最小距离（一般由系统提供）
    private Rect mTouchFrame;   // 子View所在的矩形范围
    private Scroller mScroller;
    private float mLastX;   // 滑动过程中记录上次触碰点X
    private float mFirstX, mFirstY; // 首次触碰范围
    private boolean mIsSlide;   // 是否滑动子View
    private ViewGroup mFlingView;   // 触碰的子View
    private int mPosition;  // 触碰的view的位置
    private int mMenuViewWidth;    // 菜单按钮宽度


    public MyRecyclerView(Context context) {
        this(context, null);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        obtainVelocity(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {// 动画没停止则立即终止动画
                    mScroller.abortAnimation();
                }
                mFirstX = mLastX = x;
                mFirstY = y;
                mPosition = pointToPosition(x, y);// 获取触碰点所在position
                if (mPosition != INVALID_POSITION) {
                    View view = mFlingView;
                    // 获取触碰点所在的view
                    mFlingView = (ViewGroup) getChildAt(mPosition -
                            ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition());
                    // 之前触摸view打开且当前触摸view不是上个触摸view，则关闭上一个触摸view
                    if (view != null && mFlingView != view && view.getScrollX() != 0) {
                        view.scrollTo(0, 0);
                    }
                    // 这里进行了强制的要求，RecyclerView的子ViewGroup必须要有2个子view,这样菜单按钮才会有值，
                    // 需要注意的是:如果不定制RecyclerView的子View，则要求子View必须要有固定的width。
                    // 比如使用LinearLayout作为根布局，而content部分width已经是match_parent，此时如果菜单view用的是wrap_content，
                    // menu的宽度就会为0。
                    if (mFlingView.getChildCount() == 2) {
                        mMenuViewWidth = mFlingView.getChildAt(1).getWidth();// 删除按钮宽度
                    } else {
                        mMenuViewWidth = INVALID_CHILD_WIDTH;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.computeCurrentVelocity(1000);
                // 此处有俩判断，满足其一则认为是侧滑
                // 1.如果x方向速度大于y方向速度，且大于最小速度限制；
                // 2.如果x方向的侧滑距离大于y方向滑动距离，且x方向达到最小滑动距离；
                float xVelocity = mVelocityTracker.getXVelocity();
                float yVelocity = mVelocityTracker.getYVelocity();
                if (Math.abs(xVelocity) > SNAP_VELOCITY && Math.abs(xVelocity) > Math.abs(yVelocity)
                        || Math.abs(x - mFirstX) >= mTouchSlop && Math.abs(x - mFirstX) >= Math.abs(y - mFirstY)) {
                    mIsSlide = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                releaseVelocity();
                break;
            default:
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        performClick();
        if (mIsSlide && mPosition != INVALID_POSITION) {
            float x = e.getX();
            obtainVelocity(e);
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:// 没有拦截，所以不会调用到
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 随手指滑动
                    if (mMenuViewWidth != INVALID_CHILD_WIDTH) {
                        float dx = mLastX - x;
                        if (mFlingView.getScrollX() + dx <= mMenuViewWidth
                                && mFlingView.getScrollX() + dx > 0) {// 左滑的距离小于删除按钮宽度且左滑
                            mFlingView.scrollBy((int) dx, 0);// 跟随手指滑动删除按钮
                        }
                        mLastX = x;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mMenuViewWidth != INVALID_CHILD_WIDTH) {
                        int scrollX = mFlingView.getScrollX();
                        mVelocityTracker.computeCurrentVelocity(1000);// 计算最大速率
                        // 此处有两个原因决定是否打开菜单：
                        // 1.菜单被拉出宽度大于菜单宽度一半；
                        // 2.横向滑动速度大于最小滑动速度；
                        // 注意：之所以要小于负值，是因为向左滑则速度为负值
                        if (mVelocityTracker.getXVelocity() < -SNAP_VELOCITY) {// 向左侧滑达到侧滑最低速度，则打开
                            mScroller.startScroll(scrollX, 0, mMenuViewWidth - scrollX,
                                    0, 250);// 通过一个起点移动距离
                        } else if (mVelocityTracker.getXVelocity() >= SNAP_VELOCITY) {// 向右侧滑达到最低侧滑速度则关闭
                            mScroller.startScroll(scrollX, 0, -scrollX, 0, 250);
                        } else if (scrollX >= mMenuViewWidth / 2) {// 如果超过删除按钮一半，则打开
                            mScroller.startScroll(scrollX, 0, mMenuViewWidth - scrollX, 0,
                                    250);
                        } else {// 关闭
                            mScroller.startScroll(scrollX, 0, -scrollX, 0, 250);
                        }
                        invalidate();
                    }
                    mMenuViewWidth = INVALID_CHILD_WIDTH;
                    mIsSlide = false;
                    mPosition = INVALID_POSITION;
                    releaseVelocity();// 这里之所以会调用，是因为如果前面拦截了，就不会执行ACTION_UP,需要在这里释放追踪
                    break;
                default:
            }
            return true;
        } else {
            // 此处防止RecyclerView正常滑动时，还有菜单未关闭
            closeMenu();
            // Velocity，这里的释放是防止RecyclerView正常拦截了，但是在onTouchEvent中却没有被释放；
            // 有三种情况：1.onInterceptTouchEvent并未拦截，在onInterceptTouchEvent方法中，DOWN和UP一对获取和释放；
            // 2.onInterceptTouchEvent拦截，DOWN获取，但事件不是被侧滑处理，需要在这里进行释放；
            // 3.onInterceptTouchEvent拦截，DOWN获取，事件被侧滑处理，则在onTouchEvent的UP中释放。
            releaseVelocity();
        }
        return super.onTouchEvent(e);
    }

    /**
     * 将显示子菜单的子view关闭
     * 这里本身是要自己来实现的，但是由于不定制item，因此不好监听器点击事件，因此需要调用者手动的关闭
     */
    public void closeMenu() {
        if (mFlingView != null && mFlingView.getScrollX() != 0) {
            mFlingView.scrollTo(0, 0);
        }
    }

    // 释放速度跟踪
    private void releaseVelocity() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private int pointToPosition(int x, int y) {
        // 获取屏幕中可见item的第一个
        int firstPosition = ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();
        Rect frame = mTouchFrame;
        if (frame == null) {
            mTouchFrame = new Rect();
            frame = mTouchFrame;
        }

        final int count = getChildCount();// 屏幕中可见item的数量
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {// 遍历依次获得每个item的点击矩形，，看是否包含点击的坐标
                    return firstPosition + i;// 获得点击的item的position
                }
            }
        }
        return INVALID_POSITION;// 触摸的地方不在子view范围内
    }

    // 开启观察速度
    private void obtainVelocity(MotionEvent e) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(e);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mFlingView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}