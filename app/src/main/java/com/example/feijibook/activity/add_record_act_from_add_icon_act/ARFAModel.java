package com.example.feijibook.activity.add_record_act_from_add_icon_act;

import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.http.HttpApi;
import com.example.feijibook.http.SchedulersCompat;
import com.example.feijibook.util.RealmUtils;
import com.example.feijibook.util.RecordListUtils;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import okhttp3.RequestBody;

/**
 * Created by 你是我的 on 2019/3/20
 */
public class ARFAModel implements ARFAContract.Model {
    @Named("Scalars")
    @Inject
    HttpApi mHttpApi;

    ARFAModel() {
        MyApplication.getHttpComponent().injectAddRecordFromAddIcon(this);
    }

    @Override
    public void saveRecord() {
        final RecordDetail recordDetail = RecordListUtils.getIsEditingRecord();
        // 添加记录
        RecordListUtils.addRecord(recordDetail);

        RealmUtils.saveRecord(recordDetail);
    }

    @Override
    public RecordDetail getRecord(String id) {
        Map<String, RecordDetail> map = RecordListUtils.getAllRecordDetailsMap();
        return map.get(id);
    }

    @Override
    public RecordDetail changeRecord() {
        final RecordDetail resRecord = RecordListUtils.getResRecord();
        final RecordDetail desRecord = RecordListUtils.getIsEditingRecord();

        // 删除内存类中的记录
        RecordListUtils.deleteRecord(resRecord);
        // 添加记录到内存类中
        RecordListUtils.addRecord(desRecord);

        RealmUtils.changRecord();
        return desRecord;
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
    public Observable<String> requestChangeDetailRecord(String account, String resId, RecordDetail recordDetail) {
        return mHttpApi.changeDetailRecord(resId, recordDetail.getId(), account,
                recordDetail.getYear(), recordDetail.getMonth(), recordDetail.getDay(),
                recordDetail.getWeek(), recordDetail.getWeekOfYear(), recordDetail.getType(),
                recordDetail.getIconUrl(), recordDetail.getDetailType(),
                recordDetail.getMoney(), recordDetail.getRemark(), recordDetail.getOrder())
                .compose(SchedulersCompat.applyToSchedulers2());
    }

    @Override
    public Observable<String> requestUploadTypeSetting(RequestBody body) {
        return mHttpApi.upLoadTypeSetting(body).compose(SchedulersCompat.applyToSchedulers2());
    }
}
