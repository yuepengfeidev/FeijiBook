package com.example.feijibook.activity.add_record_act_from_add_icon_act.type_choose_frag;

import android.app.Activity;
import android.content.Intent;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter2;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.adapter.RecordTypeChooseRVAdapter;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_type_bean.OptionalType;

import java.util.List;

/**
 * TCContract
 *
 * @author PengFei Yue
 * @date 2019/10/5
 * @description
 */
public interface TCContract {
    interface View extends BaseView<Presenter> {
        void initWidget();// 初始化控件


        void selectDispose(int position, boolean state);// checkbox的select操作

        /**
         * 打开活动
         */
        void startAct(Intent intent);

        /**
         * 改变RecyclerView的大小
         */
        void changRVSize(int position, int height);

        /**
         * 切换viewpager后,设置上一次的item的view Holder为null
         */
        void setLastViewHolderNull();

        /**
         * 初始化 类型列表数据
         */
        void initRVData(List<OptionalType> incomeOptionalList);

        /**
         * 更新该界面的记录
         */
        void updateRecord();

        /**
         * 选择相应记录的类型icon
         *
         * @param recordDetail 修改的记录
         */
        void checkType(RecordDetail recordDetail);
    }

    interface Presenter extends BasePresenter2 {

        /**
         * 设置 item select 或 切换
         */
        void setSelectItem(OptionalType optionalType, int position,
                           RecordTypeChooseRVAdapter.ViewHolder curViewHolder,
                           Activity activity);

        /**
         * 设置 设置按钮 点击
         */
        void setSettingClick(Intent intent, Activity activity);

        /**
         * 获得recyclerview的高度
         */
        int getRecyclerViewHeight(Activity activity, boolean isShrink);

        /**
         * 滑动viewpager时恢复recyclerview的大小，同时关闭软键盘
         */
        void recoverRV(Activity activity);

        /**
         * 获取 类型 recyclerview 的 收入类型数据
         */
        void getRVData();

        /**
         * 设置存储类型编辑后的选择状态到数据库
         */
        void setSaveTypeChooseSetting();

        /**
         * 设置选择记录类型
         *
         * @param recordDetail 当前修改的记录
         */
        void setCheckType(RecordDetail recordDetail);
    }

    interface Model {
        /**
         * 获取 可选择 收入 类型列表
         *
         * @return 可选择 收入类型列表
         */
        List<OptionalType> getIncomeTypeListData();

        /**
         * 存储编辑后的类型选择状态 到数据库
         */
        void saveTypeSettingToDB(String type);

        /**
         * 获取 可选择 支出 类型列表
         *
         * @return 可选择 支出类型列表
         */
        List<OptionalType> getExpendTypeListData();
    }
}
