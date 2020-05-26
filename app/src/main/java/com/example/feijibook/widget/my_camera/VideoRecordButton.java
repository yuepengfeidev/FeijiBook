package com.example.feijibook.widget.my_camera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.widget.MyToast;

/**
 * VideoRecordButton
 *
 * @author PengFei Yue
 * @date 2019/9/12
 * @description 防微信的视频长按录制按钮
 */
public class VideoRecordButton extends View {
    /**
     * 外圆环宽度
     */
    private float progressCircleWidth;
    /**
     * 外圆环颜色
     */
    private int outCircleColor;
    /**
     * 内圆颜色
     */
    private int innerCircleColor;
    /**
     * 进度条颜色
     */
    private int progressCircleColor;
    private Paint outCirclePaint = new Paint();
    private Paint progressCirclePaint = new Paint();
    private Paint innerCirclePaint = new Paint();
    private float width;
    private float height;
    private float outCircleRadius;
    private float innerCircleRadius;
    /**
     * 手势识别
     */
    private GestureDetectorCompat mDetector;
    private float startAngle = -90;
    private float mSweepAngleStart = 0f;
    private float mSweepAngleEnd = 360f;
    /**
     * 进度条扫过的角度
     */
    private float mSweepAngle;
    private int mLoadingTime;
    /**
     * 是否长按状态
     */
    private boolean isLongPressing;

    private AnimatorSet set;

    public VideoRecordButton(Context context) {
        this(context, null);
    }

    public VideoRecordButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoRecordButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        outCircleColor = Color.GRAY;
        innerCircleColor = ContextCompat.getColor(MyApplication.sContext, R.color.sky_blue_like);
        progressCircleColor = ContextCompat.getColor(MyApplication.sContext, R.color.sky_blue_like);
        mLoadingTime = 10;
        mDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                isLongPressing = true;
                start();
                if (mOnProgressTouchListener != null) {
                    mOnProgressTouchListener.onLongPress(VideoRecordButton.this);
                }
            }
        });
        mDetector.setIsLongpressEnabled(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width > height) {
            setMeasuredDimension(height, width);
        } else {
            setMeasuredDimension(width, height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        resetParams();
        // 绘制外圈
        outCirclePaint.setAntiAlias(true);
        outCirclePaint.setColor(outCircleColor);
        // 长按状态放大view
        if (isLongPressing) {
            canvas.scale(1.2f, 1.2f, width / 2, height / 2);
        }
        // 绘制外圈圆
        canvas.drawCircle(width / 2, height / 2,
                outCircleRadius, outCirclePaint);
        innerCirclePaint.setAntiAlias(true);
        innerCirclePaint.setColor(innerCircleColor);
        if (isLongPressing) {
            // 绘制内圈圆
            canvas.drawCircle(width / 2, height / 2,
                    innerCircleRadius / 2.0f, innerCirclePaint);
            // 绘制外圈进度条圆环
            progressCirclePaint.setAntiAlias(true);
            progressCirclePaint.setColor(progressCircleColor);
            progressCirclePaint.setStyle(Paint.Style.STROKE);
            progressCirclePaint.setStrokeWidth(progressCircleWidth / 2);
             RectF rectF = new RectF(progressCircleWidth,
                    progressCircleWidth,
                    width - progressCircleWidth,
                    height - progressCircleWidth);
            canvas.drawArc(rectF, startAngle, mSweepAngle,
                    false, progressCirclePaint);
        } else {
            canvas.drawCircle(width / 2, height / 2,
                    innerCircleRadius, innerCirclePaint); }

        super.onDraw(canvas);
    }

    public void start() {
        ValueAnimator animator = ValueAnimator.ofFloat(mSweepAngleStart, mSweepAngleEnd);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSweepAngle = (float) animation.getAnimatedValue();
                // 获取需要h绘制的角度，重新绘制
                invalidate();
            }
        });
        set = new AnimatorSet();
        set.play(animator);
        set.setDuration(mLoadingTime * 1000);
        set.setInterpolator(new LinearInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                clearAnimation();
                isLongPressing = false;
                postInvalidate();
                if (mOnProgressTouchListener != null) {
                    // 一秒为36°,当录制大于1秒时才能存储视频
                    if (mSweepAngle >= 36) {
                        mOnProgressTouchListener.onFinish();
                    } else {
                        mOnProgressTouchListener.onToast();
                    }
                }
            }
        });
        set.start();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        mDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isLongPressing = false;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isLongPressing) {
                    isLongPressing = false;
                    set.end();
                    postInvalidate();
                }
                break;
            default:
        }
        return true;
    }

    private void resetParams() {
        width = getWidth();
        height = getHeight();
        progressCircleWidth = width * 0.13f;
        outCircleRadius = (float) (Math.min(width, height) / 2.4);
        innerCircleRadius = outCircleRadius - progressCircleWidth;
    }

    public OnProgressTouchListener mOnProgressTouchListener;

    public void setOnProgressTouchListener(OnProgressTouchListener onProgressTouchListener) {
        mOnProgressTouchListener = onProgressTouchListener;
    }

    public interface OnProgressTouchListener {
        /**
         * 长按
         */
        void onLongPress(VideoRecordButton button);

        /**
         * 长按结束
         */
        void onFinish();

        /**
         * 当录制时间太短时会弹出对话框
         */
        void onToast();
    }
}
