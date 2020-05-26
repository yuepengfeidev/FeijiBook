package com.example.feijibook.http;

import android.util.Log;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * BaseObserver
 *
 * @author PengFei Yue
 * @date 2019/10/28
 * @description
 */
public abstract class BaseObserver<T> implements Observer<T> {
    private final static String TAG = "BaseObserver";
    private Disposable mDisposable;

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: " + e.getMessage());
        onHttpError(ExceptionHandle.handleException(e));
    }

    @Override
    public void onComplete() {
        mDisposable.dispose();
    }

    /**
     * 上传、下载更新进度
     *
     * @param percent 进度百分比
     */
    protected void onProgress(String percent) {

    }

    public abstract void onSuccess(T t);

    public abstract void onHttpError(ExceptionHandle.ResponseException exception);
}

