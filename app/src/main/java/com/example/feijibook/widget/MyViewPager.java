package com.example.feijibook.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by 你是我的 on 2019/3/11
 */
public class MyViewPager extends ViewPager {
    public MyViewPager(@NonNull Context context) {
        this(context,null);
    }

    public MyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCurrentItem(int item) {
        // false取消滑动特效
        super.setCurrentItem(item, false);
    }

    /**
     * 下面两个是不处理触摸事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        performClick();
        return false;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
