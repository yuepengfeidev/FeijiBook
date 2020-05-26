package com.example.feijibook.widget.my_progress_bar;

/*
 * Created by 你是我的 on 2019/5/10
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;

/**
 * 自定义比例条
 */
public class ProportionScaleView extends View {
    private int mWidth;
    private RectF mRectF;
    private Paint mPaint;
    private Float mScales;

    public ProportionScaleView(Context context) {
        super(context);
        init(context);
    }

    public ProportionScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProportionScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // 允许重写onDraw，默认为false
        setWillNotDraw(false);
        int height = dipToPx(context, 7);
        mRectF = new RectF(0, 0, 0, height);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        // 用于第一次初始化显示的长度
        mRectF.right = mWidth * mScales;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int color = ContextCompat.getColor(MyApplication.sContext,R.color.sky_blue_like);
        mPaint.setColor(color);
        canvas.drawRoundRect(mRectF, 15, 15, mPaint);
        super.onDraw(canvas);
    }

    /**
     * 设置 宽度比例，变换比例条的宽度
     *
     * @param scales 宽度比例
     */
    public void setScales(float scales) {
        // 小于一定比例，指定长度比例
        if (scales < 0.015) {
            mScales = Float.valueOf("0.01");
        } else {
            mScales = scales;
        }
        mRectF.right = mWidth * mScales;
        invalidate();
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
