package com.example.feijibook.activity.one_type_records_line_chart_act;

import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.util.RecordListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OTRLCModel
 *
 * @author yuepengfei
 * @date 2019/6/13
 * @description
 */
public class OTRLCModel implements OTRLCContract.Model {
    @Override
    public Map<String, String> getStartAndEndDate(String detailType, String dateType) {
        Map<String, String> map = new HashMap<>();
        Map<String, String> dateMap = RecordListUtils.getOneTypeTotalRecordInDateMap()
                .get(detailType).get(dateType);
        // 对应日期类型的 所有记录日期列表，按时间升序排序，最早 到 最晚
        List<String> dateList = new ArrayList<>(dateMap.keySet());
        if (dateList.size() == 1) {
            String totalMoney = dateList.get(0);
            map.put("start", totalMoney);
            map.put("end", totalMoney);
        } else {
            map.put("start", dateList.get(0));
            map.put("end", dateList.get(dateList.size() - 1));
        }
        return map;
    }

    @Override
    public List<RecordDetail> getRecord(String detailType, String dateType, String dateKey) {
        Map<String,Map<String,Map<String,List<RecordDetail>>>> map = RecordListUtils.getOneTypeAllRecordsInDateMap();
        Map<String, Map<String, List<RecordDetail>>> dateTypeMap = map.get(detailType);
        Map<String, List<RecordDetail>> dateMap = dateTypeMap.get(dateType);
        return dateMap.get(dateKey);
    }

    @Override
    public Float getTotalMoney(String detailType, String dateType, String dateKey) {
        Map<String,Map<String,Map<String,String>>> map = RecordListUtils.getOneTypeTotalRecordInDateMap();
        Map<String, Map<String, String>> dateTypeMap = map.get(detailType);
        Map<String, String> dateMap = dateTypeMap.get(dateType);
        if (dateMap.keySet().contains(dateKey)) {
            return Float.valueOf(dateMap.get(dateKey));
        } else {
            return 0f;
        }
    }
}
