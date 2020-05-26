package com.example.feijibook.activity.base.todo_mvp_base_interface;

/**
 * Created by 你是我的 on 2019/3/11
 */
public interface BaseView<T> {
    void setPresenter(T presenter);

    /**
     * 初始化监听
     */
    void initListener();

}
