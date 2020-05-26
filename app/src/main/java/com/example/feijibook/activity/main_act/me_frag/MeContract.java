package com.example.feijibook.activity.main_act.me_frag;

import android.app.Activity;
import android.content.Intent;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter2;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;

import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by 你是我的 on 2019/3/11
 */
public interface MeContract {
    interface View extends BaseView<Presenter> {
        /**
         * 打开活动，添加该层活动前移滚动初始动画
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
         * 显示头像
         *
         * @param portraitUrl 头像地址
         */
        void showPortrait(String portraitUrl);

        /**
         * 显示头像
         *
         * @param portrait 本地资源头像
         */
        void showPortrait(int portrait);

        /**
         * 显示昵称
         *
         * @param nickName 昵称，没有昵称显示用户名
         */
        void showNickName(String nickName);

        /**
         * 显示记录的天数和记录的条数
         *
         * @param dayCount    记录的天数
         * @param recordCount 记录的条数
         */
        void showDayAndRecord(String dayCount, String recordCount);

        /**
         * 设置手势密码的Switch的开关
         */
        void checkGesturePwSwitch();

        void checkSoundOpen();

        void showToast(String content);

        void showChangePwDialog();

        void showPwError();

        void dismissChangePwDialog();


    }

    interface Presenter extends BasePresenter2 {
        /**
         * 设置打开活动
         */
        void setStartActivity(Intent intent);

        /**
         * 设置获取记录天数和记账数量
         */
        void setGetDataCount();

        /**
         * 设置获取头像
         */
        void setGetPortrait();

        /**
         * 设置获取昵称
         */
        void setGetNickName();

        void setSaveSoundOpen(boolean isOpen);

        void setChangePw(String resPw, String newPw);

        void setShowChangePwDialog();
    }

    interface Model {
        /**
         * 获取记录天数和记账数量
         * key:dayCount recordCount
         */
        Map<String, Integer> getDayAndRecordCount();

        String getGesturePw();

        boolean isSoundOpen();

        /**
         * 保存声音设置
         */
        void saveSoundOpen(boolean isOpen);

        /**
         * 请求修改密码
         * @param account 用户
         * @param resPw 原密码
         * @param newPw 新密码
         */
        Observable<String> requestChangePw(String account, String resPw, String newPw);
    }
}
