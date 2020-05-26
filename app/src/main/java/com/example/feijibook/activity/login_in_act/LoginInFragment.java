package com.example.feijibook.activity.login_in_act;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.activity.sign_up_act.SignUpActivity;
import com.example.feijibook.widget.MyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginInFragment extends Fragment implements LIContract.View {
    View mView;
    @BindView(R.id.cb_dispose_password)
    CheckBox cbDisposePassword;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.cb_save_password)
    CheckBox cbSavePassword;
    Unbinder unbinder;
    MyToast mToast;

    private Activity mActivity;
    private LIContract.Presenter mPresenter;

    public LoginInFragment() {
        // Required empty public constructor
    }

    public static LoginInFragment newInstance() {
        return new LoginInFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_login_in, container, false);


        unbinder = ButterKnife.bind(this, mView);

        mPresenter.setInit();
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.bt_sign_in, R.id.tv_to_sign_up})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_sign_in:
                mPresenter.setCheckInput(etAccount.getText().toString(), etPassword.getText().toString());
                break;
            case R.id.tv_to_sign_up:
                Intent intent = new Intent(mActivity, SignUpActivity.class);
                mPresenter.setStartActivity(intent);
                break;
            default:
        }
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
    public void showAccount(String account) {
        etAccount.setText(account);
    }

    @Override
    public void showPassword(String password) {
        etPassword.setText(password);
    }

    @Override
    public void checkRememberPassword(boolean checked) {
        cbSavePassword.setChecked(checked);
    }

    @Override
    public void showToast(String content) {
        if (mToast == null) {
            mToast = new MyToast(mActivity);
        }
        mToast.showToast(content);
    }


    @Override
    public void setPresenter(LIContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initListener() {
        onViewClicked(mView);
        cbDisposePassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // check后，则显示密码
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        cbSavePassword.setOnCheckedChangeListener((buttonView, isChecked) -> mPresenter.setDisposePassword(isChecked));
    }
}
