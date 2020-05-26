package com.example.feijibook.activity.login_in_act;

import android.app.Activity;
import android.content.Intent;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;

import io.reactivex.Observable;

/**
 * LIContract
 *
 * @author PengFei Yue
 * @date 2019/10/6
 * @description
 */
public interface LIContract {
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
         * 显示账号名
         */
        void showAccount(String account);

        /**
         * 显示密码
         */
        void showPassword(String password);

        /**
         * 设置保存密码的CheckBox的状态
         *
         * @param checked true为check
         */
        void checkRememberPassword(boolean checked);

        void showToast(String content);
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
         * 设置是否存储密码
         *
         * @param isRemember true 为存储密码
         */
        void setDisposePassword(boolean isRemember);

        /**
         * 设置检查账号名和密码
         */
        void setCheckInput(String account, String password);

    }

    interface Model  {
        /**
         * 该账号是否是第一次登录
         */
        boolean isFirstLogin();

        /**
         * 是否记住该账号的密码
         */
        boolean isRememberPassword();

        /**
         * 对密码进行操作
         *
         * @param isRemember true为记住密码
         */
        void disposePassword(boolean isRemember);

        /**
         * 获取该账号
         */
        String getUserName();

        /**
         * 获取该账号的密码
         */
        String getPassWord();

        /**
         * 保存密码
         *
         * @param password 需要记录的密码
         */
        void savePassword(String password);

        /**
         * 保存账户名
         *
         * @param account 账户名
         */
        void saveAccount(String account);

        /**
         * 登录请求
         * @param account 账户名
         * @param password 密码
         * @return 请求回应
         */
        Observable<String> requestLogin(String account,String password);

        String getGesturePw();

    }
}
