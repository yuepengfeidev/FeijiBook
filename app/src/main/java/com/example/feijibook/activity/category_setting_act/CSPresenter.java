package com.example.feijibook.activity.category_setting_act;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.feijibook.activity.category_setting_act.category_edit_frag.CEContract;
import com.example.feijibook.activity.category_setting_act.category_edit_frag.CEPresenter;
import com.example.feijibook.activity.category_setting_act.category_edit_frag.CategoryEditFragment;
import com.example.feijibook.app.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 你是我的 on 2019/3/27
 */
public class CSPresenter implements CSContract.Presenter {
    private CSContract.View mView;
    private String type;

    CSPresenter(Activity activity, CSContract.View view, String type) {
        this.type = type;
        mView = view;
        mView.setPresenter(this);
        mView.getAct(activity);
    }

    @Override
    public void setInit() {
        List<Fragment> fragments = new ArrayList<>();
        CategoryEditFragment expendCategoryEditFragment = CategoryEditFragment.newInstance();
        CategoryEditFragment incomeCategoryEditFragment = CategoryEditFragment.newInstance();
        fragments.add(expendCategoryEditFragment);
        fragments.add(incomeCategoryEditFragment);
        CEContract.Presenter ecePresenter = new CEPresenter(expendCategoryEditFragment, Constants.EXPEND);
        CEContract.Presenter icePresenter = new CEPresenter(incomeCategoryEditFragment, Constants.INCOME);
        ecePresenter.start();
        icePresenter.start();
        mView.initWidget(fragments);
        showPager(type);
    }

    @Override
    public void setFinishAct() {
        mView.finishAct();
    }

    @Override
    public void setAct(Activity activity) {
        mView.getAct(activity);
    }

    @Override
    public void setSelectExpendPager(int position) {
        if (position != 0) {
            mView.selectExpendPager();
        }
    }

    @Override
    public void setSelectIncomePager(int position) {
        if (position != 1) {
            mView.selectIncomePager();
        }
    }

    @Override
    public void showPager(String type) {
        if (type.equals(Constants.EXPEND)) {
            mView.selectExpendPager();
        } else if (type.equals(Constants.INCOME)) {
            mView.selectIncomePager();
        }
    }

    @Override
    public void setStartActivity(Intent intent, int pagerPosition) {
        if (pagerPosition == 0) {
            intent.putExtra("type", "支出");
        } else if (pagerPosition == 1) {
            intent.putExtra("type", "收入");
        }
        mView.startAct(intent);
    }

    @Override
    public void start() {

    }
}
