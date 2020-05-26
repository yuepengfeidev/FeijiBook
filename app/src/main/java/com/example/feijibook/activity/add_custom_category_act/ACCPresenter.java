package com.example.feijibook.activity.add_custom_category_act;

import android.app.Activity;

import com.example.feijibook.entity.record_type_bean.CustomType;
import com.example.feijibook.entity.record_type_bean.OptionalType;
import com.example.feijibook.util.SetTypeListUtil;

/**
 * Created by 你是我的 on 2019/3/27
 */
public class ACCPresenter implements ACCContract.Presenter {
    private ACCContract.View mView;
    private String titleContent;
    private ACCContract.Model mModel;

    ACCPresenter(Activity activity, ACCContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mView.getAct(activity);
    }

    @Override
    public void setInit() {
        mView.initWidget(SetTypeListUtil.setCustomType());
        mView.initListener();
        mView.showTitle(titleContent);
    }

    @Override
    public void setFinishAct() {
        mView.finishAct();
    }

    @Override
    public void setTitle(String content) {
        titleContent = content;
    }

    @Override
    public void setSaveCustomType(String customName, CustomType customType, String category) {
        OptionalType optionalType = new OptionalType();
        optionalType.setTypeName_O(customType.getCategory());
        optionalType.setCustomTypeName(customName);
        optionalType.setTypeIconUrl_O(customType.getTypeIconUrl());
        optionalType.setIncomeOrExpend_O(category);
        optionalType.setDefaultOrCustom_O("c");
        mModel.saveCustomTypeToDB(optionalType,this);

    }

    @Override
    public void setHideKeyboard() {
        mView.hideKeyboard();
    }

    @Override
    public void start() {
        mModel = new ACCModel();
    }

}
