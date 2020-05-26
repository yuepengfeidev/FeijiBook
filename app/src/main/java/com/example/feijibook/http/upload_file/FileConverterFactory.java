package com.example.feijibook.http.upload_file;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import okhttp3.RequestBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * FileConverterFactory
 *
 * @author PengFei Yue
 * @date 2019/11/7
 * @description 转换工厂
 */
public class FileConverterFactory extends Converter.Factory {
    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        for (Annotation annotation : methodAnnotations) {
            if (annotation instanceof UpLoadFileType) {
                // 如果是上传文件，则使用文件转换器
                return new FileRequestBodyConverter();
            }
        }
        return null;
    }
}
