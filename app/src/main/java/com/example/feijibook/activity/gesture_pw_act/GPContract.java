package com.example.feijibook.activity.gesture_pw_act;

import android.app.Activity;
import android.content.Intent;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;

/**
 * GPContract
 *
 * @author PengFei Yue
 * @date 2019/11/3
 * @description
 */
public interface GPContract {
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
         * 隐藏返回键
         */
        void hideBack();

        /**
         * 显示当前输入手势密码后的状态
         *
         * @param state 状态
         * @param anim  是否播放错误动画
         */
        void showState(String state, int color,boolean anim);

        /**
         * 清除手势
         */
        void clearGesture();

        /**
         * 错误手势
         */
        void errorGesture();
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
         * 处理密码
         *
         * @param input 手势输入
         */
        void disposeGesturePw(String input);
    }

    interface Model {
        /**
         * 获取手势密码
         *
         * @return 手势密码
         */
        String getGesturePw();

        /**
         * 存储手势密码
         */
        void saveGesturePw(String gesturePw);
    }
}
