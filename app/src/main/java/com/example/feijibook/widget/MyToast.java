package com.example.feijibook.widget;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feijibook.R;

/**
 * Created by 你是我的 on 2019/4/1
 */
public class MyToast {
    private Toast mToast;
    private Activity mActivity;
    private TextView tvToast;

    public MyToast(Activity activity) {
        mActivity = activity;
    }

    public void showToast(String content) {
        if (mToast == null) {
            mToast = new Toast(mActivity);
            View view = View.inflate(mActivity, R.layout.layout_toast, null);
            tvToast = view.findViewById(R.id.tv_toast);
            tvToast.setText(content);
            mToast.setView(view);
            mToast.setGravity(Gravity.CENTER, 0, -100);
            mToast.setDuration(Toast.LENGTH_SHORT);
        } else {
            tvToast.setText(content);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
}
