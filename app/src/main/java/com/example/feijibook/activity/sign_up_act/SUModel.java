package com.example.feijibook.activity.sign_up_act;

import com.example.feijibook.app.MyApplication;
import com.example.feijibook.http.HttpApi;
import com.example.feijibook.http.SchedulersCompat;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;

/**
 * SUModel
 *
 * @author PengFei Yue
 * @date 2019/10/6
 * @description
 */
public class SUModel implements SUContact.Model {
    @Named("Scalars")
    @Inject
    HttpApi mHttpApi;

    SUModel() {
        MyApplication.getHttpComponent().injectSignUpModel(this);
    }

    @Override
    public Observable<String> requestRegister(String account, String password) {
        return mHttpApi.register(account, password).compose(SchedulersCompat.applyToSchedulers());
    }
}
