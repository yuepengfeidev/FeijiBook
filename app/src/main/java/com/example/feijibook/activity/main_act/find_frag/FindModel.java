package com.example.feijibook.activity.main_act.find_frag;

import com.example.feijibook.app.Constants;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.UserSettingBean;
import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.weather_bean.ResultBean;
import com.example.feijibook.http.HttpApi;
import com.example.feijibook.http.SchedulersCompat;
import com.example.feijibook.util.RecordListUtils;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.realm.Realm;
import okhttp3.ResponseBody;

/**
 * Created by 你是我的 on 2019/3/11
 */
public class FindModel implements FindContract.Model {
    @Named("Weather")
    @Inject
    HttpApi mHttpApi;

    public FindModel() {
        MyApplication.getHttpComponent().injectFindModel(this);
    }

    @Override
    public Map<String, String> getCurMonthTotalRecord(String month) {
        MonthRecord monthRecord = RecordListUtils.getMonthRecordRankMap().get(month);
        Map<String, String> map = new HashMap<>(2);
        String totalExpend;
        String totalIncome;
        if (monthRecord == null) {
            totalExpend = "0.00";
            totalIncome = "0.00";
        } else {
            if (monthRecord.getTotalIncome() == null) {
                totalIncome = "0.00";
            } else {
                totalIncome = monthRecord.getTotalIncome();
            }
            if (monthRecord.getTotalExpend() == null) {
                totalExpend = "0.00";
            } else {
                totalExpend = monthRecord.getTotalExpend();
            }
        }
        map.put(Constants.EXPEND, totalExpend);
        map.put(Constants.INCOME, totalIncome);
        return map;
    }

    @Override
    public String getCurMonthBudgetMoney() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        UserSettingBean userSettingBean = realm.where(UserSettingBean.class).findFirst();
        realm.commitTransaction();
        realm.close();
        return userSettingBean.getBudget();
    }

    @Override
    public void setSaveMonthBudgetMoney(String budgetMoney) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        UserSettingBean userSettingBean = realm.where(UserSettingBean.class).findFirst();
        userSettingBean.setBudget(budgetMoney);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public Observable<ResponseBody> requestWeather(String city) {
        return mHttpApi.getWeather(city, MyApplication.WEATHER_KEY).compose(SchedulersCompat.applyToSchedulers());
    }

    @Override
    public ResultBean getWeather() {
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        ResultBean resultBean = mRealm.where(ResultBean.class).findFirst();
        mRealm.commitTransaction();
        mRealm.close();
        return resultBean;
    }

    @Override
    public void saveWeather(ResultBean resultBean) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(ResultBean.class).findAll().deleteAllFromRealm();
        realm.copyToRealmOrUpdate(resultBean);
        realm.commitTransaction();
        realm.close();
    }
}
