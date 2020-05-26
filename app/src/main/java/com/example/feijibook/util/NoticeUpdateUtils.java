package com.example.feijibook.util;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter2;

import java.util.ArrayList;
import java.util.List;

/**
 * NoticeUpdateUtils
 *
 * @author yuepengfei
 * @date 2019/7/17
 * @description 通知更新记录
 */
public class NoticeUpdateUtils {
    /**
     * 存储需要更新记录的界面的Presenter，当更新记录时，调用通知各界面更新
     */
    public static List<BasePresenter2> updateRecordsPresenters = new ArrayList<>();

    /**
     * 存储需要更新类型（各个记录类型：餐饮、娱乐。。）的界面的Presenter，当更新类型时，调用通知各界面更新
     */
    public static List<BasePresenter2> updateTypesPresenters = new ArrayList<>();

    /**
     * 是否通知主界面更新个人信息
     */
    public static boolean updateInfo = false;

    /**
     * 是否通知主界面更新天气
     */
    public static boolean updateWeather = false;
    /**
     * 是否改变手势密码设置失败
     */
    public static boolean changeGesturePwFailed = false;
    /**
     * 操作按键声音失败
     */
    public static boolean changeSoundFailed = false;

    public static void noticeUpdateRecords() {
        for (BasePresenter2 presenter : updateRecordsPresenters) {
            presenter.isNeedUpdateRecord(true);
        }
    }

    public static void noticeUpdateTypes() {
        for (BasePresenter2 presenter : updateTypesPresenters) {
            presenter.isNeedUpdateRecord(true);
        }
    }
}
