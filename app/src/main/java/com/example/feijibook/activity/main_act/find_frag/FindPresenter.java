package com.example.feijibook.activity.main_act.find_frag;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import com.example.feijibook.R;
import com.example.feijibook.app.Constants;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.weather_bean.FutureBean;
import com.example.feijibook.entity.weather_bean.NearlyFiveDayStateBean;
import com.example.feijibook.entity.weather_bean.ResultBean;
import com.example.feijibook.entity.weather_bean.WeatherBean;
import com.example.feijibook.http.BaseObserver;
import com.example.feijibook.http.ExceptionHandle;
import com.example.feijibook.http.Utility;
import com.example.feijibook.util.DateFormatUtils;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.SharedPreferencesUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import okhttp3.ResponseBody;

/**
 * Created by 你是我的 on 2019/3/11
 */
public class FindPresenter implements FindContract.Presenter {
    private FindContract.View mView;
    private FindContract.Model mModel;
    private boolean isNeedUpdate = true;
    /**
     * 是否已经设置预算，用于判断弹出对应的编辑预算的Dialog
     */
    private boolean haveBudget = false;
    private DecimalFormat format = new DecimalFormat("0.00");

    public FindPresenter(Activity activity, FindContract.View view) {
        mView = view;
        mView.getAct(activity);

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mModel = new FindModel();
    }

    @Override
    public void setInit() {
        mView.initWidget();
        mView.initListener();
        getWeather();
    }

    @Override
    public void checkUpdateRecord() {
        if (NoticeUpdateUtils.updateWeather) {
            getWeather();
            NoticeUpdateUtils.updateWeather = false;
        }
        if (!isNeedUpdate) {
            mView.showPieChartAnim();
            return;
        }
        mView.updateRecord();
        isNeedUpdate = false;
    }

    @Override
    public void setStartActivity(Intent intent) {
        mView.startAct(intent);
    }

    @Override
    public void setEditBudget(String budgetMoney, String totalExpendMoney) {
        SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.BUDGET_MONEY, budgetMoney);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM", Locale.CHINA);
        String curMonth = dateFormat.format(new Date(System.currentTimeMillis()));
        setShowBudget(curMonth, budgetMoney,
                totalExpendMoney);
    }

    @Override
    public void setShowBudget(String curMonth, String budgetMoney, String totalExpendMoney) {
        float textSize;
        String remainBudget;
        String percentStr;
        String textContent;
        int textColor;
        Drawable textBg;
        curMonth = curMonth + "月预算";
        if (Double.valueOf(budgetMoney) == 0) {
            haveBudget = false;
            textContent = "+ 设置预算";
            textSize = 14;
            textColor = ContextCompat.getColor(MyApplication.sContext, R.color.text_black_color);
            textBg = ContextCompat.getDrawable(MyApplication.sContext, R.drawable.bg_edit_budget_blue);
            budgetMoney = Constants.ZERO_MONEY;
            totalExpendMoney = Constants.ZERO_MONEY;
            remainBudget = Constants.ZERO_MONEY;
            percentStr = "0%";
        } else {
            haveBudget = true;
            textContent = "编辑预算";
            textSize = 12;
            textColor = ContextCompat.getColor(MyApplication.sContext, R.color.text_light_gray_color);
            textBg = ContextCompat.getDrawable(MyApplication.sContext, R.drawable.bg_edit_budget_white);
            remainBudget = new BigDecimal(budgetMoney).subtract(new BigDecimal(totalExpendMoney)).
                    setScale(2, RoundingMode.HALF_UP).toString();
            NumberFormat numberFormat = NumberFormat.getPercentInstance();
            numberFormat.setMinimumFractionDigits(0);
            // 剩余预算占总预算的比例
            percentStr = numberFormat.format(new BigDecimal(remainBudget)
                    .divide(new BigDecimal(budgetMoney), 8, BigDecimal.ROUND_UP));
        }
        SpannableString ssCenterText;
        if (Double.valueOf(remainBudget) < 0) {
            remainBudget = "0.00";
            ssCenterText = new SpannableString("已超支");
            RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.5f);
            ForegroundColorSpan redSpan = new ForegroundColorSpan(ContextCompat.getColor(MyApplication.sContext, R.color.text_red_color));
            ssCenterText.setSpan(redSpan, 0, 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            ssCenterText.setSpan(sizeSpan, 0, 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            String pcCenterText = "剩余" + "\n" + percentStr;
            ssCenterText = new SpannableString(pcCenterText);
            ForegroundColorSpan graySpan = new ForegroundColorSpan(ContextCompat.getColor(MyApplication.sContext, R.color.pie_chart_gray));
            ForegroundColorSpan blackSpan = new ForegroundColorSpan(ContextCompat.getColor(MyApplication.sContext, R.color.text_black_color));
            RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.5f);
            ssCenterText.setSpan(graySpan, 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            ssCenterText.setSpan(blackSpan, 2, pcCenterText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            ssCenterText.setSpan(sizeSpan, 2, pcCenterText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        mView.showBudget(ssCenterText, curMonth, remainBudget, format.format(new BigDecimal(budgetMoney)),
                format.format(new BigDecimal(totalExpendMoney)), textContent, textSize, textColor, textBg);
    }

    @Override
    public void setShowBill(String curMonth, String totalExpend, String totalIncome) {
        curMonth = curMonth + "月";
        String surplus = new BigDecimal(totalIncome).subtract(new BigDecimal(totalExpend)).
                setScale(2, RoundingMode.HALF_UP).toString();
        RelativeSizeSpan sizeSpanMonth = new RelativeSizeSpan(0.65f);
        RelativeSizeSpan sizeSpanMoney = new RelativeSizeSpan(0.8f);
        SpannableString ssMonth = new SpannableString(curMonth);
        SpannableString ssExpend = new SpannableString(totalExpend);
        SpannableString ssIncome = new SpannableString(totalIncome);
        SpannableString ssSurplus = new SpannableString(surplus);
        ssMonth.setSpan(sizeSpanMonth, curMonth.length() - 1, curMonth.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ssExpend.setSpan(sizeSpanMoney, totalExpend.length() - 3, totalExpend.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ssIncome.setSpan(sizeSpanMoney, totalIncome.length() - 3, totalIncome.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ssSurplus.setSpan(sizeSpanMoney, surplus.length() - 3, surplus.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mView.showBill(ssMonth, ssExpend, ssIncome, ssSurplus);
    }

    @Override
    public void setSaveBudgetSetting(String budgetMoney) {
        // 存储预算
        mModel.setSaveMonthBudgetMoney(budgetMoney);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.CHINA);
        Date date = new Date(System.currentTimeMillis());
        String curMonth = dateFormat.format(date);
        // 获取当月总详情记录
        Map<String, String> totalRecordMap = mModel.getCurMonthTotalRecord(curMonth);
        String totalExpend = totalRecordMap.get(Constants.EXPEND);
        setEditBudget(budgetMoney, totalExpend);
    }

    @Override
    public void setClearBudgetSetting() {
        setEditBudget("0", "0");
    }

    @Override
    public void setShowDialog() {
        // 没有预算显示 设置预算的Dialog
        if (!haveBudget) {
            setShowBudgetSettingDialog();
        } else {
            mView.showBudgetEditChooseDialog();
        }
    }

    @Override
    public void setShowBudgetSettingDialog() {
        mView.showBudgetSettingDialog();
    }

    @Override
    public void getWeather() {
        String updateTime = SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.UPDATE_WEATHER_DATE);
        String weatherCity = SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.WEATHER_CITY);
        if (weatherCity == null || "".equals(weatherCity)) {
            // 默认南京
            weatherCity = "南京";
            SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.WEATHER_CITY, weatherCity);
        }
        if (updateTime == null || "".equals(updateTime) || DateFormatUtils.isIntervalSixHours(updateTime)) {
            // 如果选择了新城市 或 没有更新时间 或 距离上次更新已过了6小时，则获取天气数据
            mModel.requestWeather(weatherCity).subscribe(
                    new BaseObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    try {
                        String curTime = DateFormatUtils.
                                ymdhLong2Str(System.currentTimeMillis());
                        // 存储更新时间
                        SharedPreferencesUtils.saveStrToSp
                                (SharedPreferencesUtils.UPDATE_WEATHER_DATE, curTime);
                        WeatherBean weatherBean = Utility.
                                handleWeatherResponse(responseBody.string());
                        if (!"超过每日可允许请求次数!".equals(weatherBean.getReason())) {
                            ResultBean resultBean = weatherBean.getResult();
                            resultBean.setId(UUID.randomUUID().toString());
                            mModel.saveWeather(resultBean);
                            mView.showWeather(resultBean.getCity(), resultBean);
                            setShowAir(resultBean.getRealtime().getAqi());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onHttpError(ExceptionHandle.ResponseException exception) {

                }
            });
        } else {
            ResultBean resultBean = mModel.getWeather();
            mView.showWeather(weatherCity, resultBean);
            setShowAir(resultBean.getRealtime().getAqi());
        }
        mView.showDate(DateFormatUtils.long2Str(System.currentTimeMillis(), true));
    }

    @Override
    public void setShowAir(String aqi) {
        int aqiData = Integer.parseInt(aqi);
        // 对比PM2.5标准进行相应处理显示
        if (aqiData < 36) {
            mView.showAir(R.drawable.bg_excellent_air, aqi + " 优");
        } else if (aqiData < 76) {
            mView.showAir(R.drawable.bg_good_air, aqi + " 良");
        } else if (aqiData < 116) {
            mView.showAir(R.drawable.bg_light_color, aqi + " 轻度污染");
        } else if (aqiData < 151) {
            mView.showAir(R.drawable.bg_moderate_air, aqi + " 中度污染");
        } else if (aqiData < 251) {
            mView.showAir(R.drawable.bg_severe_air, aqi + " 重度污染");
        } else {
            mView.showAir(R.drawable.bg_serious_air, aqi + " 严重污染");
        }
    }

    @Override
    public void setShowBillAndBudget() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.CHINA);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.CHINA);
        Date date = new Date(System.currentTimeMillis());
        String curMonth = dateFormat.format(date);
        String month = monthFormat.format(date);
        // 获取当月总详情记录
        Map<String, String> totalRecordMap = mModel.getCurMonthTotalRecord(curMonth);
        String allBudget = mModel.getCurMonthBudgetMoney();
        if ("".equals(allBudget)) {
            allBudget = Constants.ZERO_MONEY;
        }
        String totalExpend = totalRecordMap.get(Constants.EXPEND);
        String totalIncome = totalRecordMap.get(Constants.INCOME);

        setShowBudget(month, allBudget, totalExpend);
        setShowBill(month, new BigDecimal(totalExpend).setScale(2, RoundingMode.HALF_UP).toString()
                , new BigDecimal(totalIncome).setScale(2, RoundingMode.HALF_UP).toString());
    }

    @Override
    public void isNeedUpdateRecord(boolean needUpdate) {
        isNeedUpdate = needUpdate;
    }
}
