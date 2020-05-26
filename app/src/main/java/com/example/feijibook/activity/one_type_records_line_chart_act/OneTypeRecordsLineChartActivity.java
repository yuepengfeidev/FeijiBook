package com.example.feijibook.activity.one_type_records_line_chart_act;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.util.ActivityUtil;

public class OneTypeRecordsLineChartActivity extends BaseActivity {
    FrameLayout mFrameLayout;
    OTRLCContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_type_records_line_chart);

        mFrameLayout = findViewById(R.id.act_one_type_records_line_chart);

        Intent intent = getIntent();
        String stringTile = intent.getStringExtra("title");
        String expendOrIncome = intent.getStringExtra("expendOrIncome");
        String dateType = intent.getStringExtra("dateType");
        String date = intent.getStringExtra("date");

        super.anim(mFrameLayout);

        OneTypeRecordsLineChartFragment oneTypeRecordsLineChartFragment = OneTypeRecordsLineChartFragment.newInstance();
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),
                oneTypeRecordsLineChartFragment, R.id.act_one_type_records_line_chart);
        mPresenter = new OTRLCPresenter(this, oneTypeRecordsLineChartFragment, stringTile,
                expendOrIncome,dateType,date);
        mPresenter.start();

        super.init(mPresenter);
    }
}
