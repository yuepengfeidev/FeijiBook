    package com.example.feijibook.entity.record_bean;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 你是我的 on 2019/4/2
 */
public class YearRecord extends RealmObject {
    @PrimaryKey
    private String id;
    // 年 1998
    private String year;
    // 年 各类别 消费总额
    private RealmList<TotalExpend> mYearTotalExpends;
    // 年 各类别 收入总额
    private RealmList<TotalIncome> mYearTotalIncomes;

    // 年总收总收入
    private String totalIncome;
    // 年总支出
    private String totalExpend;

    public String getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(String totalIncome) {
        this.totalIncome = totalIncome;
    }

    public String getTotalExpend() {
        return totalExpend;
    }

    public void setTotalExpend(String totalExpend) {
        this.totalExpend = totalExpend;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public RealmList<TotalExpend> getYearTotalExpends() {
        return mYearTotalExpends;
    }

    public void setYearTotalExpends(RealmList<TotalExpend> yearTotalExpends) {
        mYearTotalExpends = yearTotalExpends;
    }

    public RealmList<TotalIncome> getYearTotalIncomes() {
        return mYearTotalIncomes;
    }

    public void setYearTotalIncomes(RealmList<TotalIncome> yearTotalIncomes) {
        mYearTotalIncomes = yearTotalIncomes;
    }
}
