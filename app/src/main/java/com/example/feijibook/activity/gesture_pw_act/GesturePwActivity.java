package com.example.feijibook.activity.gesture_pw_act;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.util.ActivityUtil;
import com.example.feijibook.util.NoticeUpdateUtils;

public class GesturePwActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_gesture_pw);

        String mode = getIntent().getStringExtra("mode");

        GesturePwFragment fragment = GesturePwFragment.newInstance();
        GPPresenter presenter = new GPPresenter(this, fragment, mode);
        presenter.start();
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),
                fragment, R.id.fl_gesture_pw_act);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 按返回键表示，修改操作密码失败
            NoticeUpdateUtils.changeGesturePwFailed = true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
