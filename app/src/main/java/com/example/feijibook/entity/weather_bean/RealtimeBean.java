package com.example.feijibook.entity.weather_bean;

import io.realm.RealmObject;

/**
 * RealtimeBean
 *
 * @author PengFei Yue
 * @date 2019/11/1
 * @description
 */
public  class RealtimeBean extends RealmObject {
    /**
     * temperature : 22
     * humidity : 35
     * info : 晴
     * wid : 00
     * direct : 南风
     * power : 2级
     * aqi : 138
     */

    private String temperature;
    private String humidity;
    private String info;
    private String wid;
    private String direct;
    private String power;
    private String aqi;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }
}