package com.example.feijibook.activity.album_act;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.ArrayMap;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;

/**
 * AlbumContract
 *
 * @author PengFei Yue
 * @date 2019/10/3
 * @description
 */
public interface AlbumContract {
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
         * 初始化控件
         *
         * @param fragments     viewpager中的fragment列表
         * @param isPhotosAlbum 进入时，是否显示照片记录列表
         */
        void initWidget(List<Fragment> fragments, boolean isPhotosAlbum);

        void showSelectLayout();

        void hideSelectLayout();

        void showToast(String content);

        /**
         * 显示选中的数量
         */
        void showSelected(String content);

        void showDialog();

        void setDialogPercent(String percent);

        void dismissDialog();
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
         * 显示列表外的选择视图
         */
        void setShowSelectLayout();

        void setHideSelectLayout();

        /**
         * 设置显示是否显示列表的选择视图
         */
        void setShowSelect(boolean isShow);

        /**
         * 设置上传
         */
        void setUpload();

        /**
         * 设置处理选择显示或上传
         *
         * @param str 当前显示的字符串（选择  上传）
         */
        void setSelectOrUpload(String str);

        /**
         * 显示选中的数量
         *
         * @param size 选中数量
         */
        void setShowSelectedContent(int size);

        /**
         * 设置当前页面
         */
        void setPage(int pos);

        void setShowProgressPercent(String percent);
    }

    interface Model {

        /**
         * 上传所有选中的照片
         */
        Observable<String> requestUploadPhotos(String account, ArrayMap<String, Object> params);


        /**
         * 上传所有选中的视频
         */
        Observable<String> requestUploadVideos(String account, ArrayMap<String, Object> params);

        /**
         * 标记视频上传状态
         */
        void signVideosUpload(List<String> list);

        void signPhotosUpload(List<String> list);

    }
}