package com.example.feijibook.activity.add_custom_category_act;

import android.app.Activity;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter2;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.entity.record_type_bean.CustomType;
import com.example.feijibook.entity.record_type_bean.OptionalType;

import java.util.List;

/**
 * Created by 你是我的 on 2019/3/27
 */
public interface ACCContract {
    interface View extends BaseView<Presenter> {
        /**
         * 关闭活动
         */
        void finishAct();

        /**
         * 初始化控件,传入viewpager的fragment列表数据
         *
         * @param list 传入可自定义类型的图标
         */
        void initWidget(List<CustomType> list);

        /**
         * 拿到父布局的activity
         *
         * @param activity 该碎片的活动
         */
        void getAct(Activity activity);

        /**
         * 显示标题
         *
         * @param content 标题内容
         */
        void showTitle(String content);

        /**
         * 隐藏软键盘
         */
        void hideKeyboard();

        /**
         * 处理 完成时间
         */
        void disposeFinish();
    }

    interface Presenter extends BasePresenter {
        /**
         * 设置关闭活动
         */
        void setFinishAct();

        /**
         * 设置标题
         *
         * @param titleContent 标题内容
         */
        void setTitle(String titleContent);

        /**
         * 设置添加 自定义 类型
         *
         * @param typeName   自定义 类型名称
         * @param customType 选择自定义图标的信息
         * @param category   选择的大类别(收入或支出)
         */
        void setSaveCustomType(String typeName, CustomType customType, String category);

        /**
         * 设置隐藏软键盘
         */
        void setHideKeyboard();
    }

    interface Model {
        /**
         * 添加 自定义 类型 到存储类
         *
         * @param optionalType 自定义可选择类型
         */
        void saveCustomTypeToDB(OptionalType optionalType, Presenter presenter);
    }
}

