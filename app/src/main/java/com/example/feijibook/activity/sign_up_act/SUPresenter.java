package com.example.feijibook.activity.sign_up_act;

import android.app.Activity;

import com.example.feijibook.app.Constants;
import com.example.feijibook.http.BaseObserver;
import com.example.feijibook.http.ExceptionHandle;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * SUPresenter
 *
 * @author PengFei Yue
 * @date 2019/10/6
 * @description
 */
public class SUPresenter implements SUContact.Presenter {
    private SUContact.View mView;
    private SUContact.Model mModel;

    public SUPresenter(Activity activity, SUContact.View view) {
        mView = view;
        mView.getAct(activity);
        mView.setPresenter(this);
    }

    @Override
    public void setFinishAct() {
        mView.finishAct();
    }

    @Override
    public void setCheckInput(String account, String password, String confirmPassword) {
        if ("".equals(account)) {
            mView.showToast("账号不能为空！");
        } else if ("".equals(password)) {
            mView.showToast("密码不能为空！");
        } else if ("".equals(confirmPassword)) {
            mView.showToast("重复密码不能为空！");
        } else if (!password.equals(confirmPassword)) {
            mView.showToast("两次密码输入不相同！");
        } else {
            // 请求服务器，判断是否注册成功
            mModel.requestRegister(account, password).subscribe(new BaseObserver<String>() {
                @Override
                public void onSuccess(String s) {
                    if (s.equals(Constants.REGISTER_SUCCESS)) {
                        mView.showToast("注册成功!");
                        // 成功则关闭当前注册界面
                        setFinishAct();
                    } else if (s.equals(Constants.REGISTER_FAILED)) {
                        mView.showToast("已存在该账户！");
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
        mModel = new SUModel();
    }

    @Override
    public void setInit() {
        mView.initListener();
    }
}
