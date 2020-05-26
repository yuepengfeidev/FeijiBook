package com.example.feijibook.activity.main_act.detail_frag;

import android.content.Intent;
import android.text.SpannableString;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter2;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by 你是我的 on 2019/3/11
 */
public interface DetailContract {
    interface View extends BaseView<Presenter> {
        void showDatePicker();// 显示时间选择控件

        void showSelectDate(String year, String month);// 显示选择的时间

        void deleteRecord(int position);// 删除指定记录

        /**
         * 打开活动
         */
        void startAct(Intent intent);

        void initDatePicker(long beginTime, long endTime);// 初始化时间选择控件

        /**
         * 初始化 recyclerview 的类数据
         */
        void initClassData(List<RecordDetail> recordDetails, List<DayRecord> dayRecords);

        /**
         * 更新显示该月的总收入 和 总支出
         *
         * @param totalIncome 格式拼接后的总收入
         * @param totalExpend 格式拼接后的总支出
         */
        void updateMonthTotalMoney(SpannableString totalIncome, SpannableString totalExpend);

        /**
         * 关闭打开的删除按钮
         */
        void closeMenu();

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
         * 设置显示该月的总收入 和 总支出
         *
         * @param year  选择的年份
         * @param month 选择的月份
         */
        void setUpdateMonthTotalMoney(String year, String month);

        void setShowDatePicker();// 设置显示时间选择

        void setShowSelectDate(long time, boolean isDay);// 设置显示时间

        /**
         * 设置删除该条记录，如果有照片和视频则删除文件
         *
         * @param position     记录再列表中的位置
         * @param recordDetail 记录详情
         */
        void setDeleteRecord(int position, RecordDetail recordDetail);

        void setStartActivity(Intent intent);// 设置打开活动

        /**
         * 从model中 获取 数据,并加载该数据
         *
         * @param yearMonth 年月(1998-10)
         */
        void setGetData(String yearMonth);

        /**
         * 设置删除该条记录
         *
         * @param recordDetail 删除记录详情
         */
        void setDeleteRecordDetail(RecordDetail recordDetail);


        /**
         * 获取下个月或上个月的记录
         *
         * @param year   当前记录年份
         * @param month  当前记录月份
         * @param isNext 是否需要切换到下一个月
         * @return 是否需要切换到该月份并更新记录
         */
        boolean setGetNextOrLastMonthData(String year, String month, boolean isNext);


    }

    interface Model {
        /**
         * 删除该条记录
         *
         * @param recordDetail 删除记录详情
         */
        void deleteRecordDetail(RecordDetail recordDetail);

        /**
         * 获取所有记录中，最早记录的月份，和最后记录的月份
         *
         * @return 最早记录和最后记录月份的map
         */
        Map<String, String> getStartAndEndMonth();

        /**
         * 删除路径的文件
         *
         * @param filePath 文件路径
         */
        void deleteFile(String filePath);

        Observable<String> requestDeleteRecord(String id, String account);
    }
}
