package com.example.feijibook.activity.login_in_act;

import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.UserSettingBean;
import com.example.feijibook.http.HttpApi;
import com.example.feijibook.http.SchedulersCompat;
import com.example.feijibook.util.SharedPreferencesUtils;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.realm.Realm;

/**
 * LIModel
 *
 * @author PengFei Yue
 * @date 2019/10/6
 * @description
 */
public class LIModel implements LIContract.Model {
    @Named("Scalars")
    @Inject
    HttpApi mHttpApi;

    LIModel() {
        MyApplication.getHttpComponent().injectLoginInModel(this);
    }

    @Override
    public boolean isFirstLogin() {
        return SharedPreferencesUtils.getBoolFromSp(SharedPreferencesUtils.FIRST_LOGIN);
    }

    @Override
    public boolean isRememberPassword() {
        return SharedPreferencesUtils.getBoolFromSp(SharedPreferencesUtils.REMEMBER_PASSWORD);
    }

    @Override
    public void disposePassword(boolean isRemember) {
        SharedPreferencesUtils.saveBoolToSp(SharedPreferencesUtils.REMEMBER_PASSWORD, isRemember);
    }

    @Override
    public String getUserName() {
        return SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT);
    }

    @Override
    public String getPassWord() {
        return SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.PASSWORD);
    }

    @Override
    public void savePassword(String password) {
        SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.PASSWORD, password);
    }

    @Override
    public void saveAccount(String account) {
        SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.ACCOUNT, account);
    }

    @Override
    public Observable<String> requestLogin(String account, String password) {
        return mHttpApi.login(account, password).compose(SchedulersCompat.applyToSchedulers());
    }

    @Override
    public String getGesturePw() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        UserSettingBean userSettingBean = realm.where(UserSettingBean.class).findFirst();
        realm.commitTransaction();
        return userSettingBean.getGesturePw();
    }

}
