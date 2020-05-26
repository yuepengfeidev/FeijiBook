package com.example.feijibook.activity.user_info_act;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.activity.record_detail_act.RDPresenter;
import com.example.feijibook.activity.record_detail_act.RecordDetailFragment;
import com.example.feijibook.util.ActivityUtil;

public class UserInfoActivity extends BaseActivity {
    FrameLayout mFrameLayout;
    UIContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mFrameLayout = findViewById(R.id.fl_user_info_act);
        super.anim(mFrameLayout);
        UserInfoFragment userInfoFragment = UserInfoFragment.newInstance();
        mPresenter = new UIPresenter(this, userInfoFragment);
        mPresenter.start();
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(), userInfoFragment,
                R.id.fl_user_info_act);
        super.init(mPresenter);
    }
}
