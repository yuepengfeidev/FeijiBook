package com.example.feijibook.widget.my_recyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.adapter.RecordRVAdapter;
import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.util.SoundShakeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * PullChangeLayout
 *
 * @author PengFei Yue
 * @date 2019/9/27
 * @description 自定义上下拖动切换月份的Recyclerview
 */
public class PullChangeRecyclerView extends LinearLayout implements View.OnTouchListener {
    MyRecyclerView myRecyclerView;
    View headHintView;
    TextView tvHeadHint;
    int headHintMarginTop;
    private MarginLayoutParams headMarginLayoutParams;

    View tailHintView;
    TextView tvTailHint;
    int tailHintMarginBottom;

    RecordRVAdapter mRecordRVAdapter;

    /**
     * 是否已经Layout过一次
     */
    private boolean isLayout = false;
    /**
     * 手指滑动的第一次y坐标
     */
    private float mY;
    /**
     * 按下时的x，y坐标
     */
    private float downX;
    private float downY;

    private static final int PULLING_DOWN = 1;
    private static final int PULLING_UP = 2;
    private static final int RELEASE_TO_LAST = 3;
    private static final int RELEASE_TO_NEXT = 4;
    private static final int NORMAL_STATE = 0;
    private int mCurState = NORMAL_STATE;

    private OnPullListener mOnPullListener;

    private boolean isFirstMove = false;

    List<DayRecord> mDayRecordList = new ArrayList<>();
    List<RecordDetail> mRecordDetailList = new ArrayList<>();

    public PullChangeRecyclerView(Context context) {
        this(context, null);
    }

    public PullChangeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullChangeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        headHintView = LayoutInflater.from(context)
                .inflate(R.layout.layout_hint_header, null, true);
        tvHeadHint = headHintView.findViewById(R.id.tv_hint_header);
        tailHintView = LayoutInflater.from(context)
                .inflate(R.layout.layout_hint_tailer, null, true);
        tvTailHint = tailHintView.findViewById(R.id.tv_hint_tailer);

        View rvView = LayoutInflater.from(context)
                .inflate(R.layout.layout_recycler_view, null, true);
        myRecyclerView = rvView.findViewById(R.id.rv_records);

        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        myRecyclerView.setLayoutManager(manager);
        mRecordRVAdapter = new RecordRVAdapter(context, myRecyclerView);
        RecyclerView.ItemDecoration mSectionDecoration = new SectionDecoration(context,
                new SectionDecoration.DecorationCallback() {
                    @Override
                    public String getGroupId(int position) {
                        RecordDetail recordDetail = mRecordDetailList.get(position);
                        if (recordDetail.getYear() != null && recordDetail.getMonth() != null
                                && recordDetail.getDay() != null) {
                            return recordDetail.getYear() + "-" + recordDetail.getMonth() + "-" + recordDetail.getDay();
                        }
                        return "-1";
                    }

                    @Override
                    public DayRecord getDayTotal(int position) {
                        RecordDetail recordDetail = mRecordDetailList.get(position);
                        for (DayRecord record : mDayRecordList) {
                            if (record.getYear().equals(recordDetail.getYear())
                                    && record.getMonth().equals(recordDetail.getMonth())
                                    && record.getDay().equals(recordDetail.getDay())) {
                                return record;
                            }
                        }
                        return null;
                    }
                });
        myRecyclerView.addItemDecoration(mSectionDecoration);
        myRecyclerView.setAdapter(mRecordRVAdapter);
        mRecordRVAdapter.setOnClickListener(new RecordRVAdapter.OnClickListener() {
            @Override
            public void deleteRecord(int position) {
                mOnPullListener.deleteRecord(position);
            }

            @Override
            public void clickItem(String id) {
                mOnPullListener.onItemClick(id);
            }
        });

        setOrientation(VERTICAL);
        // 添加下拉提示布局
        addView(headHintView, 0);
        addView(rvView, 1);
        // 添加上拉提示布局
        addView(tailHintView, 2);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!isLayout) {
            headHintMarginTop = -tvHeadHint.getHeight();
            headMarginLayoutParams = (MarginLayoutParams) headHintView.getLayoutParams();
            headMarginLayoutParams.topMargin = headHintMarginTop;
            headHintView.setLayoutParams(headMarginLayoutParams);

            // 此处取recyclerview的高度的负值，如果为tailHintView高度的负值的话，
            // 当recyclerView的item满出屏幕的滑，tailHintView在上拉的时候不会显示，会被Recyclerview挡住
            tailHintMarginBottom = -myRecyclerView.getHeight();
            MarginLayoutParams tailMarginLayoutParams = (MarginLayoutParams) tailHintView.getLayoutParams();
            tailMarginLayoutParams.bottomMargin = tailHintMarginBottom;
            tailHintView.setLayoutParams(tailMarginLayoutParams);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myRecyclerView.getLayoutParams();
            layoutParams.height = LayoutParams.MATCH_PARENT;
            myRecyclerView.setLayoutParams(layoutParams);
            myRecyclerView.setOnTouchListener(this);
            isLayout = true;
        }

    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    public void setList(List<DayRecord> dayRecordList, List<RecordDetail> recordDetailList) {
        mDayRecordList = dayRecordList;
        mRecordDetailList = recordDetailList;
        mRecordRVAdapter.setList(recordDetailList);
    }


    /**
     * RecyclerView位移进入动画
     *
     * @param fromHeight 进入的起始Y坐标
     */
    private void translateAnim(float fromHeight) {
        headMarginLayoutParams.topMargin = headHintMarginTop;
        headHintView.setLayoutParams(headMarginLayoutParams);
        tvHeadHint.setVisibility(GONE);
        tvTailHint.setVisibility(GONE);

        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0,
                fromHeight, 0);
        translateAnimation.setDuration((long) (300));
        myRecyclerView.startAnimation(translateAnimation);

    }

    /**
     * 点击抬起后的动画
     *
     * @param haveData true为切换的月份有数据，则显示进入动画，否则回弹动画
     */
    private void actionUpAnim(boolean haveData) {
        if (mCurState == RELEASE_TO_NEXT && haveData) {
            SoundShakeUtil.playSound(SoundShakeUtil.SWOOSH1_SOUND);
            translateAnim(-getHeight());
        } else if (mCurState == RELEASE_TO_LAST && haveData) {
            SoundShakeUtil.playSound(SoundShakeUtil.SWOOSH1_SOUND);
            translateAnim(getHeight());
        }
        int distance = headMarginLayoutParams.topMargin - headHintMarginTop;
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(distance, 0)
                .setDuration((long) (Math.abs(distance) * 0.7));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float cVal = (float) valueAnimator.getAnimatedValue();
                headMarginLayoutParams.topMargin = (int) (headHintMarginTop + cVal);
                headHintView.setLayoutParams(headMarginLayoutParams);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tvHeadHint.setVisibility(GONE);
                tvTailHint.setVisibility(GONE);
            }
        });
        valueAnimator.start();
    }

    private void updateHint() {
        if (mCurState != NORMAL_STATE) {
            if (mCurState == PULLING_DOWN) {
                tvHeadHint.setText("下拉查看下月数据");
            } else if (mCurState == PULLING_UP) {
                tvTailHint.setText("上拉查看上月数据");
            } else if (mCurState == RELEASE_TO_NEXT) {
                tvHeadHint.setText("松开查看下月数据");
            } else if (mCurState == RELEASE_TO_LAST) {
                tvTailHint.setText("松开查看上月数据");
            }
        }
    }

    /**
     * 关闭RecyclerView的删除按钮的打开状态
     */
    public void closeMenu() {
        if (myRecyclerView != null) {
            myRecyclerView.closeMenu();
        }
    }

    /**
     * 移除vRecyclerView的item
     *
     * @param position 移除item的位置
     */
    public void removeItem(int position) {
        mRecordDetailList.remove(position);
        mRecordRVAdapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView是否滑动到底部
     *
     * @return true : 到底部
     */
    private boolean isScrollBottom() {
        return !myRecyclerView.canScrollVertically(1);
    }

    /**
     * RecyclerView是否到顶部（是否还可以下滑）
     *
     * @return true :到顶部
     */
    private boolean isScrollTop() {
        return !myRecyclerView.canScrollVertically(-1);
    }

    public void setOnPullListener(OnPullListener onPullListener) {
        mOnPullListener = onPullListener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = motionEvent.getX();
                downY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(downY - motionEvent.getY()) < Math.abs(downX - motionEvent.getX())
                && !isFirstMove) {
                    // 如果竖向滑动距离小于横向滑动距离，则不执行上下拉动
                    break;
                }
                if (!isScrollTop() && !isScrollBottom()) {
                    // 如果没有滚动到顶部或底部，则获取第一次滑动的坐标
                    mY = motionEvent.getY();
                    if (headMarginLayoutParams.topMargin != headHintMarginTop) {
                        headMarginLayoutParams.topMargin = headHintMarginTop;
                        headHintView.setLayoutParams(headMarginLayoutParams);
                    }
                    break;
                } else if (!isFirstMove) {
                    // 如果滚动到顶部且底部，则获取第一次滑动的坐标
                    mY = motionEvent.getY();
                    tvHeadHint.setVisibility(VISIBLE);
                    tvTailHint.setVisibility(VISIBLE);
                    isFirstMove = true;
                }

                // 下拉时：deltaY > 0   上拉时：deltaY < 0
                int deltaY = (int) (motionEvent.getY() - mY);

                // 到顶部时，上拉不响应，到底部时，下拉不响应
                if (isScrollTop() && deltaY <= 0 && !isScrollBottom()) {
                    break;
                } else if (isScrollBottom() && deltaY >= 0 && !isScrollTop()) {
                    break;
                }

                // 阻尼效果
                int offset = 0;
                if (deltaY > 0) {
                    offset = (int) Math.pow(Math.abs(deltaY), 0.8);
                } else if (deltaY < 0) {
                    offset = (int) Math.pow(Math.abs(deltaY), 0.8) * -1;
                }

                headMarginLayoutParams.topMargin = headHintMarginTop + offset;
                headHintView.setLayoutParams(headMarginLayoutParams);

                if (headMarginLayoutParams.topMargin > headHintMarginTop
                        && headMarginLayoutParams.topMargin <= tvHeadHint.getHeight()) {
                    mCurState = PULLING_DOWN;
                } else if (headMarginLayoutParams.topMargin > tvHeadHint.getHeight()) {
                    mCurState = RELEASE_TO_NEXT;
                } else if (headMarginLayoutParams.topMargin < headHintMarginTop
                        && headMarginLayoutParams.topMargin >= headHintMarginTop * 3) {
                    mCurState = PULLING_UP;
                } else if (headMarginLayoutParams.topMargin < headHintMarginTop * 3) {
                    mCurState = RELEASE_TO_LAST;
                }
                updateHint();
                return true;
            case MotionEvent.ACTION_UP:
                boolean haveData = false;
                if (mCurState == RELEASE_TO_LAST) {
                    haveData = mOnPullListener.onPullUpRelease();
                } else if (mCurState == RELEASE_TO_NEXT) {
                    haveData = mOnPullListener.onPullDownRelease();
                }
                actionUpAnim(haveData);
                mCurState = NORMAL_STATE;
                isFirstMove = false;
                break;
            default:
        }
        return false;
    }

    public interface OnPullListener {

        boolean onPullUpRelease();

        /**
         * 下拉释放切换到上一个月份
         *
         * @return 上月是否有数据，有数据则执行切换月份列表的动画，true: 有数据
         */
        boolean onPullDownRelease();

        void deleteRecord(int position);

        void onItemClick(String id);
    }
}
