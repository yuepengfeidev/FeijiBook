package com.example.feijibook.entity.record_type_bean;

import com.example.feijibook.entity.record_type_bean.AddtiveType;
import com.example.feijibook.entity.record_type_bean.OptionalType;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 你是我的 on 2019/3/29
 */

/**
 *  记录类别类型(餐饮、娱乐) 可选择和可添加 的 编辑存储实体类
 */
public class CategoryType extends RealmObject {
    // 类别（收入或支出）
    @PrimaryKey
    private String type;
    // 可选择类型 列表
    private RealmList<OptionalType> mOptionalTypeRealmList;
    // 可添加类型 列表
    private RealmList<AddtiveType> mAddtiveTypeRealmList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RealmList<OptionalType> getOptionalTypeRealmList() {
        return mOptionalTypeRealmList;
    }

    public void setOptionalTypeRealmList(RealmList<OptionalType> optionalTypeRealmList) {
        mOptionalTypeRealmList = optionalTypeRealmList;
    }

    public RealmList<AddtiveType> getAddtiveTypeRealmList() {
        return mAddtiveTypeRealmList;
    }

    public void setAddtiveTypeRealmList(RealmList<AddtiveType> addtiveTypeRealmList) {
        mAddtiveTypeRealmList = addtiveTypeRealmList;
    }
}
