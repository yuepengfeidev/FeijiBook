package com.example.feijibook.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * UserSettingBean
 *
 * @author PengFei Yue
 * @date 2019/11/3
 * @description 记录该用户的设置
 */
public class UserSettingBean extends RealmObject {
    @PrimaryKey
    String account;
    /**
     * 预算
     */
    String budget;
    /**
     * 是否开启声音
     */
    boolean openSound;
    /**
     * 手势密码
     */
    String gesturePw;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public boolean isOpenSound() {
        return openSound;
    }

    public void setOpenSound(boolean openSound) {
        this.openSound = openSound;
    }

    public String getGesturePw() {
        return gesturePw;
    }

    public void setGesturePw(String gesturePw) {
        this.gesturePw = gesturePw;
    }
}
