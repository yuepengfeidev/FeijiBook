package com.example.feijibook.entity.weather_bean;

import io.realm.RealmObject;

/**
 * FutureBean
 *
 * @author PengFei Yue
 * @date 2019/11/1
 * @description
 */
public  class FutureBean extends RealmObject {
    /**
     * date : 2019-10-30
     * temperature : 12/23℃
     * weather : 晴
     * wid : {"day":"00","night":"00"}
     * direct : 东南风
     */

    private String date;
    private String temperature;
    private String weather;
    private WidBean wid;
    private String direct;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public WidBean getWid() {
        return wid;
    }

    public void setWid(WidBean wid) {
        this.wid = wid;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }


}