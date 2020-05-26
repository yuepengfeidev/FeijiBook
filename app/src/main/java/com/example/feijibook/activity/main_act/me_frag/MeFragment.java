package com.example.feijibook.activity.main_act.me_frag;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.feijibook.R;
import com.example.feijibook.activity.album_act.AlbumActivity;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.activity.gesture_pw_act.GesturePwActivity;
import com.example.feijibook.activity.main_act.MainActivity;
import com.example.feijibook.activity.user_info_act.UserInfoActivity;
import com.example.feijibook.app.Constants;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.MyToast;
import com.example.feijibook.widget.my_constraintlayout.MyTextView;
import com.example.feijibook.widget.my_dialog.ChangePwDialog;
import com.example.feijibook.widget.my_dialog.SettingChooseDialog;
import com.example.feijibook.widget.my_dialog.SettingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment implements MeContract.View, MainActivity.ViewPagerSelect {
    MeContract.Presenter mPresenter;
    Activity mActivity;
    View mView;
    @BindView(R.id.iv_portrait)
    ImageView ivPortrait;
    @BindView(R.id.tv_day_count)
    TextView tvDayCount;
    @BindView(R.id.tv_record_count)
    TextView tvRecordCount;
    @BindView(R.id.tv_nick_name)
    TextView tvNickName;
    @BindView(R.id.tv_photo_album)
    MyTextView tvPhotoAlbum;
    @BindView(R.id.tv_video_album)
    MyTextView tvVideoAlbum;
    @BindView(R.id.switch_sound)
    Switch mSwitchSound;
    @BindView(R.id.switch_gesture_password)
    Switch mSwitchGesturePassword;
    Unbinder unbinder;
    MyToast mToast;
    ChangePwDialog mPwDialog;

    public MeFragment() {
        // Required empty public constructor
    }

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_me, container, false);
        unbinder = ButterKnife.bind(this, mView);
        NoticeUpdateUtils.updateRecordsPresenters.add(mPresenter);
        mPresenter.setInit();
        return mView;
    }


    @Override
    public void setPresenter(MeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initListener() {
        onViewClicked(mView);
        mSwitchGesturePassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SoundShakeUtil.playSound(SoundShakeUtil.TAP2_SOUND);
            if (NoticeUpdateUtils.changeGesturePwFailed) {
                // 这是改变手势密码设置失败的情况，因为会将swtich check原来的状态
                NoticeUpdateUtils.changeGesturePwFailed = false;
                return;
            }
            Intent intent = new Intent(mActivity, GesturePwActivity.class);
            if (isChecked) {
                intent.putExtra("mode", "set");
            } else {
                intent.putExtra("mode", "cancel");
            }
            mPresenter.setStartActivity(intent);
        });
        mSwitchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (NoticeUpdateUtils.changeSoundFailed) {
                NoticeUpdateUtils.changeSoundFailed = false;
                return;
            }
            mPresenter.setSaveSoundOpen(isChecked);
        });
        MainActivity.addViewPagerSelectListener(this);
    }

    @Override
    public void startAct(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void showChangePwDialog() {
        if (mPwDialog == null) {
            mPwDialog = new ChangePwDialog(mActivity);
            mPwDialog.setOnChangePwListener((resPw, newPw) -> mPresenter.setChangePw(resPw, newPw));
            mPwDialog.show();
        } else {
            mPwDialog.show();
        }
    }

    @Override
    public void showToast(String content) {
        if (mToast == null) {
            mToast = new MyToast(mActivity);
        }
        mToast.showToast(content);
    }

    @Override
    public void showPwError() {
        if (mPwDialog != null) {
            mPwDialog.erroResPw();
        }
    }

    @Override
    public void dismissChangePwDialog() {
        if (mPwDialog != null) {
            mPwDialog.dismiss();
        }
    }

    @Override
    public void getAct(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void updateRecord() {
        mPresenter.setGetDataCount();
        mPresenter.setGetPortrait();
        mPresenter.setGetNickName();
    }

    @Override
    public void showPortrait(String portraitUrl) {
        RequestOptions requestOptions = new RequestOptions();
        // 不使用磁盘缓存
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        // 不使用内存缓存
        requestOptions.skipMemoryCache(true);
        if (ivPortrait == null) {
            ivPortrait = mView.findViewById(R.id.iv_portrait);
        }
        Glide.with(MyApplication.sContext).load(portraitUrl)
                .thumbnail(0.7f)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .apply(requestOptions)
                .into(ivPortrait);
    }

    @Override
    public void showPortrait(int portrait) {
        Glide.with(this).load(portrait)
                .thumbnail(0.5f)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(ivPortrait);
    }

    @Override
    public void showNickName(String nickName) {
        tvNickName.setText(nickName);
    }

    @Override
    public void showDayAndRecord(String dayCount, String recordCount) {
        tvDayCount.setText(dayCount);
        tvRecordCount.setText(recordCount);
    }

    @Override
    public void checkGesturePwSwitch() {
        mSwitchGesturePassword.setChecked(!mSwitchGesturePassword.isChecked());
    }

    @Override
    public void checkSoundOpen() {
        mSwitchSound.setChecked(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.checkUpdateRecord();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_portrait, R.id.tv_photo_album, R.id.tv_video_album, R.id.tv_change_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_portrait:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                BaseActivity.addBindAdjacentLayer((MainActivity) mActivity);
                Intent intent = new Intent(mActivity, UserInfoActivity.class);
                mPresenter.setStartActivity(intent);
                break;
            case R.id.tv_photo_album:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                BaseActivity.addBindAdjacentLayer((MainActivity) mActivity);
                Intent intent2 = new Intent(mActivity, AlbumActivity.class);
                intent2.putExtra("isPhotosAlbum", true);
                mPresenter.setStartActivity(intent2);
                break;
            case R.id.tv_video_album:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                BaseActivity.addBindAdjacentLayer((MainActivity) mActivity);
                Intent intent3 = new Intent(mActivity, AlbumActivity.class);
                intent3.putExtra("isPhotosAlbum", false);
                mPresenter.setStartActivity(intent3);
                break;
            case R.id.tv_change_password:
                SoundShakeUtil.playSound(SoundShakeUtil.SELECT_SWOOSH1_SOUND);
                mPresenter.setShowChangePwDialog();
                break;
            default:
        }
    }

    @Override
    public void onPageSelect(int position) {
        if (position == Constants.ME_PAGE) {
            mPresenter.checkUpdateRecord();
        }
    }
}
