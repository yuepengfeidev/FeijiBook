package com.example.feijibook.entity.weather_bean;

/**
 * NearlyFiveDayStateBean
 *
 * @author PengFei Yue
 * @date 2019/10/31
 * @description 天气界面，近五天状态的实体类
 */
public class NearlyFiveDayStateBean {
    /**
     * 今天或星期几
     */
    String week;
    /**
     * 10月13日
     */
    String date;
    String weather;

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
}
