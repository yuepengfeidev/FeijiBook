package com.example.feijibook.widget.my_dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.feijibook.R;
import com.example.feijibook.widget.my_progress_bar.MyCircleProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * CircleProgressBarDialog
 *
 * @author PengFei Yue
 * @date 2019/11/8
 * @description 圆形进度条Dialog
 */
public class CircleProgressBarDialog extends Dialog {

    @BindView(R.id.myCircleProgressBar)
    MyCircleProgressBar myCircleProgressBar;

    public CircleProgressBarDialog(@NonNull Context context) {
        super(context, R.style.SettingDialog);
        getWindow().setWindowAnimations(R.style.SettingDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_cirlce_progress_bar);
        ButterKnife.bind(this);
    }

    public void setPercent(String percent) {
        myCircleProgressBar.setPercent(percent);
    }

    public void close() {
        myCircleProgressBar.cleanProgress();
        dismiss();
    }


}
