package com.example.feijibook.widget.my_constraintlayout;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.example.feijibook.R;

/**
 * LayoutAnim
 *
 * @author yuepengfei
 * @date 2019/7/13
 * @description 布局的点击动画
 * 点击View的上、下、左、右、中，分别对应的5种不一样的动画：
 * 上 ————> 上侧翻
 * 下 ————> 下侧翻
 * 左 ————> 左侧翻
 * 右 ————> 右侧翻
 * 中 ————> 缩放
 */
public class LayoutAnim {
    private static AnimEndListener mAnimEndListener;
    /**
     * 动画执行速度
     */
    private static final int ANIM_SPEED = 300;

    /**
     * 旋转角度
     */
    private static final float POTATION_VALUE = 7f;

    /**
     * 变速器
     */
    private static OvershootInterpolator interpolator = new OvershootInterpolator(3f);

    /**
     * 缩放比例
     */
    private static final float SCALE_END = 0.95f;

    /**
     * 阴影最小值
     */
    private static final float SHADOW_END = 0;

    private static final int CENTER_SCALE = 0;
    private static final int TOP_ROLL = 1;
    private static final int RIGHT_ROLL = 2;
    private static final int BOTTOM_ROLL = 3;
    private static final int LEFT_ROLL = 4;

    /**
     * 启动按压动画
     *
     * @param view   执行动画的View
     * @param superb 效果类型，false为只有缩放效果
     * @param x      触点X坐标
     * @param y      触点Y坐标
     * @return 动画执行顶点
     */
    public static int startAnimDown(View view, boolean superb, float x, float y) {
        if (!view.isClickable()) {
            return CENTER_SCALE;
        }

        // 1：上测翻    2：右侧翻   3：下侧翻   4：左侧翻
        int pivot;
        // 缩放效果
        if (!superb) {
            pivot = CENTER_SCALE;
            // 执行缩小动画
            scaleBigToSmall(view);
            return pivot;
        }

        // 侧翻效果
        int w = view.getWidth();
        int h = view.getHeight();

        // 中间区域
        if ((w / 5 * 2) < x && x < (w / 5 * 3) && (h / 5 * 2) < y && y < (h / 5 * 3)) {
            pivot = CENTER_SCALE;
        }// View左上角（第一象限）
        else if (x < w / 2 && y < h / 2) {
            // 第一象限以对左上角和右下角的点所形成的对角线分割成两个区域：包含左下角，包含右上角
            // 此判断 x所占比例比 y所占比例大，则为包含右上角的那块区域
            if (x / (w / 2) > y / (h / 2)) {
                // 上测翻
                pivot = TOP_ROLL;
            }// 其他的未第一象限包含左下角的那块区域
            else {
                // 左侧翻
                pivot = LEFT_ROLL;
            }
        }// view左下角（第四象限）
        else if (x < w / 2 && y >= h / 2) {
            // 包含左上角的区域
            if ((w - x) / (w / 2) > y / (h / 2)) {
                // 左侧翻
                pivot = LEFT_ROLL;
            }// 包含右下角的区域
            else {
                // 下侧翻
                pivot = BOTTOM_ROLL;
            }
        }// view右下角（第三象限）
        else if (x >= w / 2 && y >= h / 2) {
            // 包含左下角的区域
            if ((w - x) / (w / 2) > (h - y) / (h / 2)) {
                // 下侧翻
                pivot = BOTTOM_ROLL;
            }// 包含右上角的区域
            else {
                // 右侧翻
                pivot = RIGHT_ROLL;
            }
        }// view右上角（第二象限）
        else {
            // 包含右下角的区域
            if (x / (w / 2) > (h - y) / (h / 2)) {
                // 右侧翻
                pivot = RIGHT_ROLL;
            }// 包含左上角的区域
            else {
                // 上测翻
                pivot = TOP_ROLL;
            }
        }

        String anim = "";
        view.setPivotX(w / 2);
        view.setPivotY(h / 2);

        switch (pivot) {
            case CENTER_SCALE:
                // 点击中间区域，执行缩小效果
                scaleBigToSmall(view);
                return pivot;
            case TOP_ROLL:
            case BOTTOM_ROLL:
                // 上下侧翻，动画为以X轴旋转
                anim = "rotationX";
                break;
            case RIGHT_ROLL:
            case LEFT_ROLL:
                // 左右侧翻，动画为以Y轴旋转
                anim = "rotationY";
                break;
            default:
                break;
        }

        // 执行侧翻效果
        rollSideExecute(view, pivot, anim);
        return pivot;
    }

    /**
     * 启动抬起动画
     *
     * @param view  执行动画的View
     * @param pivot 动画执行顶点
     * @param event 点击动作
     */
    public static void startAnimUp(View view, int pivot, MotionEvent event) {
        if (!view.isClickable()) {
            return;
        }

        if (pivot == CENTER_SCALE) {
            // 执行放大效果
            scaleSmallToBig(view, event);
        } else {
            String anim = "";
            switch (pivot) {
                case TOP_ROLL:
                case BOTTOM_ROLL:
                    anim = "rotationX";
                    break;
                case RIGHT_ROLL:
                case LEFT_ROLL:
                    anim = "rotationY";
                    break;
                default:
            }
            // 执行侧翻恢复效果
            rollSideRecover(view, pivot, anim, event);
        }
    }

    /**
     * 侧翻后的恢复效果
     */
    private static void rollSideRecover(View view, int pivot, String anim, final MotionEvent event) {
        int rotation;
        if (pivot == RIGHT_ROLL || pivot == LEFT_ROLL) {
            // 左侧翻和右侧翻 以Y轴旋转，因此获取Y轴的旋转当前角度
            rotation = (int) view.getRotationY();
        } else {
            rotation = (int) view.getRotationX();
        }
        ObjectAnimator animObject = ObjectAnimator.ofFloat(view, anim, rotation, 0).setDuration(ANIM_SPEED);
        animObject.setInterpolator(interpolator);
        animObject.start();
        animObject.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 当动画结束时，且触摸动作为”ACTION_UP"时才通知动画结束，并执行点击事件
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mAnimEndListener.animEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 缩小后的放大效果
     */
    private static void scaleSmallToBig(View view, final MotionEvent event) {
        float tzStart = 0, tzEnd = 0;
        Object viewTag = view.getTag(R.string.tag_key_translation_z);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tzStart = view.getTranslationZ();
            if (viewTag instanceof Float) {
                tzEnd = (Float) viewTag;
            }
        }

        /*
         * Z轴变低
         */
        PropertyValuesHolder tz = PropertyValuesHolder.ofFloat("translationZ", tzStart, tzEnd);
        /*
         * 控件的宽变小
         */
        PropertyValuesHolder sx = PropertyValuesHolder.ofFloat("scaleX", view.getScaleX(), 1);
        /*
         * 控件的高变小
         */
        PropertyValuesHolder sy = PropertyValuesHolder.ofFloat("scaleY", view.getScaleY(), 1);

        /*
         * 动画集合，所有动画一起播放
         */
        ObjectAnimator animatorD = ObjectAnimator.ofPropertyValuesHolder(view, tz, sx, sy).setDuration(ANIM_SPEED);
        animatorD.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 当动画结束时，且触摸动作为”ACTION_UP"时才通知动画结束，并执行点击事件
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mAnimEndListener.animEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorD.setInterpolator(interpolator);
        animatorD.start();
    }

    /**
     * 点击测翻效果
     */
    private static void rollSideExecute(View view, int pivot, String anim) {
        float rotationEnd;
        if (pivot == BOTTOM_ROLL || pivot == LEFT_ROLL) {
            // 下测翻和左侧翻为负值
            rotationEnd = 0 - POTATION_VALUE;
        } else {
            rotationEnd = POTATION_VALUE;
        }

        int rotationStart;
        if (pivot == RIGHT_ROLL || pivot == LEFT_ROLL) {
            // 左侧翻和右侧翻 以Y轴旋转，因此获取Y轴的旋转当前角度
            rotationStart = (int) view.getRotationY();
        } else {
            rotationStart = (int) view.getRotationX();
        }

        ObjectAnimator animObject = ObjectAnimator.ofFloat(view, anim, rotationStart, rotationEnd)
                .setDuration(ANIM_SPEED);
        animObject.setInterpolator(interpolator);
        animObject.start();
    }

    /**
     * 点击中心的缩小效果
     */
    private static void scaleBigToSmall(View view) {
        float tzStart = 0;
        Object viewTag = view.getTag(R.string.tag_key_translation_z);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tzStart = view.getTranslationZ();
            if (!(viewTag instanceof Float)) {
                view.setTag(R.string.tag_key_translation_z, tzStart);
            }
        }

        /*
         * Z轴变低
         */
        PropertyValuesHolder tz = PropertyValuesHolder.ofFloat("translationZ", tzStart, SHADOW_END);
        /*
         * 控件的宽变小
         */
        PropertyValuesHolder sx = PropertyValuesHolder.ofFloat("scaleX", view.getScaleX(), SCALE_END);
        /*
         * 控件的高变小
         */
        PropertyValuesHolder sy = PropertyValuesHolder.ofFloat("scaleY", view.getScaleY(), SCALE_END);

        /*
         * 动画集合，所有动画一起播放
         */
        ObjectAnimator animatorD = ObjectAnimator.ofPropertyValuesHolder(view, tz, sx, sy).setDuration(ANIM_SPEED);
        animatorD.setInterpolator(interpolator);
        animatorD.start();
    }

    static void setAnimEndListener(AnimEndListener animEndListener) {
        mAnimEndListener = animEndListener;
    }

    /**
     * 动画结束监听
     */
    public interface AnimEndListener {
        /**
         * 当动画结束时，且触摸动作为”ACTION_UP"时才通知动画结束，并执行点击事件
         */
        void animEnd();
    }
}
