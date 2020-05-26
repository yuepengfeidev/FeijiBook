package com.example.feijibook.activity.record_detail_act;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.app.Constants;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.http.BaseObserver;
import com.example.feijibook.http.ExceptionHandle;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.RecordListUtils;
import com.example.feijibook.util.SharedPreferencesUtils;
import com.example.feijibook.util.SoundShakeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * RDPresenter
 *
 * @author yuepengfei
 * @date 2019/8/7
 * @description
 */
public class RDPresenter implements RDContract.Presenter {
    private RDContract.View mView;
    private RDContract.Model mModel;
    /**
     * 当前记录的id
     */
    private String mId;
    /**
     * 当前记录
     */
    private RecordDetail mRecordDetail;
    private boolean isNeedUpdate = false;

    RDPresenter(Activity activity, RDContract.View view, String id) {
        mView = view;
        mView.setPresenter(this);
        mView.getAct(activity);
        mId = id;
    }

    @Override
    public void setFinishAct() {
        mView.finishAct();
    }

    @Override
    public void setStartActivity(Intent intent) {
        mView.startAct(intent);
    }

    @Override
    public void setShowFirstPhoto(String photoUrl) {
        mView.showFirstPhoto(compressBitmap(3, photoUrl));
    }

    private Bitmap compressBitmap(int sampleSize, String url) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        // 值越大，图像越不清晰，但读取资源会更快
        options.inSampleSize = sampleSize;
        return BitmapFactory.decodeFile(url, options);
    }

    @Override
    public void setShowData() {
        if (mRecordDetail.getVideoUrl() != null && !"".equals(mRecordDetail.getVideoUrl())) {
            mView.showVideoStubView(true);
            mView.showVideo(MyApplication.VIDEO_PATH + mRecordDetail.getVideoUrl());
        } else {
            mView.showVideoStubView(false);
        }
        if (mRecordDetail.getImgUrl() != null && !"".equals(mRecordDetail.getImgUrl())) {
            mView.showPhotosViewStub(true);
            String[] imgUrls = mRecordDetail.getImgUrl().split("\\|");
            List<Bitmap> imgBitmaps = new ArrayList<>();
            List<String> urls = new ArrayList<>();
            for (String url : imgUrls) {
                imgBitmaps.add(compressBitmap(8, MyApplication.PHOTO_PATH + url));
                urls.add("file:/" + MyApplication.PHOTO_PATH + url);
            }

            mView.showPhotos(imgBitmaps,urls);
            // 有照片则显示第一张，没有照片则显示默认蓝色背景
            if (imgUrls.length >= 1) {
                setShowFirstPhoto(MyApplication.PHOTO_PATH + imgUrls[imgUrls.length - 1]);
            } else {
                mView.showFirstPhoto(BitmapFactory.decodeResource(MyApplication.sContext.getResources(), R.drawable.bg_sky_blue));
            }
        } else {
            mView.showPhotosViewStub(false);
        }
        mView.showRecordDetail(mRecordDetail);
    }

    @Override
    public void start() {
        mModel = new RDModel();
        if (mId != null) {
            // 有id则获取id的数据
            mRecordDetail = mModel.getRecord(mId);
        } else {
            // 没有id则表示是编辑状态，
            mRecordDetail = RecordListUtils.getIsEditingRecord();
        }
    }

    @Override
    public void setClickSaveOrDelete() {
        if (mId != null) {
            SoundShakeUtil.playSound(SoundShakeUtil.DELETE_SOUND);
            mModel.requestDeleteRecord(mRecordDetail.getId(),
                    SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT))
                    .subscribe(new BaseObserver<String>() {
                        @Override
                        public void onSuccess(String s) {
                            if (s.equals(Constants.DELETE_SUCCESS)) {
                                Log.d(Constants.HTTP_TAG, "请求删除记录成功！");
                            } else {
                                Log.d(Constants.HTTP_TAG, "请求删除记录失败！");
                            }
                        }

                        @Override
                        public void onHttpError(ExceptionHandle.ResponseException exception) {
                            Log.d(Constants.HTTP_TAG, "请求删除记录错误！");
                        }
                    });
            // 查看状态，点击删除
            mModel.deleteRecord(mRecordDetail);
            if (mRecordDetail.getImgUrl() != null) {
                String[] imgUrl = mRecordDetail.getImgUrl().split("\\|");
                for (String url : imgUrl) {
                    mModel.deleteFile(MyApplication.PHOTO_PATH + url);
                }
            }
            if (mRecordDetail.getVideoUrl() != null) {
                mModel.deleteFile(MyApplication.VIDEO_PATH + mRecordDetail.getVideoUrl());
            }
            mView.finishAct();
        } else {
            SoundShakeUtil.playSound(SoundShakeUtil.DEEP_SWOOSH1_SOUND);
            // 如果为编辑添加状态，点击后保存记录，并切换为删除icon
            mModel.saveRecord();
            mView.showToast("保存记录成功!");
            mRecordDetail = RecordListUtils.getIsEditingRecord();
            mId = mRecordDetail.getId();
            mModel.requestAddDetailRecord(SharedPreferencesUtils.
                    getStrFromSp(SharedPreferencesUtils.ACCOUNT), mRecordDetail)
                    .subscribe(new BaseObserver<String>() {
                        @Override
                        public void onSuccess(String s) {
                            if (s.equals(Constants.UPLOAD_SUCCESS)) {
                                Log.d(Constants.HTTP_TAG, "上传添加记录成功！");
                            } else {
                                Log.d(Constants.HTTP_TAG, "上传添加记录失败！"); } }
                        @Override
                        public void onHttpError(ExceptionHandle.ResponseException exception) {
                            Log.d(Constants.HTTP_TAG, "上传添加记录错误！"); }});
            mView.getId(mId);
            mView.changeArcMenuItem(ContextCompat.getDrawable(MyApplication.sContext, R.drawable.icon_arc_delete));
            BaseActivity.removeBindAndFinish();
            BaseActivity.bingMainAct();
        }
        NoticeUpdateUtils.noticeUpdateRecords();
    }

    @Override
    public void deleteImage(int pos) {
        String[] imgUrl = mRecordDetail.getImgUrl().split("\\|");
        String picUrl = mRecordDetail.getImgUrl().replace(imgUrl[pos] + "|", "");
        mModel.deleteFile(MyApplication.PHOTO_PATH + picUrl);
        if (mId == null) {
            // 如果id为空，则为编辑记录状态，存储删除照片后的记录到静态编辑记录
            mRecordDetail.setImgUrl(picUrl);
            RecordListUtils.setIsEditingRecord(mRecordDetail);
        } else {
            RecordListUtils.setResRecord(mRecordDetail);
            RecordDetail recordDetail = (RecordDetail) mRecordDetail.clone();
            recordDetail.setImgUrl(picUrl);
            RecordListUtils.setIsEditingRecord(recordDetail);
            // 不是编辑记录状态，则改变数据库中的数据
            RecordDetail record = mModel.changeRecord();
            mModel.requestChangeDetailRecord(SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT),
                    mId, record).subscribe(new BaseObserver<String>() {
                @Override
                public void onSuccess(String s) {
                    if (s.equals(Constants.CHANGE_SUCCESS)) {
                        Log.d(Constants.HTTP_TAG, "请求记录修改成功!");
                    }
                }

                @Override
                public void onHttpError(ExceptionHandle.ResponseException exception) {
                    Log.d(Constants.HTTP_TAG, "请求记录修改错误!");
                }
            });
            // 改变会改变记录的id，必须提示主界面列表里的记录进行更新
            NoticeUpdateUtils.noticeUpdateRecords();
        }
        if (picUrl == null || "".equals(picUrl)) {
            // 如果没有照片了，则不显示照片布局,且不显示背景记录照片
            mView.showPhotosViewStub(false);
            mView.showFirstPhoto(BitmapFactory.decodeResource(MyApplication.sContext.getResources(), R.drawable.bg_sky_blue));
        } else if (pos == imgUrl.length - 1) {
            setShowFirstPhoto(imgUrl[imgUrl.length - 2]);
        }
    }

    @Override
    public void setInit() {
        mView.initWidget();
        mView.initListener();
        setShowData();
        if (mId != null) {
            // 有id表示时查看记录，则显示删除icon
            mView.changeArcMenuItem(ContextCompat.getDrawable(MyApplication.sContext, R.drawable.icon_arc_delete));
            mView.getId(mId);
        } else {
            // 没有id表示编辑状态，则显示保存icon
            mView.changeArcMenuItem(ContextCompat.getDrawable(MyApplication.sContext, R.drawable.icon_arc_save));
        }
    }

    @Override
    public void isNeedUpdateRecord(boolean needUpdate) {
        isNeedUpdate = needUpdate;
    }

    @Override
    public void checkUpdateRecord() {
        if (!isNeedUpdate) {
            return;
        }
        // 重新获取 编辑的记录
        mRecordDetail = RecordListUtils.getIsEditingRecord();
        // 当没有id时，表示该条记录正在添加，还没有添加完，则不消除正在编辑记录，否则消除
        if (mId != null) {
            RecordListUtils.setIsEditingRecord(null);
        }
        mId = mRecordDetail.getId();
        mView.getId(mId);
        mView.updateRecord();
        isNeedUpdate = false;
    }
}
