package com.ypf.feijibookserver.entity;

/**
 * DetailRecordBean
 *
 * @author PengFei Yue
 * @date 2019/10/8
 * @description 单条账单记录的详情
 */
public class DetailRecordBean {
    /**
     * 原纪录id，用于删除原纪录id，添加id为新记录，达到修改记录的目的（只用于修改记录）
     */
    String resId;
    String id;
    String account;
    String year;
    String month;
    String day;
    /**
     * 星期几
     */
    String week;
    /**
     * 2019-41，一年的第几周 weekOfYear
     */
    String woy;
    /**
     * incomeOrExpend，收入 或 支出
     */
    String ioe;
    int icon_url;
    String detail_type;
    String money;
    String remark;
    String img_url;
    String video_url;
    int record_order;

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getWoy() {
        return woy;
    }

    public void setWoy(String woy) {
        this.woy = woy;
    }

    public String getIoe() {
        return ioe;
    }

    public void setIoe(String ioe) {
        this.ioe = ioe;
    }

    public int getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(int icon_url) {
        this.icon_url = icon_url;
    }

    public String getDetail_type() {
        return detail_type;
    }

    public void setDetail_type(String detail_type) {
        this.detail_type = detail_type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public int getRecord_order() {
        return record_order;
    }

    public void setRecord_order(int record_order) {
        this.record_order = record_order;
    }
}
