package com.example.feijibook.http;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * SchedulersCompat
 *
 * @author PengFei Yue
 * @date 2019/10/18
 * @description
 */
public class SchedulersCompat {
    /**
     * 从其他线程转回到主程进行视图处理
     */
    private final static ObservableTransformer ioMainTransformer =
            upstream -> upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

    public static <T> ObservableTransformer<T, T> applyToSchedulers() {
        return (ObservableTransformer<T, T>) ioMainTransformer;
    }

    /**
     * 上下游都是在io线程里，用于下载存储文件
     */
    private final static ObservableTransformer ioIoTransformer =
            upstream -> upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

    public static <T> ObservableTransformer<T, T> applyToSchedulers2() {
        return (ObservableTransformer<T, T>) ioIoTransformer;
    }
}
