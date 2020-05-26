package com.example.feijibook.entity.weather_bean;

import io.realm.RealmObject;

/**
 * WidBean
 *
 * @author PengFei Yue
 * @date 2019/11/1
 * @description
 */
public class WidBean extends RealmObject {
    /**
     * day : 00
     * night : 00
     */

    private String day;
    private String night;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getNight() {
        return night;
    }

    public void setNight(String night) {
        this.night = night;
    }
}