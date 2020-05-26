package com.example.feijibook.activity.user_info_act;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * UIContract
 *
 * @author PengFei Yue
 * @date 2019/10/21
 * @description
 */
public interface UIContract {
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
         * 显示头像选择设置的会话弹窗
         */
        void showPortraitSettingDialog();

        /**
         * 显示设置性别的会话弹窗
         */
        void showSexSettingDialog();

        /**
         * 显示设置昵称的会话弹窗
         */
        void showNicknameDialog();

        void showPortrait(String url);

        void showPortrait(int res);

        void showNickName(String nickName);

        void showSex(String sex);

        void showAccount(String account);

        /**
         * 活动跳转
         *
         * @param intent 活动
         * @param type   类型（照相，截切，相册）
         */
        void startActForResult(Intent intent, int type);

        void showToast(String content);



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
         * 设置显示头像选择设置的会话弹窗
         */
        void setShowPortraitSettingDialog();

        /**
         * 设置显示设置性别的会话弹窗
         */
        void setShowSexSettingDialog();

        /**
         * 设置显示设置昵称的会话弹窗
         */
        void setShowNicknameDialog();

        /**
         * 设置性别
         *
         * @param itemIndex 选择的缩影 1是“男” 2是“女”
         */
        void setSex(int itemIndex);

        /**
         * 设置昵称
         */
        void setNickName(String nickName);

        void setShowAccount();

        void setShowPortrait();


        /**
         * 剪裁图片
         */
        void setClippingPortrait(Uri contentUri);

        /**
         * 处理活动返回结果（相机，剪裁，相册）
         */
        void onActivityResult(int requestCode, int resultCode, Intent intent);

        /**
         * 拍照获取头像
         */
        void getPortraitFromCamera();

        /**
         * 从相册获取头像
         */
        void getPortraitFromAlbum();

        /**
         * 设置退出登录
         */
        void setSignOut(Activity activity);
    }

    interface Model {
        /**
         * 请求上传头像
         *
         * @param portrait 头像文件
         */
        Observable<String> requestUploadPortrait(MultipartBody.Part portrait);

        /**
         * 请求设置昵称
         *
         * @param nickName 昵称
         */
        Observable<String> requestSetNickName(String account, String nickName);

        /**
         * 请求设置性别
         *
         * @param sex 性别
         */
        Observable<String> requestSetSex(String account, String sex);

    }
}
