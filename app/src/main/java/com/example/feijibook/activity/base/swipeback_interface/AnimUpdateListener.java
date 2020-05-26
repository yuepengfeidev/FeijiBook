package com.example.feijibook.activity.base.swipeback_interface;

/**
 * Created by 你是我的 on 2019/3/14
 */

// 动画更新监听
public interface AnimUpdateListener {
    void moveAnim(int move);// 界面随滑动距离而滑动

    void finishAnim();// 活动finish之前，下层活动与屏幕对其动画

    void initAnim();// 没有关闭当前活动，则底层活动初始化到之前的状态（滚动到左侧屏幕之外）
}
