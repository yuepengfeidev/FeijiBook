package com.example.feijibook.activity.album_act.record_album_frag;

import android.app.Activity;
import android.content.Intent;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.entity.record_bean.RecordDetail;

import java.util.List;
import java.util.Map;

/**
 * RAContract
 *
 * @author PengFei Yue
 * @date 2019/10/4
 * @description
 */
public interface RAContract {
    interface View extends BaseView<Presenter> {

        /**
         * 打开活动，添加该层活动前移滚动初始动画
         */
        void startAct(Intent intent);

        /**
         * 初始化RecyclerView
         *
         * @param isPhotosAlbum 是否是照片记录列表
         */
        void initRV(boolean isPhotosAlbum);


        /**
         * 显示记录到列表上
         *
         * @param list 符合要求的记录列表
         */
        void showRecords(List<Map.Entry<String, RecordDetail>> list);

        void getAct(Activity activity);

        void showSelect(boolean isShow);

        /**
         * 清除所有选择的项
         */
        void clearSelect();

        List<RecordDetail> getSelectedRecords();

        /**
         * 显示或隐藏“暂无数据”标志
         */
        void showOrHideNoDataSign(boolean show);

    }

    interface Presenter extends BasePresenter {

        /**
         * 设置打开活动
         */
        void setStartActivity(Intent intent);

        /**
         * 设置显示含有照片的记录到列表
         */
        void setPhotosRecords();

        /**
         * 设置含有视频的记录到列表上
         */
        void setVideoRecords();

        /**
         * 设置是否显示列表选择
         *
         * @param isShow 是否选择
         */
        void setShowSelect(boolean isShow);

        void setClearSelected();

        /**
         * 获取所有选中的记录
         */
        List<RecordDetail> getSelectedRecords();

    }

    interface Model {
        /**
         * 获取含有照片的记录
         */
        List<Map.Entry<String, RecordDetail>> getHavePhotosRecords();

        /**
         * 获取含有视频的记录
         */
        List<Map.Entry<String, RecordDetail>> getHaveVideoRecords();
    }
}
