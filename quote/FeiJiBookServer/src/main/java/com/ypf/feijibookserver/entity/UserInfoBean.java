package com.ypf.feijibookserver.entity;

/**
 * UserInfoBean
 *
 * @author PengFei Yue
 * @date 2019/10/8
 * @description 数据库中“个人信息”表的所有字段
 */
public class UserInfoBean {
    String id;
    String account;
    String nickname;
    String password;
    String portrait;
    String sex;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
