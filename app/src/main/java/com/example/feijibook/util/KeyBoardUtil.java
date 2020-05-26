package com.example.feijibook.util;


import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by 你是我的 on 2019/3/4
 */

/*
 * 切换软键盘的状态0
 * 如当前为收起变为弹出,若当前为弹出变为收起
 */
public class KeyBoardUtil {

    public static void toggleInput(Context context) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 切换软键盘的显示与隐藏
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // 强制隐藏关闭显示监听
    public static void hideInput(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // 软键盘关闭显示监听
    public static class SoftKeyBoardListener {
        private View rootView;// activity的根视图
        int rootViewVisibleHeight;// 记录根视图的显示高度
        private OnSoftBoardChangeListener mOnSoftBoardChangeListener;

        public SoftKeyBoardListener(Activity activity) {
            // 获取activity的根视图
            rootView = activity.getWindow().getDecorView();

            // 监听视图中全局布局发生改变h或者视图中的某个视图的可视状态发生改变
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // 获取当前根视图在屏幕上显示的大小
                    Rect rect = new Rect();
                    rootView.getWindowVisibleDisplayFrame(rect);
                    int visibleHeight = rect.height();
                    if (rootViewVisibleHeight == 0) {
                        rootViewVisibleHeight = visibleHeight;
                        return;
                    }

                    // 根视图显示高度没有变化，可以看作键盘显示或隐藏状态没有改变
                    if (rootViewVisibleHeight == visibleHeight) {
                        return;
                    }

                    // 根视图显示高度变小超过200，可以看作软键盘显示了
                    if (rootViewVisibleHeight - visibleHeight > 200) {
                        if (mOnSoftBoardChangeListener != null) {
                            mOnSoftBoardChangeListener.keyBoardShow(rootViewVisibleHeight - visibleHeight);
                        }
                        rootViewVisibleHeight = visibleHeight;
                        return;
                    }

                    // 根视图显示高度变大超过200，可以看作软键盘隐藏了
                    if (visibleHeight - rootViewVisibleHeight > 200) {
                        if (mOnSoftBoardChangeListener != null) {
                            mOnSoftBoardChangeListener.keyboardHide(visibleHeight - rootViewVisibleHeight);
                        }
                        rootViewVisibleHeight = visibleHeight;
                        return;
                    }
                }
            });
        }

        public void setOnSoftBoardChangeListener(OnSoftBoardChangeListener onSoftBoardChangeListener) {
            mOnSoftBoardChangeListener = onSoftBoardChangeListener;
        }

        public interface OnSoftBoardChangeListener {
            void keyBoardShow(int height);

            void keyboardHide(int height);
        }

        public void setListener(OnSoftBoardChangeListener onSoftBoardChangeListener) {
            setOnSoftBoardChangeListener(onSoftBoardChangeListener);
        }

    }

    // 当有底部虚拟按键时，获取其高度
    public static int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        }
        return 0;
    }

    // 获取屏幕大小
    public static Point getScreenSize(Activity activity){
        // 获取屏幕高度和宽度
        final WindowManager windowManager = activity.getWindowManager();
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point;
    }

    /**
     * 手动测量view的高度
     * @param view
     * @return
     */
    public static int mesureHeight(View view){
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int height = view.getMeasuredHeight();

        return height;
    }
}

