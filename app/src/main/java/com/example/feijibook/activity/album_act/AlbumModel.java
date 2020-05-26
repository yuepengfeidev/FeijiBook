package com.example.feijibook.activity.album_act;

import android.util.ArrayMap;

import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.http.HttpApi;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.realm.Realm;

/**
 * AlbumModel
 *
 * @author PengFei Yue
 * @date 2019/10/3
 * @description
 */
public class AlbumModel implements AlbumContract.Model {
    @Named("File")
    @Inject
    HttpApi mHttpApi2;

    AlbumModel() {
        MyApplication.getHttpComponent().injectAlbumModel(this);
    }

    @Override
    public Observable<String> requestUploadPhotos(String account, ArrayMap<String, Object> params) {
        return mHttpApi2.uploadRecordPhotos(account, params);
    }

    @Override
    public Observable<String> requestUploadVideos(String account, ArrayMap<String, Object> params) {
        return mHttpApi2.uploadRecordVideos(account, params);
    }

    @Override
    public void signVideosUpload(List<String> list) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (String id : list) {
            RecordDetail recordDetail = realm.where(RecordDetail.class).equalTo("id", id).findFirst();
            recordDetail.setVideoUpload(true);
        }
        realm.commitTransaction();
    }

    @Override
    public void signPhotosUpload(List<String> list) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (String id : list) {
            RecordDetail recordDetail = realm.where(RecordDetail.class).equalTo("id", id).findFirst();
            recordDetail.setImgUpload(true);
        }
        realm.commitTransaction();
    }
}
