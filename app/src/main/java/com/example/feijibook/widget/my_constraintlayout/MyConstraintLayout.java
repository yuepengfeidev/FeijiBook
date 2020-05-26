package com.example.feijibook.widget.my_constraintlayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * MyConstraintLayout
 *
 * @author yuepengfei
 * @date 2019/7/13
 * @description
 */
public class MyConstraintLayout extends ConstraintLayout implements LayoutAnim.AnimEndListener {
    public ClickListener mClickListener;
    /**
     * 1：上测翻    2：右侧翻   3：下侧翻   4：左侧翻
     */
    private int pivot = 0;

    /**
     * false为只有点击缩小效果
     */
    private boolean superb = false;

    public MyConstraintLayout(Context context) {
        super(context, null);
    }

    public MyConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public MyConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setClickable(true);
    }

    /**
     * 打开侧翻效果，点击中心也有缩小效果
     */
    public void openSuperb() {
        superb = true;
    }

    /**
     * 关闭侧翻效果，只有缩小效果
     */
    public void closeSuperb() {
        superb = false;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            // 手指按下
            case MotionEvent.ACTION_DOWN:
                LayoutAnim.setAnimEndListener(this);
                // 获取点击点位置，用于按类执行抬起效果
                pivot = LayoutAnim.startAnimDown(this, superb, event.getX(), event.getY());
                break;
            // 触摸动作取消
            case MotionEvent.ACTION_CANCEL:
                // 手指抬起
            case MotionEvent.ACTION_UP:
                LayoutAnim.startAnimUp(this, pivot, event);
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    public void setClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public void animEnd() {
        if (mClickListener != null) {
            mClickListener.onClick();
        }
    }

    /**
     * 点击监听
     */
    public interface ClickListener {
        /**
         * 当动画结束后执行点击事件
         */
        void onClick();
    }

}
