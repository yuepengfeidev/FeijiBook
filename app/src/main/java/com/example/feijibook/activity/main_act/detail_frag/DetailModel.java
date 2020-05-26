package com.example.feijibook.activity.main_act.detail_frag;

import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.http.HttpApi;
import com.example.feijibook.http.SchedulersCompat;
import com.example.feijibook.util.FileUtils;
import com.example.feijibook.util.RealmUtils;
import com.example.feijibook.util.RecordListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;

/**
 * Created by 你是我的 on 2019/3/11
 */
public class DetailModel implements DetailContract.Model {
    @Named("Scalars")
    @Inject
    HttpApi mHttpApi;

    DetailModel() {
        MyApplication.getHttpComponent().injectDetailModel(this);
    }

    @Override
    public void deleteRecordDetail(RecordDetail recordDetail) {
        RecordListUtils.deleteRecord(recordDetail);

        RealmUtils.deleteRecord(recordDetail);
    }

    @Override
    public Map<String, String> getStartAndEndMonth() {
        Map<String, String> map = new HashMap<>(2);
        Map<String, MonthRecord> monthRecordMap = RecordListUtils.getMonthRecordRankMap();
        List<Map.Entry<String, MonthRecord>> monthRecordList =
                new ArrayList<>(monthRecordMap.entrySet());
        if (monthRecordList.size() == 1) {
            MonthRecord sMonthRecord = monthRecordList.get(0).getValue();
            map.put("start", sMonthRecord.getMonth());
            map.put("end", sMonthRecord.getMonth());
        } else if (monthRecordList.size() > 1) {
            MonthRecord sMonthRecord = monthRecordList.get(0).getValue();
            MonthRecord eMonthRecord = monthRecordList.get(monthRecordList.size() - 1).getValue();
            map.put("start", sMonthRecord.getMonth());
            map.put("end", eMonthRecord.getMonth());
        }
        return map;
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
