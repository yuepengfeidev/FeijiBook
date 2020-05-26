package com.example.feijibook.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;

/**
 * MyLineChart
 *
 * @author yuepengfei
 * @date 2019/6/12
 * @description 内部拦截，解决触摸LineChart显示marker时与外层NestedScrollView的滑动冲突
 */
public class MyLineChart extends LineChart {
    public MyLineChart(Context context) {
        super(context);
    }

    public MyLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 按下，拦截
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev);
    }
}
