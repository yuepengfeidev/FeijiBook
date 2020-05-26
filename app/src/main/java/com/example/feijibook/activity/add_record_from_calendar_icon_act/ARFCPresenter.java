package com.example.feijibook.activity.add_record_from_calendar_icon_act;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.feijibook.app.Constants;
import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.http.BaseObserver;
import com.example.feijibook.http.ExceptionHandle;
import com.example.feijibook.util.DateFormatUtils;
import com.example.feijibook.util.SharedPreferencesUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by 你是我的 on 2019/3/20
 */
public class ARFCPresenter implements ARFCContract.Presenter {
    private Context mContext;
    private ARFCContract.View mView;
    private ARFCContract.Model mModel;
    /**
     * 判断是否是添加记录后状态
     */
    private boolean isAdd = false;
    private int mYear;
    private int mMonth;
    private int mDay;
    private boolean isNeedUpdate = false;

    ARFCPresenter(Context context, ARFCContract.View view) {
        mContext = context;
        mView = view;

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mModel = new ARFCModel();
    }

    @Override
    public void setInit() {
        mView.initRecyclerView();
        mView.initListener();
        // 初始化时间选择器数据
        // 时间选择控件的最早和最晚选择时间
        String beginTime = "1998-10-13";
        long beginTimestamp = DateFormatUtils.str2Long(beginTime, false);
        String endTime = "2025-10-13";
        long endTimestamp = DateFormatUtils.str2Long(endTime, false);
        mView.initDatePicker(beginTimestamp, endTimestamp);

        long curTime = System.currentTimeMillis();

        // 设置textview为当前时间
        setShowSelectDate(curTime, false);

        // 获取当天的记录
        setInitRecrords("0", "0", "0");
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
    public void setShowDataPicker(String dateString) {
        // 通过 非数字 将字符串分割出来
        String[] date = dateString.split("\\D");
        mView.showDatePicker(date[0] + "-" + date[1]);
    }

    @Override
    public void setLocateToday() {
        mView.locateToday();
    }

    @Override
    public void setShowSelectDate(long time, boolean isDay) {
        // 把"2019-03"拆分为两个数组
        String date = DateFormatUtils.long2Str(time, isDay);
        String[] d = date.split("-");
        int year = Integer.valueOf(d[0]);
        int month = Integer.valueOf(d[1]);

        String dateString = d[0] + "年" + d[1] + "月▼";
        // 显示选择的日期
        mView.showSelectDate(dateString);

        try {
            mView.markRecordDay(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 如果为添加后状态，则直接定位到添加到的那天
        if (isAdd) {
            mView.locateToChoose(mYear, mMonth, mDay);
            isAdd = false;
        } else {
            SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());

            Long curTime = System.currentTimeMillis();
            String curMonth = monthFormat.format(curTime);
            String dayString = dayFormat.format(curTime);
            int curDay = Integer.valueOf(dayString);

            if (d[1].equals(curMonth)) {
                // 如果是当月就定位到当天
                mView.locateToChoose(year, month, curDay);
                // 切换的月份是当月，加载当天的记录
                setInitRecrords(String.valueOf(year), d[1], dayString);
            } else {
                // 不是当月定位到该月的1号
                mView.locateToChoose(year, month, 1);
                // 切换的月份不是当月，加载当月1号的记录
                setInitRecrords(String.valueOf(year), d[1], "01");
            }
        }
    }

    @Override
    public void setShowSelectDate(String time) {
        long date = DateFormatUtils.str2Long(time, false);
        setShowSelectDate(date, false);
    }

    @Override
    public void setDeleteRecord(int position, RecordDetail recordDetail) {
        mView.deleteRecord(position);
        mModel.requestDeleteRecord(recordDetail.getId(), SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT))
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (s.equals(Constants.DELETE_SUCCESS)) {
                            Log.d(Constants.HTTP_TAG, "请求删除记录成功！");
                        } else {
                            Log.d(Constants.HTTP_TAG, "请求删除记录失败！");
                        }
                    }

                    @Override
                    public void onHttpError(ExceptionHandle.ResponseException exception) {
                        Log.d(Constants.HTTP_TAG, "请求删除记录错误！");
                    }
                });
        mModel.deleteRecordDetail(recordDetail);
        if (recordDetail.getImgUrl() != null) {
            String[] imgUrl = recordDetail.getImgUrl().split("\\|");
            for (String url : imgUrl) {
                mModel.deleteFile(url);
            }
        }
        if (recordDetail.getVideoUrl() != null) {
            mModel.deleteFile(recordDetail.getVideoUrl());
        }
    }

    @Override
    public void setStartActivity(Intent intent) {
        mView.startAct(intent);
    }

    @Override
    public void setInitRecrords(String year, String month, String day) {
        String y = year;
        String m = month;
        String d = day;
        if ("0".equals(year)) {
            // 获取当前时间，更新当天的数据
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            String dateString = format.format(date);
            String[] dateArray = dateString.split("-");
            y = dateArray[0];
            m = dateArray[1];
            d = dateArray[2];
        }
        List<DayRecord> dayRecords = mModel.getDayRecord(y, m, d);
        List<RecordDetail> recordDetails = mModel.getRecordDetails(y, m, d);
        mView.initRecordsToRv(recordDetails, dayRecords);
    }

    @Override
    public void setDeleteRecordDetail(RecordDetail recordDetail) {
        mModel.deleteRecordDetail(recordDetail);
    }

    @Override
    public void setInitAddRecord(String year, String month, String day) {
        mYear = Integer.valueOf(year);
        mMonth = Integer.valueOf(month);
        mDay = Integer.valueOf(day);
        mDay = Integer.valueOf(day);
        isAdd = true;
        mView.locateToChoose(mYear, mMonth, mDay);
        setInitRecrords(year, month, day);
        // 更新记录后，更新显示标记日期
        setMarkRecordDay(year, month);
    }

    @Override
    public void setMarkRecordDay(String year, String month) {
        String date = year + "-" + month;
        try {
            mView.markRecordDay(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void isNeedUpdateRecord(boolean needUpdate) {
        isNeedUpdate = needUpdate;
    }
}
