package com.example.feijibook.activity.gesture_pw_act;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.GesturePwView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class GesturePwFragment extends Fragment implements GPContract.View {
    Activity mActivity;
    GPContract.Presenter mPresenter;
    View mView;
    @BindView(R.id.gesturePwView)
    GesturePwView gesturePwView;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.tv_state)
    TextView tvState;
    Unbinder unbinder;


    public GesturePwFragment() {
        // Required empty public constructor
    }

    public static GesturePwFragment newInstance() {
        return new GesturePwFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_gesture_pw, container, false);
        unbinder = ButterKnife.bind(this, mView);
        mPresenter.setInit();
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
    public void hideBack() {
        tvBack.setVisibility(View.GONE);
        ivBack.setVisibility(View.GONE);
    }

    @Override
    public void showState(String state, int color,boolean anim) {
        tvState.setText(state);
        tvState.setTextColor(color);
        if (anim) {
            Animation shake = AnimationUtils.loadAnimation(mActivity, R.anim.anim_gesture_pw_error_text);
            tvState.startAnimation(shake);
        }
    }

    @Override
    public void clearGesture() {
        gesturePwView.clearGesture();
    }

    @Override
    public void errorGesture() {
        gesturePwView.errorGesture();
    }

    @Override
    public void setPresenter(GPContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initListener() {
        onViewClicked(mView);
        gesturePwView.setOnGestureListener(new GesturePwView.OnGestureListener() {
            @Override
            public void onGestureFinish(String gesturePw) {
                mPresenter.disposeGesturePw(gesturePw);
            }

            @Override
            public void fewerThanFour() {
                showState("至少四个点，请重新输入！", Color.RED,true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_back, R.id.tv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.tv_back:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                // 点击返回的都是没有修改成功的
                NoticeUpdateUtils.changeGesturePwFailed =true;
                mPresenter.setFinishAct();
                break;
            default:
        }
    }

   
}
