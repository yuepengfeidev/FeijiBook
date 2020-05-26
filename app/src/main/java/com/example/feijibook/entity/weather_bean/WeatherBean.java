package com.example.feijibook.entity.weather_bean;

/**
 * WeatherBean
 *
 * @author PengFei Yue
 * @date 2019/10/30
 * @description
 */
public class WeatherBean {

    /**
     * reason : 查询成功!
     * result : {"City":"苏州","realtime":{"temperature":"22","humidity":"35","info":"晴","wid":"00","direct":"南风","power":"2级","aqi":"138"},"future":[{"date":"2019-10-30","temperature":"12/23℃","weather":"晴","wid":{"day":"00","night":"00"},"direct":"东南风"},{"date":"2019-10-31","temperature":"12/24℃","weather":"晴","wid":{"day":"00","night":"00"},"direct":"东南风"},{"date":"2019-11-01","temperature":"15/24℃","weather":"晴转多云","wid":{"day":"00","night":"01"},"direct":"东风"},{"date":"2019-11-02","temperature":"15/21℃","weather":"多云","wid":{"day":"01","night":"01"},"direct":"东风转北风"},{"date":"2019-11-03","temperature":"12/22℃","weather":"多云转晴","wid":{"day":"01","night":"00"},"direct":"北风转西北风"}]}
     * error_code : 0
     */

    private String reason;
    private ResultBean result;
    private int error_code;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }


}
