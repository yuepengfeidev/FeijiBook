package com.example.feijibook.entity.weather_bean;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * ResultBean
 *
 * @author PengFei Yue
 * @date 2019/11/1
 * @description
 */
public  class ResultBean extends RealmObject {
    /**
     * City : 苏州
     * realtime : {"temperature":"22","humidity":"35","info":"晴","wid":"00","direct":"南风","power":"2级","aqi":"138"}
     * future : [{"date":"2019-10-30","temperature":"12/23℃","weather":"晴","wid":{"day":"00","night":"00"},"direct":"东南风"},{"date":"2019-10-31","temperature":"12/24℃","weather":"晴","wid":{"day":"00","night":"00"},"direct":"东南风"},{"date":"2019-11-01","temperature":"15/24℃","weather":"晴转多云","wid":{"day":"00","night":"01"},"direct":"东风"},{"date":"2019-11-02","temperature":"15/21℃","weather":"多云","wid":{"day":"01","night":"01"},"direct":"东风转北风"},{"date":"2019-11-03","temperature":"12/22℃","weather":"多云转晴","wid":{"day":"01","night":"00"},"direct":"北风转西北风"}]
     */

    @PrimaryKey
    private String id;
    private String city;
    private RealtimeBean realtime;
    private RealmList<FutureBean> future;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public RealtimeBean getRealtime() {
        return realtime;
    }

    public void setRealtime(RealtimeBean realtime) {
        this.realtime = realtime;
    }

    public RealmList<FutureBean> getFuture() {
        return future;
    }

    public void setFuture(RealmList<FutureBean> future) {
        this.future = future;
    }


}