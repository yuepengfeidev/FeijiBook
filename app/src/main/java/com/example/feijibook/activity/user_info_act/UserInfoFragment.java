package com.example.feijibook.activity.user_info_act;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.feijibook.R;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.MyToast;
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
public class UserInfoFragment extends Fragment implements UIContract.View {
    View mView;
    UIContract.Presenter mPresenter;
    Activity mActivity;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.iv_portrait)
    ImageView ivPortrait;
    Unbinder unbinder;
    MyToast mToast;
    ChangePwDialog mPwDialog;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    public static UserInfoFragment newInstance() {
        return new UserInfoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_user_info, container, false);
        unbinder = ButterKnife.bind(this, mView);
        return mView;
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
    public void showPortraitSettingDialog() {
        SettingChooseDialog dialog = new SettingChooseDialog(mActivity, SettingChooseDialog.PORTRAIT);
        dialog.setOnItemClickListener(new SettingChooseDialog.OnItemClickListener() {
            @Override
            public void onFirstItemClick() {
                mPresenter.getPortraitFromCamera();
            }

            @Override
            public void onSecondItemClick() {
                mPresenter.getPortraitFromAlbum();
            }
        });
        dialog.show();
    }

    @Override
    public void showSexSettingDialog() {
        SettingChooseDialog dialog = new SettingChooseDialog(mActivity, SettingChooseDialog.SEX);
        dialog.setOnItemClickListener(new SettingChooseDialog.OnItemClickListener() {
            @Override
            public void onFirstItemClick() {
                mPresenter.setSex(1);
            }

            @Override
            public void onSecondItemClick() {
                mPresenter.setSex(2);
            }
        });
        dialog.show();
    }

    @Override
    public void showNicknameDialog() {
        SettingDialog dialog = new SettingDialog(mActivity, SettingDialog.NICKNAME);
        dialog.setOnOkListener(content -> mPresenter.setNickName(content));
        dialog.show();
    }

    @Override
    public void showPortrait(String url) {
        RequestOptions requestOptions = new RequestOptions();
        // 不使用磁盘缓存
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        // 不使用内存缓存
        requestOptions.skipMemoryCache(true);

        Glide.with(this).load(url)
                .thumbnail(0.5f)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .apply(requestOptions)
                .into(ivPortrait);
    }

    @Override
    public void showPortrait(int res) {
        Glide.with(this).load(res)
                .thumbnail(0.1f)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(ivPortrait);
    }

    @Override
    public void showNickName(String nickName) {
        tvNickname.setText(nickName);
    }

    @Override
    public void showSex(String sex) {
        tvSex.setText(sex);
    }

    @Override
    public void showAccount(String account) {
        tvAccount.setText(account);
    }

    @Override
    public void startActForResult(Intent intent, int type) {
        startActivityForResult(intent, type);
    }

    @Override
    public void showToast(String content) {
        if (mToast == null) {
            mToast = new MyToast(mActivity);
        }
        mToast.showToast(content);
    }


    @Override
    public void setPresenter(UIContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initListener() {
        onViewClicked(mView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_back, R.id.iv_back, R.id.tv_portrait, R.id.tv_nickname_item, R.id.tv_sex_item, R.id.tv_sign_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
            case R.id.iv_back:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setFinishAct();
                break;
            case R.id.tv_portrait:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setShowPortraitSettingDialog();
                break;
            case R.id.tv_nickname_item:
                SoundShakeUtil.playSound(SoundShakeUtil.SELECT_SWOOSH1_SOUND);
                mPresenter.setShowNicknameDialog();
                break;
            case R.id.tv_sex_item:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setShowSexSettingDialog();
                break;
            case R.id.tv_sign_out:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setSignOut(mActivity);
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mPresenter.onActivityResult(requestCode, resultCode, intent);
    }
}
