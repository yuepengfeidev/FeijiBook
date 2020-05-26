package com.example.feijibook.entity.record_bean;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 你是我的 on 2019/4/2
 */
public class DayRecord extends RealmObject {
    @PrimaryKey
    private String id;
    // 年 ：1998
    private String year;
    // 月: 10
    private String month;
    // 日: 13
    private String day;
    // 星期几 （星期一)
    private String week;
    // 每天总支出
    private String dayTotalExpend;
    // 每天总收入
    private String dayTotalIncome;

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

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDayTotalExpend() {
        return dayTotalExpend;
    }

    public void setDayTotalExpend(String dayTotalExpend) {
        this.dayTotalExpend = dayTotalExpend;
    }

    public String getDayTotalIncome() {
        return dayTotalIncome;
    }

    public void setDayTotalIncome(String dayTotalIncome) {
        this.dayTotalIncome = dayTotalIncome;
    }

}
