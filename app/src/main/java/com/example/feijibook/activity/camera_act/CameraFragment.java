package com.example.feijibook.activity.camera_act;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.util.SharedPreferencesUtils;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.my_camera.AutoFitTextureView;
import com.example.feijibook.widget.my_camera.CameraController;
import com.example.feijibook.widget.my_camera.VideoRecordButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class CameraFragment extends Fragment implements CameraContract.View, View.OnClickListener {
    @BindView(R.id.aftv_camera)
    AutoFitTextureView aftvCamera;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.vs_video_record_button)
    ViewStub vsVideoRecordButton;
    @BindView(R.id.vs_take_photo)
    ViewStub vsTakePhoto;
    Unbinder unbinder;
    private Activity mActivity;
    private CameraContract.Presenter mPresenter;
    private CameraController mCameraController;
    Button btTakePhoto;
    ImageView ivGetPhoto;
    ImageView ivGiveUpPhoto;
    ImageView ivPhotoDemo;


    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        unbinder = ButterKnife.bind(this, view);
        mPresenter.setInit();
        return view;
    }

    @Override
    public void finishAct() {
        mActivity.finish();
    }

    @Override
    public void startAct(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void getAct(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void initPhotoMode() {
        View view = vsTakePhoto.inflate();
        btTakePhoto = view.findViewById(R.id.bt_take_photo);
        ivGetPhoto = view.findViewById(R.id.iv_get_photo);
        ivGiveUpPhoto = view.findViewById(R.id.iv_giveup_photo);
        ivPhotoDemo = view.findViewById(R.id.iv_photo_demo);
        btTakePhoto.setOnClickListener(this);
        ivGetPhoto.setOnClickListener(this);
        ivGiveUpPhoto.setOnClickListener(this);
    }

    @Override
    public void initVideoMode() {
        View view = vsVideoRecordButton.inflate();
        VideoRecordButton videoRecordButton = view.findViewById(R.id.video_record_button);
        videoRecordButton.setOnProgressTouchListener(new VideoRecordButton.OnProgressTouchListener() {
            @Override
            public void onLongPress(VideoRecordButton button) {
                mCameraController.startRecordingVideo();
            }

            @Override
            public void onFinish() {
                mCameraController.stopRecordingVideo();
                mPresenter.setSaveVideo();
            }

            @Override
            public void onToast() {
                mCameraController.stopRecordingVideo();
                Toast.makeText(MyApplication.sContext, "录制时间太短啦！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void showConfirmPhoto(Bitmap bitmap) {
        ivGetPhoto.setVisibility(View.VISIBLE);
        ivGiveUpPhoto.setVisibility(View.VISIBLE);
        btTakePhoto.setVisibility(View.GONE);
        ivPhotoDemo.setImageBitmap(bitmap);
    }

    @Override
    public void hideConfirmPhoto() {
        ivPhotoDemo.setImageDrawable(null);
        ivGetPhoto.setVisibility(View.GONE);
        ivGiveUpPhoto.setVisibility(View.GONE);
        btTakePhoto.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPresenter(CameraContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initListener() {
        ivClose.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取相机管理类的实例
        mCameraController = CameraController.getInstance(mActivity);
        mCameraController.initCamera(aftvCamera, mPresenter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setFinishAct();
                break;
            case R.id.bt_take_photo:
                SoundShakeUtil.playSound(SoundShakeUtil.TAKE_PHOTO_SOUND);
                mCameraController.takePicture();
                btTakePhoto.setClickable(false);
                break;
            case R.id.iv_get_photo:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setHideConfirmPhoto();
                mPresenter.setSavePhoto();
                btTakePhoto.setClickable(true);
                break;
            case R.id.iv_giveup_photo:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setHideConfirmPhoto();
                btTakePhoto.setClickable(true);
                break;
            default:
        }

    }
}
