package com.example.feijibook.activity.camera_act;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.feijibook.app.Constants;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.http.BaseObserver;
import com.example.feijibook.http.ExceptionHandle;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.RecordListUtils;
import com.example.feijibook.util.SharedPreferencesUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * CameraPresenter
 *
 * @author PengFei Yue
 * @date 2019/9/14
 * @description
 */
public class CameraPresenter implements CameraContract.Presenter {
    private CameraContract.View mView;
    private CameraContract.Model mModel;
    private static String TEMP_PHOTO_PATH = MyApplication.BASE_PATH + "/Temp/tempPhoto.jpg";
    private static String TEMP_VIDEO_PATH = MyApplication.BASE_PATH + "/Temp/tempVideo.mp4";
    private int mCurMode;
    /**
     * 原纪录的id
     */
    private String mId;
    /**
     * 是否改变记录
     */
    private boolean isChangeRecord = false;

    CameraPresenter(Activity activity, CameraContract.View view, int mode, String id) {
        mView = view;
        mView.getAct(activity);
        mView.setPresenter(this);
        mCurMode = mode;
        mId = id;
    }

    @Override
    public void setFinishAct() {
        if (mId != null && !"".equals(mId) && isChangeRecord) {
            // 如果是已保存的记录，则改变内存类和数据库中的记录
            mModel.changeRecord();
            mModel.requestChangeDetailRecord(SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT),
                    mId, RecordListUtils.getIsEditingRecord()).subscribe(new BaseObserver<String>() {
                @Override
                public void onSuccess(String s) {
                    if (s.equals(Constants.CHANGE_SUCCESS)) {
                        Log.d(Constants.HTTP_TAG, "请求记录修改成功!");
                    } else {
                        Log.d(Constants.HTTP_TAG, "请求记录修改失败!");
                    }
                }

                @Override
                public void onHttpError(ExceptionHandle.ResponseException exception) {
                    Log.d(Constants.HTTP_TAG, "请求记录修改错误!");
                }
            });
        }
        if (isChangeRecord) {
            // 如果改变了记录则提示更新，且改变记录
            NoticeUpdateUtils.noticeUpdateRecords();
        }
        mView.finishAct();
    }

    @Override
    public void setStartActivity(Intent intent) {
        mView.startAct(intent);
    }

    @Override
    public void setHideConfirmPhoto() {
        mView.hideConfirmPhoto();
    }

    @Override
    public void setShowConfirmPhoto(Bitmap bitmap) {
        mView.showConfirmPhoto(bitmap);
    }

    @Override
    public void setSaveVideo() {
        String videoName = getCurData() + "video.mp4";
        String videoPath = MyApplication.VIDEO_PATH + videoName;
        mModel.saveFile(TEMP_VIDEO_PATH, videoPath);
        RecordListUtils.getIsEditingRecord().setVideoUrl(videoName);
        isChangeRecord = true;
        // 存储完视频则关闭相机界面
        setFinishAct();
    }

    @Override
    public void setSavePhoto() {
        String photoName = getCurData() + "photo.jpg";
        String photoPath = MyApplication.PHOTO_PATH + photoName;
        mModel.saveFile(TEMP_PHOTO_PATH, photoPath);
        String photoRecord;
        // 将所有照片路径通过“|”分隔开，合在一起存储
        if (RecordListUtils.getIsEditingRecord().getImgUrl() == null) {
            photoRecord = photoName + "|";
        } else {
            photoRecord = RecordListUtils.getIsEditingRecord().getImgUrl() + photoName + "|";
        }
        // 修改 编辑的记录 的照片路径
        RecordListUtils.getIsEditingRecord().setImgUrl(photoRecord);
        isChangeRecord = true;
        mView.hideConfirmPhoto();
    }

    /**
     * 获取当前时间,用来给文件夹命名
     */
    private String getCurData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        return simpleDateFormat.format(new Date());
    }


    @Override
    public void start() {
        mModel = new CameraModel();
        if (mId != null && !"".equals(mId)) {
            // 如果有id，表示要修改该id的记录，则获取该id的记录
            RecordDetail record = mModel.getRecord(mId);
            // 设置正在编辑的记录
            RecordListUtils.setIsEditingRecord((RecordDetail) record.clone());
            // 设置用于删除记录的原记录
            RecordListUtils.setResRecord((RecordDetail) record.clone());
        }

    }

    @Override
    public void setInit() {
        int photoMode = 0;
        int videoMode = 1;
        if (mCurMode == photoMode) {
            mView.initPhotoMode();
        } else if (mCurMode == videoMode) {
            mView.initVideoMode();
        }
        mView.initListener();
    }
}
