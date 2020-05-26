package com.example.feijibook.activity.pie_chart_records_act;

import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.record_bean.WeekRecord;
import com.example.feijibook.entity.record_bean.YearRecord;
import com.example.feijibook.util.RecordListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PCRModel
 *
 * @author yuepengfei
 * @date 2019/6/30
 * @description
 */
public class PCRModel implements PCRContract.Model {
    @Override
    public Map<String, String> getStartAndEndDate(String type) {
        Map<String, String> map = new HashMap<>();
        switch (type) {
            case "week":
                Map<String, WeekRecord> weekRecordMap = RecordListUtils.getWeekRecordRankMap();
                List<Map.Entry<String, WeekRecord>> weekRecordList =
                        new ArrayList<>(weekRecordMap.entrySet());
                if (weekRecordList.size() == 1) {
                    WeekRecord seWeekRecord = weekRecordList.get(0).getValue();
                    String seWeekString = seWeekRecord.getWeek();
                    map.put("start", seWeekString);
                    map.put("end", seWeekString);
                } else if (weekRecordList.size() > 1) {
                    WeekRecord sWeekRecord = weekRecordList.get(0).getValue();
                    WeekRecord eWeekRecord = weekRecordList.get(weekRecordList.size() - 1).getValue();
                    String sWeekString = sWeekRecord.getWeek();
                    String eWeekString = eWeekRecord.getWeek();
                    map.put("start", sWeekString);
                    map.put("end", eWeekString);
                }
                break;
            case "month":
                Map<String, MonthRecord> monthRecordMap = RecordListUtils.getMonthRecordRankMap();
                List<Map.Entry<String, MonthRecord>> monthRecordList =
                        new ArrayList<>(monthRecordMap.entrySet());
                if (monthRecordList.size() == 1) {
                    MonthRecord sWeekRecord = monthRecordList.get(0).getValue();
                    map.put("start", sWeekRecord.getMonth());
                    map.put("end", sWeekRecord.getMonth());
                } else if (monthRecordList.size() > 1) {
                    MonthRecord sMonthRecord = monthRecordList.get(0).getValue();
                    MonthRecord eMonthRecord = monthRecordList.get(monthRecordList.size() - 1).getValue();
                    map.put("start", sMonthRecord.getMonth());
                    map.put("end", eMonthRecord.getMonth());
                }
                break;
            case "year":
                Map<String, YearRecord> yearRecordMap = RecordListUtils.getYearRecordRankMap();
                List<Map.Entry<String, YearRecord>> yearRecordList =
                        new ArrayList<>(yearRecordMap.entrySet());
                if (yearRecordList.size() == 1) {
                    YearRecord sYearRecord = yearRecordList.get(0).getValue();
                    map.put("start", sYearRecord.getYear());
                    map.put("end", sYearRecord.getYear());
                } else if (yearRecordList.size() > 1) {
                    YearRecord sYearRecord = yearRecordList.get(0).getValue();
                    YearRecord eYearRecord = yearRecordList.get(yearRecordList.size() - 1).getValue();
                    map.put("start", sYearRecord.getYear());
                    map.put("end", eYearRecord.getYear());
                }
                break;
            default:
                break;
        }
        return map;
    }

    @Override
    public Object getTotalRecord(String dateType, String dateKey) {
        switch (dateType) {
            case "week":
                return RecordListUtils.getWeekRecordRankMap().get(dateKey);
            case "month":
                return RecordListUtils.getMonthRecordRankMap().get(dateKey);
            case "year":
                return RecordListUtils.getYearRecordRankMap().get(dateKey);
            default:
                return null;
        }
    }
}
