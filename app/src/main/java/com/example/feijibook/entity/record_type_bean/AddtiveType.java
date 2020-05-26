package com.example.feijibook.entity.record_type_bean;

import io.realm.RealmObject;

/**
 * Created by 你是我的 on 2019/3/21
 */

// 可添加到可选的类型
public class AddtiveType extends RealmObject {
    private String incomeOrExpend_A;// 收入或支出
    private String typeName_A;// 可添加的类型名
    private int typeIconUrl_A;// 可添加类型的图标

    public String getIncomeOrExpend_A() {
        return incomeOrExpend_A;
    }

    public void setIncomeOrExpend_A(String incomeOrExpend_A) {
        this.incomeOrExpend_A = incomeOrExpend_A;
    }

    public String getTypeName_A() {
        return typeName_A;
    }

    public void setTypeName_A(String typeName_A) {
        this.typeName_A = typeName_A;
    }

    public int getTypeIconUrl_A() {
        return typeIconUrl_A;
    }

    public void setTypeIconUrl_A(int typeIconUrl_A) {
        this.typeIconUrl_A = typeIconUrl_A;
    }
}
