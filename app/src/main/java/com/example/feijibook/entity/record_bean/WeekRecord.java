package com.example.feijibook.entity.record_bean;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 你是我的 on 2019/4/2
 */
public class WeekRecord extends RealmObject {
    @PrimaryKey
    private String id;
    /**
     * 年份 1998
     */
    private String year;
    /**
     * 第几周（2019-13）
     */
    private String week;
    /**
     * 周 各类别 消费总额
     */
    private RealmList<TotalExpend> mWeekTotalExpends;
    /**
     * 周 各类别 收入总额
     */
    private RealmList<TotalIncome> mWeekTotalIncomes;

    // 周总收入
    private String totalIncome;
    // 周总支出
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

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public RealmList<TotalExpend> getWeekTotalExpends() {
        return mWeekTotalExpends;
    }

    public void setWeekTotalExpends(RealmList<TotalExpend> weekTotalExpends) {
        mWeekTotalExpends = weekTotalExpends;
    }

    public RealmList<TotalIncome> getWeekTotalIncomes() {
        return mWeekTotalIncomes;
    }

    public void setWeekTotalIncomes(RealmList<TotalIncome> weekTotalIncomes) {
        mWeekTotalIncomes = weekTotalIncomes;
    }
}
