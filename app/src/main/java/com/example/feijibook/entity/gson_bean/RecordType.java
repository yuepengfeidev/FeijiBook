package com.example.feijibook.entity.gson_bean;

/**
 * RecordType
 *
 * @author PengFei Yue
 * @date 2019/11/6
 * @description 添加账单记录时，可选的记录类型
 */
public class RecordType {
    /**
     * OptionalOrAdditive, "optional" 和 "additive"
     * 可选择 和 可添加 类型
     */
    String ooa;
    /**
     * IncomeOrExpend，收入 还是 支出
     */
    String ioe;
    /**
     * 记录类型，如 娱乐、餐饮
     */
    String detail_type;
    /**
     * 记录类型的图标路径
     */
    int icon_url;
    /**
     * 自定义名称
     */
    String custom_type_name;

    /**
     * default Or Custom 两种类型
     */
    String doc;

    public RecordType(String ooa, String ioe, String detail_type, int icon_url, String custom_type_name, String doc) {
        this.ooa = ooa;
        this.ioe = ioe;
        this.detail_type = detail_type;
        this.icon_url = icon_url;
        this.custom_type_name = custom_type_name;
        this.doc = doc;
    }

    public String getCustom_type_name() {
        return custom_type_name;
    }

    public void setCustom_type_name(String custom_type_name) {
        this.custom_type_name = custom_type_name;
    }

    public String getDoc() {
        return doc;
    }

    public String getOoa() {
        return ooa;
    }

    public void setOoa(String ooa) {
        this.ooa = ooa;
    }

    public String getIoe() {
        return ioe;
    }

    public void setIoe(String ioe) {
        this.ioe = ioe;
    }

    public String getDetail_type() {
        return detail_type;
    }

    public void setDetail_type(String detail_type) {
        this.detail_type = detail_type;
    }

    public int getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(int icon_url) {
        this.icon_url = icon_url;
    }
}
