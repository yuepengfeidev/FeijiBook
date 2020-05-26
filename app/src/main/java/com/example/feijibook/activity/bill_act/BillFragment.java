package com.example.feijibook.activity.bill_act;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.adapter.BillRVAdapter;
import com.example.feijibook.entity.BillBean;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.my_datepicker.MyDataPicker;
import com.example.feijibook.widget.my_recyclerview.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class BillFragment extends Fragment implements BillContract.View {
    View mView;
    BillContract.Presenter mPresenter;
    Activity mActivity;
    @BindView(R.id.tv_year_surplus)
    TextView tvYearSurplus;
    @BindView(R.id.tv_year_income)
    TextView tvYearIncome;
    @BindView(R.id.tv_year_expend)
    TextView tvYearExpend;
    @BindView(R.id.rv_bill)
    RecyclerView rvBill;
    @BindView(R.id.tv_choose_date)
    TextView tvChooseDate;
    Unbinder unbinder;
    MyDataPicker mMyDataPicker;
    BillRVAdapter mAdapter;
    List<BillBean> mBillBeans ;

    public BillFragment() {
        // Required empty public constructor
    }

    public static BillFragment newInstance() {
        return new BillFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bill, container, false);
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void finishAct() {
        mActivity.finish();
    }

    @Override
    public void startAct(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void getAct(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void initWidget() {
        mAdapter = new BillRVAdapter(mActivity);
        rvBill.setNestedScrollingEnabled(false);
        rvBill.setLayoutManager(new LinearLayoutManager(mActivity));
        rvBill.addItemDecoration(new DividerDecoration(mActivity, LinearLayoutManager.VERTICAL, 1,
                ContextCompat.getColor(mActivity, R.color.line_gray)));
        rvBill.setAdapter(mAdapter);
    }

    @Override
    public void initDatePicker(long beginTime, long endTime) {
        // 通过时间戳初始化日期
        mMyDataPicker = new MyDataPicker(getActivity(), new MyDataPicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setShowSelectDate(timestamp);
            }
        }, beginTime, endTime);
        mMyDataPicker.setCancelable(true);
        // 只显示"年"
        mMyDataPicker.setOnlyShowYear();
        // 不允许循环滚动
        mMyDataPicker.setScrollLoop(false);
        // 不允许滚动动画
        mMyDataPicker.setCanShowAnim(false);
    }

    @Override
    public void showDatePicker(String dateStr) {
        mMyDataPicker.showYear(dateStr);
    }

    @Override
    public void showSelectDate(String dateStr) {
        tvChooseDate.setText(dateStr);
    }

    @Override
    public void showTotalBillDetail(SpannableString yearTotalIncome, SpannableString yearTotalExpend, SpannableString yearTotalSurplus) {
        tvYearIncome.setText(yearTotalIncome);
        tvYearExpend.setText(yearTotalExpend);
        tvYearSurplus.setText(yearTotalSurplus);
    }

    @Override
    public void setList(List<BillBean> billBeans) {
        if (mBillBeans == null) {
            mBillBeans = new ArrayList<>(billBeans);
            // 第一次加载不刷新动画
            mAdapter.setList(mBillBeans);
            LayoutAnimationController controller =
                    AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_fall_down);
            rvBill.setLayoutAnimation(controller);
        } else {
            mBillBeans.clear();
            mBillBeans.addAll(billBeans);
            mAdapter.setList(mBillBeans);
            // 更新数据后，显示刷新动画
            rvBill.scheduleLayoutAnimation();
        }

    }

    @Override
    public void setPresenter(BillContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initListener() {
        onViewClicked(mView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_back, R.id.tv_back, R.id.tv_choose_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.tv_back:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setFinishAct();
                break;
            case R.id.tv_choose_date:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                if (mMyDataPicker == null) {
                    mPresenter.setInitDatePicker();
                }
                mPresenter.setShowDatePicker(tvChooseDate.getText().toString());
                break;
            default:
        }
    }

    @Override
    public void onDestroy() {
        if (mMyDataPicker != null) {
            mMyDataPicker.onDestroy();
        }
        super.onDestroy();
    }
}
