package com.example.feijibook.entity.area_bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Province
 *
 * @author PengFei Yue
 * @date 2019/10/30
 * @description
 */
public class Province extends RealmObject {

    private int id;

    private String provinceName;
    @PrimaryKey
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
