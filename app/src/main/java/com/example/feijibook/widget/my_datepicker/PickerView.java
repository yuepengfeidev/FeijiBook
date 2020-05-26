package com.example.feijibook.widget.my_datepicker;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.feijibook.R;
import com.example.feijibook.util.SoundShakeUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by 你是我的 on 2019/3/2
 */

/*
 * 滚动选择控件
 */
public class PickerView extends View {
    /**
     * 自动回滚到中间的速度
     */
    private static final float AUTO_SCROLL_SPEED = 10;

    /**
     * 透明度：最小 120，最大 255，极差 135
     */
    private static final int TEXT_ALPHA_MIN = 120;
    private static final int TEXT_ALPHA_RANGE = 135;

    private float mScrollDistance;// 滚动距离

    private Timer mTimer = new Timer();
    private Context mContext;
    private int mLightColor, mDarkColor;
    private TimerTask mTimerTask;
    private OnSelectListener mOnSelectListener;
    private int mSelectedIndex;// 选择的索引
    private List<String> mDataList = new ArrayList<>();
    private Paint mPaint;
    private float mHalfWidth, mHalfHeight, mQuarterHeight;
    private float mMinTextSize, mTextSizeRange;
    private float mTextSpacing, mHalfTextSpacing;
    private boolean mCanScroll = true;// 可以滚动
    private boolean mCanScrollLoop = true;// 可以循环滚动
    private float mLastTouchY;
    private ObjectAnimator mScrollAnim;
    private boolean mCanShowAnim = true;

    private Handler mHandler = new ScrollHandler(this);

    // 选择结果回调接口
    public interface OnSelectListener {
        void onSelect(View view, String selected);
    }


    private static class ScrollTimerTask extends TimerTask {
        private WeakReference<Handler> mWeakHandler;

        private ScrollTimerTask(Handler handler) {
            mWeakHandler = new WeakReference<>(handler);
        }

        @Override
        public void run() {
            Handler handler = mWeakHandler.get();
            if (handler == null) {
                return;
            }

            handler.sendEmptyMessage(0);
        }
    }

    private static class ScrollHandler extends Handler {
        private WeakReference<PickerView> mWeakView;

        private ScrollHandler(PickerView view) {
            mWeakView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            PickerView view = mWeakView.get();
            if (view == null) {
                return;
            }

            view.keepScrolling();
        }
    }

    public PickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mLightColor = ContextCompat.getColor(mContext, R.color.date_picker_text_light);
        mDarkColor = ContextCompat.getColor(mContext, R.color.date_picker_text_dark);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mHalfWidth = getMeasuredWidth() / 2f;
        int height = getMeasuredHeight();
        mHalfHeight = getMeasuredHeight() / 2f;
        mQuarterHeight = getMeasuredHeight() / 4f;
        float maxTextSize = height / 7f;
        mMinTextSize = maxTextSize / 2.2f;
        mTextSizeRange = maxTextSize - mMinTextSize;
        mTextSpacing = mMinTextSize * 2.8f;
        mHalfTextSpacing = mTextSpacing / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mSelectedIndex >= mDataList.size()) {
            return;
        }

        // 绘制选中的text
        drawText(canvas, mLightColor, mScrollDistance, mDataList.get(mSelectedIndex));

        // 绘制选中上方的text
        for (int i = 1; i <= mSelectedIndex; i++) {
            drawText(canvas, mDarkColor, mScrollDistance - i * mTextSpacing,
                    mDataList.get(mSelectedIndex - i));
        }

        // 绘制选中下方的text
        int size = mDataList.size() - mSelectedIndex;
        for (int i = 1; i < size; i++) {
            drawText(canvas, mDarkColor, mScrollDistance + i * mTextSpacing,
                    mDataList.get(mSelectedIndex + i));
        }
    }

    // 绘制text
    private void drawText(Canvas canvas, int textColor, float offestY, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        float scale = 1 - (float) Math.pow(offestY / mQuarterHeight, 2);
        scale = scale < 0 ? 0 : scale;
        mPaint.setAlpha(TEXT_ALPHA_MIN + (int) (TEXT_ALPHA_RANGE * scale));
        mPaint.setColor(textColor);
        mPaint.setTextSize(mMinTextSize + mTextSizeRange * scale);

        // text 居中绘制，mHalfHeight + offestY 是 text 的中心坐标
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        float baseLine = mHalfHeight + offestY - (fm.top + fm.bottom) / 2f;
        canvas.drawText(text, mHalfWidth, baseLine, mPaint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return mCanScroll && super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                cancelTimerTask();
                mLastTouchY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float offestY = event.getY();
                mScrollDistance += offestY - mLastTouchY;
                if (mScrollDistance > mHalfTextSpacing) {// 向下滚动
                    SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                    if (!mCanScrollLoop) {// 不是循环滚动
                        if (mSelectedIndex == 0) {
                            mLastTouchY = offestY;
                            invalidate();
                            break;
                        } else {
                            mSelectedIndex--;
                        }
                    } else {
                        // 往下滑超过离开距离，将末尾元素移至首位
                        moveTailToHead();
                    }
                    mScrollDistance -= mTextSpacing;
                } else if (mScrollDistance < -mHalfTextSpacing) {// 向上滚动
                    SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                    if (!mCanScrollLoop) {
                        if (mSelectedIndex == mDataList.size() - 1) {
                            mLastTouchY = offestY;
                            invalidate();
                            break;
                        } else {
                            mSelectedIndex++;
                        }
                    } else {
                        // 往上滑超过离开距离，将首位元素移到末尾
                        moveHeadToTail();
                    }
                    mScrollDistance += mTextSpacing;
                }
                mLastTouchY = offestY;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                // 抬起手后 mSelectedIndex 由当前位置滚动到中间选中位置
                if (Math.abs(mScrollDistance) < 0.01) {
                    mScrollDistance = 0;
                    break;
                }
                cancelTimerTask();
                mTimerTask = new ScrollTimerTask(mHandler);
                mTimer.schedule(mTimerTask, 0, 10);
                break;
                default:
                    break;
        }
        return true;
    }

    // 末尾移至首位
    private void moveTailToHead() {
        if (!mCanScrollLoop || mDataList.isEmpty()) {
            return;
        }

        String tail = mDataList.get(mDataList.size() - 1);
        mDataList.remove(mDataList.size() - 1);
        mDataList.add(0, tail);// 添加到第一个
    }

    // 首位移至末尾
    private void moveHeadToTail() {
        if (!mCanScrollLoop || mDataList.isEmpty()) {
            return;
        }

        String head = mDataList.get(0);
        mDataList.remove(0);
        mDataList.add(head);// 添加到末尾
    }

    // 保持滚动
    private void keepScrolling() {
        if (Math.abs(mScrollDistance) < AUTO_SCROLL_SPEED) {
            mScrollDistance = 0;
            if (mTimerTask != null) {
                cancelTimerTask();

                if (mOnSelectListener != null && mSelectedIndex < mDataList.size()) {
                    // 替换非数字为""
                    String select = mDataList.get(mSelectedIndex).replaceAll("\\D+","");
                    mOnSelectListener.onSelect(this, select);
                }
            }
        } else if (mScrollDistance > 0) {
            // 向下滚动,减
            mScrollDistance -= AUTO_SCROLL_SPEED;
        } else {
            // 向上滚动，加
            mScrollDistance += AUTO_SCROLL_SPEED;
        }
        invalidate();
    }

    // 设置数据
    public void setDataList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        mDataList = list;
        // 重置 mSelectedIndex ，防止溢出
        mSelectedIndex = 0;
        invalidate();
    }

    // 取消timer线程任务
    private void cancelTimerTask() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.purge();
        }
    }

    // 选择选中项
    public void setSelected(int index) {
        if (index >= mDataList.size()) {
            return;
        }

        mSelectedIndex = index;
        if (mCanScrollLoop) {
            // 可循环滚动时，mSelectedIndex 值固定为 mDatalist / 2
            int position = mDataList.size() / 2 - mSelectedIndex;
            if (position < 0) {// 小于0表示往上滑
                for (int i = 0; i < -position; i++) {
                    moveHeadToTail();
                    mSelectedIndex--;// 每个被选中由于往上滑的减1
                }
            } else if (position > 0) {// 往下滑
                for (int i = 0; i < position; i++) {
                    moveTailToHead();
                    mSelectedIndex++;// 每个被选中的都加1
                }
            }
        }
        invalidate();
    }

    // 设置选择结果监听
    public void setOnSelectListener(OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
    }

    // 是否允许滚动
    public void setCanScroll(boolean canScroll) {
        mCanScroll = canScroll;
    }

    // 是否允许循环滚动
    public void setCanScrollLoop(boolean canScrollLoop) {
        mCanScrollLoop = canScrollLoop;
    }

    // 执行滚动动画
    public void starAnim(){
        if (!mCanShowAnim) {
            return;
        }

        if (mScrollAnim == null){
            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha",1f,0f,1f);
            PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",1f,1.3f,1f);
            PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",1f,1.3f,1f);
            mScrollAnim = ObjectAnimator.ofPropertyValuesHolder(this,alpha,scaleX,scaleY).setDuration(200);
        }

        if (!mScrollAnim.isRunning()){
            mScrollAnim.start();
        }
    }

    // 是否允许滚动动画
    public void setCanShowAnim(boolean canShowAnim){
        mCanShowAnim = canShowAnim;
    }

    // 销毁资源
    public void onDestroy(){
        mOnSelectListener = null;
        mHandler.removeCallbacksAndMessages(null);
        if (mScrollAnim != null && mScrollAnim.isRunning()){
            mScrollAnim.cancel();
        }
        cancelTimerTask();
        if (mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }
}
