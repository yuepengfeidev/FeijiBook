package com.example.feijibook.activity.main_act.detail_frag;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import com.example.feijibook.app.Constants;
import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.http.BaseObserver;
import com.example.feijibook.http.ExceptionHandle;
import com.example.feijibook.util.DateFormatUtils;
import com.example.feijibook.util.RecordListUtils;
import com.example.feijibook.util.SharedPreferencesUtils;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by 你是我的 on 2019/3/11
 */
public class DetailPresenter implements DetailContract.Presenter {
    private Activity mActivity;
    private DetailContract.View mView;
    private DetailContract.Model mModel;
    private boolean isNeedUpdate = true;

    public DetailPresenter(Activity activity, DetailContract.View view) {
        mActivity = activity;
        mView = view;

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mModel = new DetailModel();
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
    public void setUpdateMonthTotalMoney(String year, String month) {
        String date = year + "-" + month;
        Map<String, MonthRecord> monthRecordMap = RecordListUtils.getMonthRecordRankMap();
        String expend;
        String income;
        if (monthRecordMap.get(date) != null) {
            expend = Objects.requireNonNull(monthRecordMap.get(date)).getTotalExpend();
            income = Objects.requireNonNull(monthRecordMap.get(date)).getTotalIncome();
        } else {
            expend = "0.00";
            income = "0.00";
        }
        SpannableString totalExpend;
        SpannableString totalIncome;
        // 小数字体大小
        RelativeSizeSpan decimalsSize = new RelativeSizeSpan(0.75f);
        // 没有小数则添加小数（.00）
        if (!expend.contains(".")) {
            expend = expend + ".00";
        }

        totalExpend = new SpannableString(expend);
        totalExpend.setSpan(decimalsSize, expend.length() - 2, expend.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        if (!income.contains(".")) {
            income = income + ".00";
        }

        totalIncome = new SpannableString(income);
        totalIncome.setSpan(decimalsSize, income.length() - 2, income.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mView.updateMonthTotalMoney(totalIncome, totalExpend);
    }

    @Override
    public void setInit() {
        // 初始化时间选择器数据
        // 时间选择控件的最早和最晚选择时间
        String beginTime = "1998-10-13";
        long beginTimestamp = DateFormatUtils.str2Long(beginTime, false);
        String endTime = "2025-10-13";
        long endTimestamp = DateFormatUtils.str2Long(endTime, false);
        mView.initDatePicker(beginTimestamp, endTimestamp);

        // 设置textview为当前时间
        setShowSelectDate(System.currentTimeMillis(), false);
    }

    @Override
    public void setShowDatePicker() {
        mView.showDatePicker();
    }

    @Override
    public void setShowSelectDate(long time, boolean isDay) {
        // 把"2019-03"拆分为两个数组
        String date = DateFormatUtils.long2Str(time, isDay);
        String[] d = date.split("-");

        mView.showSelectDate(d[0], d[1]);
    }

    @Override
    public void setDeleteRecord(int position,RecordDetail recordDetail) {
        mView.closeMenu();// 在删除之前需要关闭删除菜单按钮
        mView.deleteRecord(position);
        if (recordDetail.getImgUrl() != null) {
            String[] imgUrl = recordDetail.getImgUrl().split("\\|");
            for (String url : imgUrl) {
                mModel.deleteFile(url);
            }
        }
        if (recordDetail.getVideoUrl() != null) {
            mModel.deleteFile(recordDetail.getVideoUrl());
        }
        mModel.requestDeleteRecord(recordDetail.getId(), SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT))
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (s.equals(Constants.DELETE_SUCCESS)) {
                            Log.d(Constants.HTTP_TAG,  "请求删除记录成功！");
                        }else {
                            Log.d(Constants.HTTP_TAG,  "请求删除记录失败！");
                        }
                    }

                    @Override
                    public void onHttpError(ExceptionHandle.ResponseException exception) {
                        Log.d(Constants.HTTP_TAG, "请求删除记录错误！");
                    }
                });
    }

    @Override
    public void setStartActivity(Intent intent) {
        mView.closeMenu();// 打开活动之前关闭 打开着的删除菜单栏
        mView.startAct(intent);
    }

    @Override
    public void setGetData(String yearMonth) {
        Map<String, List<RecordDetail>> recordDetailMap = RecordListUtils.getRecordDetailsInMonthMap();
        Map<String, List<DayRecord>> dayRecordMap = RecordListUtils.getDayRecordsInMonthMap();
        // 获取所有该月的 账单记录 和 日总详情记录
        mView.initClassData(recordDetailMap.get(yearMonth), dayRecordMap.get(yearMonth));
    }

    @Override
    public void setDeleteRecordDetail(RecordDetail recordDetail) {
        mModel.deleteRecordDetail(recordDetail);
    }

    @Override
    public boolean setGetNextOrLastMonthData(String year, String month, boolean isNext) {
        boolean needChange = false;
        Map<String, String> map = mModel.getStartAndEndMonth();
        Calendar calendar = Calendar.getInstance();
        Calendar compareCalendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        if (isNext) {
            calendar.add(Calendar.MONTH, 1);
            // 最后的记录的月份在该记录月份之后或相同，则可以切换到下一个月
            if (map.containsKey("end")) {
                String[] d = map.get("end").split("-");
                compareCalendar.set(Calendar.YEAR, Integer.parseInt(d[0]));
                compareCalendar.set(Calendar.MONTH, Integer.parseInt(d[1]) - 1);
                if (compareCalendar.after(calendar) ||
                        compareCalendar.equals(calendar)) {
                    needChange = true;
                }
            }
        } else {
            // 最早的记录的月份在该记录之前或相同，则可以切换到上一个月
            calendar.add(Calendar.MONTH, -1);
            if (map.containsKey("start")) {
                String[] d = map.get("start").split("-");
                compareCalendar.set(Calendar.YEAR, Integer.parseInt(d[0]));
                compareCalendar.set(Calendar.MONTH, Integer.parseInt(d[1]) - 1);
                if (compareCalendar.before(calendar)
                        || compareCalendar.equals(calendar)) {
                    needChange = true;
                }
            }
        }
        String y = String.valueOf(calendar.get(Calendar.YEAR));
        DecimalFormat format = new DecimalFormat("00");
        String m = format.format(calendar.get(Calendar.MONTH) + 1);
        String date = y + "-" + m;
        if (needChange) {
            mView.showSelectDate(y, m);
            // 但需要切换月份才会重新获取数据
            setGetData(date);
        }
        return needChange;
    }

    @Override
    public void isNeedUpdateRecord(boolean needUpdate) {
        isNeedUpdate = needUpdate;
    }
}
