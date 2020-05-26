package com.example.feijibook.activity.main_act.me_frag;

import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.UserSettingBean;
import com.example.feijibook.http.HttpApi;
import com.example.feijibook.http.SchedulersCompat;
import com.example.feijibook.util.RecordListUtils;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.realm.Realm;

/**
 * Created by 你是我的 on 2019/3/11
 */
public class MeModel implements MeContract.Model {
    @Named("Scalars")
    @Inject
    HttpApi mHttpApi;

    public MeModel() {
        MyApplication.getHttpComponent().injectMeModel(this);
    }

    @Override
    public Map<String, Integer> getDayAndRecordCount() {
        Map<String, Integer> map = new HashMap<>(2);
        map.put("dayCount", RecordListUtils.getAllDayRecordsMap().size());
        map.put("recordCount", RecordListUtils.getAllRecordDetailsMap().size());
        return map;
    }

    @Override
    public String getGesturePw() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        UserSettingBean userSettingBean = realm.where(UserSettingBean.class).findFirst();
        realm.commitTransaction();
        realm.close();
        return userSettingBean.getGesturePw();
    }

    @Override
    public boolean isSoundOpen() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        UserSettingBean userSettingBean = realm.where(UserSettingBean.class).findFirst();
        realm.commitTransaction();
        realm.close();
        return userSettingBean.isOpenSound();
    }

    @Override
    public void saveSoundOpen(boolean isOpen) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        UserSettingBean userSettingBean = realm.where(UserSettingBean.class).findFirst();
        userSettingBean.setOpenSound(isOpen);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public Observable<String> requestChangePw(String account, String resPw, String newPw) {
        return mHttpApi.changePassword(account, resPw, newPw).compose(SchedulersCompat.applyToSchedulers());
    }

}
