package com.example.feijibook.widget.my_recyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.DayRecord;

/**
 * Created by 你是我的 on 2019/3/11
 */
public class SectionDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private TextPaint mTextPaint;
    private Paint mLinePaint;
    private DecorationCallback mDecorationCallback;
    private int topGap;
    private int alignBottom;

    public SectionDecoration(Context context, DecorationCallback decorationCallback) {
        Resources resources = context.getResources();
        mDecorationCallback = decorationCallback;
        // 悬浮栏画笔
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#F7F6F6"));

        // 悬浮栏文字画笔
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(34);
        mTextPaint.setColor(ContextCompat.getColor(MyApplication.sContext,R.color.day_record_gray));

        // 绘制灰色线条画笔
        mLinePaint = new Paint();
        mLinePaint.setColor(ContextCompat.getColor(MyApplication.sContext,R.color.line_gray));

        // 决定悬浮栏的高度
        topGap = resources.getDimensionPixelOffset(R.dimen.float_height);
        // 决定文本的显示位置
        alignBottom = resources.getDimensionPixelSize(R.dimen.text_position);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        // 获取view在adapter中的位置
        int position = parent.getChildAdapterPosition(view);
        String groupId = mDecorationCallback.getGroupId(position);
        if ("-1".equals(groupId)) {
            return;
        }
        // 只有是第一个列表中第一个item或者同一组的第一个item才显示悬浮窗
        if (position == 0 || isFirstInGroup(position)) {
            outRect.top = topGap;
        } else {
            outRect.top = 0;
        }
    }

    /**
     * 绘制悬浮
     */
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int itemCount = state.getItemCount();
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        String preGroupId;
        String groupId = "-1";
        // 遍历所有显示的item，绘制装饰
        for (int i = 0; i < childCount; i++) {
            // 获取该显示的item的view
            View view = parent.getChildAt(i);
            // 获取该显示item的位置
            int position = parent.getChildAdapterPosition(view);

            preGroupId = groupId;
            groupId = mDecorationCallback.getGroupId(position);
            // 如果不是每组的第一item或值为空时，则绘制线条装饰，然后循环到下一个item
            if ("-1".equals(groupId) || groupId.equals(preGroupId)) {
                continue;
            }

            String textLine = mDecorationCallback.getGroupId(position);
            if (TextUtils.isEmpty(textLine)) {
                continue;// 如果item内容为空，则循环到下一个item
            }

            DayRecord dayRecord = mDecorationCallback.getDayTotal(position);
            String totalExpend = dayRecord.getDayTotalExpend();
            String totalIncome = dayRecord.getDayTotalIncome();
            String teContent = "支出：" + totalExpend;
            String tiContent = "收入：" + totalIncome;
            String dateText = dayRecord.getMonth() + "月" + dayRecord.getDay() + "日" + "  " + dayRecord.getWeek();

            int viewBottom = view.getBottom();
            float textY = Math.max(topGap, view.getTop());
            if (position + 1 < itemCount) {
                String nextGroupId = mDecorationCallback.getGroupId(position + 1);
                // 组内最后一个item进入了header
                if (!nextGroupId.equals(groupId) && viewBottom < textY) {
                    textY = viewBottom;
                }
            }
            // textY - topGap 决定了悬浮栏绘制的高度和位置
            c.drawRect(left, textY - topGap, right, textY, mPaint);
            //left+alignBottom 决定了文本往左偏移的多少（加-->向左移）
            //textY-alignBottom  决定了文本往上偏移的多少  (减-->向上移)
            c.drawText(dateText, left + alignBottom, textY - alignBottom, mTextPaint);

            // 如果 有收入 则先绘制 从右边先绘制 收入 金额
            if (totalIncome != null && Double.valueOf(totalIncome).intValue() != 0) {
                c.drawText(tiContent, right - mTextPaint.measureText(tiContent) - 30, textY - alignBottom, mTextPaint);
            }
            if (totalExpend != null && Double.valueOf(totalExpend).intValue() != 0) {
                // 没有 收入 直接从右 开始 绘制支出
                if (totalIncome == null || Integer.valueOf(totalIncome) == 0) {
                    c.drawText(teContent, right - mTextPaint.measureText(teContent) - 30, textY - alignBottom, mTextPaint);
                }// 有收入 从 收入 左边 开始绘制
                else {
                    c.drawText(teContent, right - mTextPaint.measureText(tiContent)
                            - mTextPaint.measureText(teContent) - 65, textY - alignBottom, mTextPaint);
                }
            }
            // 绘制悬浮栏顶部装饰线
            c.drawLine(left, textY - topGap, right, textY - topGap, mLinePaint);
            // 绘制悬浮栏底部装饰线
            c.drawLine(left, textY, right, textY, mLinePaint);
        }

    }

    /**
     * 绘制item
     */
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent,
                       @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            String groupId = mDecorationCallback.getGroupId(position);
            if ("-1".equals(groupId)) {
                return;// 值为空时不绘制
            }if (!isFirstInGroup(position)) {
                // 不是第一个则绘制装饰线
                c.drawLine(left + 152, view.getTop(),
                        right, view.getTop(), mLinePaint); } else {
                c.drawLine(left, view.getTop(), right, view.getTop(), mLinePaint);
            }if (position == childCount - 1) {
                // 最后一个item 绘制顶部装饰线
                c.drawLine(left, view.getBottom(), right,
                        view.getBottom(), mLinePaint); } } }

    private boolean isFirstInGroup(int position) {
        if (position == 0) {
            return true;
        } else {
            String preGroup = mDecorationCallback.getGroupId(position - 1);
            String groupId = mDecorationCallback.getGroupId(position);
            return !preGroup.equals(groupId);
        }
    }

    public interface DecorationCallback {
        String getGroupId(int position);

        DayRecord getDayTotal(int position);
    }
}
