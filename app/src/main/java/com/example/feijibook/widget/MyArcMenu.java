package com.example.feijibook.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.example.feijibook.R;
import com.example.feijibook.util.SoundShakeUtil;

/**
 * MyArcMenu
 *
 * @author PengFei Yue
 * @date 2019/8/19
 * @description
 */
public class MyArcMenu extends ViewGroup implements View.OnClickListener {
    /**
     * 菜单按钮，中心按钮
     */
    private View mCButton;

    /**
     * 当前状态栏状态
     */
    private Status mCurStatus = Status.CLOSE;

    /**
     * 菜单展开的半径，子项按钮与中心按钮的距离
     */
    private int mRadius = 0;
    private OnMenuItemClickListener mOnMenuItemClickListener;

    private enum Status {
        /**
         * 状态栏的状态
         */
        OPEN, CLOSE
    }

    public interface OnMenuItemClickListener {
        /**
         * 点击菜单栏子项的点击监听
         *
         * @param view 菜单栏
         * @param pos  子项的位置
         */
        void onClick(View view, int pos);
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public MyArcMenu(Context context) {
        this(context, null);
    }

    public MyArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        // 将100dp转为标准尺寸的px
        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
                context.getResources().getDisplayMetrics());
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcMenu
                , defStyleAttr, 0);
        mRadius = (int) array.getDimension(R.styleable.ArcMenu_radius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
                context.getResources().getDisplayMetrics()));
        // 回收TypeArray
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View v) {
        SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
        if (mCurStatus == Status.CLOSE) {
            rotateCButton(v, 0f, 45f, 300);
        } else {
            rotateCButton(v, 45f, 0f, 300);
        }
        toggleMenu(300);
    }

    /**
     * 根据状态弹出或关闭菜单,所有子项按钮的动画效果及点击监听
     *
     * @param duration 动画持续时间
     */
    private void toggleMenu(int duration) {
        int count = getChildCount();
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) getChildAt(0).getLayoutParams();
        for (int j = 0; j < count - 1; j++) {
            final View child = getChildAt(j + 1);
            child.setVisibility(VISIBLE);
            int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * j));
            int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * j));
            AnimationSet animationSet = new AnimationSet(true);
            Animation tranAnim;
            if (mCurStatus == Status.CLOSE) {
                // 打开则从右下角移动到左边
                tranAnim = new TranslateAnimation(cl - marginLayoutParams.rightMargin, 0,
                        ct - marginLayoutParams.bottomMargin, 0);
                child.setClickable(true);
                child.setFocusable(true);
            } else {
                tranAnim = new TranslateAnimation(0, cl,
                        0, ct);
                child.setClickable(false);
                child.setFocusable(false);
            }
            tranAnim.setFillAfter(true);
            // 设置动画启动时间
            tranAnim.setStartOffset((j * 100) / count);
            tranAnim.setDuration(duration);
            tranAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurStatus == Status.CLOSE) {
                        child.setVisibility(GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            RotateAnimation rotateAnimation = new RotateAnimation(45, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(duration);
            rotateAnimation.setFillAfter(true);
            // 每个子项按钮添加选中动画和位移动画
            animationSet.addAnimation(rotateAnimation);
            animationSet.addAnimation(tranAnim);
            child.startAnimation(animationSet);
            final int pos = j + 1;
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    changStatus();
                    rotateCButton(mCButton, 45f, 0f, 300);
                    menuItemAnim(v, pos);
                }
            });
        }
        changStatus();
    }

    /**
     * 点击所有菜单子项按钮，点击子项按钮和未被点击子项按钮的点击动画效果
     *
     * @param pos 被点击的子项按钮的位置
     */
    private void menuItemAnim(View view, int pos) {
        int count = getChildCount();
        for (int j = 1; j < count; j++) {
            View child = getChildAt(j);
            // 点击的子项按钮，放大消失
            if (pos == j) {
                child.startAnimation(scaleBigAnim(view, pos, 300));
            } else {
                child.startAnimation(scaleSmallAnim(300));
            }
        }
    }

    /**
     * 未被点击的子项按钮的缩小消失效果
     *
     * @param duration 动画持续时间
     * @return 动画
     */
    private Animation scaleSmallAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);
        // 缩小动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // 透明消失动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    /**
     * 被点击的子项按钮的放大消失效果
     *
     * @param duration 动画持续时间
     * @return 动画
     */
    private Animation scaleBigAnim(final View v, final int position, int duration) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 2.5f, 1f, 2.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mOnMenuItemClickListener.onClick(v, position);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animationSet;
    }

    /**
     * 旋转中心按钮
     *
     * @param v        中心按钮的View
     * @param start    开始旋转的角度
     * @param end      结束旋转的角度
     * @param duration 旋转动画的时间
     */
    private void rotateCButton(View v, float start, float end, int duration) {
        // 相对与中心按钮的自身中心点旋转
        RotateAnimation animation = new RotateAnimation(start, end,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        v.startAnimation(animation);
    }

    /**
     * 改变当前的状态
     */
    private void changStatus() {
        mCurStatus = (mCurStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
    }

    /**
     * 卫星菜单是否为打开状态
     */
    public boolean isOpen() {
        return mCurStatus == Status.OPEN;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            layoutButton();
            int count = getChildCount();
            MarginLayoutParams marginLayoutParams =
                    (MarginLayoutParams) getChildAt(0).getLayoutParams();
            for (int i = 0; i < count - 1; i++) {
                // 第一个子View是中心按钮，所以第一个子项菜单为第二个，要+1
                View child = getChildAt(i + 1);
                child.setVisibility(GONE);
                // 如果有四个子项按钮，就可以把90°分成3份
                // => Π / 2 / （四个子项 + 中心按钮 - 2）
                // 第一个子项按钮是从中心按钮的上面开始的，左边为最后一个子项按钮
                int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
                int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));
                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();
                // 每个子view都在右下角
                ct = getMeasuredHeight() - cHeight - ct;
                cl = getMeasuredWidth() - cWidth - cl;
                child.layout(cl - marginLayoutParams.rightMargin,
                        ct - marginLayoutParams.bottomMargin,
                        cl + cWidth - marginLayoutParams.rightMargin,
                        ct + cHeight - marginLayoutParams.bottomMargin); } } }

    /**
     * 不知中心按钮的位置
     */
    private void layoutButton() {
        mCButton = getChildAt(0);
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) mCButton.getLayoutParams();
        mCButton.setOnClickListener(this);
        int l;
        int t;
        int width = mCButton.getMeasuredWidth();
        int height = mCButton.getMeasuredHeight();
        l = getMeasuredWidth() - width;
        t = getMeasuredHeight() - height;
        mCButton.layout(l - marginLayoutParams.rightMargin,
                t - marginLayoutParams.bottomMargin,
                l + width - marginLayoutParams.rightMargin,
                t + height - marginLayoutParams.bottomMargin);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /**
     * 使该view支持margin
     */
    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
