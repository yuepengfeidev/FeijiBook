package com.example.feijibook.activity.record_detail_act;

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

/**
 * RDModel
 *
 * @author yuepengfei
 * @date 2019/8/7
 * @description
 */
public class RDModel implements RDContract.Model {
    @Named("Scalars")
    @Inject
    HttpApi mHttpApi;

    RDModel() {
        MyApplication.getHttpComponent().injectRDModel(this);
    }

    @Override
    public RecordDetail getRecord(String id) {
        Map<String, RecordDetail> map = RecordListUtils.getAllRecordDetailsMap();
        return map.get(id);
    }

    @Override
    public void deleteRecord(RecordDetail recordDetail) {
        RecordListUtils.deleteRecord(recordDetail);

        RealmUtils.deleteRecord(recordDetail);
    }

    @Override
    public void saveRecord() {
        final RecordDetail recordDetail = RecordListUtils.getIsEditingRecord();
        RecordListUtils.addRecord(recordDetail);

        RealmUtils.saveRecord(recordDetail);
    }

    @Override
    public void deleteFile(String filePath) {
        FileUtils.deleteFile(filePath);
    }

    @Override
    public RecordDetail changeRecord() {
        RecordDetail resRecord = RecordListUtils.getResRecord();
        final RecordDetail desRecord = RecordListUtils.getIsEditingRecord();

        RecordListUtils.deleteRecord(resRecord);
        RecordListUtils.addRecord(desRecord);

        RealmUtils.changRecord();

        return desRecord;
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

    @Override
    public Observable<String> requestAddDetailRecord(String account, RecordDetail recordDetail) {
        return mHttpApi.addDetailRecord(recordDetail.getId(), account, recordDetail.getYear(),
                recordDetail.getMonth(), recordDetail.getDay(), recordDetail.getWeek(), recordDetail.getWeekOfYear(),
                recordDetail.getType(), recordDetail.getIconUrl(), recordDetail.getDetailType(),
                recordDetail.getMoney(), recordDetail.getRemark(),
                recordDetail.getOrder()).compose(SchedulersCompat.applyToSchedulers2());
    }

    @Override
    public Observable<String> requestDeleteRecord(String id, String account) {
        return mHttpApi.deleteDetailRecord(id, account).compose(SchedulersCompat.applyToSchedulers2());
    }
}
