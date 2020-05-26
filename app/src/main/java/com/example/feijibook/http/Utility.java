package com.example.feijibook.http;

import android.text.TextUtils;

import com.example.feijibook.entity.weather_bean.WeatherBean;
import com.example.feijibook.entity.area_bean.City;
import com.example.feijibook.entity.area_bean.County;
import com.example.feijibook.entity.area_bean.Province;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility
 *
 * @author PengFei Yue
 * @date 2019/10/30
 * @description 转换JSON为相应实体类
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static List<Province> handleProvinceResponse(String response) {
        List<Province> provinces = new ArrayList<>();
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    provinces.add(province);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return provinces;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static List<City> handleCityResponse(String response, int provinceId) {
        List<City> cities = new ArrayList<>();
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    cities.add(city);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return cities;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static List<County> handleCountyResponse(String response, int cityId) {
        List<County> counties = new ArrayList<>();
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    counties.add(county);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return counties;
    }

    /**
     * 将返回的JSON数据解析成Weather实体类
     */
    public static WeatherBean handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String weatherContent = jsonObject.toString();
            return new Gson().fromJson(weatherContent, WeatherBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
