package com.example.feijibook.activity.sign_up_act;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.feijibook.R;
import com.example.feijibook.util.ActivityUtil;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_sign_up);

        SignUpFragment fragment = SignUpFragment.newInstance();
        SUPresenter presenter = new SUPresenter(this, fragment);
        presenter.start();
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),
                fragment, R.id.fl_sign_up_act);
    }
}
