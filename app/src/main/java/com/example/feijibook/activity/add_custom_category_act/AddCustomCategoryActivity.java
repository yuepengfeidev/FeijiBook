package com.example.feijibook.activity.add_custom_category_act;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.util.ActivityUtil;

public class AddCustomCategoryActivity extends BaseActivity{
    ACCContract.Presenter mPresenter;
    FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expend_category);

        // 添加进入动画
        mFrameLayout = findViewById(R.id.fl_add_expend_category);
        super.anim(mFrameLayout);

        AddCustomCategoryFragment addCustomCategoryFragment = AddCustomCategoryFragment.getInstance();
        mPresenter = new ACCPresenter(this, addCustomCategoryFragment);
        mPresenter.start();
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(), addCustomCategoryFragment,
                R.id.fl_add_expend_category);
        mPresenter.setTitle(getIntent().getStringExtra("type"));
        super.init(mPresenter);
    }



}
