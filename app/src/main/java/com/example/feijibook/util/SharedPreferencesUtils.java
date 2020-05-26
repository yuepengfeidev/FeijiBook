package com.example.feijibook.util;

import android.content.SharedPreferences;

import com.example.feijibook.app.MyApplication;

/**
 * SharedPreferencesUtils
 *
 * @author yuepengfei
 * @date 2019/7/14
 * @description
 */
public class SharedPreferencesUtils {
    public static final String BUDGET_MONEY = "BudgetMoney";
    public static final String FIRST_LOGIN = "FirstLogin";
    public static final String ACCOUNT = "Account";
    public static final String PASSWORD = "PassWord";
    public static final String PORTRAIT = "Portrait";
    public static final String SEX = "Sex";
    public static final String NICKNAME = "NickName";
    public static final String REMEMBER_PASSWORD = "RememberPassword";
    public static final String UPDATE_WEATHER_DATE = "UpdateWeatherDate";
    public static final String WEATHER_CITY = "WeatherCity";
    public static final String BING_PIC = "BingPic";
    public static final String OPEND_SOUND = "OpenSound";
    public static final String GETSTURE_PASSWORD = "GesturePassword";


    /**
     * 存储相应的string类型数据到SharedPreference
     *
     * @param stringType 存储的键
     * @param content    存储的值
     */
    public static void saveStrToSp(String stringType, String content) {
        SharedPreferences sharedPreferences = MyApplication.sContext.getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(stringType, content);
        editor.apply();
    }

    /**
     * 相应键的存储过的String
     *
     * @return 相应键的存储内容
     */
    public static String getStrFromSp(String stringType) {
        SharedPreferences sharedPreferences = MyApplication.sContext.getSharedPreferences("UserInfo", 0);
        return sharedPreferences.getString(stringType, "");
    }

    public static void saveBoolToSp(String booleanType, boolean content) {
        SharedPreferences sharedPreferences = MyApplication.sContext.getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(booleanType, content);
        editor.apply();
    }

    public static boolean getBoolFromSp(String booleanType) {
        SharedPreferences sharedPreferences = MyApplication.sContext.getSharedPreferences("UserInfo", 0);
        return sharedPreferences.getBoolean(booleanType, true);
    }
}
