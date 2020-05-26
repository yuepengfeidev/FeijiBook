package com.example.feijibook.activity.main_act.chart_frag;

import android.app.Activity;
import android.content.Intent;
import android.widget.PopupWindow;

import com.example.feijibook.app.Constants;
import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_bean.WeekRecord;
import com.example.feijibook.entity.record_bean.YearRecord;
import com.example.feijibook.util.DateUtils;
import com.example.feijibook.util.MeasureUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.highlight.Highlight;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 你是我的 on 2019/3/11
 */
public class ChartPresenter implements ChartContract.Presenter {
    private ChartContract.View mView;
    private ChartContract.Model mModel;
    private DecimalFormat mDecimalFormat = new DecimalFormat("00");
    /**
     * 是否需要更新当前界面的记录，默认true
     */
    private boolean isNeedUpate = true;

    public ChartPresenter(Activity activity, ChartContract.View view) {
        mView = view;
        mView.getAct(activity);

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mModel = new ChartModel();
    }

    @Override
    public void setInit() {
        mView.initWidget();
        mView.initListener();
        mView.initData();
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
    public void setLoadChart(String dateType, String dateRange, String expendOrIncome) {
        // chart的Y轴数据/金额
        List<Float> list = new ArrayList<>();
        // 时间主键列表,用于获取该天的所有记录和总金额
        List<String> keyList;
        // x轴显示的轴值
        List<String> xAxisList;
        // 该类型范围（周、月、年）的总金额
        BigDecimal totalMoney = new BigDecimal(mModel.getTotalMoney(dateType, expendOrIncome, dateRange));
        BigDecimal averageMoney;

        String[] dateString = dateRange.split("-");
        String year = dateString[0];
        Map<String, List<String>> map;
        // week 或 month
        if (dateString.length == 2) {
            map = DateUtils.getDatesOfWeekOrMonth(dateType, year, dateString[1]);
        }// year
        else {
            map = DateUtils.getDatesOfWeekOrMonth(dateType, year, "");
        }
        keyList = map.get("keyList");
        xAxisList = map.get("xAxisList");

        String typeOfGetRecords;
        // 如果是week 和 month类型,获取每天的总金额
        if (dateType.equals(Constants.WEEK) || dateType.equals(Constants.MONTH)) {
            typeOfGetRecords = Constants.DAY;
        }// 如果是year ,获取每月的总金额
        else {
            typeOfGetRecords = Constants.MONTH;
        }

        // 获取 chart的Y轴数据/金额
        for (String date : keyList) {
            Float money = mModel.getTotalMoney(typeOfGetRecords, expendOrIncome, date);
            if (money == null) {
                list.add(0f);
            } else {
                list.add(money);
            }
        }
        averageMoney = totalMoney.divide(new BigDecimal(list.size()), 2, BigDecimal.ROUND_HALF_UP);

        mView.loadChart(list, xAxisList, keyList,
                totalMoney.setScale(2, RoundingMode.HALF_UP).toString(), averageMoney.toString());

        // 显示 chart 上方的 总金额 和 平均值数据
        String type = "支出";
        if (expendOrIncome.equals(Constants.EXPEND)) {
            type = "支出";
        } else if (expendOrIncome.equals(Constants.INCOME)) {
            type = "收入";
        }
        String amString = "平均值：" + averageMoney;
        String tmString = "总" + type + "：" + totalMoney.setScale(2, RoundingMode.HALF_UP).toString();
        mView.showTotalAverage(tmString, amString);
    }

    @Override
    public void setInitRecyclerViewData(String dateType, String dateRange, String expendOrIncome) {
        Object o = mModel.getTotalRecord(dateType, dateRange);
        List totalList = new ArrayList();
        if (o == null) {
            mView.loadRecyclerViewData(new ArrayList());
        } else if (o instanceof WeekRecord) {
            WeekRecord weekRecord = (WeekRecord) o;
            if (expendOrIncome.equals(Constants.INCOME)) {
                totalList = weekRecord.getWeekTotalIncomes();
            } else if (expendOrIncome.equals(Constants.EXPEND)) {
                totalList = weekRecord.getWeekTotalExpends();
            }
        } else if (o instanceof MonthRecord) {
            MonthRecord monthRecord = (MonthRecord) o;
            if (expendOrIncome.equals(Constants.INCOME)) {
                totalList = monthRecord.getMonthTotalIncomes();
            } else if (expendOrIncome.equals(Constants.EXPEND)) {
                totalList = monthRecord.getMonthTotalExpends();
            }
        } else if (o instanceof YearRecord) {
            YearRecord yearRecord = (YearRecord) o;
            if (expendOrIncome.equals(Constants.INCOME)) {
                totalList = yearRecord.getYearTotalIncomes();
            } else if (expendOrIncome.equals(Constants.EXPEND)) {
                totalList = yearRecord.getYearTotalExpends();
            }
        }
        mView.loadRecyclerViewData(totalList);
    }

    @Override
    public void setInitChartMarker(Highlight highlight, List<String> keyList, String dateType, String expendOrIncome) {
        float yValue = highlight.getY();
        int index = (int) highlight.getX();
        // 获取选择日期 的所有 记录(记录按金额降序排序)
        String dateKey = keyList.get(index);
        List<RecordDetail> list = mModel.getRecord(expendOrIncome, dateType, dateKey);
        String totalMoneyString;
        Float totalMoney;
        // 周、月图表 获取每天的总费用
        if (dateType.equals(Constants.WEEK) || dateType.equals(Constants.MONTH)) {
            totalMoney = mModel.getTotalMoney(Constants.DAY, expendOrIncome, dateKey);
        } else {
            totalMoney = mModel.getTotalMoney(Constants.MONTH, expendOrIncome, dateKey);
        }
        if (dateType.equals(Constants.YEAR)) {
            if (expendOrIncome.equals(Constants.EXPEND)) {
                totalMoneyString = "当月总支出：" + totalMoney;
            } else {
                totalMoneyString = "当月总收入：" + totalMoney;
            }
        } else {
            if (expendOrIncome.equals(Constants.EXPEND)) {
                totalMoneyString = "当日总支出：" + totalMoney;
            } else {
                totalMoneyString = "当日总收入：" + totalMoney;
            }
        }
        mView.initChartMarker(yValue, totalMoneyString, list);
    }

    @Override
    public void setShowChartMarker(Highlight highlight, LineChart lineChart, PopupWindow dw, PopupWindow pw, PopupWindow rw) {
        Map<String, Object> map = MeasureUtils.getLineChartMarkersLocation(highlight, lineChart, dw, pw, rw);
        int chartCenterX = (int) map.get("chartCenterX");
        int chartCenterY = (int) map.get("chartCenterY");
        int dwX = (int) map.get("dwX");
        int dwY = (int) map.get("dwY");
        int pwX = (int) map.get("pwX");
        int pwY = (int) map.get("pwY");
        int rwX = (int) map.get("rwX");
        int rwY = (int) map.get("rwY");
        boolean onlyShowDetailMarker = (boolean) map.get("onlyShowDetailMarker");

        mView.showChartMarker(chartCenterX, chartCenterY, dwX, dwY, pwX, pwY, rwX, rwY, onlyShowDetailMarker);
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
    public void isNeedUpdateRecord(boolean needUpdate) {
        isNeedUpate = needUpdate;
    }

    @Override
    public void checkUpdateRecord() {
        if (!isNeedUpate) {
            return;
        }
        mView.updateRecord();
        isNeedUpate = false;
    }
}
