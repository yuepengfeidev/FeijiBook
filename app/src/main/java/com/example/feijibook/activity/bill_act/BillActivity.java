package com.example.feijibook.activity.bill_act;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.activity.pie_chart_records_act.PCRContract;
import com.example.feijibook.util.ActivityUtil;

public class BillActivity extends BaseActivity {
    FrameLayout mFrameLayout;
    BillContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        mFrameLayout = findViewById(R.id.fl_bill);
        super.anim(mFrameLayout);
        BillFragment billFragment = BillFragment.newInstance();
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(), billFragment,
                R.id.fl_bill);
        mPresenter = new BillPresenter(this, billFragment);
        mPresenter.start();

        super.init(mPresenter);
    }
}
