package com.example.feijibook.activity.search_record_act;

import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.util.RecordListUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by 你是我的 on 2019/3/28
 */
public class SRModel implements SRContract.Model {

    @Override
    public Map<String, List<RecordDetail>> getAllTypesRecords() {
        return RecordListUtils.getOneTypeAllRecordsMap();
    }

    @Override
    public void deleteRecordDetail(RecordDetail recordDetail) {
        RecordListUtils.deleteRecord(recordDetail);
    }
}
