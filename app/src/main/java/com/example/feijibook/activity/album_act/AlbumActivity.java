package com.example.feijibook.activity.album_act;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.util.ActivityUtil;

public class AlbumActivity extends BaseActivity {
    FrameLayout mFrameLayout;
    AlbumContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        mFrameLayout = findViewById(R.id.fl_album_act);

        boolean isPhotoAlbum = getIntent().getBooleanExtra("isPhotosAlbum", true);

        super.anim(mFrameLayout);

        AlbumFragment albumFragment = AlbumFragment.newInstance();
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),
                albumFragment, R.id.fl_album_act);
        mPresenter = new AlbumPresenter(this, albumFragment,isPhotoAlbum);
        mPresenter.start();

        super.init(mPresenter);
    }
}
