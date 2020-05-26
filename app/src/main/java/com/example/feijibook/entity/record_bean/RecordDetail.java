package com.example.feijibook.entity.record_bean;

import android.support.annotation.Nullable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 你是我的 on 2019/3/12
 */

/*
 * 每条记录的详情
 */
public class RecordDetail extends RealmObject implements Cloneable {
    @PrimaryKey
    private String id;
    // 年 ：1998
    private String year;
    // 月: 10
    private String month;
    // 日: 13
    private String day;
    // 星期几
    private String week;
    // 第几周：13(第13周)
    private String weekOfYear;
    // 类型：收入或支出
    private String type;
    // 图标: 高详细类型的图标，如餐饮的图标
    private int iconUrl;
    // 详细类型： 如支出中的 餐饮，收入中的 礼金
    private String detailType;
    // 金额
    private String money;
    // 备注，默认为详细类型
    private String remark;
    // 拍的图片地址，多张图片可用地址 通过中间添加'|'组合
    private String imgUrl;
    // 拍摄视频地址
    private String videoUrl;

    /**
     * 视频和照片是否上传
     */
    private boolean isVideoUpload;
    private boolean isImgUpload;

    /**
     * 记录顺序（从小到大，为从先的记录到后记录的）
     */
    private int order;

    public boolean isVideoUpload() {
        return isVideoUpload;
    }

    public void setVideoUpload(boolean videoUpload) {
        isVideoUpload = videoUpload;
    }

    public boolean isImgUpload() {
        return isImgUpload;
    }

    public void setImgUpload(boolean imgUpload) {
        isImgUpload = imgUpload;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getWeekOfYear() {
        return weekOfYear;
    }

    public void setWeekOfYear(String weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(int iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public Object clone() {
        RecordDetail recordDetail = null;
        try {
            recordDetail = (RecordDetail) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return recordDetail;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        RecordDetail recordDetail = (RecordDetail) obj;
        return this.id.equals(recordDetail.getId());
    }
}
