package com.example.feijibook.activity.bill_act;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.entity.BillBean;
import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.record_bean.YearRecord;

import java.util.List;

/**
 * BillContract
 *
 * @author yuepengfei
 * @date 2019/7/23
 * @description
 */
public interface BillContract {
    interface View extends BaseView<Presenter> {
        /**
         * 销毁当前活动
         */
        void finishAct();

        /**
         * 打开活动，添加该层活动前移滚动初始动画
         */
        void startAct(Intent intent);

        /**
         * 获得该碎片的activity
         */
        void getAct(Activity activity);

        /**
         * 初始化控件
         */
        void initWidget();

        /**
         * 初始化时间选择器DatePicker
         */
        void initDatePicker(long beginTime, long endTime);

        /**
         * 显示DatePicker
         */
        void showDatePicker(String dateStr);

        /**
         * 右上角显示选择的年份
         *
         * @param dateStr 选择的年份
         */
        void showSelectDate(String dateStr);

        /**
         * 显示年总账单详情
         *
         * @param yearTotalIncome  年收入
         * @param yearTotalExpend  年支出
         * @param yearTotalSurplus 年结余
         */
        void showTotalBillDetail(SpannableString yearTotalIncome, SpannableString yearTotalExpend, SpannableString yearTotalSurplus);

        /**
         * 设置该年的账单数据
         *
         * @param billBeans 该年每月的数据
         */
        void setList(List<BillBean> billBeans);
    }

    interface Presenter extends BasePresenter {
        /**
         * 设置销毁当前活动
         */
        void setFinishAct();

        /**
         * 设置打开活动
         */
        void setStartActivity(Intent intent);

        /**
         * 设置初始化DatePicker
         */
        void setInitDatePicker();

        /**
         * 设置显示DatePicer
         */
        void setShowDatePicker(String dateStr);

        /**
         * 设置选择的时间
         *
         * @param timestamp 选择的时间
         */
        void setShowSelectDate(long timestamp);

        /**
         * 显示该年的账单数据
         *
         * @param year 选择的年份
         */
        void setShowBillData(String year);

    }

    interface Model {
        /**
         * 获取指定月份的总详情记录
         *
         * @param moy 指定月份，如1998-10
         * @return 指定月份的月总详情记录
         */
        MonthRecord getMonthTotalRecord(String moy);

        /**
         * 获取指定年分的总详情记录
         *
         * @param year 指定年份
         * @return 返回指定年份的总详情记录
         */
        YearRecord getYearTotalRecord(String year);

    }
}
