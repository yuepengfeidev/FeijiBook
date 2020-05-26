package com.example.feijibook.activity.add_record_from_calendar_icon_act;

import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.http.HttpApi;
import com.example.feijibook.http.SchedulersCompat;
import com.example.feijibook.util.FileUtils;
import com.example.feijibook.util.RealmUtils;
import com.example.feijibook.util.RecordListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;

/**
 * Created by 你是我的 on 2019/3/20
 */
public class ARFCModel implements ARFCContract.Model {
    @Named("Scalars")
    @Inject
    HttpApi mHttpApi;


    public ARFCModel() {
        MyApplication.getHttpComponent().injectARFCModel(this);
    }

    @Override
    public List<DayRecord> getDayRecord(String year, String month, String day) {
        List<DayRecord> list = new ArrayList<>();
        String date = year + "-" + month;
        Map<String, List<DayRecord>> map = RecordListUtils.getDayRecordsInMonthMap();
        if (map.containsKey(date)) {
            for (DayRecord record : Objects.requireNonNull(map.get(date))) {
                if (record.getDay().equals(day)) {
                    list.add(record);
                    break;
                }
            }
        }
        return list;
    }

    @Override
    public List<RecordDetail> getRecordDetails(String year, String month, String day) {
        List<RecordDetail> list = new ArrayList<>();
        String date = year + "-" + month;
        Map<String, List<RecordDetail>> map = RecordListUtils.getRecordDetailsInMonthMap();
        if (map.containsKey(date)) {
            for (RecordDetail record : Objects.requireNonNull(map.get(date))) {
                if (record.getDay().equals(day)) {
                    list.add(record);
                }
            }
        }
        return list;
    }

    @Override
    public void deleteRecordDetail(RecordDetail recordDetail) {
        RecordListUtils.deleteRecord(recordDetail);

        RealmUtils.deleteRecord(recordDetail);
    }

    @Override
    public void deleteFile(String filePath) {
        FileUtils.deleteFile(filePath);
    }

    @Override
    public Observable<String> requestDeleteRecord(String id, String account) {
        return mHttpApi.deleteDetailRecord(id, account).compose(SchedulersCompat.applyToSchedulers2());
    }
}
