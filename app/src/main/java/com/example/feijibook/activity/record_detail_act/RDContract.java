package com.example.feijibook.activity.record_detail_act;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter2;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.entity.record_bean.RecordDetail;

import java.util.List;

import io.reactivex.Observable;

/**
 * RDContract
 *
 * @author yuepengfei
 * @date 2019/8/7
 * @description
 */
public interface RDContract {
    interface View extends BaseView<Presenter> {
        /**
         * 销毁当前活动
         */
        void finishAct();

        /**
         * 打开活动
         */
        void startAct(Intent intent);

        /**
         * 获得该碎片的activity
         */
        void getAct(Activity activity);

        /**
         * 获取该记录的ID
         *
         * @param id 该记录的id
         */
        void getId(String id);

        /**
         * 初始化控件
         */
        void initWidget();

        /**
         * 当记录有拍照时，则显示第一张照片为顶部背景
         *
         * @param bitmap 第一张照片
         */
        void showFirstPhoto(Bitmap bitmap);

        /**
         * 如果是从记录进入该页面，ArcMenu第一个子项为删除按钮，
         * 如果是从添加记录进入该页面，第一个子项为保存按钮
         *
         * @param drawable 对应按钮的图标（删除/保存）
         */
        void changeArcMenuItem(Drawable drawable);

        /**
         * 显示photo部分的ViewStub
         *
         * @param needShow 是否需要显示
         */
        void showPhotosViewStub(boolean needShow);

        /**
         * 显示该记录的所有照片
         *
         * @param bitmaps 照片列表
         */
        void showPhotos(List<Bitmap> bitmaps,List<String> urls);

        /**
         * 显示视频
         *
         * @param videoUrl 视频url
         */
        void showVideo(String videoUrl);

        /**
         * 显示Video部分的ViewStub
         *
         * @param needShow 是否需要显示
         */
        void showVideoStubView(boolean needShow);

        /**
         * 显示Toast提示
         *
         * @param content 提示内容
         */
        void showToast(String content);

        /**
         * 更新该界面的记录
         */
        void updateRecord();

        /**
         * 显示记录详情
         *
         * @param recordDetail 记录
         */
        void showRecordDetail(RecordDetail recordDetail);

    }

    interface Presenter extends BasePresenter2 {
        /**
         * 设置销毁当前活动
         */
        void setFinishAct();

        /**
         * 设置打开活动
         */
        void setStartActivity(Intent intent);

        /**
         * 设置显示第一张图片
         *
         * @param photoUrl 第一张图片的地址
         */
        void setShowFirstPhoto(String photoUrl);

        /**
         * 设置判断显示照片和视频,详情等数据
         */
        void setShowData();

        /**
         * 设置点击删除或保存按钮
         */
        void setClickSaveOrDelete();

        /**
         * 删除记录中的照片照片
         *
         * @param pos 照片的位置
         */
        void deleteImage(int pos);
    }

    interface Model {
        /**
         * 获取该id的记录
         */
        RecordDetail getRecord(String id);


        /**
         * 删除id
         *
         * @param recordDetail 删除的记录
         */
        void deleteRecord(RecordDetail recordDetail);

        /**
         * 保存记录
         */
        void saveRecord();

        /**
         * 删除路径的文件
         *
         * @param filePath 文件路径
         */
        void deleteFile(String filePath);

        RecordDetail changeRecord();

        Observable<String> requestChangeDetailRecord(String account, String resId, RecordDetail recordDetail);

        Observable<String> requestAddDetailRecord(String account,RecordDetail recordDetail);

        Observable<String> requestDeleteRecord(String id, String account);
    }
}