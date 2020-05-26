package com.example.feijibook.widget.my_calendar;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.WeekView;

/**
 * Created by 你是我的 on 2019/3/19
 */

// 自定义周视图
public class MyWeekView extends WeekView {
    private Paint mPointPaint = new Paint();// 标记日期上的标记点画笔
    private float mPointRadius;// 圆形标记点半径
    private int mPadding;
    private Paint mCurrentDayPaint = new Paint();// 今天的圆形背景画笔

    private int mRadius;

    public MyWeekView(Context context) {
        super(context);

        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setTextAlign(Paint.Align.CENTER);

        mCurrentDayPaint.setAntiAlias(true);
        mCurrentDayPaint.setStyle(Paint.Style.FILL);
        mCurrentDayPaint.setTextAlign(Paint.Align.CENTER);
        mCurrentDayPaint.setColor(Color.parseColor("#B7E0E0E0"));

        mPadding = dipToPx(getContext(), 4);
        mPointRadius = dipToPx(context, 2);

        //兼容硬件加速无效的代码
        setLayerType(View.LAYER_TYPE_SOFTWARE, mSelectedPaint);
        //4.0以上硬件加速会导致无效
        mSelectedPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.SOLID));// 光晕效果

        //兼容硬件加速无效的代码
        setLayerType(View.LAYER_TYPE_SOFTWARE, mCurrentDayPaint);
        //4.0以上硬件加速会导致无效
        mCurrentDayPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.SOLID));// 光晕效果
    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 11 * 5;
    }

    /**
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return true 则绘制onDrawScheme，因为这里背景色不是是互斥的
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, boolean hasScheme) {
        mSelectedPaint.setStyle(Paint.Style.FILL);
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x) {
        boolean isSelected = isSelected(calendar);
        if (isSelected | (calendar.isCurrentDay() && !isSelected)) {
            mPointPaint.setColor(Color.parseColor("#E7E7E7"));
        } else {
            mPointPaint.setColor(Color.parseColor("#C2C2C2"));
        }

        canvas.drawCircle(x + mItemWidth / 2, mItemHeight - 2 * mPadding + 5, mPointRadius, mPointPaint);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, boolean hasScheme, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;
        int top = -mItemHeight / 6;

        mSelectTextPaint.setFakeBoldText(false);
        mSchemeTextPaint.setFakeBoldText(false);
        mOtherMonthTextPaint.setFakeBoldText(false);
        mCurDayTextPaint.setFakeBoldText(false);
        mCurMonthTextPaint.setFakeBoldText(false);

        mSchemeTextPaint.setColor(Color.parseColor("#000000"));

        if (calendar.isCurrentDay() && !isSelected) {
            canvas.drawCircle(cx, cy, mRadius, mCurrentDayPaint);
            if (hasScheme) {// 今天有标记的加上白色点标记点
                mPointPaint.setColor(Color.parseColor("#E7E7E7"));
                canvas.drawCircle(x + mItemWidth / 2, mItemHeight - 2 * mPadding + 5, mPointRadius, mPointPaint);
            }
        }

        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    mSelectTextPaint);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + mItemHeight / 10, mSelectedLunarTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);

            if (calendar.isCurrentDay()) {// 今天的农历绘制为黑色字
                mCurMonthLunarTextPaint.setFakeBoldText(false);
            }
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + mItemHeight / 10, mCurMonthLunarTextPaint);
        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);

            mCurDayLunarTextPaint.setFakeBoldText(false);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + mItemHeight / 10,
                    calendar.isCurrentDay() ? mCurDayLunarTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthLunarTextPaint : mOtherMonthLunarTextPaint);
        }
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