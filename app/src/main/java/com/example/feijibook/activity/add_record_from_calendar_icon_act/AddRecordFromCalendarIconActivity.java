package com.example.feijibook.activity.add_record_from_calendar_icon_act;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.util.ActivityUtil;


// 从calendar(日历)icon点击添加记录的activity
public class AddRecordFromCalendarIconActivity extends BaseActivity {
    ARFCContract.Presenter mPresenter;
    FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record_from_calendar);

        mFrameLayout = findViewById(R.id.fl_add_record_from_calendar);
        super.anim(mFrameLayout);

        AddRecordFromCalendarIconFragment addRecordFromCalendarIconFragment = AddRecordFromCalendarIconFragment.newInstance();
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),
                addRecordFromCalendarIconFragment, R.id.fl_add_record_from_calendar);
        mPresenter = new ARFCPresenter(this, addRecordFromCalendarIconFragment);
        mPresenter.start();

        super.init(mPresenter);
    }

}
