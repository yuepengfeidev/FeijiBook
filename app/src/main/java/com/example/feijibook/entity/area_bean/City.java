package com.example.feijibook.entity.area_bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * City
 *
 * @author PengFei Yue
 * @date 2019/10/30
 * @description
 */
public class City extends RealmObject {

    private int id;

    private String cityName;
    @PrimaryKey
    private int cityCode;

    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
