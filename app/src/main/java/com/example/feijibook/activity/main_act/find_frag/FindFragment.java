package com.example.feijibook.activity.main_act.find_frag;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.activity.bill_act.BillActivity;
import com.example.feijibook.activity.main_act.MainActivity;
import com.example.feijibook.activity.weather_act.WeatherActivity;
import com.example.feijibook.app.Constants;
import com.example.feijibook.entity.weather_bean.ResultBean;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.my_constraintlayout.MyConstraintLayout;
import com.example.feijibook.widget.my_dialog.SettingChooseDialog;
import com.example.feijibook.widget.my_dialog.SettingDialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindFragment extends Fragment implements FindContract.View, MainActivity.ViewPagerSelect {
    FindContract.Presenter mPresenter;
    View mView;
    Activity mActivity;
    @BindView(R.id.tv_bill_month)
    TextView tvBillMonth;
    @BindView(R.id.tv_bill_income)
    TextView tvBillIncome;
    @BindView(R.id.tv_bill_expend)
    TextView tvBillExpend;
    @BindView(R.id.tv_bill_surplus)
    TextView tvBillSurplus;
    @BindView(R.id.layout_bill)
    MyConstraintLayout layoutBill;
    @BindView(R.id.pc_budget)
    PieChart pcBudget;
    @BindView(R.id.tv_budget_month)
    TextView tvBudgetMonth;
    @BindView(R.id.tv_remain_budget)
    TextView tvRemainBudget;
    @BindView(R.id.tv_all_budget)
    TextView tvAllBudget;
    @BindView(R.id.tv_all_expend)
    TextView tvAllExpend;
    @BindView(R.id.layout_budget)
    MyConstraintLayout layoutBudget;
    @BindView(R.id.layout_weather)
    MyConstraintLayout layoutWeather;
    @BindView(R.id.tv_set_budget)
    TextView tvSetBudget;
    Unbinder unbinder;
    SettingDialog mSettingDialog;
    SettingChooseDialog mSettingChooseDialog;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.tv_temperature)
    TextView tvTemperature;
    @BindView(R.id.tv_weather)
    TextView tvWeather;
    @BindView(R.id.tv_direct_power)
    TextView tvDirectPower;
    @BindView(R.id.tv_humidity)
    TextView tvHumidity;
    @BindView(R.id.tv_air)
    TextView tvAir;
    @BindView(R.id.tv_date)
    TextView tvDate;

    public FindFragment() {
        // Required empty public constructor
    }

    public static FindFragment newInstance() {
        return new FindFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_find, container, false);
        unbinder = ButterKnife.bind(this, mView);
        NoticeUpdateUtils.updateRecordsPresenters.add(mPresenter);
        mPresenter.setInit();
        Log.d("yue", "onCreateView: findFragment ");
        return mView;
    }

    @Override
    public void setPresenter(FindContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initListener() {
        layoutBill.setClickListener(new MyConstraintLayout.ClickListener() {
            @Override
            public void onClick() {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                BaseActivity.addBindAdjacentLayer((MainActivity) getActivity());
                Intent intent = new Intent(mActivity, BillActivity.class);
                mPresenter.setStartActivity(intent);
            }
        });
        layoutBudget.setClickListener(new MyConstraintLayout.ClickListener() {
            @Override
            public void onClick() {
                SoundShakeUtil.playSound(SoundShakeUtil.SELECT_SWOOSH1_SOUND);
                mPresenter.setShowDialog();
            }
        });
        layoutWeather.setClickListener(new MyConstraintLayout.ClickListener() {
            @Override
            public void onClick() {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                BaseActivity.addBindAdjacentLayer((MainActivity) getActivity());
                Intent intent = new Intent(mActivity, WeatherActivity.class);
                mPresenter.setStartActivity(intent);
            }
        });
        MainActivity.addViewPagerSelectListener(this);
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
    public void showBill(SpannableString month, SpannableString expend, SpannableString income, SpannableString remain) {
        tvBillMonth.setText(month);
        tvBillExpend.setText(expend);
        tvBillIncome.setText(income);
        tvBillSurplus.setText(remain);
    }

    @Override
    public void showBudget(SpannableString centerText, String month, String remainBudget, String allBudget, String allExpend,
                           String textContent, float textSize, int textColor, Drawable textBg) {
        tvBudgetMonth.setText(month);
        tvRemainBudget.setText(remainBudget);
        tvAllBudget.setText(allBudget);
        tvAllExpend.setText(allExpend);
        tvSetBudget.setText(textContent);
        tvSetBudget.setTextSize(textSize);
        tvSetBudget.setTextColor(textColor);
        tvSetBudget.setBackground(textBg);

        pcBudget.setCenterText(centerText);
        List<PieEntry> pieEntries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(mActivity, R.color.pie_chart_gray));
        if (Constants.ZERO_MONEY.equals(allBudget)) {
            pieEntries.add(new PieEntry(1, 0));
        } else {
            pieEntries.add(new PieEntry(Float.valueOf(remainBudget), 0));
            pieEntries.add(new PieEntry(Float.valueOf(allExpend), 1));
            colors.add(0, ContextCompat.getColor(mActivity, R.color.sky_blue_like));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setDrawValues(false);
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        pcBudget.setData(pieData);
        showPieChartAnim();
        pcBudget.invalidate();
    }

    @Override
    public void initWidget() {
        // 初始化 PieChart
        pcBudget.setTouchEnabled(false);
        pcBudget.setTransparentCircleRadius(0f);
        pcBudget.setHoleRadius(75f);
        pcBudget.getLegend().setEnabled(false);
        pcBudget.getDescription().setEnabled(false);
        pcBudget.setDrawCenterText(true);
        pcBudget.setCenterTextSize(13);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.checkUpdateRecord();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NoticeUpdateUtils.updateRecordsPresenters.remove(mPresenter);
    }

    @Override
    public void updateRecord() {
        mPresenter.setShowBillAndBudget();
    }

    @Override
    public void showPieChartAnim() {
        pcBudget.animateXY(750, 750);
    }

    @Override
    public void showBudgetSettingDialog() {
        mSettingDialog = new SettingDialog(mActivity, SettingDialog.BILL);
        mSettingDialog.setOnOkListener(content -> mPresenter.setSaveBudgetSetting(content));
        mSettingDialog.show();
    }

    @Override
    public void showBudgetEditChooseDialog() {
        mSettingChooseDialog = new SettingChooseDialog(mActivity, SettingChooseDialog.BILL);
        mSettingChooseDialog.setOnItemClickListener(new SettingChooseDialog.OnItemClickListener() {
            @Override
            public void onFirstItemClick() {
                mPresenter.setShowBudgetSettingDialog();
            }

            @Override
            public void onSecondItemClick() {
                mPresenter.setClearBudgetSetting();
            }
        });
        mSettingChooseDialog.show();
    }

    @Override
    public void showWeather(String area, ResultBean resultBean) {
        String areaData = area + "天气实况";
        String humidity = resultBean.getRealtime().getHumidity() + "%";
        String directPower = resultBean.getRealtime().getDirect() + " " + resultBean.getRealtime().getPower();
        String temperature = resultBean.getRealtime().getTemperature() + "℃";
        tvArea.setText(areaData);
        tvTemperature.setText(temperature);
        tvDirectPower.setText(directPower);
        tvHumidity.setText(humidity);
        tvWeather.setText(resultBean.getRealtime().getInfo());
    }

    @Override
    public void showAir(int bg, String air) {
        tvAir.setBackground(ContextCompat.getDrawable(mActivity, bg));
        tvAir.setText(air);
    }

    @Override
    public void showDate(String date) {
        tvDate.setText(date);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (mSettingDialog != null && mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
        }
    }

    @Override
    public void onPageSelect(int position) {
        if (position == Constants.FIND_PAGE) {
            mPresenter.checkUpdateRecord();
        }
    }
}
