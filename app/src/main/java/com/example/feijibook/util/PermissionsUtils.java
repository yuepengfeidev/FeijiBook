package com.example.feijibook.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.feijibook.app.MyApplication;

/**
 * PermissionsUtils
 *
 * @author yuepengfei
 * @date 2019/5/26
 * @description 权限处理工具
 */
public class PermissionsUtils {
    public static final int PERMISSION_REQUEST_CODE = 1;
    public static String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.INTERNET",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.READ_EXTERNAL_STORAGE"
};

public static boolean hasPermissions(String[]permissions){
        int result=-1;
        for(String permission:permissions){
        result=ContextCompat.checkSelfPermission(MyApplication.sContext,permission);
        }
        return result==PackageManager.PERMISSION_GRANTED;
        }

public static void requestNecessaryPermissions(Activity activity,String[]permissions){
        ActivityCompat.requestPermissions(activity,permissions,PERMISSION_REQUEST_CODE);
        }
        }
