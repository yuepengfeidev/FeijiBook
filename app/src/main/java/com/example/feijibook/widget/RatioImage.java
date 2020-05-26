package com.example.feijibook.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;

/**
 * RatioImage
 *
 * @author PengFei Yue
 * @date 2019/8/15
 * @description 从Bitmap中间截取与该View大小比例适配的图片
 */
public class RatioImage extends View {
    private Bitmap mBitmap;
    private Matrix matrix = new Matrix();
    private boolean firstMesure = false;

    public RatioImage(Context context) {
        this(context, null);
    }

    public RatioImage(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackground(ContextCompat.getDrawable(MyApplication.sContext, R.drawable.bg_sky_blue));
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        firstMesure = true;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureImage();
    }

    public void measureImage() {
        if (mBitmap != null && firstMesure) {
            firstMesure = false;
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            int imageWidth = mBitmap.getWidth();
            int imageHeight = mBitmap.getHeight();
            float ratio;
            // 获取与该View相同长宽比例的Bitmap的长宽
            if (imageWidth > imageHeight) {
                // 该View的长宽比例
                ratio = (float) width / height;
                imageWidth *= ratio;
                // 从图片中间截取对应长宽比例的图片
                mBitmap = Bitmap.createBitmap(mBitmap, (mBitmap.getWidth() - imageWidth), 0,
                        imageWidth, imageHeight);
            } else if (imageHeight > imageWidth) {
                ratio = (float) height / width;
                imageHeight *= ratio;
                mBitmap = Bitmap.createBitmap(mBitmap, 0, (mBitmap.getHeight() - imageHeight) / 2,
                        imageWidth, imageHeight);
            }

            // 截取好的图片缩放到与View相同大小
            if (imageHeight != height && imageWidth != width) {
                float scaleWidth = (float) width / imageWidth;
                float scaleHeight = (float) height / imageHeight;
                matrix.postScale(scaleWidth, scaleHeight);
                mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, imageWidth, imageHeight, matrix, true);
            }

        }
    }

    private void setBackGround() {
        if (mBitmap != null) {
            setBackground(new BitmapDrawable(getResources(), mBitmap));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setBackGround();
        super.onDraw(canvas);
    }
}
