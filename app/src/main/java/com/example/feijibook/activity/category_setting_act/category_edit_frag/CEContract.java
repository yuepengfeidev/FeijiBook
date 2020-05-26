package com.example.feijibook.activity.category_setting_act.category_edit_frag;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter2;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.entity.record_type_bean.AddtiveType;
import com.example.feijibook.entity.record_type_bean.OptionalType;

import java.util.List;

/**
 * CEContract
 *
 * @author PengFei Yue
 * @date 2019/10/12
 * @description
 */
public interface CEContract {
    interface View extends BaseView<Presenter> {

        /**
         *  初始化控件
         */
        void initWidget();

        /**
         *  初始化 rcyclerview 的数据
         * @param optionalTypes 可选择类型列表
         * @param additiveTypes 可添加类型列表
         */
        void initRVData(List<OptionalType> optionalTypes,List<AddtiveType> additiveTypes);

        /**
         * 更新该界面的记录
         */
        void updateRecord();
    }

    interface Presenter extends BasePresenter2 {

        /**
         * 设置 类型列表 的存储
         * @param optionalTypes 可选列表
         * @param additiveTypes 可添加列表
         */
        void setTypeList(List<OptionalType> optionalTypes, List<AddtiveType> additiveTypes);

        /**
         * 设置初始化 recyclerview 的数据
         */
        void setInitRVData();
    }

    interface Model {
        /**
         * 存储到 设置列表工具类中
         *
         * @param type          income 或 expend
         * @param optionalTypes 可选列表
         * @param additiveTypes  可添加列表
         */
        void saveTypeList(String type, List<OptionalType> optionalTypes, List<AddtiveType> additiveTypes);

        /**
         * 获取 支出 的可选择类型列表
         *
         * @return 支出 的可选择类型列表
         */
        List<OptionalType> getExpendOptionalTypeList();

        /**
         * 获取 支出 的可添加类型列表
         *
         * @return 支出 的可添加类型列表
         */
        List<AddtiveType> getExpendAdditiveTypeList();


        /**
         * 获取 收入 的可选择类型列表
         * @return 收入 的可选择类型列表
         */
        List<OptionalType> getIncomeOptionalTypeList();
        /**
         * 获取 收入 的可添加类型列表
         * @return 收入 的可添加类型列表
         */
        List<AddtiveType> getIncomeAdditiveTypeList( );
    }
}
