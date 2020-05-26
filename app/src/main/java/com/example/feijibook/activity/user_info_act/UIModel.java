package com.example.feijibook.activity.user_info_act;

import com.example.feijibook.app.MyApplication;
import com.example.feijibook.http.HttpApi;
import com.example.feijibook.http.SchedulersCompat;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import okhttp3.MultipartBody;

/**
 * UIModel
 *
 * @author PengFei Yue
 * @date 2019/10/21
 * @description
 */
public class UIModel implements UIContract.Model {
    @Named("Scalars")
    @Inject
    HttpApi mHttpApi;

    UIModel() {
        MyApplication.getHttpComponent().injectUserInfoModel(this);
    }


    @Override
    public Observable<String> requestUploadPortrait(MultipartBody.Part portrait) {
        return mHttpApi.uploadPortrait(portrait).compose(SchedulersCompat.applyToSchedulers());
    }

    @Override
    public Observable<String> requestSetNickName(String account, String nickName) {
        return mHttpApi.changeNickName(account, nickName).compose(SchedulersCompat.applyToSchedulers());
    }

    @Override
    public Observable<String> requestSetSex(String account, String sex) {
        return mHttpApi.changeSex(account, sex).compose(SchedulersCompat.applyToSchedulers());
    }

}
