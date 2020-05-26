package com.example.feijibook.activity.search_record_act;

import android.app.Activity;
import android.content.Intent;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter2;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;

import java.util.List;
import java.util.Map;

/**
 * Created by 你是我的 on 2019/3/28
 */
public interface SRContract {
    interface View extends BaseView<Presenter> {
        /**
         * 销毁当前活动
         */
        void finishAct();

        /**
         * 初始化控件
         */
        void initWidget();

        /**
         * 打开活动
         */
        void startAct(Intent intent);

        /**
         * 获得该碎片的activity
         */
        void getAct(Activity activity);

        /**
         * 更新该界面的记录
         */
        void updateRecord();

        /**
         * 显示搜索出的所有记录
         *
         * @param recordDetails 记录详情
         * @param dayRecords    记录总详情
         */
        void setList(List<RecordDetail> recordDetails, List<DayRecord> dayRecords);

        /**
         * 删除记录
         *
         * @param position 记录位置
         */
        void deleteRecord(int position);

        /**
         * 关闭打开的删除按钮
         */
        void closeMenu();

        void clearRV();
    }

    interface Presenter extends BasePresenter2 {

        /**
         * 设置销毁当前活动
         */
        void setFinishAct();

        /**
         * 设置打开活动
         */
        void setStartActivity(Intent intent);

        /**
         * 设置搜索输入的类型
         *
         * @param detailType 记录类型
         */
        void setSearchRecords(String detailType);

        /**
         * 删除列表中的记录item
         *
         * @param position 记录位置
         */
        void setDeleteRecord(int position);

        /**
         * 删除该记录
         * @param recordDetail 记录详情
         */
        void setDeleteRecordDetail(RecordDetail recordDetail);

        /**
         * 设置清楚列表
         */
        void setClearRV();
    }

    interface Model {
        /**
         * 获取所有类型的对应的所有记录
         */
        Map<String, List<RecordDetail>> getAllTypesRecords();


        /**
         * 删除该条记录
         * @param recordDetail 删除记录详情
         */
        void deleteRecordDetail(RecordDetail recordDetail);
    }
}

