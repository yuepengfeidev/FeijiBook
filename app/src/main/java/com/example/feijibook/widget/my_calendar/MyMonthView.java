package com.example.feijibook.widget.my_calendar;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;

/**
 * Created by 你是我的 on 2019/3/19
 */

// 自定义月视图
public class MyMonthView extends MonthView {
    int mRadius;// 选择日期圆形背景半径
    private Paint mPointPaint = new Paint();// 标记日期上的标记点画笔
    private float mPointRadius;// 圆形标记点半径

    private int mPadding;
    private Paint mCurrentDayPaint = new Paint();// 今天的圆形背景画笔

    public MyMonthView(Context context) {
        super(context);

        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setTextAlign(Paint.Align.CENTER);

        mCurrentDayPaint.setAntiAlias(true);
        mCurrentDayPaint.setStyle(Paint.Style.FILL);
        mCurrentDayPaint.setTextAlign(Paint.Align.CENTER);
        mCurrentDayPaint.setColor(Color.parseColor("#B7E0E0E0"));

        mRadius = dipToPx(getContext(), 7);
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
     * 绘制选中的日子
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param y         日历Card y起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return 返回true 则会继续绘制onDrawScheme，因为这里背景色不是是互斥的，所以返回true，返回false，则点击scheme标记的日子，则不继续绘制onDrawScheme，自行选择即可
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        mSelectedPaint.setStyle(Paint.Style.FILL);
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        return true;
    }

    /**
     * 绘制标记的事件日子
     * @param canvas   canvas
     * @param calendar 日历calendar
     * @param x        日历Card x起点坐标
     * @param y        日历Card y起点坐标
     */
    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        boolean isSelected = isSelected(calendar);
        if (isSelected) {
            mPointPaint.setColor(Color.parseColor("#E7E7E7"));
        }// 今日且标记的标记点放在绘制今日背景后绘制，这里只绘制标记且不是今日且未选择的标记点
        else if (!calendar.isCurrentDay()){
            mPointPaint.setColor(Color.parseColor("#C2C2C2"));
        }

        canvas.drawCircle(x + mItemWidth / 2, y + mItemHeight - 2*mPadding +5, mPointRadius, mPointPaint);
    }

    /**
     * 绘制文本
     * @param canvas     canvas
     * @param calendar   日历calendar
     * @param x          日历Card x起点坐标
     * @param y          日历Card y起点坐标
     * @param hasScheme  是否是标记的日期
     * @param isSelected 是否选中
     */
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        int top = y - mItemHeight / 6;

        mCurDayTextPaint.setFakeBoldText(false);
        mCurMonthTextPaint.setFakeBoldText(false);
        mOtherMonthTextPaint.setFakeBoldText(false);
        mSelectTextPaint.setFakeBoldText(false);
        mSchemeTextPaint.setFakeBoldText(false);
        mSchemeTextPaint.setColor(Color.parseColor("#000000"));
        mOtherMonthTextPaint.setFakeBoldText(false);

        if (calendar.isCurrentDay() && !isSelected) {// 今天在未选中的情况下的背景
            canvas.drawCircle(cx, cy, mRadius, mCurrentDayPaint);
            // 当是今天，且有标记时绘制标记点，标记点必须在绘制今日背景后才能绘制,否则颜色被遮盖后会变成不一样的色
            if (hasScheme) {
                mPointPaint.setColor(Color.parseColor("#C2C2C2"));
                canvas.drawCircle(x + mItemWidth / 2, y + mItemHeight - 2 * mPadding + 5, mPointRadius, mPointPaint);
            }
        }

        if (isSelected) {//优先绘制选择的
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    mSelectTextPaint);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10, mSelectedLunarTextPaint);
        } else if (hasScheme) {//否则绘制具有标记的
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);

            if (calendar.isCurrentDay()) {// 今天的农历绘制为黑色字
                mCurMonthLunarTextPaint.setFakeBoldText(false);
            }
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10, mCurMonthLunarTextPaint);
        } else {//最好绘制普通文本
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);

            mCurDayLunarTextPaint.setFakeBoldText(false);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10,
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