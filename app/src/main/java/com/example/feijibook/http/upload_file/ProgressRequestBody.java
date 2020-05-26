package com.example.feijibook.http.upload_file;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * ProgressRequestBody
 *
 * @author PengFei Yue
 * @date 2019/11/7
 * @description
 */
public class ProgressRequestBody extends RequestBody {
    private File mFile;
    private String mContentType;
    private UploadOnSubscribe mSubscribe;

    ProgressRequestBody(File file, String contentType, UploadOnSubscribe subscribe) {
        mFile = file;
        mContentType = contentType;
        mSubscribe = subscribe;
    }

    @Override
    public long contentLength() {
        return mFile.length();
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return MediaType.parse(mContentType);
    }

    private int sum;
    /**
     * 一个文件最大15MB
     */
    private static final int SEGMENT_SIZE = 15360;

    @Override
    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
        sum++;
        boolean isPercent = bufferedSink instanceof Buffer;

        Source source = null;
        try {
            source = Okio.source(mFile);

            long read;
            while ((read = source.read(bufferedSink.getBuffer(), SEGMENT_SIZE)) != -1) {
                bufferedSink.flush();
                // 有的手机会执行2、3次该方法，所以限制执行读取进度
                if (!isPercent && sum > 1) {
                    if (null != mSubscribe) {
                            mSubscribe.onRead(read);
                    }
                }
            }
        } finally {
            Util.closeQuietly(source);
        }
    }
}
