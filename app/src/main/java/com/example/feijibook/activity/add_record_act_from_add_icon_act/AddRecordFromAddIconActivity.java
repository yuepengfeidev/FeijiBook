package com.example.feijibook.activity.add_record_act_from_add_icon_act;

import android.os.Bundle;
import android.os.Message;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.util.ActivityUtil;

public class AddRecordFromAddIconActivity extends BaseActivity {
    ARFAContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record_from_add_icon);

        AddRecordFromAddIconFragment addRecordFromAddIconFragment = AddRecordFromAddIconFragment.getInstance();

        String id = getIntent().getStringExtra("id");
        String date = getIntent().getStringExtra("date");
        mPresenter = new ARFAPresenter(this, addRecordFromAddIconFragment,id);
        mPresenter.start();

        if (date != null) {
            mPresenter.setChooseDate(date);
        }
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),
                addRecordFromAddIconFragment, R.id.fl_add_record_from_add_icon);

        super.init(mPresenter);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.anim_slide_outout_to_bottom);
    }

    @Override
    public boolean enableSliding() {
        return false;
    }

    @Override
    public void handleMsg(Message msg) {
        if (msg.what == 0) {
            createRootView();
            mPresenter.setInit();
        }
    }
}
