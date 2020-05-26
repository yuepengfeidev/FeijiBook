package com.example.feijibook.activity.add_custom_category_act;

import com.example.feijibook.entity.record_type_bean.OptionalType;
import com.example.feijibook.util.SetTypeListUtil;

/**
 * Created by 你是我的 on 2019/4/1
 */
public class ACCModel implements ACCContract.Model {
    @Override
    public void saveCustomTypeToDB(OptionalType optionalType, ACCContract.Presenter presenter) {
        // 添加到存储类
        SetTypeListUtil.addCustomType(optionalType);
        presenter.setFinishAct();
    }
}
