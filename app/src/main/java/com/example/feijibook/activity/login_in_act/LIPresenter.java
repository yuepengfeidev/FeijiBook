package com.example.feijibook.activity.login_in_act;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

import com.example.feijibook.activity.gesture_pw_act.GesturePwActivity;
import com.example.feijibook.activity.main_act.MainActivity;
import com.example.feijibook.app.Constants;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.http.BaseObserver;
import com.example.feijibook.http.ExceptionHandle;

/**
 * LIPresenter
 *
 * @author PengFei Yue
 * @date 2019/10/6
 * @description
 */
public class LIPresenter implements LIContract.Presenter {
    private LIContract.View mView;
    private LIContract.Model mModel;
    private Activity mActivity;

    public LIPresenter(Activity activity, LIContract.View view) {
        mView = view;
        mActivity = activity;
        mView.getAct(activity);
        mView.setPresenter(this);
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
    public void setDisposePassword(boolean isRemember) {
        mModel.disposePassword(isRemember);
    }

    @Override
    public void setCheckInput(String account, String password) {
        if ("".equals(account)) {
            mView.showToast("账号不能为空！");
        } else if ("".equals(password)) {
            mView.showToast("密码不能为空！");
        } else {
            // 管理员和账户，不用请求服务器直接登录
            if (account.equals("123456")) {
                if (!password.equals("admin")) {
                    mView.showToast("密码错误！");
                    return;
                } else {
                    if (mModel.isRememberPassword()) {
                        mModel.savePassword(password);
                    }
                    mModel.saveAccount(account);
                    MyApplication.initData();
                    Intent intent = new Intent(mActivity, MainActivity.class);
                    setStartActivity(intent);
                    setFinishAct();
                    return;
                }
            }
            // 发送请求给服务器，判断账号密码是否正确
            mModel.requestLogin(account, password).subscribe(new BaseObserver<String>() {
                @Override
                public void onSuccess(String s) {
                    if (s.equals(Constants.LOGIN_SUCCESS)) {
                        if (mModel.isRememberPassword()) {
                            mModel.savePassword(password);
                        }
                        mModel.saveAccount(account);
                        MyApplication.initData();
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            Intent intent = new Intent(mActivity, MainActivity.class);
                            setStartActivity(intent);
                            setFinishAct();
                        }, 500);

                    } else if (s.equals(Constants.PASSWORD_ERROR)) {
                        mView.showToast("密码错误！");
                    } else if (s.equals(Constants.ACCOUNT_ERROR)) {
                        mView.showToast("不存在该账户！");
                    }
                }

                @Override
                public void onHttpError(ExceptionHandle.ResponseException exception) {
                    mView.showToast(exception.message);
                }
            });
        }
    }

    @Override
    public void start() {
        mModel = new LIModel();
    }

    @Override
    public void setInit() {
        if (mModel.isFirstLogin()) {
            if (!"".equals(mModel.getUserName())) {
                mView.showAccount(mModel.getUserName());
                if (!"".equals(mModel.getPassWord())) {
                    if (mModel.isRememberPassword()) {
                        mView.showPassword(mModel.getPassWord());
                    } else {
                        mView.checkRememberPassword(false);
                    }
                }
            }
        } else {
            // 初始化相应账号的数据库和文件夹
            MyApplication.initData();
            Intent intent;

            if ("".equals(mModel.getGesturePw())) {
                // 不是第一次登录就直接跳到主界面
                intent = new Intent(mActivity, MainActivity.class);
            } else {
                // 如果有手势密码，则跳转到输入手势密码界面
                intent = new Intent(mActivity, GesturePwActivity.class);
                intent.putExtra("mode", "input");
            }

            setStartActivity(intent);
            setFinishAct();
        }
        mView.initListener();
    }
}
