package com.example.feijibook.util;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Preconditions;

/**
 * Created by 你是我的 on 2019/3/18
 */

/**
 * 判断添加fragment到activity中时，是否有为空
 */
public class ActivityUtil {
    @SuppressLint("RestrictedApi")
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId){
        Preconditions.checkNotNull(fragmentManager);
        Preconditions.checkNotNull(fragment);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId,fragment);
        transaction.commit();
    }
}
