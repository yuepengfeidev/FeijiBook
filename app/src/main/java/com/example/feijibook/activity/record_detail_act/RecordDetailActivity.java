package com.example.feijibook.activity.record_detail_act;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.util.ActivityUtil;

import cn.jzvd.Jzvd;

public class RecordDetailActivity extends BaseActivity {
    FrameLayout mFrameLayout;
    RDContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_record_detail);

        String id = getIntent().getStringExtra("id");

        mFrameLayout = findViewById(R.id.act_record_detail);
        super.anim(mFrameLayout);

        RecordDetailFragment recordDetailFragment = RecordDetailFragment.getInstance();
        mPresenter = new RDPresenter(this, recordDetailFragment,id);
        mPresenter.start();
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(), recordDetailFragment,
                R.id.act_record_detail);
    }

}