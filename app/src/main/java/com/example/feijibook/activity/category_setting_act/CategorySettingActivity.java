package com.example.feijibook.activity.category_setting_act;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.util.ActivityUtil;

public class CategorySettingActivity extends BaseActivity {
    CSContract.Presenter mPresenter;
    FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_setting);

        mFrameLayout = findViewById(R.id.fl_category_setting);
        super.anim(mFrameLayout);

        CategorySettingFragment categorySettingFragment = CategorySettingFragment.getInstance();
        mPresenter = new CSPresenter(this, categorySettingFragment, getIntent().getStringExtra("type"));
        mPresenter.start();
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),
                categorySettingFragment, R.id.fl_category_setting);

        super.init(mPresenter);
    }
}
