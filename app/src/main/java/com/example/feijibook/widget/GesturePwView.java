package com.example.feijibook.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.util.SoundShakeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * GesturePwView
 *
 * @author PengFei Yue
 * @date 2019/11/3
 * @description 九宫格手势密码控件
 */
public class GesturePwView extends View {
    /**
     * 外部大圆圈半径
     */
    private float oCircleRadius;
    /**
     * 内部实心圆半径
     */
    private int iCircleRadius;
    //A的坐标
    private float a1, b1;
    //B的坐标
    private float a2, b2;
    //C的坐标
    private float a3, b3;
    //D的坐标
    private float a4, b4;
    //E的坐标
    private float a5, b5;
    //F的坐标
    private float a6, b6;
    //G的坐标
    private float a7, b7;
    //H的坐标
    private float a8, b8;
    //I的坐标
    private float a9, b9;
    /**
     * 绘制大圆圈用到的画笔
     */
    private Paint oCirclePaint;
    private Paint oCircleErrorPaint;
    /**
     * 绘制小实心圆用到的画笔
     */
    private Paint iCirclePaint;
    private Paint iCircleErrorPaint;
    private Paint linePaint;
    private Paint lineErrorPaint;
    /**
     * 九个正方形区域
     */
    private RectF rt1, rt2, rt3, rt4, rt5, rt6, rt7, rt8, rt9;
    /**
     * 画完手势后则称的手势密码“ABCDEFGHI”
     */
    private String passwordValue = "";
    /**
     * 存储线段起始及终止坐标的二维数组
     */
    float[][] fts;
    /**
     * 存储所有经过的点的坐标
     */
    List<float[][]> listCoordinate = new ArrayList();
    /**
     * 经过圆的数量
     */
    int num = 0;
    /**
     * 正在滑动的线段的起点
     */
    Point moveLineSPoint = new Point();
    Point moveLineEPoint = new Point();
    /**
     * 手势错误
     */
    private boolean isError = false;
    /**
     * 手指抬起状态
     */
    private boolean upState = false;
    public OnGestureListener mOnGestureListener;

    public GesturePwView(Context context) {
        this(context, null);
    }

    public GesturePwView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GesturePwView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) MyApplication.sContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metric);
        setBackground(ContextCompat.getDrawable(MyApplication.sContext, R.color.sky_blue_like));
        // 屏幕的高度宽度
        int displayWidth = metric.widthPixels;
        int displayHeight = metric.heightPixels;
        //以下计算是根据屏幕调试出来的合理大小
        oCircleRadius = (displayWidth / 3) / 4;
        iCircleRadius = (displayWidth / 3) / 9;
        //点A的横坐标，及纵坐标
        a1 = (displayWidth / 3) / 2;
        b1 = (displayWidth / 3) / 2 + (displayHeight - displayWidth) / 2;
        //B点坐标
        a2 = (displayWidth / 3) + (displayWidth / 3) / 2;
        b2 = b1;
        //C点坐标
        a3 = (displayWidth / 3) * 2 + (displayWidth / 3) / 2;
        b3 = b1;
        //D点坐标
        a4 = a1;
        b4 = (displayWidth / 3) + (displayWidth / 3) / 2 + (displayHeight - displayWidth) / 2;
        //E点坐标
        a5 = a2;
        b5 = b4;
        //F点坐标
        a6 = a3;
        b6 = b4;
        //G点坐标
        a7 = a1;
        b7 = (displayWidth / 3) * 2 + (displayWidth / 3) / 2 + (displayHeight - displayWidth) / 2;
        //H点坐标
        a8 = a5;
        b8 = b7;
        //I点坐标
        a9 = a6;
        b9 = b7;

        // 左上角坐标（a1 - iCircleRadius, b1 - iCircleRadius）及右下角坐标（a1 + iCircleRadius, b1 + iCircleRadius）
        rt1 = new RectF(a1 - oCircleRadius, b1 - oCircleRadius, a1 + oCircleRadius, b1 + oCircleRadius);
        rt2 = new RectF(a2 - oCircleRadius, b2 - oCircleRadius, a2 + oCircleRadius, b2 + oCircleRadius);
        rt3 = new RectF(a3 - oCircleRadius, b3 - oCircleRadius, a3 + oCircleRadius, b3 + oCircleRadius);
        rt4 = new RectF(a4 - oCircleRadius, b4 - oCircleRadius, a4 + oCircleRadius, b4 + oCircleRadius);
        rt5 = new RectF(a5 - oCircleRadius, b5 - oCircleRadius, a5 + oCircleRadius, b5 + oCircleRadius);
        rt6 = new RectF(a6 - oCircleRadius, b6 - oCircleRadius, a6 + oCircleRadius, b6 + oCircleRadius);
        rt7 = new RectF(a7 - oCircleRadius, b7 - oCircleRadius, a7 + oCircleRadius, b7 + oCircleRadius);
        rt8 = new RectF(a8 - oCircleRadius, b8 - oCircleRadius, a8 + oCircleRadius, b8 + oCircleRadius);
        rt9 = new RectF(a9 - oCircleRadius, b9 - oCircleRadius, a9 + oCircleRadius, b9 + oCircleRadius);

        oCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        oCircleErrorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 外圈画笔类型为描边
        oCirclePaint.setStyle(Paint.Style.STROKE);
        oCirclePaint.setStrokeWidth(4);
        oCircleErrorPaint.setStyle(Paint.Style.STROKE);
        oCircleErrorPaint.setStrokeWidth(4);
        iCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        iCircleErrorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(4);
        lineErrorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lineErrorPaint.setStrokeWidth(4);

        iCirclePaint.setColor(Color.BLACK);
        oCirclePaint.setColor(Color.BLACK);
        linePaint.setColor(Color.BLACK);

        iCircleErrorPaint.setColor(Color.RED);
        oCircleErrorPaint.setColor(Color.RED);
        lineErrorPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (passwordValue.contains("A")) {
            if (isError) {
                canvas.drawCircle(a1, b1, oCircleRadius, oCircleErrorPaint);
                canvas.drawCircle(a1, b1, iCircleRadius, iCircleErrorPaint);
            } else {
                //渲染大圆圈,圆心（a1,b1）半径oCircleRadius，画笔oCirclePaint
                canvas.drawCircle(a1, b1, oCircleRadius, oCirclePaint);
                //轨迹经过圆A
                canvas.drawCircle(a1, b1, iCircleRadius, iCirclePaint);
            } } else {
            //渲染大圆圈,圆心（a1,b1）半径oCircleRadius，画笔oCirclePaint
            canvas.drawCircle(a1, b1, oCircleRadius, oCirclePaint); }
        if (passwordValue.contains("B")) {
            if (isError) {
                canvas.drawCircle(a2, b2, oCircleRadius, oCircleErrorPaint);
                //轨迹经过圆B
                canvas.drawCircle(a2, b2, iCircleRadius, iCircleErrorPaint);
            } else {
                canvas.drawCircle(a2, b2, oCircleRadius, oCirclePaint);
                //轨迹经过圆B
                canvas.drawCircle(a2, b2, iCircleRadius, iCirclePaint);
            }
        } else {
            canvas.drawCircle(a2, b2, oCircleRadius, oCirclePaint);
        }
        if (passwordValue.contains("C")) {
            if (isError) {
                canvas.drawCircle(a3, b3, oCircleRadius, oCircleErrorPaint);
                //轨迹经过圆C
                canvas.drawCircle(a3, b3, iCircleRadius, iCircleErrorPaint);
            } else {
                canvas.drawCircle(a3, b3, oCircleRadius, oCirclePaint);
                //轨迹经过圆C
                canvas.drawCircle(a3, b3, iCircleRadius, iCirclePaint);
            }

        } else {
            canvas.drawCircle(a3, b3, oCircleRadius, oCirclePaint);
        }
        if (passwordValue.contains("D")) {
            if (isError) {
                canvas.drawCircle(a4, b4, oCircleRadius, oCircleErrorPaint);
                //轨迹经过圆D
                canvas.drawCircle(a4, b4, iCircleRadius, iCircleErrorPaint);
            } else {
                canvas.drawCircle(a4, b4, oCircleRadius, oCirclePaint);
                //轨迹经过圆D
                canvas.drawCircle(a4, b4, iCircleRadius, iCirclePaint);
            }

        } else {
            canvas.drawCircle(a4, b4, oCircleRadius, oCirclePaint);
        }
        if (passwordValue.contains("E")) {
            if (isError) {
                canvas.drawCircle(a5, b5, oCircleRadius, oCircleErrorPaint);
                //轨迹经过圆E
                canvas.drawCircle(a5, b5, iCircleRadius, iCircleErrorPaint);
            } else {
                canvas.drawCircle(a5, b5, oCircleRadius, oCirclePaint);
                //轨迹经过圆E
                canvas.drawCircle(a5, b5, iCircleRadius, iCirclePaint);
            }

        } else {
            canvas.drawCircle(a5, b5, oCircleRadius, oCirclePaint);
        }
        if (passwordValue.contains("F")) {
            if (isError) {
                canvas.drawCircle(a6, b6, oCircleRadius, oCircleErrorPaint);
                //轨迹经过圆F
                canvas.drawCircle(a6, b6, iCircleRadius, iCircleErrorPaint);
            } else {
                canvas.drawCircle(a6, b6, oCircleRadius, oCirclePaint);
                //轨迹经过圆F
                canvas.drawCircle(a6, b6, iCircleRadius, iCirclePaint);
            }

        } else {
            canvas.drawCircle(a6, b6, oCircleRadius, oCirclePaint);
        }

        if (passwordValue.contains("G")) {
            if (isError) {
                canvas.drawCircle(a7, b7, oCircleRadius, oCircleErrorPaint);
                //轨迹经过圆G
                canvas.drawCircle(a7, b7, iCircleRadius, iCircleErrorPaint);
            } else {
                canvas.drawCircle(a7, b7, oCircleRadius, oCirclePaint);
                //轨迹经过圆G
                canvas.drawCircle(a7, b7, iCircleRadius, iCirclePaint);
            }

        } else {
            canvas.drawCircle(a7, b7, oCircleRadius, oCirclePaint);
        }
        if (passwordValue.contains("H")) {
            if (isError) {
                canvas.drawCircle(a8, b8, oCircleRadius, oCircleErrorPaint);
                //轨迹经过圆H
                canvas.drawCircle(a8, b8, iCircleRadius, iCircleErrorPaint);
            } else {
                canvas.drawCircle(a8, b8, oCircleRadius, oCirclePaint);
                //轨迹经过圆H
                canvas.drawCircle(a8, b8, iCircleRadius, iCirclePaint);
            }

        } else {
            canvas.drawCircle(a8, b8, oCircleRadius, oCirclePaint);
        }

        if (passwordValue.contains("I")) {
            if (isError) {
                canvas.drawCircle(a9, b9, oCircleRadius, oCircleErrorPaint);
                //轨迹经过圆I
                canvas.drawCircle(a9, b9, iCircleRadius, iCircleErrorPaint);
            } else {
                canvas.drawCircle(a9, b9, oCircleRadius, oCirclePaint);
                //轨迹经过圆I
                canvas.drawCircle(a9, b9, iCircleRadius, iCirclePaint);
            }
        } else {
            canvas.drawCircle(a9, b9, oCircleRadius, oCirclePaint);
        }

        for (int i = 0; i < listCoordinate.size(); i++) {
            float[][] lineCoordinate = listCoordinate.get(i);
            float startX = lineCoordinate[0][0];
            float startY = lineCoordinate[0][1];
            float stopX = lineCoordinate[1][0];
            float stopY = lineCoordinate[1][1];
            if (isError) {
                canvas.drawLine(startX, startY, stopX, stopY, lineErrorPaint);
            } else {
                canvas.drawLine(startX, startY, stopX, stopY, linePaint);
            }
        }

        if (num < 9 && !"".equals(passwordValue) && !upState) {
            // 当经过一点且经过所有点之前，绘制手势路径
            canvas.drawLine(moveLineSPoint.x, moveLineSPoint.y, moveLineEPoint.x, moveLineEPoint.y, linePaint);
        }
    }

    public void clearGesture() {
        listCoordinate.clear();
        num = 0;
        passwordValue = "";
        invalidate();
    }

    public void errorGesture() {
        isError = true;
        new Handler().postDelayed(() -> {
            isError = false;
            clearGesture();
        }, 700);
        invalidate();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                upState = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                //X坐标点
                int tX = (int) event.getX();
                //Y坐标点
                int tY = (int) event.getY();

                moveLineEPoint.set(tX, tY);

                //首次经过圆A
                if (rt1.contains(tX, tY) && !passwordValue.contains("A")) {
                    passwordValue += "A";
                    moveLineSPoint.set((int) a1, (int) b1);
                    //num经过的圆的数量
                    //num != 0代表不是第一个经过的圆，第一个经过的圆只能是线段的起始坐标不能是线段的终止坐标
                    if (num != 0) {
                        //线段的终止坐标
                        fts[1] = new float[]{a1, b1};
                        //存储线段起及始终止坐标的二维数组存储到列表中
                        listCoordinate.add(fts);
                    }
                    //初始化存储线段坐标的二维数组
                    fts = new float[2][2];
                    //线段的起始坐标
                    fts[0] = new float[]{a1, b1};
                    num += 1;
                } else if (rt2.contains(tX, tY) && !passwordValue.contains("B")) {
                    //首次经过圆B
                    passwordValue += "B";
                    moveLineSPoint.set((int) a2, (int) b2);
                    if (num != 0) {
                        //线段的终止坐标
                        fts[1] = new float[]{a2, b2};
                        listCoordinate.add(fts);
                    }
                    //初始化存储线段坐标的二维数组
                    fts = new float[2][2];
                    //线段的起始坐标
                    fts[0] = new float[]{a2, b2};
                    num += 1;
                } else if (rt3.contains(tX, tY) && !passwordValue.contains("C")) {
                    //首次经过圆C
                    passwordValue += "C";
                    moveLineSPoint.set((int) a3, (int) b3);
                    if (num != 0) {
                        //线段的终止坐标
                        fts[1] = new float[]{a3, b3};
                        listCoordinate.add(fts);
                    }
                    //初始化存储线段坐标的二维数组
                    fts = new float[2][2];
                    //线段的起始坐标
                    fts[0] = new float[]{a3, b3};
                    num += 1;
                } else if (rt4.contains(tX, tY) && !passwordValue.contains("D")) {
                    //首次经过圆D
                    passwordValue += "D";
                    moveLineSPoint.set((int) a4, (int) b4);
                    if (num != 0) {
                        //线段的终止坐标
                        fts[1] = new float[]{a4, b4};
                        listCoordinate.add(fts);
                    }
                    //初始化存储线段坐标的二维数组
                    fts = new float[2][2];
                    //线段的起始坐标
                    fts[0] = new float[]{a4, b4};
                    num += 1;
                } else if (rt5.contains(tX, tY) && !passwordValue.contains("E")) {
                    //首次经过圆E
                    passwordValue += "E";
                    moveLineSPoint.set((int) a5, (int) b5);
                    if (num != 0) {
                        //线段的终止坐标
                        fts[1] = new float[]{a5, b5};
                        listCoordinate.add(fts);
                    }
                    //初始化存储线段坐标的二维数组
                    fts = new float[2][2];
                    //线段的起始坐标
                    fts[0] = new float[]{a5, b5};
                    num += 1;
                } else if (rt6.contains(tX, tY) && !passwordValue.contains("F")) {
                    //首次经过圆F
                    passwordValue += "F";
                    moveLineSPoint.set((int) a6, (int) b6);
                    if (num != 0) {
                        //线段的终止坐标
                        fts[1] = new float[]{a6, b6};
                        listCoordinate.add(fts);
                    }
                    //初始化存储线段坐标的二维数组
                    fts = new float[2][2];
                    //线段的起始坐标
                    fts[0] = new float[]{a6, b6};
                    num += 1;
                } else if (rt7.contains(tX, tY) && !passwordValue.contains("G")) {
                    //首次经过圆G
                    passwordValue += "G";
                    moveLineSPoint.set((int) a7, (int) b7);
                    if (num != 0) {
                        //线段的终止坐标
                        fts[1] = new float[]{a7, b7};
                        listCoordinate.add(fts);
                    }
                    //初始化存储线段坐标的二维数组
                    fts = new float[2][2];
                    //线段的起始坐标
                    fts[0] = new float[]{a7, b7};
                    num += 1;
                } else if (rt8.contains(tX, tY) && !passwordValue.contains("H")) {
                    //首次经过圆H
                    passwordValue += "H";
                    moveLineSPoint.set((int) a8, (int) b8);
                    if (num != 0) {
                        //线段的终止坐标
                        fts[1] = new float[]{a8, b8};
                        listCoordinate.add(fts);
                    }
                    //初始化存储线段坐标的二维数组
                    fts = new float[2][2];
                    //线段的起始坐标
                    fts[0] = new float[]{a8, b8};
                    num += 1;
                } else if (rt9.contains(tX, tY) && !passwordValue.contains("I")) {
                    //首次经过圆I
                    passwordValue += "I";
                    moveLineSPoint.set((int) a9, (int) b9);
                    if (num != 0) {
                        //线段的终止坐标
                        fts[1] = new float[]{a9, b9};
                        listCoordinate.add(fts);
                    }
                    //初始化存储线段坐标的二维数组
                    fts = new float[2][2];
                    //线段的起始坐标
                    fts[0] = new float[]{a9, b9};
                    num += 1;
                }
                invalidate();// 刷新画布，回调onDraw()方法
                break;
            case MotionEvent.ACTION_UP:
                upState = true;
                if (num < 4 && num > 0) {
                    // 经过的点小于4个，则显示红色错误，7ms后清除手势
                    errorGesture();
                    // 错误震动
                    SoundShakeUtil.shakePhone();
                    mOnGestureListener.fewerThanFour();
                } else {
                    mOnGestureListener.onGestureFinish(passwordValue);
                }
                break;
            default:
        }
        return super.onTouchEvent(event);
    }

    public void setOnGestureListener(OnGestureListener onGestureListener) {
        mOnGestureListener = onGestureListener;
    }

    public interface OnGestureListener {
        /**
         * 成功完成手势输入后，返回输入的手势内容
         *
         * @param gesturePw 手势内容
         */
        void onGestureFinish(String gesturePw);

        /**
         * 少于四个错误
         */
        void fewerThanFour();
    }
}
