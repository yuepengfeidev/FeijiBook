package com.example.feijibook.activity.category_setting_act.category_edit_frag;

import com.example.feijibook.app.Constants;
import com.example.feijibook.entity.record_type_bean.AddtiveType;
import com.example.feijibook.entity.record_type_bean.OptionalType;

import java.util.List;

/**
 * CEPresenter
 *
 * @author PengFei Yue
 * @date 2019/10/12
 * @description
 */
public class CEPresenter implements CEContract.Presenter {
    private CEContract.View mView;
    private CEContract.Model mModel;
    /**
     * 界面类型：支出类型设置 和 收入类型设置
     */
    private String mType;
    private boolean isNeedUpdate = true;

    public CEPresenter(CEContract.View view, String type) {
        mView = view;
        mType = type;
        mView.setPresenter(this);
    }

    @Override
    public void setTypeList(List<OptionalType> optionalTypes, List<AddtiveType> additiveTypes) {
        mModel.saveTypeList(mType,optionalTypes,additiveTypes);
    }

    @Override
    public void setInitRVData() {
        if (mType.equals(Constants.INCOME)) {
            mView.initRVData(mModel.getIncomeOptionalTypeList(),mModel.getIncomeAdditiveTypeList());
        }else {
            mView.initRVData(mModel.getExpendOptionalTypeList(),mModel.getExpendAdditiveTypeList());
        }
    }

    @Override
    public void isNeedUpdateRecord(boolean needUpdate) {
        isNeedUpdate = needUpdate;
    }

    @Override
    public void checkUpdateRecord() {
        if (!isNeedUpdate) {
            return;
        }
        mView.updateRecord();
        isNeedUpdate = false;
    }

    @Override
    public void start() {
        mModel = new CEModel();
    }

    @Override
    public void setInit() {
        mView.initWidget();
        mView.initListener();
    }
}
