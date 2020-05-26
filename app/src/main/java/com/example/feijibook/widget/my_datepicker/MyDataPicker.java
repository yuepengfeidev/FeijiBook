package com.example.feijibook.widget.my_datepicker;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.feijibook.R;
import com.example.feijibook.util.DateFormatUtils;
import com.example.feijibook.util.SoundShakeUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by 你是我的 on 2019/3/3
 */
public class MyDataPicker implements View.OnClickListener, PickerView.OnSelectListener {
    private Context mContext;
    private Callback mCallback;
    private Calendar mBeginTime, mEndTime, mSelectedTime;
    private boolean mCanDialogShow;
    private PickerView mDpvYear, mDpvMonth, mDpvDay;
    private int mBeginYear, mBeginMonth, mBeginDay,
            mEndYear, mEndMonth, mEndDay;

    private Dialog mPickerDialog;
    // 显示日
    private boolean mCanShowDay;


    /**
     * 时间单位的最大显示值
     */
    private static final int MAX_MONTH_UNIT = 12;

    // 单位时间格式
    private DecimalFormat mDecimalFormat = new DecimalFormat("00");

    /**
     * 级联滚动延迟时间
     */
    private static final long LINKAGE_DELAY_DEFAULT = 100L;

    // 时间容器
    private List<String> mYearUnits = new ArrayList<>(), mMonthUnits = new ArrayList<>(),
            mDayUnits = new ArrayList<>();

    /**
     * 时间选择结果回调接口
     */
    public interface Callback {
        void onTimeSelected(long timestamp);
    }

    /**
     * 通过日期字符串初始换时间选择器
     *
     * @param context      Activity Context
     * @param callback     选择结果回调
     * @param beginDateStr 日期字符串，格式为 yyyy-MM-dd HH:mm
     * @param endDateStr   日期字符串，格式为 yyyy-MM-dd HH:mm
     */
    public MyDataPicker(Context context, Callback callback, String beginDateStr, String endDateStr) {
        this(context, callback, DateFormatUtils.str2Long(beginDateStr, true),
                DateFormatUtils.str2Long(endDateStr, true));
    }

    /**
     * 通过时间戳初始换时间选择器，毫秒级别
     *
     * @param context        Activity Context
     * @param callback       选择结果回调
     * @param beginTimestamp 毫秒级时间戳
     * @param endTimestamp   毫秒级时间戳
     */
    public MyDataPicker(Context context, Callback callback, long beginTimestamp, long endTimestamp) {
        if (context == null || callback == null || beginTimestamp <= 0 ||
                beginTimestamp >= endTimestamp) {
            mCanDialogShow = false;
            return;
        }

        mContext = context;
        mCallback = callback;
        mBeginTime = Calendar.getInstance();
        mBeginTime.setTimeInMillis(beginTimestamp);// 设置开始时间到calendar
        mEndTime = Calendar.getInstance();
        mEndTime.setTimeInMillis(endTimestamp);// 设置结束时间
        mSelectedTime = Calendar.getInstance();

        initView();
        initData();
        mCanDialogShow = true;
    }

    private void initData() {
        // 获取到从格林威治时间到现在的时间，设置进入Calendar，则选择时间为当前时间
        mSelectedTime.setTimeInMillis(mBeginTime.getTimeInMillis());

        // 获取时间开始值
        mBeginYear = mBeginTime.get(Calendar.YEAR);
        // Calendar.MONTH 值为 0-11
        mBeginMonth = mBeginTime.get(Calendar.MONTH) + 1;
        mBeginDay = mBeginTime.get(Calendar.DAY_OF_MONTH);

        // 获取时间结束值
        mEndYear = mEndTime.get(Calendar.YEAR);
        mEndMonth = mEndTime.get(Calendar.MONTH) + 1;
        mEndDay = mEndTime.get(Calendar.DAY_OF_MONTH);

        // 是否要跨时间（跨年，跨月等）
        boolean canSpanYear = mBeginYear != mEndYear;
        boolean canSpanMonth = !canSpanYear && mBeginMonth != mEndMonth;
        boolean canSpanDay = !canSpanMonth && mBeginDay != mEndDay;

        if (canSpanYear) {
            initDateUnits(23, mBeginTime.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else if (canSpanMonth) {
            initDateUnits(mEndMonth, mBeginTime.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else if (canSpanDay) {
            initDateUnits(mEndMonth, mEndDay);
        }
    }

    // 初始化时间容器
    private void initDateUnits(int endMonth, int endDay) {
        for (int i = mBeginYear; i <= mEndYear; i++) {
            mYearUnits.add(String.valueOf(i));
        }

        for (int i = mBeginMonth; i <= endMonth; i++) {
            mMonthUnits.add(mDecimalFormat.format(i));
        }

        for (int i = mBeginDay; i <= endDay; i++) {
            mDayUnits.add(mDecimalFormat.format(i));
        }


        mDpvYear.setDataList(mYearUnits);
        mDpvYear.setSelected(0);
        mDpvMonth.setDataList(mMonthUnits);
        mDpvMonth.setSelected(0);
        mDpvDay.setDataList(mDayUnits);
        mDpvDay.setSelected(0);

        setCanScroll();
    }

    private void initView() {
        mPickerDialog = new Dialog(mContext, R.style.date_picker_dialog);
        mPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPickerDialog.setContentView(R.layout.dialog_date_picker);

        Window window = mPickerDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }

        mPickerDialog.findViewById(R.id.tv_cancel).setOnClickListener(this);
        mPickerDialog.findViewById(R.id.tv_confirm).setOnClickListener(this);

        mDpvYear = mPickerDialog.findViewById(R.id.dpv_year);
        mDpvYear.setOnSelectListener(this);
        mDpvMonth = mPickerDialog.findViewById(R.id.dpv_month);
        mDpvMonth.setOnSelectListener(this);
        mDpvDay = mPickerDialog.findViewById(R.id.dpv_day);
        mDpvDay.setOnSelectListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                break;
            case R.id.tv_confirm:
                if (mCallback != null) {
                    mCallback.onTimeSelected(mSelectedTime.getTimeInMillis());
                }
                break;
            default:
        }

        // 点击取消关闭dialog
        if (mPickerDialog != null && mPickerDialog.isShowing()) {
            mPickerDialog.dismiss();
        }
    }

    @Override
    public void onSelect(View view, String selected) {
        if (view == null || TextUtils.isEmpty(selected)) {
            return;
        }

        int timeUnit;
        try {
            timeUnit = Integer.parseInt(selected);
        } catch (Throwable ignored) {
            return;
        }

        switch (view.getId()) {
            case R.id.dpv_year:
                mSelectedTime.set(Calendar.YEAR, timeUnit);
                linkageMontUnit(true, LINKAGE_DELAY_DEFAULT);
                break;
            case R.id.dpv_month:
                // 防止类似 2018/12/31 滚动到11月时因溢出变成 2018/12/01
                int lastSelectedMonth = mSelectedTime.get(Calendar.MONTH) + 1;
                mSelectedTime.add(Calendar.MONTH, timeUnit - lastSelectedMonth);
                linkageDayUnit(true, LINKAGE_DELAY_DEFAULT);
                break;
            case R.id.dpv_day:
                mSelectedTime.set(Calendar.DAY_OF_MONTH, timeUnit);
                break;
            default:
        }
    }

    /**
     * 联动“月”变化
     *
     * @param showAnim 是否展示滚动动画
     * @param delay    联动下一级延迟时间
     */
    private void linkageMontUnit(final boolean showAnim, final long delay) {
        int minMonth;
        int maxMonth;
        int selectedYear = mSelectedTime.get(Calendar.YEAR);
        if (mBeginYear == mEndYear) {// 开始年份为最后一年
            minMonth = mBeginMonth;
            maxMonth = mEndMonth;
        } else if (selectedYear == mBeginYear) {// 选择年份为开始年
            minMonth = mBeginMonth;
            maxMonth = MAX_MONTH_UNIT;
        } else if (selectedYear == mEndYear) {// 选择年份为最后一年
            minMonth = 1;
            maxMonth = mEndMonth;
        } else {
            minMonth = 1;
            maxMonth = MAX_MONTH_UNIT;
        }

        // 重新初始化时间单元容器
        mMonthUnits.clear();
        for (int i = minMonth; i <= maxMonth; i++) {
            mMonthUnits.add(mDecimalFormat.format(i) + "月");
        }
        mDpvMonth.setDataList(mMonthUnits);

        // 确保联动时不会溢出或改变关联选中值
        int selectedMonth = getValueInRange(mSelectedTime.get(Calendar.MONTH) + 1, minMonth, maxMonth);
        mSelectedTime.set(Calendar.MONTH, selectedMonth - 1);
        mDpvMonth.setSelected(selectedMonth - minMonth);
        if (showAnim) {
            mDpvMonth.starAnim();
        }

        // 联动"日"的变化
        mDpvMonth.postDelayed(new Runnable() {
            @Override
            public void run() {
                linkageDayUnit(showAnim, delay);
            }
        }, delay);
    }

    /**
     * 联动“日”变化
     *
     * @param showAnim 是否展示滚动动画
     * @param delay    联动下一级延迟时间
     */
    private void linkageDayUnit(final boolean showAnim, final long delay) {
        int minDay;
        int maxDay;
        int selectedYear = mSelectedTime.get(Calendar.YEAR);
        int selectedMonth = mSelectedTime.get(Calendar.MONTH) + 1;
        // 开始年份为最后年份，开始月份为最后月份
        if (mBeginYear == mEndYear && mBeginMonth == mEndMonth) {
            minDay = mBeginDay;
            maxDay = mEndDay;
            // 选中最开始的一年和最开始的一月
        } else if (selectedYear == mBeginYear && selectedMonth == mBeginMonth) {
            minDay = mBeginDay;
            maxDay = mSelectedTime.getActualMaximum(Calendar.DAY_OF_MONTH);
        }// 选中最后一个月和最后一年
        else if (selectedYear == mEndYear && selectedMonth == mEndMonth) {
            minDay = 1;
            maxDay = mEndDay;
        } else {
            minDay = 1;
            maxDay = mSelectedTime.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        mDayUnits.clear();
        for (int i = minDay; i <= maxDay; i++) {
            mDayUnits.add(mDecimalFormat.format(i) + "日");
        }
        mDpvDay.setDataList(mDayUnits);

        int selectedDay = getValueInRange(mSelectedTime.get(Calendar.DAY_OF_MONTH), minDay, maxDay);
        mSelectedTime.set(Calendar.DAY_OF_MONTH, selectedDay);
        mDpvDay.setSelected(selectedDay - minDay);
        if (showAnim) {
            mDpvDay.starAnim();
        }

    }


    // 设置可以滚动
    private void setCanScroll() {
        mDpvYear.setCanScroll(mYearUnits.size() > 1);
        mDpvMonth.setCanScroll(mMonthUnits.size() > 1);
        mDpvDay.setCanScroll(mDayUnits.size() > 1);
    }

    // 获得后一位联动的值
    private int getValueInRange(int value, int minValue, int maxValue) {
        if (value < minValue) {
            return minValue;
        } else if (value > maxValue) {
            return maxValue;
        } else {
            return value;
        }
    }


    /**
     * 展示时间选择器
     *
     * @param dateStr 日期字符串，格式为 yyyy-MM 或 yyyy-MM-dd
     */
    public void show(String dateStr) {
        if (!canShow() || TextUtils.isEmpty(dateStr)) {
            return;
        }

        // 弹窗时，考虑用户体验，不展示滚动动画
        if (setSelectedTime(dateStr, false)) {
            mPickerDialog.show();
        }
    }

    /**
     * 展示年时间选择器
     *
     * @param dateStr 日期字符串，格式为 yyyy
     */
    public void showYear(String dateStr) {
        if (!canShow() || TextUtils.isEmpty(dateStr)) {
            return;
        }

        // 弹窗时，考虑用户体验，不展示滚动动画
        if (setSelectedTime2(dateStr, false)) {
            mPickerDialog.show();
        }
    }

    private boolean canShow() {
        return mCanDialogShow && mPickerDialog != null;
    }

    /**
     * 设置日期选择器的选中时间
     *
     * @param dateStr  日期字符串
     * @param showAnim 是否展示动画
     * @return 是否设置成功
     */
    public boolean setSelectedTime(String dateStr, boolean showAnim) {
        return canShow() && !TextUtils.isEmpty(dateStr)
                && setSelectedTime(DateFormatUtils.str2Long(dateStr, mCanShowDay), showAnim);
    }

    /**
     * 设置日期选择器的选中时间只有“年”
     *
     * @param dateStr  日期字符串
     * @param showAnim 是否展示动画
     * @return 是否设置成功
     */
    public boolean setSelectedTime2(String dateStr, boolean showAnim) {
        return canShow() && !TextUtils.isEmpty(dateStr)
                && setSelectedTime(DateFormatUtils.str2Long(dateStr), showAnim);
    }

    /**
     * 展示时间选择器
     *
     * @param timestamp 时间戳，毫秒级别
     */
    public void show(long timestamp) {
        if (!canShow()) {
            return;
        }

        if (setSelectedTime(timestamp, false)) {
            mPickerDialog.show();
        }
    }

    /**
     * 设置日期选择器的选中时间
     *
     * @param timestamp 毫秒级时间戳
     * @param showAnim  是否展示动画
     * @return 是否设置成功
     */
    public boolean setSelectedTime(long timestamp, boolean showAnim) {
        if (!canShow()) {
            return false;
        }
        if (timestamp < mBeginTime.getTimeInMillis()) {
            timestamp = mBeginTime.getTimeInMillis();
        } else if (timestamp > mEndTime.getTimeInMillis()) {
            timestamp = mEndTime.getTimeInMillis();
        }
        mSelectedTime.setTimeInMillis(timestamp);

        mYearUnits.clear();
        for (int i = mBeginYear; i <= mEndYear; i++) {
            mYearUnits.add(String.valueOf(i) + "年");
        }
        mDpvYear.setDataList(mYearUnits);
        // 选中选中项
        mDpvYear.setSelected(mSelectedTime.get(Calendar.YEAR) - mBeginYear);
        linkageMontUnit(showAnim, showAnim ? LINKAGE_DELAY_DEFAULT : 0);
        return true;
    }

    /**
     * 设置是否允许点击屏幕或物理返回键关闭
     */
    public void setCancelable(boolean cancelable) {
        if (!canShow()) {
            return;
        }

        mPickerDialog.setCancelable(cancelable);
    }

    /**
     * 设置日期控件是否显示"日"
     *
     * @param canShowDay 是否显示
     */

    public void setCanShowDay(boolean canShowDay) {
        if (!canShow()) {
            return;
        }

        // 显示"日"
        if (canShowDay) {
            mDpvDay.setVisibility(View.VISIBLE);
            // 设置年份和日分选择器靠中显示
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mDpvYear.getLayoutParams();
            layoutParams.setMargins(250, 0, 0, 0);
            mDpvYear.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) mDpvDay.getLayoutParams();
            layoutParams1.setMargins(0, 0, 240, 0);
            mDpvDay.setLayoutParams(layoutParams1);
        } else {
            mDpvDay.setVisibility(View.GONE);
            // 设置年份和月份选择器 靠近居中显示
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mDpvYear.getLayoutParams();
            layoutParams.setMargins(250, 0, 0, 0);
            mDpvYear.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) mDpvMonth.getLayoutParams();
            layoutParams1.setMargins(0, 0, 250, 0);
            mDpvMonth.setLayoutParams(layoutParams1);
        }
        mCanShowDay = canShowDay;
    }

    public void setOnlyShowYear() {
        if (!canShow()) {
            return;
        }

        mDpvDay.setVisibility(View.GONE);
        mDpvMonth.setVisibility(View.GONE);
    }

    /**
     * 设置日期控件是否可以循环滚动
     */
    public void setScrollLoop(boolean canLoop) {
        if (!canShow()) {
            return;
        }

        mDpvYear.setCanScrollLoop(canLoop);
        mDpvMonth.setCanScrollLoop(canLoop);
        mDpvDay.setCanScrollLoop(canLoop);
    }

    /**
     * 设置日期控件是否展示滚动动画
     */
    public void setCanShowAnim(boolean canShowAnim) {
        if (!canShow()) {
            return;
        }

        mDpvYear.setCanShowAnim(canShowAnim);
        mDpvMonth.setCanShowAnim(canShowAnim);
        mDpvDay.setCanShowAnim(canShowAnim);
    }


    /**
     * 销毁弹窗
     */
    public void onDestroy() {
        if (mPickerDialog != null) {
            mPickerDialog.dismiss();
            mPickerDialog = null;

            mDpvYear.onDestroy();
            mDpvMonth.onDestroy();
            mDpvDay.onDestroy();
        }
    }
}
