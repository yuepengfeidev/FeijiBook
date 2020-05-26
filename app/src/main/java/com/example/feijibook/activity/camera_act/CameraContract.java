package com.example.feijibook.activity.camera_act;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.entity.record_bean.RecordDetail;

import io.reactivex.Observable;

/**
 * CameraContract
 *
 * @author PengFei Yue
 * @date 2019/9/14
 * @description
 */
public interface CameraContract {
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
         * 初始滑照相模式控件
         */
        void initPhotoMode();

        /**
         * 初始滑摄像模式控件
         */
        void initVideoMode();

        /**
         * 显示确认照片界面
         *
         * @param photo 刚拍的且需要确认的照片
         */
        void showConfirmPhoto(Bitmap photo);

        /**
         * 隐藏确认照片界面
         */
        void hideConfirmPhoto();

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
         * 设置隐藏确认的照片
         */
        void setHideConfirmPhoto();

        /**
         * 设置显示确认的照片
         */
        void setShowConfirmPhoto(Bitmap bitmap);

        /**
         * 设置存储视频
         */
        void setSaveVideo();

        /**
         * 设置存储照片
         */
        void setSavePhoto();

    }

    interface Model {
        /**
         * 存储文件
         *
         * @param tempPath 临时文件路径
         * @param savePath 文件路径
         */
        void saveFile(String tempPath, String savePath);

        /**
         * 获取刚拍摄的临时照片
         *
         * @param tempPhotoPath 临时照片路径
         * @return 临时照片
         */
        Bitmap getTempPhoto(String tempPhotoPath);

        /**
         * 改变原来的记录（删除原先的，添加改变的）
         */
        void  changeRecord();

        /**
         * 通过id获取相应记录
         *
         * @param id 记录的id
         */
        RecordDetail getRecord(String id);

        Observable<String> requestChangeDetailRecord(String account, String resId, RecordDetail recordDetail);
    }
}
