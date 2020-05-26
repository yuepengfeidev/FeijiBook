package com.example.feijibook.activity.bill_act;

import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.record_bean.YearRecord;
import com.example.feijibook.util.RecordListUtils;

import java.util.Map;

/**
 * BillModel
 *
 * @author yuepengfei
 * @date 2019/7/23
 * @description
 */
public class BillModel implements BillContract.Model {

    @Override
    public MonthRecord getMonthTotalRecord(String moy) {
        Map<String, MonthRecord> monthRecordMap = RecordListUtils.getMonthRecordRankMap();
        if (monthRecordMap.containsKey(moy)) {
            return monthRecordMap.get(moy);
        }
        return null;
    }

    @Override
    public YearRecord getYearTotalRecord(String year) {
        Map<String, YearRecord> yearRecordMap = RecordListUtils.getYearRecordRankMap();
        if (yearRecordMap.containsKey(year)) {
            return yearRecordMap.get(year);
        }
        return null;
    }
}
