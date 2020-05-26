package com.example.feijibook.entity.record_type_bean;

import io.realm.RealmObject;

/**
 * Created by 你是我的 on 2019/3/21
 */

// 添加记录时可选的类型
public class OptionalType extends RealmObject {
    private String incomeOrExpend_O;// 是收入还是支出
    private String typeName_O;// 类型名称
    private int typeIconUrl_O;// 类型图表地址
    private String defaultOrCustom_O;// 默认类型或是自定义
    // 自定义类型 名称，默认类型没有自定义名称，为null
    private String customTypeName;

    public String getCustomTypeName() {
        return customTypeName;
    }

    public void setCustomTypeName(String customTypeName) {
        this.customTypeName = customTypeName;
    }

    public String getDefaultOrCustom_O() {
        return defaultOrCustom_O;
    }

    public void setDefaultOrCustom_O(String defaultOrCustom_O) {
        this.defaultOrCustom_O = defaultOrCustom_O;
    }

    public String getIncomeOrExpend_O() {
        return incomeOrExpend_O;
    }

    public void setIncomeOrExpend_O(String incomeOrExpend_O) {
        this.incomeOrExpend_O = incomeOrExpend_O;
    }

    public String getTypeName_O() {
        return typeName_O;
    }

    public void setTypeName_O(String typeName_O) {
        this.typeName_O = typeName_O;
    }

    public int getTypeIconUrl_O() {
        return typeIconUrl_O;
    }

    public void setTypeIconUrl_O(int typeIconUrl_O) {
        this.typeIconUrl_O = typeIconUrl_O;
    }
}
