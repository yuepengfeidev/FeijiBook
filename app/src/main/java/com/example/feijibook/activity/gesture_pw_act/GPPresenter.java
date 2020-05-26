package com.example.feijibook.activity.gesture_pw_act;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import com.example.feijibook.activity.main_act.MainActivity;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.SoundShakeUtil;

/**
 * GPPresenter
 *
 * @author PengFei Yue
 * @date 2019/11/3
 * @description
 */
public class GPPresenter implements GPContract.Presenter {
    private GPContract.View mView;
    private GPContract.Model mModel;
    /**
     * 界面模式:input\set\cancel （输入密码 和 设置密码 和取消密码 模式）
     */
    private String mMode;
    /**
     * 第一次设置的手势密码
     */
    private String gestureSetPw;

    GPPresenter(Activity activity, GPContract.View view, String mode) {
        mView = view;
        mMode = mode;
        mView.setPresenter(this);
        mView.getAct(activity);
    }

    @Override
    public void setFinishAct() {
        mView.finishAct();
    }

    @Override
    public void setStartActivity(Intent intent) {
        mView.startAct(intent);
    }

    @Override
    public void disposeGesturePw(String input) {
        switch (mMode) {
            case "input":
                String pw = mModel.getGesturePw();
                if (pw.equals(input)) {
                    Intent intent = new Intent(MyApplication.sContext, MainActivity.class);
                    mView.startAct(intent);
                    mView.finishAct();
                } else {
                    SoundShakeUtil.shakePhone();
                    mView.showState("密码错误，重新输入！", Color.RED,true);
                    mView.errorGesture();
                }
                break;
            case "set":
                if (gestureSetPw == null || "".equals(gestureSetPw)) {
                    gestureSetPw = input;
                    mView.clearGesture();
                    mView.showState("再次输入确认密码", Color.BLACK,false);
                } else if (gestureSetPw.equals(input)) {
                    mModel.saveGesturePw(input);
                    mView.finishAct();
                } else {
                    SoundShakeUtil.shakePhone();
                    mView.showState("两次密码不相同,重新输入", Color.RED,true);
                    mView.errorGesture();
                }
                break;
            case "cancel":
                String pw3 = mModel.getGesturePw();
                if (pw3.equals(input)) {
                    mModel.saveGesturePw("");
                    mView.finishAct();
                } else {
                    SoundShakeUtil.shakePhone();
                    mView.showState("密码错误，重新输入！", Color.RED,true);
                    mView.errorGesture();
                }
                break;
            default:
        }
    }

    @Override
    public void start() {
        mModel = new GPModel();
    }

    @Override
    public void setInit() {
        switch (mMode) {
            case "input":
                mView.hideBack();
                mView.showState("输入密码", Color.BLACK,false);
                break;
            case "set":
                mView.showState("输入设置密码", Color.BLACK,false);
                break;
            case "cancel":
                mView.showState("输入原密码", Color.BLACK,false);
                break;
            default:
        }
        mView.initListener();
    }
}
