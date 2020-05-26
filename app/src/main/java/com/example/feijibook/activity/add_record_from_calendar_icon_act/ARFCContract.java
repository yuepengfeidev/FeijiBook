package com.example.feijibook.activity.add_record_from_calendar_icon_act;

import android.content.Intent;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter2;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.haibin.calendarview.Calendar;

import java.text.ParseException;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by 你是我的 on 2019/3/20
 */
public interface ARFCContract {
    interface View extends BaseView<Presenter> {
        /**
         * 销毁当前活动
         */
        void finishAct();

        /**
         * 显示时间选择器
         */
        void showDatePicker(String dateStr);

        /**
         * calendar定位到今天
         */
        void locateToday();

        /**
         * calendar定位到选择的时间
         */
        void locateToChoose(int year, int month, int day);

        /**
         * 显示选择的时间
         */
        void showSelectDate(String dateString);

        /**
         * 初始化时间选择控件
         */
        void initDatePicker(long beginTime, long endTime);

        /**
         * 显示recyclerview
         */
        void initRecyclerView();

        /**
         * 删除指定记录
         */
        void deleteRecord(int position);

        /**
         * 打开活动
         */
        void startAct(Intent intent);

        /**
         * 通过recyclerview 加载 当天的所有记录
         *
         * @param recordDetails 所有记录
         * @param dayRecords    当天总详情记录
         */
        void initRecordsToRv(List<RecordDetail> recordDetails, List<DayRecord> dayRecords);

        /**
         * 标记含有记录的日期
         *
         * @param date 选择的月份
         */
        void markRecordDay(String date) throws ParseException;

        /**
         * 获取含有记录的日期的Calendar
         *
         * @param year  还有记录的年份
         * @param month 还有记录的月份
         * @param day   还有记录的日
         * @return 含有记录的日期的Calendar
         */
        Calendar getSchemeCalendar(int year, int month, int day);

        /**
         * 更新该界面的记录
         */
        void updateRecord();

        /**
         * 显示或隐藏“暂无数据”标志
         */
        void showOrHideNoDataSign();

    }

    interface Presenter extends BasePresenter2 {
        /**
         * 设置销毁当前活动
         */
        void setFinishAct();

        /**
         * 设置显示时间选择器
         */
        void setShowDataPicker(String dateString);

        /**
         * 设置定位到今天
         */
        void setLocateToday();

        /**
         * 设置显示时间
         */
        void setShowSelectDate(long time, boolean isDay);

        /**
         * 设置显示时间
         */
        void setShowSelectDate(String time);

        /**
         * 设置删除该条记录
         */
        void setDeleteRecord(int position,RecordDetail recordDetail);

        /**
         * 设置打开活动
         */
        void setStartActivity(Intent intent);

        /**
         * 设置获取当天的 所有记录 和 总详情记录
         *
         * @param year  选择的年份
         * @param month 选择的月份
         * @param day   选择的日
         */
        void setInitRecrords(String year, String month, String day);

        /**
         * 设置删除该条记录
         *
         * @param recordDetail 删除记录详情
         */
        void setDeleteRecordDetail(RecordDetail recordDetail);

        /**
         * 加载新添加的数据
         *
         * @param year  选择的年份
         * @param month 选择的月份
         * @param day   选择的日
         */
        void setInitAddRecord(String year, String month, String day);

        /**
         * 设置标记有记录的日期
         *
         * @param month 选择的月份
         * @param year  选择的年份
         */
        void setMarkRecordDay(String year, String month);
    }

    interface Model {
        /**
         * 获取当天 总详情记录
         *
         * @param year  选择的年份
         * @param month 选择的月份
         * @param day   选择的日
         */
        List<DayRecord> getDayRecord(String year, String month, String day);

        /**
         * 获取当天所有记录
         *
         * @param year  选择的年份
         * @param month 选择的月份
         * @param day   选择的日
         */
        List<RecordDetail> getRecordDetails(String year, String month, String day);

        /**
         * 删除该条记录
         *
         * @param recordDetail 删除记录详情
         */
        void deleteRecordDetail(RecordDetail recordDetail);

        /**
         * 删除路径的文件
         *
         * @param filePath 文件路径
         */
        void deleteFile(String filePath);

        Observable<String> requestDeleteRecord(String id, String account);
    }
}
