package com.example.feijibook.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.feijibook.R;

/**
 * Created by 你是我的 on 2019/3/11
 */

/*
 * 底部导航栏
 */
public class MyBottomBar extends RelativeLayout implements View.OnClickListener {
    private Paint mPaintPic;// 画图的画笔
    private Paint mPaintText;// 画字的画笔
    private Paint mPaintLine;// 画灰色线的画笔
    // 矩形top距离
    private Float mGroupTop = 55f;
    // 圆半径
    private Float mRadius = 69f;
    // Bar底部颜色
    private Integer mBarColor = Color.WHITE;
    // 中心的Bitmap
    private Bitmap mBitmap;
    // 中心图片上面移动的距离，默认中心点
    private Float mCenterBottom = -30f;
    // 底部按钮
    private RadioButton radioOne, radioTwo, radioThree, radioFour;
    private RelativeLayout radioCenter;
    RadioGroup radioGroup;// 按钮组合

    private int[] defRes = {R.drawable.detail_select_icon, R.drawable.chart_select_icon,
            R.drawable.find_select_icon, R.drawable.me_select_icon};

    private int centerImage = R.drawable.bottom_add_icon;

    //第一个按钮为默认按钮 记录当前选中按钮
    private Integer mDefaultPage = 0;

    //点击回调
    private OnRadioClickListener mListener;

    //避免重复点击 默认打开
    private Boolean mRepeated = true;

    // 中间图片的标题
    private String centerTitle = "记账";

    public MyBottomBar(Context context) {
        super(context);
    }

    public MyBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // 初始化画笔
        mPaintPic = new Paint();
        mPaintText = new Paint();
        mPaintLine = new Paint();
        // 初始化bitmap图片
        mBitmap = BitmapFactory.decodeResource(getResources(), centerImage);
        // 裁剪图片为统一大小
        mBitmap = zoomImage(mBitmap, 128, 128, true);
        // 设置背景透明色，防止onDraw不执行
        setBackgroundColor(Color.TRANSPARENT);
        setGravity(CENTER_HORIZONTAL);
        // 初始化控件
        initView(context);
        // 初始化点击
        initListener();
    }

    private void initListener() {
        //设置点击方法
        radioOne.setOnClickListener(this);
        radioTwo.setOnClickListener(this);
        radioThree.setOnClickListener(this);
        radioFour.setOnClickListener(this);
        radioCenter.setOnClickListener(this);

    }

    private void initView(Context context) {
        radioGroup = new RadioGroup(context);
        radioGroup.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        // 单选组合框向下偏移
        radioGroup.setPadding(0, mGroupTop.intValue(), 0, 0);
        radioGroup.setGravity(Gravity.CENTER_VERTICAL);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);

        // 创建第一个按钮实例
        radioOne = new RadioButton(context);
        // 设置布局参数
        radioOne.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,
                1.0f));
        radioOne.setPadding(0,17,0,0);
        // 设置对其方式
        radioOne.setGravity(Gravity.CENTER_HORIZONTAL);
        // 取消点击效果
        radioOne.setBackground(null);
        // 设置RadioButton默认样式为空
        radioOne.setButtonDrawable(null);
        // 设置文字和图片的间距
        radioOne.setCompoundDrawablePadding(3);
        //设置文字
        radioOne.setText("明细");
        // 设置文字大小
        radioOne.setTextSize(10f);

        //创建第二个按钮实例...
        radioTwo = new RadioButton(context);
        radioTwo.setPadding(0,17,0,0);
        radioTwo.setGravity(Gravity.CENTER_HORIZONTAL);
        radioTwo.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
        radioTwo.setBackground(null);
        radioTwo.setButtonDrawable(null);
        radioTwo.setCompoundDrawablePadding(3);
        radioTwo.setText("图表");
        radioTwo.setTextSize(10f);

        //创建中心按钮实例...
        radioCenter = new RelativeLayout(context);
        radioCenter.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
        radioCenter.setGravity(Gravity.CENTER_HORIZONTAL);

        //创建第四个按钮实例...
        radioThree = new RadioButton(context);
        radioThree.setPadding(0,17,0,0);
        radioThree.setGravity(Gravity.CENTER_HORIZONTAL);
        radioThree.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
        radioThree.setBackground(null);
        radioThree.setButtonDrawable(null);
        radioThree.setCompoundDrawablePadding(3);
        radioThree.setText("发现");
        radioThree.setTextSize(10f);

        //创建第五个按钮实例...
        radioFour = new RadioButton(context);
        radioFour.setPadding(0,17,0,0);
        radioFour.setGravity(Gravity.CENTER_HORIZONTAL);
        radioFour.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
        radioFour.setBackground(null);
        radioFour.setButtonDrawable(null);
        radioFour.setCompoundDrawablePadding(3);
        radioFour.setText("我的");
        radioFour.setTextSize(10f);

        // 设置资源文件
        setResoursePictures(defRes);

        // 为Radiogroup添加子View
        radioGroup.addView(radioOne);
        radioGroup.addView(radioTwo);
        radioGroup.addView(radioCenter);
        radioGroup.addView(radioThree);
        radioGroup.addView(radioFour);
        // 最后添加RadioGroup
        addView(radioGroup);

        // 设置默认按钮
        setDefaultPage();

    }

    private void setDefaultPage() {
        setDefaultPage(mDefaultPage);
    }

    /**
     * 设置默认打开按钮
     * @param defaultPage 默认位置
     */
    private void setDefaultPage(Integer defaultPage) {
        // 重置当前page
        switch (defaultPage) {
            case 0:
                radioOne.setChecked(true);
                break;
            case 1:
                radioTwo.setChecked(true);
                break;
            case 2:
                radioThree.setChecked(true);
                break;
            case 3:
                radioFour.setChecked(true);
                break;
            default:
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        // 设置画字画笔字体大小
        mPaintText.setTextSize(25f);
        mPaintText.setFakeBoldText(true);
        // 设置画笔颜色
        mPaintText.setColor(Color.BLACK);
        mPaintLine.setColor(Color.parseColor("#EBE9E9"));
        mPaintPic.setColor(mBarColor);
        // 仅描边
        mPaintLine.setStyle(Paint.Style.STROKE);
        // 画一个矩形，宽为父布局的宽度，高为父布局的高度减去mGroupTop
        canvas.drawRect(0, mGroupTop, getWidth(), getHeight(), mPaintPic);
        float stringWidth = mPaintText.measureText(centerTitle);
        mPaintLine.setStrokeWidth(2f);
        canvas.drawLine(0, mGroupTop, getWidth(), mGroupTop, mPaintLine);
        // 画一个圆，x轴距离父布局宽的一半，y轴距离为圆的的半径
        // 露出mGroupTop高的小半圆出来
        canvas.drawCircle(getWidth() / 2, mRadius, mRadius, mPaintPic);
        mPaintLine.setStrokeWidth(3f);
        mPaintLine.setColor(Color.parseColor("#EEE8E8"));
        // 画弧线
        RectF oval = new RectF(getWidth() / 2 - mRadius,
                0, getWidth() / 2 + mRadius,
                mRadius * 2);
        canvas.drawArc(oval, 172, 196, false, mPaintLine);
        // 绘制中心图片
        canvas.drawBitmap(mBitmap, getWidth() / 2 - mBitmap.getWidth() / 2,
                getHeight() / 2 - mBitmap.getHeight() / 2 + mCenterBottom, mPaintPic);
        // 绘制“记账”
        canvas.drawText(centerTitle, getWidth() / 2 - stringWidth / 2,
                getHeight() / 2 + mBitmap.getHeight() / 2 + 5, mPaintText);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置资源图片
     */
    public void setResoursePictures(int[] drawableTop) {
        if (drawableTop.length != 4) {
            return;
        }
        // 画的drawable的宽高是按drawable固定的宽高
        radioOne.setCompoundDrawablesRelativeWithIntrinsicBounds(null, this.getResources().getDrawable(drawableTop[0]), null, null);
        radioTwo.setCompoundDrawablesRelativeWithIntrinsicBounds(null, this.getResources().getDrawable(drawableTop[1]), null, null);
        radioThree.setCompoundDrawablesRelativeWithIntrinsicBounds(null, this.getResources().getDrawable(drawableTop[2]), null, null);
        radioFour.setCompoundDrawablesRelativeWithIntrinsicBounds(null, this.getResources().getDrawable(drawableTop[3]), null, null);
    }

    /**
     * 设置中心图片
     */
    public void setCenterImage(int centerImage) {
        this.centerImage = centerImage;
        mBitmap = zoomImage(mBitmap, 130, 130, true);
        invalidate();
    }

    /**
     * 设置中心图片
     */
    public void setCenterImage(int centerImage, int newWidth, int newHeight) {
        this.centerImage = centerImage;
        mBitmap = BitmapFactory.decodeResource(getResources(), centerImage);
        mBitmap = zoomImage(mBitmap, newWidth, newHeight, false);
        invalidate();
    }

    private Bitmap zoomImage(Bitmap bitmap, int newWidth, int newHeight, boolean fixed) {
        // 获取这个图片的宽和高
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = width / width;
        float scaleHeight = height / height;
        if (fixed) {
            if (newWidth < width) {
                scaleWidth = ((float) newWidth) / width;
            }
            if (newHeight < height) {
                scaleHeight = ((float) newHeight) / height;
            }
        } else {
            scaleWidth = ((float) newWidth) / width;
            scaleHeight = ((float) newHeight) / height;
        }
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, (int) width, (int) height, matrix, true);
    }

    public void setRadioText(String[] strings) {
        if (strings.length != 4) {
            return;
        }
        radioOne.setText(strings[0]);
        radioTwo.setText(strings[1]);
        radioThree.setText(strings[2]);
        radioFour.setText(strings[3]);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                float x = event.getX();
                float y = event.getY();
                // 中间图标的点击事件
                if (x >= getWidth() / 2 - 69 && x <= getWidth() / 2 + 69
                        && y < mGroupTop) {
                    mListener.onClick(2);
                    return true;
                }
                break;
            default:
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        // 中间按钮不参与单选
        if (v != radioCenter) {
            radioGroup.check(v.getId());
        }
        if (v == radioOne) {
            clickInterception(0);
        } else if (v == radioTwo) {
            clickInterception(1);
        } else if (v == radioCenter) {
            mListener.onClick(2);
        } else if (v == radioThree) {
            clickInterception(3);
        } else if (v == radioFour) {
            clickInterception(4);
        }
    }

    /**
     * 点击拦截，过滤重复点击1
     * @param page 点击位置
     */
    private void clickInterception(int page) {
        // 重复过滤开启并且点击记录重复视为重复点击
        if (mRepeated && page == mDefaultPage) {
            return;
        }
        // 记录新的页码
        mDefaultPage = page;
        // 开始回调
        mListener.onClick(page);
    }

    public void setOnRadioClickListener(OnRadioClickListener onRadioClickListener) {
        mListener = onRadioClickListener;
    }

    /**
     * 按钮点击回调
     */
    public interface OnRadioClickListener {
        void onClick(int position);
    }
}
