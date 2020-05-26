package com.example.feijibook.http.upload_file;

import android.util.ArrayMap;

import java.io.File;
import java.util.List;

import javax.annotation.Nullable;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * FileRequestBodyConverter
 *
 * @author PengFei Yue
 * @date 2019/11/7
 * @description 上传文件 转换文件类型的转换器
 */
public class FileRequestBodyConverter implements Converter<ArrayMap<String, Object>, RequestBody> {
    UploadOnSubscribe mUploadOnSubscribe;

    @Nullable
    @Override
    public RequestBody convert(ArrayMap<String, Object> params) {
        mUploadOnSubscribe = (UploadOnSubscribe) params.get("UploadOnSubscribe");
        // 文件对应记录的id
        List<String> list = (List<String>) params.get("ids");
        if (params.containsKey("filePathList")) {
            return filesToMultipartBody(list, (List<String>) params.get("filePathList"));
        } else if (params.containsKey("files")) {
            return filesToMultipartBody(list, (List<File>) params.get("files"));
        } else {
            return null;
        }
    }

    /**
     * 用于把 File集合 或者 File路径集合 转化成 MultipartBody
     *
     * @param files File列表或者 File 路径列表
     * @param <T>   泛型（File 或者 String）
     * @return MultipartBody（retrofit 多文件文件上传）
     */
    public synchronized <T> MultipartBody filesToMultipartBody(List<String> ids, List<T> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        // 所有文件总大小
        long sumLeng = 0;
        File file;
        for (T t : files) {
            if (t instanceof File) {
                file = (File) t;
            } else if (t instanceof String) {
                file = new File((String) t);
            } else {
                break;
            }
            sumLeng += file.length();
            ProgressRequestBody requestBody =
                    new ProgressRequestBody(file, "multipart/form-data", mUploadOnSubscribe);
            builder.addFormDataPart(ids.get(files.indexOf(t)), file.getName(), requestBody);
        }
        mUploadOnSubscribe.setSumLength(sumLeng);
        return builder.build();
    }
}
