package com.example.feijibook.activity.main_act;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.UserSettingBean;
import com.example.feijibook.entity.gson_bean.UserInfoBean;
import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_bean.WeekRecord;
import com.example.feijibook.entity.record_bean.YearRecord;
import com.example.feijibook.entity.record_type_bean.CategoryType;
import com.example.feijibook.entity.record_type_bean.AddtiveType;
import com.example.feijibook.entity.record_type_bean.OptionalType;
import com.example.feijibook.http.HttpApi;
import com.example.feijibook.http.SchedulersCompat;
import com.example.feijibook.util.RecordListUtils;
import com.example.feijibook.util.SetTypeListUtil;
import com.example.feijibook.util.SharedPreferencesUtils;
import com.example.feijibook.util.SoundShakeUtil;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.ResponseBody;

/**
 * Created by 你是我的 on 2019/3/29
 */
public class MainModel implements MainContract.Model {
    @Named("Gson")
    @Inject
    HttpApi mHttpApi;

    public MainModel() {
        MyApplication.getHttpComponent().injectMainModel(this);
    }

    @Override
    public boolean checkFirstLogin(Activity activity) {
        // 获取不到则为第一次登陆，为true
        boolean isFirstLogin = SharedPreferencesUtils.getBoolFromSp(SharedPreferencesUtils.FIRST_LOGIN);
        if (isFirstLogin) {
            SharedPreferencesUtils.saveBoolToSp(SharedPreferencesUtils.FIRST_LOGIN, false);
            return true;
        }
        return false;
    }

    @Override
    public RealmAsyncTask saveDefaultChooseType(Realm realm) {
        RealmList<OptionalType> expendOptionalTypeRealmList = new RealmList<>();
        expendOptionalTypeRealmList.addAll(SetTypeListUtil.getOptionalTypeList("支出"));
        RealmList<AddtiveType> expendAddtibleTypeRealmList = new RealmList<>();
        expendAddtibleTypeRealmList.addAll(SetTypeListUtil.getAddibleTypeList("支出"));
        RealmList<OptionalType> incomeOptionTypeRealmList = new RealmList<>();
        incomeOptionTypeRealmList.addAll(SetTypeListUtil.getOptionalTypeList("收入"));
        RealmList<AddtiveType> incomeAddibleTypeRealmList = new RealmList<>();
        incomeAddibleTypeRealmList.addAll(SetTypeListUtil.getAddibleTypeList("收入"));
        final CategoryType expendCategoryType = new CategoryType();
        expendCategoryType.setType("支出");
        expendCategoryType.setAddtiveTypeRealmList(expendAddtibleTypeRealmList);
        expendCategoryType.setOptionalTypeRealmList(expendOptionalTypeRealmList);
        final CategoryType inComeCategoryType = new CategoryType();
        inComeCategoryType.setType("收入");
        inComeCategoryType.setAddtiveTypeRealmList(incomeAddibleTypeRealmList);
        inComeCategoryType.setOptionalTypeRealmList(incomeOptionTypeRealmList);

        SetTypeListUtil.setTypeList("收入", incomeOptionTypeRealmList, incomeAddibleTypeRealmList);
        SetTypeListUtil.setTypeList("支出", expendOptionalTypeRealmList, expendAddtibleTypeRealmList);

        return realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.insertOrUpdate(expendCategoryType);
                realm.insertOrUpdate(inComeCategoryType);
            }
        });
    }

    @Override
    public void setTypeList(RealmResults<CategoryType> categoryTypes, Realm realm) {
        // 从数据库中获取数据
        CategoryType expend = realm.copyFromRealm(categoryTypes).get(0);
        CategoryType income = realm.copyFromRealm(categoryTypes).get(1);
        List<OptionalType> expendOptionalTypes = expend.getOptionalTypeRealmList();
        List<AddtiveType> expendAddibleTypes = expend.getAddtiveTypeRealmList();
        List<OptionalType> incomeOptionalTypes = income.getOptionalTypeRealmList();
        List<AddtiveType> incomeAddtiveTypes = income.getAddtiveTypeRealmList();

        SetTypeListUtil.setTypeList("支出", expendOptionalTypes, expendAddibleTypes);
        SetTypeListUtil.setTypeList("收入", incomeOptionalTypes, incomeAddtiveTypes);
    }

    @Override
    public void setRecordDetailTypeList(RealmResults<RecordDetail> recordDetails, Realm realm) {
        List<RecordDetail> list = realm.copyFromRealm(recordDetails);
        RecordListUtils.setRecordDetailList(list);
    }

    @Override
    public void setDayRecordTypeList(RealmResults<DayRecord> dayRecords, Realm realm) {
        List<DayRecord> list = realm.copyFromRealm(dayRecords);
        RecordListUtils.setDayRecordList(list);
    }

    @Override
    public void setWeekRecordTypeList(RealmResults<WeekRecord> weekRecords, Realm realm) {
        List<WeekRecord> list = realm.copyFromRealm(weekRecords);
        RecordListUtils.setWeekRecordList(list);
    }

    @Override
    public void setMonthRecordTypeList(RealmResults<MonthRecord> monthRecords, Realm realm) {
        List<MonthRecord> list = realm.copyFromRealm(monthRecords);
        RecordListUtils.setMonthRecordList(list);
    }

    @Override
    public void setYearRecordTypeList(RealmResults<YearRecord> yearRecords, Realm realm) {
        List<YearRecord> list = realm.copyFromRealm(yearRecords);
        RecordListUtils.setYearRecordList(list);
    }

    @Override
    public Observable<UserInfoBean> requestUserInfo(String account) {
        return mHttpApi.getUserInfo(account).compose(SchedulersCompat.applyToSchedulers());
    }

    @Override
    public Observable<ResponseBody> downLoadPortrait(String url) {
        return mHttpApi.downloadPortrait(url).compose(SchedulersCompat.applyToSchedulers2());
    }

    @Override
    public void saveUserSetting() {
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserSettingBean usb = realm.where(UserSettingBean.class).findFirst();
                if (usb == null) {
                    // 没有设置记录，则存储默认设置
                    UserSettingBean userSettingBean = new UserSettingBean();
                    userSettingBean.setAccount(SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT));
                    userSettingBean.setBudget("");
                    userSettingBean.setGesturePw("");
                    userSettingBean.setOpenSound(true);
                    realm.insert(userSettingBean);
                }
                realm.close();
            }
        });
    }
}
