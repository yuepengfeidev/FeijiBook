package com.example.feijibook.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * MySpinner
 *
 * @author PengFei Yue
 * @date 2019/11/2
 * @description 解决再次选择同一item时不触发监听
 */
public class MySpinner extends android.support.v7.widget.AppCompatSpinner {

    private int lastPosition = 0;

    public MySpinner(Context context, int mode) {
        super(context, mode);
    }

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    @Override
    public void setSelection(int position, boolean animate) {
        super.setSelection(position, animate);
        if (position == lastPosition) {
            // 与上次相同item，则手动调用监听
            getOnItemSelectedListener().onItemSelected(this, null, position, 0);
        }
        lastPosition = position;
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (position == lastPosition) {
            getOnItemSelectedListener().onItemSelected(this, null, position, 0);
        }
        lastPosition = position;
    }
}