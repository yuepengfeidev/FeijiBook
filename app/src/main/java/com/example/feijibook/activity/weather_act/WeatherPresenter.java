package com.example.feijibook.activity.weather_act;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import com.example.feijibook.entity.weather_bean.FutureBean;
import com.example.feijibook.entity.weather_bean.NearlyFiveDayStateBean;
import com.example.feijibook.entity.weather_bean.ResultBean;
import com.example.feijibook.entity.weather_bean.WeatherBean;
import com.example.feijibook.entity.area_bean.City;
import com.example.feijibook.entity.area_bean.County;
import com.example.feijibook.entity.area_bean.Province;
import com.example.feijibook.http.BaseObserver;
import com.example.feijibook.http.ExceptionHandle;
import com.example.feijibook.http.Utility;
import com.example.feijibook.util.DateFormatUtils;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.SharedPreferencesUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * WeatherPresenter
 *
 * @author PengFei Yue
 * @date 2019/10/29
 * @description
 */
public class WeatherPresenter implements WeatherContract.Presenter {
    private WeatherContract.Model mModel;
    private WeatherContract.View mView;

    WeatherPresenter(Activity activity, WeatherContract.View view) {
        mView = view;
        mView.getAct(activity);
        mView.setPresenter(this);
    }

    @Override
    public void setFinishAct() {
        mView.finishAct();
    }

    @Override
    public void setStartActivity(Intent intent) {
        mView.startAct(intent);
    }

    @Override
    public void getProvince() {
        List<Province> provinces = mModel.getProvince();
        if (provinces.size() != 0) {
            // 数据库中有数据
            getProvinceAndId(provinces);
        } else {
            mModel.requestProvince().subscribe(new BaseObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    try {
                        List<Province> provinces = Utility.handleProvinceResponse(responseBody.string());
                        mModel.saveProvince(provinces);
                        getProvinceAndId(provinces);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onHttpError(ExceptionHandle.ResponseException exception) {

                }
            });
        }
    }

    @Override
    public void getProvinceAndId(List<Province> list) {
        List<String> provinces = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        for (Province province : list) {
            provinces.add(province.getProvinceName());
            ids.add(province.getProvinceCode());
        }
        mView.showProvince(provinces, ids);
    }

    @Override
    public void getCounty(int provinceId, int cityId) {
        List<County> counties = mModel.getCounty(cityId);
        if ( counties.size() !=0) {
        getCountyAndId(counties);
        } else {
            mModel.requestCounty(provinceId, cityId).subscribe(new BaseObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    try {
                        List<County> counties = Utility.handleCountyResponse(responseBody.string(), cityId);
                        mModel.saveCounty(counties);
                        getCountyAndId(counties);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onHttpError(ExceptionHandle.ResponseException exception) {

                }
            });
        }
    }

    @Override
    public void getCountyAndId(List<County> list) {
        List<String> counties = new ArrayList<>();
        for (County county : list) {
            counties.add(county.getCountyName());
        }
        mView.showCounty(counties);
    }

    @Override
    public void getCity(int provinceId) {
        List<City> cities = mModel.getCity(provinceId);
        if (cities.size() != 0) {
            getCityAndId(cities);
        } else {
            mModel.requestCity(provinceId).subscribe(new BaseObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    try {
                        List<City> cities = Utility.handleCityResponse(responseBody.string(), provinceId);
                        mModel.saveCity(cities);
                        getCityAndId(cities);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onHttpError(ExceptionHandle.ResponseException exception) {

                }
            });
        }
    }

    @Override
    public void getCityAndId(List<City> list) {
        List<String> cities = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        for (City city : list) {
            cities.add(city.getCityName());
            ids.add(city.getCityCode());
        }
        mView.showCity(cities, ids);
    }

    @Override
    public void getWeather(boolean needUpdate) {
        String updateTime = SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.UPDATE_WEATHER_DATE);
        String weatherCity = SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.WEATHER_CITY);
        if (needUpdate || updateTime == null || "".equals(updateTime) || DateFormatUtils.isIntervalSixHours(updateTime)) {
            // 如果选择了新城市 或 没有更新时间 或 距离上次更新已过了6小时，则获取天气数据
            mModel.requestWeather(weatherCity).subscribe(new BaseObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    try {
                        String curTime = DateFormatUtils.ymdhLong2Str(System.currentTimeMillis());
                        // 存储更新时间
                        SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.UPDATE_WEATHER_DATE, curTime);
                        WeatherBean weatherBean = Utility.handleWeatherResponse(responseBody.string());
                        if (!"超过每日可允许请求次数!".equals(weatherBean.getReason())) {
                            mModel.saveWeather(weatherBean.getResult());
                            List<NearlyFiveDayStateBean> nearlyFiveDayStateBeans = new ArrayList<>();
                            for (FutureBean futureBean : weatherBean.getResult().getFuture()) {
                                NearlyFiveDayStateBean nearlyFiveDayStateBean = new NearlyFiveDayStateBean();
                                Map<String, String> map = DateFormatUtils.getWeekAndMD(futureBean.getDate());
                                nearlyFiveDayStateBean.setDate(map.get("date"));
                                nearlyFiveDayStateBean.setWeek(map.get("week"));
                                nearlyFiveDayStateBean.setWeather(futureBean.getWeather());
                                nearlyFiveDayStateBeans.add(nearlyFiveDayStateBean);
                            }
                            mView.showWeather(weatherBean.getResult(), nearlyFiveDayStateBeans);
                            setShowLineChart(weatherBean.getResult());
                            mView.showUpdateDate(curTime + "时");
                            // 重新获取了天气数据，需要通知主界面更新
                            NoticeUpdateUtils.updateWeather = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onHttpError(ExceptionHandle.ResponseException exception) {

                }
            });
        } else {
            ResultBean resultBean = mModel.getWeather();
            List<NearlyFiveDayStateBean> nearlyFiveDayStateBeans = new ArrayList<>();
            for (FutureBean futureBean : resultBean.getFuture()) {
                NearlyFiveDayStateBean nearlyFiveDayStateBean = new NearlyFiveDayStateBean();
                Map<String, String> map = DateFormatUtils.getWeekAndMD(futureBean.getDate());
                nearlyFiveDayStateBean.setDate(map.get("date"));
                nearlyFiveDayStateBean.setWeek(map.get("week"));
                nearlyFiveDayStateBean.setWeather(futureBean.getWeather());
                nearlyFiveDayStateBeans.add(nearlyFiveDayStateBean);
            }
            mView.showWeather(resultBean, nearlyFiveDayStateBeans);
            setShowLineChart(resultBean);
        }
    }

    @Override
    public void setShowBG() {
        String updateTime = SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.UPDATE_WEATHER_DATE);
        String pic = SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.BING_PIC);
        if (pic == null || "".equals(pic) || !DateFormatUtils.isSameDay(updateTime)) {
            // 没有图片地址，或当前时间与上次更新不是同一天，则更新每日图片
            mModel.requestBingPic().subscribe(new BaseObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    try {
                        String pic = responseBody.string();
                        SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.BING_PIC, pic);
                        mView.showBG(pic);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onHttpError(ExceptionHandle.ResponseException exception) {

                }
            });
        } else {
            mView.showBG(pic);
        }
    }

    @Override
    public void setShowCity() {
        String city = SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.WEATHER_CITY);
        if (city == null || "".equals(city)) {
            // 默认显示南京
            city = "南京";
            SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.WEATHER_CITY, city);
            mView.showCity(city);
        } else {
            mView.showCity(city);
        }
    }

    @Override
    public void setInitLineChart(LineChart lineChart) {
        //是否展示网格线
        lineChart.setDrawGridBackground(false);
        //是否显示边界
        lineChart.setDrawBorders(false);
        //是否可以拖动
        lineChart.setDragEnabled(false);
        //是否有触摸事件
        lineChart.setTouchEnabled(false);
        lineChart.animateXY(1500, 1500);
        lineChart.getDescription().setEnabled(false);

        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setEnabled(false);

        lineChart.getLegend().setEnabled(false);
    }

    @Override
    public void initLineDataSet(LineDataSet lineDataSet) {
        lineDataSet.setColor(Color.parseColor("#EBEBEB"));
        lineDataSet.setCircleColor(Color.parseColor("#ffffff"));
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
    }

    @Override
    public void setShowLineChart(ResultBean resultBean) {
        List<Entry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        int floatX = 0;
        for (FutureBean future : resultBean.getFuture()) {
            //得到的天气数据格式为：12/8℃，所以先分组成 string[0]="12", string[1]="8℃"
            String[] strings = future.getTemperature().split("/");
            //要得到纯数字，所以还要去掉8℃的符号，所以再使用split()进行分组，
            // 分组成：string[0]="8", string[1]="℃"，然后直接使用string[0]就是纯数字字符串
            int floatY = Integer.valueOf(strings[0]);
            int floatY2 = Integer.valueOf(strings[1].split("℃")[0]);

            Entry entry = new Entry(floatX, floatY);
            entries.add(entry);
            Entry entry2 = new Entry(floatX, floatY2);
            entries2.add(entry2);
            floatX++;
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "");
        initLineDataSet(lineDataSet);

        LineDataSet lineDataSet2 = new LineDataSet(entries2, "");
        initLineDataSet(lineDataSet2);

        lineDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return (int) value + "℃";
            }
        });
        //设置折线图填充
        lineDataSet.setValueTextColor(Color.parseColor("#ffffff"));
        lineDataSet2.setValueTextColor(Color.parseColor("#ffffff"));

        lineDataSet2.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return (int) value + "℃";
            }
        });
        LineData lineData = new LineData(lineDataSet, lineDataSet2);
        mView.showLineChart(lineData);
    }

    @Override
    public void start() {
        mModel = new WeatherModel();
    }

    @Override
    public void setInit() {
        mView.initWidget();
        mView.initListener();
        setShowBG();
        setShowCity();
        mView.showUpdateDate(SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.UPDATE_WEATHER_DATE));
        getWeather(false);
    }
}
