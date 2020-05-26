package com.example.feijibook.activity.category_setting_act;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;

import java.util.List;


/**
 * Created by 你是我的 on 2019/3/27
 */
public interface CSContract {

    interface View extends BaseView<Presenter> {
        /**
         * 关闭活动
         */
        void finishAct();

        /**
         * 初始化控件,传入viewpager的fragment列表数据
         *
         * @param fragments 给viewpager添加的碎片列表
         */
        void initWidget(List<Fragment> fragments);

        /**
         * 拿到父布局的activity
         *
         */
        void getAct(Activity activity);

        /**
         * 切换到支出类型编辑界面
         */
        void selectExpendPager();

        /**
         * 切换到收入类型编辑界面
         */
        void selectIncomePager();


        /**
         * 打开活动
         */
        void startAct(Intent intent);
    }

    interface Presenter extends BasePresenter {

        /**
         * 设置关闭活动
         */
        void setFinishAct();

        /**
         * 给碎片传入activity
         *
         */
        void setAct(Activity activity);

        /**
         * 设置支出界面切换
         *
         * @param position 当前页面
         */
        void setSelectExpendPager(int position);

        /**
         * 设置收入界面切换
         *
         * @param position 当前页面
         */
        void setSelectIncomePager(int position);

        /**
         * 通过接收到的值显示viewpager
         * @param type 接收到的类型
         */
        void showPager(String type);

        /**
         * 设置打开活动
         * @param pagerPosition 当前viewpager的页面
         */
        void setStartActivity(Intent intent,int pagerPosition);
    }

    interface Model {

    }
}
