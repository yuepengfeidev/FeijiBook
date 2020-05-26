package com.example.feijibook.activity.search_record_act;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.util.ActivityUtil;

public class SearchRecordActivity extends BaseActivity {
    SRContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_record);

        SearchRecordFragment searchRecordFragment = SearchRecordFragment.getInstance();
        mPresenter = new SRPresenter(this,searchRecordFragment);
        mPresenter.start();
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),
                searchRecordFragment,R.id.fl_search_record);

        super.init(mPresenter);
    }

    @Override
    public boolean enableSliding() {
        return false;
    }

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 0:
                createRootView();
                mPresenter.setInit();
                break;
            default:
        }
    }
}
