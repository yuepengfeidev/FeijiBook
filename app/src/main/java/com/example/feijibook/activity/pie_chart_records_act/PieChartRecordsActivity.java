package com.example.feijibook.activity.pie_chart_records_act;

import android.os.Bundle;
import android.os.Message;
import android.widget.RelativeLayout;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.util.ActivityUtil;

public class PieChartRecordsActivity extends BaseActivity {
    RelativeLayout mFrameLayout;
    PCRContract.Presenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart_records);

        String dateType = getIntent().getStringExtra("dateType");
        int index = getIntent().getIntExtra("index", 0);
        String expendOrIncome = getIntent().getStringExtra("expendOrIncome");

        mFrameLayout = findViewById(R.id.act_pie_chart_records);
        super.anim(mFrameLayout);
        PieChartRecordFragment pieChartRecordFragment = PieChartRecordFragment.newInstance();
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),
                pieChartRecordFragment, R.id.fragment_pie_chart_record);
        mPresenter = new PCRPresenter(this, pieChartRecordFragment,dateType,index,expendOrIncome);
        mPresenter.start();
        super.init(mPresenter);
    }

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 0:
                mPresenter.setInit();
                break;
            case 1:
                createRootView();
                // 重写handleMsg，创建完RootView后，设置Fragment绑定RootView的滑动监听
                mPresenter.setMoveListener(rootView);
                break;
            default:
        }
    }

}
