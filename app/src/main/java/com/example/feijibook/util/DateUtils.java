package com.example.feijibook.util;

/**
 * Created by 你是我的 on 2019/4/3
 */

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 获取 时间 的工具了
 */
public class DateUtils {

    /**
     * 获取 时间信息
     *
     * @return 年、月、日、星期几 的map
     */
    public static Map<String, String> getDate(String time) {
        Map<String, String> map = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE", Locale.CHINA);
        String selectDate;
        String dayOfWeek = null;
        String[] date;
        if (time.equals("今天")) {
            long curTime = System.currentTimeMillis();
            selectDate = format.format(curTime);
            dayOfWeek = weekFormat.format(curTime);
            date = selectDate.split("-");
        } else {
            date = time.split("/");
            selectDate = date[0] + "-" + date[1] + "-" + date[2];
            try {
                Date d = format.parse(selectDate);
                dayOfWeek = weekFormat.format(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        map.put("year", date[0]);
        map.put("month", date[1]);
        map.put("day", date[2]);
        assert dayOfWeek != null;
        map.put("dayOfWeek", dayOfWeek);
        map.put("weekOfYear", getWeekOfYear(selectDate));
        return map;
    }

    /**
     * 获取 该 日期的信息
     *
     * @param time 指定 时间 (2019-4-9)
     * @return 年、月、日、星期几 的map
     */
    public static Map<String, String> getThisDate(String time) {
        Map<String, String> map = new HashMap<>();
        SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE", Locale.CHINA);
        String dayOfWeek = weekFormat.format(time);
        String[] date = time.split("-");
        map.put("year", date[0]);
        map.put("month", date[1]);
        map.put("day", date[2]);
        map.put("dayOfWeek", dayOfWeek);
        map.put("weekOfYear", getWeekOfYear(time));

        return map;
    }

    /**
     * 获取 当前 月有多少天
     *
     * @param year  当前年
     * @param month 当前月
     * @return 该月多少天
     */
    private static int getDayOfMonth(int year, int month) {
        int day;
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            day = 29;
        } else {
            day = 28;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return day;
            default:
        }
        return 0;
    }

    /**
     * 获取 当前是第几周
     *
     * @return 返回 第几周
     */
    private static String getWeekOfYear(String time) {
        DecimalFormat mDecimalFormat = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.setTime(date);
        return mDecimalFormat.format(calendar.get(Calendar.WEEK_OF_YEAR));
    }

    /**
     * 获取 两个时间 之间的时间
     *
     * @param start 开始时间
     * @param end   结束时间
     * @param type  类型( 之间 的 日，周、月份、年份）
     * @return 返回之间 时间的列表的map
     */
    public static List<Map<String, Integer>> getBetweenDates(String start, String end, String type) {
        List<Map<String, Integer>> result = new ArrayList<>();
        Calendar tempStart = Calendar.getInstance();
        Calendar tempEnd = Calendar.getInstance();
        Calendar tempCur = Calendar.getInstance();
        tempCur.setTime(new Date());
        switch (type) {
            case "day":
                try {
                    SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    Date startTime = dayFormat.parse(start);
                    Date endTime = dayFormat.parse(end);

                    tempStart.setTime(startTime);

                    tempEnd.setTime(endTime);
                    while (tempStart.before(tempEnd) || tempStart.equals(tempEnd)) {
                        Map<String, Integer> map = new HashMap<>();
                        map.put("year", tempStart.get(Calendar.YEAR));
                        map.put("month", tempStart.get(Calendar.MONTH));
                        map.put("day", tempStart.get(Calendar.DAY_OF_MONTH));
                        result.add(map);
                        tempStart.add(Calendar.DAY_OF_YEAR, 1);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case "week":
                tempCur.setFirstDayOfWeek(Calendar.MONDAY);
                String[] sWeekStrings = start.split("-");
                String[] eWeekStrings = end.split("-");
                tempStart.set(Calendar.YEAR, Integer.parseInt(sWeekStrings[0]));
                tempStart.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(sWeekStrings[1]));
                tempStart.setFirstDayOfWeek(Calendar.MONDAY);
                tempEnd.set(Calendar.YEAR, Integer.parseInt(eWeekStrings[0]));
                tempEnd.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(eWeekStrings[1]));
                tempEnd.setFirstDayOfWeek(Calendar.MONDAY);

                // 当 最早记录日期 在 当前日期后面时，则将当前日期视为最早记录日期
                if (tempStart.get(Calendar.YEAR) > tempCur.get(Calendar.YEAR)
                        || (tempStart.get(Calendar.YEAR) == tempCur.get(Calendar.YEAR)
                        && tempStart.get(Calendar.WEEK_OF_YEAR) > tempCur.get(Calendar.WEEK_OF_YEAR))) {
                    tempStart.set(Calendar.YEAR, tempCur.get(Calendar.YEAR));
                    tempStart.set(Calendar.WEEK_OF_YEAR, tempCur.get(Calendar.WEEK_OF_YEAR));
                }
                // 当最后记录日期 在 当前日期之前时，则将当前日期视为最后记录日期
                if (tempEnd.get(Calendar.YEAR) < tempCur.get(Calendar.YEAR)
                        || (tempEnd.get(Calendar.YEAR) == tempCur.get(Calendar.YEAR)
                        && tempEnd.get(Calendar.WEEK_OF_YEAR) < tempCur.get(Calendar.WEEK_OF_YEAR))) {
                    tempEnd.set(Calendar.YEAR, tempCur.get(Calendar.YEAR));
                    tempEnd.set(Calendar.WEEK_OF_YEAR, tempCur.get(Calendar.WEEK_OF_YEAR));
                }

                while (tempStart.before(tempEnd) || tempStart.equals(tempEnd)) {
                    Map<String, Integer> map = new HashMap<>();
                    //一年有52周， 2018/12/31 为2018 的第53周 且为 2019 的第1周，当第1周时，要将2018改为2019
                    if (tempStart.get(Calendar.WEEK_OF_YEAR) == 1) {
                        map.put("year", tempStart.get(Calendar.YEAR) + 1);
                    } else {
                        map.put("year", tempStart.get(Calendar.YEAR));
                    }
                    map.put("week", tempStart.get(Calendar.WEEK_OF_YEAR));
                    result.add(map);
                    tempStart.add(Calendar.WEEK_OF_YEAR, 1);
                }
                break;
            case "month":
                String[] sMonthStrings = start.split("-");
                String[] eMonthStrings = end.split("-");
                tempStart.set(Calendar.YEAR, Integer.parseInt(sMonthStrings[0]));
                // 设置月份时，要减1，因为Calendar 是 0-11
                tempStart.set(Calendar.MONTH, Integer.parseInt(sMonthStrings[1]) - 1);
                tempEnd.set(Calendar.YEAR, Integer.parseInt(eMonthStrings[0]));
                tempEnd.set(Calendar.MONTH, Integer.parseInt(eMonthStrings[1]) - 1);
                if (tempStart.get(Calendar.YEAR) > tempCur.get(Calendar.YEAR)
                        || (tempStart.get(Calendar.YEAR) == tempCur.get(Calendar.YEAR)
                        && tempStart.get(Calendar.MONTH) > tempCur.get(Calendar.MONTH))) {
                    tempStart.set(Calendar.YEAR, tempCur.get(Calendar.YEAR));
                    tempStart.set(Calendar.MONTH, tempCur.get(Calendar.MONTH));
                }
                if (tempEnd.get(Calendar.YEAR) < tempCur.get(Calendar.YEAR)
                        || (tempEnd.get(Calendar.YEAR) == tempCur.get(Calendar.YEAR)
                        && tempEnd.get(Calendar.MONTH) < tempCur.get(Calendar.MONTH))) {
                    tempEnd.set(Calendar.YEAR, tempCur.get(Calendar.YEAR));
                    tempEnd.set(Calendar.MONTH, tempCur.get(Calendar.MONTH));
                }

                while (tempStart.before(tempEnd) || tempStart.equals(tempEnd)) {
                    Map<String, Integer> map = new HashMap<>();
                    map.put("year", tempStart.get(Calendar.YEAR));
                    // 读取月份时 +1
                    map.put("month", tempStart.get(Calendar.MONTH) + 1);
                    result.add(map);
                    tempStart.add(Calendar.MONTH, 1);
                }
                break;
            case "year":
                tempStart.set(Calendar.YEAR, Integer.parseInt(start));
                tempEnd.set(Calendar.YEAR, Integer.parseInt(end));

                if (tempStart.get(Calendar.YEAR) > tempCur.get(Calendar.YEAR)) {
                    tempStart.set(Calendar.YEAR, tempCur.get(Calendar.YEAR));
                }
                if (tempEnd.get(Calendar.YEAR) < tempCur.get(Calendar.YEAR)) {
                    tempEnd.set(Calendar.YEAR, tempCur.get(Calendar.YEAR));
                }


                while (tempStart.before(tempEnd) || tempStart.equals(tempEnd)) {
                    Map<String, Integer> map = new HashMap<>();
                    map.put("year", tempStart.get(Calendar.YEAR));
                    result.add(map);
                    tempStart.add(Calendar.YEAR, 1);
                }
                break;
            default:
                break;
        }

        return result;
    }

    /**
     * 返回 该周 中七天的时间信息 或 一个月中的时间信息
     *
     * @param year     第几年
     * @param type     类型 是周还是月
     * @param typeDate 第几周 或第几月
     * @return 返回 该月或该周 所有日期的map，用于显示chartX轴的数据列表和获取记录的主键日期列表
     */
    public static Map<String, List<String>> getDatesOfWeekOrMonth(String type, String year, String typeDate) {
        Map<String, List<String>> map = new HashMap<>();
        SimpleDateFormat xAxisFormat = new SimpleDateFormat("M-dd", Locale.CHINA);
        SimpleDateFormat keyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        List<String> xAxisList = new ArrayList<>();
        List<String> keyList = new ArrayList<>();
        Calendar curCalendar = Calendar.getInstance();
        curCalendar.setTime(new Date());
        String curDate = keyFormat.format(curCalendar.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        switch (type) {
            case "week":
                calendar.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(typeDate));
                // 设置一周 从周一开始
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                // 必须从2开始，从1开始是 周日
                for (int i = 2; i < 9; i++) {
                    calendar.set(Calendar.DAY_OF_WEEK, i);
                    String xAxisData = xAxisFormat.format(calendar.getTime());
                    String keyDate = keyFormat.format(calendar.getTime());
                    if (curDate.equals(keyDate)) {
                        xAxisList.add("今天");
                    } else {
                        xAxisList.add(xAxisData);
                    }
                    keyList.add(keyDate);
                }
                break;
            case "month":
                // 月份少1，所以像设置 1 月 必须减 1
                calendar.set(Calendar.MONTH, Integer.parseInt(typeDate) - 1);
                int dayCountsInMonth = getDayOfMonth(Integer.parseInt(year), Integer.parseInt(typeDate));
                for (int i = 1; i <= dayCountsInMonth; i++) {
                    calendar.set(Calendar.DAY_OF_MONTH, i);
                    keyList.add(keyFormat.format(calendar.getTime()));
                    if (i == dayCountsInMonth) {
                        xAxisList.add(String.valueOf(dayCountsInMonth));
                    } else {
                        switch (i) {
                            case 1:
                                xAxisList.add("1");
                                break;
                            case 5:
                                xAxisList.add("5");
                                break;
                            case 10:
                                xAxisList.add("10");
                                break;
                            case 15:
                                xAxisList.add("15");
                                break;
                            case 20:
                                xAxisList.add("20");
                                break;
                            case 25:
                                xAxisList.add("25");
                                break;
                            default:
                                xAxisList.add("");
                                break;
                        }
                    }
                }
                break;
            case "year":
                DecimalFormat mDecimalFormat = new DecimalFormat("00");
                for (int i = 1; i <= 12; i++) {
                    keyList.add(year + "-" + mDecimalFormat.format(i));
                    switch (i) {
                        case 1:
                            xAxisList.add("1月");
                            break;
                        case 3:
                            xAxisList.add("3月");
                            break;
                        case 6:
                            xAxisList.add("6月");
                            break;
                        case 9:
                            xAxisList.add("9月");
                            break;
                        case 12:
                            xAxisList.add("12月");
                            break;
                        default:
                            xAxisList.add("");
                            break;
                    }
                }
                break;
            default:
                break;
        }
        map.put("xAxisList", xAxisList);
        map.put("keyList", keyList);
        return map;
    }

    /**
     * 获取 TabLayout 需要显示的 数据列表 和 作为键获取数据 的键值列表
     *
     * @param startDate 最早日期
     * @param endDate   最后日期
     * @param dateType  日期类型（week,month,year）
     * @return 返回 数据列表 和 简直列表 的Map
     */
    public static Map<String, List<String>> getKeyListAndShowList(String startDate, String endDate,
                                                                  String dateType) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        List<String> keyList = new ArrayList<>();
        List<String> showList = new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();

        if (startDate != null && endDate != null) {
            // 今年和去年
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            String curYear = String.valueOf(calendar.get(Calendar.YEAR));
            calendar.add(Calendar.YEAR, -1);
            String lastYear = String.valueOf(calendar.get(Calendar.YEAR));
            // 本月和上月
            calendar.setTime(new Date());
            String curMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            calendar.add(Calendar.MONTH, -1);
            String lastMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            // 本周和上周
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            calendar.setTime(new Date());
            String curWeek = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            String lastWeek = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));

            // 处理组合 TabLayout上显示的数据列表
            List<Map<String, Integer>> dates = new ArrayList<>(DateUtils.getBetweenDates(startDate, endDate, dateType));
            switch (dateType) {
                case "week":
                    for (Map<String, Integer> m : dates) {
                        int year = m.get("year");
                        int week = m.get("week");
                        String weekString = decimalFormat.format(week);
                        String date = year + "-" + weekString;
                        if (String.valueOf(year).equals(curYear)) {
                            if (String.valueOf(week).equals(lastWeek)) {
                                showList.add("上周");
                            } else if (String.valueOf(week).equals(curWeek)) {
                                showList.add("本周");
                            } else {
                                showList.add(weekString + "周");
                            }
                        } else {
                            showList.add(date + "周");
                        }
                        keyList.add(date);
                    }
                    break;
                case "month":
                    for (Map<String, Integer> m : dates) {
                        int year = m.get("year");
                        int month = m.get("month");
                        String monthString = decimalFormat.format(month);
                        String date = year + "-" + monthString;
                        if (String.valueOf(year).equals(curYear)) {
                            if (String.valueOf(month).equals(lastMonth)) {
                                showList.add("上月");
                            } else if (String.valueOf(month).equals(curMonth)) {
                                showList.add("本月");
                            } else {
                                showList.add(monthString + "月");
                            }
                        } else {
                            showList.add(date + "月");
                        }
                        keyList.add(date);
                    }
                    break;
                case "year":
                    for (Map<String, Integer> m : dates) {
                        String yearString = String.valueOf(m.get("year"));
                        if (yearString.equals(curYear)) {
                            showList.add("今年");
                        } else if (yearString.equals(lastYear)) {
                            showList.add("去年");
                        } else {
                            showList.add(yearString + "年");
                        }
                        keyList.add(yearString);
                    }
                    break;
                default:
                    break;
            }
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            calendar.setTime(new Date());
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            // 月份是 0-11，所以要加1
            String month = decimalFormat.format(calendar.get(Calendar.MONTH) + 1);
            String week = decimalFormat.format(calendar.get(Calendar.WEEK_OF_YEAR));
            switch (dateType) {
                case "week":
                    showList.add("本周");
                    keyList.add(year + "-" + week);
                    break;
                case "month":
                    showList.add("本月");
                    keyList.add(year + "-" + month);
                    break;
                case "year":
                    showList.add("今年");
                    keyList.add(year);
                    break;
                default:
            }
        }
        map.put("keyList", keyList);
        map.put("showList", showList);
        return map;
    }
}
