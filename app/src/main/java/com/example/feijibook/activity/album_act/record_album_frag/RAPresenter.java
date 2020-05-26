package com.example.feijibook.activity.album_act.record_album_frag;

import android.app.Activity;
import android.content.Entity;
import android.content.Intent;

import com.example.feijibook.entity.record_bean.RecordDetail;

import java.util.List;
import java.util.Map;

/**
 * RAPresenter
 *
 * @author PengFei Yue
 * @date 2019/10/4
 * @description
 */
public class RAPresenter implements RAContract.Presenter {
    private RAContract.Model mModel;
    private RAContract.View mView;
    private boolean mIsPhotosAlbum;

    public RAPresenter(Activity activity, RAContract.View view, boolean isPhotosAlbum) {
        mView = view;
        mIsPhotosAlbum = isPhotosAlbum;
        mView.setPresenter(this);
        mView.getAct(activity);
    }

    @Override
    public void setStartActivity(Intent intent) {
        mView.startAct(intent);
    }

    @Override
    public void setPhotosRecords() {
        List<Map.Entry<String, RecordDetail>> list = mModel.getHavePhotosRecords();
        mView.showRecords(list);
        if (list == null || list.size() == 0) {
            mView.showOrHideNoDataSign(true);
        }
    }

    @Override
    public void setVideoRecords() {
        List<Map.Entry<String, RecordDetail>> list = mModel.getHaveVideoRecords();
        mView.showRecords(list);
        if (list == null || list.size() == 0) {
            mView.showOrHideNoDataSign(true);
        }
    }

    @Override
    public void setShowSelect(boolean isShow) {
        mView.showSelect(isShow);
    }

    @Override
    public void setClearSelected() {
        mView.clearSelect();
    }

    @Override
    public List<RecordDetail> getSelectedRecords() {
        return mView.getSelectedRecords();
    }

    @Override
    public void start() {
        mModel = new RAModel();
    }

    @Override
    public void setInit() {
        mView.initRV(mIsPhotosAlbum);
        if (mIsPhotosAlbum) {
            setPhotosRecords();
        } else {
            setVideoRecords();
        }
    }
}
