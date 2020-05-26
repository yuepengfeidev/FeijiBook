package com.example.feijibook.activity.sign_up_act;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.PreloadTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.widget.MyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements SUContact.View {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.bt_sign_up)
    Button btSignUp;
    @BindView(R.id.bt_cancel_sign_up)
    Button btCancelSignUp;
    Unbinder unbinder;
    private View mView;
    private Activity mActivity;
    private SUContact.Presenter mPresenter;

    private MyToast mToast;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        unbinder = ButterKnife.bind(this, mView);

        mPresenter.setInit();
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_back, R.id.tv_back, R.id.bt_sign_up, R.id.bt_cancel_sign_up})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.tv_back:
            case R.id.bt_cancel_sign_up:
                mPresenter.setFinishAct();
                break;
            case R.id.bt_sign_up:
                mPresenter.setCheckInput(etAccount.getText().toString(),
                        etPassword.getText().toString(),
                        etConfirmPassword.getText().toString());
                break;
            default:
        }
    }

    @Override
    public void finishAct() {
        mActivity.finish();
    }

    @Override
    public void getAct(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void showToast(String content) {
        if (mToast == null) {
            mToast = new MyToast(mActivity);
        }
        mToast.showToast(content);
    }

    @Override
    public void setPresenter(SUContact.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initListener() {
        onViewClicked(mView);
    }
}
