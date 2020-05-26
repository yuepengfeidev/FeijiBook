package com.example.feijibook.activity.category_setting_act.category_edit_frag;

import com.example.feijibook.app.Constants;
import com.example.feijibook.entity.record_type_bean.AddtiveType;
import com.example.feijibook.entity.record_type_bean.OptionalType;
import com.example.feijibook.util.SetTypeListUtil;

import java.util.List;

/**
 * CEModel
 *
 * @author PengFei Yue
 * @date 2019/10/12
 * @description
 */
public class CEModel implements CEContract.Model {

    @Override
    public void saveTypeList(String type, List<OptionalType> optionalTypes, List<AddtiveType> additiveTypes) {
        if (type.equals(Constants.INCOME)) {
            SetTypeListUtil.setTypeList("收入", optionalTypes, additiveTypes);
        } else {
            SetTypeListUtil.setTypeList("支出", optionalTypes, additiveTypes);
        }
    }

    @Override
    public List<OptionalType> getExpendOptionalTypeList() {
        return SetTypeListUtil.getOptionalTypeListInDB("支出");
    }

    @Override
    public List<AddtiveType> getExpendAdditiveTypeList() {
        return SetTypeListUtil.getAddibleTypeLstInDB("支出");
    }

    @Override
    public List<OptionalType> getIncomeOptionalTypeList() {
        return SetTypeListUtil.getOptionalTypeListInDB("收入");
    }

    @Override
    public List<AddtiveType> getIncomeAdditiveTypeList() {
        return SetTypeListUtil.getAddibleTypeLstInDB("收入");
    }
}
