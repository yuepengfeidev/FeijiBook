package com.example.feijibook.activity.weather_act;

import android.app.Activity;
import android.content.Intent;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.entity.weather_bean.NearlyFiveDayStateBean;
import com.example.feijibook.entity.weather_bean.ResultBean;
import com.example.feijibook.entity.weather_bean.WeatherBean;
import com.example.feijibook.entity.area_bean.City;
import com.example.feijibook.entity.area_bean.County;
import com.example.feijibook.entity.area_bean.Province;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * WeatherContract
 *
 * @author PengFei Yue
 * @date 2019/10/29
 * @description
 */
public interface WeatherContract {
    interface View extends BaseView<Presenter> {
        /**
         * 销毁当前活动
         */
        void finishAct();

        /**
         * 打开活动，添加该层活动前移滚动初始动画
         */
        void startAct(Intent intent);

        /**
         * 获得该碎片的activity
         */
        void getAct(Activity activity);

        /**
         * 显示天气
         */
        void showWeather(ResultBean resultBean, List<NearlyFiveDayStateBean> nearlyFiveDayStateBeans);

        /**
         * 显示更新时间
         */
        void showUpdateDate(String date);

        /**
         * 显示背景
         */
        void showBG(String bg);

        void showCity(String city);

        void initWidget();

        void showLineChart(LineData lineData);

        /**
         * 显示省的下拉框
         *
         * @param provinces 全国省列表数据
         */
        void showProvince(List<String> provinces, List<Integer> ids);

        void showCity(List<String> cities, List<Integer> ids);

        void showCounty(List<String> counties);
    }

    interface Presenter extends BasePresenter {
        /**
         * 设置销毁当前活动
         */
        void setFinishAct();

        /**
         * 设置打开活动
         */
        void setStartActivity(Intent intent);

        /**
         * 请求获取省
         */
        void getProvince();

        void getProvinceAndId(List<Province> list);

        /**
         * 请求获取区
         */
        void getCounty(int provinceId, int cityId);

       void getCountyAndId(List<County> list);

        /**
         * 请求获取市
         */
        void getCity(int provinceId);

        void getCityAndId(List<City> list);

        /**
         * 获取该城市的天气
         *
         * @param needUpdate 是否需要更新
         */
        void getWeather(boolean needUpdate);

        /**
         * 设置显示背景
         */
        void setShowBG();

        void setShowCity();

        void setInitLineChart(LineChart lineChart);

        void initLineDataSet(LineDataSet lineDataSet);

        void setShowLineChart(ResultBean resultBean);


    }

    interface Model {
        /**
         * 请求获取省
         */
        Observable<ResponseBody> requestProvince();

        /**
         * 请求获取市
         */
        Observable<ResponseBody> requestCity(int provinceId);

        /**
         * 请求获取区
         */
        Observable<ResponseBody> requestCounty(int provinceId, int cityId);

        /**
         * 获取该城市的天气接口
         *
         * @param city 城市
         */
        Observable<ResponseBody> requestWeather(String city);

        /**
         * 获取Bing每日图片接口
         */
        Observable<ResponseBody> requestBingPic();

        /**
         * 从数据库中获取省
         */
        List<Province> getProvince();

        /**
         * 从数据库中获取市
         */
        List<City> getCity(int provinceId);

        /**
         * 从数据库中获取区
         */
        List<County> getCounty(int cityId);

        /**
         * 存储所有省
         */
        void saveProvince(List<Province> list);

        void saveCity(List<City> list);

        void saveCounty(List<County> list);

        /**
         * 从数据库中获取天气数据
         */
        ResultBean getWeather();

        /**
         * 存储天气数据
         */
        void saveWeather(ResultBean resultBean);
    }
}
