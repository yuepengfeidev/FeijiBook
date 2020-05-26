package com.example.feijibook.activity.pie_chart_records_act;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.PopupWindow;

import com.example.feijibook.app.Constants;
import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.record_bean.TotalExpend;
import com.example.feijibook.entity.record_bean.TotalIncome;
import com.example.feijibook.entity.record_bean.WeekRecord;
import com.example.feijibook.entity.record_bean.YearRecord;
import com.example.feijibook.util.DateUtils;
import com.example.feijibook.widget.SwipeBackLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * PCRPresenter
 *
 * @author yuepengfei
 * @date 2019/6/30
 * @description
 */
public class PCRPresenter implements PCRContract.Presenter {
    private PCRContract.View mView;
    private PCRContract.Model mModel;
    private NumberFormat mNumberFormat = NumberFormat.getPercentInstance();
    private DecimalFormat mDecimalFormat = new DecimalFormat("00");
    private boolean isNeedUpdate = false;

    PCRPresenter(Activity activity, PCRContract.View view, String dateType, int index,String expendOrIncome) {
        mView = view;
        mView.setPresenter(this);
        mView.getData(activity, dateType, index,expendOrIncome);
        // 设置百分比后一位小数
        mNumberFormat.setMinimumFractionDigits(1);
    }

    @Override
    public void setInit() {
        mView.initWidget();
        mView.initListener();
        mView.initData();
    }

    @Override
    public void checkUpdateRecord() {
        if (!isNeedUpdate) {
            return;
        }
        mView.updateRecord();
        isNeedUpdate = false;
    }

    @Override
    public void setFinishAct() {
        mView.finishAct();
    }

    @Override
    public void setStartAct(Intent intent) {
        mView.startAct(intent);
    }

    @Override
    public void setLoadTabLayoutData(String type, int index) {
        Map<String, String> map = mModel.getStartAndEndDate(type);
        List<String> keyList;
        List<String> showList;
        String startDate = map.get("start");
        String endDate = map.get("end");
        Map<String, List<String>> listMap = DateUtils.getKeyListAndShowList(startDate, endDate, type);
        keyList = listMap.get("keyList");
        showList = listMap.get("showList");
        if (index == -1 || index >= keyList.size()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            String curYear = String.valueOf(calendar.get(Calendar.YEAR));
            String curMonth = String.valueOf(mDecimalFormat.format(calendar.get(Calendar.MONTH) + 1));
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            String curWeek = String.valueOf(mDecimalFormat.format(calendar.get(Calendar.WEEK_OF_YEAR)));
            String curDate = null;
            switch (type) {
                case Constants.YEAR:
                    curDate = curYear;
                    break;
                case Constants.MONTH:
                    curDate = curYear + "-" + curMonth;
                    break;
                case Constants.WEEK:
                    curDate = curYear + "-" + curWeek;
                    break;
                default:
            }
            for (int i = 0; i < keyList.size(); i++) {
                if (curDate.equals(keyList.get(i))) {
                    index = i;
                    break;
                }
            }
        }
        mView.loadTabLayout(keyList, showList, index);
    }

    @Override
    public void setLoadChart(List totalList, String totalMoney) {
        // 取金额最大的前五位类型，其余的合并作为“其他”，
        List<String> categoryList = new ArrayList<>(6);
        // 上面6个类型的总金额
        List<Float> totalMoneyList = new ArrayList<>(6);
        List<String> legendPerStrList = new ArrayList<>();
        float otherTotalMoney = 0.0f;
        for (int i = 0; i < totalList.size(); i++) {
            Object o = totalList.get(i);
            if (o instanceof TotalExpend) {
                TotalExpend totalExpend = (TotalExpend) o;
                // 添加饼图类型数据
                if (i < 5) {
                    categoryList.add(i, totalExpend.getType());
                    totalMoneyList.add(i, Float.valueOf(totalExpend.getTotalExpend()));
                } else if (i == 5) {
                    // 当金额类型为6个，正好显示全部，否则如果超过六个，则第六个为前五个外的总额
                    if (totalList.size() == 6) {
                        categoryList.add(i, totalExpend.getType());
                        totalMoneyList.add(i, Float.valueOf(totalExpend.getTotalExpend()));
                    } else {
                        otherTotalMoney = Float.parseFloat(totalExpend.getTotalExpend());
                    }
                } else {
                    otherTotalMoney += Float.parseFloat(totalExpend.getTotalExpend());
                    // 当类型个数超过6个，且遍历到最后一位类型金额时，添加“其他”金额
                    if (i == totalList.size() - 1) {
                        categoryList.add(5, "其他");
                        totalMoneyList.add(5, otherTotalMoney);
                    }
                }
            } // 添加饼图金额数
            else if (o instanceof TotalIncome) {
                TotalIncome totalIncome = (TotalIncome) o;
                // 添加饼图类型数据
                if (i < 5) {
                    categoryList.add(i, totalIncome.getType());
                    totalMoneyList.add(i, Float.valueOf(totalIncome.getTotalIncome()));
                } else if (i == 5) {
                    // 当金额类型为6个，正好显示全部，否则如果超过六个，则第六个为前五个外的总额
                    if (totalList.size() == 6) {
                        categoryList.add(i, totalIncome.getType());
                        totalMoneyList.add(i, Float.valueOf(totalIncome.getTotalIncome()));
                    } else {
                        otherTotalMoney = Float.parseFloat(totalIncome.getTotalIncome());
                    }
                } else {
                    otherTotalMoney += Float.parseFloat(totalIncome.getTotalIncome());
                    // 当类型个数超过6个，且遍历到最后一位类型金额时，添加“其他”金额
                    if (i == totalList.size() - 1) {
                        categoryList.add(5, "其他");
                        totalMoneyList.add(5, otherTotalMoney);
                    }
                }
            }
        }

        // 获取各类型占总金额的比例
        BigDecimal bdTotalMoney = new BigDecimal(totalMoney);
        for (Float money : totalMoneyList) {
            BigDecimal bigDecimal = new BigDecimal(money).divide(bdTotalMoney, 8, BigDecimal.ROUND_UP);
            legendPerStrList.add(mNumberFormat.format(bigDecimal));
        }
        mView.loadChart(totalMoneyList, categoryList);
        // 显示饼图中间的该日期段的总金额,格式为两位小数
        setPieCenterText(bdTotalMoney.setScale(2, RoundingMode.HALF_UP).toString());
        // 没有记录的Legend数值
        if (categoryList.size() == 0 && legendPerStrList.size() == 0) {
            for (int i = 0; i < 6; i++) {
                categoryList.add("-");
                legendPerStrList.add("0.0%");
            }
        }
        // 初始化自定义Legend
        mView.initCustomLegend(categoryList, legendPerStrList);
    }

    @Override
    public void setInitRecyclerViewDataAndChart(String dateType, String dateRange, String expendOrIncome) {
        Object o = mModel.getTotalRecord(dateType, dateRange);
        List totalList = new ArrayList();
        String totalMoney = "0.00";
        if (o == null) {
            mView.loadRecyclerViewData(new ArrayList());
        } else if (o instanceof WeekRecord) {
            WeekRecord weekRecord = (WeekRecord) o;
            if (expendOrIncome.equals(Constants.INCOME)) {
                totalMoney = weekRecord.getTotalIncome();
                totalList = weekRecord.getWeekTotalIncomes();
            } else if (expendOrIncome.equals(Constants.EXPEND)) {
                totalMoney = weekRecord.getTotalExpend();
                totalList = weekRecord.getWeekTotalExpends();
            }
        } else if (o instanceof MonthRecord) {
            MonthRecord monthRecord = (MonthRecord) o;
            if (expendOrIncome.equals(Constants.INCOME)) {
                totalMoney = monthRecord.getTotalIncome();
                totalList = monthRecord.getMonthTotalIncomes();
            } else if (expendOrIncome.equals(Constants.EXPEND)) {
                totalMoney = monthRecord.getTotalExpend();
                totalList = monthRecord.getMonthTotalExpends();
            }
        } else if (o instanceof YearRecord) {
            YearRecord yearRecord = (YearRecord) o;
            if (expendOrIncome.equals(Constants.INCOME)) {
                totalMoney = yearRecord.getTotalIncome();
                totalList = yearRecord.getYearTotalIncomes();
            } else if (expendOrIncome.equals(Constants.EXPEND)) {
                totalMoney = yearRecord.getTotalExpend();
                totalList = yearRecord.getYearTotalExpends();
            }
        }
        mView.loadRecyclerViewData(totalList);
        // 设置饼图的数据
        setLoadChart(totalList, totalMoney);
    }

    @Override
    public void setShowOrDismissExpendOrIncomeChoosePopupWindow(PopupWindow pwEIChoose) {
        if (pwEIChoose.isShowing()) {
            mView.dismissExpendOrIncomeChoosePopupWindow();
        } else if (!pwEIChoose.isShowing()) {
            mView.showExpendOrIncomeChoosePopupWindow();
        }
    }

    @Override
    public void setChooseExpendOrIncome(String expendOrIncome) {
        mView.chooseExpendOrIncome(expendOrIncome);
        setShowExpendOrIncome(expendOrIncome);
    }

    @Override
    public void setMarginShadowView() {
        mView.reMarginShadowView();
    }

    @Override
    public void setPieCenterText(String totalMoney) {
        String content = "总金额" + "\n" + totalMoney;
        SpannableString spannableString = new SpannableString(content);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.GRAY);
        // 设置“总金额”为灰色
        spannableString.setSpan(colorSpan, 0, 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(0.8f), 0, 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(1.6f), 3, content.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mView.showPieChartCenterText(spannableString);
    }

    @Override
    public void setShowExpendOrIncome(String expendOrIncome) {
        if (expendOrIncome.equals(Constants.INCOME)) {
            mView.showExpendOrIncome("收入");
        } else if (expendOrIncome.equals(Constants.EXPEND)) {
            mView.showExpendOrIncome("支出");
        }
    }

    @Override
    public void setMoveListener(SwipeBackLayout rootView) {
        mView.bindMoveListener(rootView);
    }

    @Override
    public void start() {
        mModel = new PCRModel();
    }

    @Override
    public void isNeedUpdateRecord(boolean needUpdate) {
        isNeedUpdate = needUpdate;
    }
}
