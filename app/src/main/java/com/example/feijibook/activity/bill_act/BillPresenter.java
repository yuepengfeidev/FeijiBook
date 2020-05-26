package com.example.feijibook.activity.bill_act;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;

import com.example.feijibook.entity.BillBean;
import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.record_bean.YearRecord;
import com.example.feijibook.util.DateFormatUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * BillPresenter
 *
 * @author yuepengfei
 * @date 2019/7/23
 * @description
 */
public class BillPresenter implements BillContract.Presenter {
    private BillContract.View mView;
    private BillContract.Model mModel;
    private DecimalFormat mMonthFormat = new DecimalFormat("00");
    private DecimalFormat mMoneyFormat = new DecimalFormat("0.00");

    BillPresenter(Activity activity, BillContract.View view) {
        mView = view;
        mView.getAct(activity);
        mView.setPresenter(this);
    }

    @Override
    public void setFinishAct() {
        mView.finishAct();
    }

    @Override
    public void setStartActivity(Intent intent) {
        mView.startAct(intent);
    }

    @Override
    public void setInitDatePicker() {
        // 初始化时间选择器数据
        // 时间选择控件的最早和最晚选择时间
        String beginTime = "1998";
        long beginTimestamp = DateFormatUtils.str2Long(beginTime);
        String endTime = "2025";
        long endTimestamp = DateFormatUtils.str2Long(endTime);
        mView.initDatePicker(beginTimestamp, endTimestamp);
        mView.initDatePicker(beginTimestamp, endTimestamp);
    }

    @Override
    public void setShowDatePicker(String dateString) {
        // 通过 非数字 将字符串分割出来
        String[] date = dateString.split("\\D");
        mView.showDatePicker(date[0]);
    }

    @Override
    public void setShowSelectDate(long timestamp) {
        String year = DateFormatUtils.long2Str(timestamp);
        String yearStr = year + "年▼";
        mView.showSelectDate(yearStr);
        setShowBillData(year);
    }

    @Override
    public void setShowBillData(String year) {
        // 小数后两位设置小一些
        RelativeSizeSpan sizeSpan2 = new RelativeSizeSpan(0.81f);
        List<BillBean> billBeans = new ArrayList<>();
        YearRecord yearRecord = mModel.getYearTotalRecord(year);
        String yearExpend;
        String yearIncome;
        String yearSurplus;
        if (yearRecord == null) {
            yearExpend = "0.00";
            yearIncome = "0.00";
        } else {
            yearExpend = mMoneyFormat.format(Double.valueOf(yearRecord.getTotalExpend()));
            yearIncome = mMoneyFormat.format(Double.valueOf(yearRecord.getTotalIncome()));
        }
        yearSurplus = new BigDecimal(yearIncome).subtract(new BigDecimal(yearExpend)).
                setScale(2, RoundingMode.HALF_UP).toString();
        SpannableString yearExpendStr = new SpannableString(yearExpend);
        SpannableString yearIncomeStr = new SpannableString(yearIncome);
        SpannableString yearSurplusStr = new SpannableString(yearSurplus);
        yearExpendStr.setSpan(sizeSpan2, yearExpendStr.length() - 2, yearExpendStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        yearIncomeStr.setSpan(sizeSpan2, yearIncomeStr.length() - 2, yearIncomeStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        yearSurplusStr.setSpan(sizeSpan2, yearSurplusStr.length() - 2, yearSurplusStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        // 显示年总账单记录
        mView.showTotalBillDetail(yearIncomeStr, yearExpendStr, yearSurplusStr);

        for (int i = 1; i <= 12; i++) {
            SpannableString monthStr = new SpannableString(i + "月");
            String monthKey = year + "-" + mMonthFormat.format(i);
            MonthRecord monthRecord = mModel.getMonthTotalRecord(monthKey);
            String monthExpend;
            String monthIncome;
            String surplus;
            if (monthRecord == null) {
                monthExpend = "0.00";
                monthIncome = "0.00";
            } else {
                monthExpend = mMoneyFormat.format(Double.valueOf(monthRecord.getTotalExpend()));
                monthIncome = mMoneyFormat.format(Double.valueOf(monthRecord.getTotalIncome()));
            }
            surplus = new BigDecimal(monthIncome).subtract(new BigDecimal(monthExpend)).
                    setScale(2, RoundingMode.HALF_UP).toString();
            SpannableString monthExpendStr = new SpannableString(monthExpend);
            SpannableString monthIncomeStr = new SpannableString(monthIncome);
            SpannableString surplusStr = new SpannableString(surplus);
            monthExpendStr.setSpan(sizeSpan2, monthExpendStr.length() - 2, monthExpendStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            monthIncomeStr.setSpan(sizeSpan2, monthIncomeStr.length() - 2, monthIncomeStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            surplusStr.setSpan(sizeSpan2, surplusStr.length() - 2, surplusStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            BillBean billBean = new BillBean(monthStr, monthIncomeStr, monthExpendStr, surplusStr);
            billBeans.add(billBean);
        }
        // 设置显示该年所有月份的账单记录
        mView.setList(billBeans);
    }

    @Override
    public void start() {
        mModel = new BillModel();
    }

    @Override
    public void setInit() {
        mView.initWidget();
        mView.initListener();
        setShowSelectDate(System.currentTimeMillis());
    }
}
