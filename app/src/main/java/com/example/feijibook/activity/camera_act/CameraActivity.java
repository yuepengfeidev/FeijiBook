package com.example.feijibook.activity.camera_act;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.feijibook.R;
import com.example.feijibook.util.ActivityUtil;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideBottomUIMenu();
        setContentView(R.layout.activity_camera);
        // 获取相机界面的模式，默认为照相模式0
        int mode = getIntent().getIntExtra("mode", 0);
        // 获取该id的记录
        String id = getIntent().getStringExtra("id");
        CameraFragment cameraFragment = CameraFragment.newInstance();
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(), cameraFragment,
                R.id.fl_camera);
        CameraPresenter presenter = new CameraPresenter(this, cameraFragment, mode, id);
        presenter.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.anim_scale_out);
    }

    /**
     * 隐藏虚拟按键,后两个uiOptions是为了设置状态栏透明
     */
    private void hideBottomUIMenu() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
