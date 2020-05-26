package com.example.feijibook.activity.one_type_records_line_chart_act;

import android.app.Activity;
import android.content.Intent;
import android.widget.PopupWindow;

import com.example.feijibook.app.Constants;
import com.example.feijibook.entity.record_bean.RecordDetail;
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
 * OTRLCPresenter
 *
 * @author yuepengfei
 * @date 2019/6/13
 * @description
 */
public class OTRLCPresenter implements OTRLCContract.Presenter {
    private OTRLCContract.View mView;
    private OTRLCContract.Model mModel;
    private String detailType;
    private String expendOrIncome;
    private DecimalFormat mDecimalFormat = new DecimalFormat("00");
    private boolean isNeedUpdate = false;

    OTRLCPresenter(Activity activity, OTRLCContract.View view, String detailType, String expendOrIncome,
                   String dateType, String date) {
        mView = view;
        this.detailType = detailType;
        this.expendOrIncome = expendOrIncome;
        mView.setPresenter(this);
        mView.getData(activity, dateType, date);
    }

    @Override
    public void setInit() {
        mView.initWidget();
        mView.initListener();
        mView.initData();
        mView.showTitle(detailType);
        if (expendOrIncome.equals(Constants.EXPEND)) {
            mView.showExpendOrIncome("支出");
        } else {
            mView.showExpendOrIncome("收入");
        }
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
    public void setStartActivity(Intent intent) {
        mView.startAct(intent);
    }

    @Override
    public void setLoadTabLayoutData(String type, String date) {
        Map<String, String> map = mModel.getStartAndEndDate(detailType, type);
        List<String> keyList;
        List<String> showList;
        String startDate = map.get("start");
        String endDate = map.get("end");
        Map<String, List<String>> listMap = DateUtils.getKeyListAndShowList(startDate, endDate, type);
        keyList = listMap.get("keyList");
        showList = listMap.get("showList");
        int index = 0;
        if ("".equals(date)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            String curYear = String.valueOf(calendar.get(Calendar.YEAR));
            String curMonth =String.valueOf( mDecimalFormat.format(calendar.get(Calendar.MONTH) + 1));
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            String curWeek = String.valueOf(mDecimalFormat.format(calendar.get(Calendar.WEEK_OF_YEAR)));
            switch (type) {
                case Constants.YEAR:
                    date = curYear;
                    break;
                case Constants.MONTH:
                    date = curYear + "-" + curMonth;
                    break;
                case Constants.WEEK:
                    date = curYear + "-" + curWeek;
                    break;
                default:
            }
        }
        // 根据上个界面选择的时间段，选择该界面的TabLayout
        for (int i = 0; i < keyList.size(); i++) {
            if (date.equals(keyList.get(i))) {
                index = i;
                break;
            }
        }
        mView.loadTabLayout(keyList, showList, index);
    }

    @Override
    public void setLoadChart(String dateType, String dateRange) {
        // chart的Y轴数据/金额
        List<Float> list = new ArrayList<>();
        // 时间主键列表,用于获取该天的所有记录和总金额
        List<String> keyList;
        // x轴显示的轴值
        List<String> xAxisList;
        // 该类型范围（周、月、年）的总金额
        BigDecimal totalMoney = new BigDecimal(mModel.getTotalMoney(detailType, dateType, dateRange));
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
            Float money = mModel.getTotalMoney(detailType, typeOfGetRecords, date);
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
    public void setInitRecyclerViewData(String dateType, String dateRange) {
        mView.loadRecyclerViewData(mModel.getRecord(detailType, dateType, dateRange));
    }

    @Override
    public void setInitChartMarker(Highlight highlight, List<String> keyList, String dateType) {
        float yValue = highlight.getY();
        int index = (int) highlight.getX();
        // 获取选择日期 的所有 记录(记录按金额降序排序)
        String dateKey = keyList.get(index);
        String totalMoneyString;
        Float totalMoney;
        // 周、月图表 获取每天的总费用
        if (dateType.equals(Constants.WEEK) || dateType.equals(Constants.MONTH)) {
            dateType = Constants.DAY;
        } else {
            dateType = Constants.MONTH;
        }
        List<RecordDetail> list = mModel.getRecord(detailType, dateType, dateKey);
        totalMoney = mModel.getTotalMoney(detailType, dateType, dateKey);
        if (dateType.equals(Constants.YEAR)) {
            if (expendOrIncome.equals(Constants.YEAR)) {
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
    public void start() {
        mModel = new OTRLCModel();
    }

    @Override
    public void isNeedUpdateRecord(boolean needUpdate) {
        isNeedUpdate = needUpdate;
    }
}
