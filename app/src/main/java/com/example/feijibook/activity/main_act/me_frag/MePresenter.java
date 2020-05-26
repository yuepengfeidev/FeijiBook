package com.example.feijibook.activity.main_act.me_frag;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.feijibook.R;
import com.example.feijibook.app.Constants;
import com.example.feijibook.http.BaseObserver;
import com.example.feijibook.http.ExceptionHandle;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.SharedPreferencesUtils;
import com.example.feijibook.util.SoundShakeUtil;

import java.util.Map;

/**
 * Created by 你是我的 on 2019/3/11
 */
public class MePresenter implements MeContract.Presenter {
    private MeContract.View mView;
    private MeContract.Model mModel;
    private boolean isNeedUpdate = true;

    public MePresenter(Activity activity, MeContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mView.getAct(activity);
    }

    @Override
    public void start() {
        mModel = new MeModel();
    }

    @Override
    public void setInit() {
        mView.initListener();
        if (!"".equals(mModel.getGesturePw())) {
            // 有手势密码，打开相应Switch，设置fail是为了不让出发switch切换操作
            NoticeUpdateUtils.changeGesturePwFailed = true;
            mView.checkGesturePwSwitch();
        }
        SoundShakeUtil.initSoundRes();
        if (mModel.isSoundOpen()) {
            NoticeUpdateUtils.changeSoundFailed = true;
            mView.checkSoundOpen();
        } else {
            SoundShakeUtil.OpenSound(false);
        }
    }

    @Override
    public void setStartActivity(Intent intent) {
        mView.startAct(intent);
    }

    @Override
    public void setGetDataCount() {
        Map<String, Integer> map = mModel.getDayAndRecordCount();
        mView.showDayAndRecord(String.valueOf(map.get("dayCount")), String.valueOf(map.get("recordCount")));
    }

    @Override
    public void setChangePw(String resPw, String newPw) {
        mModel.requestChangePw(SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT),
                resPw, newPw).subscribe(new BaseObserver<String>() {
            @Override
            public void onSuccess(String s) {
                if (s.equals(Constants.CHANGE_SUCCESS)) {
                    SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.PASSWORD, newPw);
                    mView.showToast("请求修改密码成功！");
                    mView.dismissChangePwDialog();
                } else if (s.equals(Constants.PASSWORD_ERROR)) {
                    mView.showPwError();
                }
            }

            @Override
            public void onHttpError(ExceptionHandle.ResponseException exception) {
                mView.showToast("请求修改密码错误！");
                Log.d(Constants.HTTP_TAG, "请求修改密码错误！");
            }
        });
    }

    @Override
    public void setShowChangePwDialog() {
        mView.showChangePwDialog();
    }

    @Override
    public void setGetPortrait() {
        String portrait = SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.PORTRAIT);
        // 没有上传头像，则使用默认头像
        if (portrait == null || "".equals(portrait)) {
            mView.showPortrait(R.drawable.portrait_default);
        } else {
            mView.showPortrait(portrait);
        }
    }

    @Override
    public void setGetNickName() {
        String nickName = SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.NICKNAME);
        if (nickName == null || "".equals(nickName)) {
            nickName = SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT);
        }
        mView.showNickName(nickName);
    }

    @Override
    public void setSaveSoundOpen(boolean isOpen) {
        mModel.saveSoundOpen(isOpen);
        SoundShakeUtil.OpenSound(isOpen);
        SoundShakeUtil.playSound(SoundShakeUtil.TAP2_SOUND);
    }


    @Override
    public void isNeedUpdateRecord(boolean needUpdate) {
        isNeedUpdate = needUpdate;
    }

    @Override
    public void checkUpdateRecord() {
        // 通知更新个人信息
        if (NoticeUpdateUtils.updateInfo) {
            setGetNickName();
            setGetPortrait();
            NoticeUpdateUtils.updateInfo = false;
        }
        if (NoticeUpdateUtils.changeGesturePwFailed) {
            // 操作密码失败，则将switch该户原来的状态
            mView.checkGesturePwSwitch();
        }
        if (!isNeedUpdate) {
            return;
        }
        mView.updateRecord();
        isNeedUpdate = false;
    }
}
