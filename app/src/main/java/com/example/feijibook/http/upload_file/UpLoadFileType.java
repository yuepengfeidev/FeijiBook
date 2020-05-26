package com.example.feijibook.http.upload_file;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UpLoadFileType
 *
 * @author PengFei Yue
 * @date 2019/11/7
 * @description 定义上传文件的方法注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UpLoadFileType {

}
