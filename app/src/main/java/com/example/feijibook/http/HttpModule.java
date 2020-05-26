package com.example.feijibook.http;

import com.example.feijibook.app.MyApplication;
import com.example.feijibook.http.upload_file.FileConverterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.example.feijibook.app.Constants.HTTP_CONNECT_TIMEOUT;

/**
 * HttpModule
 *
 * @author PengFei Yue
 * @date 2019/10/18
 * @description Http Dagger2 Module
 */
@Module
public class HttpModule {
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        // 打印接口响应的数据
        loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(loggingInterceptor)
                .build();
        return okHttpClient;
    }

    /**
     * 用于只解析返回字符串
     */
    @Named("Scalars")
    @Provides
    @Singleton
    HttpApi provideHttpApiWithScalars(OkHttpClient okHttpClient) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.HTTP_URL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(HttpApi.class);
    }

    /**
     * 用于解析返回GSON
     */
    @Named("Gson")
    @Provides
    @Singleton
    HttpApi provideHttpApiWithGson(OkHttpClient okHttpClient) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.HTTP_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(HttpApi.class);
    }

    /**
     * 用户上传文件
     */
    @Named("File")
    @Provides
    @Singleton
    HttpApi provideHttpApiWithFile(OkHttpClient okHttpClient) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.HTTP_URL)
                .client(okHttpClient)
                .addConverterFactory(new FileConverterFactory())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(HttpApi.class);
    }

    /**
     * 用于获取聚合天气数据接口
     */
    @Named("Weather")
    @Provides
    @Singleton
    HttpApi provideHttpApiWithWeather(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://apis.juhe.cn/simpleWeather/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(HttpApi.class);
    }

    /**
     * 用于获取全国省市区接口和Bing图片
     */
    @Named("AreaBing")
    @Provides
    @Singleton
    HttpApi provideHttpApiWithArea(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://guolin.tech/api/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(HttpApi.class);
    }


}
