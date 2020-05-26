package com.example.feijibook.activity.base.todo_mvp_base_interface;

/**
 * BasePresenter2
 *
 * @author yuepengfei
 * @date 2019/7/17
 * @description 该接口为需要更新记录的界面所使用
 */
public interface BasePresenter2 extends BasePresenter {
    /**
     * 是否需要更新该界面的记录
     *
     * @param needUpdate true为更新记录
     */
    void isNeedUpdateRecord(boolean needUpdate);

    /**
     * 检查是否要更新界面记录
     */
    void checkUpdateRecord();

}
