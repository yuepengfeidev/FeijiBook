package com.example.feijibook.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.example.feijibook.http.DaggerHttpComponent;
import com.example.feijibook.http.HttpComponent;
import com.example.feijibook.http.HttpModule;
import com.example.feijibook.util.SharedPreferencesUtils;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by 你是我的 on 2019/3/11
 */
public class MyApplication extends Application {
    public static Context sContext;
    public static int sStatusBarHeight;
    public static String BASE_PATH;
    public static String VIDEO_PATH;
    public static String PHOTO_PATH;
    public final static String HTTP_URL = "http://192.168.137.1:8081/FeiJiBookServer_war_exploded/user/";
    /**
     * 聚合天气接口的Key
     */
    public final static String WEATHER_KEY = "01bc45cec9d660659e597ca4dc9af9e3";
    private static HttpComponent sMHttpComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        //Realm数据库初始化
        Realm.init(this);

        //Stetho初始化
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build()
        );

        sContext = getApplicationContext();

        Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        sStatusBarHeight = resources.getDimensionPixelSize(resourceId);

        sMHttpComponent = DaggerHttpComponent.builder().httpModule(new HttpModule()).build();
    }

    public static HttpComponent getHttpComponent() {
        return sMHttpComponent;
    }

    /**
     * 用于登录账号，初始化对应账户的数据库和文件夹
     */
    public static void initData() {
        BASE_PATH = sContext.getExternalFilesDir(null) + "/" + SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT);
        VIDEO_PATH = BASE_PATH + "/Videos/";
        PHOTO_PATH = BASE_PATH + "/Photos/";
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                // 文件名
                .name(SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT) + ".realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(configuration);
    }
}
