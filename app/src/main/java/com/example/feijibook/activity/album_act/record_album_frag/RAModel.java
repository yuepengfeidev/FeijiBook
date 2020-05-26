package com.example.feijibook.activity.album_act.record_album_frag;

import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.util.RecordListUtils;

import java.util.List;
import java.util.Map;

/**
 * RAModel
 *
 * @author PengFei Yue
 * @date 2019/10/4
 * @description
 */
public class RAModel implements RAContract.Model {
    @Override
    public List<Map.Entry<String, RecordDetail>> getHavePhotosRecords() {
        return RecordListUtils.getHavePhotosRecordList();
    }

    @Override
    public List<Map.Entry<String, RecordDetail>> getHaveVideoRecords() {
        return RecordListUtils.getHaveVideoRecordList();
    }
}
