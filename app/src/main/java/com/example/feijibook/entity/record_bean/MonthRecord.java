package com.example.feijibook.entity.record_bean;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 你是我的 on 2019/4/2
 */
public class MonthRecord extends RealmObject {
    @PrimaryKey
    private String id;
    // 年份(1998)
    private String year;
    // 月份（1998-10)
    private String month;
    // 月 各类别 消费总额
    private RealmList<TotalExpend> mMonthTotalExpends;
    // 月 各类别 收入总额
    private RealmList<TotalIncome> mMonthTotalIncomes;
    // 月总收入
    private String totalIncome;
    // 月总支出
    private String totalExpend;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public RealmList<TotalExpend> getMonthTotalExpends() {
        return mMonthTotalExpends;
    }

    public void setMonthTotalExpends(RealmList<TotalExpend> monthTotalExpends) {
        mMonthTotalExpends = monthTotalExpends;
    }

    public RealmList<TotalIncome> getMonthTotalIncomes() {
        return mMonthTotalIncomes;
    }

    public void setMonthTotalIncomes(RealmList<TotalIncome> monthTotalIncomes) {
        mMonthTotalIncomes = monthTotalIncomes;
    }
}
