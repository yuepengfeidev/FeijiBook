package com.example.feijibook.util;

import android.support.annotation.NonNull;

import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_bean.WeekRecord;
import com.example.feijibook.entity.record_bean.YearRecord;

import java.util.Map;

import io.realm.Realm;

/**
 * RealmUtils
 *
 * @author PengFei Yue
 * @date 2019/9/23
 * @description
 */
public class RealmUtils {

    /**
     * 保存静态变量
     */
    public static void saveRecord(final RecordDetail recordDetail) {
        String day = recordDetail.getDay();
        final String weekOfYear = recordDetail.getWeekOfYear();
        String month = recordDetail.getMonth();
        final String year = recordDetail.getYear();

        final String moy = year + "-" + month;
        final String woy = year + "-" + weekOfYear;
        final String ymd = moy + "-" + day;

        final Map<String, DayRecord> dayRecordMap = RecordListUtils.getAllDayRecordsMap();
        final Map<String, WeekRecord> weekRecordMap = RecordListUtils.getAllWeekRecordsMap();
        final Map<String, MonthRecord> monthRecordMap = RecordListUtils.getAllMonthRecordsMap();
        final Map<String, YearRecord> yearRecordMap = RecordListUtils.getAllYearRecordsMap();

        final Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.insert(recordDetail);
                DayRecord dayRecord = realm.where(DayRecord.class).
                        equalTo("id", ymd)
                        .findFirst();
                if (dayRecord != null) {
                    String totalIncome = dayRecordMap.get(ymd).getDayTotalIncome();
                    String totalExpend = dayRecordMap.get(ymd).getDayTotalExpend();
                    dayRecord.setDayTotalIncome(checkNull(totalIncome));
                    dayRecord.setDayTotalExpend(checkNull(totalExpend));
                } else {
                    realm.insert(dayRecordMap.get(ymd));
                }

                WeekRecord weekRecord = realm.where(WeekRecord.class).equalTo("id", woy)
                        .findFirst();
                if (weekRecord != null) {
                    String totalIncome = weekRecordMap.get(woy).getTotalIncome();
                    String totalExpend = weekRecordMap.get(woy).getTotalExpend();
                    weekRecord.setTotalExpend(checkNull(totalExpend));
                    weekRecord.setTotalIncome(checkNull(totalIncome));
                    weekRecord.getWeekTotalIncomes().clear();
                    weekRecord.getWeekTotalExpends().clear();
                    weekRecord.getWeekTotalIncomes().addAll(weekRecordMap.get(woy).getWeekTotalIncomes());
                    weekRecord.getWeekTotalExpends().addAll(weekRecordMap.get(woy).getWeekTotalExpends());
                } else {
                    realm.insert(weekRecordMap.get(woy));
                }

                MonthRecord monthRecord = realm.where(MonthRecord.class).equalTo("id", moy)
                        .findFirst();
                if (monthRecord != null) {
                    String totalIncome = monthRecordMap.get(moy).getTotalIncome();
                    String totalExpend = monthRecordMap.get(moy).getTotalExpend();
                    monthRecord.setTotalIncome(checkNull(totalIncome));
                    monthRecord.setTotalExpend(checkNull(totalExpend));
                    monthRecord.getMonthTotalExpends().clear();
                    monthRecord.getMonthTotalIncomes().clear();
                    monthRecord.getMonthTotalIncomes().addAll(monthRecordMap.get(moy).getMonthTotalIncomes());
                    monthRecord.getMonthTotalExpends().addAll(monthRecordMap.get(moy).getMonthTotalExpends());
                } else {
                    realm.insert(monthRecordMap.get(moy));
                }

                YearRecord yearRecord = realm.where(YearRecord.class).equalTo("id", year)
                        .findFirst();
                if (yearRecord != null) {
                    String totalIncome = yearRecordMap.get(year).getTotalIncome();
                    String totalExpend = yearRecordMap.get(year).getTotalExpend();
                    yearRecord.setTotalIncome(checkNull(totalIncome));
                    yearRecord.setTotalExpend(checkNull(totalExpend));
                    yearRecord.getYearTotalIncomes().clear();
                    yearRecord.getYearTotalExpends().clear();
                    yearRecord.getYearTotalIncomes().addAll(yearRecordMap.get(year).getYearTotalIncomes());
                    yearRecord.getYearTotalExpends().addAll(yearRecordMap.get(year).getYearTotalExpends());
                } else {
                    realm.insert(yearRecordMap.get(year));
                }

                mRealm.close();
            }
        });
    }

    /**
     * 删除当前记录
     */
    public static void deleteRecord(final RecordDetail recordDetail) {
        final String id = recordDetail.getId();
        String day = recordDetail.getDay();
        final String weekOfYear = recordDetail.getWeekOfYear();
        String month = recordDetail.getMonth();
        final String year = recordDetail.getYear();

        final String moy = year + "-" + month;
        final String woy = year + "-" + weekOfYear;
        final String ymd = moy + "-" + day;

        final Map<String, DayRecord> dayRecordMap = RecordListUtils.getAllDayRecordsMap();
        final Map<String, WeekRecord> weekRecordMap = RecordListUtils.getAllWeekRecordsMap();
        final Map<String, MonthRecord> monthRecordMap = RecordListUtils.getAllMonthRecordsMap();
        final Map<String, YearRecord> yearRecordMap = RecordListUtils.getAllYearRecordsMap();

        final Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RecordDetail record = realm.where(RecordDetail.class).equalTo("id", id)
                        .findFirst();
                record.deleteFromRealm();

                DayRecord dayRecord = realm.where(DayRecord.class).equalTo("id", ymd)
                        .findFirst();
                if (dayRecordMap.containsKey(ymd)) {
                    String totalIncome = dayRecordMap.get(ymd).getDayTotalIncome();
                    String totalExpend = dayRecordMap.get(ymd).getDayTotalExpend();
                    dayRecord.setDayTotalIncome(checkNull(totalIncome));
                    dayRecord.setDayTotalExpend(checkNull(totalExpend));
                } else {
                    dayRecord.deleteFromRealm();
                }

                WeekRecord weekRecord = realm.where(WeekRecord.class).equalTo("id", woy)
                        .findFirst();
                if (weekRecordMap.containsKey(woy)) {
                    String totalIncome = weekRecordMap.get(woy).getTotalIncome();
                    String totalExpend = weekRecordMap.get(woy).getTotalExpend();
                    weekRecord.setTotalExpend(checkNull(totalExpend));
                    weekRecord.setTotalIncome(checkNull(totalIncome));
                    weekRecord.getWeekTotalIncomes().clear();
                    weekRecord.getWeekTotalExpends().clear();
                    weekRecord.getWeekTotalIncomes().addAll(weekRecordMap.get(woy).getWeekTotalIncomes());
                    weekRecord.getWeekTotalExpends().addAll(weekRecordMap.get(woy).getWeekTotalExpends());
                } else {
                    weekRecord.deleteFromRealm();
                }

                MonthRecord monthRecord = realm.where(MonthRecord.class).equalTo("id", moy)
                        .findFirst();
                if (monthRecordMap.containsKey(moy)) {
                    String totalIncome = monthRecordMap.get(moy).getTotalIncome();
                    String totalExpend = monthRecordMap.get(moy).getTotalExpend();
                    monthRecord.setTotalIncome(checkNull(totalIncome));
                    monthRecord.setTotalExpend(checkNull(totalExpend));
                    monthRecord.getMonthTotalExpends().clear();
                    monthRecord.getMonthTotalIncomes().clear();
                    monthRecord.getMonthTotalIncomes().addAll(monthRecordMap.get(moy).getMonthTotalIncomes());
                    monthRecord.getMonthTotalExpends().addAll(monthRecordMap.get(moy).getMonthTotalExpends());
                } else {
                    monthRecord.deleteFromRealm();
                }

                YearRecord yearRecord = realm.where(YearRecord.class).equalTo("id", year)
                        .findFirst();
                if (yearRecordMap.containsKey(year)) {
                    String totalIncome = yearRecordMap.get(year).getTotalIncome();
                    String totalExpend = yearRecordMap.get(year).getTotalExpend();
                    yearRecord.setTotalIncome(checkNull(totalIncome));
                    yearRecord.setTotalExpend(checkNull(totalExpend));
                    yearRecord.getYearTotalIncomes().clear();
                    yearRecord.getYearTotalExpends().clear();
                    yearRecord.getYearTotalIncomes().addAll(yearRecordMap.get(year).getYearTotalIncomes());
                    yearRecord.getYearTotalExpends().addAll(yearRecordMap.get(year).getYearTotalExpends());
                } else {
                    yearRecord.deleteFromRealm();
                }

                mRealm.close();
            }
        });
    }

    /**
     * 改变编辑后的记录
     */
    public static void changRecord() {
        final RecordDetail resRecord = RecordListUtils.getResRecord();
        final RecordDetail desRecord = RecordListUtils.getIsEditingRecord();
        String resDay = resRecord.getDay();
        String resWeekOfYear = resRecord.getWeekOfYear();
        String resMonth = resRecord.getMonth();
        final String resYear = resRecord.getYear();

        final String desDay = desRecord.getDay();
        String desWeekOfYear = desRecord.getWeekOfYear();
        String desMonth = desRecord.getMonth();
        final String desYear = desRecord.getYear();

        final String resMOY = resYear + "-" + resMonth;
        final String resWOY = resYear + "-" + resWeekOfYear;
        final String resYMD = resMOY + "-" + resDay;

        final String desMOY = desYear + "-" + desMonth;
        final String desWOY = desYear + "-" + desWeekOfYear;
        final String desYMD = desMOY + "-" + desDay;

        final Map<String, DayRecord> dayRecordMap = RecordListUtils.getAllDayRecordsMap();
        final Map<String, WeekRecord> weekRecordMap = RecordListUtils.getAllWeekRecordsMap();
        final Map<String, MonthRecord> monthRecordMap = RecordListUtils.getAllMonthRecordsMap();
        final Map<String, YearRecord> yearRecordMap = RecordListUtils.getAllYearRecordsMap();

        final Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RecordDetail record = realm.where(RecordDetail.class)
                        .equalTo("id", resRecord.getId()).findFirst();
                // 删除原记录
                record.deleteFromRealm();
                // 添加编辑后的记录
                realm.insert(desRecord);

                DayRecord dayRecord = realm.where(DayRecord.class)
                        .equalTo("id", resYMD).findFirst();
                if (resYMD.equals(desYMD) && dayRecordMap.containsKey(resYMD)) {
                    // 如果修改记录时没有修改日期，则直接修改原纪录
                    String totalIncome = dayRecordMap.get(resYMD).getDayTotalIncome();
                    String totalExpend = dayRecordMap.get(resYMD).getDayTotalExpend();
                    dayRecord.setDayTotalIncome(checkNull(totalIncome));
                    dayRecord.setDayTotalExpend(checkNull(totalExpend));
                } else {
                    if (dayRecordMap.containsKey(resYMD)) {
                        // 修改日期后，有原数据则修改数据
                        String totalIncome = dayRecordMap.get(resYMD).getDayTotalIncome();
                        String totalExpend = dayRecordMap.get(resYMD).getDayTotalExpend();
                        dayRecord.setDayTotalIncome(checkNull(totalIncome));
                        dayRecord.setDayTotalExpend(checkNull(totalExpend));
                    } else {
                        // 没有原数据，则删除原数据
                        dayRecord.deleteFromRealm();
                    }

                    DayRecord desDayRecord = realm.where(DayRecord.class)
                            .equalTo("id", desYMD).findFirst();
                    if (desDayRecord != null) {
                        // 如果修改到的当天日期有数据，则修改
                        // 修改日期后，有原数据则修改数据
                        String totalIncome = dayRecordMap.get(desYMD).getDayTotalIncome();
                        String totalExpend = dayRecordMap.get(desYMD).getDayTotalExpend();
                        desDayRecord.setDayTotalIncome(checkNull(totalIncome));
                        desDayRecord.setDayTotalExpend(checkNull(totalExpend));
                    } else {
                        // 没有当天的数据，则插入
                        DayRecord curDayRecord = dayRecordMap.get(desYMD);
                        realm.insert(curDayRecord);
                    }
                }


                WeekRecord weekRecord = realm.where(WeekRecord.class)
                        .equalTo("id", resWOY).findFirst();
                if (resWOY.equals(desWOY) && weekRecordMap.containsKey(resWOY)) {
                    // 如果修改记录时没有修改日期，则直接修改原纪录
                    String totalIncome = weekRecordMap.get(resWOY).getTotalIncome();
                    String totalExpend = weekRecordMap.get(resWOY).getTotalExpend();
                    weekRecord.setTotalIncome(checkNull(totalIncome));
                    weekRecord.setTotalExpend(checkNull(totalExpend));
                } else {
                    if (weekRecordMap.containsKey(resWOY)) {
                        // 修改日期后，有原数据则修改数据
                        String totalIncome = weekRecordMap.get(resWOY).getTotalIncome();
                        String totalExpend = weekRecordMap.get(resWOY).getTotalExpend();
                        weekRecord.setTotalIncome(checkNull(totalIncome));
                        weekRecord.setTotalExpend(checkNull(totalExpend));
                        weekRecord.getWeekTotalIncomes().clear();
                        weekRecord.getWeekTotalExpends().clear();
                        weekRecord.getWeekTotalIncomes().addAll(weekRecordMap.get(resWOY).getWeekTotalIncomes());
                        weekRecord.getWeekTotalExpends().addAll(weekRecordMap.get(resWOY).getWeekTotalExpends());
                    } else {
                        // 没有原数据，则删除原数据
                        weekRecord.deleteFromRealm();
                    }

                    WeekRecord desWeekRecord = realm.where(WeekRecord.class)
                            .equalTo("id", desWOY).findFirst();
                    if (desWeekRecord != null) {
                        // 如果修改到的当天日期有数据，则修改
                        // 修改日期后，有原数据则修改数据
                        String totalIncome = weekRecordMap.get(desWOY).getTotalIncome();
                        String totalExpend = weekRecordMap.get(desWOY).getTotalExpend();
                        desWeekRecord.setTotalIncome(checkNull(totalIncome));
                        desWeekRecord.setTotalExpend(checkNull(totalExpend));
                        desWeekRecord.getWeekTotalIncomes().clear();
                        desWeekRecord.getWeekTotalExpends().clear();
                        desWeekRecord.getWeekTotalIncomes().addAll(weekRecordMap.get(desWOY).getWeekTotalIncomes());
                        desWeekRecord.getWeekTotalExpends().addAll(weekRecordMap.get(desWOY).getWeekTotalExpends());
                    } else {
                        // 没有当周的数据，则插入
                        WeekRecord curWeekRecord = weekRecordMap.get(desWOY);
                        realm.insert(curWeekRecord);
                    }
                }

                MonthRecord monthRecord = realm.where(MonthRecord.class)
                        .equalTo("id", resMOY).findFirst();
                if (resMOY.equals(desMOY) && monthRecordMap.containsKey(resMOY)) {
                    // 如果修改记录时没有修改日期，则直接修改原纪录
                    String totalIncome = monthRecordMap.get(resMOY).getTotalIncome();
                    String totalExpend = monthRecordMap.get(resMOY).getTotalExpend();
                    monthRecord.setTotalIncome(checkNull(totalIncome));
                    monthRecord.setTotalExpend(checkNull(totalExpend));
                } else {
                    if (monthRecordMap.containsKey(resMOY)) {
                        // 修改日期后，有原数据则修改数据
                        String totalIncome = monthRecordMap.get(resMOY).getTotalIncome();
                        String totalExpend = monthRecordMap.get(resMOY).getTotalExpend();
                        monthRecord.setTotalIncome(checkNull(totalIncome));
                        monthRecord.setTotalExpend(checkNull(totalExpend));
                        monthRecord.getMonthTotalExpends().clear();
                        monthRecord.getMonthTotalIncomes().clear();
                        monthRecord.getMonthTotalIncomes().addAll(monthRecordMap.get(resMOY).getMonthTotalIncomes());
                        monthRecord.getMonthTotalExpends().addAll(monthRecordMap.get(resMOY).getMonthTotalExpends());
                    } else {
                        // 没有原数据，则删除原数据
                        monthRecord.deleteFromRealm();
                    }

                    MonthRecord desMonthRecord = realm.where(MonthRecord.class)
                            .equalTo("id", desMOY).findFirst();
                    if (desMonthRecord != null) {
                        // 如果修改到的当天日期有数据，则修改
                        // 修改日期后，有原数据则修改数据
                        String totalIncome = monthRecordMap.get(desMOY).getTotalIncome();
                        String totalExpend = monthRecordMap.get(desMOY).getTotalExpend();
                        desMonthRecord.setTotalIncome(checkNull(totalIncome));
                        desMonthRecord.setTotalExpend(checkNull(totalExpend));
                        desMonthRecord.getMonthTotalIncomes().clear();
                        desMonthRecord.getMonthTotalExpends().clear();
                        desMonthRecord.getMonthTotalIncomes().addAll(monthRecordMap.get(desMOY).getMonthTotalIncomes());
                        desMonthRecord.getMonthTotalExpends().addAll(monthRecordMap.get(desMOY).getMonthTotalExpends());
                    } else {
                        // 没有当月的数据，则插入
                        MonthRecord curWeekRecord = monthRecordMap.get(desMOY);
                        realm.insert(curWeekRecord);
                    }
                }

                YearRecord yearRecord = realm.where(YearRecord.class)
                        .equalTo("id", resYear).findFirst();
                if (resYear.equals(desYear) && yearRecordMap.containsKey(resYear)) {
                    // 如果修改记录时没有修改日期，则直接修改原纪录
                    String totalIncome = yearRecordMap.get(resYear).getTotalIncome();
                    String totalExpend = yearRecordMap.get(resYear).getTotalExpend();
                    yearRecord.setTotalIncome(checkNull(totalIncome));
                    yearRecord.setTotalExpend(checkNull(totalExpend));
                } else {
                    if (yearRecordMap.containsKey(resYear)) {
                        // 修改日期后，有原数据则修改数据
                        String totalIncome = yearRecordMap.get(resYear).getTotalIncome();
                        String totalExpend = yearRecordMap.get(resYear).getTotalExpend();
                        yearRecord.setTotalIncome(checkNull(totalIncome));
                        yearRecord.setTotalExpend(checkNull(totalExpend));
                        yearRecord.getYearTotalExpends().clear();
                        yearRecord.getYearTotalIncomes().clear();
                        yearRecord.getYearTotalIncomes().addAll(yearRecordMap.get(resYear).getYearTotalIncomes());
                        yearRecord.getYearTotalExpends().addAll(yearRecordMap.get(resYear).getYearTotalExpends());
                    } else {
                        // 没有原数据，则删除原数据
                        yearRecord.deleteFromRealm();
                    }

                    YearRecord desYearRecord = realm.where(YearRecord.class)
                            .equalTo("id", desYear).findFirst();
                    if (desYearRecord != null) {
                        // 如果修改到的当天日期有数据，则修改
                        // 修改日期后，有原数据则修改数据
                        String totalIncome = yearRecordMap.get(desYear).getTotalIncome();
                        String totalExpend = yearRecordMap.get(desYear).getTotalExpend();
                        desYearRecord.setTotalIncome(checkNull(totalIncome));
                        desYearRecord.setTotalExpend(checkNull(totalExpend));
                        desYearRecord.getYearTotalIncomes().clear();
                        desYearRecord.getYearTotalExpends().clear();
                        desYearRecord.getYearTotalIncomes().addAll(yearRecordMap.get(desYear).getYearTotalIncomes());
                        desYearRecord.getYearTotalExpends().addAll(yearRecordMap.get(desYear).getYearTotalExpends());
                    } else {
                        // 没有当年的数据，则插入
                        YearRecord curYearRecord = yearRecordMap.get(desYear);
                        realm.insert(curYearRecord);
                    }
                }
                mRealm.close();
            }
        });
    }

    /**
     * 如果金额为空，则返回“0”
     */
    public static String checkNull(String str) {
        if (str != null) {
            return str;
        } else {
            return "0";
        }
    }
}
