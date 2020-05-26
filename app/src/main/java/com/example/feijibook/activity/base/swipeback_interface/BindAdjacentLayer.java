package com.example.feijibook.activity.base.swipeback_interface;

/*
 * Created by 你是我的 on 2019/3/14
 */

/**
 * 绑定相邻层活动
 */
public interface BindAdjacentLayer {
    /**
     * 活动退出监听，用于下层活动同步显示动画
     */
    void exit();

    /**
     * 活动进入，用于监听上层活动滑动，下层同步更新
     */
    void bindUpdate();

    /**
     * 下层左移动画结束后，Scroll到左侧屏幕外
     */
    void moveToFront();

    /**
     * 新活动打开时，新活动下层的界面同步左移动画
     */
    void animToFront();

    /**
     * 关闭活动
     */
    void finishAct();
}
