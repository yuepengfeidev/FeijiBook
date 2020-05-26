package com.example.feijibook.activity.weather_act;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.feijibook.R;
import com.example.feijibook.entity.weather_bean.NearlyFiveDayStateBean;
import com.example.feijibook.entity.weather_bean.ResultBean;
import com.example.feijibook.util.SharedPreferencesUtils;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.MySpinner;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment implements WeatherContract.View {
    @BindView(R.id.iv_choose_area)
    ImageView ivChooseArea;
    @BindView(R.id.tv_update_time)
    TextView tvUpdateTime;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.tv_temperature)
    TextView tvTemperature;
    @BindView(R.id.tv_humdity)
    TextView tvHumidity;
    @BindView(R.id.tv_air_quality)
    TextView tvAirQuality;
    @BindView(R.id.tv_wind_power)
    TextView tvWindPower;
    @BindView(R.id.tv_wind_dirction)
    TextView tvWindDirction;
    @BindView(R.id.line_chart_temperature)
    LineChart lineChart;
    @BindView(R.id.tv_week_1)
    TextView tvWeek1;
    @BindView(R.id.tv_week_2)
    TextView tvWeek2;
    @BindView(R.id.tv_weeK_3)
    TextView tvWeeK3;
    @BindView(R.id.tv_weeK_4)
    TextView tvWeeK4;
    @BindView(R.id.tv_week_5)
    TextView tvWeek5;
    @BindView(R.id.tv_data_1)
    TextView tvData1;
    @BindView(R.id.tv_data_2)
    TextView tvData2;
    @BindView(R.id.tv_data_3)
    TextView tvData3;
    @BindView(R.id.tv_data_4)
    TextView tvData4;
    @BindView(R.id.tv_data_5)
    TextView tvData5;
    @BindView(R.id.tv_weather_1)
    TextView tvWeather1;
    @BindView(R.id.tv_weather_2)
    TextView tvWeather2;
    @BindView(R.id.tv_weather_3)
    TextView tvWeather3;
    @BindView(R.id.tv_weather_4)
    TextView tvWeather4;
    @BindView(R.id.tv_weather_5)
    TextView tvWeather5;
    @BindView(R.id.srl_refresh_weather)
    SwipeRefreshLayout srlRefreshWeather;
    @BindView(R.id.spinner_province)
    MySpinner spinnerProvince;
    @BindView(R.id.spinner_city)
    MySpinner spinnerCity;
    @BindView(R.id.spinner_county)
    MySpinner spinnerCounty;
    @BindView(R.id.dl_choose_area)
    DrawerLayout dlChooseArea;
    @BindView(R.id.tv_cur_state)
    TextView tvCurState;
    @BindView(R.id.bt_ok)
    Button btOk;
    Unbinder unbinder;
    private WeatherContract.Presenter mPresenter;
    private Activity mActivity;
    View mView;
    ArrayAdapter<String> provinceAdapter;
    ArrayAdapter<String> cityAdapter;
    ArrayAdapter<String> countyAdapter;
    List<Integer> provinceIds = new ArrayList<>();
    List<Integer> cityIds = new ArrayList<>();
    private int selectedProvinceId;
    private String area;

    public WeatherFragment() {
        // Required empty public constructor
    }

    public static WeatherFragment newInstance() {
        return new WeatherFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_weather, container, false);
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void finishAct() {
        mActivity.finish();
    }

    @Override
    public void startAct(Intent intent) {
        mPresenter.setStartActivity(intent);
    }

    @Override
    public void getAct(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void showWeather(ResultBean resultBean, List<NearlyFiveDayStateBean> nearlyFiveDayStateBeans) {
        String temperature = resultBean.getRealtime().getTemperature() + "℃";
        tvTemperature.setText(temperature);
        String curState = resultBean.getRealtime().getInfo() + " " + resultBean.getFuture().get(0).getTemperature();
        tvCurState.setText(curState);
        tvWeek1.setText(nearlyFiveDayStateBeans.get(0).getWeek());
        tvData1.setText(nearlyFiveDayStateBeans.get(0).getDate());
        tvWeather1.setText(nearlyFiveDayStateBeans.get(0).getWeather());
        tvWeek2.setText(nearlyFiveDayStateBeans.get(1).getWeek());
        tvData2.setText(nearlyFiveDayStateBeans.get(1).getDate());
        tvWeather2.setText(nearlyFiveDayStateBeans.get(1).getWeather());
        tvWeeK3.setText(nearlyFiveDayStateBeans.get(2).getWeek());
        tvData3.setText(nearlyFiveDayStateBeans.get(2).getDate());
        tvWeather3.setText(nearlyFiveDayStateBeans.get(2).getWeather());
        tvWeeK4.setText(nearlyFiveDayStateBeans.get(3).getWeek());
        tvData4.setText(nearlyFiveDayStateBeans.get(3).getDate());
        tvWeather4.setText(nearlyFiveDayStateBeans.get(3).getWeather());
        tvWeek5.setText(nearlyFiveDayStateBeans.get(4).getWeek());
        tvData5.setText(nearlyFiveDayStateBeans.get(4).getDate());
        tvWeather5.setText(nearlyFiveDayStateBeans.get(4).getWeather());
        String humidity = resultBean.getRealtime().getHumidity() + "%";
        String direct = resultBean.getRealtime().getDirect();
        String aqi = resultBean.getRealtime().getAqi();
        String power = resultBean.getRealtime().getPower();
        tvHumidity.setText(humidity);
        tvWindPower.setText(power);
        tvAirQuality.setText(aqi);
        tvWindDirction.setText(direct);
    }

    @Override
    public void showUpdateDate(String date) {
        tvUpdateTime.setText(date);
    }

    @Override
    public void showBG(String bg) {
        Glide.with(mActivity).asBitmap()
                .thumbnail(0.2f)
                .apply(RequestOptions.centerCropTransform())
                .load(bg).into(new CustomViewTarget<DrawerLayout, Bitmap>(dlChooseArea) {
            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
            }

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                dlChooseArea.setBackground(new BitmapDrawable(mActivity.getResources(), resource));
            }

            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    @Override
    public void showCity(String city) {
        tvArea.setText(city);
    }

    @Override
    public void initWidget() {
        mPresenter.setInitLineChart(lineChart);
    }

    @Override
    public void showLineChart(LineData lineData) {
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    @Override
    public void showProvince(List<String> provinces, List<Integer> ids) {
        if (provinceAdapter == null) {
            provinceIds.addAll(ids);
            provinceAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, provinces);
            spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                    selectedProvinceId = provinceIds.get(position);
                    mPresenter.getCity(selectedProvinceId);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spinnerProvince.setAdapter(provinceAdapter);
        }
    }

    @Override
    public void showCity(List<String> cities, List<Integer> ids) {
        if (cityAdapter == null) {
            cityIds.addAll(ids);
            cityAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, cities);
            spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                    mPresenter.getCounty(selectedProvinceId, cityIds.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spinnerCity.setAdapter(cityAdapter);
        } else {
            cityIds.clear();
            cityIds.addAll(ids);
            cityAdapter.clear();
            cityAdapter.addAll(cities);
            cityAdapter.notifyDataSetChanged();
        }
        // 默认选择第一个，更新县
        spinnerCity.setSelection(0);
    }

    @Override
    public void showCounty(List<String> counties) {
        if (countyAdapter == null) {
            countyAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, counties);
            spinnerCounty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                    area = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spinnerCounty.setAdapter(countyAdapter);
        } else {
            countyAdapter.clear();
            countyAdapter.addAll(counties);
            countyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setPresenter(WeatherContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initListener() {
        ivChooseArea.setOnClickListener(v -> dlChooseArea.openDrawer(Gravity.END));
        srlRefreshWeather.setOnRefreshListener(() -> {
            mPresenter.getWeather(true);
            srlRefreshWeather.setRefreshing(false);
        });
        dlChooseArea.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                SoundShakeUtil.playSound(SoundShakeUtil.SWOOSH1_SOUND);
                mPresenter.getProvince();
            }
        });
        btOk.setOnClickListener(v -> {
            SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
            dlChooseArea.closeDrawers();
            SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.WEATHER_CITY, area);
            mPresenter.setShowCity();
            mPresenter.getWeather(true);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
