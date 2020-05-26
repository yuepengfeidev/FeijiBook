package com.example.feijibook.entity.record_bean;

import io.realm.RealmObject;

/**
 * Created by 你是我的 on 2019/4/2
 */


public class TotalIncome  extends RealmObject {
    // 收入类别：礼金、工资、兼职
    private String type;
    // 该类别 收入 这周 收入总额
    private String totalIncome;
    // 该类别 的图标
    private int iconUrl;

    public int getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(int iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(String totalIncome) {
        this.totalIncome = totalIncome;
    }
}
