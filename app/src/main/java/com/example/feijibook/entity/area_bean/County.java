package com.example.feijibook.entity.area_bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * County
 *
 * @author PengFei Yue
 * @date 2019/10/30
 * @description
 */
public class County extends RealmObject {

    private int id;
    @PrimaryKey
    private String countyName;

    private String weatherId;

    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
