package com.example.feijibook.activity.category_setting_act;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.activity.add_custom_category_act.AddCustomCategoryActivity;
import com.example.feijibook.adapter.ViewPagerAdapter;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.MyViewPager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategorySettingFragment extends Fragment implements CSContract.View {
    CSContract.Presenter mPresenter;

    @BindView(R.id.iv_back)
    ImageView ivCategorySettingBack;
    @BindView(R.id.tv_back)
    TextView tvCategorySettingBack;
    @BindView(R.id.tv_add_custom_category_title)
    TextView textView5;
    @BindView(R.id.tv_category_setting_income)
    TextView tvCategorySettingIncome;
    @BindView(R.id.tv_category_setting_expend)
    TextView tvCategorySettingExpend;
    @BindView(R.id.vp_category_setting_view_pager)
    MyViewPager vpCategorySettingViewPager;
    @BindView(R.id.tv_add_category)
    TextView tvAddCategory;
    Unbinder unbinder;
    View view;
    Activity mActivity;
    String[] titles = {"支出", "收入"};

    public CategorySettingFragment() {
        // Required empty public constructor
    }

    public static CategorySettingFragment getInstance() {
        return new CategorySettingFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_category_setting, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void finishAct() {
        mActivity.finish();
    }

    @Override
    public void initListener() {
        onViewClicked(view);
    }

    @Override
    public void initWidget(List<Fragment> fragments) {
        ViewPagerAdapter viewPagerAdapter = new
                ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addTitlesAndFragments(titles, fragments);
        vpCategorySettingViewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void getAct(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void selectExpendPager() {
        tvCategorySettingExpend.setTextColor(ContextCompat.getColor(MyApplication.sContext,R.color.sky_blue_like));
        tvCategorySettingExpend.setBackground(ContextCompat.getDrawable(MyApplication.sContext,R.drawable.category_setting_selected_left));
        tvCategorySettingIncome.setTextColor(ContextCompat.getColor(MyApplication.sContext,R.color.category_setting_selected_color));
        tvCategorySettingIncome.setBackground(ContextCompat.getDrawable(MyApplication.sContext,R.drawable.category_setting_unselected_right));

        vpCategorySettingViewPager.setCurrentItem(0);
    }

    @Override
    public void selectIncomePager() {
        tvCategorySettingExpend.setTextColor(ContextCompat.getColor(MyApplication.sContext,R.color.category_setting_selected_color));
        tvCategorySettingExpend.setBackground(ContextCompat.getDrawable(MyApplication.sContext,R.drawable.category_setting_unselected_left));
        tvCategorySettingIncome.setTextColor(ContextCompat.getColor(MyApplication.sContext,R.color.sky_blue_like));
        tvCategorySettingIncome.setBackground(ContextCompat.getDrawable(MyApplication.sContext,R.drawable.category_setting_selected_right));

        vpCategorySettingViewPager.setCurrentItem(1);
    }

    @Override
    public void startAct(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void setPresenter(CSContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_back, R.id.tv_back, R.id.tv_category_setting_income, R.id.tv_category_setting_expend, R.id.tv_add_category})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.tv_back:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setFinishAct();
                break;
            case R.id.tv_category_setting_income:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setSelectIncomePager(vpCategorySettingViewPager.getCurrentItem());
                break;
            case R.id.tv_category_setting_expend:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setSelectExpendPager(vpCategorySettingViewPager.getCurrentItem());
                break;
            case R.id.tv_add_category:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                BaseActivity.addBindAdjacentLayer((CategorySettingActivity) mActivity);
                mPresenter.setStartActivity(new Intent(mActivity, AddCustomCategoryActivity.class),
                        vpCategorySettingViewPager.getCurrentItem());
                break;
                default:
        }
    }
}
