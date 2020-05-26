package com.example.feijibook.activity.gesture_pw_act;

import com.example.feijibook.entity.UserSettingBean;

import io.realm.Realm;

/**
 * GPModel
 *
 * @author PengFei Yue
 * @date 2019/11/3
 * @description
 */
public class GPModel implements GPContract.Model {
    @Override
    public String getGesturePw() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        UserSettingBean userSettingBean = realm.where(UserSettingBean.class).findFirst();
        realm.commitTransaction();
        return userSettingBean.getGesturePw();
    }

    @Override
    public void saveGesturePw(String gesturePw) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        UserSettingBean userSettingBean = realm.where(UserSettingBean.class).findFirst();
        userSettingBean.setGesturePw(gesturePw);
        realm.commitTransaction();
    }
}
