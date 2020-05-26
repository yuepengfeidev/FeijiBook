package com.example.feijibook.entity.record_bean;

import io.realm.RealmObject;

/**
 * Created by 你是我的 on 2019/4/2
 */
public class TotalExpend extends RealmObject {
    /**
     * 消费类别： 饮食、娱乐
     */
    private String type;
    /**
     * 该类别 消费 这周 消费总额
     */
    private String totalExpend;
    /**
     * 该类别的图标
     */
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

    public String getTotalExpend() {
        return totalExpend;
    }

    public void setTotalEpend(String totalExpend) {
        this.totalExpend = totalExpend;
    }
}
