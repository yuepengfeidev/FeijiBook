package com.example.feijibook.activity.camera_act;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.http.HttpApi;
import com.example.feijibook.http.SchedulersCompat;
import com.example.feijibook.util.FileUtils;
import com.example.feijibook.util.RealmUtils;
import com.example.feijibook.util.RecordListUtils;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.realm.Realm;

/**
 * CameraModel
 *
 * @author PengFei Yue
 * @date 2019/9/14
 * @description
 */
public class CameraModel implements CameraContract.Model {
    @Named("Scalars")
    @Inject
    HttpApi mHttpApi;

    public CameraModel() {
        MyApplication.getHttpComponent().injectCameraModel(this);
    }

    @Override
    public void saveFile(String tempPath, String savePath) {
        FileUtils.copyToFile(tempPath, savePath);
    }

    @Override
    public Bitmap getTempPhoto(String tempPhotoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        // 值越大，图像越不清晰，但读取资源会更快
        options.inSampleSize = 1;

        return BitmapFactory.decodeFile(tempPhotoPath, options);
    }

    @Override
    public void changeRecord() {
        RecordDetail resRecord = RecordListUtils.getResRecord();
        final RecordDetail desRecord = RecordListUtils.getIsEditingRecord();

        RecordListUtils.deleteRecord(resRecord);
         RecordListUtils.addRecord(desRecord);
        RealmUtils.changRecord();
    }

    @Override
    public RecordDetail getRecord(String id) {
        Map<String, RecordDetail> map = RecordListUtils.getAllRecordDetailsMap();
        return map.get(id);
    }

    @Override
    public Observable<String> requestChangeDetailRecord(String account, String resId, RecordDetail recordDetail) {
        return mHttpApi.changeDetailRecord(resId, recordDetail.getId(), account,
                recordDetail.getYear(), recordDetail.getMonth(), recordDetail.getDay(),
                recordDetail.getWeek(), recordDetail.getWeekOfYear(), recordDetail.getType(),
                recordDetail.getIconUrl(), recordDetail.getDetailType(),
                recordDetail.getMoney(), recordDetail.getRemark(), recordDetail.getOrder())
                .compose(SchedulersCompat.applyToSchedulers2());
    }

}
