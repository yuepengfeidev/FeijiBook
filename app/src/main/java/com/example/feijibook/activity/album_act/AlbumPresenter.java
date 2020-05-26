package com.example.feijibook.activity.album_act;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.ArrayMap;
import android.util.Log;

import com.example.feijibook.activity.album_act.record_album_frag.RAContract;
import com.example.feijibook.activity.album_act.record_album_frag.RAPresenter;
import com.example.feijibook.activity.album_act.record_album_frag.RecordAlbumFragment;
import com.example.feijibook.app.Constants;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.http.ExceptionHandle;
import com.example.feijibook.http.SchedulersCompat;
import com.example.feijibook.http.upload_file.LoadCallBack;
import com.example.feijibook.http.upload_file.UploadOnSubscribe;
import com.example.feijibook.util.SharedPreferencesUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * AlbumPresenter
 *
 * @author PengFei Yue
 * @date 2019/10/3
 * @description
 */
public class AlbumPresenter implements AlbumContract.Presenter {
    private AlbumContract.View mView;
    private boolean mIsPhotosAlbum;
    private Activity mActivity;
    private RAContract.Presenter photosRecordPresenter;
    private RAContract.Presenter videoRecordPresenter;
    private AlbumContract.Model mModel;
    private boolean showProgressBar = true;

    AlbumPresenter(Activity activity, AlbumContract.View view, boolean isPhotosAlbum) {
        mView = view;
        mIsPhotosAlbum = isPhotosAlbum;
        mActivity = activity;
        mView.getAct(activity);
        mView.setPresenter(this);
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
    public void setShowSelectLayout() {
        mView.showSelectLayout();
    }

    @Override
    public void setHideSelectLayout() {
        mView.hideSelectLayout();
    }

    @Override
    public void setShowSelect(boolean isShow) {
        if (mIsPhotosAlbum) {
            photosRecordPresenter.setShowSelect(isShow);
        } else {
            videoRecordPresenter.setShowSelect(isShow);
        }
    }

    @Override
    public void setUpload() {
        List<RecordDetail> recordDetails;
        String account = SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT);
        // 获取选中的记录
        if (mIsPhotosAlbum) {
            recordDetails = photosRecordPresenter.getSelectedRecords();
            if (recordDetails.size() == 0) {
                mView.showToast("请选择上传项！");
                return;
            }
            mView.showDialog();
            List<String> ids = new ArrayList<>();
            List<String> fileNames = new ArrayList<>();
            ArrayMap<String, Object> params = new ArrayMap<>();
            UploadOnSubscribe uploadOnSubscribe = new UploadOnSubscribe();
            Observable.create(uploadOnSubscribe).subscribe();
            for (RecordDetail recordDetail : recordDetails) {
                recordDetail.setImgUpload(true);
                for (String imgUrl : recordDetail.getImgUrl().split("\\|")) {
                    fileNames.add(MyApplication.PHOTO_PATH + imgUrl);
                    ids.add(recordDetail.getId());
                }
            }
            params.put("ids", ids);
            params.put("filePathList", fileNames);
            params.put("UploadOnSubscribe", uploadOnSubscribe);

            Observable.merge(Observable.create(uploadOnSubscribe), mModel.requestUploadPhotos(account, params))
                    .compose(SchedulersCompat.applyToSchedulers())
                    .subscribe(new LoadCallBack<Serializable>() {
                        @Override
                        protected void onProgress(String percent) {
                            setShowProgressPercent(percent);
                        }

                        @Override
                        public void onSuccess(Serializable serializable) {
                            if (serializable instanceof String) {
                                String s = (String) serializable;
                                if (s.equals(Constants.UPLOAD_SUCCESS)) {
                                    List<String> list = new ArrayList<>(ids);
                                    mModel.signPhotosUpload(list);
                                    Log.d(Constants.HTTP_TAG, "请求上传照片成功！");
                                } else {
                                    Log.d(Constants.HTTP_TAG, "请求上传照片失败！");
                                }
                            }
                        }

                        @Override
                        public void onHttpError(ExceptionHandle.ResponseException exception) {
                            mView.dismissDialog();
                            mView.showToast("请求上传照片错误！");
                            Log.d(Constants.HTTP_TAG, "请求上传照片错误！");
                        }
                    });
        } else {
            recordDetails = videoRecordPresenter.getSelectedRecords();
            if (recordDetails.size() == 0) {
                mView.showToast("请选择上传项！");
                return;
            }
            mView.showDialog();
            List<String> ids = new ArrayList<>();
            List<String> fileNames = new ArrayList<>();
            ArrayMap<String, Object> params = new ArrayMap<>();
            UploadOnSubscribe uploadOnSubscribe = new UploadOnSubscribe();
            Observable.create(uploadOnSubscribe).subscribe();
            for (RecordDetail recordDetail : recordDetails) {
                recordDetail.setVideoUpload(true);
                fileNames.add(MyApplication.VIDEO_PATH + recordDetail.getVideoUrl());
                ids.add(recordDetail.getId());
            }
            params.put("ids", ids);
            params.put("filePathList", fileNames);
            params.put("UploadOnSubscribe", uploadOnSubscribe);
            Observable.merge(Observable.create(uploadOnSubscribe),
                    mModel.requestUploadVideos(account, params))
                    .compose(SchedulersCompat.applyToSchedulers())
                    .subscribe(new LoadCallBack<Serializable>() {
                        @Override protected void onProgress(String percent) {
                            setShowProgressPercent(percent); }
                        @Override public void onSuccess(Serializable serializable) {
                            if (serializable instanceof String) {
                                String s = (String) serializable;
                                if (s.equals(Constants.UPLOAD_SUCCESS)) {
                                    List<String> list = new ArrayList<>(ids);
                                    mModel.signVideosUpload(list);
                                    Log.d(Constants.HTTP_TAG, "请求上传视频成功！");
                                } else {
                                    Log.d(Constants.HTTP_TAG, "请求上传视频失败！"); }
                            } }
                        @Override public void onHttpError
                                (ExceptionHandle.ResponseException exception) {
                            mView.dismissDialog();
                            mView.showToast("请求上传视频错误！");
                            Log.d(Constants.HTTP_TAG, "请求上传视频错误！");
                        }});
        }
        if (mIsPhotosAlbum) {
            photosRecordPresenter.setClearSelected();
        } else {
            videoRecordPresenter.setClearSelected();
        }
        setHideSelectLayout();
        setShowSelect(false);
        setShowSelectedContent(0);
    }

    @Override
    public void setSelectOrUpload(String str) {
        if ("选择".equals(str)) {
            setShowSelectLayout();
            setShowSelect(true);
        } else {
            setUpload();
        }
    }

    @Override
    public void setShowSelectedContent(int size) {
        String content = "已选中" + size + "项";
        mView.showSelected(content);
    }

    @Override
    public void setPage(int pos) {
        mIsPhotosAlbum = pos == 0;
    }

    @Override
    public void setShowProgressPercent(String percent) {
        double p = Double.valueOf(percent);
        if (p == 100) {
            // 进度条满后，关闭进度条
            mView.dismissDialog();
            showProgressBar = true;
        } else {
            mView.setDialogPercent(percent);
        }
    }

    @Override
    public void start() {
        mModel = new AlbumModel();
    }

    @Override
    public void setInit() {
        RecordAlbumFragment photosRecordFrag = RecordAlbumFragment.newInstance();
        RecordAlbumFragment videoRecordFrag = RecordAlbumFragment.newInstance();
        photosRecordPresenter = new RAPresenter(mActivity, photosRecordFrag, true);
        videoRecordPresenter = new RAPresenter(mActivity, videoRecordFrag, false);
        photosRecordPresenter.start();
        videoRecordPresenter.start();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(photosRecordFrag);
        fragments.add(videoRecordFrag);
        mView.initWidget(fragments, mIsPhotosAlbum);
        mView.initListener();
    }
}
