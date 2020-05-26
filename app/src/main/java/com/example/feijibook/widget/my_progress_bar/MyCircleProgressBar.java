package com.example.feijibook.widget.my_progress_bar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.feijibook.R;

/**
 * MyCircleProgressBar
 *
 * @author PengFei Yue
 * @date 2019/11/8
 * @description 自定义圆环型上传文件进度条
 */
public class MyCircleProgressBar extends View {
    private Paint mTextPaint;
    private Paint mCirclePaint;
    private Paint mCircleProgressPaint;
    private int circleColor;
    private int circleProgressColor;
    private int textColor;
    private float textSize;
    /**
     * 圆环宽度
     */
    private float circleWidth;
    /**
     * 绘制外层大圆环
     */
    int center;
    /**
     * 圆环半径
     */
    int radius;
    String mPercent = "0.0%";
    float curAngle = 0f;
    RectF oval;


    public MyCircleProgressBar(Context context) {
        this(context, null);
    }

    public MyCircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray mTypeArray = context.obtainStyledAttributes(attrs, R.styleable.MyCircleProgressBar);
        circleColor = mTypeArray.getColor(R.styleable.MyCircleProgressBar_circleColor,
                ContextCompat.getColor(context, R.color.background_light_gray2));
        circleProgressColor = mTypeArray.getColor(R.styleable.MyCircleProgressBar_circleProgressColor,
                ContextCompat.getColor(context, R.color.sky_blue_like));
        textColor = mTypeArray.getColor(R.styleable.MyCircleProgressBar_textColor,
                ContextCompat.getColor(context, R.color.sky_blue_like));
        textSize = mTypeArray.getDimension(R.styleable.MyCircleProgressBar_textSize, 40);
        circleWidth = mTypeArray.getDimension(R.styleable.MyCircleProgressBar_circleWidth, 5);
        mTypeArray.recycle();

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(circleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        // 设置圆环宽度
        mCirclePaint.setStrokeWidth(circleWidth);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        mCircleProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleProgressPaint.setColor(circleProgressColor);
        mCircleProgressPaint.setStyle(Paint.Style.STROKE);
        mCircleProgressPaint.setStrokeWidth(circleWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (center == 0) {
            // 绘制外层大圆环
            center = getWidth() / 2;
            // 圆环半径
            radius = (int) (center - circleWidth / 2);
            oval = new RectF(center - radius - 1, center - radius - 1, center
                    + radius + 1, center + radius + 1);
        }
        canvas.drawCircle(center, center, radius, mCirclePaint);
        float textWidth = mTextPaint.measureText(mPercent);
        canvas.drawText(mPercent, center - textWidth / 2, center + textSize / 3, mTextPaint);
        canvas.drawArc(oval, -90, curAngle, false, mCircleProgressPaint);
    }

    /**
     * 设置进度，并更新
     */
    public void setPercent(String percent) {
        mPercent = percent + "%";
        curAngle = Float.parseFloat(percent) / 100 * 360;
        invalidate();
    }

    /**
     * 清除进度
     */
    public void cleanProgress() {
        curAngle = 0;
        mPercent = "0.0%";
    }
}
