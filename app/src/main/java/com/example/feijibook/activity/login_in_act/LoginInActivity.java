package com.example.feijibook.activity.login_in_act;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.feijibook.R;
import com.example.feijibook.util.ActivityUtil;

public class LoginInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_login_in);

        LoginInFragment fragment = LoginInFragment.newInstance();
        LIPresenter presenter = new LIPresenter(this, fragment);
        presenter.start();
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),fragment,
                R.id.fl_login_in_act);



    }
}
