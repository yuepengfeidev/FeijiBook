package com.example.feijibook.util;

/**
 * Created by 你是我的 on 2019/4/3
 */

import android.util.Log;

import com.example.feijibook.app.Constants;
import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_bean.TotalExpend;
import com.example.feijibook.entity.record_bean.TotalIncome;
import com.example.feijibook.entity.record_bean.WeekRecord;
import com.example.feijibook.entity.record_bean.YearRecord;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;

import io.realm.RealmList;

/**
 * 从数据库一次性 读取所有记录，存储到该类中
 */
public class RecordListUtils {
    /**
     * 按时时间 从最近到最早降序
     */
    private static List<RecordDetail> sRecordDetailList = new ArrayList<>();
    private static List<DayRecord> sDayRecordList = new ArrayList<>();
    private static List<MonthRecord> sMonthRecordList = new ArrayList<>();
    private static List<WeekRecord> sWeekRecordList = new ArrayList<>();
    private static List<YearRecord> sYearRecordList = new ArrayList<>();
    /**
     * 比较器
     */
    private static ComparatorUtils.RecordDetailDayDesComparator sRecordDetailDayDesComparator
            = new ComparatorUtils.RecordDetailDayDesComparator();
    private static ComparatorUtils.RecordDetailMoneyDesComparator sRecordDetailMoneyDesComparator
            = new ComparatorUtils.RecordDetailMoneyDesComparator();
    private static ComparatorUtils.DayRecordDayDesComparator sDayRecordDayDesComparator
            = new ComparatorUtils.DayRecordDayDesComparator();
    private static ComparatorUtils.TotalExpendDesComparator sTotalExpendDesComparator
            = new ComparatorUtils.TotalExpendDesComparator();
    private static ComparatorUtils.TotalIncomeDesComparator sTotalIncomeDesComparator
            = new ComparatorUtils.TotalIncomeDesComparator();
    private static ComparatorUtils.DayAscComparator sDayAscComparator = new ComparatorUtils.DayAscComparator();
    private static ComparatorUtils.DayDesComparator sDayDesComparator = new ComparatorUtils.DayDesComparator();
    private static ComparatorUtils.WeekAscComparator sWeekAscComparator = new ComparatorUtils.WeekAscComparator();
    private static ComparatorUtils.MonthAscComparator sMonthAscComparator = new ComparatorUtils.MonthAscComparator();
    private static ComparatorUtils.YearAscComparator sYearAscComparator = new ComparatorUtils.YearAscComparator();
    /**
     * 每月的日总记录详情 map <月，该月所有日总记录详情>
     */
    private static Map<String, List<DayRecord>> sDayRecordsInMonthMap = new HashMap<>();
    /**
     * 每月的所有详情记录<月，该月的每笔记录的详情>
     */
    private static Map<String, List<RecordDetail>> sRecordDetailsInMonthMap = new HashMap<>();
    /**
     * 每天的日总记录map <日，当日的日总详情记录>
     */
    private static Map<String, DayRecord> sDayRecordsInDayMap = new HashMap<>();

    /**
     * 周、月、年 各类型费用排行map <周，该周各类型费用降序排行list>
     */
    private static Map<String, WeekRecord> sWeekRecordRankMap = new TreeMap<>(new Comparator<String>() {
        // 按时间排序，从最早到最后\升序,  第一位时最早日期，最后一位时最晚日期
        @Override
        public int compare(String o1, String o2) {
            String[] s1 = o1.split("-");
            String[] s2 = o2.split("-");
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
    });

    /**
     * <年，年用详情记录>
     */
    private static Map<String, YearRecord> sYearRecordRankMap = new TreeMap<>(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            int year1 = Integer.valueOf(o1);
            int year2 = Integer.valueOf(o2);
            return Integer.compare(year1, year2);
        }
    });
    /**
     * <月，月总详情记录>
     */
    private static Map<String, MonthRecord> sMonthRecordRankMap = new TreeMap<>(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            String[] s1 = o1.split("-");
            String[] s2 = o2.split("-");
            int year1 = Integer.parseInt(s1[0]);
            int year2 = Integer.parseInt(s2[0]);
            int month1 = Integer.parseInt(s1[1]);
            int month2 = Integer.parseInt(s2[1]);
            if (year1 < year2) {
                return -1;
            } else if (year1 == year2) {
                return Integer.compare(month1, month2);
            } else {
                return 1;
            }
        }
    });

    /**
     * 通过类型 获取 该周、该月、该年 所有该类型的记录
     * <类型，<日期类型（week,month,year,day）,<日期,所有该类型的金额降序记录list>>>
     */
    private static Map<String, Map<String, Map<String, List<RecordDetail>>>> sOneTypeAllRecordsInDateMap = new HashMap<>();

    /**
     * 通过类型 获取 该周、该月、该年 该类型在各个时间段的总额
     * <类型，<日期类型（week,month,year,day）,<日降序获取最早值和最晚值,所有该类型的金额降序记录list>>>
     */
    private static Map<String, Map<String, Map<String, String>>> sOneTypeTotalRecordInDateMap = new HashMap<>();

    /**
     * 记录下 刚添加 的记录的时间, 便于定位到那天的
     */
    private static Map<String, String> addRecordDateMap = new HashMap<>();

    /**
     * 当天记录，<每天日期（2019-05-08）,<收入 或 支出 类型,当天所有记录按金额降序排列>>，用于显示 一天中最大的三比记录
     */
    private static Map<String, Map<String, List<RecordDetail>>> sAllRecordsInDayMap = new HashMap<>();
    /**
     * 当月所有记录 <每月（2019-05）,<收入或支出类型，当所月有记录按金额降序排列>>，用于显示 一月中最大的三比记录
     */
    private static Map<String, Map<String, List<RecordDetail>>> sAllRecordsInMonthMap = new HashMap<>();

    /**
     * 该类型的所有记录，记录顺序按时间降序排序 <类型，所有该类型记录按时间降序/> 用于搜索
     */
    private static Map<String, List<RecordDetail>> sOneTypeAllRecordsMap = new HashMap<>();


    /**
     * 存储所有记录详情的Map <id,记录详情/>
     */
    private static Map<String, RecordDetail> sAllRecordDetailsMap = new HashMap<>();
    /**
     * 所有日总详情记录map<id,日总详情/>
     */
    private static Map<String, DayRecord> sAllDayRecordsMap = new HashMap<>();
    /**
     * 所有周总详情记录<id,周总详情/>
     */
    private static Map<String, WeekRecord> sAllWeekRecordsMap = new HashMap<>();
    /**
     * 所有月总详情记录<id,月总详情/>
     */
    private static Map<String, MonthRecord> sAllMonthRecordsMap = new HashMap<>();

    /**
     * 所有年总详情记录<id,年总详情/>
     */
    private static Map<String, YearRecord> sAllYearRecordsMap = new HashMap<>();

    /**
     * 正在编辑的记录
     */
    private static RecordDetail sIsEditingRecord;

    /**
     * 编辑前的原数据（用于删除存储类中的该记录）
     */
    private static RecordDetail sResRecord;

    /**
     * 含有照片的记录的map 和 含有视频的记录的map
     */
    private static Map<String, RecordDetail> sHavePhotosRecordsMap = new TreeMap<>();
    private static Map<String, RecordDetail> sHaveVideoRecordsMap = new TreeMap<>();
    /**
     * 含有照片的记录的List 和 含有视频的记录的List ，按时间降序（最近到最早）
     */
    private static List<Map.Entry<String, RecordDetail>> sHavePhotosRecordList = new ArrayList<>();
    private static List<Map.Entry<String, RecordDetail>> sHaveVideoRecordList = new ArrayList<>();

    public static List<Map.Entry<String, RecordDetail>> getHavePhotosRecordList() {
        return sHavePhotosRecordList;
    }

    public static List<Map.Entry<String, RecordDetail>> getHaveVideoRecordList() {
        return sHaveVideoRecordList;
    }

    /**
     * 含有照片的记录的map 和 含有视频的记录的map中，按值的时间降序排序存入含有照片的记录的List 和 含有视频的记录的List
     *
     * @param map  含有照片的记录的map 和 含有视频的记录的map
     * @param list 含有照片的记录的List 和 含有视频的记录的List
     */
    private static void sortList(Map<String, RecordDetail> map, List<Map.Entry<String, RecordDetail>> list) {
        list.clear();
        list.addAll(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, RecordDetail>>() {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

            @Override
            public int compare(Map.Entry<String, RecordDetail> o1, Map.Entry<String, RecordDetail> o2) {

                RecordDetail recordDetail1 = o1.getValue();
                RecordDetail recordDetail2 = o2.getValue();

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
        });
    }

    public static RecordDetail getResRecord() {
        return sResRecord;
    }

    static Map<String, MonthRecord> getAllMonthRecordsMap() {
        return sAllMonthRecordsMap;
    }


    public static void setResRecord(RecordDetail resRecord) {
        sResRecord = resRecord;
    }

    public static RecordDetail getIsEditingRecord() {
        return sIsEditingRecord;
    }

    public static void setIsEditingRecord(RecordDetail isEditingRecord) {
        sIsEditingRecord = isEditingRecord;
    }

    public static Map<String, RecordDetail> getAllRecordDetailsMap() {
        return sAllRecordDetailsMap;
    }

    public static Map<String, DayRecord> getAllDayRecordsMap() {
        return sAllDayRecordsMap;
    }


    static Map<String, WeekRecord> getAllWeekRecordsMap() {
        return sAllWeekRecordsMap;
    }


    static Map<String, YearRecord> getAllYearRecordsMap() {
        return sAllYearRecordsMap;
    }

    public static Map<String, List<RecordDetail>> getOneTypeAllRecordsMap() {
        return sOneTypeAllRecordsMap;
    }

    public static Map<String, Map<String, Map<String, String>>> getOneTypeTotalRecordInDateMap() {
        return sOneTypeTotalRecordInDateMap;
    }

    public static Map<String, Map<String, Map<String, List<RecordDetail>>>> getOneTypeAllRecordsInDateMap() {
        return sOneTypeAllRecordsInDateMap;
    }


    public static Map<String, String> getAddRecordDateMap() {
        return addRecordDateMap;
    }

    public static Map<String, List<DayRecord>> getDayRecordsInMonthMap() {
        return sDayRecordsInMonthMap;
    }

    public static Map<String, DayRecord> getDayRecordsInDayMap() {
        return sDayRecordsInDayMap;
    }

    public static Map<String, List<RecordDetail>> getRecordDetailsInMonthMap() {
        return sRecordDetailsInMonthMap;
    }

    public static Map<String, WeekRecord> getWeekRecordRankMap() {
        return sWeekRecordRankMap;
    }

    public static Map<String, YearRecord> getYearRecordRankMap() {
        return sYearRecordRankMap;
    }

    public static Map<String, MonthRecord> getMonthRecordRankMap() {
        return sMonthRecordRankMap;
    }

    public static Map<String, Map<String, List<RecordDetail>>> getAllRecordsInDayMap() {
        return sAllRecordsInDayMap;
    }


    public static Map<String, Map<String, List<RecordDetail>>> getAllRecordsInMonthMap() {
        return sAllRecordsInMonthMap;
    }


    public static List<DayRecord> getSearchedDayRecords(List<RecordDetail> list) {
        Collections.sort(list, sRecordDetailDayDesComparator);
        Map<String, DayRecord> dayRecordMap = new TreeMap<>();
        for (RecordDetail record : list) {
            String date = record.getYear() + "-" + record.getMonth() + "-" + record.getDay();
            if (dayRecordMap.containsKey(date)) {
                DayRecord resDayRecord = dayRecordMap.get(date);
                if (record.getType().equals("收入")) {
                    resDayRecord.setDayTotalIncome(
                            getTotalMoeny("add", resDayRecord.getDayTotalIncome(), record.getMoney()));
                } else {
                    resDayRecord.setDayTotalExpend(
                            getTotalMoeny("add", resDayRecord.getDayTotalExpend(), record.getMoney())
                    );
                }
            } else {
                DayRecord dayRecord = new DayRecord();
                dayRecord.setId(date);
                dayRecord.setDay(record.getDay());
                dayRecord.setMonth(record.getMonth());
                dayRecord.setWeek(record.getWeek());
                dayRecord.setYear(record.getYear());
                if (record.getType().equals("收入")) {
                    dayRecord.setDayTotalIncome(record.getMoney());
                } else {
                    dayRecord.setDayTotalExpend(record.getMoney());
                }
                dayRecordMap.put(date, dayRecord);
            }
        }

        return new ArrayList<>(dayRecordMap.values());
    }


    /**
     * 是否 添加 或 改变 记录，提醒 记录显示界面 更新记录
     */
    public static void deleteRecord(RecordDetail recordDetail) {
        String day = recordDetail.getDay();
        String month = recordDetail.getMonth();
        String year = recordDetail.getYear();
        String weekOfYear = recordDetail.getWeekOfYear();
        String money = recordDetail.getMoney();
        String type = recordDetail.getType();
        String detailType = recordDetail.getDetailType();
        String remark = recordDetail.getRemark();
        String id = recordDetail.getId();
        String videoUrl = recordDetail.getVideoUrl();
        String imgUrl = recordDetail.getImgUrl();

        // 第几周 （2019-14） 2019年第14周
        String woy = year + "-" + weekOfYear;

        // 第几个 月 （2019-04）
        String moy = year + "-" + month;

        // 2019-9-23
        String ymd = moy + "-" + day;

        if (imgUrl != null && !"".equals(imgUrl)) {
            sHavePhotosRecordsMap.remove(id);
            sortList(sHavePhotosRecordsMap, sHavePhotosRecordList);
        }
        if (videoUrl != null && !"".equals(videoUrl)) {
            sHaveVideoRecordsMap.remove(id);
            sortList(sHaveVideoRecordsMap, sHaveVideoRecordList);
        }

        // 删除 单项记录
        for (RecordDetail record : Objects.requireNonNull(sRecordDetailsInMonthMap.get(moy))) {
            if (record.getId().equals(id)) {
                Objects.requireNonNull(sRecordDetailsInMonthMap.get(moy)).remove(record);
                sAllRecordDetailsMap.remove(id);
                break;
            }
        }

        // 从日总记录中删除关于该条记录的详情
        List<DayRecord> dayRecords = sDayRecordsInMonthMap.get(moy);
        for (DayRecord dayRecord : dayRecords) {
            if (dayRecord.getId().equals(ymd)) {
                switch (type) {
                    case "收入":
                        String totalIncome = dayRecord.getDayTotalIncome();
                        if (Double.valueOf(totalIncome) > 0) {
                            dayRecord.setDayTotalIncome(getTotalMoeny("sub", dayRecord.getDayTotalIncome(), money));
                        }
                        break;
                    case "支出":
                        String totalExpend = dayRecord.getDayTotalExpend();
                        if (Double.valueOf(totalExpend) > 0) {
                            dayRecord.setDayTotalExpend(getTotalMoeny("sub", dayRecord.getDayTotalExpend(), money));
                        }
                        break;
                    default:
                        break;
                }
                if ("0".equals(RealmUtils.checkNull(dayRecord.getDayTotalExpend()))
                        && "0".equals(RealmUtils.checkNull(dayRecord.getDayTotalIncome()))) {
                    dayRecords.remove(dayRecord);
                    sAllDayRecordsMap.remove(ymd);
                    break;
                } else {
                    sAllDayRecordsMap.put(ymd, dayRecord);
                }
            }
        }
        if (dayRecords.size() == 0) {
            sDayRecordsInMonthMap.remove(moy);
        }

        if (sWeekRecordRankMap.containsKey(woy)) {
            WeekRecord weekRecord = sWeekRecordRankMap.get(woy);
            assert weekRecord != null;
            RealmList<TotalExpend> totalExpends = weekRecord.getWeekTotalExpends();
            RealmList<TotalIncome> totalIncomes = weekRecord.getWeekTotalIncomes();
            switch (type) {
                case "收入":
                    weekRecord.setTotalIncome(getTotalMoeny("sub", weekRecord.getTotalIncome(), money));
                    for (TotalIncome income : totalIncomes) {
                        // 包含该类型记录，则从该周总记录中减去该条记录
                        if (income.getType().equals(detailType)) {
                            String totalIncome = getTotalMoeny("sub", income.getTotalIncome(), money);
                            // 该类型金额为0时，删除该类型
                            if (Double.valueOf(totalIncome) == 0) {
                                totalIncomes.remove(income);
                            } else {
                                income.setTotalIncome(totalIncome);
                            }
                            break;
                        } else if (totalIncomes.indexOf(income)
                                == totalIncomes.size() - 1) {
                            Log.d("deleteInWeek:", "不存在该条记录");
                        }
                    }
                    break;
                case "支出":
                    weekRecord.setTotalExpend(getTotalMoeny("sub", weekRecord.getTotalExpend(), money));
                    for (TotalExpend expend : totalExpends) {
                        if (expend.getType().equals(detailType)) {
                            String totalExpend = getTotalMoeny("sub", expend.getTotalExpend(), money);
                            if (Double.valueOf(totalExpend) == 0) {
                                totalExpends.remove(expend);
                            } else {
                                expend.setTotalEpend(totalExpend);
                            }
                            break;
                        } else if (totalExpends.indexOf(expend)
                                == totalExpends.size() - 1) {
                            Log.d("deleteInWeek:", "不存在该条记录");
                        }
                    }
                    break;
                default:
                    break;
            }
            if ("0".equals(RealmUtils.checkNull(weekRecord.getTotalExpend()))
                    && "0".equals(RealmUtils.checkNull(weekRecord.getTotalIncome()))) {
                sWeekRecordRankMap.remove(woy);
                sAllWeekRecordsMap.remove(woy);
            } else {
                sAllWeekRecordsMap.put(woy, weekRecord);
            }
        } else {
            Log.d("deleteInWeek:", "不存在该条记录");
        }

        if (sMonthRecordRankMap.containsKey(moy)) {
            MonthRecord monthRecord = sMonthRecordRankMap.get(moy);
            assert monthRecord != null;
            RealmList<TotalExpend> totalExpends = monthRecord.getMonthTotalExpends();
            RealmList<TotalIncome> totalIncomes = monthRecord.getMonthTotalIncomes();
            switch (type) {
                case "收入":
                    monthRecord.setTotalIncome(getTotalMoeny("sub", monthRecord.getTotalIncome(), money));
                    for (TotalIncome income : totalIncomes) {
                        // 包含该类型记录，则从该周总记录中减去该条记录
                        if (income.getType().equals(detailType)) {
                            String totalIncome = getTotalMoeny("sub", income.getTotalIncome(), money);
                            // 如果没有该类型的收入，则删除该类型
                            if ("0".equals(totalIncome)) {
                                totalIncomes.remove(income);
                            } else {
                                income.setTotalIncome(totalIncome);
                            }
                            break;
                        } else if (totalIncomes.indexOf(income) == totalIncomes.size() - 1) {
                            Log.d("deleteInMonth:", "不存在该条记录");
                        }
                    }
                    break;
                case "支出":
                    monthRecord.setTotalExpend(getTotalMoeny("sub", monthRecord.getTotalExpend(), money));
                    for (TotalExpend expend : totalExpends) {
                        if (expend.getType().equals(detailType)) {
                            String totalExpend = getTotalMoeny("sub", expend.getTotalExpend(), money);
                            if (totalExpend.equals("0")) {
                                totalExpends.remove(expend);
                            } else {
                                expend.setTotalEpend(totalExpend);
                            }
                            break;
                        } else if (totalExpends.indexOf(expend) == totalExpends.size() - 1) {
                            Log.d("deleteInMonth:", "不存在该条记录");
                        }
                    }
                    break;
                default:
                    break;
            }
            // 如果该月没有总收入且没有总支出，则表示该月没有记录，删除该月的记录
            if ("0".equals(RealmUtils.checkNull(monthRecord.getTotalIncome()))
                    && "0".equals(RealmUtils.checkNull(monthRecord.getTotalExpend()))) {
                sMonthRecordRankMap.remove(moy);
                sAllMonthRecordsMap.remove(moy);
            } else {
                sAllMonthRecordsMap.put(moy, monthRecord);
            }
        } else {
            Log.d("deleteInMonth:", "不存在该条记录");
        }

        if (sYearRecordRankMap.containsKey(year)) {
            YearRecord yearRecord = sYearRecordRankMap.get(year);
            assert yearRecord != null;
            RealmList<TotalExpend> totalExpends = yearRecord.getYearTotalExpends();
            RealmList<TotalIncome> totalIncomes = yearRecord.getYearTotalIncomes();
            switch (type) {
                case "收入":
                    yearRecord.setTotalIncome(getTotalMoeny("sub", yearRecord.getTotalIncome(), money));
                    for (TotalIncome income : totalIncomes) {
                        // 包含该类型记录，则从该周总记录中减去该条记录
                        if (income.getType().equals(detailType)) {
                            String totalIncome = getTotalMoeny("sub", income.getTotalIncome(), money);
                            if (Double.valueOf(totalIncome) == 0) {
                                totalIncomes.remove(income);
                            } else {
                                income.setTotalIncome(totalIncome);
                            }
                            break;
                        } else if (totalIncomes.indexOf(income)
                                == totalIncomes.size() - 1) {
                            Log.d("deleteInYear:", "不存在该条记录");
                        }
                    }
                    break;
                case "支出":
                    yearRecord.setTotalExpend(getTotalMoeny("sub", yearRecord.getTotalExpend(), money));
                    for (TotalExpend expend : totalExpends) {
                        if (expend.getType().equals(detailType)) {
                            String totalExpend = getTotalMoeny("sub", expend.getTotalExpend(), money);
                            if (Double.valueOf(totalExpend) == 0) {
                                totalExpends.remove(expend);
                            } else {
                                expend.setTotalEpend(totalExpend);
                            }
                            break;
                        } else if (totalExpends.indexOf(expend)
                                == totalExpends.size() - 1) {
                            Log.d("deleteInYear:", "不存在该条记录");
                        }
                    }
                    break;
                default:
                    break;
            }
            if ("0".equals(RealmUtils.checkNull(yearRecord.getTotalExpend()))
                    && "0".equals(RealmUtils.checkNull(yearRecord.getTotalIncome()))) {
                sYearRecordRankMap.remove(year);
                sAllYearRecordsMap.remove(year);
            } else {
                sAllYearRecordsMap.put(year, yearRecord);
            }
        } else {
            Log.d("deleteInYear:", "不存在该条记录");
        }

        deleteRecordFromAllRecordsInDayAndAllRecordsInMonth(recordDetail);
        deleteRecordFromOneTypeAllRecordsInDateMap(recordDetail);

        // 删除对应类型和标注类型的记录
        deleteRecordFromOneTypeAllRecordsMap(recordDetail, remark);
        if (!remark.equals(detailType)) {
            deleteRecordFromOneTypeAllRecordsMap(recordDetail, detailType);
        }
    }

    /**
     * 添加单项记录
     *
     * @param recordDetail 该记录详情
     * @return id
     */
    @SuppressWarnings("unchecked")
    public static void addRecord(RecordDetail recordDetail) {
        String day = recordDetail.getDay();
        String dayOfWeek = recordDetail.getWeek();
        String month = recordDetail.getMonth();
        String year = recordDetail.getYear();
        String weekOfYear = recordDetail.getWeekOfYear();
        String money = recordDetail.getMoney();
        String type = recordDetail.getType();
        String detailType = recordDetail.getDetailType();
        String remark = recordDetail.getRemark();
        String imgUrl = recordDetail.getImgUrl();
        String videoUrl = recordDetail.getVideoUrl();
        int iconUrl = recordDetail.getIconUrl();
        String id = UUID.randomUUID().toString();
        // 每条记录的排序号
        int order = 0;
        recordDetail.setId(id);

        // 第几周 （2019-14） 2019年第14周
        String woy = year + "-" + weekOfYear;

        // 第几个 月 （2019-04）
        String moy = year + "-" + month;

        // 年月日
        String ymd = moy + "-" + day;

        sAllRecordDetailsMap.put(id, recordDetail);
        sIsEditingRecord.setId(id);


        DayRecord dayRecord;
        if (sDayRecordsInDayMap.containsKey(ymd)) {
            dayRecord = sDayRecordsInDayMap.get(ymd);
            switch (type) {
                case "收入":
                    dayRecord.setDayTotalIncome(getTotalMoeny("add", dayRecord.getDayTotalIncome(), money));
                    break;
                case "支出":
                    dayRecord.setDayTotalExpend(getTotalMoeny("add", dayRecord.getDayTotalExpend(), money));
                    break;
                default:
                    break;
            }
        } else {
            // 创建该天详情总记录
            dayRecord = new DayRecord();
            dayRecord.setId(ymd);
            dayRecord.setDay(day);
            dayRecord.setWeek(dayOfWeek);
            dayRecord.setMonth(month);
            dayRecord.setYear(year);
            switch (type) {
                case "收入":
                    dayRecord.setDayTotalIncome(money);
                    break;
                case "支出":
                    dayRecord.setDayTotalExpend(money);
                    break;
                default:
                    break;
            }
            sDayRecordsInDayMap.put(ymd, dayRecord);
        }
        sAllDayRecordsMap.put(ymd, dayRecord);

        addRecordDateMap.put(Constants.YEAR, year);
        addRecordDateMap.put(Constants.MONTH, month);
        addRecordDateMap.put(Constants.DAY, day);

        if (sRecordDetailsInMonthMap.keySet().contains(moy)) {
            // 当该月有记录，但被删除后，该月的map，依旧存在，但其中的记录没有了，所以不用遍历插入，直接插入list
            if (Objects.requireNonNull(sRecordDetailsInMonthMap.get(moy)).size() == 0) {
                recordDetail.setOrder(order);
                Objects.requireNonNull(sRecordDetailsInMonthMap.get(moy)).add(recordDetail);
            } else {
                int index = -1;
                boolean isAdd = false;
                for (int i = 0; i < Objects.requireNonNull(sRecordDetailsInMonthMap.get(moy)).size(); i++) {
                    RecordDetail recordDetail1 = Objects.requireNonNull(sRecordDetailsInMonthMap.get(moy)).get(i);
                    int curDay = Integer.valueOf(day);
                    int compareDay = Integer.valueOf(recordDetail1.getDay());

                    if (curDay >= compareDay && !isAdd) {
                        // 记录插入的位置
                        index = i;
                        isAdd = true;
                    }

                    if (curDay == compareDay) {
                        // 取得相同日期的最后一条记录的排序号，用来设置该条记录的排序号
                        order = recordDetail1.getOrder() + 1;
                        break;
                    }
                }
                recordDetail.setOrder(order);
                if (isAdd) {
                    sRecordDetailsInMonthMap.get(moy).add(index, recordDetail);
                } else {
                    // 该条记录的日期，在该月所有记录之前，则添加在列表的最后
                    sRecordDetailsInMonthMap.get(moy).add(recordDetail);
                }

            }
        } else {
            List<RecordDetail> list = new ArrayList<>();
            list.add(recordDetail);
            sRecordDetailsInMonthMap.put(moy, list);
        }

        if (imgUrl != null && !"".equals(imgUrl)) {
            sHavePhotosRecordsMap.put(id, recordDetail);
            sortList(sHavePhotosRecordsMap, sHavePhotosRecordList);
        }
        if (videoUrl != null && !"".equals(videoUrl)) {
            sHaveVideoRecordsMap.put(id, recordDetail);
            sortList(sHaveVideoRecordsMap, sHaveVideoRecordList);
        }

        // dayRecordMap为存在日总详情记录添加金额时，也是对sEveryDayRecordMap添加金额
        if (!sDayRecordsInMonthMap.containsKey(moy)) {
            List<DayRecord> list = new ArrayList<>();
            list.add(dayRecord);
            sDayRecordsInMonthMap.put(moy, list);
        } else {
            List<DayRecord> list = sDayRecordsInMonthMap.get(moy);
            assert list != null;
            for (int i = 0; i < list.size(); i++) {
                DayRecord record = list.get(i);
                if (Integer.valueOf(day).equals(Integer.valueOf(record.getDay()))) {
                    // 日期相同时不用修改，该map中的dayrecord与sAllDayRecordsMap中的地址相同，且已修改
                    break;
                } else if (Integer.valueOf(day) > Integer.valueOf(record.getDay())) {
                    // 在该天之后，则添加到该索引之前
                    sDayRecordsInMonthMap.get(moy).add(i, dayRecord);
                    break;
                } else if (i == list.size() - 1) {
                    // 在该月所有记录日期之前，则直接添加到该列表最后
                    sDayRecordsInMonthMap.get(moy).add(dayRecord);
                    break;
                }
            }
        }

        if (sWeekRecordRankMap.containsKey(woy)) {
            WeekRecord weekRecord = sWeekRecordRankMap.get(woy);
            assert weekRecord != null;
            RealmList<TotalExpend> totalExpends = weekRecord.getWeekTotalExpends();
            RealmList<TotalIncome> totalIncomes = weekRecord.getWeekTotalIncomes();
            switch (type) {
                case "收入":
                    weekRecord.setTotalIncome(getTotalMoeny("add", weekRecord.getTotalIncome(), money));
                    if (totalIncomes.size() == 0) {
                        addTotalIncomeToList(totalIncomes, detailType, iconUrl, money);
                    } else {
                        for (TotalIncome income : totalIncomes) {
                            if (income.getType().equals(detailType)) {
                                income.setTotalIncome(getTotalMoeny("add", income.getTotalIncome(), money));
                                break;
                            } else if (totalIncomes.indexOf(income) == totalIncomes.size() - 1) {
                                addTotalIncomeToList(totalIncomes, detailType, iconUrl, money);
                                break;
                            }
                        }
                        Collections.sort(totalIncomes, sTotalIncomeDesComparator);
                    }
                    break;
                case "支出":
                    weekRecord.setTotalExpend(getTotalMoeny("add", weekRecord.getTotalExpend(), money));
                    if (totalExpends.size() == 0) {
                        addTotalExpendToList(totalExpends, detailType, iconUrl, money);
                    } else {
                        for (TotalExpend expend : totalExpends) {
                            if (expend.getType().equals(detailType)) {
                                expend.setTotalEpend(getTotalMoeny("add", expend.getTotalExpend(), money));
                                break;
                            } else if (totalExpends.indexOf(expend) == totalExpends.size() - 1) {
                                addTotalExpendToList(totalExpends, detailType, iconUrl, money);
                                break;
                            }
                        }
                        Collections.sort(totalExpends, sTotalExpendDesComparator);
                    }
                    break;
                default:
            }
            sAllWeekRecordsMap.put(woy, weekRecord);
        } else {
            RealmList<TotalExpend> expendList = new RealmList();
            RealmList<TotalIncome> incomeList = new RealmList();
            WeekRecord weekRecord = new WeekRecord();
            switch (type) {
                case "收入":
                    addTotalIncomeToList(incomeList, detailType, iconUrl, money);

                    weekRecord.setTotalIncome(money);
                    weekRecord.setTotalExpend("0");
                    break;
                case "支出":
                    addTotalExpendToList(expendList, detailType, iconUrl, money);

                    weekRecord.setTotalExpend(money);
                    weekRecord.setTotalIncome("0");
                    break;
                default:
            }
            weekRecord.setId(woy);
            weekRecord.setYear(year);
            weekRecord.setWeek(woy);
            weekRecord.setWeekTotalExpends(expendList);
            weekRecord.setWeekTotalIncomes(incomeList);
            sWeekRecordRankMap.put(woy, weekRecord);
            sAllWeekRecordsMap.put(woy, weekRecord);
        }

        if (sMonthRecordRankMap.containsKey(moy)) {
            MonthRecord monthRecord = sMonthRecordRankMap.get(moy);
            assert monthRecord != null;
            RealmList<TotalExpend> totalExpends = monthRecord.getMonthTotalExpends();
            RealmList<TotalIncome> totalIncomes = monthRecord.getMonthTotalIncomes();
            switch (type) {
                case "收入":
                    monthRecord.
                            setTotalIncome(getTotalMoeny("add",
                                    monthRecord.getTotalIncome(), money));
                    if (totalIncomes.size() == 0) {
                        addTotalIncomeToList(totalIncomes, detailType, iconUrl, money);
                    } else {
                        for (TotalIncome income : totalIncomes) {
                            if (income.getType().equals(detailType)) {
                                income.setTotalIncome(getTotalMoeny("add", income.getTotalIncome(), money));
                                break;
                            } else if (totalIncomes.indexOf(income)
                                    == totalIncomes.size() - 1) {
                                addTotalIncomeToList(totalIncomes, detailType, iconUrl, money);
                                break;
                            }
                        }
                        Collections.sort(totalIncomes, sTotalIncomeDesComparator);
                    }
                    break;
                case "支出":
                    monthRecord.setTotalExpend(getTotalMoeny("add",
                            monthRecord.getTotalExpend(), money));
                    if (totalExpends.size() == 0) {
                        addTotalExpendToList(totalExpends, detailType, iconUrl, money);
                    } else {
                        for (TotalExpend expend : totalExpends) {
                            if (expend.getType().equals(detailType)) {
                                expend.setTotalEpend(getTotalMoeny("add", expend.getTotalExpend(), money));
                                break;
                            } else if (totalExpends.indexOf(expend)
                                    == totalExpends.size() - 1) {
                                addTotalExpendToList(totalExpends, detailType, iconUrl, money);
                                break;
                            }
                        }
                        Collections.sort(totalExpends, sTotalExpendDesComparator);
                    }
                    break;
                default:
            }
            sAllMonthRecordsMap.put(moy, monthRecord);
        } else {
            RealmList expendList = new RealmList();
            RealmList incomeList = new RealmList();
            MonthRecord monthRecord = new MonthRecord();
            switch (type) {
                case "收入":
                    // 添加该类型到该类型总收入详情
                    addTotalIncomeToList(incomeList, detailType, iconUrl, money);

                    // 设置该月总收入和总支出
                    monthRecord.setTotalIncome(money);
                    monthRecord.setTotalExpend("0");
                    break;
                case "支出":
                    addTotalExpendToList(expendList, detailType, iconUrl, money);

                    monthRecord.setTotalExpend(money);
                    monthRecord.setTotalIncome("0");
                    break;
                default:
            }
            monthRecord.setId(moy);
            monthRecord.setYear(year);
            monthRecord.setMonth(moy);
            monthRecord.setMonthTotalExpends(expendList);
            monthRecord.setMonthTotalIncomes(incomeList);
            sMonthRecordRankMap.put(moy, monthRecord);
            sAllMonthRecordsMap.put(moy, monthRecord);
        }

        if (sYearRecordRankMap.containsKey(year)) {
            YearRecord yearRecord = sYearRecordRankMap.get(year);
            assert yearRecord != null;
            RealmList<TotalExpend> totalExpends = yearRecord.getYearTotalExpends();
            RealmList<TotalIncome> totalIncomes = yearRecord.getYearTotalIncomes();
            switch (type) {
                case "收入":
                    yearRecord.setTotalIncome(getTotalMoeny("add", yearRecord.getTotalIncome(), money));
                    if (totalIncomes.size() == 0) {
                        addTotalIncomeToList(totalIncomes, detailType, iconUrl, money);
                    } else {
                        for (TotalIncome income : totalIncomes) {
                            if (income.getType().equals(detailType)) {
                                income.setTotalIncome(getTotalMoeny("add", income.getTotalIncome(), money));
                                break;
                            } else if (totalIncomes.indexOf(income)
                                    == totalIncomes.size() - 1) {
                                addTotalIncomeToList(totalIncomes, detailType, iconUrl, money);
                                break;
                            }
                        }
                        Collections.sort(totalIncomes, sTotalIncomeDesComparator);
                    }
                    break;
                case "支出":
                    yearRecord.setTotalExpend(getTotalMoeny("add", yearRecord.getTotalExpend(), money));
                    if (totalExpends.size() == 0) {
                        addTotalExpendToList(totalExpends, detailType, iconUrl, money);
                    } else {
                        for (TotalExpend expend : totalExpends) {
                            if (expend.getType().equals(detailType)) {
                                expend.setTotalEpend(getTotalMoeny("add", expend.getTotalExpend(), money));
                                break;
                            } else if (totalExpends.indexOf(expend)
                                    == totalExpends.size() - 1) {
                                addTotalExpendToList(totalExpends, detailType, iconUrl, money);
                                break;
                            }
                        }
                        Collections.sort(totalExpends, sTotalExpendDesComparator);
                    }
                    break;
                default:
            }
            sAllYearRecordsMap.put(year, yearRecord);
        } else {
            RealmList expendList = new RealmList();
            RealmList incomeList = new RealmList();
            YearRecord yearRecord = new YearRecord();
            switch (type) {
                case "收入":
                    addTotalIncomeToList(incomeList, detailType, iconUrl, money);

                    yearRecord.setTotalIncome(money);
                    yearRecord.setTotalExpend("0");
                    break;
                case "支出":
                    addTotalExpendToList(expendList, detailType, iconUrl, money);

                    yearRecord.setTotalExpend(money);
                    yearRecord.setTotalIncome("0");
                    break;
                default:
            }
            yearRecord.setId(year);
            yearRecord.setYear(year);
            yearRecord.setYearTotalExpends(expendList);
            yearRecord.setYearTotalIncomes(incomeList);
            sYearRecordRankMap.put(year, yearRecord);
            sAllYearRecordsMap.put(year, yearRecord);
        }

        setAddRecordToAllRecordsInDayAndAllRecordsInMonth(recordDetail, true);
        addRecordToOneTypeAllRecordsInDateMap(recordDetail, true);

        // 添加类型 或 备注为主键的 单类型所有记录map
        addRecordToOneAllRecordsMap(recordDetail, remark);
        if (!remark.equals(detailType)) {
            addRecordToOneAllRecordsMap(recordDetail, detailType);
        }

    }

    /**
     * 将 两个 金钱 字符串 转换为 bigdecimal 类型计算
     *
     * @param arithmeticType 运算类型
     * @param lastMoney      上次金额
     * @param curMoney       当前需要相加金额
     * @return 总金额的字符串
     */
    private static String getTotalMoeny(String arithmeticType, String lastMoney, String curMoney) {
        if (lastMoney == null) {
            lastMoney = "0";
        }
        BigDecimal last = new BigDecimal(lastMoney);
        BigDecimal cur = new BigDecimal(curMoney);
        BigDecimal total = null;
        switch (arithmeticType) {
            case "add":
                total = last.add(cur);
                break;
            case "sub":
                total = last.subtract(cur);
                break;
            default:
        }

        assert total != null;
        return total.toString();
    }


    @SuppressWarnings("unchecked")
    public static void setRecordDetailList(List<RecordDetail> recordDetailList) {
        if (sRecordDetailList.size() != 0) {
            return;
        }
        if (recordDetailList.size() != 0) {
            sRecordDetailList = recordDetailList;
            List<RecordDetail> sRecordDetailList2 = new ArrayList<>(recordDetailList);
            // 按金额降序
            Collections.sort(sRecordDetailList2, sRecordDetailMoneyDesComparator);
            // 按时间降序
            Collections.sort(sRecordDetailList, sRecordDetailDayDesComparator);

            // 按 月（2019-04）存入map，方便 通过 月主键获取
            for (RecordDetail recordDetail : sRecordDetailList) {
                String detailType = recordDetail.getDetailType();
                String remark = recordDetail.getRemark();
                String year = recordDetail.getYear();
                String month = year + "-" + recordDetail.getMonth();
                String id = recordDetail.getId();
                String videoUrl = recordDetail.getVideoUrl();
                String imgUrl = recordDetail.getImgUrl();


                if (imgUrl != null && !"".equals(imgUrl)) {
                    sHavePhotosRecordsMap.put(id, recordDetail);
                }
                if (videoUrl != null && !"".equals(videoUrl)) {
                    sHaveVideoRecordsMap.put(id, recordDetail);
                }

                sAllRecordDetailsMap.put(id, recordDetail);

                if (sRecordDetailsInMonthMap.keySet().contains(month)) {
                    Objects.requireNonNull(sRecordDetailsInMonthMap.get(month)).add(recordDetail);
                } else {
                    List<RecordDetail> recordDetails = new ArrayList<>();
                    recordDetails.add(recordDetail);
                    sRecordDetailsInMonthMap.put(month, recordDetails);
                }


                // 通过 备注 和 类型 作为主键 添加记录  单类型所有记录map
                addRecordToOneAllRecordsMap(recordDetail, remark);
                if (!detailType.equals(remark)) {
                    addRecordToOneAllRecordsMap(recordDetail, detailType);
                }
            }

            sortList(sHavePhotosRecordsMap, sHavePhotosRecordList);
            sortList(sHaveVideoRecordsMap, sHaveVideoRecordList);

            // 将相同周且相同类型存入map,且是按金额降序存入的
            for (RecordDetail recordDetail : sRecordDetailList2) {
                setAddRecordToAllRecordsInDayAndAllRecordsInMonth(recordDetail, false);
                addRecordToOneTypeAllRecordsInDateMap(recordDetail, false);
            }
        }
    }


    @SuppressWarnings("unchecked")
    public static void setDayRecordList(List<DayRecord> dayRecordList) {
        if (sDayRecordList.size() != 0) {
            return;
        }
        if (dayRecordList.size() != 0) {
            sDayRecordList = dayRecordList;
            Collections.sort(sDayRecordList, sDayRecordDayDesComparator);

            for (DayRecord dayRecord : sDayRecordList) {
                String moy = dayRecord.getYear() + "-" + dayRecord.getMonth();
                String ymd = moy + "-" + dayRecord.getDay();
                String id = dayRecord.getId();

                sAllDayRecordsMap.put(id, dayRecord);

                // sDayRecordsInMonthMap 和 sDayRecordsInDayMap 中的 日总详情记录都是 同一地址，如需更改只要dayRecordMap删除添加一次
                if (sDayRecordsInMonthMap.keySet().contains(moy)) {
                    Objects.requireNonNull(sDayRecordsInMonthMap.get(moy)).add(dayRecord);
                } else {
                    List<DayRecord> dayRecords = new ArrayList<>();
                    dayRecords.add(dayRecord);
                    sDayRecordsInMonthMap.put(moy, dayRecords);
                }

                if (sDayRecordsInDayMap.containsKey(ymd)) {
                    Log.d("setDayRecordList:", "出现两个相同日器的日总详情记录!");
                } else {
                    sDayRecordsInDayMap.put(ymd, dayRecord);
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    public static void setMonthRecordList(List<MonthRecord> monthRecordList) {
        if (sMonthRecordList.size() != 0) {
            return;
        }
        if (monthRecordList.size() != 0) {
            sMonthRecordList = monthRecordList;

            for (MonthRecord monthRecord : sMonthRecordList) {
                String id = monthRecord.getId();
                sAllMonthRecordsMap.put(id, monthRecord);
                Collections.sort(monthRecord.getMonthTotalExpends(), sTotalExpendDesComparator);
                Collections.sort(monthRecord.getMonthTotalIncomes(), sTotalIncomeDesComparator);
                sMonthRecordRankMap.put(monthRecord.getMonth(), monthRecord);
            }
        }
    }


    @SuppressWarnings("unchecked")
    public static void setWeekRecordList(List<WeekRecord> weekRecordList) {
        if (sWeekRecordList.size() != 0) {
            return;
        }
        if (weekRecordList.size() != 0) {
            sWeekRecordList = weekRecordList;

            for (WeekRecord weekRecord : sWeekRecordList) {
                String id = weekRecord.getId();
                sAllWeekRecordsMap.put(id, weekRecord);
                // 将中记录中的 支出 和 收入 总额详情 按金额 降序
                Collections.sort(weekRecord.getWeekTotalExpends(), sTotalExpendDesComparator);
                Collections.sort(weekRecord.getWeekTotalIncomes(), sTotalIncomeDesComparator);
                // 通过 周 获取 该周 记录排行
                sWeekRecordRankMap.put(weekRecord.getWeek(), weekRecord);
            }

        }
    }


    @SuppressWarnings("unchecked")
    public static void setYearRecordList(List<YearRecord> yearRecordList) {
        if (sYearRecordList.size() != 0) {
            return;
        }
        if (yearRecordList.size() != 0) {
            sYearRecordList = yearRecordList;

            for (YearRecord yearRecord : sYearRecordList) {
                String id = yearRecord.getId();
                sAllYearRecordsMap.put(id, yearRecord);

                Collections.sort(yearRecord.getYearTotalExpends(), sTotalExpendDesComparator);
                Collections.sort(yearRecord.getYearTotalIncomes(), sTotalIncomeDesComparator);

                sYearRecordRankMap.put(yearRecord.getYear(), yearRecord);
            }
        }
    }

    /**
     * 删除 该笔 详情记录从 sAllRecordsInDayMap 和 sAllRecordsInMonthMap
     *
     * @param recordDetail 删除的单笔详情记录
     */
    private static void deleteRecordFromAllRecordsInDayAndAllRecordsInMonth(RecordDetail recordDetail) {
        String month = recordDetail.getYear() + "-" + recordDetail.getMonth();
        String ymd = month + "-" + recordDetail.getDay();
        String expendOrIncome;
        if (recordDetail.getType().equals("支出")) {
            expendOrIncome = Constants.EXPEND;
        } else {
            expendOrIncome = Constants.INCOME;
        }

        Map<String, List<RecordDetail>> dMap = sAllRecordsInDayMap.get(ymd);
        Map<String, List<RecordDetail>> mMap = sAllRecordsInMonthMap.get(month);
        List<RecordDetail> allRecordsInDayList = dMap.get(expendOrIncome);
        List<RecordDetail> allRecordsInMonthList = mMap.get(expendOrIncome);
        for (RecordDetail recordDetail1 : allRecordsInDayList) {
            if (recordDetail.getId().equals(recordDetail1.getId())) {
                allRecordsInDayList.remove(recordDetail1);
                break;
            }
        }
        for (RecordDetail recordDetail1 : allRecordsInMonthList) {
            if (recordDetail.getId().equals(recordDetail1.getId())) {
                allRecordsInMonthList.remove(recordDetail);
                break;
            }
        }

        if (dMap.get(Constants.EXPEND).size() == 0 && dMap.get(Constants.INCOME).size() == 0) {
            sAllRecordsInDayMap.remove(ymd);
        }

        if (mMap.get(Constants.EXPEND).size() == 0 && mMap.get(Constants.INCOME).size() == 0) {
            sAllRecordsInMonthMap.remove(month);
        }
    }

    /**
     * 设置 添加 该笔 详情记录到 sAllRecordsInDayMap 和 sAllRecordsInMonthMap
     *
     * @param recordDetail 添加的单笔详情记录
     */
    @SuppressWarnings("unchecked")
    private static void setAddRecordToAllRecordsInDayAndAllRecordsInMonth(RecordDetail recordDetail, boolean isSort) {
        String month = recordDetail.getYear() + "-" + recordDetail.getMonth();
        String ymd = month + "-" + recordDetail.getDay();
        String expendOrIncome;
        if (recordDetail.getType().equals("支出")) {
            expendOrIncome = Constants.EXPEND;
        } else {
            expendOrIncome = Constants.INCOME;
        }
        // 添加记录
        addRecordToAllRecordsInDayAndAllRecordsInMonth(sAllRecordsInDayMap, ymd, expendOrIncome, recordDetail, isSort);
        addRecordToAllRecordsInDayAndAllRecordsInMonth(sAllRecordsInMonthMap, month, expendOrIncome, recordDetail, isSort);
    }

    /**
     * 添加 该笔 详情记录到 sAllRecordsInDayMap 和 sAllRecordsInMonthMap
     *
     * @param map            存储记录的map
     * @param date           日期(周 或 月 的日期)
     * @param expendOrIncome 收入 或 支出 类型
     * @param recordDetail   记录详情
     * @param isSort         是否要排序
     */
    @SuppressWarnings("unchecked")
    private static void addRecordToAllRecordsInDayAndAllRecordsInMonth(Map<String, Map<String, List<RecordDetail>>> map,
                                                                       String date, String expendOrIncome,
                                                                       RecordDetail recordDetail, boolean isSort) {
        // 有该天的记录，则添加到该天所有记录列表中
        if (map.keySet().contains(date)) {
            Map<String, List<RecordDetail>> m = map.get(date);
            List<RecordDetail> list = m.get(expendOrIncome);
            list.add(recordDetail);
            if (isSort) {
                Collections.sort(list, sRecordDetailMoneyDesComparator);
            }
        } else {
            Map<String, List<RecordDetail>> m = new HashMap<>();
            List<RecordDetail> expends = new ArrayList<>();
            List<RecordDetail> incomes = new ArrayList<>();
            if (Constants.EXPEND.equals(expendOrIncome)) {
                expends.add(recordDetail);
            } else {
                incomes.add(recordDetail);
            }
            m.put(Constants.INCOME, incomes);
            m.put(Constants.EXPEND, expends);
            map.put(date, m);
        }

    }


    /**
     * 添加记录到 用于查询的 单类型所有记录map中
     *
     * @param recordDetail 记录详情
     * @param keyType      记录map的类型主键
     */
    private static void deleteRecordFromOneTypeAllRecordsMap(RecordDetail recordDetail, String keyType) {
// 删除对应类型和标注类型的记录
        if (sOneTypeAllRecordsMap.containsKey(keyType)) {
            List<RecordDetail> recordDetails = sOneTypeAllRecordsMap.get(keyType);
            recordDetails.remove(recordDetail);
            if (recordDetails.size() == 0) {
                sOneTypeAllRecordsMap.remove(keyType);
            }
        }
    }

    /**
     * 用于查询的 单类型所有日总记录map中 删除 记录
     *
     * @param recordDetail 记录详情
     * @param keyType      记录map的类型主键
     */
    private static void addRecordToOneAllRecordsMap(RecordDetail recordDetail, String keyType) {
        if (sOneTypeAllRecordsMap.containsKey(keyType)) {
            List<RecordDetail> recordDetails = sOneTypeAllRecordsMap.get(keyType);
            recordDetails.add(recordDetail);
            Collections.sort(recordDetails, sRecordDetailDayDesComparator);
        } else {
            List<RecordDetail> recordDetails = new ArrayList<>();
            recordDetails.add(recordDetail);
            sOneTypeAllRecordsMap.put(keyType, recordDetails);
        }
    }

    /**
     * 删除 该笔 详情记录从 sOneTypeAllRecordsInDateMap 和 总额从· sOneTypeTotalRecordInDateMap
     *
     * @param recordDetail 删除的单笔详情记录
     */
    private static void deleteRecordFromOneTypeAllRecordsInDateMap(RecordDetail recordDetail) {
        String year = recordDetail.getYear();
        String month = recordDetail.getYear() + "-" + recordDetail.getMonth();
        String weekOfYear = recordDetail.getYear() + "-" + recordDetail.getWeekOfYear();
        String ymd = month + "-" + recordDetail.getDay();
        String type = recordDetail.getDetailType();
        String money = recordDetail.getMoney();

        Map<String, Map<String, List<RecordDetail>>> dateTypeMap = sOneTypeAllRecordsInDateMap.get(type);
        Map<String, List<RecordDetail>> recordsInYearMap = dateTypeMap.get(Constants.YEAR);
        Map<String, List<RecordDetail>> recordsInMonthMap = dateTypeMap.get(Constants.MONTH);
        Map<String, List<RecordDetail>> recordsInWeekMap = dateTypeMap.get(Constants.WEEK);
        Map<String, List<RecordDetail>> recordsInDayMap = dateTypeMap.get(Constants.DAY);
        List<RecordDetail> recordsInYearList = recordsInYearMap.get(year);
        List<RecordDetail> recordsInMonthList = recordsInMonthMap.get(month);
        List<RecordDetail> recordsInWeekList = recordsInWeekMap.get(weekOfYear);
        List<RecordDetail> recordsInDayList = recordsInDayMap.get(ymd);

        recordsInYearList.remove(recordDetail);
        recordsInMonthList.remove(recordDetail);
        recordsInWeekList.remove(recordDetail);
        recordsInDayList.remove(recordDetail);

        // 当该记录 没有 年记录时，表明没有该类型的记录，则删除掉该类型的记录
        if (recordsInYearList.size() == 0) {
            sOneTypeAllRecordsInDateMap.remove(type);
        }

        Map<String, Map<String, String>> dateTypeMap2 = sOneTypeTotalRecordInDateMap.get(type);
        Map<String, String> totalRecordInYearMap = dateTypeMap2.get(Constants.YEAR);
        Map<String, String> totalRecordInMonthMap = dateTypeMap2.get(Constants.MONTH);
        Map<String, String> totalRecordInWeekMap = dateTypeMap2.get(Constants.WEEK);
        Map<String, String> totalRecordInDayMap = dateTypeMap2.get(Constants.DAY);

        totalRecordInYearMap.put(year, getTotalMoeny("sub", totalRecordInYearMap.get(year), money));
        totalRecordInMonthMap.put(month, getTotalMoeny("sub", totalRecordInMonthMap.get(month), money));
        totalRecordInWeekMap.put(weekOfYear, getTotalMoeny("sub", totalRecordInWeekMap.get(weekOfYear), money));
        totalRecordInDayMap.put(ymd, getTotalMoeny("sub", totalRecordInDayMap.get(ymd), money));

        // 当年总额为0时，移除该类型
        if (totalRecordInYearMap.get(year).equals(Constants.ZERO_MONEY)) {
            sOneTypeTotalRecordInDateMap.remove(type);
        }
    }

    /**
     * 添加 该笔 详情记录到 sOneTypeAllRecordsInDateMap 和 总额到 sOneTypeTotalRecordInDateMap
     *
     * @param recordDetail 添加的单笔详情记录
     */
    @SuppressWarnings("unchecked")
    private static void addRecordToOneTypeAllRecordsInDateMap(RecordDetail recordDetail, boolean isSort) {
        String year = recordDetail.getYear();
        String month = recordDetail.getYear() + "-" + recordDetail.getMonth();
        String weekOfYear = recordDetail.getYear() + "-" + recordDetail.getWeekOfYear();
        String ymd = month + "-" + recordDetail.getDay();
        String type = recordDetail.getDetailType();
        String money = recordDetail.getMoney();

        if (sOneTypeAllRecordsInDateMap.containsKey(type)) {
            Map<String, Map<String, List<RecordDetail>>> dateTypeMap = sOneTypeAllRecordsInDateMap.get(type);
            Map<String, List<RecordDetail>> recordsInYearMap = dateTypeMap.get(Constants.YEAR);
            Map<String, List<RecordDetail>> recordsInMonthMap = dateTypeMap.get(Constants.MONTH);
            Map<String, List<RecordDetail>> recordsInWeekMap = dateTypeMap.get(Constants.WEEK);
            Map<String, List<RecordDetail>> recordsInDayMap = dateTypeMap.get(Constants.DAY);
            List<RecordDetail> recordsInYearList;
            if (!recordsInYearMap.containsKey(year)) {
                recordsInYearList = new ArrayList<>();
                recordsInYearMap.put(year, recordsInYearList);
            } else {
                recordsInYearList = recordsInYearMap.get(year);
            }
            List<RecordDetail> recordsInMonthList;
            if (!recordsInMonthMap.containsKey(month)) {
                recordsInMonthList = new ArrayList<>();
                recordsInMonthMap.put(month, recordsInMonthList);
            } else {
                recordsInMonthList = recordsInMonthMap.get(month);
            }
            List<RecordDetail> recordsInWeekList;
            if (!recordsInWeekMap.containsKey(weekOfYear)) {
                recordsInWeekList = new ArrayList<>();
                recordsInWeekMap.put(weekOfYear, recordsInWeekList);
            } else {
                recordsInWeekList = recordsInWeekMap.get(weekOfYear);
            }
            List<RecordDetail> recordsInDayList;
            if (!recordsInDayMap.containsKey(ymd)) {
                recordsInDayList = new ArrayList<>();
                recordsInDayMap.put(ymd, recordsInDayList);
            } else {
                recordsInDayList = recordsInDayMap.get(ymd);
            }
            recordsInYearList.add(recordDetail);
            recordsInMonthList.add(recordDetail);
            recordsInWeekList.add(recordDetail);
            recordsInDayList.add(recordDetail);
            if (isSort) {
                Collections.sort(recordsInYearList, sRecordDetailMoneyDesComparator);
                Collections.sort(recordsInMonthList, sRecordDetailMoneyDesComparator);
                Collections.sort(recordsInWeekList, sRecordDetailMoneyDesComparator);
                Collections.sort(recordsInDayList, sRecordDetailMoneyDesComparator);
            }
        } else {
            Map<String, Map<String, List<RecordDetail>>> dateTypeMap = new HashMap<>(4);
            Map<String, List<RecordDetail>> recordsInYearMap = new HashMap<>(1);
            Map<String, List<RecordDetail>> recordsInMonthMap = new HashMap<>(1);
            Map<String, List<RecordDetail>> recordsInWeekMap = new HashMap<>(1);
            Map<String, List<RecordDetail>> recordsInDayMap = new HashMap<>(1);
            List<RecordDetail> recordsInYearList = new ArrayList<>();
            List<RecordDetail> recordsInMonthList = new ArrayList<>();
            List<RecordDetail> recordsInWeekList = new ArrayList<>();
            List<RecordDetail> recordsInDayList = new ArrayList<>();
            recordsInYearList.add(recordDetail);
            recordsInMonthList.add(recordDetail);
            recordsInWeekList.add(recordDetail);
            recordsInDayList.add(recordDetail);
            recordsInYearMap.put(year, recordsInYearList);
            recordsInMonthMap.put(month, recordsInMonthList);
            recordsInWeekMap.put(weekOfYear, recordsInWeekList);
            recordsInDayMap.put(ymd, recordsInDayList);
            dateTypeMap.put(Constants.YEAR, recordsInYearMap);
            dateTypeMap.put(Constants.MONTH, recordsInMonthMap);
            dateTypeMap.put(Constants.WEEK, recordsInWeekMap);
            dateTypeMap.put(Constants.DAY, recordsInDayMap);
            sOneTypeAllRecordsInDateMap.put(type, dateTypeMap);
        }

        if (sOneTypeTotalRecordInDateMap.containsKey(type)) {
            Map<String, Map<String, String>> dateTypeMap = sOneTypeTotalRecordInDateMap.get(type);
            Map<String, String> totalRecordInYearMap = dateTypeMap.get(Constants.YEAR);
            Map<String, String> totalRecordInMonthMap = dateTypeMap.get(Constants.MONTH);
            Map<String, String> totalRecordInWeekMap = dateTypeMap.get(Constants.WEEK);
            Map<String, String> totalRecordInDayMap = dateTypeMap.get(Constants.DAY);

            if (!totalRecordInYearMap.containsKey(year)) {
                totalRecordInYearMap.put(year, money);
            } else {
                totalRecordInYearMap.put(year, getTotalMoeny("sub", totalRecordInYearMap.get(year), money));
            }
            if (!totalRecordInMonthMap.containsKey(month)) {
                totalRecordInMonthMap.put(month, money);
            } else {
                totalRecordInMonthMap.put(month, getTotalMoeny("add", totalRecordInMonthMap.get(month), money));
            }
            if (!totalRecordInWeekMap.containsKey(weekOfYear)) {
                totalRecordInWeekMap.put(weekOfYear, money);
            } else {
                totalRecordInWeekMap.put(weekOfYear, getTotalMoeny("add", totalRecordInWeekMap.get(weekOfYear), money));
            }
            if (!totalRecordInDayMap.containsKey(ymd)) {
                totalRecordInDayMap.put(ymd, money);
            } else {
                totalRecordInDayMap.put(ymd, getTotalMoeny("add", totalRecordInDayMap.get(ymd), money));
            }
        } else {
            Map<String, Map<String, String>> dateTypeMap = new HashMap<>(4);
            // 使用TreeMap，按时间升序，最早 到 最晚
            Map<String, String> totalRecordInYearMap = new TreeMap<>(sYearAscComparator);
            Map<String, String> totalRecordInMonthMap = new TreeMap<>(sMonthAscComparator);
            Map<String, String> totalRecordInWeekMap = new TreeMap<>(sWeekAscComparator);
            Map<String, String> totalRecordInDayMap = new TreeMap<>(sDayAscComparator);
            totalRecordInYearMap.put(year, money);
            totalRecordInMonthMap.put(month, money);
            totalRecordInWeekMap.put(weekOfYear, money);
            totalRecordInDayMap.put(ymd, money);
            dateTypeMap.put(Constants.YEAR, totalRecordInYearMap);
            dateTypeMap.put(Constants.MONTH, totalRecordInMonthMap);
            dateTypeMap.put(Constants.WEEK, totalRecordInWeekMap);
            dateTypeMap.put(Constants.DAY, totalRecordInDayMap);
            sOneTypeTotalRecordInDateMap.put(type, dateTypeMap);
        }
    }

    /**
     * 添加 总支出 到列表
     *
     * @param list       总支出添加列表
     * @param detailType 记录类别（吃饭、娱乐。。）
     * @param iconUrl    记录icon
     * @param money      总费用
     */
    private static void addTotalExpendToList(RealmList<TotalExpend> list,
                                             String detailType, int iconUrl, String money) {
        TotalExpend totalExpend = new TotalExpend();
        totalExpend.setType(detailType);
        totalExpend.setIconUrl(iconUrl);
        totalExpend.setTotalEpend(money);
        list.add(totalExpend);
    }

    /**
     * 添加 总收入 到列表
     *
     * @param list       总收入添加列表
     * @param detailType 记录类别（吃饭、娱乐。。）
     * @param iconUrl    记录icon
     * @param money      总费用
     */
    private static void addTotalIncomeToList(RealmList<TotalIncome> list,
                                             String detailType, int iconUrl, String money) {
        TotalIncome totalIncome = new TotalIncome();
        totalIncome.setType(detailType);
        totalIncome.setIconUrl(iconUrl);
        totalIncome.setTotalIncome(money);
        list.add(totalIncome);
    }
}
