package com.example.feijibook.activity.weather_act;

import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.area_bean.City;
import com.example.feijibook.entity.area_bean.County;
import com.example.feijibook.entity.area_bean.Province;
import com.example.feijibook.entity.weather_bean.ResultBean;
import com.example.feijibook.http.HttpApi;
import com.example.feijibook.http.SchedulersCompat;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;

/**
 * WeatherModel
 *
 * @author PengFei Yue
 * @date 2019/10/29
 * @description
 */
public class WeatherModel implements WeatherContract.Model {
    @Named("AreaBing")
    @Inject
    HttpApi mHttpApi;

    @Named("Weather")
    @Inject
    HttpApi mHttpApi2;

    WeatherModel() {
        MyApplication.getHttpComponent().injectWeatherModel(this);
    }

    @Override
    public Observable<ResponseBody> requestProvince() {
        return mHttpApi.getProvince().compose(SchedulersCompat.applyToSchedulers());
    }

    @Override
    public Observable<ResponseBody> requestCity(int provinceId) {
        return mHttpApi.getCity(provinceId).compose(SchedulersCompat.applyToSchedulers());
    }

    @Override
    public Observable<ResponseBody> requestCounty(int provinceId, int cityId) {
        return mHttpApi.getCounty(provinceId, cityId).compose(SchedulersCompat.applyToSchedulers());
    }

    @Override
    public Observable<ResponseBody> requestWeather(String city) {
        return mHttpApi2.getWeather(city, MyApplication.WEATHER_KEY).compose(SchedulersCompat.applyToSchedulers());
    }

    @Override
    public Observable<ResponseBody> requestBingPic() {
        return mHttpApi.getBingPic().compose(SchedulersCompat.applyToSchedulers());
    }


    @Override
    public List<Province> getProvince() {
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        RealmResults<Province> provinces = mRealm.where(Province.class).findAll();
        mRealm.commitTransaction();
        mRealm.close();
        return provinces;
    }

    @Override
    public List<City> getCity(int provinceId) {
        Realm mRealm = Realm.getDefaultInstance();
        RealmResults<City> cities = mRealm.where(City.class).equalTo("provinceId", provinceId).findAll();
        mRealm.close();
        return cities;
    }

    @Override
    public List<County> getCounty(int cityId) {
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        RealmResults<County> counties = mRealm.where(County.class).equalTo("cityId", cityId).findAll();
        mRealm.commitTransaction();
        mRealm.close();
        return counties;
    }

    @Override
    public void saveProvince(List<Province> list) {
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Province province : list) {
                    realm.insert(province);
                }
                mRealm.close();
            }
        });
    }

    @Override
    public void saveCity(List<City> list) {
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (City city : list) {
                    realm.insertOrUpdate(city);
                }
                mRealm.close();
            }
        });
    }

    @Override
    public void saveCounty(List<County> list) {
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (County county : list) {
                    realm.insertOrUpdate(county);
                }
                mRealm.close();
            }
        });
    }

    @Override
    public ResultBean getWeather() {
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        ResultBean resultBean = mRealm.where(ResultBean.class).findFirst();
        mRealm.commitTransaction();
        mRealm.close();
        return resultBean;
    }

    @Override
    public void saveWeather(ResultBean resultBean) {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.beginTransaction();
            realm.where(ResultBean.class).findAll().deleteAllFromRealm();
            realm.insertOrUpdate(resultBean);
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.cancelTransaction();
        }
        realm.close();
    }

}
