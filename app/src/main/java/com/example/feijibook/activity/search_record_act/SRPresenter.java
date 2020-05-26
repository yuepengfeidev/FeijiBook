package com.example.feijibook.activity.search_record_act;

import android.app.Activity;
import android.content.Intent;

import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.util.ComparatorUtils;
import com.example.feijibook.util.RecordListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by 你是我的 on 2019/3/28
 */
public class SRPresenter implements SRContract.Presenter {
    private SRContract.View mView;
    private SRContract.Model mModel;
    private boolean isNeedUpdate = false;

    SRPresenter(Activity activity, SRContract.View view) {
        mView = view;
        mView.getAct(activity);
        mView.setPresenter(this);
    }

    @Override
    public void setInit() {
        mView.initWidget();
        mView.initListener();
    }

    @Override
    public void checkUpdateRecord() {
        if (isNeedUpdate) {
            mView.updateRecord();
            isNeedUpdate = false;
        }
    }

    @Override
    public void setFinishAct() {
        mView.finishAct();
    }

    @Override
    public void setStartActivity(Intent intent) {
        mView.closeMenu();// 打开活动之前关闭 打开着的删除菜单栏
        mView.startAct(intent);
    }

    @Override
    public void setSearchRecords(String detailType) {
        Map<String, List<RecordDetail>> map = mModel.getAllTypesRecords();
        List<RecordDetail> recordDetails;
        List<DayRecord> dayRecords;
        // haseSet去重，去除id相同的记录
        HashSet<RecordDetail> hashSet = new HashSet<>();
        // 遍历找出包含该字符的类型键
        for (String key : map.keySet()) {
            if (key.contains(detailType)) {
                List<RecordDetail> list = map.get(key);
                if (list != null && list.size() != 0) {
                    hashSet.addAll(list);
                }
            }
        }

        recordDetails = new ArrayList<>(hashSet);
        dayRecords = RecordListUtils.getSearchedDayRecords(recordDetails);

        mView.setList(recordDetails, dayRecords);
    }


    @Override
    public void setDeleteRecord(int position) {
        mView.closeMenu();// 在删除之前需要关闭删除菜单按钮
        mView.deleteRecord(position);
    }

    @Override
    public void setDeleteRecordDetail(RecordDetail recordDetail) {
        mModel.deleteRecordDetail(recordDetail);
    }

    @Override
    public void setClearRV() {
        mView.clearRV();
    }

    @Override
    public void start() {
        mModel = new SRModel();
    }

    @Override
    public void isNeedUpdateRecord(boolean needUpdate) {
        isNeedUpdate = needUpdate;
    }
}
