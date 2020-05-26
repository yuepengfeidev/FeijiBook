package com.example.feijibook.http.upload_file;

import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * UploadOnSubscribe
 *
 * @author PengFei Yue
 * @date 2019/11/7
 * @description 文件上传进度观察者
 */
public class UploadOnSubscribe implements ObservableOnSubscribe<Double> {
    /**
     * 进度观察者
     */
    private ObservableEmitter<Double> mObservableEmitter;
    /**
     * 文件总长度
     */
    public long mSumLength = 0L;
    /**
     * 已上传的长度
     */
    public AtomicLong uploaded = new AtomicLong();
    /**
     * 上传进度比例
     */
    private double mPercent = 0;


    @Override
    public void subscribe(ObservableEmitter<Double> emitter) {
        this.mObservableEmitter = emitter;
    }

    public void onRead(long read) {
        uploaded.addAndGet(read);

        if (mSumLength <= 0) {
            onProgress(0);
        } else {
            onProgress(100d * uploaded.get() / mSumLength);
        }
    }

    private void onProgress(double percent) {
        if (null == mObservableEmitter) {
            return;
        }
        if (percent == mPercent) {
            return;
        }
        mPercent = percent;
        if (percent >= 100d) {
            // 当进度为100%时，完成上传
            percent = 100d;
            mObservableEmitter.onNext(percent);
            mObservableEmitter.onComplete();
        } else {
            mObservableEmitter.onNext(percent);
        }
    }

    public void setSumLength(long sumLength) {
        mSumLength = sumLength;
    }

    /**
     * 上传完成，清除进度数据
     */
    public void clean() {
        mPercent = 0;
        uploaded = new AtomicLong();
        mSumLength = 0;
    }
}
