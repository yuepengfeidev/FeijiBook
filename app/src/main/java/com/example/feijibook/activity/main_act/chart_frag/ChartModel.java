package com.example.feijibook.activity.main_act.chart_frag;

import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_bean.WeekRecord;
import com.example.feijibook.entity.record_bean.YearRecord;
import com.example.feijibook.util.RecordListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 你是我的 on 2019/3/11
 */
public class ChartModel implements ChartContract.Model {
    @Override
    public Map<String, String> getStartAndEndDate(String type) {
        Map<String, String> map = new HashMap<>(2);
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
                    String sWeekString =  sWeekRecord.getWeek();
                    String eWeekString =  eWeekRecord.getWeek();
                    map.put("start", sWeekString);
                    map.put("end", eWeekString);
                }
                break;
            case "month":
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
    public List<RecordDetail> getRecord(String expendOrIncome, String dateType, String dateKey) {
        List<RecordDetail> list = null;
        switch (dateType) {
            case "week":
            case "month":
                if (RecordListUtils.getAllRecordsInDayMap().containsKey(dateKey)) {
                    list = RecordListUtils.getAllRecordsInDayMap().get(dateKey).get(expendOrIncome);
                }
                return list;
            case "year":
                if (RecordListUtils.getAllRecordsInMonthMap().containsKey(dateKey)) {
                    list = RecordListUtils.getAllRecordsInMonthMap().get(dateKey).get(expendOrIncome);
                }
                return list;
            default:
                return list;

        }
    }


    @Override
    public Float getTotalMoney(String dateType, String expendOrIncome, String dateKey) {
        switch (dateType) {
            case "day":
                DayRecord dayRecord = RecordListUtils.getDayRecordsInDayMap().get(dateKey);
                if (dayRecord == null) {
                    return null;
                } else {
                    if (expendOrIncome.equals("expend") && dayRecord.getDayTotalExpend() != null) {
                        return Float.valueOf(dayRecord.getDayTotalExpend());
                    } else if (expendOrIncome.equals("income") && dayRecord.getDayTotalIncome() != null) {
                        return Float.valueOf(dayRecord.getDayTotalIncome());
                    } else {
                        return 0f;
                    }
                }
            case "week":
                WeekRecord weekRecord = RecordListUtils.getWeekRecordRankMap().get(dateKey);
                if (weekRecord == null) {
                    return 0f;
                } else {
                    if (expendOrIncome.equals("expend") && weekRecord.getTotalExpend() != null) {
                        return Float.valueOf(weekRecord.getTotalExpend());
                    } else if (weekRecord.getTotalIncome() != null) {
                        return Float.valueOf(weekRecord.getTotalIncome());
                    } else {
                        return 0f;
                    }
                }
            case "month":
                MonthRecord monthRecord = RecordListUtils.getMonthRecordRankMap().get(dateKey);
                if (monthRecord == null) {
                    return 0f;
                } else {
                    if (expendOrIncome.equals("expend") && monthRecord.getTotalExpend() != null) {
                        return Float.valueOf(monthRecord.getTotalExpend());
                    } else if (monthRecord.getTotalIncome() != null) {
                        return Float.valueOf(monthRecord.getTotalIncome());
                    } else {
                        return 0f;
                    }
                }
            case "year":
                YearRecord yearRecord = RecordListUtils.getYearRecordRankMap().get(dateKey);
                if (yearRecord == null) {
                    return 0f;
                } else {
                    if (expendOrIncome.equals("expend") && yearRecord.getTotalExpend() != null) {
                        return Float.valueOf(yearRecord.getTotalExpend());
                    } else if (yearRecord.getTotalIncome() != null) {
                        return Float.valueOf(yearRecord.getTotalIncome());
                    } else {
                        return 0f;
                    }
                }
            default:
                return null;
        }
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
