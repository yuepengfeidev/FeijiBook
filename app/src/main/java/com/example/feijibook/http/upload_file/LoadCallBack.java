package com.example.feijibook.http.upload_file;

import com.example.feijibook.http.BaseObserver;

import java.text.DecimalFormat;

/**
 * LoadCallBack
 *
 * @author PengFei Yue
 * @date 2019/11/7
 * @description  自定义文件上传、下载 观察者
 */
public abstract class LoadCallBack<T> extends BaseObserver<T> {
    private DecimalFormat df = new DecimalFormat("##0.0");

    @Override
    public void onNext(T t) {
        if (t instanceof Double) {
            String percent = df.format(((Double) t).doubleValue());
            onProgress(percent);
        } else {
           onSuccess(t);
        }
    }

}
