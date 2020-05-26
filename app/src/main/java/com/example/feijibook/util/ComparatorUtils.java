package com.example.feijibook.util;

import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_bean.TotalExpend;
import com.example.feijibook.entity.record_bean.TotalIncome;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * ComparatorUtils
 *
 * @author yuepengfei
 * @date 2019/6/9
 * @description 比较器工具合集
 */
public class ComparatorUtils {
    /**
     * 账单记录 按 时间(2019-04-16)最近到最早排序\降序
     */
    public static class RecordDetailDayDesComparator implements Comparator {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        @Override
        public int compare(Object o1, Object o2) {
            RecordDetail recordDetail1 = (RecordDetail) o1;
            RecordDetail recordDetail2 = (RecordDetail) o2;

            int order1 = recordDetail1.getOrder();
            int order2 = recordDetail2.getOrder();
            String time1 = recordDetail1.getYear() + "-" + recordDetail1.getMonth() + "-" + recordDetail1.getDay();
            String time2 = recordDetail2.getYear() + "-" + recordDetail2.getMonth() + "-" + recordDetail2.getDay();
            Date date1;
            Date date2;
            try {
                date1 = mSimpleDateFormat.parse(time1);
                date2 = mSimpleDateFormat.parse(time2);

            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
            if (date1.before(date2)) {
                return 1;
            } else if (date1.after(date2)) {
                return -1;
            } else {
                // 一般相同天记录的排序是不同的，不会返回0
                return Integer.compare(order2, order1);
            }
        }
    }

    /**
     * 账单记录 按 金额从大到小排序
     */
    public static class RecordDetailMoneyDesComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            RecordDetail recordDetail1 = (RecordDetail) o1;
            RecordDetail recordDetail2 = (RecordDetail) o2;

            BigDecimal money1 = new BigDecimal(recordDetail1.getMoney());
            BigDecimal money2 = new BigDecimal(recordDetail2.getMoney());

            int a = money1.compareTo(money2);

            if (a != 0) {
                // 如果 a为1 ，表示 o1 的金额 大于 o2，返回 -1，返回 -1,降序
                return a > 0 ? -1 : 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * 日总详情 按 时间(2019-04-16)最近到最早排序
     */
    public static class DayRecordDayDesComparator implements Comparator {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        @Override
        public int compare(Object o1, Object o2) {
            DayRecord dayRecord1 = (DayRecord) o1;
            DayRecord dayRecord2 = (DayRecord) o2;

            String time1 = dayRecord1.getYear() + "-" + dayRecord1.getMonth() + "-" + dayRecord1.getDay();
            String time2 = dayRecord2.getYear() + "-" + dayRecord2.getMonth() + "-" + dayRecord2.getDay();
            Date date1;
            Date date2;
            try {
                date1 = mSimpleDateFormat.parse(time1);
                date2 = mSimpleDateFormat.parse(time2);

            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
            if (date1.before(date2)) {
                return 1;
            } else if (date1.after(date2)) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    /**
     * 支出总额 按 金额降序
     */
    public static class TotalExpendDesComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            TotalExpend totalExpend1 = (TotalExpend) o1;
            TotalExpend totalExpend2 = (TotalExpend) o2;
            BigDecimal expend1 = new BigDecimal(totalExpend1.getTotalExpend());
            BigDecimal expend2 = new BigDecimal(totalExpend2.getTotalExpend());

            int a = expend1.compareTo(expend2);

            if (a != 0) {
                // 如果 a为1 ，表示 o1 的金额 大于 o2，返回 -1，返回 -1,降序
                return a > 0 ? -1 : 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * 收入总额 按 金额降序
     */
    public static class TotalIncomeDesComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            TotalIncome totalIncome1 = (TotalIncome) o1;
            TotalIncome totalIncome2 = (TotalIncome) o2;
            BigDecimal income1 = new BigDecimal(totalIncome1.getTotalIncome());
            BigDecimal income2 = new BigDecimal(totalIncome2.getTotalIncome());

            int a = income1.compareTo(income2);

            if (a != 0) {
                // 如果 a为1 ，表示 o1 的金额 大于 o2，返回 -1，返回 -1,降序
                return a > 0 ? -1 : 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * 年升序
     */
    public static class YearAscComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            int year1 = Integer.valueOf(String.valueOf(o1));
            int year2 = Integer.valueOf(String.valueOf(o2));
            return Integer.compare(year1, year2);
        }
    }

    /**
     * 月升序（最早到最晚）
     */
    public static class MonthAscComparator implements Comparator {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM", Locale.CHINA);

        @Override
        public int compare(Object o1, Object o2) {
            Date date1;
            Date date2;
            try {
                date1 = mSimpleDateFormat.parse(String.valueOf(o1));
                date2 = mSimpleDateFormat.parse(String.valueOf(o2));

            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
            if (date1.before(date2)) {
                return -1;
            } else if (date1.after(date2)) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * 周升序（最早到最晚）
     */
    public static class WeekAscComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            String date1 = (String) o1;
            String date2 = (String) o2;
            String[] s1 = date1.split("-");
            String[] s2 = date2.split("-");
            int year1 = Integer.parseInt(s1[0]);
            int year2 = Integer.parseInt(s2[0]);
            int week1 = Integer.parseInt(s1[1]);
            int week2 = Integer.parseInt(s2[1]);
            if (year1 < year2) {
                return -1;
            } else if (year1 == year2) {
                return Integer.compare(week1, week2);
            } else {
                return 1;
            }
        }
    }

    /**
     * 日升序（最早到最晚）
     */
    public static class DayAscComparator implements Comparator {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        @Override
        public int compare(Object o1, Object o2) {
            Date date1;
            Date date2;
            try {
                date1 = mSimpleDateFormat.parse(String.valueOf(o1));
                date2 = mSimpleDateFormat.parse(String.valueOf(o2));

            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
            if (date1.before(date2)) {
                return -1;
            } else if (date1.after(date2)) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * 日升序（最早到最晚）
     */
    public static class DayDesComparator implements Comparator {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        @Override
        public int compare(Object o1, Object o2) {
            Date date1;
            Date date2;
            try {
                date1 = mSimpleDateFormat.parse(String.valueOf(o1));
                date2 = mSimpleDateFormat.parse(String.valueOf(o2));

            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
            if (date1.before(date2)) {
                return 1;
            } else if (date1.after(date2)) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
