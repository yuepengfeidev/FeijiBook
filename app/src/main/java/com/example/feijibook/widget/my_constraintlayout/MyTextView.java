package com.example.feijibook.widget.my_constraintlayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * MyTextView
 *
 * @author PengFei Yue
 * @date 2019/10/3
 * @description
 */
public class MyTextView extends android.support.v7.widget.AppCompatTextView implements LayoutAnim.AnimEndListener {
    public MyConstraintLayout.ClickListener mClickListener;
    /**
     * 1：上测翻    2：右侧翻   3：下侧翻   4：左侧翻
     */
    private int pivot = 0;

    /**
     * false为只有点击缩小效果
     */
    private boolean superb = true;

    public MyTextView(Context context) {
        this(context, null);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    public void setClickListener(MyConstraintLayout.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public void animEnd() {
        if (mClickListener != null) {
            mClickListener.onClick();
        }
    }
}
