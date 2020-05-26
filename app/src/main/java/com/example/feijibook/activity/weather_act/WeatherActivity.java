package com.example.feijibook.activity.weather_act;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.util.ActivityUtil;

public class WeatherActivity extends BaseActivity {
    FrameLayout mFrameLayout;
    WeatherContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        // 添加进入动画
        mFrameLayout = findViewById(R.id.fl_weather_act);
        super.anim(mFrameLayout);

        WeatherFragment weatherFragment = WeatherFragment.newInstance();
        mPresenter = new WeatherPresenter(this, weatherFragment);
        mPresenter.start();
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(), weatherFragment,
                R.id.fl_weather_act);
        super.init(mPresenter);
    }
}
