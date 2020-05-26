package com.example.feijibook.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by 你是我的 on 2019/3/3
 */
public class DateFormatUtils {
    private static final String DATE_FORMAT_PATTERN_YM = "yyyy-MM";
    private static final String DATE_FORMAT_PATTERN_YMD = "yyyy-MM-dd";
    private static final String DATE_FORMAT_PATTERN_Y = "yyyy";
    private static final String DATE_FORMAT_PATTERN_YMDH = "yyyy-MM-dd HH";
    private static final String DATE_FORMAT_PATTERN_MD = "MM月dd日";
    private static final String DATE_FORMAT_PATTERN_EEEE = "EEEE";

    /**
     * 获取当前时间的信息
     *
     * @param time yyyy-MM-dd
     * @return 今天或星期几 和 MM月dd日
     */
    public static Map<String, String> getWeekAndMD(String time) {
        long d = str2Long(time, true);
        String week;
        if (time.equals(long2Str(System.currentTimeMillis(), true))) {
            week = "今天";
        } else {
            week = new SimpleDateFormat(DATE_FORMAT_PATTERN_EEEE, Locale.CHINA).format(d);
        }
        String date = new SimpleDateFormat(DATE_FORMAT_PATTERN_MD, Locale.CHINA).format(d);
        Map<String, String> map = new HashMap<>(2);
        map.put("week", week);
        map.put("date", date);
        return map;
    }

    public static String ymdhLong2Str(long timestamp) {
        return new SimpleDateFormat(DATE_FORMAT_PATTERN_YMDH, Locale.CHINA).format(new Date(timestamp));
    }

    public static long ymdhStr2Long(String timestamp) {
        try {
            return new SimpleDateFormat(DATE_FORMAT_PATTERN_YMDH, Locale.CHINA).parse(timestamp).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 更新天气时间是否已过6小时
     *
     * @return 更新已过6小时为true
     */
    public static boolean isIntervalSixHours(String updateDate) {
        long updateTime = ymdhStr2Long(updateDate);
        long i = (System.currentTimeMillis() - updateTime) / (1000 * 60 * 60);
        return i >= 6;
    }

    public static boolean isSameDay(String updateDate) {
        String curTime = long2Str(System.currentTimeMillis(), true);
        // 当前时间是否和更新时间是同一天，不是同一天则更新每日图片
        return updateDate.contains(curTime);
    }


    /**
     * 时间戳转字符串
     *
     * @param timestamp 时间戳
     * @param isDay     是否包含日
     * @return 格式化的日期字符串
     */
    public static String long2Str(long timestamp, boolean isDay) {
        return long2Str(timestamp, getFormatPattern(isDay));
    }

    /**
     * 转成只有“年”的格式字符串
     */
    public static String long2Str(long timestamp) {
        return long2Str(timestamp, DATE_FORMAT_PATTERN_Y);
    }

    public static String long2Str(long timestamp, String pattern) {
        return new SimpleDateFormat(pattern, Locale.CHINA).format(new Date(timestamp));
    }

    /**
     * 字符串转时间戳
     *
     * @param dateStr 日期字符串
     * @param isDay   是否包含日
     * @return 时间戳
     */
    public static long str2Long(String dateStr, boolean isDay) {
        return str2Long(dateStr, getFormatPattern(isDay));
    }

    /**
     * 转成只有“年”的格式long类型
     */
    public static long str2Long(String dateStr) {
        return str2Long(dateStr, DATE_FORMAT_PATTERN_Y);
    }

    private static long str2Long(String dateStr, String pattern) {
        try {
            return new SimpleDateFormat(pattern, Locale.CHINA).parse(dateStr).getTime();
        } catch (Throwable ignored) {
        }
        return 0;
    }

    private static String getFormatPattern(boolean showSpecificTime) {
        if (showSpecificTime) {
            return DATE_FORMAT_PATTERN_YMD;
        } else {
            return DATE_FORMAT_PATTERN_YM;
        }
    }
}
