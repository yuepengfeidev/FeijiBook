package com.example.feijibook.activity.sign_up_act;

import android.app.Activity;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;

import io.reactivex.Observable;

/**
 * SUContact
 *
 * @author PengFei Yue
 * @date 2019/10/6
 * @description
 */
public interface SUContact {
    interface View extends BaseView<Presenter> {
        /**
         * 销毁当前活动
         */
        void finishAct();

        /**
         * 获得该碎片的activity
         */
        void getAct(Activity activity);

        void showToast(String content);

    }

    interface Presenter extends BasePresenter {
        /**
         * 设置销毁当前活动
         */
        void setFinishAct();

        /**
         * 设置检查输入
         */
        void setCheckInput(String account, String password, String confirmPassword);

    }

    interface Model {
        /**
         * 注册请求
         *
         * @param account  用户名
         * @param password 密码
         * @return 请求回应
         */
        Observable<String> requestRegister(String account, String password);
    }
}
